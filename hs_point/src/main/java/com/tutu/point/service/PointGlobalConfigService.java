package com.tutu.point.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.point.entity.PointGlobalConfig;
import com.tutu.point.mapper.PointGlobalConfigMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分全局配置 Service
 */
@Service
public class PointGlobalConfigService extends ServiceImpl<PointGlobalConfigMapper, PointGlobalConfig> {
    
    /**
     * 获取积分比例配置
     * @return 积分全局配置
     */
    public PointGlobalConfig getConfig() {
        List<PointGlobalConfig> configs = list();
        if (configs.isEmpty()) {
            // 如果没有配置，创建一个默认配置
            PointGlobalConfig config = new PointGlobalConfig();
            config.setPointRatio(BigDecimal.valueOf(0));
            save(config);
            return config;
        }
        return configs.getFirst();
    }
    
    /**
     * 更新积分比例配置
     */
    public void updatePointRatio(PointGlobalConfig pointGlobalConfig) {
        BigDecimal pointRatio = pointGlobalConfig.getPointRatio();
        if (pointRatio == null || pointRatio.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ServiceException("积分比例必须大于0");
        }

        List<PointGlobalConfig> configs = list();
        PointGlobalConfig config;
        if (configs.isEmpty()) {
            config = new PointGlobalConfig();
            config.setPointRatio(pointRatio);
            save(config);
        } else {
            config = configs.getFirst();
            config.setPointRatio(pointRatio);
            updateById(config);
        }
    }
}

