package com.cfmoto.bar.code.service.saleInvoice;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceLine;

import java.util.Map;

/**
 * <p>
 * 销售发货单子表 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
public interface ICfSaleInvoiceLinePickService extends IService<CfSaleInvoiceLine> {
    Map<String, Object> addCfSaleInvoiceData(int userId, Map<String, Object> params)  throws Exception;

    Map<String, Object> submitCfSaleInvoiceData(int userId, Map<String, Object> params)  throws Exception;


    Map<String, Object> getCfSaleInvoiceDataPick(int userId, Map<String, Object> params)  throws Exception;


}
