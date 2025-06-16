<template>
    <div class="habit-management-page">
        <!-- 饮食习惯列表 -->
        <div class="habit-list">
            <el-table :data="habits" stripe>
                <el-table-column prop="name" label="习惯名称">
                    <template #default="scope">
                        <span @click="handleRowClick(scope.row)">{{ scope.row.name }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="食物标签" prop="tags">
                    <template #default="scope">
                        <el-tag v-for="tag in scope.row.tags" :key="tag.id" class="mx-1">{{ tag.name }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" fixed="right" min-width="120">
                    <template #default="scope">
                        <el-button @click.prevent="deleteHabit(scope.row.id)" type="danger" :icon="Delete" circle />
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 悬浮添加按钮 -->
        <div class="floating-add-btn" @click="openAddDialog">
            <span class="plus-icon">+</span>
        </div>

        <!-- 添加饮食习惯对话框 -->
        <el-dialog v-model="dialogVisible" title="添加饮食习惯" width="90%">
            <el-form :model="newHabit" ref="habitForm">
                <el-form-item label="习惯名称" prop="name"
                    :rules="[{ required: true, message: '请输入习惯名称', trigger: 'blur' }]">
                    <el-input v-model="newHabit.name" placeholder="请输入习惯名称"></el-input>
                </el-form-item>
                <el-form-item label="食物标签" prop="tagIds"
                    :rules="[{ required: true, message: '请选择至少一个食物标签', trigger: 'change' }]">
                    <el-select v-model="newHabit.tagIds" multiple placeholder="请选择食物标签">
                        <el-option v-for="tag in allTags" :key="tag.id" :label="tag.name" :value="tag.id"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAddHabit">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { createFoodHabit, getAllFoodHabits, deleteFoodHabit, getAllFoodTagList } from '~/api/food/foodHabitApi';
import { ElMessage } from 'element-plus';
import { Delete } from '@element-plus/icons-vue'

// 对话框状态
const dialogVisible = ref(false);
// 新饮食习惯数据
const newHabit = ref({ name: '', tagIds: [] });
// 表单引用
const habitForm = ref(null);
// 饮食习惯列表
const habits = ref([]);
// 所有食物标签列表（用于选择）
const allTags = ref([]);

onMounted(() => {
    searchAllFoodHabits();
    searchAllFoodTags();
})

// 获取所有饮食习惯
const searchAllFoodHabits = async () => {
    try {
        const res = await getAllFoodHabits();
        habits.value = res.data;
    } catch (error) {
        ElMessage.error('获取饮食习惯列表失败');
    }
};

// 获取所有食物标签（用于选择）
const searchAllFoodTags = async () => {
    try {
        const res = await getAllFoodTagList();
        allTags.value = res.data;
    } catch (error) {
        ElMessage.error('获取食物标签列表失败');
    }
};

// 删除饮食习惯
const deleteHabit = async (id) => {
    try {
        await deleteFoodHabit(id);
        ElMessage.success('删除成功');
        searchAllFoodHabits();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

// 打开添加对话框
const openAddDialog = () => {
    newHabit.value = { name: '', tagIds: [] };
    dialogVisible.value = true;
};

// 处理添加饮食习惯
const handleAddHabit = async () => {
    try {
        // 表单验证
        await habitForm.value.validate();
        // 调用API添加饮食习惯
        await createFoodHabit(newHabit.value);
        ElMessage.success('饮食习惯添加成功');
        dialogVisible.value = false;
        // 刷新列表
        searchAllFoodHabits();
    } catch (error) {
        if (error.name !== 'ValidationError') {
            ElMessage.error('添加失败');
        }
    }
};
</script>

<style scoped>
.habit-management-page {
    position: relative;
    min-height: 100vh;
    padding-bottom: 80px;
}

/* 悬浮添加按钮样式 */
.floating-add-btn {
    position: fixed;
    bottom: 30px;
    right: 30px;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background-color: #409eff;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    cursor: pointer;
    transition: all 0.3s ease;
    z-index: 100;
}

.floating-add-btn:hover {
    transform: scale(1.1);
    background-color: #337ecc;
}

.plus-icon {
    font-size: 32px;
    line-height: 1;
}
</style>