package com.tutu.api.controller.mobile.food;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.entity.BaseEntity;
import com.tutu.food.entity.history.EatHistory;
import com.tutu.food.service.EatHistoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/food/history")
public class UserFoodHistoryController {
    @Resource
    private EatHistoryService eatHistoryService;

    /**
     * 创建饮食历史记录
     * @param eatHistory 饮食历史记录实体
     * @return 操作结果
     */
    @PostMapping
    public String createFoodHistory(@RequestBody EatHistory eatHistory) {
        if (eatHistoryService.save(eatHistory)) {
            return "创建成功";
        }
        return "创建失败";
    }

    /**
     * 获取所有饮食历史记录
     * @return 饮食历史记录列表
     */
    @GetMapping("/list")
    public BaseResponse<Map<String, List<EatHistory>>> getAllFoodHistories() {
        List<EatHistory> list = eatHistoryService.list();
        if (CollUtil.isNotEmpty(list)) {
            Map<String, List<EatHistory>> collect = list.stream().collect(Collectors.groupingBy(item -> DateUtil.format(item.getCreateTime(), "yyyy-MM-dd")));
            return BaseResponse.success(collect);
        }
        return BaseResponse.success(Map.of());
    }

    /**
     * 根据 ID 获取饮食历史记录
     * @param id 饮食历史记录 ID
     * @return 饮食历史记录实体
     */
    @GetMapping("/{id}")
    public EatHistory getFoodHistoryById(@PathVariable String id) {
        return eatHistoryService.getById(id);
    }

    /**
     * 更新饮食历史记录
     * @param eatHistory 饮食历史记录实体
     * @return 操作结果
     */
    @PutMapping
    public String updateFoodHistory(@RequestBody EatHistory eatHistory) {
        if (eatHistoryService.updateById(eatHistory)) {
            return "更新成功";
        }
        return "更新失败";
    }

    /**
     * 根据 ID 删除饮食历史记录
     * @param id 饮食历史记录 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public String deleteFoodHistory(@PathVariable String id) {
        if (eatHistoryService.removeById(id)) {
            return "删除成功";
        }
        return "删除失败";
    }
}
