package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.entity.AdUserTenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户-租户成员关系 Mapper（全局表）。
 */
@Mapper
public interface AdUserTenantMapper extends BaseMapper<AdUserTenant> {

    /**
     * 查询某租户下的用户分页列表（join ad_user + ad_user_tenant），并将成员表的 dept_id 映射到 AdUser.deptId（exist=false 字段）。
     */
    IPage<AdUser> selectTenantUserPage(
            Page<AdUser> page,
            @Param("tenantId") String tenantId,
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("deptId") String deptId,
            @Param("excludeUserIds") java.util.List<String> excludeUserIds
    );
}
