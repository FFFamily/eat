package com.tutu.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.system.entity.City;
import com.tutu.system.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市控制器
 */
@RestController
@RequestMapping("/system/city")
public class CityController {

    @Autowired
    private CityService cityService;

    /**
     * 获取所有顶级城市（省份）
     */
    @GetMapping("/top-level")
    public BaseResponse<List<City>> getTopLevelCities() {
        return BaseResponse.success(cityService.getTopLevelCities());
    }

    /**
     * 根据父级编码获取子级城市
     */
    @GetMapping("/children/{pCode}")
    public BaseResponse<List<City>> getChildrenByParentCode(@PathVariable String pCode) {
        return BaseResponse.success(cityService.getByParentCode(pCode));
    }

    /**
     * 根据城市编码获取城市信息
     */
    @GetMapping("/code/{code}")
    public BaseResponse<City> getByCode(@PathVariable String code) {
        return BaseResponse.success(cityService.getByCode(code));
    }

    /**
     * 根据城市名称搜索
     */
    @GetMapping("/search")
    public BaseResponse<List<City>> searchByName(@RequestParam String name) {
        return BaseResponse.success(cityService.searchByName(name));
    }

    /**
     * 根据层级链查询城市
     */
    @GetMapping("/chain")
    public BaseResponse<City> getByChain(@RequestParam String chain) {
        return BaseResponse.success(cityService.getByChain(chain));
    }

    /**
     * 分页查询城市
     */
    @GetMapping("/page")
    public BaseResponse<Page<City>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String pCode) {
        Page<City> page = new Page<>(pageNum, pageSize);
        return BaseResponse.success(cityService.pageQuery(page, name, pCode));
    }

    /**
     * 根据ID获取城市
     */
    @GetMapping("/{id}")
    public BaseResponse<City> getById(@PathVariable String id) {
        return BaseResponse.success(cityService.getById(id));
    }

    /**
     * 创建城市
     */
    @PostMapping("/create")
    public BaseResponse<City> create(@RequestBody City city) {
        try {
            return BaseResponse.success(cityService.createCity(city));
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 更新城市
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody City city) {
        try {
            return BaseResponse.success(cityService.updateCity(city));
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 删除城市
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        try {
            return BaseResponse.success(cityService.deleteCity(id));
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 批量删除城市
     */
    @DeleteMapping("/delete/batch")
    public BaseResponse<Boolean> deleteBatch(@RequestBody List<String> ids) {
        try {
            return BaseResponse.success(cityService.removeByIds(ids));
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}
