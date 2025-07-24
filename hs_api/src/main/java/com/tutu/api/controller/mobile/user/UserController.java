package com.tutu.api.controller.mobile.user;

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
    @PostMapping
    public User createUser(@RequestBody User user) {
        if (userService.save(user)) {
            return user;
        }
        return null;
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
    @PutMapping
    public boolean updateUser(@RequestBody User user) {
        return userService.updateById(user);
    }

    /**
     * 根据用户 ID 删除用户
     * @param id 用户 ID
     * @return 删除成功返回 true，失败返回 false
     */
    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable String id) {
        return userService.removeById(id);
    }
}
