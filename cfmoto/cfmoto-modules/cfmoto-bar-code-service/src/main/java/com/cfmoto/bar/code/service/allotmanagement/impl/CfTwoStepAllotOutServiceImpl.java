package com.cfmoto.bar.code.service.allotmanagement.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfKtmReceivingOrderService;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.allotmanagement.*;
import com.cfmoto.bar.code.utiles.BarcodeUtils;
import com.github.pig.common.vo.UserVO;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 两部调拨出库业务层接口实现类
 *
 * @author ye
 */
@Service
@Log4j
public class CfTwoStepAllotOutServiceImpl implements ICfTwoStepAllotOutService {

    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private ICfKtmReceivingOrderService cfKtmReceivingOrderService;

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private ICfAllotInventoryService cfAllotInventoryService;

    @Autowired
    private ICfAllotScanRecordService cfAllotScanRecordService;

    @Autowired
    private ICfAllotManagementCommonService cfAllotManagementCommonService;

    @Autowired
    private ICfAllotOnWayDataService cfAllotOnWayDataService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CfAllotManagementVo scanBarcode(String barcode, String barcodeType, String orderNo, String wareHouseNo, int userId) throws Exception {
        //判断条码类型，选择对应的业务逻辑进行处理

        //条码为库存条码
        if (barcodeType.equals(BarcodeUtils.INVENTORY_BARCODE_CODE)) {

            //校验是否存在KTM:
            CfKtmReceivingOrder ktm = cfKtmReceivingOrderService.selectOne(new EntityWrapper<CfKtmReceivingOrder>().eq("frame_no", barcode));
            CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", barcode));

            if (ktm != null) {
                //存在KTM表，校验数据是否为0
                if (ktm.getBarCodeNumber().compareTo(new BigDecimal(0)) == 0) {
                    throw new Exception("条码不在库，请检查！");
                }
            } else {
                //不存在KTM表,判断是否存在库存表
                if (barcodeInventory != null) {
                    //	CP/EG校验数据是否为0，为0报错“条码不在库，请注意！”；不为0，带出相关数据
                    //	OT校验数据条码状态是否为不可用，不可用报错“状态为条码不可用，请注意！”；其他状态，带出相关数据
                    String type = barcodeInventory.getBarcodeType();
                    switch (type) {
                        case "CP":
                        case "EG": {
                            //校验数据是否为0，为0报错“条码不在库，请注意！”；不为0，带出相关数据
                            if (barcodeInventory.getBarCodeNumber().compareTo(new BigDecimal(0)) == 0) {
                                throw new Exception("条码不在库，请注意！");
                            }
                            break;
                        }
                        case "OT": {
                            //OT校验数据条码状态是否为不可用，不可用报错“状态为条码不可用，请注意！”；其他状态，带出相关数据
                            if (barcodeInventory.getState().equals(BarcodeUtils.UNAVAILABLE_STATE_CODE)) {
                                throw new Exception("状态为条码不可用，请注意！");
                            }
                            break;
                        }
                        default: {
                            throw new Exception("条码类型错误！！！");
                        }
                    }
                } else {
                    //不存在库存表,报错
                    throw new Exception("条码不在库，请检查！");
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
                            .eq("operate_type", "01"));
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
                //	当汇总中实出数量=应出数量，报错“该物料已经出库完成，请注意”；
                if (cfAllotInventory.getNumber().intValue() == cfAllotInventory.getAllotOutScannedNumber().intValue()) {
                    throw new Exception("该物料已经出库完成，请注意");
                    //当汇总中实出数量<应出数量
                } else if (cfAllotInventory.getAllotOutScannedNumber() < cfAllotInventory.getNumber()) {

                    //应出数量-实出数量
                    BigDecimal bigDecimal = new BigDecimal(cfAllotInventory.getNumber() - cfAllotInventory.getAllotOutScannedNumber());

                    CfAllotScanRecord record = new CfAllotScanRecord();

                    //	库存表中条码数量<应出数量-实出数量
                    if (barcodeNumber.compareTo(bigDecimal) == -1) {
                        //1.	记录已扫描界面的数量=库存表中条码数量，并插入数据到调拨扫描记录表（操作类型为01；；箱号、长、宽、高、毛重、发运单号、快递公司都为空），状态为未提交
                        record.setNumber(barcodeNumber.intValue());
                        //	库存表中条码数量>应出数量-实出数量
                    } else {
                        //1.	记录已扫描界面的数量=应出数量-实出数量，并插入数据到调拨扫描记录表（操作类型为01；；箱号、长、宽、高、毛重、发运单号、快递公司都为空），状态为未提交
                        record.setNumber(bigDecimal.intValue());
                    }
                    record.setAllotInWarehouse(cfAllotInventory.getAllotInWarehouse());
                    record.setOrderNo(orderNo);
                    record.setOperateType("01");
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

                    //2.	更新汇总界面信息，更新调拨清单表中的数据（调出已扫描数量）
                    cfAllotInventory.setAllotOutScannedNumber(cfAllotInventory.getAllotOutScannedNumber() + record.getNumber());
                    try {
                        cfAllotInventoryService.updateById(cfAllotInventory);
                    } catch (Exception e) {
                        //打印错误日志
                        log.info(e.getMessage());
                        //输出错误信息到控制台
                        e.printStackTrace();
                        throw new Exception("更新调拨清单表错误!!!");
                    }
                }
                //从数据库中获取数据并返回
                return cfAllotManagementCommonService.getDataFromDataBase(orderNo, "01", userId);
            }


            //条码为物料条码
        } else if (barcodeType.equals(BarcodeUtils.MATERIALS_BARCODE_CODE)) {
            //	校验单据头的调出仓库是否有选中数据
            //	否，报错“请在单据头选择调出仓库”
            if (StrUtil.isBlank(wareHouseNo)) {
                throw new Exception("请在单据头选择调出仓库");
            }
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
            //	当汇总中实发数量=应发数量，报错“该物料已经领料完成，请注意”；
            if (cfAllotInventory.getNumber().equals(cfAllotInventory.getAllotOutScannedNumber())) {
                throw new Exception("该物料已经调出完成，请注意");
            }

            int subNumber = cfAllotInventory.getNumber() - cfAllotInventory.getAllotOutScannedNumber();
            //	当条码数量>应发数量-实发数量，报错“该物料条码超过待领料数量，请注意”；
            if (number > subNumber) {
                throw new Exception("该物料条码超过待调出数量，请注意");
                //	当条码数量<=应发数量-实发数量，弹出物料批次匹配界面
            } else {
                //调用SAP接口10获取批次数据
                //传入当前登陆用户的工厂
                UserVO user = userFeignService.user(userId);
                List<Map<String, Object>> maps = sapApiService.newGetDataFromSapApi10(wareHouseNo, materialsNo, user.getSite(), "");
                //自动匹配
                List<Map<String, Object>> mapList = cfAllotManagementCommonService.dynamicMatchNumberByBarcodeNumber(number, "01", maps, materialsNo,userId);

                //校验匹配数量是否足够
                int matchedNumber = 0;
                for (Map<String, Object> tempMap : mapList) {
                    matchedNumber += (int) tempMap.get("toBeMatchNumber");
                }
                if (number > matchedNumber) {
                    throw new Exception("可匹配数量不足,为" + matchedNumber + "个,请注意！！！");
                }

                CfAllotManagementVo cfAllotManagementVo = new CfAllotManagementVo();
                cfAllotManagementVo.setBatchMatchList(mapList);
                //物料代码
                cfAllotManagementVo.setMaterialsNo(cfAllotInventory.getMaterialsNo());
                //物料名称
                cfAllotManagementVo.setMaterialsName(cfAllotInventory.getMaterialsName());
                //未清数量
                cfAllotManagementVo.setUnClearedNumber(cfAllotInventory.getNumber() - cfAllotInventory.getAllotOutScannedNumber());
                cfAllotManagementVo.setBarcodeNumber(number);
                return cfAllotManagementVo;
            }

        } else {
            throw new Exception("请输入有效的条码类型!!!");
        }

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String finalCommit(String orderNo, int userId) throws Exception {
        //从扫描表中获取未提交的对应操作类型、对应用户id的数据
        List<CfAllotScanRecord> scanRecords = cfAllotScanRecordService.getUncommittedCfAllotScanRecordListByOrderNo(orderNo, "01", userId);

        sapApiService.postDataToSapApi09A(scanRecords);

        //走到则说明sap返回的状态码为成功
        //01、将调拨扫描记录表中对应账号的对应单号的操作类型为01的未提交的行数据插入到调拨在途数据表
        //02、OT条码扣减对应库存，CP/EG/KTM扣减库存数量为0（KTM在KTM表），BP条码系统不做处理；
        for (CfAllotScanRecord scanRecord : scanRecords) {

            //判断条码类型
            //OT、BP，根据扫描表去查在途表数据，有则更新，无则插入
            if (scanRecord.getBarcodeType().equals("OT") || scanRecord.getBarcodeType().equals("BP")) {
                CfAllotOnWayData onWayData = cfAllotOnWayDataService.selectOne(new EntityWrapper<CfAllotOnWayData>().eq("order_no", scanRecord.getOrderNo()).eq("materials_no", scanRecord.getMaterialsNo()).eq("batch_no", scanRecord.getBatchNo()));
                //判断查找的在途数据是否为空
                if (onWayData != null) {
                    //更新调出数量
                    onWayData.setNumber(onWayData.getNumber() + scanRecord.getNumber());
                    onWayData.setBasicAttributeForUpdate(userId, new Date());
                    cfAllotOnWayDataService.updateById(onWayData);
                } else {
                    //为空，直接插入
                    CfAllotOnWayData cfAllotOnWayData = new CfAllotOnWayData();
                    cfAllotOnWayData.setOrderNo(scanRecord.getOrderNo());
                    cfAllotOnWayData.setMaterialsName(scanRecord.getMaterialsName());
                    cfAllotOnWayData.setMaterialsNo(scanRecord.getMaterialsNo());
                    cfAllotOnWayData.setSpec(scanRecord.getSpec());
                    cfAllotOnWayData.setBarcode(scanRecord.getBarcode());
                    cfAllotOnWayData.setBarcodeType(scanRecord.getBarcodeType());
                    cfAllotOnWayData.setFactory(userFeignService.user(userId).getSite());
                    cfAllotOnWayData.setBatchNo(scanRecord.getBatchNo());
                    cfAllotOnWayData.setNumber(scanRecord.getNumber());
                    cfAllotOnWayData.setAllotOutWarehouse(scanRecord.getWarehouse());
                    cfAllotOnWayData.setAllotInWarehouse(scanRecord.getAllotInWarehouse());
                    cfAllotOnWayData.setObjectSetBasicAttribute(userId, new Date());
                    cfAllotOnWayDataService.insert(cfAllotOnWayData);
                }

            } else {
                //其他类型，直接插入
                CfAllotOnWayData cfAllotOnWayData = new CfAllotOnWayData();
                cfAllotOnWayData.setOrderNo(scanRecord.getOrderNo());
                cfAllotOnWayData.setMaterialsName(scanRecord.getMaterialsName());
                cfAllotOnWayData.setMaterialsNo(scanRecord.getMaterialsNo());
                cfAllotOnWayData.setSpec(scanRecord.getSpec());
                cfAllotOnWayData.setBarcode(scanRecord.getBarcode());
                cfAllotOnWayData.setBarcodeType(scanRecord.getBarcodeType());
                cfAllotOnWayData.setBatchNo(scanRecord.getBatchNo());
                cfAllotOnWayData.setNumber(scanRecord.getNumber());
                cfAllotOnWayData.setAllotOutWarehouse(scanRecord.getWarehouse());
                cfAllotOnWayData.setAllotInWarehouse(scanRecord.getAllotInWarehouse());
                cfAllotOnWayData.setObjectSetBasicAttribute(userId, new Date());

                cfAllotOnWayDataService.insert(cfAllotOnWayData);
            }


            String barcode = scanRecord.getBarcode();
            String barcodeType = scanRecord.getBarcodeType();
            switch (barcodeType) {
                case "OT": {
                    CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", barcode));
                    barcodeInventory.setBarCodeNumber(barcodeInventory.getBarCodeNumber().subtract(new BigDecimal(scanRecord.getNumber())));
                    barcodeInventory.setObjectSetBasicAttribute(userId, new Date());
                    barcodeInventoryService.updateById(barcodeInventory);
                    break;
                }
                case "CP":
                case "EG": {
                    CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", barcode));
                    barcodeInventory.setBarCodeNumber(new BigDecimal(0));
                    barcodeInventory.setObjectSetBasicAttribute(userId, new Date());
                    barcodeInventoryService.updateById(barcodeInventory);
                    break;
                }
                case "KTM": {
                    CfKtmReceivingOrder ktm = cfKtmReceivingOrderService.selectOne(new EntityWrapper<CfKtmReceivingOrder>().eq("barcode", barcode));
                    ktm.setBarCodeNumber(new BigDecimal(0));
                    ktm.setObjectSetBasicAttribute(userId, new Date());
                    cfKtmReceivingOrderService.updateById(ktm);
                    break;
                }
                case "BP": {
                    log.info(scanRecord.getBarcode() + "条码为BP条码，不做处理!!!");
                    break;
                }
                default: {
                    throw new Exception("条码类型不正确");
                }
            }

        }
        //03、删除调拨扫描记录表中对应账号的对应单号的未提交的行数据
        cfAllotScanRecordService.deleteList(scanRecords);


        //调用两步调拨单传输接口，获取调拨单状态
        CfAllotManagementVo vo = sapApiService.getDataFromSapApi08(orderNo);

        //04、当订单状态为已完成，删除单号对应的信息表、清单表，提示“调拨单已完成！”。否则，不处理。
        if (vo.getOrderStatus().equals("已完成")) {
            cfAllotManagementCommonService.deleteAllotInfoAndInventory(orderNo);
        }

        return vo.getOrderStatus();
    }

}
