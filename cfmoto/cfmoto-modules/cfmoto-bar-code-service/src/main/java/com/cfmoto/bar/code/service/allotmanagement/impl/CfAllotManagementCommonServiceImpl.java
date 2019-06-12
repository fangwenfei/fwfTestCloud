package com.cfmoto.bar.code.service.allotmanagement.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.bo.CfAllotManagementBo;
import com.cfmoto.bar.code.model.entity.CfAllotInfo;
import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayData;
import com.cfmoto.bar.code.model.entity.CfAllotScanRecord;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.allotmanagement.*;
import com.xiaoleilu.hutool.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


/**
 * 调拨管理业务层接口实现类
 *
 * @author ye
 */

@Service
public class CfAllotManagementCommonServiceImpl implements ICfAllotManagementCommonService {

    @Autowired
    private ICfAllotInfoService cfAllotInfoService;

    @Autowired
    private ICfAllotInventoryService cfAllotInventoryService;

    @Autowired
    private ICfAllotScanRecordService cfAllotScanRecordService;

    @Autowired
    private CfAllotManagementBo cfAllotManagementBo;

    @Autowired
    private ICfAllotManagementCommonService allotManagementCommonService;

    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private ICfAllotOnWayDataService cfAllotOnWayDataService;

    private final Logger logger = LoggerFactory.getLogger(CfAllotManagementCommonServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CfAllotManagementVo getDataByOrderNo(String orderNo, String opType, int userId) throws Exception {

        //调用SAP08接口获取数据
        CfAllotManagementVo cf = sapApiService.getDataFromSapApi08(orderNo);

        //校验sap返回的状态码和订单状态
        cfAllotManagementBo.verifySapCodeThenOrderStatus(cf);

        List<CfAllotInventory> sapInventoryList = cf.getCfAllotInventoryList();

        //根据调拨单号获取调拨单信息表数据
        CfAllotInfo allotInfo = cfAllotInfoService.getAllotInfoByOrderNo(orderNo);

        //从数据库中获取清单表数据
        List<CfAllotInventory> dbInventoryList = cfAllotInventoryService.getCfAllotInventoryListByOrderNo(orderNo);

        //对比结果有三种情况：
        //    1.从数据库获取的信息表数据为空，则直接将SAP返回的数据插入到数据库
        if (allotInfo == null) {
            //插入信息表和清单表
            try {
                allotManagementCommonService.insertAllotInfoAndInventory(userId, cf);
                //从数据库查询数据并返回
                return allotManagementCommonService.getDataFromDataBase(orderNo, opType, userId);
            } catch (Exception e) {
                e.printStackTrace();
                //打印错误日志
                logger.info(e.getMessage());
                throw new Exception("插入数据失败!!!,请联系管理员!!!");
            }
        } else if (dbInventoryList.size() == 0) {
            //插入清单表
            try {
                cfAllotInventoryService.insertInventoryList(userId, allotInfo.getAllotInfoId(), cf.getCfAllotInventoryList());
                //从数据库查询数据并返回
                return allotManagementCommonService.getDataFromDataBase(orderNo, opType, userId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
                throw new Exception("插入数据失败!!!,请联系管理员!!!");
            }
        }


        //    2.SAP获取的数据与数据库获取的数据一致（条数，每条数据的物料条码和数量都一致），那么则不做操作,查询数据库数据并返回
        //    3.SAP获取的数据与数据库获取的数据不一致：删除数据库中的清单表数据和扫描表数据，然后将SAP获取的数据插入到数据库中

        //不一致
        if (sapInventoryList.size() != dbInventoryList.size()) {
            //删除数据库清单表
            cfAllotInventoryService.delete(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo));
            //插入sap的清单表数据
            cfAllotInventoryService.insertInventoryList(userId, allotInfo.getAllotInfoId(), cf.getCfAllotInventoryList());
            //比对数据库扫描表数据并修改清单表的数量
            allotManagementCommonService.updateInventoryByScanListForTwoStep(orderNo, userId);
        } else {

            boolean flag = true;

            //将sap返回的数据和数据库查找到数据逐个匹配，判断是否一致
            for (int i = 0; i < sapInventoryList.size(); i++) {
                if (sapInventoryList.get(i).getMaterialsNo().equals(dbInventoryList.get(i).getMaterialsNo())) {
                    if (sapInventoryList.get(i).getNumber() - dbInventoryList.get(i).getNumber() != 0) {
                        flag = false;
                        break;
                    }
                } else {
                    flag = false;
                    break;
                }
            }

            if (!flag) {
                //不一致
                //删除数据库清单表
                cfAllotInventoryService.delete(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo));
                //插入sap的清单表数据
                cfAllotInventoryService.insertInventoryList(userId, allotInfo.getAllotInfoId(), cf.getCfAllotInventoryList());
                //比对数据库扫描表数据并修改清单表的数量
                allotManagementCommonService.updateInventoryByScanListForTwoStep(orderNo, userId);
            }
        }
        //从数据库查询数据并返回
        return allotManagementCommonService.getDataFromDataBase(orderNo, opType, userId);

    }

    @Override
    public CfAllotManagementVo getDataFromDataBase(String orderNo, String opType, int userId) {
        CfAllotManagementVo allotManagementVo = new CfAllotManagementVo();
        allotManagementVo.setCfAllotInfo(cfAllotInfoService.getAllotInfoByOrderNo(orderNo));
        allotManagementVo.setCfAllotInventoryList(cfAllotInventoryService.getCfAllotInventoryListByOrderNo(orderNo));
        allotManagementVo.setCfAllotScanRecordList(cfAllotScanRecordService.getUncommittedCfAllotScanRecordListByOrderNo(orderNo, opType, userId));
        return allotManagementVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertAllotInfoAndInventory(int userId, CfAllotManagementVo cfAllotManagementVo) {

        //插入调拨信息表
        CfAllotInfo cfAllotInfo = cfAllotManagementVo.getCfAllotInfo();
        cfAllotInfo.setObjectSetBasicAttribute(userId, new Date());
        cfAllotInfoService.insertAllotInfo(cfAllotInfo);

        //插入调拨清单表集合数据
        cfAllotInventoryService.insertInventoryList(userId, cfAllotInfo.getAllotInfoId(), cfAllotManagementVo.getCfAllotInventoryList());

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAllotInfoAndInventory(String orderNo) {

        //删除信息表数据
        EntityWrapper<CfAllotInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        cfAllotInfoService.delete(entityWrapper);


        //删除清单表数据
        EntityWrapper<CfAllotInventory> inventoryEntityWrapper = new EntityWrapper<>();
        inventoryEntityWrapper.eq("order_no", orderNo);
        cfAllotInventoryService.delete(inventoryEntityWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRow(String orderNo, Integer id, String opType, int userId) throws Exception {

        //首先根据id查找到扫描行数据
        CfAllotScanRecord cfAllotScanRecord = cfAllotScanRecordService.selectById(id);

        //代表数据已被删除或不存在
        if (cfAllotScanRecord == null) {
            return;
        }

        //获取扫描行数据的外键清单表id
        int inventoryId = cfAllotScanRecord.getAllotInventoryId();

        //获取行数据的数量
        int number = cfAllotScanRecord.getNumber();

        //删除行数据
        cfAllotScanRecordService.delete(new EntityWrapper<>(cfAllotScanRecord));

        //更新清单表数据

        //首先根据清单表主键id查找到对应清单行数据
        CfAllotInventory inventory = cfAllotInventoryService.selectById(inventoryId);

        switch (opType) {
            case "01": {
                //二步调拨出库：更新清单表的调出已扫描数量
                inventory.setAllotOutScannedNumber(inventory.getAllotOutScannedNumber() - number);
                break;
            }
            case "02": {
                //二步调拨入库：更新清单表的调入已扫描数量
                inventory.setAllotInScannedNumber(inventory.getAllotInScannedNumber() - number);
                break;
            }
            case "03": {
                //一步调拨：更新清单表的调出和调入已扫描数量
                inventory.setAllotOutScannedNumber(inventory.getAllotOutScannedNumber() - number);
                inventory.setAllotInScannedNumber(inventory.getAllotInScannedNumber() - number);
                break;
            }
            default: {
                throw new Exception("请输入有效的操作类型!!!");
            }
        }

        //更新数据库中的清单表数据
        cfAllotInventoryService.updateById(inventory);
    }


    /**
     * 部品扫描表数据删除
     *
     * @param orderNo 调拨单
     * @param id      扫描标识
     * @param packNo  包号
     * @param userId  用户
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void scanDeleteRow(String orderNo, Integer id, String packNo, int userId) throws Exception {

        //首先根据id查找到扫描行数据
        //库存删除
        if (StrUtil.isBlank(packNo)) {
            deleteRow(orderNo, id, "02", userId);
        } else {

            EntityWrapper<CfAllotScanRecord> wrapper = new EntityWrapper<>();
            CfAllotScanRecord cfAllotScanRecord = new CfAllotScanRecord();
            cfAllotScanRecord.setPackNo(packNo);
            cfAllotScanRecord.setOrderNo(orderNo);
            wrapper.setEntity(cfAllotScanRecord);
            List<CfAllotScanRecord> cfAllotScanRecordList = cfAllotScanRecordService.selectList(wrapper);
            if (cfAllotScanRecordList.size() == 0) {
                throw new Exception("数据已修改，请重新获取单号数据");
            }
            //汇总包对应的数量
            int packNoQty = cfAllotScanRecordList.stream().map(CfAllotScanRecord::getNumber).mapToInt(Integer::intValue).sum();
            //删除扫描行数据
            cfAllotScanRecordService.delete(wrapper);
            //更新汇总表数据
            //查询汇总表数据
            CfAllotInventory inventory = cfAllotInventoryService.selectById(cfAllotScanRecordList.get(0).getAllotInventoryId());
            if (inventory != null) {
                CfAllotInventory cfAllotInventory = new CfAllotInventory();
                cfAllotInventory.setAllotInScannedNumber(inventory.getAllotInScannedNumber() - packNoQty);
                cfAllotInventory.setAllotInventoryId(inventory.getAllotInventoryId());
                cfAllotInventoryService.updateById(cfAllotInventory);
            }
        }
    }

    /**
     * 根据条码数量动态匹配数量
     * 适用于两步调拨出库和一步调拨
     *
     * @param barcodeNumber 条码数量
     * @param list          sap返回数据结果集
     * @return map
     * @author ye
     */
    @Override
    public List<Map<String, Object>> dynamicMatchNumberByBarcodeNumber(int barcodeNumber, String opType, List<Map<String, Object>> list, String materialsNo,int userId) {
        //根据订单号加载已扫描界面
        List<CfAllotScanRecord> scanRecordList = cfAllotScanRecordService.getUncommittedCfAllotScanRecordListByMaterialsNo(materialsNo, opType, userId);

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
                for (CfAllotScanRecord scanRecord : scanRecordList) {
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertRecordAndUpdateInventory(String orderNo, String opType, String materialsNo, String barcode, List<Map<String, Object>> list, int userId) {
        //根据订单号和物料代码获取清单表数据

        CfAllotInventory inventory = new CfAllotInventory();
        inventory.setOrderNo(orderNo);
        inventory.setMaterialsNo(materialsNo);

        CfAllotInventory newInventory = cfAllotInventoryService.selectOne(new EntityWrapper<>(inventory));

        //循环遍历提交的数据进行新增记录表数据和修改清单表数据
        for (Map<String, Object> stringObjectMap : list) {
            CfAllotScanRecord scanRecord = new CfAllotScanRecord();
            scanRecord.setOrderNo(orderNo);
            scanRecord.setOperateType(opType);
            scanRecord.setMaterialsNo(materialsNo);
            scanRecord.setMaterialsName(newInventory.getMaterialsName());
            scanRecord.setSpec((String) stringObjectMap.get("spec"));
            scanRecord.setBarcode(barcode);
            scanRecord.setBarcodeType("BP");
            scanRecord.setAllotInWarehouse(newInventory.getAllotInWarehouse());
            scanRecord.setBatchNo((String) stringObjectMap.get("batchNo"));
            scanRecord.setNumber((Integer) stringObjectMap.get("toBeMatchNumber"));
            scanRecord.setWarehouse((String) stringObjectMap.get("wareHouse"));
            scanRecord.setStorageArea((String) stringObjectMap.get("storageArea"));
            scanRecord.setWarehousePosition((String) stringObjectMap.get("warehousePosition"));
            scanRecord.setState("");
            scanRecord.setSupplier((String) stringObjectMap.get("NAME_ORG1"));
            scanRecord.setObjectSetBasicAttribute(userId, new Date());
            scanRecord.setAllotInfoId(newInventory.getAllotInfoId());
            scanRecord.setAllotInventoryId(newInventory.getAllotInventoryId());

            cfAllotScanRecordService.insert(scanRecord);

            //更新对应的清单表中的对应数据
            newInventory.setAllotOutScannedNumber(newInventory.getAllotOutScannedNumber() + scanRecord.getNumber());
        }

        newInventory.setObjectSetBasicAttribute(userId, new Date());
        cfAllotInventoryService.updateById(newInventory);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertRecordAndUpdateInventoryForAllotIn(String orderNo, String opType, String materialsNo, String barcode, List<Map<String, Object>> list, int userId) throws Exception {


        //根据订单号和物料代码获取清单表数据

        CfAllotInventory inventory = new CfAllotInventory();
        inventory.setOrderNo(orderNo);
        inventory.setMaterialsNo(materialsNo);

        CfAllotInventory newInventory = cfAllotInventoryService.selectOne(new EntityWrapper<>(inventory));

        //获取条码类型

        //循环遍历提交的数据进行新增记录表数据和修改清单表数据
        for (Map<String, Object> stringObjectMap : list) {
            if ((Integer) stringObjectMap.get("toBeMatchNumber") == 0) {
                throw new Exception("调入数量大于调出数量，请注意！");
            }
            CfAllotOnWayData onWayData = (CfAllotOnWayData) stringObjectMap.get("onWayData");

            CfAllotScanRecord scanRecord = new CfAllotScanRecord();
            scanRecord.setOrderNo(orderNo);
            scanRecord.setOperateType(opType);
            scanRecord.setMaterialsNo(onWayData.getMaterialsNo());
            scanRecord.setMaterialsName(onWayData.getMaterialsName());
            scanRecord.setSpec(onWayData.getSpec());
            scanRecord.setBarcode(onWayData.getBarcode());
            scanRecord.setBarcodeType("BP");
            scanRecord.setAllotInWarehouse(onWayData.getAllotInWarehouse());
            scanRecord.setBatchNo(onWayData.getBatchNo());
            scanRecord.setNumber((Integer) stringObjectMap.get("toBeMatchNumber"));
            scanRecord.setWarehouse(onWayData.getAllotInWarehouse());
            scanRecord.setAllotInWarehouse(onWayData.getAllotInWarehouse());
            scanRecord.setState("");
            scanRecord.setObjectSetBasicAttribute(userId, new Date());
            scanRecord.setAllotInfoId(newInventory.getAllotInfoId());
            scanRecord.setAllotInventoryId(newInventory.getAllotInventoryId());

            cfAllotScanRecordService.insert(scanRecord);

            //更新对应的清单表中的对应数据
            newInventory.setAllotInScannedNumber(newInventory.getAllotInScannedNumber() + scanRecord.getNumber());
        }

        newInventory.setObjectSetBasicAttribute(userId, new Date());
        cfAllotInventoryService.updateById(newInventory);

    }


    /**
     * 两步调拨入库的动态批次匹配功能
     * 将在途表中数据和扫描表中数据进行匹配
     *
     * @param barcodeNumber 条码数量
     * @param materialsNo   物料条码
     * @return map
     * @author ye
     */
    @Override
    public List<Map<String, Object>> dynamicMatchNumberByBarcodeNumberFoAllotIn(int barcodeNumber, String materialsNo, String orderNo) throws Exception {

        List<CfAllotOnWayData> onWayDataList = cfAllotOnWayDataService.selectList(new EntityWrapper<CfAllotOnWayData>().eq("order_no", orderNo).eq("materials_no", materialsNo));

        List<Map<String, Object>> mapList = new ArrayList<>();


        //判断已扫描数据集合是否为空
        if (onWayDataList.size() == 0) {

            throw new Exception("在途表中没有数据!!!");
        } else {
            //匹配已扫描界面和sap返回的数据，得到对应的数据集合
            for (CfAllotOnWayData onWayData : onWayDataList) {
                Map<String, Object> map = new HashMap<>();
                //初始化已匹配数量
                map.put("matchedNumber", 0);
                //批次数量
                map.put("batchNumber", onWayData.getNumber() - onWayData.getAllotInNumber());
                //初始化待匹配数量
                map.put("toBeMatchNumber", 0);
                map.put("onWayData", onWayData);
                List<CfAllotScanRecord> cfAllotScanRecords = cfAllotScanRecordService.selectList(new EntityWrapper<CfAllotScanRecord>().eq("order_no", orderNo).eq("materials_no", materialsNo).eq("batch_no", onWayData.getBatchNo()).eq("operate_type", "02"));

                for (CfAllotScanRecord cfAllotScanRecord : cfAllotScanRecords) {
                    //计算每条批次数据的已匹配数量
                    if ((int) map.get("matchedNumber") == 0) {
                        map.put("matchedNumber", cfAllotScanRecord.getNumber());
                    } else {
                        map.put("matchedNumber", (int) map.get("matchedNumber") + cfAllotScanRecord.getNumber());
                    }
                }

                mapList.add(map);

            }

        }

        //根据条码数量计算每条批次的匹配数量
        for (Map<String, Object> stringObjectMap : mapList) {
            int batchNumber = (int) stringObjectMap.get("batchNumber");
            int matchedNumber = (int) stringObjectMap.get("matchedNumber");
            if (barcodeNumber > batchNumber - matchedNumber && barcodeNumber != 0) {
                stringObjectMap.put("toBeMatchNumber", batchNumber - matchedNumber);
                barcodeNumber = barcodeNumber - (batchNumber - matchedNumber);
            } else {
                stringObjectMap.put("toBeMatchNumber", barcodeNumber);
                barcodeNumber = 0;
            }
        }

        return mapList;

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAllotInventoryAndScanRecord(String orderNo) {
        cfAllotInventoryService.delete(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo));

        cfAllotScanRecordService.delete(new EntityWrapper<CfAllotScanRecord>().eq("order_no", orderNo));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String orderNo, String barcode, String materialsNo, String apiNo, String opType, Integer userId) throws Exception {
        List<CfAllotScanRecord> scanRecordList;

        //判断接口号，1是库存记录  2是物料记录
        if (apiNo.equals("1")) {
            scanRecordList = cfAllotScanRecordService.selectList(new EntityWrapper<CfAllotScanRecord>()
                    .eq("order_no", orderNo).eq("barcode", barcode)
                    .eq("materials_no", materialsNo)
                    .eq("operate_type", opType).eq("created_by", userId));
        } else if (apiNo.equals("2")) {
            scanRecordList = cfAllotScanRecordService.selectList(new EntityWrapper<CfAllotScanRecord>()
                    .eq("order_no", orderNo)
                    .eq("materials_no", materialsNo)
                    .eq("operate_type", opType).eq("created_by", userId));
        } else {
            throw new Exception("接口号无效,请注意!!!");
        }

        //根据单号、物料代码、操作类型、用户id删除数据集合
        if (scanRecordList != null && scanRecordList.size() != 0) {
            cfAllotScanRecordService.deleteList(scanRecordList);
        } else {
            throw new Exception("未找到该物料的扫描表数据,请注意!!!");
        }

        int number = 0;
        for (CfAllotScanRecord scanRecord : scanRecordList) {
            number += scanRecord.getNumber();
        }

        //根据操作类型更新汇总界面的扫描数量
        CfAllotInventory inventory = cfAllotInventoryService.selectOne(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo).eq("materials_no", materialsNo));
        if (inventory != null) {
            switch (opType) {
                case "01":
                case "03": {
                    //更新清单表的调出扫描数量
                    inventory.setAllotOutScannedNumber(inventory.getAllotOutScannedNumber() - number);
                    break;
                }
                case "02": {
                    //更新清单表的调入扫描数量
                    inventory.setAllotInScannedNumber(inventory.getAllotInScannedNumber() - number);
                    break;
                }
                default: {
                    break;
                }
            }
            inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
            cfAllotInventoryService.updateById(inventory);
        } else {
            throw new Exception("未找到该行扫描数据对应的清单表数据,请注意!!!");
        }

    }

    @Override
    public Integer getTotal(String orderNo, String materialsNo, String opType, Integer userId) throws Exception {
        List<CfAllotScanRecord> scanRecordList = cfAllotScanRecordService.selectList(new EntityWrapper<CfAllotScanRecord>().eq("order_no", orderNo)
                .eq("materials_no", materialsNo).eq("operate_type", opType).eq("created_by", userId));
        Integer total = 0;
        if (scanRecordList != null && scanRecordList.size() != 0) {
            //计算物料总数
            for (CfAllotScanRecord scanRecord : scanRecordList) {
                total += scanRecord.getNumber();
            }
        } else {
            throw new Exception("该账号下未根据单号和物料代码获取到物料总数,请注意!!!");
        }
        return total;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(String orderNo, String barcode, String materialsNo, String opType, String apiNo, Integer number, Integer total, Integer userId) throws Exception {
        //库存
        if (apiNo.equals("1")) {
            //修改行数据数量
            CfAllotScanRecord allotScanRecord = cfAllotScanRecordService.selectOne(new EntityWrapper<CfAllotScanRecord>()
                    .eq("order_no", orderNo).eq("barcode", barcode)
                    .eq("materials_no", materialsNo)
                    .eq("operate_type", opType).eq("created_by", userId));
            if (allotScanRecord != null) {
                if (number > allotScanRecord.getNumber()) {
                    throw new Exception("修改数量不能大于当前数量!!!");
                } else {
                    allotScanRecord.setNumber(number);
                    allotScanRecord.setObjectSetBasicAttributeForUpdate(userId, new Date());
                    cfAllotScanRecordService.updateById(allotScanRecord);
                }
            }
            //物料
        } else if (apiNo.equals("2")) {
            List<CfAllotScanRecord> scanRecordList = cfAllotScanRecordService.selectList(new EntityWrapper<CfAllotScanRecord>()
                    .eq("order_no", orderNo)
                    .eq("materials_no", materialsNo)
                    .eq("operate_type", opType).eq("created_by", userId));
            if (scanRecordList != null && scanRecordList.size() != 0) {
                if (number > total) {
                    throw new Exception("修改数量不能大于当前物料总数!!!");
                }
                int toBeDelete = total - number;
                for (CfAllotScanRecord allotScanRecord : scanRecordList) {
                    //如果待删除数量为0则跳出循环
                    if (toBeDelete == 0) {
                        break;
                    }
                    if (allotScanRecord.getNumber() <= toBeDelete) {
                        //删除扫描表
                        cfAllotScanRecordService.deleteById(allotScanRecord);
                    } else {
                        //修改扫描表数量
                        allotScanRecord.setNumber(allotScanRecord.getNumber() - toBeDelete);
                        allotScanRecord.setObjectSetBasicAttributeForUpdate(userId, new Date());
                        cfAllotScanRecordService.updateById(allotScanRecord);
                    }
                    //递减待删除数量
                    toBeDelete -= allotScanRecord.getNumber();
                }
            } else {
                throw new Exception("未找到该物料的扫描表数据,请注意!!!");
            }
        } else {
            throw new Exception("接口号无效,请注意!!!");
        }

        //根据操作类型更新汇总界面的扫描数量
        CfAllotInventory inventory = cfAllotInventoryService.selectOne(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo).eq("materials_no", materialsNo));
        if (inventory != null) {
            switch (opType) {
                case "01":
                case "03": {
                    //更新清单表的调出扫描数量
                    inventory.setAllotOutScannedNumber(number);
                    break;
                }
                case "02": {
                    //更新清单表的调入扫描数量
                    inventory.setAllotInScannedNumber(number);
                    break;
                }
                default: {
                    break;
                }
            }
            inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
            cfAllotInventoryService.updateById(inventory);
        } else {
            throw new Exception("未找到该行扫描数据对应的清单表数据,请注意!!!");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateInventoryByScanList(String orderNo, Integer userId) {
        List<CfAllotInventory> inventoryList = cfAllotInventoryService.selectList(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo));
        //比对清单表和扫描表
        for (CfAllotInventory inventory : inventoryList) {
            //根据清单表的物料代码和单号找到对应的扫描表数据
            List<CfAllotScanRecord> scanRecords = cfAllotScanRecordService.selectList(new EntityWrapper<CfAllotScanRecord>().eq("materials_no", inventory.getMaterialsNo()).eq("order_no", inventory.getOrderNo()));
            for (CfAllotScanRecord scanRecord : scanRecords) {
                inventory.setAllotOutScannedNumber(inventory.getAllotOutScannedNumber() + scanRecord.getNumber());
                inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
                cfAllotInventoryService.updateById(inventory);
            }

        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateInventoryByScanListForTwoStep(String orderNo, Integer userId) {
        List<CfAllotInventory> inventoryList = cfAllotInventoryService.selectList(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo));
        //比对清单表和扫描表
        for (CfAllotInventory inventory : inventoryList) {
            //根据清单表的物料代码和单号和操作类型找到对应的扫描表数据
            List<CfAllotScanRecord> scanRecords = cfAllotScanRecordService.selectList(
                    new EntityWrapper<CfAllotScanRecord>()
                            .eq("materials_no", inventory.getMaterialsNo())
                            .eq("order_no", inventory.getOrderNo()));
            for (CfAllotScanRecord scanRecord : scanRecords) {
                if (scanRecord.getOperateType().equals("01")) {
                    inventory.setAllotOutScannedNumber(inventory.getAllotOutScannedNumber() + scanRecord.getNumber());
                }
                if (scanRecord.getOperateType().equals("02")) {
                    inventory.setAllotInScannedNumber(inventory.getAllotInScannedNumber() + scanRecord.getNumber());
                }
                inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
                cfAllotInventoryService.updateById(inventory);
            }

        }

    }
}