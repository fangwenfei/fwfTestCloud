package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfOrderScanTemp;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 订单扫描临时表 服务类
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
public interface ICfOrderScanTempService extends IService<CfOrderScanTemp> {

    void scanBarcode(int userId, String outSourceOrder, String barcode) throws Exception;

    void ourSourceModifyRow( int userId, String orderNo, String orderSumTempIdRef, String orderScanTempId,
                            String remarks, String qty ) throws Exception;

    void ourSourceDeleteRow(int userId, String orderNo, String orderSumTempIdRef, String orderScanTempId) throws Exception;
}
