package com.cfmoto.bar.code.service.allotmanagement;

import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;

/**
 * 调拨管理中
 * 一步调拨功能的业务层接口
 *
 * @author ye
 */
public interface ICfOneStepAllotService {
    /**
     * 一步调拨的调拨单功能
     *
     * @param orderNo 调拨单号
     * @param userId  用户ID
     * @return vo
     * @throws Exception sap或数据库相关异常
     */
    CfAllotManagementVo getDataByOrderNo(String orderNo, int userId) throws Exception;

    /**
     * 一步调拨的提交功能
     *
     * @param orderNo 调拨单号
     * @param userId  用户id
     * @return msg    提示消息
     * @throws Exception
     */
    String finalCommit(String orderNo, int userId) throws Exception;

    /**
     * 扫描条码
     *
     * @param barcode     条码
     * @param barcodeType 条码类型
     * @param orderNo     调拨单号
     * @param wareHouseNo 仓库号
     * @param userId      用户id
     * @return
     */
    CfAllotManagementVo scanBarcode(String barcode, String barcodeType, String orderNo, String wareHouseNo, int userId) throws Exception;
}
