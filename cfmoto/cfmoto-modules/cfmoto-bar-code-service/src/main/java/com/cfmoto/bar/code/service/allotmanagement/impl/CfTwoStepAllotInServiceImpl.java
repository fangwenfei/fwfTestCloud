package com.cfmoto.bar.code.service.allotmanagement.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfKtmReceivingOrderService;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.allotmanagement.*;
import com.cfmoto.bar.code.utiles.BarcodeUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 两部调拨入库业务层接口实现类
 *
 * @author ye
 */

@Service
@Log4j
public class CfTwoStepAllotInServiceImpl implements ICfTwoStepAllotInService {
    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private ICfAllotScanRecordService scanRecordService;

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;

    @Autowired
    private ICfKtmReceivingOrderService ktmReceivingOrderService;

    @Autowired
    private ICfAllotOnWayDataService cfAllotOnWayDataService;

    @Autowired
    private ICfAllotManagementCommonService cfAllotManagementCommonService;

    @Autowired
    private ICfKtmReceivingOrderService cfKtmReceivingOrderService;

    @Autowired
    private ICfAllotInventoryService cfAllotInventoryService;

    @Autowired
    private ICfAllotScanRecordService cfAllotScanRecordService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CfAllotManagementVo scanBarcode(String barcode, String barcodeType, String orderNo, int userId) throws Exception {
        //判断条码类型，选择对应的业务逻辑进行处理

        //条码为库存条码
        if (barcodeType.equals(BarcodeUtils.INVENTORY_BARCODE_CODE)) {

            //校验是否存在KTM:
            CfKtmReceivingOrder ktm = cfKtmReceivingOrderService.selectOne(new EntityWrapper<CfKtmReceivingOrder>().eq("frame_no", barcode));
            CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", barcode));
            CfAllotOnWayData cfAllotOnWayData;
            if (ktm != null) {
                //存在KTM表，校验数据是否为0
                //不为0，报错
                if (ktm.getBarCodeNumber().compareTo(new BigDecimal(0)) != 0) {
                    throw new Exception("条码已经入库，请检查！");
                } else {
                    //为0
                    cfAllotOnWayData = cfAllotOnWayDataService.selectOne(new EntityWrapper<CfAllotOnWayData>().eq("barcode", barcode));
                    //在途表数据不存在，报错
                    if (cfAllotOnWayData == null) {
                        throw new Exception("条码不在该调拨单的在途数据，请注意！");
                    }
                }
            } else {
                //不存在KTM表,判断是否存在库存表
                if (barcodeInventory != null) {
                    //	CP/EG校验数据是否为0，
                    //	不为0报错“条码已经入库，请注意！”；
                    //	为0，校验是否存在调拨在途表中对应调拨单的行数据
                    //	不存在，报错“条码不在该调拨单的在途数据，请注意！”
                    //	存在，带出相关数据；
                    String type = barcodeInventory.getBarcodeType();
                    switch (type) {
                        case "CP":
                        case "EG": {

                            //不为0
                            if (barcodeInventory.getBarCodeNumber().compareTo(new BigDecimal(0)) != 0) {
                                throw new Exception("条码已经入库，请注意！");
                            } else {
                                //为0
                                cfAllotOnWayData = cfAllotOnWayDataService.selectOne(new EntityWrapper<CfAllotOnWayData>().eq("barcode", barcode));
                                if (cfAllotOnWayData == null) {
                                    throw new Exception("条码不在该调拨单的在途数据，请注意！");
                                }
                            }
                            break;

                        }
                        case "OT": {
                            //OT校验数据条码状态是否为不可用，	非不可用报错“条码已经入库，请注意！”；
                            //	不可用状态，校验物料+批次是否存在调拨在途表中对应调拨单的行数据
                            //	不存在，报错“条码对应的物料及对应批次不在该调拨单的在途数据，请注意！”
                            //	存在，带出相关数据
                            if (!barcodeInventory.getState().equals(BarcodeUtils.UNAVAILABLE_STATE_CODE)) {
                                throw new Exception("条码已经入库，请注意！");
                            } else {
                                //不可用状态
                                cfAllotOnWayData = cfAllotOnWayDataService.selectOne(new EntityWrapper<CfAllotOnWayData>().eq("materials_no", barcodeInventory.getMaterialsNo()).eq("batch_no", barcodeInventory.getBatchNo()));
                                if (cfAllotOnWayData == null) {
                                    throw new Exception("条码对应的物料及对应批次不在该调拨单的在途数据，请注意！");
                                }
                            }
                            break;
                        }
                        default: {
                            throw new Exception("条码类型错误！！！");
                        }
                    }
                } else {
                    //不存在库存表,报错
                    throw new Exception("条码不存在，请注意！");
                }
            }

            String materialsName;
            String materialsNo;
            BigDecimal barcodeNumber;
            String spec;
            String type;
            String batchNo;
            String wareHouse;
            String storageArea;
            String wareHousePosition;
            String supplier;
            //走到这里说明条码存在且符合校验条件
            if (ktm != null) {
                //存在ktm
                materialsNo = ktm.getMaterialsNo();
                barcodeNumber = ktm.getBarCodeNumber();
                materialsName = ktm.getMaterialsName();
                spec = "";
                type = "KTM";
                batchNo = ktm.getBatchNo();
                wareHouse = ktm.getRepository();
                storageArea = "";
                wareHousePosition = "";
                supplier = ktm.getSupplier();
            } else {
                //存在库存
                materialsNo = barcodeInventory.getMaterialsNo();
                barcodeNumber = barcodeInventory.getBarCodeNumber();
                materialsName = barcodeInventory.getMaterialsName();
                spec = barcodeInventory.getMode();
                type = barcodeInventory.getBarcodeType();
                batchNo = barcodeInventory.getBatchNo();
                wareHouse = barcodeInventory.getWarehouse();
                storageArea = barcodeInventory.getStorageArea();
                wareHousePosition = barcodeInventory.getWarehousePosition();
                supplier = barcodeInventory.getSuppler();
            }

            //根据条码、物料代码、批次 校验该物料是否以及扫描过
            //根据条码、物料代码、批次 校验该物料是否以及扫描过
            CfAllotScanRecord scanRecord = cfAllotScanRecordService.selectOne(
                    new EntityWrapper<CfAllotScanRecord>()
                            .eq("barcode", barcode)
                            .eq("materials_no", materialsNo)
                            .eq("batch_no", batchNo)
                            .eq("operate_type", "02"));
            if (scanRecord != null) {
                throw new Exception("此条码已经扫描过了，请注意!!!");
            }


            //检验物料是否存在汇总,根据调拨单号和物料代码获取调拨清单表数据
            CfAllotInventory cfAllotInventory = cfAllotInventoryService.selectOne(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo).eq("materials_no", materialsNo));
            if (cfAllotInventory == null) {
                //不存在，报错“该物料不在汇总清单，请注意！”
                throw new Exception("该物料不在汇总清单，请注意！");
            } else {

                //存在
                //	当汇总中实入数量=应入数量，报错“该物料已经入库完成，请注意”；
                if (cfAllotInventory.getNumber().intValue() == cfAllotInventory.getAllotInScannedNumber().intValue()) {
                    throw new Exception("该物料已经入库完成，请注意");
                    //当汇总中实入数量<应入数量
                } else if (cfAllotInventory.getAllotInScannedNumber() < cfAllotInventory.getNumber()) {

                    //应入数量-实入数量
                    BigDecimal bigDecimal = new BigDecimal(cfAllotInventory.getNumber() - cfAllotInventory.getAllotInScannedNumber());

                    //如果是CP/EG条码，就默认数量为1
                    if (type.equals("CP") || type.equals("EG") || type.equals("KTM")) {
                        barcodeNumber = new BigDecimal(1);
                    }

                    //	库存表中条码数量<=应入数量-实入数量
                    if (barcodeNumber.intValue() <= bigDecimal.intValue()) {
                        CfAllotScanRecord record = new CfAllotScanRecord();
                        record.setNumber(barcodeNumber.intValue());
                        record.setOrderNo(orderNo);
                        record.setOperateType("02");
                        record.setMaterialsName(materialsName);
                        record.setMaterialsNo(materialsNo);
                        record.setSpec(spec);
                        record.setBarcode(barcode);
                        record.setBarcodeType(type);
                        record.setBatchNo(batchNo);
                        record.setWarehouse(wareHouse);

                        record.setStorageArea(storageArea);
                        record.setWarehousePosition(wareHousePosition);
                        record.setState("");
                        record.setSupplier(supplier);
                        record.setAllotInWarehouse(cfAllotInventory.getAllotInWarehouse());
                        record.setAllotInventoryId(cfAllotInventory.getAllotInventoryId());
                        record.setAllotInfoId(cfAllotInventory.getAllotInfoId());
                        record.setObjectSetBasicAttribute(userId, new Date());
                        try {
                            cfAllotScanRecordService.insert(record);
                        } catch (Exception e) {
                            //打印错误日志
                            log.info(e.getMessage());
                            //输出错误信息到控制台
                            e.printStackTrace();
                            throw new Exception("插入扫描记录表错误!!!");
                        }

                        //2.	更新汇总界面信息，更新调拨清单表中的数据（调入已扫描数量）
                        cfAllotInventory.setAllotInScannedNumber(cfAllotInventory.getAllotInScannedNumber() + record.getNumber());
                        try {
                            cfAllotInventoryService.updateById(cfAllotInventory);
                        } catch (Exception e) {
                            //打印错误日志
                            log.info(e.getMessage());
                            //输出错误信息到控制台
                            e.printStackTrace();
                            throw new Exception("更新调拨清单表错误!!!");
                        }

                    } else {
                        throw new Exception("条码异常，条码数量超出待调入数量");
                    }

                }
                //从数据库中获取数据并返回
                return cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId);
            }

            //条码为物料条码
        } else if (barcodeType.equals(BarcodeUtils.MATERIALS_BARCODE_CODE)) {

            //	校验条码中是否含有*号
            //	否，报错“条码非部品条码，请注意!”
            //	是，解析出物料代码和数量（*号之前为物料条码，*号之后为数量）
            Map<String, Object> map = BarcodeUtils.anaylysisAndSplitBarcodeThrowException(barcode);
            String materialsNo = (String) map.get("materialsNo");
            int number = (int) map.get("number");
            //	当解析的数量为0时，报错“条码数量为0，请注意！”
            if (number == 0) {
                throw new Exception("条码数量为0，请注意！");
            }
            //	检验物料是否存在汇总界面
            //	否，报错“条码不存在汇总，请注意！”
            //	是，获取物料行数据
            CfAllotInventory cfAllotInventory = cfAllotInventoryService.selectOne(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo).eq("materials_no", materialsNo));
            if (cfAllotInventory == null) {
                //不存在，报错“该物料不在汇总清单，请注意！”
                throw new Exception("条码不存在汇总，请注意！");
            }
            //	校验汇总数量
            //	当汇总中实入数量=应入数量，报错“该物料已经领料完成，请注意”；
            if (cfAllotInventory.getNumber().equals(cfAllotInventory.getAllotInScannedNumber())) {
                throw new Exception("该物料已经调入完成，请注意");
            }

            int subNumber = cfAllotInventory.getNumber() - cfAllotInventory.getAllotInScannedNumber();
            //	当条码数量>应入数量-实入数量，报错“该物料条码超过待领料数量，请注意”；
            if (number > subNumber) {
                throw new Exception("该物料条码超过待调入数量，请注意");
                //	当条码数量<=应发数量-实发数量，弹出物料批次匹配界面
            } else {

                //自动匹配
                List<Map<String, Object>> mapList = cfAllotManagementCommonService.dynamicMatchNumberByBarcodeNumberFoAllotIn(number, materialsNo, orderNo);

                //校验匹配数量是否足够
                int matchedNumber = 0;
                for (Map<String, Object> tempMap : mapList) {
                    matchedNumber += (int) tempMap.get("toBeMatchNumber");
                }
                if (number > matchedNumber) {
                    throw new Exception("可匹配数量不足,为"+matchedNumber+"个,请注意！！！");
                }

                //提交
                cfAllotManagementCommonService.insertRecordAndUpdateInventoryForAllotIn(orderNo, "02", materialsNo, barcode, mapList, userId);

                //返回数据
                return cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId);

            }

        } else {
            throw new Exception("请输入有效的条码类型!!!");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String finalCommit(String orderNo, int userId) throws Exception {
        List<CfAllotScanRecord> scanRecords = scanRecordService.getUncommittedCfAllotScanRecordListByOrderNo(orderNo, "02", userId);

        if (scanRecords == null) {
            throw new Exception("没有可提交的已扫描数据!!!");
        }

        //调用sap接口
        sapApiService.postDataToSapApi09B(scanRecords);

        for (CfAllotScanRecord scanRecord : scanRecords) {
            //获取条码类型
            String barcodeType = scanRecord.getBarcodeType();

            CfAllotOnWayData cfAllotOnWayData;

            switch (barcodeType) {
                case "OT":
                case "BP": {
                    cfAllotOnWayData = cfAllotOnWayDataService.selectOne(new EntityWrapper<CfAllotOnWayData>().eq("materials_no", scanRecord.getMaterialsNo()).eq("batch_no", scanRecord.getBatchNo()));
                    break;
                }
                default: {
                    cfAllotOnWayData = cfAllotOnWayDataService.selectOne(new EntityWrapper<CfAllotOnWayData>().eq("barcode", scanRecord.getBarcode()));
                    break;
                }
            }

            if (cfAllotOnWayData == null) {
                throw new Exception("在途数据表中没有数据!!!");
            }

            cfAllotOnWayData.setAllotInNumber(cfAllotOnWayData.getAllotInNumber() + scanRecord.getNumber());
            cfAllotOnWayData.setBasicAttributeForUpdate(userId, new Date());
            cfAllotOnWayDataService.updateById(cfAllotOnWayData);

            //当调入数量等于在途表中数量时，后台删除该行在途数据表数据；否则，不做删除操作。
            if (cfAllotOnWayData.getNumber().equals(cfAllotOnWayData.getAllotInNumber())) {
                cfAllotOnWayDataService.deleteById(cfAllotOnWayData);
            }


            CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", scanRecord.getBarcode()));

            if(barcodeInventory != null){
                barcodeInventory.setWarehouse(scanRecord.getAllotInWarehouse());
                barcodeInventory.setStorageArea("");
                barcodeInventory.setWarehousePosition("");
                barcodeInventory.setBasicAttributeForUpdate(userId, new Date());

                switch (barcodeType) {
                    case "OT": {
                        barcodeInventory.setState("");
                        break;
                    }
                    case "CP":
                    case "EG": {
                        barcodeInventory.setBarCodeNumber(new BigDecimal(1));
                        break;
                    }
                    default: {
                        break;
                    }
                }

                barcodeInventoryService.updateById(barcodeInventory);
            }

            CfKtmReceivingOrder ktm = ktmReceivingOrderService.selectOne(new EntityWrapper<CfKtmReceivingOrder>().eq("frame_no", scanRecord.getBarcode()));
            if (barcodeInventory == null && ktm != null) {
                ktm.setBarCodeNumber(new BigDecimal(1));
                ktm.setRepository(scanRecord.getAllotInWarehouse());
                ktmReceivingOrderService.updateById(ktm);
            }

        }

        //删除调拨扫描表
        scanRecordService.deleteList(scanRecords);


        //调用两步调拨单传输接口获取调拨单状态
        CfAllotManagementVo vo = sapApiService.getDataFromSapApi08(orderNo);

        //判断订单状态，已完成则删除信息表和清单表
        if (vo.getOrderStatus().equals("已完成")) {
            cfAllotManagementCommonService.deleteAllotInfoAndInventory(orderNo);
        }
        return vo.getOrderStatus();
    }

}
