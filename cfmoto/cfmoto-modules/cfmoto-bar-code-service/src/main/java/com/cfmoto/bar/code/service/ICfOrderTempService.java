package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cfmoto.bar.code.model.entity.CfOrderTemp;
import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.vo.OrderFullVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单临时表 服务类
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
public interface ICfOrderTempService extends IService<CfOrderTemp> {

    OrderFullVo outSourceOrderData( int userId, String outSourceOrder ) throws Exception;

    /**
     * 通过CfOrderTemp删除
     * @param cfOrderTemp
     * @return
     */
    Integer deleteByCfOrderTemp( CfOrderTemp cfOrderTemp ) throws Exception;

    /**
     * 汇总保存订单数据
     * @return
     */
    OrderFullVo saveOrderData( int userId, String outSourceOrderId, List<Map<String, Object>> etDataList ) throws Exception;

    /**
     * 获取委外出库单数据
     * @param userId
     * @param outSourceOrder
     * @return
     */
    OrderFullVo getOutSourceOutOrderData( int userId, String outSourceOrder ) throws Exception;
}
