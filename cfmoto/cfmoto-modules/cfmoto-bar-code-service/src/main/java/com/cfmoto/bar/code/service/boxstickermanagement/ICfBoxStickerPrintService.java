package com.cfmoto.bar.code.service.boxstickermanagement;

import com.cfmoto.bar.code.model.entity.CfBarcodeBind;

import java.util.List;

/**
 * 箱外贴打印业务层接口
 *
 * @author ye
 * @date 2019-04-29
 */
public interface ICfBoxStickerPrintService {
    /**
     * 扫描整车cp条码
     *
     * @param carCpCode 整车cp条码
     * @param printType 打印类型
     * @param carType   车型
     * @return CfBarcodeBind 三码绑定表数据
     * @throws Exception
     */
    CfBarcodeBind scanCarCpCode(String carCpCode, String printType, String carType) throws Exception;

    /**
     * 箱外贴打印
     *
     * @param barcodeBind 三码绑定对象
     * @param userId      用户ID
     * @return printContent 打印内容
     * @throws Exception
     */
    List<String> printBoxSticker(CfBarcodeBind barcodeBind, Integer userId) throws Exception;

    /**
     * 根据整车Cp条码查找对应的三码绑定记录
     *
     * @param carCpCode 整车cp条码
     * @return CfBarcodeBind
     */
    CfBarcodeBind makeUpScanCarCpCode(String carCpCode);

    /**
     * 箱外贴补打印
     *
     * @param carCpCode 整车CP条码
     * @return r
     * @throws Exception
     */
    List<String> makeUpPrint(String carCpCode) throws Exception;
}
