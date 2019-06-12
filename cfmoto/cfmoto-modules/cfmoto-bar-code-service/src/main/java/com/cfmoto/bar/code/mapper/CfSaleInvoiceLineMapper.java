package com.cfmoto.bar.code.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceLine;

/**
 * <p>
 * 销售发货单子表 Mapper 接口
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
public interface CfSaleInvoiceLineMapper extends BaseMapper<CfSaleInvoiceLine> {

    /**
     * 修改仓库条码数据量
     * @param cfSaleInvoiceLine
     * @return
     */
  int updateSubtractBarCodeNumber(CfSaleInvoiceLine cfSaleInvoiceLine);

}
