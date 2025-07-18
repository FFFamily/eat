package com.tutu.admin_user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.DepartmentDTO;
import com.tutu.admin_user.entity.Department;
import com.tutu.admin_user.service.DepartmentService;
import com.tutu.common.Response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/admin/department")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;
    
    /**
     * 分页查询部门列表
     */
    @GetMapping("/page")
    public BaseResponse<IPage<Department>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<Department> page = departmentService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询部门树
     */
    @GetMapping("/tree")
    public BaseResponse<List<Department>> getDepartmentTree() {
        List<Department> tree = departmentService.getDepartmentTree();
        return BaseResponse.success(tree);
    }
    
    /**
     * 查询所有启用的部门
     */
    @GetMapping("/all")
    public BaseResponse<List<Department>> findAllEnabled() {
        List<Department> departments = departmentService.findAllEnabled();
        return BaseResponse.success(departments);
    }
    
    /**
     * 根据ID查询部门详情
     */
    @GetMapping("/{id}")
    public BaseResponse<Department> getById(@PathVariable String id) {
        Department department = departmentService.getById(id);
        if (department == null) {
            return BaseResponse.error("部门不存在");
        }
        return BaseResponse.success(department);
    }
    
    /**
     * 创建部门
     */
    @PostMapping
    public BaseResponse<String> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        try {
            boolean result = departmentService.createDepartment(departmentDTO);
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
    public BaseResponse<String> updateDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        try {
            boolean result = departmentService.updateDepartment(departmentDTO);
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
            boolean result = departmentService.deleteDepartment(id);
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
            boolean result = departmentService.batchDeleteDepartments(ids);
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
