package com.cfmoto.bar.code.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawInfoMapper;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawInventoryMapper;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawScanRecordMapper;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInfo;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawScanRecord;
import com.cfmoto.bar.code.service.ICfCostCenterPickService;
import com.cfmoto.bar.code.utiles.BigDecimalUtils;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 成本中心领退料service层实现类
 *
 * @author ye
 */

@Service
public class CfCostCenterPickServiceImpl implements ICfCostCenterPickService {

    @Autowired
    private CfCostCenterPickOrWithdrawInfoMapper infoMapper;

    @Autowired
    private CfCostCenterPickOrWithdrawInventoryMapper inventoryMapper;

    @Autowired
    private CfCostCenterPickOrWithdrawScanRecordMapper scanRecordMapper;

    /**
     * 根据订单号orderNo从成本中心领退料信息表和清单表中加载数据返回
     *
     * @param orderNo
     * @return map
     * @author ye
     */
    @Override
    public Map<String, Object> loadDataFromLocalDataBase(String orderNo, int userId) {

        Map<String, Object> map = new HashMap<>(5);

        //根据orderNo加载信息表数据
        CfCostCenterPickOrWithdrawInfo info = new CfCostCenterPickOrWithdrawInfo();
        info.setOrderNo(orderNo);
        CfCostCenterPickOrWithdrawInfo newInfo = infoMapper.selectOne(info);

        //根据orderNO加载清单表数据集合
        CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();
        inventory.setOrderNo(orderNo);
        List<CfCostCenterPickOrWithdrawInventory> inventoryList = inventoryMapper.selectList(new EntityWrapper<>(inventory));

        //根据orderNo加载记录表中状态为未提交的数据
        CfCostCenterPickOrWithdrawScanRecord scanRecord = new CfCostCenterPickOrWithdrawScanRecord();
        scanRecord.setOrderNo(orderNo);
        scanRecord.setCreatedBy(userId);

        EntityWrapper<CfCostCenterPickOrWithdrawScanRecord> wrapper = new EntityWrapper<>(scanRecord);
        wrapper.eq("state", "").or().isNull("state");

        List<CfCostCenterPickOrWithdrawScanRecord> scanRecordList = scanRecordMapper.selectList(wrapper);

        //封装单据头数据
        map.put("invoiceHeaderView", newInfo);
        //汇总界面数据
        map.put("gatherView", inventoryList);
        //已扫描界面数据
        map.put("scannedView", scanRecordList);
        return map;
    }


    /**
     * 更新数据（插入成本中心领料记录表和更新清单表）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDataAfterScanBarcode(CfCostCenterPickOrWithdrawScanRecord scanRecord, CfCostCenterPickOrWithdrawInventory gatherView) {
        //插入一条新的记录数据
        scanRecordMapper.insert(scanRecord);

        //更新清单表中的实发数量（已扫描数量）
        inventoryMapper.updateById(gatherView);
    }


    /**
     * 解析sap返回的数据并插入到扫描记录表和清单表并返回
     *
     * @param jsonObject sap返回的数据对象
     * @param userId     用户id
     * @return map
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> getDataFromSapAndReturnData(JSONObject jsonObject, int userId) {

        Map<String, Object> map = new HashMap<>(6);

        CfCostCenterPickOrWithdrawInfo info = null;

        List<CfCostCenterPickOrWithdrawInventory> inventoryList = new ArrayList<>();

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) jsonObject.get("ET_DATA");

        for (Map<String, Object> tempMap : dataList) {
            String orderNo = (String) tempMap.getOrDefault("RSNUM", "");
            String orderType = (String) tempMap.getOrDefault("BWART", "");

            Date date = DateUtil.parseDate(tempMap.getOrDefault("BDTER", "").toString());

            if (info == null) {
                info = new CfCostCenterPickOrWithdrawInfo();
                //封装信息表数据
                info.setOrderNo(orderNo);
                info.setOrderType(orderType);
                info.setMadeOrderDate(date);
                info.setDept((String) tempMap.getOrDefault("KLTXT", ""));
                info.setObjectSetBasicAttribute(userId, new Date());
            }

            CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();
            //封装清单表数据
            inventory.setOrderNo(orderNo);
            inventory.setOrderType(orderType);
            inventory.setMaterialsName((String) tempMap.getOrDefault("MAKTX", ""));
            inventory.setMaterialsNo((String) tempMap.getOrDefault("MATNR", ""));
            inventory.setSpec((String) tempMap.getOrDefault("WRKST", ""));

            if ("1".equals(orderType)) {
                inventory.setWarehouse((String) tempMap.getOrDefault("LGORT", ""));
            } else if ("2".equals(orderType)) {
                inventory.setWithdrawWarehouse((String) tempMap.getOrDefault("LGORT", ""));
            } else {
                map.put("flag", false);
                return map;
            }

            inventory.setStorageArea((String) tempMap.getOrDefault("BLATT", ""));
            inventory.setShouldPickOrWithdrawNumber(BigDecimalUtils.numberObjectToInteger(tempMap.getOrDefault("ERFMG", 0)));
            inventory.setScannedNumber(inventory.getShouldPickOrWithdrawNumber() - BigDecimalUtils.numberObjectToInteger(tempMap.getOrDefault("ZWQSL", 0)));
            inventory.setSpStorePositionNo((String) tempMap.getOrDefault("ZSPCWH", 0));
            inventory.setObjectSetBasicAttribute(userId, new Date());
            inventory.setCostCenterPickOrWithdrawInfoId(info.getCostCenterPickOrWithdrawInfoId());

            inventoryList.add(inventory);
        }

        map.put("invoiceHeaderView", info);
        map.put("gatherView", inventoryList);
        return map;
    }
}
