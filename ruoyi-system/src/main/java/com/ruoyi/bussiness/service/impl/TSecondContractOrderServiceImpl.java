package com.ruoyi.bussiness.service.impl;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.ruoyi.bussiness.domain.*;
import com.ruoyi.bussiness.domain.setting.AddMosaicSetting;
import com.ruoyi.bussiness.domain.setting.Setting;
import com.ruoyi.bussiness.mapper.TAppAssetMapper;
import com.ruoyi.bussiness.mapper.TAppUserMapper;
import com.ruoyi.bussiness.mapper.TSecondCoinConfigMapper;
import com.ruoyi.bussiness.mapper.TSecondContractOrderMapper;
import com.ruoyi.bussiness.service.*;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.CachePrefix;
import com.ruoyi.common.enums.CommonEnum;
import com.ruoyi.common.enums.RecordEnum;
import com.ruoyi.common.enums.SettingEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.OrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;


/**
 * 秒合约订单Service业务层处理
 *
 * @author ruoyi
 * @date 2023-07-13
 */
@Service
@Slf4j
public class TSecondContractOrderServiceImpl extends ServiceImpl<TSecondContractOrderMapper, TSecondContractOrder> implements ITSecondContractOrderService
{
    @Resource
    private TSecondContractOrderMapper tSecondContractOrderMapper;
    @Resource
    private TSecondCoinConfigMapper tSecondCoinConfigMapper;
    @Resource
    private ITSecondPeriodConfigService itSecondPeriodConfigService;
    @Resource
    private TAppUserMapper appUserMapper;
    @Resource
    private ITAppAssetService assetService;
    @Resource
    private ITAppWalletRecordService appWalletRecordService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private SettingService settingService;
    @Resource
    private TAppAssetMapper tAppAssetMapper;
    @Resource
    private TAppWalletRecordServiceImpl tAppWalletRecordService;
    @Resource
    private TAppUserMapper tAppUserMapper;


    /**
     * 查询秒合约订单
     *
     * @param id 秒合约订单主键
     * @return 秒合约订单
     */
    @Override
    public TSecondContractOrder selectTSecondContractOrderById(Long id)
    {
        return tSecondContractOrderMapper.selectTSecondContractOrderById(id);
    }

    /**
     * 查询秒合约订单列表
     *
     * @param tSecondContractOrder 秒合约订单
     * @return 秒合约订单
     */
    @Override
    public List<TSecondContractOrder> selectTSecondContractOrderList(TSecondContractOrder tSecondContractOrder)
    {
        List<TSecondContractOrder> tSecondContractOrders = tSecondContractOrderMapper.selectTSecondContractOrderList(tSecondContractOrder);
        for (TSecondContractOrder secondContractOrder : tSecondContractOrders) {
            if(Objects.equals(CommonEnum.ZERO.getCode(), secondContractOrder.getStatus())){
                long time = (secondContractOrder.getCloseTime() - new Date().getTime()) / 1000;
                secondContractOrder.setTime((int) time);
            }else {
                secondContractOrder.setTime(0);
            }
        }
        return tSecondContractOrders;
    }

    /**
     * 新增秒合约订单
     *
     * @param tSecondContractOrder 秒合约订单
     * @return 结果
     */
    @Override
    public int insertTSecondContractOrder(TSecondContractOrder tSecondContractOrder)
    {
        tSecondContractOrder.setCreateTime(DateUtils.getNowDate());
        return tSecondContractOrderMapper.insertTSecondContractOrder(tSecondContractOrder);
    }

    /**
     * 修改秒合约订单
     *
     * @param tSecondContractOrder 秒合约订单
     * @return 结果
     */
    @Override
    public int updateTSecondContractOrder(TSecondContractOrder tSecondContractOrder)
    {
        return tSecondContractOrderMapper.updateTSecondContractOrder(tSecondContractOrder);
    }

    /**
     * 批量删除秒合约订单
     *
     * @param ids 需要删除的秒合约订单主键
     * @return 结果
     */
    @Override
    public int deleteTSecondContractOrderByIds(Long[] ids)
    {
        return tSecondContractOrderMapper.deleteTSecondContractOrderByIds(ids);
    }

    /**
     * 删除秒合约订单信息
     *
     * @param id 秒合约订单主键
     * @return 结果
     */
    @Override
    public int deleteTSecondContractOrderById(Long id)
    {
        return tSecondContractOrderMapper.deleteTSecondContractOrderById(id);
    }

    @Override
    public String createSecondContractOrder(TSecondContractOrder order) {
        log.info("下单"+ JSONObject.toJSONString(order));
        try{
            String serialId = "R" + OrderUtils.generateOrderNum();
            long loginIdAsLong = StpUtil.getLoginIdAsLong();
            BigDecimal amount = order.getBetAmount();
            TAppUser user = appUserMapper.selectTAppUserByUserId(loginIdAsLong);
            TSecondPeriodConfig secondPeriodConfig = itSecondPeriodConfigService.getById(order.getPeriodId());
            //判断金额上下限
            if(secondPeriodConfig.getMaxAmount().compareTo(amount)<0 || secondPeriodConfig.getMinAmount().compareTo(amount) > 0){
                return MessageUtils.message("order_amount_limit");
            }
            //1. 时间判断， 不能太频繁

            // 平台资产
            Map<String, TAppAsset> assetMap=assetService.getAssetByUserIdList(user.getUserId());
            TAppAsset asset=assetMap.get(order.getBaseSymbol().toLowerCase()+user.getUserId());
            if ( asset.getAvailableAmount().compareTo(amount) < 0) {
                return MessageUtils.message("order_amount_error");
            }
            //根据ID 查看时间 周期

            //下单
            Date date = new Date();
            order.setRate(secondPeriodConfig.getOdds());
            order.setType(secondPeriodConfig.getPeriod().intValue());
            order.setRateFlag(secondPeriodConfig.getFlag());
            order.setUserId(user.getUserId());
            order.setOrderNo(serialId);
            order.setCreateTime(date);
            order.setCloseTime(date.getTime()+(order.getType()-2)* 1000L);//60秒后的时间)
            order.setUserAddress(user.getAddress());
            order.setStatus(0);
            order.setBetAmount(order.getBetAmount());
            order.setRewardAmount(BigDecimal.ZERO);
            //当前币种最新价格
            BigDecimal price = redisCache.getCacheObject(CachePrefix.CURRENCY_PRICE.getPrefix() + order.getCoinSymbol().toLowerCase());
            TSecondCoinConfig one = tSecondCoinConfigMapper.selectOne(new LambdaQueryWrapper<TSecondCoinConfig>().eq(TSecondCoinConfig::getCoin, order.getCoinSymbol().toUpperCase()));
            if(one != null && 2!=one.getType()){
                price=redisCache.getCacheObject(CachePrefix.CURRENCY_PRICE.getPrefix() + order.getCoinSymbol().toUpperCase());
            }
            order.setOpenPrice(price);
            order.setSign(0);
            order.setManualIntervention(1);
            order.setAdminParentIds(user.getAdminParentIds());
            //先扣钱，再下单
            if (redisCache.tryLock(CachePrefix.USER_WALLET.getPrefix() + user.getUserId(), user.getUserId(), 1000)) {
                //成功的情况下才进行加锁限制
                if (!redisCache.hasKey(CachePrefix.ORDER_SECOND_CONTRACT.getPrefix() + user.getUserId())) {
                    redisCache.setCacheObject(CachePrefix.ORDER_SECOND_CONTRACT.getPrefix() + user.getUserId(), serialId, 10000, TimeUnit.MILLISECONDS);
                } else {
                    return MessageUtils.message("order_10s_retry");
                }
                BigDecimal availableAmount = asset.getAvailableAmount();
                asset.setAmout(asset.getAmout().subtract(amount));
                asset.setAvailableAmount(availableAmount.subtract(amount));
                assetService.updateByUserId(asset);
                appWalletRecordService.generateRecord(user.getUserId(), amount, RecordEnum.OPTION_BETTING.getCode(), user.getLoginName(), serialId, RecordEnum.OPTION_BETTING.getInfo(), availableAmount, availableAmount.subtract(amount),order.getBaseSymbol(),user.getAdminParentIds());
                this.insertTSecondContractOrder(order);
                log.debug("下注提交成功, userId:{}, orderId:{}, money:{}", user.getUserId(), serialId, amount);

                //秒合约打码
                Setting setting = settingService.get(SettingEnum.ADD_MOSAIC_SETTING.name());
                if (Objects.nonNull(setting)){
                    AddMosaicSetting addMosaic = JSONUtil.toBean(setting.getSettingValue(), AddMosaicSetting.class);
                    if (Objects.nonNull(addMosaic) && Objects.nonNull(addMosaic.getIsOpen()) && addMosaic.getIsOpen() && Objects.nonNull(addMosaic.getSencordIsOpen()) && addMosaic.getSencordIsOpen()){
                        user.setTotleAmont(user.getTotleAmont().add(order.getBetAmount()));
                        appUserMapper.updateTotleAmont(user);
                    }
                }

                return String.valueOf(order.getId());
            }else {
                return MessageUtils.message("withdraw.refresh");
            }
        }catch (Exception e){
            log.info(JSONObject.toJSONString(e));
        }
        return  MessageUtils.message("withdraw.refresh");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCoverPosition(TSecondContractOrder tSecondContractOrderList) {
        if (tSecondContractOrderList != null){
                //判断是否全选
                if(tSecondContractOrderList.getIsAll()){
                    // 1.全选补仓操作

                    List<TSecondContractOrder> list = selectTSecondContractOrderList(tSecondContractOrderList);

                    // 2. 将订单中的所有用户id 按填写的补仓比例，进行补仓
                    for (TSecondContractOrder contractOrder : list) {
                        // 2.1 判断订单是否需要补仓(输的补仓)
                        if(contractOrder.getOpenResult().equals("2")){
                            // 进行补仓操作 即修改用户余额，生成帐变信息
                            TAppAsset appAsset = tAppAssetMapper.selectOne(new LambdaQueryWrapper<TAppAsset>()
                                    .eq(TAppAsset::getUserId, tSecondContractOrderList.getUserId())
                                    .eq(TAppAsset::getSymbol, "usdt")
                                    .eq(TAppAsset::getType, 1));
                            // 下注金额 + （下注金额 * 补仓比例）
                            BigDecimal coverAmount = contractOrder.getBetAmount().add(contractOrder.getBetAmount().multiply(tSecondContractOrderList.getCompensationRate()).multiply(BigDecimal.valueOf(0.01)));
                            // 进行帐变记录
                            Integer type= RecordEnum.ONE_CLICK_REPLENISHMENT.getCode();
                            String username = getUsername();
                            String remark = "一键补仓";
                            TAppUser appUser = tAppUserMapper.selectById(contractOrder.getUserId());
                            tAppWalletRecordService.generateRecord(contractOrder.getUserId(),coverAmount, type,
                                    username,"Y" + OrderUtils.generateOrderNum(),remark,
                                    appAsset.getAmout(),appAsset.getAmout().add(coverAmount),"usdt",appUser.getAdminParentIds());
                            appAsset.setAmout(appAsset.getAmout().add(coverAmount));
                            appAsset.setAvailableAmount(appAsset.getAvailableAmount().add(coverAmount));

                            // 更新用户资产表
                            tAppAssetMapper.updateByUserId(appAsset);
                        }

                    }

                }else{
                    try {
                        for (TSecondContractOrder order  : tSecondContractOrderList.getOrders()) {
                            // 3. 否则只补仓传过来的订单
                            TAppAsset appAsset = tAppAssetMapper.selectOne(new LambdaQueryWrapper<TAppAsset>()
                                    .eq(TAppAsset::getUserId, order.getUserId())
                                    .eq(TAppAsset::getSymbol, "usdt")
                                    .eq(TAppAsset::getType, 1));
                            // 下注金额 + （下注金额 * 补仓比例）
                            BigDecimal coverAmount = order.getBetAmount().add(order.getBetAmount().multiply(tSecondContractOrderList.getCompensationRate()).multiply(BigDecimal.valueOf(0.01)));
                            // 进行帐变记录
                            Integer type= RecordEnum.ONE_CLICK_REPLENISHMENT.getCode();
                            String username = getUsername();
                            String remark = "一键补仓";
                            TAppUser appUser = tAppUserMapper.selectById(order.getUserId());
                            tAppWalletRecordService.generateRecord(order.getUserId(),coverAmount, type,
                                    username,"Y" + OrderUtils.generateOrderNum(),remark,
                                    appAsset.getAmout(),appAsset.getAmout().add(coverAmount),"usdt",appUser.getAdminParentIds());
                            appAsset.setAmout(appAsset.getAmout().add(coverAmount));
                            appAsset.setAvailableAmount(appAsset.getAvailableAmount().add(coverAmount));

                            // 更新用户资产表
                            tAppAssetMapper.updateByUserId(appAsset);

                        }
                    } catch (Exception e) {
                        log.error("一键补仓，补仓订单不能为空");
                        throw new RuntimeException("补仓订单不能为空");
                    }


                }

        }

        return 1;
    }
}
