package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawScanRecord;
import com.cfmoto.bar.code.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成本中心领料-部品 业务层实现类
 *
 * @author ye
 */

@Service
public class CfCostCenterPickComponentServiceImpl implements ICfCostCenterPickComponentService {

    private final ICfCostCenterPickOrWithdrawInventoryService inventoryService;

    private final ICfCostCenterPickOrWithdrawScanRecordService scanRecordService;

    private final ICfBarcodeInventoryService barcodeInventoryService;

    private final ICfCostCenterPickService pickService;

    /**
     * 使用构造器强制注入
     *
     * @author ye
     */
    @Autowired
    public CfCostCenterPickComponentServiceImpl(ICfCostCenterPickOrWithdrawInventoryService inventoryService,
                                                ICfCostCenterPickOrWithdrawScanRecordService scanRecordService,
                                                ICfBarcodeInventoryService barcodeInventoryService,
                                                ICfCostCenterPickService pickService) {
        this.inventoryService = inventoryService;
        this.scanRecordService = scanRecordService;
        this.barcodeInventoryService = barcodeInventoryService;
        this.pickService = pickService;
    }

    /**
     * 根据条码数量动态匹配数量
     *
     * @param barcodeNumber 条码数量
     * @param list          sap返回数据结果集
     * @return map
     * @author ye
     */
    @Override
    public List<Map<String, Object>> dynamicMatchNumberByBarcodeNumber(int barcodeNumber, List<Map<String, Object>> list, String materialsNo,int userId) {
        //根据订单号加载已扫描界面
        List<CfCostCenterPickOrWithdrawScanRecord> scanRecordList = scanRecordService.getUnCommitedDataByMaterialsNo(materialsNo,userId);

        //判断已扫描数据集合是否为空
        if (scanRecordList.size() == 0) {
            for (Map<String, Object> stringObjectMap : list) {
                stringObjectMap.put("matchedNumber", 0);
            }
        } else {
            //匹配已扫描界面和sap返回的数据，得到对应的数据集合
            for (Map<String, Object> stringObjectMap : list) {
                //初始化已匹配数量
                stringObjectMap.put("matchedNumber", 0);
                for (CfCostCenterPickOrWithdrawScanRecord scanRecord : scanRecordList) {
                    if (stringObjectMap.get("materialsNo").equals(scanRecord.getMaterialsNo()) && stringObjectMap.get("batchNo").equals(scanRecord.getBatchNo())) {

                        //计算每条批次数据的已匹配数量
                        if ((int) stringObjectMap.get("matchedNumber") == 0) {
                            stringObjectMap.put("matchedNumber", scanRecord.getNumber());
                        } else {
                            stringObjectMap.put("matchedNumber", (int) stringObjectMap.get("matchedNumber") + scanRecord.getNumber());
                        }

                    }
                }
            }

        }

        //根据条码数量计算每条批次的匹配数量
        for (Map<String, Object> stringObjectMap : list) {
            int batchNumber = new BigDecimal(stringObjectMap.get("batchNumber").toString()).intValue();
            stringObjectMap.put("batchNumber", batchNumber);
            int matchedNumber = Integer.parseInt(stringObjectMap.get("matchedNumber").toString());
            if (barcodeNumber > batchNumber - matchedNumber && barcodeNumber != 0) {
                stringObjectMap.put("toBeMatchNumber", batchNumber - matchedNumber);
                barcodeNumber = barcodeNumber - (batchNumber - matchedNumber);
            } else {
                stringObjectMap.put("toBeMatchNumber", barcodeNumber);
                barcodeNumber = 0;
            }
        }

        return list;

    }


    /**
     * 新增扫描记录表和更新清单表
     *
     * @param orderNo     订单号
     * @param materialsNo 物料代码
     * @param barcode     条码
     * @param list        物料批次匹配数据
     * @param userId      用户ID
     * @return R
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> insertRecordAndUpdateInventory(String orderNo, String materialsNo, String barcode, List<Map<String, Object>> list, int userId) {
        Map<String, Object> returnMap = new HashMap<>();

        //根据订单号和物料代码获取清单表数据

        CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();
        inventory.setOrderNo(orderNo);
        inventory.setMaterialsNo(materialsNo);

        CfCostCenterPickOrWithdrawInventory newInventory = inventoryService.selectOne(new EntityWrapper<>(inventory));

        //循环遍历提交的数据进行新增记录表数据和修改清单表数据
        for (Map<String, Object> stringObjectMap : list) {

            CfCostCenterPickOrWithdrawScanRecord scanRecord = new CfCostCenterPickOrWithdrawScanRecord();
            scanRecord.setOrderNo(orderNo);
            scanRecord.setOrderType(newInventory.getOrderType());
            scanRecord.setMaterialsNo(materialsNo);
            scanRecord.setMaterialsName(newInventory.getMaterialsName());
            scanRecord.setSpec((String) stringObjectMap.get("spec"));
            scanRecord.setBarcode(barcode);
            scanRecord.setBarcodeType("BP");

            scanRecord.setBatchNo((String) stringObjectMap.get("batchNo"));
            scanRecord.setNumber((Integer) stringObjectMap.get("toBeMatchNumber"));
            scanRecord.setWarehouse((String) stringObjectMap.get("wareHouse"));
            scanRecord.setStorageArea((String) stringObjectMap.get("storageArea"));
            scanRecord.setWarehousePosition((String) stringObjectMap.get("warehousePosition"));
            scanRecord.setState("");
            scanRecord.setObjectSetBasicAttribute(userId, new Date());
            scanRecord.setCostCenterPickOrWithdrawInfoId(inventory.getCostCenterPickOrWithdrawInfoId());
            scanRecord.setCostCenterPickOrWithdrawInventoryId(inventory.getCostCenterPickOrWithdrawInventoryId());

            boolean insert = scanRecordService.insert(scanRecord);
            if (!insert) {
                returnMap.put("code", 1);
                returnMap.put("msg", "数据更新异常，请联系管理员!");
                return returnMap;
            }

            //更新对应的清单表中的对应数据
            newInventory.setScannedNumber(newInventory.getScannedNumber() + scanRecord.getNumber());
        }

        newInventory.setObjectSetBasicAttribute(userId, new Date());

        boolean update = inventoryService.updateById(newInventory);

        if (update) {
            returnMap.put("code", 0);
            returnMap.put("data", pickService.loadDataFromLocalDataBase(orderNo, userId));
        } else {
            returnMap.put("code", 1);
            returnMap.put("msg", "数据更新异常，请联系管理员!");
        }

        return returnMap;
    }

}
