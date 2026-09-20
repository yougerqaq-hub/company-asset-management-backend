<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">归还申请</h2>
    </div>

    <el-card style="max-width: 720px; margin: 0 auto">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择资产" prop="assetId">
          <el-select v-model="form.assetId" placeholder="请选择要归还的资产" filterable style="width: 100%" @change="handleAssetChange">
            <el-option
              v-for="asset in inUseAssets"
              :key="asset.id"
              :label="`${asset.assetCode} - ${asset.name}（${asset.brand} ${asset.model}）`"
              :value="asset.id"
            />
          </el-select>
          <div style="font-size: 12px; color: #909399; margin-top: 4px">仅显示在用状态的资产</div>
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="form.assetName" disabled />
        </el-form-item>
        <el-form-item label="归还数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="归还部门" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择归还部门" style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="实际归还日期">
          <el-date-picker v-model="form.expectedDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="归还说明" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请说明资产归还情况、是否有损坏等" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="其他需要说明的信息" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" style="width: 100%">
            提交归还申请
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitReturn, getAssetList } from '@/api/asset'
import { getDepartments } from '@/api/system'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)
const departments = ref([])
const assetList = ref([])

const form = reactive({
  type: 'ASSET_RETURN',
  assetId: null,
  assetName: '',
  quantity: 1,
  applicantId: userStore.userId,
  departmentId: null,
  handlerId: null,
  expectedDate: '',
  reason: '',
  remark: ''
})

const rules = {
  assetId: [{ required: true, message: '请选择要归还的资产', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入归还数量', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择归还部门', trigger: 'change' }],
  reason: [{ required: true, message: '请输入归还说明', trigger: 'blur' }]
}

const inUseAssets = computed(() => assetList.value.filter(a => a.status === 'IN_USE'))

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
    await submitReturn(form)
    ElMessage.success('归还申请已提交，等待审批')
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
