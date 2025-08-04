package com.tutu.api.controller;

import com.tutu.common.Response.BaseResponse;
import com.tutu.lease.entity.LeaseGoodCategory;
import com.tutu.lease.service.LeaseGoodCategoryService;
import com.tutu.system.dto.HomeConfigDto;
import com.tutu.system.entity.config.HomeConfig;
import com.tutu.system.service.HomeConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/home/config")
public class HomeConfigController {

    @Autowired
    private HomeConfigService homeConfigService;
    @Resource
    private LeaseGoodCategoryService leaseGoodCategoryService;

    /**
     * 获取所有首页配置信息
     * @return 首页配置信息列表
     */
    @GetMapping
    public BaseResponse<List<HomeConfig>> getAllHomeConfigs() {
        return BaseResponse.success(homeConfigService.getAllHomeConfigs());
    }

    /**
     * 获取微信端首页配置信息
     * @return 首页配置信息
     */
    @GetMapping("/wx")
    public BaseResponse<HomeConfigDto> getWxConfig() {
        HomeConfigDto homeConfigDto = homeConfigService.getAllHomeConfigs().getFirst().covertToDto();
        List<LeaseGoodCategory> leaseGoodCategories = leaseGoodCategoryService.findByIdList(homeConfigDto.getMallType());
        homeConfigDto.setMallTypeList(leaseGoodCategories.stream().map(i -> {
            HomeConfigDto.MallType mallType = new HomeConfigDto.MallType();
            mallType.setId(i.getId());
            mallType.setName(i.getName());
            return mallType;
        }).collect(Collectors.toList()));
        return BaseResponse.success(homeConfigDto);
    }

    /**
     * 根据 id 获取首页配置信息
     * @param id 配置信息 id
     * @return 首页配置信息
     */
    @GetMapping("/{id}")
    public BaseResponse<HomeConfig> getHomeConfigById(@PathVariable Long id) {
        return BaseResponse.success(homeConfigService.getHomeConfigById(id));
    }

    /**
     * 保存首页配置信息
     * @param homeConfig 首页配置信息
     * @return 保存结果
     */
    @PostMapping
    public BaseResponse<Boolean> saveHomeConfig(@RequestBody HomeConfig homeConfig) {
        return BaseResponse.success(homeConfigService.saveHomeConfig(homeConfig));
    }

    /**
     * 根据 id 更新首页配置信息
     * @param id 配置信息 id
     * @param homeConfig 首页配置信息
     * @return 更新结果
     */
    @PutMapping("/update/{id}")
    public BaseResponse<Boolean> updateHomeConfigById(@PathVariable Long id, @RequestBody HomeConfig homeConfig) {
        return BaseResponse.success(homeConfigService.updateHomeConfigById(id, homeConfig));
    }

    /**
     * 根据 id 删除首页配置信息
     * @param id 配置信息 id
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteHomeConfigById(@PathVariable Long id) {
        return BaseResponse.success(homeConfigService.deleteHomeConfigById(id));
    }

}
