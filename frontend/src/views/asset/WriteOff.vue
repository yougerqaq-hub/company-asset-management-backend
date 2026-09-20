<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">核销申请</h2>
    </div>

    <el-card style="max-width: 720px; margin: 0 auto">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择资产" prop="assetId">
          <el-select v-model="form.assetId" placeholder="请选择要核销的资产" filterable style="width: 100%" @change="handleAssetChange">
            <el-option
              v-for="asset in assetList"
              :key="asset.id"
              :label="`${asset.assetCode} - ${asset.name}（${asset.brand} ${asset.model}）`"
              :value="asset.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="form.assetName" disabled />
        </el-form-item>
        <el-form-item label="核销数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="申请部门" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择申请部门" style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="核销原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请详细说明核销原因，如：已达使用年限、损坏无法修复、丢失等" />
        </el-form-item>
        <el-form-item label="资产现状">
          <el-radio-group v-model="form.assetCondition">
            <el-radio value="damaged">已损坏</el-radio>
            <el-radio value="lost">已丢失</el-radio>
            <el-radio value="obsolete">已淘汰</el-radio>
            <el-radio value="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="其他需要说明的信息" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" :loading="submitting" @click="handleSubmit" style="width: 100%">
            提交核销申请
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
import { submitWriteOff, getAssetList } from '@/api/asset'
import { getDepartments } from '@/api/system'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)
const departments = ref([])
const assetList = ref([])

const form = reactive({
  type: 'ASSET_WRITE_OFF',
  assetId: null,
  assetName: '',
  quantity: 1,
  applicantId: userStore.userId,
  departmentId: null,
  handlerId: null,
  expectedDate: '',
  reason: '',
  assetCondition: '',
  remark: ''
})

const rules = {
  assetId: [{ required: true, message: '请选择要核销的资产', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入核销数量', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择申请部门', trigger: 'change' }],
  reason: [{ required: true, message: '请输入核销原因', trigger: 'blur' }]
}

function handleAssetChange(id) {
  const asset = assetList.value.find(a => a.id === id)
  if (asset) form.assetName = asset.name
}

async function loadData() {
  try {
    const [assetRes, deptRes] = await Promise.all([getAssetList(), getDepartments()])
    assetList.value = assetRes.data
    departments.value = deptRes.data
  } catch (e) {}
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    submitting.value = true
    const { assetCondition, ...submitData } = form
    await submitWriteOff(submitData)
    ElMessage.success('核销申请已提交，等待审批')
    router.push('/assets/operations')
  } catch (e) {
    // handled
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
