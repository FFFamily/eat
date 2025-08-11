package com.tutu.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.lease.dto.LeaseGoodDto;
import com.tutu.lease.entity.LeaseGood;
import com.tutu.lease.enums.LeaseGoodStatusEnum;
import com.tutu.lease.service.LeaseGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/lease/good")
public class LeaseGoodController {

    @Autowired
    private LeaseGoodService leaseGoodService;

    @GetMapping("/list")
    public BaseResponse<List<LeaseGood>> list() {
        return BaseResponse.success(leaseGoodService.list());
    }

    @GetMapping("/{id}")
    public BaseResponse<LeaseGood> getById(@PathVariable String id) {
        return BaseResponse.success(leaseGoodService.getById(id));
    }

    @PostMapping("/create")
    public BaseResponse<Boolean> save(@RequestBody LeaseGood leaseGood) {
        return BaseResponse.success(leaseGoodService.save(leaseGood));
    }

    @PutMapping("/update")
    public BaseResponse<Void> update(@RequestBody LeaseGood leaseGood) {
        leaseGoodService.updateById(leaseGood);
        return BaseResponse.success();
    }

    /**
     * 删除商品
     * @param id 商品id
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Void> remove(@PathVariable String id) {
        leaseGoodService.removeById(id);
        return BaseResponse.success();
    }

    /**
     * 分页查询商品
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 结果
     */
    @GetMapping("/page")
    public BaseResponse<Page<LeaseGoodDto>> page(@RequestParam Long pageNum, @RequestParam Long pageSize, LeaseGood leaseGood) {
        return BaseResponse.success(leaseGoodService.getByPage(pageNum, pageSize, leaseGood));
    }

    /**
     * 根据类型查询
     * @return 列表
     */
    @PostMapping("/wx/by/type")
    public BaseResponse<List<LeaseGood>> pageByType(@RequestBody HashMap<String,String> data) {
        String type = data.get("type");
        LambdaQueryWrapper<LeaseGood> queryWrapper = new LambdaQueryWrapper<LeaseGood>()
                .eq(LeaseGood::getStatus, LeaseGoodStatusEnum.UP.getCode())
                .orderByDesc(LeaseGood::getCreateTime);
        if (StrUtil.isNotBlank(type) && ! "all".equals(type)){
            queryWrapper.eq(LeaseGood::getType, type);
        }
        return BaseResponse.success(leaseGoodService.list(queryWrapper));
    }

    /**
     * 改变状态
     * @param goodId 商品id
     * @param status 状态
     * @return 结果
     */
    @PutMapping("/changeStatus")
    public BaseResponse<String> changeStatus(@RequestParam String goodId,@RequestParam String status) {
        LeaseGood leaseGood = leaseGoodService.getById(goodId);
        if (leaseGood == null) {
            return BaseResponse.error("商品不存在");
        }
        leaseGood.setStatus(status);
        leaseGoodService.updateById(leaseGood);
        return BaseResponse.success();
    }
}