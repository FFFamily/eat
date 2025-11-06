package com.tutu.point.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.point.constant.UserPointLockConstant;
import com.tutu.point.dto.ExchangePointGoodsDto;
import com.tutu.point.entity.AccountPointDetail;
import com.tutu.point.entity.AccountPointUseDetail;
import com.tutu.point.enums.PointChangeDirectionEnum;
import com.tutu.point.enums.PointChangeTypeEnum;
import com.tutu.point.mapper.AccountPointUseDetailMapper;
import com.tutu.point.vo.AccountPointUseDetailVO;
import com.tutu.user.entity.Account;
import com.tutu.user.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 账户积分使用详情 Service
 */
@Service
public class AccountPointUseDetailService extends ServiceImpl<AccountPointUseDetailMapper, AccountPointUseDetail> {
    @Resource
    private AccountService accountService;

    @Resource
    private AccountPointDetailService accountPointDetailService;
    /**
     * 根据账户ID查询积分使用详情列表（关联查询商品名称）
     * @param accountId 账户ID
     * @return 积分使用详情列表
     */
    public List<AccountPointUseDetail> getByAccountId(String accountId) {
        if (StrUtil.isBlank(accountId)) {
            throw new ServiceException("账户ID不能为空");
        }
        return baseMapper.getByAccountIdWithJoin(accountId);
    }
    
    /**
     * 根据账户ID查询已使用的积分详情
     * @param accountId 账户ID
     * @return 已使用的积分详情列表
     */
    public List<AccountPointUseDetail> getUsedByAccountId(String accountId) {
        if (StrUtil.isBlank(accountId)) {
            throw new ServiceException("账户ID不能为空");
        }
        LambdaQueryWrapper<AccountPointUseDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountPointUseDetail::getAccountId, accountId);
        wrapper.eq(AccountPointUseDetail::getIsUsed, true);
        wrapper.orderByDesc(AccountPointUseDetail::getCreateTime);
        return list(wrapper);
    }
    
    /**
     * 根据账户ID查询未使用的积分详情
     * @param accountId 账户ID
     * @return 未使用的积分详情列表
     */
    public List<AccountPointUseDetail> getUnusedByAccountId(String accountId) {
        if (StrUtil.isBlank(accountId)) {
            throw new ServiceException("账户ID不能为空");
        }
        LambdaQueryWrapper<AccountPointUseDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountPointUseDetail::getAccountId, accountId);
        wrapper.eq(AccountPointUseDetail::getIsUsed, false);
        wrapper.orderByDesc(AccountPointUseDetail::getCreateTime);
        return list(wrapper);
    }
    
    /**
     * 根据兑换码查询积分使用详情
     * @param exchangeCode 兑换码
     * @return 积分使用详情
     */
    public AccountPointUseDetail getByExchangeCode(String exchangeCode) {
        if (StrUtil.isBlank(exchangeCode)) {
            throw new ServiceException("兑换码不能为空");
        }
        LambdaQueryWrapper<AccountPointUseDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountPointUseDetail::getExchangeCode, exchangeCode);
        return getOne(wrapper);
    }
    
    /**
     * 根据积分商品ID查询积分使用详情
     * @param pointGoodsId 积分商品ID
     * @return 积分使用详情列表
     */
    public List<AccountPointUseDetail> getByPointGoodsId(String pointGoodsId) {
        if (StrUtil.isBlank(pointGoodsId)) {
            throw new ServiceException("积分商品ID不能为空");
        }
        LambdaQueryWrapper<AccountPointUseDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountPointUseDetail::getPointGoodsId, pointGoodsId);
        wrapper.orderByDesc(AccountPointUseDetail::getCreateTime);
        return list(wrapper);
    }
    
    /**
     * 分页查询积分使用详情（带用户名和商品名称）
     * @param page 页码
     * @param size 每页条数
     * @param accountId 账户ID（可选）
     * @param pointGoodsId 积分商品ID（可选）
     * @param isUsed 是否已使用（可选）
     * @return 分页结果
     */
    public Page<AccountPointUseDetailVO> pageDetail(Integer page, Integer size, String accountId, String pointGoodsId, Boolean isUsed) {
        Page<AccountPointUseDetailVO> pageObj = new Page<>(page, size);
        return baseMapper.pageDetailWithJoin(pageObj, accountId, pointGoodsId, isUsed);
    }
    /**
     * 兑换商品
     * @param useDetail 积分使用详情
     */
    public void exchangeGoods(AccountPointUseDetail useDetail) {
    }

    /**
     * 创建积分使用详情记录（兑换商品）
     * @param useDetail 积分使用详情
     * @return 积分使用详情
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountPointUseDetail createUseDetail(ExchangePointGoodsDto useDetail) {
        if (StrUtil.isBlank(useDetail.getAccountId())) {
            throw new ServiceException("账户ID不能为空");
        }
        if (StrUtil.isBlank(useDetail.getPointGoodsId())) {
            throw new ServiceException("积分商品ID不能为空");
        }
        // 生成兑换码
        if (StrUtil.isBlank(useDetail.getExchangeCode())) {
            useDetail.setExchangeCode(generateExchangeCode());
        }
        // 默认未使用
        if (useDetail.getIsUsed() == null) {
            useDetail.setIsUsed(false);
        }
        // 新增积分明细记录
        Long point = useDetail.getPoint();
        AccountPointDetail pointDetail = new AccountPointDetail();
        pointDetail.setAccountId(useDetail.getAccountId());
        pointDetail.setChangeDirection(PointChangeDirectionEnum.SUB.getValue());
        pointDetail.setChangePoint(Objects.requireNonNullElse(point, 0L));
        pointDetail.setChangeType(PointChangeTypeEnum.SYSTEM_REWARD.getType());
        pointDetail.setChangeReason(useDetail.getReason());
        pointDetail.setRemark(useDetail.getRemark());
        accountPointDetailService.createDetail(pointDetail);
        // 系统兑换 无需积分
        useDetail.setPoint(0L);
        useDetail.setDetailId(pointDetail.getId());
        save(useDetail);
        return useDetail;
    }
    
    /**
     * 使用兑换码（标记为已使用）
     * @param detail 兑换码详情
     * @return 是否成功
     */
    public boolean useExchangeCode(AccountPointUseDetail detail) {
        AccountPointUseDetail useDetail = getById(detail.getId());
        if (useDetail == null) {
            throw new ServiceException("兑换码不存在");
        }
        if (useDetail.getIsUsed()) {
            throw new ServiceException("兑换码已被使用");
        }
        LambdaUpdateWrapper<AccountPointUseDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AccountPointUseDetail::getId, useDetail.getId())
                    .set(AccountPointUseDetail::getIsUsed, true)
                .set(AccountPointUseDetail::getVoucherImage, detail.getVoucherImage());
        return update(updateWrapper);
    }
    
    /**
     * 生成兑换码
     * @return 兑换码
     */
    private String generateExchangeCode() {
        // 生成格式：POINT + 时间戳后8位 + UUID前8位
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return timestamp.substring(timestamp.length() - 8) + uuid.substring(0, 8).toUpperCase();
    }
    /**
     * 根据ID查询积分使用详情（兑换商品）
     * @param id 积分使用详情ID
     * @return 积分使用详情
     */
    public ExchangePointGoodsDto getInfoById(String id) {
        AccountPointUseDetail detail = getById(id);
        ExchangePointGoodsDto res = new ExchangePointGoodsDto();
        BeanUtils.copyProperties(detail, res);
        // 查询对应的明细
        AccountPointDetail pointDetail = accountPointDetailService.getById(detail.getDetailId());
        if (pointDetail != null) {
            res.setReason(pointDetail.getChangeReason());
            res.setRemark(pointDetail.getRemark());
        }
        return  res;
    }


}

