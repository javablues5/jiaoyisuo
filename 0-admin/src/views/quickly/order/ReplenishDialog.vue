<template>
  <el-dialog
    title="一键补仓"
    :visible.sync="visibleInner"
    width="720px"
    append-to-body
    @close="handleCancel"
  >
    <el-form :model="form" ref="formRef" label-width="120px" size="small">
      <!-- 本金基准配置 -->
      <el-card shadow="never" class="mb12">
        <div slot="header">本金基准配置</div>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="本金计算方式" prop="principalCalcMethod">
              <el-select v-model="form.principalCalcMethod" placeholder="请选择">
                <el-option label="固定金额" value="fixed" />
                <el-option label="按余额比例" value="balance_ratio" />
                <el-option label="按亏损比例" value="loss_ratio" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基准金额设置" prop="baseAmount">
              <el-input-number
                v-model="form.baseAmount"
                :min="0"
                :step="100"
                :precision="2"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-checkbox v-model="form.autoAdjustByRisk">根据风险评级自动调整基准</el-checkbox>
        </el-form-item>
      </el-card>

      <!-- 补偿比例设置 -->
      <el-card shadow="never" class="mb12">
        <div slot="header">补偿比例设置</div>
        <el-row :gutter="12">
          <el-col :span="24">
            <el-form-item label="计算方式" prop="compensationMode">
              <el-radio-group v-model="form.compensationMode">
                <el-radio label="fixed">固定比率</el-radio>
                <el-radio label="ladder">阶梯比率</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="补偿比例(%)" prop="compensationRate">
              <el-input-number
                v-model="form.compensationRate"
                :min="0"
                :max="100"
                :step="1"
                :precision="2"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 用户筛选条件 -->
      <el-card shadow="never" class="mb12">
        <div slot="header">用户筛选条件</div>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="账户状态" prop="accountStatus">
              <el-select v-model="form.accountStatus" placeholder="请选择">
                <el-option label="全部" value="" />
                <el-option label="正常" value="active" />
                <el-option label="冻结" value="frozen" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险等级" prop="riskLevel">
              <el-select v-model="form.riskLevel" placeholder="请选择">
                <el-option label="全部" value="" />
                <el-option label="低" value="low" />
                <el-option label="中" value="medium" />
                <el-option label="高" value="high" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最近活动" prop="recentActivity">
              <el-select v-model="form.recentActivity" placeholder="请选择">
                <el-option label="全部" value="" />
                <el-option label="7天内活跃" value="7d" />
                <el-option label="30天内活跃" value="30d" />
                <el-option label="90天内活跃" value="90d" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户类型" prop="accountType">
              <el-select v-model="form.accountType" placeholder="请选择">
                <el-option label="全部" value="" />
                <el-option label="普通用户" value="normal" />
                <el-option label="VIP" value="vip" />
                <el-option label="机构" value="institution" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="亏损金额(>=)" prop="lossAmount">
              <el-input-number
                v-model="form.lossAmount"
                :min="0"
                :precision="2"
                :step="100"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" @click="emitApplyFilter">应用筛选</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "ReplenishDialog",
  props: {
    visible: { type: Boolean, default: false },
  },
  data() {
    return {
      visibleInner: false,
      form: this.getInitForm(),
    };
  },
  watch: {
    visible: {
      immediate: true,
      handler(val) {
        this.visibleInner = val;
        if (val) {
          this.resetForm();
        }
      },
    },
    visibleInner(val) {
      this.$emit("update:visible", val);
    },
  },
  methods: {
    getInitForm() {
      return {
        // 本金基准配置
        principalCalcMethod: "fixed",
        baseAmount: 1000,
        autoAdjustByRisk: false,
        // 补偿比例设置
        compensationMode: "fixed",
        compensationRate: 0,
        // 用户筛选条件
        accountStatus: "",
        riskLevel: "",
        recentActivity: "",
        accountType: "",
        lossAmount: 0,
      };
    },
    resetForm() {
      this.form = this.getInitForm();
      this.$nextTick(() => {
        if (this.$refs.formRef) this.$refs.formRef.clearValidate();
      });
    },
    handleCancel() {
      this.resetForm();
      this.visibleInner = false;
      this.$emit("cancel");
    },
    handleConfirm() {
      // 直接返回表单当前值
      this.$emit("confirm", { ...this.form });
      this.visibleInner = false;
    },
    emitApplyFilter() {
      this.$emit("apply-filter", { ...this.form });
    },
  },
};
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
</style>


