package com.tutu.api.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Service
public class WeChatService {
    @Resource
    private WxMaService wxMaService;

    /**
     * 获取token
     * @return token
     */
    public String getAccessToken()  {
        // 调用微信 API 获取新 Token
        try {
            return wxMaService.getAccessToken();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    public void getwxacodeunlimit(String scene, String page, String checkPath, String envVersion) {

    }
}
