package com.cfmoto.bar.code.service;

import java.util.List;
import java.util.Map;

/**
 * 成本中心领料-部品  业务层
 *
 * @author ye
 */
public interface ICfCostCenterPickComponentService {

    /**
     * 根据条码动态匹配数量
     *
     * @param barcodeNumber 条码数量
     * @param list          list
     * @param materialsNo   物料代码
     * @return list
     */
    List<Map<String, Object>> dynamicMatchNumberByBarcodeNumber(int barcodeNumber, List<Map<String, Object>> list, String materialsNo, int userId);


    /**
     * 新增扫描记录表和更新清单表
     *
     * @param orderNo
     * @param materialsNo
     * @param barcode
     * @param list
     * @param userId
     * @return
     */
    Map<String, Object> insertRecordAndUpdateInventory(String orderNo, String materialsNo, String barcode, List<Map<String, Object>> list, int userId);
}
