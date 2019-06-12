package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfOrderSumTemp;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 订单汇总临时表 服务类
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
public interface ICfOrderSumTempService extends IService<CfOrderSumTemp> {

    void insertDataByBatch( List<CfOrderSumTemp> cfOrderSumTempList );
}
