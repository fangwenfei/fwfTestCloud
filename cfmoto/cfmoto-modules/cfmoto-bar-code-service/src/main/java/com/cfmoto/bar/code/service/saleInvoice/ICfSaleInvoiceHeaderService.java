package com.cfmoto.bar.code.service.saleInvoice;



import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceHeader;

import java.util.Map;

/**
 * <p>
 * 销售发货单 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
public interface ICfSaleInvoiceHeaderService extends IService<CfSaleInvoiceHeader> {

    Map<String, Object> getCfSaleInvoiceData(int userId,Map<String, Object> params) throws Exception;

}
