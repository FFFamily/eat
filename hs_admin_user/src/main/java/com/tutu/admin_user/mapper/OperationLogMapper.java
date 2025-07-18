package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    
    /**
     * 分页查询操作日志
     */
    @Select("<script>" +
            "SELECT * FROM operation_log WHERE is_deleted = '0' " +
            "<if test='userId != null and userId != \"\"'> AND user_id = #{userId} </if>" +
            "<if test='operationType != null and operationType != \"\"'> AND operation_type = #{operationType} </if>" +
            "<if test='keyword != null and keyword != \"\"'> " +
            "AND (username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR operation_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR request_url LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time <= #{endTime} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<OperationLog> selectPageByCondition(Page<OperationLog> page, 
                                              @Param("userId") String userId,
                                              @Param("operationType") String operationType,
                                              @Param("keyword") String keyword,
                                              @Param("startTime") Date startTime,
                                              @Param("endTime") Date endTime);
    
    /**
     * 根据用户ID查询操作日志
     */
    @Select("SELECT * FROM operation_log WHERE user_id = #{userId} AND is_deleted = '0' " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<OperationLog> findByUserId(@Param("userId") String userId, @Param("limit") int limit);
    
    /**
     * 根据操作类型查询操作日志
     */
    @Select("SELECT * FROM operation_log WHERE operation_type = #{operationType} AND is_deleted = '0' " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<OperationLog> findByOperationType(@Param("operationType") String operationType, @Param("limit") int limit);
    
    /**
     * 统计今日操作次数
     */
    @Select("SELECT COUNT(*) FROM operation_log WHERE DATE(create_time) = CURDATE() AND is_deleted = '0'")
    Long countTodayOperations();
    
    /**
     * 统计用户今日操作次数
     */
    @Select("SELECT COUNT(*) FROM operation_log WHERE user_id = #{userId} " +
            "AND DATE(create_time) = CURDATE() AND is_deleted = '0'")
    Long countUserTodayOperations(@Param("userId") String userId);
    
    /**
     * 清理指定天数前的日志
     */
    @Select("UPDATE operation_log SET is_deleted = '1' " +
            "WHERE create_time < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int cleanOldLogs(@Param("days") int days);
}
