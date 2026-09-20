<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">采购申请</h2>
    </div>

    <el-card style="max-width: 720px; margin: 0 auto">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="form.assetName" placeholder="请输入要采购的资产名称" />
        </el-form-item>
        <el-form-item label="资产编号">
          <el-input v-model="form.assetCode" placeholder="如有指定资产编号请填写" />
        </el-form-item>
        <el-form-item label="采购数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="申请部门" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择申请部门" style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="期望到货日期">
          <el-date-picker v-model="form.expectedDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="采购原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请详细说明采购原因、用途及预算" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="其他需要说明的信息" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" style="width: 100%">
            提交采购申请
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitPurchase } from '@/api/asset'
import { getDepartments } from '@/api/system'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)
const departments = ref([])

const form = reactive({
  type: 'ASSET_PURCHASE',
  assetId: null,
  assetName: '',
  assetCode: '',
  quantity: 1,
  applicantId: userStore.userId,
  departmentId: null,
  handlerId: null,
  expectedDate: '',
  reason: '',
  remark: ''
})

const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入采购数量', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择申请部门', trigger: 'change' }],
  reason: [{ required: true, message: '请输入采购原因', trigger: 'blur' }]
}

async function loadDepartments() {
  try {
    const res = await getDepartments()
    departments.value = res.data
  } catch (e) {}
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    submitting.value = true
    await submitPurchase(form)
    ElMessage.success('采购申请已提交，等待审批')
    router.push('/assets/operations')
  } catch (e) {
    // handled
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadDepartments()
})
</script>
