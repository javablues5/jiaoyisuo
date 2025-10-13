<template>
  <div>
    <!-- 统计信息 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>总数</span>
          </div>
          <div class="statistic-number">{{ statistics.total || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>待补偿</span>
          </div>
          <div class="statistic-number text-warning">{{ statistics.pending || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>已补偿</span>
          </div>
          <div class="statistic-number text-success">{{ statistics.success || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>补偿失败</span>
          </div>
          <div class="statistic-number text-danger">{{ statistics.failed || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待补偿" value="0" />
          <el-option label="已补偿" value="1" />
          <el-option label="补偿失败" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-refresh"
          size="mini"
          :disabled="multiple"
          @click="handleRetry"
          v-hasPermi="['bussiness:compensation:detail:retry']"
        >重试补偿</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['bussiness:compensation:detail:export']"
        >导出</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="detailList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单号" align="center" prop="orderNo" show-overflow-tooltip />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="用户名" align="center" prop="loginName" />
      <el-table-column label="交易对" align="center" prop="symbol" />
      <el-table-column label="投注金额" align="center" prop="betAmount" />
      <el-table-column label="补偿金额" align="center" prop="compensationAmount" />
      <el-table-column label="补偿比例" align="center" prop="compensationRate">
        <template slot-scope="scope">
          {{ (scope.row.compensationRate * 100).toFixed(2) }}%
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待补偿</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">已补偿</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger">补偿失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="补偿时间" align="center" prop="compensationTime" width="180">
        <template slot-scope="scope">
          <span v-if="scope.row.compensationTime">{{ parseTime(scope.row.compensationTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="错误信息" align="center" prop="errorMsg" show-overflow-tooltip />
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listDetailByBatchId, getStatistics, retryFailedCompensation } from "@/api/bussiness/compensation/detail";

export default {
  name: "CompensationDetail",
  props: {
    batchId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 补偿详细记录表格数据
      detailList: [],
      // 统计信息
      statistics: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        orderNo: null,
        status: null,
      }
    };
  },
  created() {
    this.getList();
    this.getStatistics();
  },
  methods: {
    /** 查询补偿详细记录列表 */
    getList() {
      this.loading = true;
      listDetailByBatchId(this.batchId, this.queryParams).then(response => {
        this.detailList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取统计信息 */
    getStatistics() {
      getStatistics(this.batchId).then(response => {
        this.statistics = response.data;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    /** 重试补偿 */
    handleRetry() {
      const ids = this.ids;
      this.$modal.confirm('确认重试选中的补偿记录？').then(() => {
        return retryFailedCompensation(ids);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("重试补偿成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('bussiness/compensation/detail/export', {
        batchId: this.batchId,
        ...this.queryParams
      }, `compensation_detail_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.statistic-number {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  padding: 10px 0;
}

.text-warning {
  color: #E6A23C;
}

.text-success {
  color: #67C23A;
}

.text-danger {
  color: #F56C6C;
}

.box-card {
  margin-bottom: 10px;
}
</style>
