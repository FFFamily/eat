package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.entity.Site;
import com.tutu.recycle.mapper.SiteMapper;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 站点服务类
 */
@Service
public class SiteService extends ServiceImpl<SiteMapper, Site> {

    /**
     * 创建站点
     */
    @Transactional(rollbackFor = Exception.class)
    public Site createSite(Site site) {
        save(site);
        return site;
    }

    /**
     * 根据ID查询站点
     */
    public Site getSiteById(String id) {
        return getById(id);
    }

    /**
     * 更新站点
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSite(Site site) {
        if (site.getId() == null) {
            throw new ServiceException("站点ID不能为空");
        }
        return updateById(site);
    }

    /**
     * 删除站点
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSite(String id) {
        if (StrUtil.isBlank(id)) {
            throw new ServiceException("站点ID不能为空");
        }
        return removeById(id);
    }

    /**
     * 查询所有站点
     */
    public List<Site> getAllSites() {
        return list();
    }

    /**
     * 分页查询站点
     */
    public IPage<Site> getSitesPage(Page<Site> page, String name) {
        LambdaQueryWrapper<Site> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(Site::getName, name);
        }
        return page(page, wrapper);
    }
}
