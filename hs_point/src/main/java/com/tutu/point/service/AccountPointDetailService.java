package com.tutu.point.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.point.constant.UserPointLockConstant;
import com.tutu.point.entity.AccountPointDetail;
import com.tutu.point.entity.AccountPointUseDetail;
import com.tutu.point.enums.PointChangeDirectionEnum;
import com.tutu.point.enums.PointChangeTypeEnum;
import com.tutu.point.mapper.AccountPointDetailMapper;
import com.tutu.user.entity.Account;
import com.tutu.user.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 账户积分详情 Service
 */
@Service
public class AccountPointDetailService extends ServiceImpl<AccountPointDetailMapper, AccountPointDetail> {

    @Resource
    private AccountService accountService;

    /**
     * 根据账户ID查询积分详情列表（带账户名称）
     * @param accountId 账户ID
     * @return 积分详情列表
     */
    public List<AccountPointDetail> getByAccountId(String accountId) {
        if (StrUtil.isBlank(accountId)) {
            throw new ServiceException("账户ID不能为空");
        }
        return baseMapper.getByAccountIdWithJoin(accountId);
    }
    
    /**
     * 根据账户ID和变更类型查询积分详情（带账户名称）
     * @param accountId 账户ID
     * @param changeType 变更类型
     * @return 积分详情列表
     */
    public List<AccountPointDetail> getByAccountIdAndChangeType(String accountId, String changeType) {
        if (StrUtil.isBlank(accountId)) {
            throw new ServiceException("账户ID不能为空");
        }
        return baseMapper.getByAccountIdAndChangeTypeWithJoin(accountId, changeType);
    }
    
    /**
     * 分页查询积分详情（带账户名称）
     * @param page 页码
     * @param size 每页条数
     * @param accountId 账户ID（可选）
     * @param changeType 变更类型（可选）
     * @param changeDirection 变更方向（可选）
     * @return 分页结果
     */
    public Page<AccountPointDetail> pageDetail(Integer page, Integer size, String accountId, String changeType, String changeDirection) {
        Page<AccountPointDetail> pageObj = new Page<>(page, size);
        return baseMapper.pageDetailWithJoin(pageObj, accountId, changeType, changeDirection);
    }
    
    /**
     * 创建积分详情记录
     * @param detail 积分详情
     * @return 积分详情
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountPointDetail createDetail(AccountPointDetail detail) {
        if (StrUtil.isBlank(detail.getAccountId())) {
            throw new ServiceException("账户ID不能为空");
        }
        if (detail.getChangePoint() == null) {
            throw new ServiceException("变更积分不能为空");
        }
        Account account = accountService.getById(detail.getAccountId());
        synchronized (UserPointLockConstant.POINT_LOCK_KEY + detail.getAccountId().intern()){
            account.setPoint(account.getPoint() + PointChangeDirectionEnum.getPointValue(detail.getChangeDirection(), detail.getChangePoint()));
            accountService.updateById(account);
            save(detail);
        }
        return detail;
    }
    /**
     * 根据账户ID查询积分
     * @param accountId 账户ID
     * @return 积分
     */
    public long getPointByAccountId(String accountId) {
        if (StrUtil.isBlank(accountId)) {
            throw new ServiceException("账户ID不能为空");
        }
        Account account = accountService.getById(accountId);
        if (account == null) {
            throw new ServiceException("账户不存在");
        }
        return account.getPoint();
    }

    /**
     * 消耗积分
     * 和商品无关
     * @param useDetail 消耗积分详情
     */
    @Transactional(rollbackFor = Exception.class)
    public void exchangePoint(AccountPointDetail useDetail) {
        useDetail.setChangeDirection(PointChangeDirectionEnum.SUB.getValue());
        createDetail(useDetail);
    }

    /**
     * 获取对应活动的消耗积分
     * @param accountId
     * @param mark
     * @return
     */
    public Long getAccountActivityPoint(String accountId, String mark) {
        List<AccountPointDetail> list = list(new LambdaQueryWrapper<AccountPointDetail>()
                .eq(AccountPointDetail::getAccountId, accountId)
                .eq(AccountPointDetail::getMark, mark));
        return list.stream().mapToLong(AccountPointDetail::getChangePoint).sum();
    }
}

