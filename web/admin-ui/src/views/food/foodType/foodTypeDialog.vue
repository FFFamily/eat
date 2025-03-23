<template>
  <el-dialog v-model="isOpen" title="新增食物类型" width="30%" :before-close="handleClose">
    <el-form :model="foodType" label-width="80px">
      <el-form-item label="类型名称">
        <el-input v-model="foodType.name" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="isOpen = false">取消</el-button>
        <el-button type="primary" @click="addFoodTypeFunc">
          提交
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import {ref,computed} from 'vue'
import {addFoodType} from '~/api/food/foodTypeApi'
const emit = defineEmits(["update:modelValue"])
const foodType = ref({name:''})
const props = defineProps<{
  dialogVisible: boolean
}>()
const isOpen = computed({
  get: () => props.dialogVisible,
  set: (value) => emit("update:modelValue", value),
})


function handleClose(){

}

function addFoodTypeFunc(){
  console.log(foodType.value)
  addFoodType(foodType.value).then((res) => {
    console.log(res)
  })
}

</script>



<style scoped>

</style>