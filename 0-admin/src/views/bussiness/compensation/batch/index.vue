<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="批次名称" prop="batchName">
        <el-input
          v-model="queryParams.batchName"
          placeholder="请输入批次名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待执行" value="0" />
          <el-option label="执行中" value="1" />
          <el-option label="已完成" value="2" />
          <el-option label="已取消" value="3" />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleCreateBatch"
          v-hasPermi="['bussiness:compensation:batch:create']"
        >创建补偿批次</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['bussiness:compensation:batch:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="batchList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="批次号" align="center" prop="batchNo" />
      <el-table-column label="批次名称" align="center" prop="batchName" show-overflow-tooltip />
      <el-table-column label="策略名称" align="center" prop="strategyName" />
      <el-table-column label="订单总数" align="center" prop="totalOrders" />
      <el-table-column label="用户总数" align="center" prop="totalUsers" />
      <el-table-column label="总投注金额" align="center" prop="totalBetAmount" />
      <el-table-column label="总补偿金额" align="center" prop="totalCompensationAmount" />
      <el-table-column label="补偿比例" align="center" prop="compensationRate">
        <template slot-scope="scope">
          {{ (scope.row.compensationRate * 100).toFixed(2) }}%
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待执行</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="primary">执行中</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="danger">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleViewDetails(scope.row)"
            v-hasPermi="['bussiness:compensation:detail:list']"
          >查看详情</el-button>
          <el-button
            v-if="scope.row.status === 0"
            size="mini"
            type="text"
            icon="el-icon-video-play"
            @click="handleExecute(scope.row)"
            v-hasPermi="['bussiness:compensation:batch:execute']"
          >执行</el-button>
          <el-button
            v-if="scope.row.status === 0"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleCancel(scope.row)"
            v-hasPermi="['bussiness:compensation:batch:cancel']"
          >取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 创建补偿批次对话框 -->
    <el-dialog title="创建补偿批次" :visible.sync="createOpen" width="600px" append-to-body>
      <el-form ref="createForm" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="批次名称" prop="batchName">
          <el-input v-model="createForm.batchName" placeholder="请输入批次名称" />
        </el-form-item>
        <el-form-item label="补偿策略" prop="strategyId">
          <el-select v-model="createForm.strategyId" placeholder="请选择补偿策略" style="width: 100%">
            <el-option
              v-for="strategy in enabledStrategies"
              :key="strategy.id"
              :label="strategy.strategyName + ' (' + (strategy.compensationRate * 100).toFixed(2) + '%' + ')'"
              :value="strategy.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交易对" prop="symbol">
          <el-input v-model="createForm.symbol" placeholder="请输入交易对，为空表示全部" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="createForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="createForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="自定义比例" prop="customRate">
          <el-input-number
            v-model="createForm.customRate"
            :precision="4"
            :step="0.01"
            :max="1"
            :min="0"
            placeholder="可选，覆盖策略默认比例"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="info" @click="handlePreview" :loading="previewLoading">预览搜索结果</el-button>
        </el-form-item>
        <el-form-item v-if="previewData" label="预览结果">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单数量">{{ previewData.orderCount }}</el-descriptions-item>
            <el-descriptions-item label="用户数量">{{ previewData.userCount }}</el-descriptions-item>
            <el-descriptions-item label="总投注金额">{{ previewData.totalBetAmount }}</el-descriptions-item>
            <el-descriptions-item label="预计补偿金额">
              {{ (previewData.totalBetAmount * (createForm.customRate || selectedStrategyRate || 0)).toFixed(6) }}
            </el-descriptions-item>
          </el-descriptions>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitCreateForm" :loading="createLoading">确 定</el-button>
        <el-button @click="cancelCreate">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 补偿详情对话框 -->
    <el-dialog title="补偿详情" :visible.sync="detailOpen" width="80%" append-to-body>
      <CompensationDetail v-if="detailOpen" :batch-id="selectedBatchId" />
    </el-dialog>
  </div>
</template>

<script>
import { listBatch, searchPreview, createBatch, executeBatch, cancelBatch } from "@/api/bussiness/compensation/batch";
import { getEnabledStrategies } from "@/api/bussiness/compensation/strategy";
import CompensationDetail from './detail';

export default {
  name: "Batch",
  components: {
    CompensationDetail
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 补偿批次表格数据
      batchList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示创建对话框
      createOpen: false,
      // 是否显示详情对话框
      detailOpen: false,
      // 选中的批次ID
      selectedBatchId: null,
      // 创建加载状态
      createLoading: false,
      // 预览加载状态
      previewLoading: false,
      // 预览数据
      previewData: null,
      // 启用的策略列表
      enabledStrategies: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchName: null,
        status: null,
      },
      // 创建表单参数
      createForm: {
        batchName: null,
        strategyId: null,
        symbol: null,
        startTime: null,
        endTime: null,
        customRate: null
      },
      // 创建表单校验
      createRules: {
        batchName: [
          { required: true, message: "批次名称不能为空", trigger: "blur" }
        ],
        strategyId: [
          { required: true, message: "请选择补偿策略", trigger: "change" }
        ],
        startTime: [
          { required: true, message: "请选择开始时间", trigger: "change" }
        ],
        endTime: [
          { required: true, message: "请选择结束时间", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    selectedStrategyRate() {
      const strategy = this.enabledStrategies.find(s => s.id === this.createForm.strategyId);
      return strategy ? strategy.compensationRate : 0;
    }
  },
  created() {
    this.getList();
    this.getEnabledStrategies();
  },
  methods: {
    /** 查询补偿批次列表 */
    getList() {
      this.loading = true;
      listBatch(this.queryParams).then(response => {
        this.batchList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取启用的策略列表 */
    getEnabledStrategies() {
      getEnabledStrategies().then(response => {
        this.enabledStrategies = response.data;
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 创建补偿批次 */
    handleCreateBatch() {
      this.resetCreateForm();
      this.createOpen = true;
      this.previewData = null;
    },
    /** 重置创建表单 */
    resetCreateForm() {
      this.createForm = {
        batchName: null,
        strategyId: null,
        symbol: null,
        startTime: null,
        endTime: null,
        customRate: null
      };
      this.resetForm("createForm");
    },
    /** 取消创建 */
    cancelCreate() {
      this.createOpen = false;
      this.resetCreateForm();
    },
    /** 预览搜索结果 */
    handlePreview() {
      this.$refs["createForm"].validateField(['startTime', 'endTime'], (valid) => {
        if (!valid) {
          this.previewLoading = true;
          const params = {
            symbol: this.createForm.symbol,
            startTime: this.createForm.startTime,
            endTime: this.createForm.endTime
          };
          searchPreview(params).then(response => {
            this.previewData = response.data;
            this.previewLoading = false;
          }).catch(() => {
            this.previewLoading = false;
          });
        }
      });
    },
    /** 提交创建表单 */
    submitCreateForm() {
      this.$refs["createForm"].validate(valid => {
        if (valid) {
          this.createLoading = true;
          createBatch(this.createForm).then(response => {
            this.$modal.msgSuccess("补偿批次创建成功");
            this.createOpen = false;
            this.createLoading = false;
            this.getList();
            this.resetCreateForm();
          }).catch(() => {
            this.createLoading = false;
          });
        }
      });
    },
    /** 执行补偿批次 */
    handleExecute(row) {
      this.$modal.confirm('确认执行补偿批次"' + row.batchName + '"？执行后将无法撤销！').then(() => {
        return executeBatch(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("补偿批次执行成功");
      }).catch(() => {});
    },
    /** 取消补偿批次 */
    handleCancel(row) {
      this.$modal.confirm('确认取消补偿批次"' + row.batchName + '"？').then(() => {
        return cancelBatch(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("补偿批次已取消");
      }).catch(() => {});
    },
    /** 查看详情 */
    handleViewDetails(row) {
      this.selectedBatchId = row.id;
      this.detailOpen = true;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('bussiness/compensation/batch/export', {
        ...this.queryParams
      }, `batch_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
