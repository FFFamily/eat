package com.tutu.api.controller.account;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.util.PasswordUtil;
import com.tutu.user.entity.Account;
import com.tutu.user.service.AccountService;

import cn.hutool.core.util.StrUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/user")
public class UserController {
    @Autowired
    private AccountService accountService;

    /**
     * 创建用户
     * @param account 用户实体
     * @return 创建成功返回用户实体，失败返回 null
     */
    @PostMapping("/create")
    public BaseResponse<Void> createUser(@RequestBody Account account) {
        accountService.create(account);
        return BaseResponse.success();
    }
    
    /**
     * 根据用户类型生成对应的用户账号
     * @param accountType 用户类型
     * @return 用户账号
     */
    @GetMapping("/generateAccountUsername/{accountType}")
    public BaseResponse<String> generateAccountUsername(@RequestParam(required = false) String accountId,@PathVariable String accountType) {
        return BaseResponse.success(accountService.generateAccountUsername(accountId,accountType));
    }
    /**
     * 拿到所有的useType
     * @return
     */
    @GetMapping("/useTypeList")
    public BaseResponse<List<Map<String,String>>> getAllUseType() {
        return BaseResponse.success(accountService.getAllUseType());
    }

    /**
     * 修改用户使用类型
     * @param userId 用户 ID
     * @param useType 使用类型
     * @return 修改成功返回 true，失败返回 false
     */
    @PutMapping("/changeUseType")
    public BaseResponse<Void> changeUseType(@RequestParam String userId, @RequestParam String useType) {
        accountService.changeUseType(userId, useType);
        return BaseResponse.success();
    }

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public BaseResponse<Page<Account>> page(@RequestParam int pageNum, @RequestParam int pageSize,@RequestParam(required = false,defaultValue = "true") Boolean isFilterPass,Account account) {
        Page<Account> page = new Page<>(pageNum, pageSize);
        // 转换密码
        Page<Account> result = accountService.page(page,new LambdaQueryWrapper<Account>()
                .like(StrUtil.isNotBlank(account.getUsername()), Account::getUsername,"%"+account.getUsername()+"%")
                .like(StrUtil.isNotBlank(account.getNickname()), Account::getNickname,"%"+account.getNickname()+"%")
                );
        if(!isFilterPass){
            result.getRecords().forEach(item -> {
                item.setPassword(PasswordUtil.decode(item.getPassword()));
            });
        }else{
            result.getRecords().forEach(item -> {
                item.setPassword(null);
            });
        }
        return BaseResponse.success(result);
    }

    /**
     * 根据用户 ID 获取用户信息
     * @param id 用户 ID
     * @return 用户实体，若不存在则返回 null
     */
    @GetMapping("/info/{id}")
    public BaseResponse getUserById(@PathVariable String id) {
        return BaseResponse.success(accountService.getById(id));
    }

    /**
     * 获取所有用户信息
     * @return 用户列表
     */
    @GetMapping("/list")
    public BaseResponse<List<Account>> getAllUsers() {
        return BaseResponse.success(accountService.list());
    }

    /**
     * 更新用户信息
     * @param account 用户实体
     * @return 更新成功返回 true，失败返回 false
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateUser(@RequestBody Account account) {
        accountService.updateUser(account);
        return BaseResponse.success();
    }

    /**
     * 修改用户状态
     * @param id 用户 ID
     * @return 修改成功返回 true，失败返回 false
     */
    @PutMapping("/changeStatus")
    public BaseResponse<Void> changeStatus(@RequestParam String id) {
        accountService.changeStatus(id);
        return BaseResponse.success();
    }

    /**
     * 根据用户 ID 删除用户
     * @param id 用户 ID
     * @return 删除成功返回 true，失败返回 false
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Void> deleteUser(@PathVariable String id) {
        accountService.removeById(id);
        return BaseResponse.success();
    }
}
