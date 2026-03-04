package com.tutu.system.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.entity.Announcement;
import com.tutu.system.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService extends ServiceImpl<AnnouncementMapper, Announcement> {

    public IPage<Announcement> getPageList(int current, int size, String keyword, Integer status) {
        int pageNo = Math.max(current, 1);
        int pageSize = Math.min(Math.max(size, 1), 200);
        Page<Announcement> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Announcement> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(Announcement::getTitle, keyword)
                    .or()
                    .like(Announcement::getContent, keyword));
        }
        qw.eq(status != null, Announcement::getStatus, status);
        qw.orderByDesc(Announcement::getCreateTime);
        return page(page, qw);
    }

    public boolean createAnnouncement(Announcement announcement) {
        validatePayload(announcement);
        announcement.setId(null);
        if (announcement.getStatus() == null) {
            announcement.setStatus(CommonConstant.YES_INT);
        } else {
            validateStatus(announcement.getStatus());
        }
        return save(announcement);
    }

    public boolean updateAnnouncement(String id, Announcement announcement) {
        Announcement existing = getById(id);
        if (existing == null) {
            throw new ServiceException("公告不存在");
        }
        validatePayload(announcement);
        existing.setTitle(announcement.getTitle().trim());
        existing.setContent(announcement.getContent());
        return updateById(existing);
    }

    public boolean updateStatus(String id, Integer status) {
        Announcement existing = getById(id);
        if (existing == null) {
            throw new ServiceException("公告不存在");
        }
        validateStatus(status);
        existing.setStatus(status);
        return updateById(existing);
    }

    public List<Announcement> listHomeAnnouncements(Integer limit) {
        int safeLimit = limit == null ? 5 : Math.min(Math.max(limit, 1), 20);
        LambdaQueryWrapper<Announcement> qw = new LambdaQueryWrapper<>();
        qw.eq(Announcement::getStatus, CommonConstant.YES_INT);
        qw.orderByDesc(Announcement::getCreateTime);
        qw.last("LIMIT " + safeLimit);
        return list(qw);
    }

    private void validatePayload(Announcement announcement) {
        if (announcement == null) {
            throw new ServiceException("请求参数不能为空");
        }
        if (StrUtil.isBlank(announcement.getTitle())) {
            throw new ServiceException("公告标题不能为空");
        }
        if (StrUtil.isBlank(announcement.getContent())) {
            throw new ServiceException("公告内容不能为空");
        }
        String title = announcement.getTitle().trim();
        if (title.length() > 100) {
            throw new ServiceException("公告标题长度不能超过100");
        }
        if (announcement.getContent().length() > 5000) {
            throw new ServiceException("公告内容长度不能超过5000");
        }
        announcement.setTitle(title);
    }

    private void validateStatus(Integer status) {
        if (status == null || (status != CommonConstant.YES_INT && status != CommonConstant.NO_INT)) {
            throw new ServiceException("公告状态非法");
        }
    }
}
