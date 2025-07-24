package com.tutu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.system.entity.config.HomeConfig;
import com.tutu.system.mapper.HomeConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeConfigService extends ServiceImpl<HomeConfigMapper, HomeConfig> {

    /**
     * 获取所有首页配置信息
     * @return 首页配置信息列表
     */
    public List<HomeConfig> getAllHomeConfigs() {
        return list(new QueryWrapper<>());
    }

    /**
     * 根据 id 获取首页配置信息
     * @param id 配置信息 id
     * @return 首页配置信息
     */
    public HomeConfig getHomeConfigById(Long id) {
        return getById(id);
    }

    /**
     * 保存首页配置信息
     * @param homeConfig 首页配置信息
     * @return 保存结果
     */
    public boolean saveHomeConfig(HomeConfig homeConfig) {
        return save(homeConfig);
    }

    /**
     * 根据 id 更新首页配置信息
     * @param id 配置信息 id
     * @param homeConfig 首页配置信息
     * @return 更新结果
     */
    public boolean updateHomeConfigById(Long id, HomeConfig homeConfig) {
        homeConfig.setId(id);
        return updateById(homeConfig);
    }

    /**
     * 根据 id 删除首页配置信息
     * @param id 配置信息 id
     * @return 删除结果
     */
    public boolean deleteHomeConfigById(Long id) {
        return removeById(id);
    }
}
