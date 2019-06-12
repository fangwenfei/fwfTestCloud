package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;

/**
 * 两部调拨入库 业务层接口
 *
 * @author ye
 */
public interface ICfTwoStepAllotInService {

    /**
     * 提交
     *
     * @param orderNo 调拨单号
     * @param userId  用户id
     * @return msg    提示消息
     */
    String finalCommit(String orderNo, int userId) throws Exception;

    /**
     * 扫描条码
     *
     * @param barcode     条码
     * @param barcodeType 条码类型(1:库存条码/2:物料条码)
     * @param orderNo     调拨单号
     * @param userId      用户id
     * @return vo
     */
    CfAllotManagementVo scanBarcode(String barcode, String barcodeType, String orderNo, int userId) throws Exception;
}
