<template>
    <div class="adRole-management-container">
      <!-- 搜索和操作栏 -->
      <div class="filter-container">
        <el-input
          v-model="listQuery.roleName"
          placeholder="角色名称"
          style="width: 200px"
          @keyup.enter="handleFilter"
        />
        <el-button type="primary" @click="handleFilter">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button type="success" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>
  
      <!-- 角色表格 -->
      <el-table
        :data="roleList"
        border
        fit
        highlight-current-row
        style="width: 100%"
      >
        <el-table-column prop="roleKey" label="角色标识" width="180" />
        <el-table-column prop="roleName" label="角色名称" width="180" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 分页 -->
      <el-pagination
        class="pagination-container"
        :current-page="listQuery.page"
        :page-size="listQuery.limit"
        :total="total"
        @current-change="handlePageChange"
      />
  
      <!-- 角色编辑/新增对话框 -->
      <el-dialog
        :title="dialogStatus === 'create' ? '新增角色' : '编辑角色'"
        v-model="dialogVisible"
      >
        <el-form
          ref="roleForm"
          :model="tempRole"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="角色标识" prop="roleKey">
            <el-input v-model="tempRole.roleKey" />
          </el-form-item>
          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="tempRole.roleName" />
          </el-form-item>
          <el-form-item label="角色描述">
            <el-input
              v-model="tempRole.description"
              type="textarea"
              :rows="3"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createRole() : updateRole()">
            确认
          </el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search, Plus } from '@element-plus/icons-vue'
  
  // 模拟数据
  const mockData = [
    {
      id: 1,
      roleKey: 'admin',
      roleName: '管理员',
      description: '系统管理员，拥有所有权限',
      createTime: '2023-08-20 10:00:00'
    },
    {
      id: 2,
      roleKey: 'user',
      roleName: '普通用户',
      description: '普通用户权限',
      createTime: '2023-08-20 11:00:00'
    }
  ]
  
  const roleList = ref([])
  const total = ref(0)
  const dialogVisible = ref(false)
  const dialogStatus = ref('create')
  const roleForm = ref(null)
  
  const listQuery = reactive({
    page: 1,
    limit: 10,
    roleName: ''
  })
  
  const tempRole = reactive({
    id: null,
    roleKey: '',
    roleName: '',
    description: ''
  })
  
  const rules = reactive({
    roleKey: [
      { required: true, message: '角色标识不能为空', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    roleName: [
      { required: true, message: '角色名称不能为空', trigger: 'blur' }
    ]
  })
  
  // 初始化数据
  function getList() {
    // 这里应替换为真实的API调用
    roleList.value = mockData
    total.value = mockData.length
  }
  
  function handleFilter() {
    listQuery.page = 1
    getList()
  }
  
  function handleCreate() {
    resetTempRole()
    dialogStatus.value = 'create'
    dialogVisible.value = true
  }
  
  function handleEdit(row) {
    Object.assign(tempRole, row)
    dialogStatus.value = 'edit'
    dialogVisible.value = true
  }
  
  function handleDelete(row) {
    ElMessageBox.confirm('确认删除该角色？', '警告', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 调用删除API
      ElMessage.success('删除成功')
      getList()
    })
  }
  
  function createRole() {
    roleForm.value.validate(valid => {
      if (valid) {
        // 调用创建API
        ElMessage.success('创建成功')
        dialogVisible.value = false
        getList()
      }
    })
  }
  
  function updateRole() {
    roleForm.value.validate(valid => {
      if (valid) {
        // 调用更新API
        ElMessage.success('更新成功')
        dialogVisible.value = false
        getList()
      }
    })
  }
  
  function resetTempRole() {
    Object.assign(tempRole, {
      id: null,
      roleKey: '',
      roleName: '',
      description: ''
    })
  }
  
  function handlePageChange(page) {
    listQuery.page = page
    getList()
  }
  
  onMounted(() => {
    getList()
  })
  </script>
  
  <style scoped>
  .filter-container {
    margin-bottom: 20px;
  }
  .pagination-container {
    margin-top: 20px;
    text-align: center;
  }
  </style>色温Ω