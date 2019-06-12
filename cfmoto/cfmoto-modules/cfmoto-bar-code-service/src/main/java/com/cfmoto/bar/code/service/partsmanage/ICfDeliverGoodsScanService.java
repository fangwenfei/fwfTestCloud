package com.cfmoto.bar.code.service.partsmanage;

import com.cfmoto.bar.code.model.entity.CfDeliverGoodsScan;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
public interface ICfDeliverGoodsScanService extends IService<CfDeliverGoodsScan> {

    /**
     * 发货单条码扫描
     * @param orderNo
     * @param caseNo
     * @param userId
     * @throws Exception
     */
    void scanCaseNo( String orderNo, String caseNo, int userId ) throws Exception;

    /**
     * 发货单删除行数据
     * @param deliverGoodsScanId
     * @param deliverGoodsSumIdRef
     * @param userId
     * @throws Exception
     */
    void deleteScanRow( String deliverGoodsScanId, String deliverGoodsSumIdRef, int userId ) throws Exception;
}
