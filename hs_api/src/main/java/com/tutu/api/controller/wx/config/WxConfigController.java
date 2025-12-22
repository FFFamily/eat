package com.tutu.api.controller.wx.config;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tutu.common.Response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/wx/config")
public class WxConfigController {
    /**
     * 获取活动配置
     * @return
     */
    @PostMapping("/activity")
    public BaseResponse<Object> getActivityImg(@RequestBody HashMap<String, String> map) {
        JSONObject response = JSONUtil.createObj();
        String activityType = map.get("activityType");
        if (activityType.equals("green")){
            response.set("url", "https://www.funingeco.com/api/files/2025/12/02/8434ebe5b18b414d8fdb77fa88d82982.jpg");
        }else {
            response.set("url", "https://www.funingeco.com/api/files/2025/12/02/f3f22f117c024c42b9f555cd51f7100c.jpg");
        }
        return  BaseResponse.success(response);
    }

    /**
     * 获取活动配置
     * @return
     */
    @PostMapping("/activity/rule")
    public BaseResponse<Object> getActivityruleImg(@RequestBody HashMap<String, String> map) {
        JSONObject response = JSONUtil.createObj();
        String activityType = map.get("activityType");
        if (activityType.equals("green")){
            response.set("url", "https://www.funingeco.com/api/files/2025/12/02/936b06e8e97b4bdeb519bf28c9e1ce85.jpg");
        }else {
            response.set("url", "https://www.funingeco.com/api/files/2025/12/02/27018444853a4f9fa1657f1be7b5dd6e.jpg");
        }
        return  BaseResponse.success(response);
    }
}
