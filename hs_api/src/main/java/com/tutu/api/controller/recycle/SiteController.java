package com.tutu.api.controller.recycle;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.Site;
import com.tutu.recycle.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站点控制器
 */
@RestController
@RequestMapping("/recycle/site")
public class SiteController {

    @Autowired
    private SiteService siteService;

    /**
     * 创建站点
     */
    @PostMapping("/create")
    public BaseResponse<Site> createSite(@RequestBody Site site) {
        Site result = siteService.createSite(site);
        return BaseResponse.success(result);
    }

    /**
     * 根据ID查询站点
     */
    @GetMapping("/detail/{id}")
    public BaseResponse<Site> getSiteById(@PathVariable String id) {
        Site site = siteService.getSiteById(id);
        if (site == null) {
            return BaseResponse.error("站点不存在");
        }
        return BaseResponse.success(site);
    }

    /**
     * 更新站点
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updateSite(@RequestBody Site site) {
        try {
            boolean result = siteService.updateSite(site);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 删除站点
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteSite(@PathVariable String id) {
        try {
            boolean result = siteService.deleteSite(id);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 查询所有站点
     */
    @GetMapping("/list")
    public BaseResponse<List<Site>> getAllSites() {
        List<Site> list = siteService.getAllSites();
        return BaseResponse.success(list);
    }

    /**
     * 分页查询站点
     */
    @GetMapping("/page")
    public BaseResponse<IPage<Site>> getSitesPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        Page<Site> page = new Page<>(current, size);
        IPage<Site> result = siteService.getSitesPage(page, name);
        return BaseResponse.success(result);
    }
}
