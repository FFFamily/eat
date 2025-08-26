package com.tutu.common.util;

public class PageUtil {
    public static Integer calculateStartPage(Integer pageNum, Integer pageSize) {
        return (pageNum - 1) * pageSize;
    }
}
