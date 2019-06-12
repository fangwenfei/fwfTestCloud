package com.cfmoto.bar.code.service.partsmanage.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfPackingListMapper;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.ICfSaleNextNumberService;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.allotmanagement.*;
import com.cfmoto.bar.code.service.partsmanage.ICfPackingListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 装箱清单数据表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-04-09
 */
@Slf4j
@Service
public class CfPackingListServiceImpl extends ServiceImpl<CfPackingListMapper, CfPackingList> implements ICfPackingListService {

    @Autowired
    private CfPackingListMapper cfPackingListMapper;

    @Autowired
    private ICfAllotInfoService cfAllotInfoService;

    @Autowired
    private ICfAllotInventoryService iCfAllotInventoryService;

    @Autowired
    private ICfAllotScanRecordService iCfAllotScanRecordService;

    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private ICfAllotManagementCommonService cfAllotManagementCommonService;

    @Autowired
    private ICfBarcodeInventoryService iCfBarcodeInventoryService;

    @Autowired
    private ICfAllotOnWayDataService iCfAllotOnWayDataService;

    @Autowired
    private ICfSaleNextNumberService iCfSaleNextNumberService;

    @Autowired
    private ICfNextNumberService iCfNextNumberService;

    /**
     * 获取调拨单数据
     * 会存在sap数据更改、本地数据未同步问题
     *
     * @param orderNo
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public CfAllotManagementVo getAllocationOrderData(String orderNo, int userId) throws Exception {

        //根据调拨单号获取调拨单信息表数据
        CfAllotInfo allotInfo = cfAllotInfoService.getAllotInfoByOrderNo(orderNo);
        //判断信息表是否为空
        //为空
        if (allotInfo == null) {
            //调用SAP08接口获取数据
            CfAllotManagementVo vo = sapApiService.getDataFromSapApi08(orderNo);
            if ("已完成".equals(vo.getOrderStatus())) {
                throw new Exception("装箱已完成，请注意！");
            }
            //插入信息表和清单表数据
            cfAllotManagementCommonService.insertAllotInfoAndInventory(userId, vo);
        }
        //最终从数据库中获取信息表、清单表、扫描表数据封装到CfAllotManagementVo对象中返回
        try {
            return cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            throw new Exception("从数据库中加载数据出错!!!");
        }

    }

    /**
     * 部品装箱功能-扫描行
     *
     * @param orderNo
     * @param barCode
     * @param scanType I-库存，M-物料
     * @param userId
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void scanRowData(String orderNo, String barCode, String scanType, int userId) throws Exception {

        //查询调拨单数据
        CfAllotManagementVo cfAllotManagementVo = cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId);
        CfAllotInfo cfAllotInfo = cfAllotManagementVo.getCfAllotInfo();
        if (cfAllotInfo == null) {
            throw new Exception("调拨单数据已修改，请重新获取调拨单数据");
        }
        //调拨清单数据
        List<CfAllotInventory> cfAllotInventoryList = cfAllotManagementVo.getCfAllotInventoryList();
        if (cfAllotInventoryList.size() == 0) {
            throw new Exception("调拨清单无数据");
        }
        //库存
        if (StrUtil.equals(scanType, "I")) {

            //扫描库存
            scanInventoryCode(barCode, userId, cfAllotManagementVo.getCfAllotScanRecordList(), cfAllotInventoryList, cfAllotInfo);

            //物料
        } else if (StrUtil.equals(scanType, "M")) {

            //扫描物料
            scanItemCode(barCode, userId, cfAllotInventoryList, cfAllotInfo);

        } else {
            throw new Exception("未知扫码类型");
        }

    }

    /**
     * 扫描库存
     *
     * @param barCode
     * @param userId
     * @param cfAllotScanRecordList
     * @param cfAllotInventoryList
     * @param cfAllotInfo
     * @throws Exception
     */
    public void scanInventoryCode(String barCode, int userId, List<CfAllotScanRecord> cfAllotScanRecordList, List<CfAllotInventory> cfAllotInventoryList,
                                  CfAllotInfo cfAllotInfo) throws Exception {
        //校验条码是否在扫描表
        for (int i = 0, len = cfAllotScanRecordList.size(); i < len; i++) {
            if (barCode.equals(cfAllotScanRecordList.get(i).getBarcode())) {
                throw new Exception("条码" + barCode + "已扫描");
            }
        }
        //查询库存
        EntityWrapper<CfBarcodeInventory> inventoryWrapper = new EntityWrapper<>();
        CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
        cfBarcodeInventory.setBarcode(barCode);
        inventoryWrapper.setEntity(cfBarcodeInventory);
        cfBarcodeInventory = iCfBarcodeInventoryService.selectOne(inventoryWrapper);
        if (cfBarcodeInventory == null) {
            throw new Exception("条码不存在，请注意！");
        }
        //库存对应的调拨清单物料
        CfAllotInventory cfAllotInventory = null;
        //遍历调拨清单查询库存对应的物料
        for (int i = 0, len = cfAllotInventoryList.size(); i < len; i++) {
            if (cfBarcodeInventory.getMaterialsNo() != null &&
                    StrUtil.equals(cfBarcodeInventory.getMaterialsNo(), cfAllotInventoryList.get(i).getMaterialsNo())) {
                cfAllotInventory = cfAllotInventoryList.get(i);
                break;
            }
        }
        if (cfAllotInventory == null) {
            throw new Exception("库存对应的该物料" + cfBarcodeInventory.getMaterialsNo() + "不在汇总清单，请注意！");
        }
        //CP/EG校验
        if (StrUtil.equalsIgnoreCase(cfBarcodeInventory.getBarcodeType(), "CP") || StrUtil.equalsIgnoreCase(cfBarcodeInventory.getBarcodeType(), "EG")) {

            BigDecimal qty = cfBarcodeInventory.getBarCodeNumber();
            if (qty != null && qty.doubleValue() > 0) {
                throw new Exception("条码已经入库，请注意！");
                //校验是否存在调拨在途表中对应调拨单的行数据
            } else if (qty != null && qty.intValue() == 0) {
                EntityWrapper<CfAllotOnWayData> onWayWrapper = new EntityWrapper<>();
                CfAllotOnWayData onWayDataEntity = new CfAllotOnWayData();
                onWayDataEntity.setBarcode(barCode);
                onWayWrapper.setEntity(onWayDataEntity);
                List<CfAllotOnWayData> cfAllotOnWayDataList = iCfAllotOnWayDataService.selectList(onWayWrapper);
                if (cfAllotOnWayDataList.size() == 0) {
                    throw new Exception("条码不在该调拨单的在途数据，请注意！");
                }
            } else {
                throw new Exception("条码" + barCode + "数量异常");
            }
            //默认将数量修改为1
            cfBarcodeInventory.setBarCodeNumber(BigDecimal.ONE);
        } else { //OT
            String state = cfBarcodeInventory.getState();
            //非不可用
            if (!StrUtil.equalsIgnoreCase(state, "N")) {
                throw new Exception("条码已经入库，请注意！");
            } else { //不可用
                EntityWrapper<CfAllotOnWayData> onWayWrapper = new EntityWrapper<>();
                CfAllotOnWayData onWayDataEntity = new CfAllotOnWayData();
                onWayDataEntity.setMaterialsNo(cfBarcodeInventory.getMaterialsNo());
                onWayDataEntity.setBatchNo(cfBarcodeInventory.getBatchNo());
                onWayWrapper.setEntity(onWayDataEntity);
                List<CfAllotOnWayData> cfAllotOnWayDataList = iCfAllotOnWayDataService.selectList(onWayWrapper);
                if (cfAllotOnWayDataList.size() == 0) {
                    throw new Exception("条码对应的物料及对应批次不在该调拨单的在途数据，请注意！");
                }
            }
            //默认将数量修改为1
            cfBarcodeInventory.setBarCodeNumber(BigDecimal.ONE);
        }
        //应入数量
        Integer shouldInQty = cfAllotInventory.getNumber();
        if (shouldInQty == null) {
            throw new Exception("在途应入数量不能为空");
        }
        //实入数量
        Integer actualInQty = cfAllotInventory.getAllotInScannedNumber();
        if (actualInQty == null) {
            actualInQty = 0;
        }
        if (actualInQty.intValue() >= shouldInQty.intValue()) {
            throw new Exception("该物料已经入库完成，请注意！");
        }
        if (cfBarcodeInventory.getBarCodeNumber().intValue() > (shouldInQty.intValue() - actualInQty.intValue())) {
            throw new Exception("条码异常，条码数量超出待调入数量");
        }
        //更新汇总表数据
        CfAllotInventory sumAllotInventory = new CfAllotInventory();
        sumAllotInventory.setAllotInventoryId(cfAllotInventory.getAllotInventoryId());
        sumAllotInventory.setAllotInScannedNumber(actualInQty.intValue() + cfBarcodeInventory.getBarCodeNumber().intValue());
        sumAllotInventory.setLastUpdatedBy(userId);
        sumAllotInventory.setLastUpdateDate(new Date());
        iCfAllotInventoryService.updateById(sumAllotInventory);
        //保存扫描表数据
        CfAllotScanRecord cfAllotScanRecord = assemblyScanData(cfAllotInfo.getOrderNo(), cfAllotInventory.getSaleOrderNo(), cfBarcodeInventory.getMaterialsNo(),
                cfBarcodeInventory.getMaterialsName(), cfAllotInventory.getSpec(), cfBarcodeInventory.getBarcode(), "", cfBarcodeInventory.getBarcodeType(),
                cfBarcodeInventory.getBatchNo(), cfBarcodeInventory.getBarCodeNumber().intValue(), cfBarcodeInventory.getWarehouse(),
                cfBarcodeInventory.getStorageArea(), cfBarcodeInventory.getWarehousePosition(), cfAllotInventory.getAllotInWarehouse(),
                cfAllotInventory.getAllotInventoryId(), cfAllotInfo.getAllotInfoId(), userId);
        iCfAllotScanRecordService.insert(cfAllotScanRecord);
    }

    /**
     * 扫描物料
     *
     * @param barCode
     * @param userId
     * @param cfAllotInventoryList
     * @param cfAllotInfo
     * @throws Exception
     */
    public void scanItemCode(String barCode, int userId, List<CfAllotInventory> cfAllotInventoryList, CfAllotInfo cfAllotInfo) throws Exception {

        //物料
        if (!barCode.contains("*")) {
            throw new Exception("条码非部品条码，请注意!");
        }
        String[] barCodes = barCode.split("\\*");
        String item = barCodes[0];
        String qtyStr = barCodes[1];
        int qtyInt = 0;
        try {
            qtyInt = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            throw new Exception("条码" + barCode + "*之后非数字");
        }
        if (qtyInt == 0) {
            throw new Exception("条码数量为0，请注意！");
        }
        //库存对应的调拨清单物料
        CfAllotInventory cfAllotInventory = null;
        //遍历调拨清单查询库存对应的物料
        for (int i = 0, len = cfAllotInventoryList.size(); i < len; i++) {
            if (StrUtil.equals(item, cfAllotInventoryList.get(i).getMaterialsNo())) {
                cfAllotInventory = cfAllotInventoryList.get(i);
                break;
            }
        }
        if (cfAllotInventory == null) {
            throw new Exception("条码" + item + "不存在汇总，请注意！");
        }
        //应入数量
        Integer shouldInQty = cfAllotInventory.getNumber();
        if (shouldInQty == null) {
            throw new Exception("在途应入数量不能为空");
        }
        //实入数量
        Integer actualInQty = cfAllotInventory.getAllotInScannedNumber();
        if (actualInQty == null) {
            actualInQty = 0;
        }
        if (actualInQty.intValue() >= shouldInQty.intValue()) {
            throw new Exception("该物料已经调入完成，请注意");
        }
        if (qtyInt > (shouldInQty.intValue() - actualInQty.intValue())) {
            throw new Exception("该物料条码超过待调入数量，请注意");
        }
        //查询单据在途表物料行数据,按批次排序
        EntityWrapper<CfAllotOnWayData> onWayWrapper = new EntityWrapper<>();
        CfAllotOnWayData cfAllotOnWayDataEntity = new CfAllotOnWayData();
        cfAllotOnWayDataEntity.setMaterialsNo(item);
        cfAllotOnWayDataEntity.setOrderNo(cfAllotInfo.getOrderNo());
        onWayWrapper.orderBy("batch_no", true);
        onWayWrapper.setEntity(cfAllotOnWayDataEntity);
        List<CfAllotOnWayData> cfAllotOnWayDataList = iCfAllotOnWayDataService.selectList(onWayWrapper);
        if (cfAllotOnWayDataList.size() == 0) {
            throw new Exception("物料" + item + "无在途数据");
        }

        /**
         * 修改者：叶成翔
         * 修改原因：在途表的数量计算错误，每次都会将数量设置为数量-调入数量，现修改为每条数据只修改一次，即为初始化数量
         */
        //初始化在图标的可用数量：数量= 数量-调入数量
        for (CfAllotOnWayData onWayData : cfAllotOnWayDataList) {
            if (onWayData.getNumber() > onWayData.getAllotInNumber()) {
                onWayData.setNumber(onWayData.getNumber() - onWayData.getAllotInNumber());
            } else {
                throw new Exception("单号" + onWayData.getOrderNo() + "对应的在途表数据调出数量小于调入数量,请注意!!!");
            }
        }

        //查询单据扫描物料对应的行数据
        EntityWrapper<CfAllotScanRecord> scanWrapper = new EntityWrapper<>();
        CfAllotScanRecord cfAllotScanRecordEntity = new CfAllotScanRecord();
        cfAllotScanRecordEntity.setMaterialsNo(item);
        cfAllotScanRecordEntity.setOrderNo(cfAllotInfo.getOrderNo());
        cfAllotScanRecordEntity.setOperateType("02");
        scanWrapper.setEntity(cfAllotScanRecordEntity);
        List<CfAllotScanRecord> cfAllotScanRecordList = iCfAllotScanRecordService.selectList(scanWrapper);
        if (cfAllotScanRecordList != null && cfAllotScanRecordList.size() > 0) {
            //修改在途表数量供后续计算使用
            //扫描
            CfAllotScanRecord cfAllotScanRecord = null;
            //在途
            CfAllotOnWayData cfAllotOnWayData = null;
            for (int i = 0, len = cfAllotScanRecordList.size(); i < len; i++) {
                cfAllotScanRecord = cfAllotScanRecordList.get(i);
                for (int j = 0, jen = cfAllotOnWayDataList.size(); j < jen; j++) {
                    cfAllotOnWayData = cfAllotOnWayDataList.get(j);

                    //扫描表批次和在途表批次对比,更改批次数量为批次剩余数量
                    if (cfAllotScanRecord.getBatchNo() != null && cfAllotScanRecord.getBatchNo().equals(cfAllotOnWayData.getBatchNo())) {
                        //计算剩余数量
                        /*if (cfAllotOnWayData.getAllotInNumber() != null) {
                            //剩余数量
                            cfAllotOnWayData.setNumber(cfAllotOnWayData.getNumber() - cfAllotOnWayData.getAllotInNumber());
                        }*/

                        //剩余数量
                        cfAllotOnWayData.setNumber(cfAllotOnWayData.getNumber() - cfAllotScanRecord.getNumber());
                        if (cfAllotOnWayData.getNumber() < 0) {
                            throw new Exception("单号" + cfAllotInfo.getOrderNo() + "对应批次" + cfAllotOnWayData.getBatchNo() + "数量不足");
                        }
                        break;
                    }
                }
            }


        }
        //匹配对应的在途数据
        List<CfAllotOnWayData> matchOnWayDataList = new ArrayList<>();
        List<Integer> matchOnWayQtyList = new ArrayList<>();
        for (int i = 0, len = cfAllotOnWayDataList.size(); i < len; i++) {
            if (cfAllotOnWayDataList.get(i).getNumber() > 0) {
                matchOnWayDataList.add(cfAllotOnWayDataList.get(i));
                //代表该笔在途数量不足
                if (qtyInt - cfAllotOnWayDataList.get(i).getNumber() > 0) {
                    matchOnWayQtyList.add(cfAllotOnWayDataList.get(i).getNumber());
                    qtyInt = qtyInt - cfAllotOnWayDataList.get(i).getNumber();
                } else {//代表在途数量满足
                    matchOnWayQtyList.add(qtyInt);
                    break;
                }
            }
        }
        //验证在途数量是否满足扫描数量
        int onWayNumber = matchOnWayQtyList.stream().mapToInt(Integer::intValue).sum();
        if (onWayNumber < Integer.parseInt(qtyStr)) {
            throw new Exception("单据" + cfAllotInfo.getOrderNo() + "物料" + item + "在途数量不足");
        }

        //更新汇总数据
        CfAllotInventory sumAllotInventory = new CfAllotInventory();
        sumAllotInventory.setAllotInventoryId(cfAllotInventory.getAllotInventoryId());
        sumAllotInventory.setAllotInScannedNumber(actualInQty.intValue() + Integer.parseInt(qtyStr));
        sumAllotInventory.setLastUpdatedBy(userId);
        sumAllotInventory.setLastUpdateDate(new Date());
        iCfAllotInventoryService.updateById(sumAllotInventory);

        //保存扫描数据
        String packNo = iCfNextNumberService.generateNextNumber("PACK_LIST_NO");
        CfAllotOnWayData saveOnWayData = null; //在途
        EntityWrapper<CfBarcodeInventory> inventoryWrapper = null; //查询
        CfBarcodeInventory inv = null; //库存
        for (int i = 0, len = matchOnWayDataList.size(); i < len; i++) {
            saveOnWayData = matchOnWayDataList.get(i);
            //查询库存数据
           /* inventoryWrapper = new EntityWrapper<>();
            inventoryWrapper.eq( "barcode", saveOnWayData.getBarcode() );
            inv = iCfBarcodeInventoryService.selectOne( inventoryWrapper );
            if( inv==null ){
                throw new Exception( "在途库存"+saveOnWayData.getBarcode()+ "不存在" );
            }*/
            CfAllotScanRecord cfAllotScanRecord = assemblyScanData(cfAllotInfo.getOrderNo(), cfAllotInventory.getSaleOrderNo(), saveOnWayData.getMaterialsNo(),
                    saveOnWayData.getMaterialsName(), saveOnWayData.getSpec(), barCode, packNo, saveOnWayData.getBarcodeType(), saveOnWayData.getBatchNo(),
                    matchOnWayQtyList.get(i), saveOnWayData.getAllotInWarehouse(), "", "", saveOnWayData.getAllotInWarehouse(),
                    cfAllotInventory.getAllotInventoryId(), cfAllotInfo.getAllotInfoId(), userId);
            iCfAllotScanRecordService.insert(cfAllotScanRecord);
        }

    }

    /**
     * 部品装箱功能-修改行
     *
     * @param orderNo 调拨单
     * @param barCode 条码
     * @param qty     数量
     * @param userId  用户
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void changeRowData(String orderNo, String barCode, String qty, int userId) throws Exception {
        //代表是包修改，只能增加
        if (barCode.contains("*")) {
            int qtyInt = 0;
            try {
                qtyInt = Integer.parseInt(qty);
            } catch (NumberFormatException e) {
                throw new Exception("修改数量不合法");
            }
            if (qtyInt <= 1) {
                throw new Exception("修改数量必须大于1");
            }
            EntityWrapper<CfAllotScanRecord> wrapper = new EntityWrapper<>();
            CfAllotScanRecord cfAllotScanRecord = new CfAllotScanRecord();
            cfAllotScanRecord.setOrderNo(orderNo);
            cfAllotScanRecord.setBarcode(barCode);
            cfAllotScanRecord.setCreatedBy(userId);
            wrapper.setEntity(cfAllotScanRecord);
            List<CfAllotScanRecord> cfAllotScanRecordList = iCfAllotScanRecordService.selectList(wrapper);
            if (cfAllotScanRecordList.size() == 0) {
                throw new Exception("条码" + barCode + "无扫描记录，不能修改");
            }
            //查询主表数据
            EntityWrapper<CfAllotInfo> infoWrapper = new EntityWrapper<>();
            infoWrapper.eq("order_no", orderNo);
            CfAllotInfo cfAllotInfo = cfAllotInfoService.selectOne(infoWrapper);
            if (cfAllotInfo == null) {
                throw new Exception("数据已修改，请重新获取单号数据");
            }

            EntityWrapper<CfAllotInventory> inventoryWrapper = new EntityWrapper<>();
            inventoryWrapper.eq("order_no", orderNo);
            //执行物料扫描功能
            for (int i = 0; i < qtyInt - 1; i++) {
                //iCfAllotInventoryService.selectList( inventoryWrapper )每次循环执行该步骤原因是如下方法会修改，该查询结果
                scanItemCode(barCode, userId, iCfAllotInventoryService.selectList(inventoryWrapper), cfAllotInfo);
            }

        } else { //代表是库存修改
            throw new Exception("库存条码数量不能修改");
        }
    }

    /**
     * 部品装箱功能-关箱
     *
     * @param userId                用户
     * @param orderNo               调拨单
     * @param length                长
     * @param wide                  宽
     * @param high                  高
     * @param weight                毛重
     * @param express               快递公司
     * @param wayBillNo             运单号
     * @param businessStreamOrderNo 商流订单
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public CfAllotManagementVo closeCaseNo(int userId, String orderNo, Integer length, Integer wide, Integer high,
                                           String weight, String express, String wayBillNo, String businessStreamOrderNo) throws Exception {

        //查询扫描数据
        EntityWrapper<CfAllotScanRecord> scanWrapper = new EntityWrapper<>();
        scanWrapper.eq("order_no", orderNo);
        scanWrapper.andNew().eq("operate_type", "02");
        scanWrapper.andNew().eq("created_by", userId);
        scanWrapper.andNew().eq("state", "").or().isNull("state");
        List<CfAllotScanRecord> cfAllotScanRecordList = iCfAllotScanRecordService.selectList(scanWrapper);
        if (cfAllotScanRecordList.size() == 0) {
            throw new Exception("无关箱数据");
        }

        //生成箱号
        String caseNo = iCfSaleNextNumberService.generateSaleCaseNoNextNumber(userId, cfAllotScanRecordList.get(0).getSaleOrderNo());

        //组装需要更新的扫描表数据：长 宽 高 毛重 运单号 箱号 快递公司
        List<CfAllotScanRecord> scanList = new ArrayList<>();
        Date updateDate = new Date();
        CfAllotScanRecord cfAllotScanRecord = null;
        for (int i = 0, len = cfAllotScanRecordList.size(); i < len; i++) {
            cfAllotScanRecord = cfAllotScanRecordList.get(i);
            cfAllotScanRecord.setLength(length.doubleValue());
            cfAllotScanRecord.setCaseNo(caseNo);
            cfAllotScanRecord.setWidth(wide.doubleValue());
            cfAllotScanRecord.setHeight(high.doubleValue());
            cfAllotScanRecord.setRoughWeight(Double.parseDouble(weight));
            cfAllotScanRecord.setSendWaybillNo(wayBillNo);
            cfAllotScanRecord.setExpressCompany(express);
            cfAllotScanRecord.setLastUpdatedBy(userId);
            cfAllotScanRecord.setLastUpdateDate(updateDate);
            scanList.add(cfAllotScanRecord);
        }
        //iCfAllotScanRecordService.batchUpdateScan( scanList );

        //更新在途表数据
        CfAllotScanRecord allotScanRecord = null; //扫描对象
        CfAllotOnWayData allotOnWayData = null; //在途对象
        CfAllotOnWayData allotOnWayData2 = null; //在途对象
        CfAllotOnWayData allotOnWayData3 = null; //在途对象
        EntityWrapper<CfAllotOnWayData> wayWrapper = null; //在途
        CfBarcodeInventory cfBarcodeInventory = null; //库存对象
        EntityWrapper<CfBarcodeInventory> inventoryWrapper = null; //库存
        for (int i = 0, len = scanList.size(); i < len; i++) {

            allotScanRecord = scanList.get(i);
            if ("BP".equalsIgnoreCase(allotScanRecord.getBarcodeType()) || "OT".equalsIgnoreCase(allotScanRecord.getBarcodeType())) {
                wayWrapper = new EntityWrapper<>();
                allotOnWayData = new CfAllotOnWayData();
                allotOnWayData.setOrderNo(allotScanRecord.getOrderNo());
                allotOnWayData.setMaterialsNo(allotScanRecord.getMaterialsNo());
                allotOnWayData.setBatchNo(allotScanRecord.getBatchNo());
                wayWrapper.setEntity(allotOnWayData);
                allotOnWayData2 = iCfAllotOnWayDataService.selectOne(wayWrapper); //查询在途数据
                if (allotOnWayData2 == null) {
                    throw new Exception("单号" + allotScanRecord.getOrderNo() + "物料" + allotScanRecord.getMaterialsNo() +
                            "批次" + allotScanRecord.getBatchNo() + "无在途数据");
                }
                //更新在途数据
                if (allotOnWayData2.getNumber() < (allotOnWayData2.getAllotInNumber() == null ? allotScanRecord.getNumber() :
                        allotOnWayData2.getAllotInNumber() + allotScanRecord.getNumber())) {
                    throw new Exception("单号" + allotScanRecord.getOrderNo() + "物料" + allotScanRecord.getMaterialsNo()
                            + "批次" + allotScanRecord.getBatchNo() + "在途数量不足");
                }
            } else { //CP EG
                wayWrapper = new EntityWrapper<>();
                allotOnWayData = new CfAllotOnWayData();
                allotOnWayData.setBarcode(allotScanRecord.getBarcode());
                wayWrapper.setEntity(allotOnWayData);
                allotOnWayData2 = iCfAllotOnWayDataService.selectOne(wayWrapper); //查询在途数据
                if (allotOnWayData2 == null) {
                    throw new Exception("条码" + allotScanRecord.getBarcode() + "无在途数据");
                }

            }
            //更新在途数据，调入数量
            allotOnWayData3 = new CfAllotOnWayData();
            allotOnWayData3.setAllotInNumber(allotOnWayData2.getAllotInNumber() == null ? allotScanRecord.getNumber() :
                    allotOnWayData2.getAllotInNumber() + allotScanRecord.getNumber());
            allotOnWayData3.setAllotOnWayDataId(allotOnWayData2.getAllotOnWayDataId());
            iCfAllotOnWayDataService.updateById(allotOnWayData3);
            //如果调入数量等于在途数量，则删除该笔在途数据
            if (allotOnWayData3.getAllotInNumber().intValue() == allotOnWayData2.getNumber().intValue()) {
                iCfAllotOnWayDataService.deleteById(allotOnWayData3.getAllotOnWayDataId());
            } else {
                //也有可能更新之后调入数量等于在途数量，然后删除数量
                allotOnWayData3 = iCfAllotOnWayDataService.selectById(allotOnWayData3.getAllotOnWayDataId());
                if (allotOnWayData3.getAllotInNumber().intValue() == allotOnWayData3.getNumber().intValue()) {
                    iCfAllotOnWayDataService.deleteById(allotOnWayData3.getAllotOnWayDataId());
                }
            }

            //OT条码状态改为可用、仓库改为调入仓库、存储区和仓位为空，CP/EG/KTM改库存数量为1（KTM在KTM表）、仓库改为调入仓库、存储区和仓位为空，BP条码系统不做处理
            if ("OT".equalsIgnoreCase(allotScanRecord.getBarcodeType())) {//条码状态改为可用、仓库改为调入仓库、存储区和仓位为空
                cfBarcodeInventory = new CfBarcodeInventory();
                cfBarcodeInventory.setBarcode(allotOnWayData2.getBarcode());
                cfBarcodeInventory.setWarehouse(allotOnWayData2.getAllotInWarehouse());
                cfBarcodeInventory.setStorageArea("");
                cfBarcodeInventory.setWarehousePosition("");
                cfBarcodeInventory.setState("");
                inventoryWrapper = new EntityWrapper<CfBarcodeInventory>();
                inventoryWrapper.eq("barcode", allotOnWayData2.getBarcode());
                iCfBarcodeInventoryService.update(cfBarcodeInventory, inventoryWrapper);
            } else if ("CP".equalsIgnoreCase(allotScanRecord.getBarcodeType()) || "EG".equalsIgnoreCase(allotScanRecord.getBarcodeType())
                    || "KTM".equalsIgnoreCase(allotScanRecord.getBarcodeType())) { //改库存数量为1（KTM在KTM表）、仓库改为调入仓库、存储区和仓位为空
                cfBarcodeInventory = new CfBarcodeInventory();
                cfBarcodeInventory.setBarcode(allotScanRecord.getBarcode());
                cfBarcodeInventory.setWarehouse(allotOnWayData2.getAllotInWarehouse());
                cfBarcodeInventory.setBarCodeNumber(new BigDecimal(1));
                cfBarcodeInventory.setStorageArea("");
                cfBarcodeInventory.setWarehousePosition("");
                cfBarcodeInventory.setState("");
                inventoryWrapper = new EntityWrapper<>();
                inventoryWrapper.eq("barcode", allotScanRecord.getBarcode());
                iCfBarcodeInventoryService.update(cfBarcodeInventory, inventoryWrapper);
            }
        }
        //发送ERP数据scanList
        CfAllotManagementVo cfAllotManagementVo = sapApiService.postDataToSapApi09B(scanList);
        cfAllotManagementVo.setCaseNo(caseNo); //设置箱号

        //删除扫描表数据
        List<Integer> scanIdList = new ArrayList<>();
        for (int i = 0, len = scanList.size(); i < len; i++) {
            scanIdList.add(scanList.get(i).getAllotScanRecordId());
        }
        iCfAllotScanRecordService.deleteBatchIds(scanIdList);

        //将调拨扫描记录表中对应账号的对应单号的操作类型为02的未提交的行数据，逐条插入到装箱清单数据表（当箱号和物料代码存在时，累加数量）
        List<CfPackingList> cfPackingListList = new ArrayList<>();
        CfPackingList cfPackingList = null;
        CfAllotScanRecord scanRecord = null;
        for (int i = 0, len = scanList.size(); i < len; i++) {
            cfPackingList = new CfPackingList();
            scanRecord = scanList.get(i);
            cfPackingList.setPackingListId("PackListBO:1000," + scanRecord.getCaseNo() + "," + scanRecord.getMaterialsNo());
            cfPackingList.setOrderNo(scanRecord.getOrderNo());
            cfPackingList.setSaleOrder(scanRecord.getSaleOrderNo());
            cfPackingList.setBusinessOrder(businessStreamOrderNo);
            cfPackingList.setItem(scanRecord.getMaterialsNo());
            cfPackingList.setItemDesc(scanRecord.getMaterialsName());
            cfPackingList.setMode(scanRecord.getSpec());
            cfPackingList.setCaseNo(scanRecord.getCaseNo());
            cfPackingList.setQty(new BigDecimal(scanRecord.getNumber()));
            cfPackingList.setObjectSetBasicAttribute(userId, new Date());
            cfPackingListList.add(cfPackingList);
        }
        cfPackingListMapper.packListInsertOrSaveBatch(cfPackingListList);

        //当订单状态为已完成，删除单号对应的信息表、清单表，提示“调拨单已完成！”。否则，不处理
        if ("已完成".equalsIgnoreCase(cfAllotManagementVo.getOrderStatus())) {
            Map<String, Object> infoMap = new HashMap<String, Object>();
            infoMap.put("order_no", orderNo);
            cfAllotInfoService.deleteByMap(infoMap); //删除主表
            iCfAllotInventoryService.deleteByMap(infoMap); //删除汇总表
            iCfAllotOnWayDataService.deleteByMap(infoMap); //删除在途表
        }
        return cfAllotManagementVo;

    }


    /**
     * 组装扫描数据
     *
     * @param orderNo           调拨单
     * @param saleOrder         销售订单
     * @param materialsNo       物料代码
     * @param materialsName     物料名称
     * @param spec              规格
     * @param barCode           条码
     * @param packNo            包号
     * @param barcodeType       条码类型
     * @param batchNo           批次
     * @param barCodeNumber     数量
     * @param warehouse         仓库
     * @param storageArea       存储区域
     * @param warehousePosition 仓位
     * @param allotInWarehouse  调入仓库
     * @param allotInventoryId
     * @param allotInfoId
     * @param userId            用户
     * @return
     */
    public CfAllotScanRecord assemblyScanData(String orderNo, String saleOrder, String materialsNo, String materialsName, String spec, String barCode,
                                              String packNo, String barcodeType, String batchNo, Integer barCodeNumber, String warehouse, String storageArea,
                                              String warehousePosition, String allotInWarehouse, Integer allotInventoryId, Integer allotInfoId, int userId) {
        //组装扫描数据
        CfAllotScanRecord cfAllotScanRecord = new CfAllotScanRecord();
        cfAllotScanRecord.setOrderNo(orderNo);
        cfAllotScanRecord.setSaleOrderNo(saleOrder);
        cfAllotScanRecord.setOperateType("02");
        cfAllotScanRecord.setMaterialsNo(materialsNo);
        cfAllotScanRecord.setMaterialsName(materialsName);
        cfAllotScanRecord.setSpec(spec);
        cfAllotScanRecord.setBarcode(barCode);
        cfAllotScanRecord.setPackNo(packNo);
        cfAllotScanRecord.setBarcodeType(barcodeType);
        cfAllotScanRecord.setBatchNo(batchNo);
        cfAllotScanRecord.setNumber(barCodeNumber);
        cfAllotScanRecord.setWarehouse(warehouse);
        cfAllotScanRecord.setStorageArea(storageArea);
        cfAllotScanRecord.setWarehousePosition(warehousePosition);
        cfAllotScanRecord.setState("");
        cfAllotScanRecord.setAllotInWarehouse(allotInWarehouse);
        cfAllotScanRecord.setCaseNo(null);
        cfAllotScanRecord.setLength(null);
        cfAllotScanRecord.setWidth(null);
        cfAllotScanRecord.setHeight(null);
        cfAllotScanRecord.setRoughWeight(null);
        cfAllotScanRecord.setSendWaybillNo(null);
        cfAllotScanRecord.setExpressCompany(null);
        cfAllotScanRecord.setAllotInfoId(allotInfoId);
        cfAllotScanRecord.setAllotInventoryId(allotInventoryId);
        cfAllotScanRecord.setObjectSetBasicAttribute(userId, new Date());
        return cfAllotScanRecord;
    }


}
