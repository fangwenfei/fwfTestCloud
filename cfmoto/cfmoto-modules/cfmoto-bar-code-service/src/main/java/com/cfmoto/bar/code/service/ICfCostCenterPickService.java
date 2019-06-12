package com.cfmoto.bar.code.service;

import com.alibaba.fastjson.JSONObject;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawScanRecord;

import java.util.Map;

/**
 * 成本中心领退料service接口
 *
 * @author ye
 */
public interface ICfCostCenterPickService {
    /**
     * 根据订单号从本地数据库中加载成本中心领退料信息和清单表数据返回
     *
     * @param orderNo 订单号
     * @param userId  用户id
     * @return map
     * @author ye
     */
    Map<String, Object> loadDataFromLocalDataBase(String orderNo, int userId);


    /**
     * 更新数据（插入成本中心领料记录表和更新清单表）
     *
     * @param scanRecord 扫描记录表
     * @param gatherView 汇总界面数据
     */
    void updateDataAfterScanBarcode(CfCostCenterPickOrWithdrawScanRecord scanRecord, CfCostCenterPickOrWithdrawInventory gatherView);

    /**
     * 将sap返回的数据封装
     *
     * @param jsonObject sap返回的数据对象
     * @param userId     用户ID
     * @return map键值对对象
     * @author ye
     */
    Map<String, Object> getDataFromSapAndReturnData(JSONObject jsonObject, int userId);
}
