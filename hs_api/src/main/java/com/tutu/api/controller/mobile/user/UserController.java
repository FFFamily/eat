package com.tutu.api.controller.mobile.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.User;
import com.tutu.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 创建用户
     * @param user 用户实体
     * @return 创建成功返回用户实体，失败返回 null
     */
    @PostMapping("/create")
    public BaseResponse<Void> createUser(@RequestBody User user) {
        userService.create(user);
        return BaseResponse.success();
    }

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public BaseResponse<Page<User>> page(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return BaseResponse.success(userService.page(page));
    }

    /**
     * 根据用户 ID 获取用户信息
     * @param id 用户 ID
     * @return 用户实体，若不存在则返回 null
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getById(id);
    }

    /**
     * 获取所有用户信息
     * @return 用户列表
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.list();
    }

    /**
     * 更新用户信息
     * @param user 用户实体
     * @return 更新成功返回 true，失败返回 false
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateUser(@RequestBody User user) {
        userService.updateById(user);
        return BaseResponse.success();
    }

    /**
     * 修改用户状态
     * @param id 用户 ID
     * @return 修改成功返回 true，失败返回 false
     */
    @PutMapping("/changeStatus")
    public BaseResponse<Void> changeStatus(@RequestParam String id) {
        userService.changeStatus(id);
        return BaseResponse.success();
    }

    /**
     * 根据用户 ID 删除用户
     * @param id 用户 ID
     * @return 删除成功返回 true，失败返回 false
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Void> deleteUser(@PathVariable String id) {
        userService.removeById(id);
        return BaseResponse.success();
    }
}
