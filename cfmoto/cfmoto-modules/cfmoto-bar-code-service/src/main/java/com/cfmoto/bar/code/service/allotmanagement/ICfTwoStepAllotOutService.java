package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;

/**
 * 两步调拨出库业务层接口
 *
 * @author ye
 */
public interface ICfTwoStepAllotOutService {

    /**
     * 两步调拨出库的扫描条码接口
     *
     * @param barcode     条码
     * @param barcodeType 条码类型
     * @param orderNo     调拨单号
     * @param wareHouseNo 仓库号
     * @param userId      用户id
     * @return
     * @throws Exception
     */
    CfAllotManagementVo scanBarcode(String barcode, String barcodeType, String orderNo, String wareHouseNo, int userId) throws Exception;

    /**
     * 提交
     *
     * @param userId  用户ID
     * @param orderNo 调拨单号
     * @throws Exception 可能出现的异常
     */
    String finalCommit(String orderNo, int userId) throws Exception;
}
