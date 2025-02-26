<template>
  <el-dialog v-model="dialogVisible" title="新增食物" width="30%" :before-close="handleClose">
    <el-form :model="food" label-width="80px">
      <el-form-item label="食物名称">
        <el-input v-model="food.name" />
      </el-form-item>
      <el-form-item label="食物类别">
        <el-select multiple v-model="food.foodTypeId">
          <el-option label="Zone one" value="shanghai" />
          <el-option label="Zone two" value="beijing" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addFood">
          提交
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import {ref} from 'vue'



</script>



<style scoped>

</style>