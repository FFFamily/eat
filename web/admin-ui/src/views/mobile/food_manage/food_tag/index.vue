<template>
    <div class="tag-management-page">
        <!-- 标签列表内容 -->
        <div class="tag-list">
            <!-- 现有标签列表内容 -->
            <el-table :data="tags" stripe>
                <el-table-column prop="name" label="标签名称">
                    <template #default="scope">
                        <span @click="handleRowClick(scope.row)">{{ scope.row.name }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" fixed="right" min-width="120">
                    <template #default="scope">
                        <el-button @click.prevent="deleteTag(scope.row.id)" type="danger" :icon="Delete" circle />
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 悬浮添加按钮 -->
        <div class="floating-add-btn" @click="openAddDialog">
            <span class="plus-icon">+</span>
        </div>

        <!-- 添加标签对话框 -->
        <el-dialog v-model="dialogVisible" title="添加标签" width="90%">
            <el-form :model="newTag" ref="tagForm">
                <el-form-item label="标签名称" prop="name"
                    :rules="[{ required: true, message: '请输入标签名称', trigger: 'blur' }]">
                    <el-input v-model="newTag.name" placeholder="请输入标签名称"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAddTag">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { createFoodTag, getAllFoodTagList, deleteFoodTag } from '~/api/food/foodTagApi';
import { ElMessage } from 'element-plus';
import {
    Delete
} from '@element-plus/icons-vue'

// 对话框状态
const dialogVisible = ref(false);
// 新标签数据
const newTag = ref({ name: '' });
// 表单引用
const tagForm = ref(null);
// 标签列表
const tags = ref([]);
onMounted(() => {
    searchAllFoodTagList();
})

// 获取所有标签
const searchAllFoodTagList = async () => {
    try {
        const res = await getAllFoodTagList();
        tags.value = res.data;
    } catch (error) {
        ElMessage.error('获取标签列表失败');
    }
};

// 删除标签
const deleteTag = async (id) => {
    await deleteFoodTag(id);
    searchAllFoodTagList();
};

// 打开添加对话框
const openAddDialog = () => {
    newTag.value = { name: '' };
    dialogVisible.value = true;
};

// 处理添加标签
const handleAddTag = async () => {
    try {
        // 表单验证
        await tagForm.value.validate();
        // 调用API添加标签
        await createFoodTag(newTag.value);
        ElMessage.success('标签添加成功');
        dialogVisible.value = false;
        // 刷新标签列表（需实现列表刷新逻辑）
        searchAllFoodTagList();
    } catch (error) {
        if (error.name !== 'ValidationError') {
            ElMessage.error('标签添加失败');
        }
    }
};
</script>

<style scoped>
.tag-management-page {
    position: relative;
    min-height: 100vh;
    padding-bottom: 80px;
    /* 避免底部内容被按钮遮挡 */
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