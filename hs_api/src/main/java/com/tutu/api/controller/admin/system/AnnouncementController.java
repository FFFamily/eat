package com.tutu.api.controller.admin.system;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.PermissionRequired;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.entity.Announcement;
import com.tutu.system.service.AnnouncementService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/announcement")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private AdUserService adUserService;

    @PermissionRequired("announcement:list")
    @GetMapping("/page")
    public BaseResponse<IPage<Announcement>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        return BaseResponse.success(announcementService.getPageList(current, size, keyword, status));
    }

    @PermissionRequired("announcement:list")
    @GetMapping("/{id}")
    public BaseResponse<Announcement> getById(@PathVariable String id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return BaseResponse.error("公告不存在");
        }
        return BaseResponse.success(announcement);
    }

    @PermissionRequired("announcement:create")
    @PostMapping("/create")
    public BaseResponse<Boolean> create(@RequestBody Announcement announcement) {
        return BaseResponse.success(announcementService.createAnnouncement(announcement));
    }

    @PermissionRequired("announcement:update")
    @PutMapping("/update/{id}")
    public BaseResponse<Boolean> update(@PathVariable String id, @RequestBody Announcement announcement) {
        return BaseResponse.success(announcementService.updateAnnouncement(id, announcement));
    }

    @PermissionRequired("announcement:publish")
    @PutMapping("/status/{id}")
    public BaseResponse<Boolean> updateStatus(@PathVariable String id, @RequestParam Integer status) {
        return BaseResponse.success(announcementService.updateStatus(id, status));
    }

    @PermissionRequired("announcement:delete")
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(announcementService.removeById(id));
    }

    @GetMapping("/home")
    public BaseResponse<List<Announcement>> home(@RequestParam(required = false) Integer limit) {
        String loginId = StpUtil.getLoginIdAsString();
        if (adUserService.getById(loginId) == null) {
            throw new ServiceException("仅后台用户可访问公告");
        }
        return BaseResponse.success(announcementService.listHomeAnnouncements(limit));
    }
}
