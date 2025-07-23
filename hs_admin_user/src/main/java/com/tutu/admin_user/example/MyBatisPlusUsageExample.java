package com.tutu.admin_user.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.common.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MyBatis Plus使用示例
 * 展示如何使用重构后的Service层
 */
@Component
public class MyBatisPlusUsageExample {
    
    @Autowired
    private AdUserService adUserService;
    
    /**
     * 基本查询示例
     */
    public void basicQueryExample() {
        // 1. 根据ID查询
        AdUser user = adUserService.getById("1");
        
        // 2. 根据用户名查询
        AdUser userByUsername = adUserService.findByUsername("admin");
        
        // 3. 查询所有用户
        List<AdUser> allUsers = adUserService.list();
        
        // 4. 分页查询
        IPage<AdUser> userPage = adUserService.getPageList(1, 10, "admin");
    }
    
    /**
     * 条件查询示例
     */
    public void conditionalQueryExample() {
        // 使用LambdaQueryWrapper进行条件查询
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 1. 等值查询
        queryWrapper.eq(AdUser::getStatus, 1)
                   .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);
        List<AdUser> activeUsers = adUserService.list(queryWrapper);
        
        // 2. 模糊查询
        queryWrapper.clear();
        queryWrapper.like(AdUser::getName, "张")
                   .or()
                   .like(AdUser::getEmail, "@gmail.com");
        List<AdUser> searchResults = adUserService.list(queryWrapper);
        
        // 3. 范围查询
        queryWrapper.clear();
        queryWrapper.in(AdUser::getDeptId, List.of("1", "2", "3"))
                   .isNotNull(AdUser::getEmail);
        List<AdUser> deptUsers = adUserService.list(queryWrapper);
        
        // 4. 排序查询
        queryWrapper.clear();
        queryWrapper.eq(AdUser::getIsDeleted, CommonConstant.NO_STR)
                   .orderByDesc(AdUser::getCreateTime)
                   .orderByAsc(AdUser::getName);
        List<AdUser> sortedUsers = adUserService.list(queryWrapper);
    }
    
    /**
     * 分页查询示例
     */
    public void pageQueryExample() {
        // 创建分页对象
        Page<AdUser> page = new Page<>(1, 10); // 第1页，每页10条
        
        // 创建查询条件
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getStatus, 1)
                   .like(AdUser::getName, "张")
                   .orderByDesc(AdUser::getCreateTime);
        
        // 执行分页查询
        IPage<AdUser> result = adUserService.page(page, queryWrapper);
        
        // 获取结果
        List<AdUser> records = result.getRecords(); // 当前页数据
        long total = result.getTotal(); // 总记录数
        long pages = result.getPages(); // 总页数
        long current = result.getCurrent(); // 当前页
        long size = result.getSize(); // 每页大小
    }
    
    /**
     * 更新操作示例
     */
    public void updateExample() {
        // 1. 根据ID更新
        AdUser user = new AdUser();
        user.setId("1");
        user.setName("新名称");
        user.setEmail("new@email.com");
        adUserService.updateById(user);
        
        // 2. 条件更新
        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getDeptId, "1")
                    .set(AdUser::getStatus, 0); // 将部门1的所有用户状态设为禁用
        adUserService.update(updateWrapper);
        
        // 3. 批量更新
        List<AdUser> users = List.of(
            createUser("user1", "张三"),
            createUser("user2", "李四")
        );
        adUserService.updateBatchById(users);
    }
    
    /**
     * 删除操作示例
     */
    public void deleteExample() {
        // 1. 根据ID删除（逻辑删除）
        adUserService.removeById("1");
        
        // 2. 批量删除
        adUserService.removeByIds(List.of("1", "2", "3"));
        
        // 3. 条件删除
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getStatus, 0)
                   .isNull(AdUser::getEmail);
        adUserService.remove(queryWrapper);
    }
    
    /**
     * 统计查询示例
     */
    public void countExample() {
        // 1. 统计总数
        long totalCount = adUserService.count();
        
        // 2. 条件统计
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getStatus, 1)
                   .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);
        long activeUserCount = adUserService.count(queryWrapper);
        
        // 3. 检查是否存在
        queryWrapper.clear();
        queryWrapper.eq(AdUser::getUsername, "admin");
        boolean exists = adUserService.count(queryWrapper) > 0;
    }
    
    /**
     * 复杂查询示例
     */
    public void complexQueryExample() {
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 复杂条件组合
        queryWrapper.eq(AdUser::getIsDeleted, CommonConstant.NO_STR)
                   .and(wrapper -> wrapper
                       .eq(AdUser::getStatus, 1)
                       .or()
                       .isNull(AdUser::getStatus)
                   )
                   .and(wrapper -> wrapper
                       .like(AdUser::getName, "张")
                       .or()
                       .like(AdUser::getEmail, "@company.com")
                   )
                   .in(AdUser::getDeptId, List.of("1", "2", "3"))
                   .orderByDesc(AdUser::getCreateTime)
                   .last("LIMIT 100"); // 限制结果数量
        
        List<AdUser> complexResults = adUserService.list(queryWrapper);
    }
    
    /**
     * 创建用户辅助方法
     */
    private AdUser createUser(String username, String name) {
        AdUser user = new AdUser();
        user.setUsername(username);
        user.setName(name);
        user.setStatus(1);
        return user;
    }
}
