package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.User;
import com.tutu.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台用户管理
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Resource
    private UserService userService;

    /**
     * 分页查询所有用户
     * GET /api/users?page=0&size=10
     */
    @GetMapping("/page")
    public BaseResponse<Page<User>> getPageUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        // 使用 PageRequest 进行分页查询
        Page<User> pageParam = new Page<>(page, size);
        Page<User> users = userService.page(pageParam);
        return BaseResponse.success(users);
    }
    /**
     * 查询所有用户
     * GET /api/users
     */
    @GetMapping("/list")
    public BaseResponse<List<User>> getAllUsers() {
        List<User> list = userService.list();
        return BaseResponse.success(list);
    }

    /**
     * 根据ID获取用户详情
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public BaseResponse<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return BaseResponse.success(user);
    }

    /**
     * 创建新用户
     * POST /api/users
     */
    @PostMapping("/create")
    public BaseResponse<User> createUser(@RequestBody User user) {
        userService.save(user);
        return BaseResponse.success();
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public BaseResponse<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return new BaseResponse<>(404, "用户不存在", null);
        }
        BeanUtils.copyProperties(user, existingUser);
        userService.save(existingUser);
        return new BaseResponse<>(200, "更新成功", existingUser);
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteUser(@PathVariable("id") Long id) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return new BaseResponse<>(404, "用户不存在", null);
        }
        userService.removeById(id);
        return new BaseResponse<>(200, "删除成功", null);
    }
}
