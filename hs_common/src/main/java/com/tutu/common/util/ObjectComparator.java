package com.tutu.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ObjectComparator {
    @Data
    @AllArgsConstructor
    public static class Diff{
        private String field;
        private String fieldLabel;
        private String oldValue;
        private String newValue;
        public Diff(String field,String oldValue, String newValue) {
            this.field = field;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }

    /**
     * 比较两个对象的差异
     * @param oldObj 旧对象
     * @param newObj 新对象
     * @return 差异列表
     */
    public static List<Diff> compare(Object oldObj, Object newObj) {
        List<Diff> diffs = new ArrayList<>();
        if (oldObj == null || newObj == null) {
            return diffs;
        }
        if (oldObj.getClass() != newObj.getClass()) {
            return diffs;
        }
        JSONObject oldJson = JSONUtil.parseObj(oldObj);
        JSONObject newJson = JSONUtil.parseObj(newObj);
        
        // 比较旧对象中存在的字段，但新对象中不存在的字段
        for (String key : oldJson.keySet()) {
            if (!newJson.containsKey(key)) {
                diffs.add(new Diff(key, oldJson.getStr(key), ""));
            }
        }

        // 比较新对象中存在的字段，但旧对象中不存在的字段
        for (String key : newJson.keySet()) {
            if (!oldJson.containsKey(key)) {
                diffs.add(new Diff(key, "", newJson.getStr(key)));
            }
        }

        // 比较旧对象和新对象中都存在的字段
        for (String key : oldJson.keySet()) {
            if (newJson.containsKey(key)) {
                if (!Objects.equals(oldJson.getStr(key), newJson.getStr(key))) {
                    diffs.add(new Diff(key, oldJson.getStr(key), newJson.getStr(key)));
                }
            }
        }

        return diffs;
    }
}
