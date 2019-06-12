package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfSaleNextNumber;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 获取销售订单下一编号 服务类
 * </p>
 *
 * @author space
 * @since 2019-04-12
 */
public interface ICfSaleNextNumberService extends IService<CfSaleNextNumber> {

    /**
     * 获取销售订单箱号
     * @param userId
     * @param saleOrder
     * @return
     * @throws Exception
     */
    String generateSaleCaseNoNextNumber( Integer userId, String saleOrder ) throws Exception;
}
