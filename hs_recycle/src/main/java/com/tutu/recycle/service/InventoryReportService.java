package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.recycle.dto.InventoryReportDto;
import com.tutu.recycle.mapper.RecycleOrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 库存统计报表服务
 */
@Service
public class InventoryReportService {

    @Autowired
    private RecycleOrderItemMapper recycleOrderItemMapper;

    /**
     * 获取库存统计报表（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 分页的库存统计报表
     */
    public IPage<InventoryReportDto> getInventoryReport(int page, int size) {
        // 参数验证
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100; // 限制最大每页大小
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 获取总数
        long total = recycleOrderItemMapper.selectInventoryReportCount();
        
        // 使用LIMIT查询分页数据
        java.util.List<InventoryReportDto> records = recycleOrderItemMapper.selectInventoryReport(offset, size);
        
        // 创建分页对象
        Page<InventoryReportDto> resultPage = new Page<>(page, size);
        resultPage.setTotal(total);
        resultPage.setRecords(records);
        
        return resultPage;
    }
    
    /**
     * 获取库存报表总数
     * @return 库存报表记录总数
     */
    public long getInventoryReportCount() {
        return recycleOrderItemMapper.selectInventoryReportCount();
    }

}