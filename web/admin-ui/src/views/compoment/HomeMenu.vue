<template>
    <div class="home-menu-page">
        <div class="menu-item">
            <el-icon :size="24">
                <location />
            </el-icon>
        </div>
        <div class="menu-item">
            <el-icon :size="24">
                <location />
            </el-icon>
        </div>
        <div class="main-menu-item">
            吃
        </div>
        <div class="menu-item">
            <el-icon :size="24">
                <location />
            </el-icon>
        </div>
        <div class="menu-item">
            <el-icon :size="24">
                <location />
            </el-icon>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ElIcon } from 'element-plus'
import {Location,}   from '@element-plus/icons-vue';
</script>
<style scoped lang="scss">
.home-menu-page {
    display: flex;
    position: fixed;
    bottom: 0;
    width: 100%;
    height: 80px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    align-items: center;
    justify-content: space-around;
    // padding: 0 16px;
}

.menu-item {
    border: 2px #606266 ;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    border-radius: 50%;
    transition: all 0.3s ease;
    color: #606266;
    
    &:hover {
        background: rgba(64, 158, 255, 0.1);
        color: #409eff;
    }
}

.main-menu-item {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: #409eff;
    color: white;
    font-weight: bold;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    transform: translateY(-16px);
    transition: all 0.3s ease;
    
    &:hover {
        transform: translateY(-16px) scale(1.05);
    }
}
</style>