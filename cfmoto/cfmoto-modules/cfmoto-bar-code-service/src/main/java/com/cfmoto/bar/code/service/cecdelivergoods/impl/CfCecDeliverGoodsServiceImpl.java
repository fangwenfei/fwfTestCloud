package com.cfmoto.bar.code.service.cecdelivergoods.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.bo.CfCecDeliverGoodsBo;
import com.cfmoto.bar.code.model.dto.CfCecDeliverGoodsDto;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.model.vo.CfCecDeliverGoodsVo;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.cecdelivergoods.ICfCecDeliverGoodsInfoService;
import com.cfmoto.bar.code.service.cecdelivergoods.ICfCecDeliverGoodsInventoryService;
import com.cfmoto.bar.code.service.cecdelivergoods.ICfCecDeliverGoodsScanRecordService;
import com.cfmoto.bar.code.service.cecdelivergoods.ICfCecDeliverGoodsService;
import com.cfmoto.bar.code.service.partsmanage.ICfPackingListService;
import com.cfmoto.bar.code.utiles.BarcodeUtils;
import com.github.pig.common.vo.UserVO;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部品网购发货 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-04
 */
@Service
@Log4j
public class CfCecDeliverGoodsServiceImpl implements ICfCecDeliverGoodsService {
    @Autowired
    private ICfCecDeliverGoodsInfoService infoService;

    @Autowired
    private ICfCecDeliverGoodsInventoryService inventoryService;

    @Autowired
    private ICfCecDeliverGoodsScanRecordService scanRecordService;

    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private ICfPackingListService packingListService;

    /**
     * 根据交货单号去SAP系统中查找数据，并与本地数据库比较更新后返回最新数据
     *
     * @param deliverOrderNo 交货单号
     * @return CfCecDeliverGoodsVo
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CfCecDeliverGoodsVo getDataByDeliverOrderNo(String deliverOrderNo, int userId) throws Exception {
        //先根据交货单号去SAP中拉取销售订单数据
        CfCecDeliverGoodsVo deliverGoodsVo = sapApiService.getDataFromSapApi007(deliverOrderNo);

        // TODO 校验sap返回的订单类型

        String orderStatus = deliverGoodsVo.getOrderStatus();
        //校验sap返回的订单状态
        if (StrUtil.isBlank(orderStatus)) {
            throw new Exception("订单状态为空，请注意!!!");
        }

        //订单状态已完成，报错
        if (orderStatus.equals(CfCecDeliverGoodsBo.COMPLETE_ORDER_STATUS)) {
            throw new Exception("发货通知单处于已完成状态，请注意！");
        }

        //获取sap返回的清单表集合
        List<CfCecDeliverGoodsInventory> sapInventoryList = deliverGoodsVo.getInventoryList();

        //根据交货单号获取数据库中的信息表数据
        CfCecDeliverGoodsInfo dbInfo = infoService.selectOne(new EntityWrapper<CfCecDeliverGoodsInfo>().eq("deliver_order_no", deliverOrderNo));

        //从数据库中获取清单表数据
        List<CfCecDeliverGoodsInventory> dbInventoryList = inventoryService.selectList(new EntityWrapper<CfCecDeliverGoodsInventory>().eq("deliver_order_no", deliverOrderNo));

        //对比结果有三种情况：
        //    1.从数据库获取的信息表数据为空，则直接将SAP返回的数据插入到数据库
        if (dbInfo == null) {
            //插入信息表和清单表
            try {

                this.insertInfoAndInventory(userId, deliverGoodsVo);
                //从数据库查询数据并返回
                return this.getDataFromDataBase(deliverOrderNo, userId);
            } catch (Exception e) {
                e.printStackTrace();
                //打印错误日志
                log.error(e.getMessage());
                throw new Exception("插入数据失败!!!,请联系管理员!!!");
            }
        } else if (dbInventoryList.size() == 0) {
            //插入清单表
            try {
                this.insertInventoryList(userId, sapInventoryList);
                //从数据库查询数据并返回
                return this.getDataFromDataBase(deliverOrderNo, userId);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                throw new Exception("插入数据失败!!!,请联系管理员!!!");
            }
        }


        //    2.SAP获取的数据与数据库获取的数据一致（条数，每条数据的物料条码和数量都一致），那么则不做操作,查询数据库数据并返回
        //    3.SAP获取的数据与数据库获取的数据不一致：删除数据库中的清单表数据和扫描表数据，然后将SAP获取的数据插入到数据库中

        //不一致
        if (sapInventoryList.size() != dbInventoryList.size()) {
            //删除数据库清单表
            inventoryService.delete(new EntityWrapper<CfCecDeliverGoodsInventory>().eq("deliver_order_no", deliverOrderNo));
            //插入sap的清单表数据
            this.insertInventoryList(userId, sapInventoryList);
            //比对数据库扫描表数据并修改清单表的数量
            this.updateInventoryByScanList(deliverOrderNo, userId);
        } else {

            boolean flag = true;

            //将sap返回的数据和数据库查找到数据按物料代码和行项目逐个匹配，判断是否一致
            for (int i = 0; i < sapInventoryList.size(); i++) {
                if (sapInventoryList.get(i).getMaterialsNo().equals(dbInventoryList.get(i).getMaterialsNo()) && sapInventoryList.get(i).getRowItem().equals(dbInventoryList.get(i).getRowItem())) {
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
                inventoryService.delete(new EntityWrapper<CfCecDeliverGoodsInventory>().eq("deliver_order_no", deliverOrderNo));
                //插入sap的清单表数据
                this.insertInventoryList(userId, sapInventoryList);
                //比对数据库扫描表数据并修改清单表的数量
                this.updateInventoryByScanList(deliverOrderNo, userId);
            }
        }
        //从数据库查询数据并返回
        return this.getDataFromDataBase(deliverOrderNo, userId);
    }

    @Override
    public CfCecDeliverGoodsVo scanBarcode(CfCecDeliverGoodsDto dto) throws Exception {
        //获取用户工厂
        UserVO user = userFeignService.user(dto.getUserId());
        dto.setFactory(user.getSite());
        if (StrUtil.isBlank(user.getSite())) {
            throw new Exception("该用户还没有绑定工厂,请去绑定工厂信息!!!");
        }

        //拆分条码中的物料代码和数量
        Map<String, Object> map = BarcodeUtils.anaylysisAndSplitBarcodeThrowException(dto.getBarcode());
        String materialsNo = (String) map.get("materialsNo");
        int barcodeNumber = Integer.parseInt(map.get("number").toString());

        //根据交货单和物料代码去清单表中查找数据
        CfCecDeliverGoodsInventory inventory = inventoryService.selectOne(new EntityWrapper<CfCecDeliverGoodsInventory>()
                .eq("deliver_order_no", dto.getDeliverOrderNo())
                .eq("materials_no", materialsNo));

        //如果未查找到数据，则报错
        if (inventory == null) {
            throw new Exception("该物料不在汇总清单，请注意！");
        } else {
            //比较未清数量和条码数量
            int unCleardNumber = inventory.getNumber() - inventory.getScannedNumber();
            //如果条码数量大于条码数量则报错
            if (barcodeNumber > unCleardNumber) {
                throw new Exception("条码数量大于未清数量，请注意！");
            } else {
                //进行物料批次匹配
                //下载SAP库存信息
                List<Map<String, Object>> sapMapList = sapApiService.newGetDataFromSapApi10(dto.getWarehouse(), materialsNo, dto.getFactory(), "");
                //匹配
                List<Map<String, Object>> matchMapList = this.dynamicMatchNumberByBarcodeNumber(barcodeNumber, sapMapList, materialsNo, dto.getDeliverOrderNo(), dto.getUserId());

                //校验匹配数量是否足够
                int matchedNumber = 0;
                for (Map<String, Object> tempMap : matchMapList) {
                    matchedNumber += (int) tempMap.get("toBeMatchNumber");
                }
                if (barcodeNumber > matchedNumber) {
                    throw new Exception("可匹配数量不足,为" + matchedNumber + "个,请注意！！！");
                }

                //封装返回信息
                CfCecDeliverGoodsVo deliverGoodsVo = new CfCecDeliverGoodsVo();
                deliverGoodsVo.setBatchMatchList(matchMapList);
                //物料代码
                deliverGoodsVo.setMaterialsNo(materialsNo);
                //物料名称
                deliverGoodsVo.setMaterialsName(inventory.getMaterialsName());
                //未清数量
                deliverGoodsVo.setUnClearedNumber(inventory.getNumber() - inventory.getScannedNumber());
                //数量
                deliverGoodsVo.setBarcodeNumber(barcodeNumber);
                //条码
                deliverGoodsVo.setBarcode(dto.getBarcode());
                //行项目
                deliverGoodsVo.setRowItem(inventory.getRowItem());
                //交货单
                deliverGoodsVo.setDeliverOrderNo(dto.getDeliverOrderNo());
                //运单号
                deliverGoodsVo.setTrackingNo(dto.getTrackingNo());
                //销售订单
                deliverGoodsVo.setSalesOrderNo(inventory.getSalesOrderNo());
                return deliverGoodsVo;
            }

        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertInventoryList(int userId, List<CfCecDeliverGoodsInventory> inventoryList) {
        for (CfCecDeliverGoodsInventory inventory : inventoryList) {
            inventory.setObjectSetBasicAttribute(userId, new Date());
            inventoryService.insert(inventory);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertInfoAndInventory(int userId, CfCecDeliverGoodsVo deliverGoodsVo) {
        //插入信息表数据
        CfCecDeliverGoodsInfo info = deliverGoodsVo.getInfo();
        info.setObjectSetBasicAttribute(userId, new Date());
        infoService.insert(info);

        //插入清单表数据
        this.insertInventoryList(userId, deliverGoodsVo.getInventoryList());
    }

    @Override
    public CfCecDeliverGoodsVo getDataFromDataBase(String deliverOrderNo, int userId) {
        //初始化VO对象
        CfCecDeliverGoodsVo deliverGoodsVo = new CfCecDeliverGoodsVo();

        //获取信息表对象
        CfCecDeliverGoodsInfo info = infoService.selectOne(new EntityWrapper<CfCecDeliverGoodsInfo>().eq("deliver_order_no", deliverOrderNo));
        deliverGoodsVo.setInfo(info);

        //获取清单表集合
        List<CfCecDeliverGoodsInventory> inventoryList = inventoryService.selectList(new EntityWrapper<CfCecDeliverGoodsInventory>().eq("deliver_order_no", deliverOrderNo));
        deliverGoodsVo.setInventoryList(inventoryList);

        //获取扫描表集合
        List<CfCecDeliverGoodsScanRecord> scanRecordList = scanRecordService.selectList(new EntityWrapper<CfCecDeliverGoodsScanRecord>()
                .eq("deliver_order_no", deliverOrderNo)
                .eq("created_by", userId));

        List<CfCecDeliverGoodsScanRecord> processedScanRecordList = this.gatherManyToOne(scanRecordList);

        //将处理好的扫描表集合放入vo对象中
        deliverGoodsVo.setScanRecordList(processedScanRecordList);
        return deliverGoodsVo;
    }

    @Override
    public List<CfCecDeliverGoodsScanRecord> gatherManyToOne(List<CfCecDeliverGoodsScanRecord> scanRecordList) {
        List<CfCecDeliverGoodsScanRecord> tempScanRecordList = new ArrayList<>();

        //将扫描表中的同一物料代码的数据汇合成一条扫描表记录
        for (int i = 0; i < scanRecordList.size(); i++) {
            for (int j = 0; j < scanRecordList.size(); j++) {
                if (i != j) {
                    //如果templist中不存在此条对应单号和物料代码的数据则放入
                    for (CfCecDeliverGoodsScanRecord record : tempScanRecordList) {
                        if (!record.getDeliverOrderNo().equals(scanRecordList.get(i).getDeliverOrderNo()) && !record.getMaterialsNo().equals(scanRecordList.get(i).getMaterialsNo())) {
                            //叠加交货单号和物料代码相同的扫描表数据
                            if (scanRecordList.get(i).getDeliverOrderNo().equals(scanRecordList.get(j).getDeliverOrderNo()) && scanRecordList.get(i).getMaterialsNo().equals(scanRecordList.get(j).getMaterialsNo())) {
                                scanRecordList.get(i).setNumber(scanRecordList.get(j).getNumber() + scanRecordList.get(i).getNumber());
                            }
                        }
                    }
                }
            }
            //将汇合好的数据放入集合
            tempScanRecordList.add(scanRecordList.get(i));
        }
        return tempScanRecordList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateInventoryByScanList(String deliverOrderNo, Integer userId) {
        List<CfCecDeliverGoodsInventory> inventoryList = inventoryService.selectList(new EntityWrapper<CfCecDeliverGoodsInventory>().eq("deliver_order_no", deliverOrderNo));
        //比对清单表和扫描表
        for (CfCecDeliverGoodsInventory inventory : inventoryList) {
            //根据清单表的物料代码和单号和行项目找到对应的扫描表数据
            List<CfCecDeliverGoodsScanRecord> scanRecords = scanRecordService.selectList(new EntityWrapper<CfCecDeliverGoodsScanRecord>()
                    .eq("materials_no", inventory.getMaterialsNo())
                    .eq("deliver_order_no", inventory.getDeliverOrderNo())
                    .eq("row_item", inventory.getRowItem()));
            for (CfCecDeliverGoodsScanRecord scanRecord : scanRecords) {
                inventory.setScannedNumber(inventory.getScannedNumber() + scanRecord.getNumber());
            }
            inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
            inventoryService.updateById(inventory);
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
    public List<Map<String, Object>> dynamicMatchNumberByBarcodeNumber(int barcodeNumber, List<Map<String, Object>> list, String materialsNo, String deliverOrderNo, int userId) {
        //根据交货单号和物料代码和用户ID加载已扫描数据
        List<CfCecDeliverGoodsScanRecord> scanRecordList = scanRecordService.selectList(new EntityWrapper<CfCecDeliverGoodsScanRecord>()
                .eq("deliver_order_no", deliverOrderNo)
                .eq("materials_no", materialsNo)
                .eq("created_by", userId));

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
                for (CfCecDeliverGoodsScanRecord scanRecord : scanRecordList) {
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
    public void insertRecordAndUpdateInventory(CfCecDeliverGoodsVo cfCecDeliverGoodsVo, int userId) throws Exception {
        CfCecDeliverGoodsInventory inventory = inventoryService.selectOne(new EntityWrapper<CfCecDeliverGoodsInventory>()
                .eq("deliver_order_no", cfCecDeliverGoodsVo.getDeliverOrderNo())
                .eq("materials_no", cfCecDeliverGoodsVo.getMaterialsNo())
                .eq("row_item", cfCecDeliverGoodsVo.getRowItem()));

        if (inventory == null) {
            throw new Exception("物料批次匹配提交失败，未找到对应清单表数据!!!");
        }

        List<Map<String, Object>> batchMatchList = cfCecDeliverGoodsVo.getBatchMatchList();
        for (Map<String, Object> map : batchMatchList) {
            //插入扫描表数据
            CfCecDeliverGoodsScanRecord record = new CfCecDeliverGoodsScanRecord();
            record.setBatchNo(map.get("batchNo").toString());
            record.setNumber(Integer.parseInt(map.get("toBeMatchNumber").toString()));
            record.setBarcode(cfCecDeliverGoodsVo.getBarcode());
            record.setBarcodeType("BP");
            record.setDeliverOrderNo(cfCecDeliverGoodsVo.getDeliverOrderNo());
            record.setMaterialsName(cfCecDeliverGoodsVo.getMaterialsName());
            record.setMaterialsNo(cfCecDeliverGoodsVo.getMaterialsNo());
            record.setRowItem(cfCecDeliverGoodsVo.getRowItem());
            record.setSalesOrderNo(cfCecDeliverGoodsVo.getSalesOrderNo());
            record.setWarehousePosition(map.get("warehousePosition").toString());
            record.setStorageArea(map.get("storageArea").toString());
            record.setSpec(map.get("spec").toString());
            record.setWarehouse(map.get("wareHouse").toString());
            record.setTrackingNo(cfCecDeliverGoodsVo.getTrackingNo());
            record.setObjectSetBasicAttribute(userId, new Date());
            scanRecordService.insert(record);

            //更新对应的清单表中的已扫描数量
            inventory.setScannedNumber(inventory.getScannedNumber() + record.getNumber());
        }
        inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
        inventoryService.updateById(inventory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(String deliverOrderNo, String materialsNo, Integer number, Integer total, Integer userId) throws Exception {
        List<CfCecDeliverGoodsScanRecord> scanRecordList = scanRecordService.selectList(new EntityWrapper<CfCecDeliverGoodsScanRecord>()
                .eq("deliver_order_no", deliverOrderNo)
                .eq("materials_no", materialsNo)
                .eq("created_by", userId));
        if (scanRecordList != null && scanRecordList.size() != 0) {
            if (number > total) {
                throw new Exception("修改数量不能大于当前已扫描物料总数!!!");
            }
            int toBeDelete = total - number;
            for (CfCecDeliverGoodsScanRecord scanRecord : scanRecordList) {
                //如果待删除数量为0则跳出循环
                if (toBeDelete == 0) {
                    break;
                }
                if (scanRecord.getNumber() <= toBeDelete) {
                    //删除扫描表
                    scanRecord.deleteById(scanRecord);
                } else {
                    //修改扫描表数量
                    scanRecord.setNumber(scanRecord.getNumber() - toBeDelete);
                    scanRecord.setObjectSetBasicAttributeForUpdate(userId, new Date());
                    scanRecordService.updateById(scanRecord);
                }
                //递减待删除数量
                toBeDelete -= scanRecord.getNumber();
            }

            //更新汇总表已扫描数量
            CfCecDeliverGoodsInventory inventory = inventoryService.selectOne(new EntityWrapper<CfCecDeliverGoodsInventory>()
                    .eq("deliver_order_no", deliverOrderNo)
                    .eq("materials_no", materialsNo)
                    .eq("row_item", scanRecordList.get(0).getRowItem()));
            if (inventory == null) {
                throw new Exception("未找到该行记录表对应的清单表数据,请注意!!!");
            }
            inventory.setScannedNumber(number);
            inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
            inventoryService.updateById(inventory);

        } else {
            throw new Exception("未找到该物料的扫描表数据,请注意!!!");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String deliverOrderNo, String materialsNo, Integer userId) throws Exception {
        //根据交货单号和物料代码和用户ID查询扫描表记录
        List<CfCecDeliverGoodsScanRecord> scanRecordList = scanRecordService.selectList(new EntityWrapper<CfCecDeliverGoodsScanRecord>()
                .eq("deliver_order_no", deliverOrderNo)
                .eq("materials_no", materialsNo)
                .eq("created_by", userId));

        //删除扫描表数据
        if (scanRecordList != null && scanRecordList.size() != 0) {
            for (CfCecDeliverGoodsScanRecord scanRecord : scanRecordList) {
                scanRecordService.deleteById(scanRecord);
            }
        } else {
            throw new Exception("未找到该物料的扫描表数据,请注意!!!");
        }

        //根据操作类型更新汇总界面的扫描数量
        CfCecDeliverGoodsInventory inventory = inventoryService.selectOne(new EntityWrapper<CfCecDeliverGoodsInventory>()
                .eq("deliver_order_no", deliverOrderNo)
                .eq("materials_no", materialsNo)
                .eq("row_item", scanRecordList.get(0).getRowItem()));
        if (inventory != null) {
            inventory.setScannedNumber(0);
            inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
            inventoryService.updateById(inventory);
        } else {
            throw new Exception("未找到该行扫描数据对应的清单表数据,请注意!!!");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finalCommit(String deliverOrderNo, int userId) throws Exception {
        //获取对应单号的扫描表数据并发送给sap
        List<CfCecDeliverGoodsScanRecord> scanRecordList = scanRecordService.selectList(new EntityWrapper<CfCecDeliverGoodsScanRecord>()
                .eq("deliver_order_no", deliverOrderNo)
                .eq("created_by", userId));

        if (scanRecordList == null || scanRecordList.size() == 0) {
            throw new Exception("该交货单下没有对应的扫描数据,请注意!!!");
        }
        //发送数据给SAP 006接口
        CfCecDeliverGoodsVo deliverGoodsVo = sapApiService.postDataToSapApi006(deliverOrderNo, scanRecordList);

        //判断返回的订单状态
        if (StrUtil.isBlank(deliverGoodsVo.getOrderStatus()) || !deliverGoodsVo.getOrderStatus().equals(CfCecDeliverGoodsBo.COMPLETE_ORDER_STATUS)) {
            throw new Exception("提交后的订单状态有误,请注意!!!");
        }

        //插入数据到packingList表中
        for (CfCecDeliverGoodsScanRecord scanRecord : scanRecordList) {
            CfPackingList packingList = new CfPackingList();
            packingList.setItem(scanRecord.getMaterialsNo());
            packingList.setItemDesc(scanRecord.getMaterialsName());
            packingList.setMode(scanRecord.getSpec());
            packingList.setQty(new BigDecimal(scanRecord.getNumber()));
            packingList.setObjectSetBasicAttribute(userId, new Date());

            packingListService.insert(packingList);
        }

        //删除交货单号下对饮的所有信息、清单、扫描表数据
        infoService.delete(new EntityWrapper<CfCecDeliverGoodsInfo>().eq("deliver_order_no", deliverOrderNo));
        inventoryService.delete(new EntityWrapper<CfCecDeliverGoodsInventory>().eq("deliver_order_no", deliverOrderNo));
        //删除属于当前用户的扫描表数据
        scanRecordService.delete(new EntityWrapper<CfCecDeliverGoodsScanRecord>()
                .eq("deliver_order_no", deliverOrderNo)
                .eq("created_by", userId));

    }
}
