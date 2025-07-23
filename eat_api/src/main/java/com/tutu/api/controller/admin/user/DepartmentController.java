package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.AdDepartmentDTO;
import com.tutu.admin_user.entity.AdDepartment;
import com.tutu.admin_user.service.AdDepartmentService;
import com.tutu.common.Response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/admin/department")
public class DepartmentController {
    
    @Autowired
    private AdDepartmentService adDepartmentService;
    
    /**
     * 分页查询部门列表
     */
    @GetMapping("/page")
    public BaseResponse<IPage<AdDepartment>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<AdDepartment> page = adDepartmentService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询部门树
     */
    @GetMapping("/tree")
    public BaseResponse<List<AdDepartment>> getDepartmentTree() {
        List<AdDepartment> tree = adDepartmentService.getDepartmentTree();
        return BaseResponse.success(tree);
    }
    
    /**
     * 查询所有启用的部门
     */
    @GetMapping("/all")
    public BaseResponse<List<AdDepartment>> findAllEnabled() {
        List<AdDepartment> adDepartments = adDepartmentService.findAllEnabled();
        return BaseResponse.success(adDepartments);
    }
    
    /**
     * 根据ID查询部门详情
     */
    @GetMapping("/{id}")
    public BaseResponse<AdDepartment> getById(@PathVariable String id) {
        AdDepartment adDepartment = adDepartmentService.getById(id);
        if (adDepartment == null) {
            return BaseResponse.error("部门不存在");
        }
        return BaseResponse.success(adDepartment);
    }
    
    /**
     * 创建部门
     */
    @PostMapping
    public BaseResponse<String> createDepartment(@Valid @RequestBody AdDepartment adDepartmentDTO) {
        try {
            boolean result = adDepartmentService.createDepartment(adDepartmentDTO);
            if (result) {
                return BaseResponse.success("创建成功");
            } else {
                return BaseResponse.error("创建失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新部门
     */
    @PutMapping
    public BaseResponse<String> updateDepartment(@Valid @RequestBody AdDepartment adDepartmentDTO) {
        try {
            boolean result = adDepartmentService.updateDepartment(adDepartmentDTO);
            if (result) {
                return BaseResponse.success("更新成功");
            } else {
                return BaseResponse.error("更新失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public BaseResponse<String> deleteDepartment(@PathVariable String id) {
        try {
            boolean result = adDepartmentService.deleteDepartment(id);
            if (result) {
                return BaseResponse.success("删除成功");
            } else {
                return BaseResponse.error("删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除部门
     */
    @DeleteMapping("/batch")
    public BaseResponse<String> batchDeleteDepartments(@RequestBody List<String> ids) {
        try {
            boolean result = adDepartmentService.batchDeleteDepartments(ids);
            if (result) {
                return BaseResponse.success("批量删除成功");
            } else {
                return BaseResponse.error("批量删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}
