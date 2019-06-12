package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceLine;
import com.cfmoto.bar.code.model.entity.CfStockScanLine;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.common.util.R;
import com.github.pig.common.vo.UserVO;

import java.util.Map;

/**
 * <p>
 * 备料扫描记录 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-12
 */
public interface ICfStockScanLineService extends IService<CfStockScanLine> {
     UserVO user(Integer id);
    Map<String, Object> getDataByStockListNo(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> addScanLineData(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> deleteDataByBarCodeNo(int userId,  Map<String, Object> params)  throws Exception;

    Map<String, Object> submitICfStockScanLineData(int userId, Map<String, Object> params) throws Exception;

    Page getCfStockInventoryPage(int userId, Map<String, Object> params) throws Exception;

    Map<String, Object> updateByStockScanLineData(int userId,  Map<String, Object> params) throws Exception;
}
