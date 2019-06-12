package com.cfmoto.bar.code.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.entity.CfKtmReceivingOrder;
import com.cfmoto.bar.code.model.entity.CfOrderScanTemp;
import com.cfmoto.bar.code.model.entity.CfOrderSumTemp;
import com.cfmoto.bar.code.model.entity.CfOrderTemp;
import com.cfmoto.bar.code.model.vo.*;
import com.cfmoto.bar.code.service.*;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.github.pig.common.util.StringUtils;
import com.github.pig.common.vo.UserVO;
import com.xiaoleilu.hutool.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CfOrderManageServiceImpl implements ICfOrderManageService {

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private ICfOrderTempService iCfOrderTempService;

    @Autowired
    private ICfOrderSumTempService iCfOrderSumTempService;

    @Autowired
    private ICfOrderScanTempService iCfOrderScanTempService;

    @Autowired
    private ICfBarcodeInventoryService iCfBarcodeInventoryService;

    @Autowired
    private ICfKtmReceivingOrderService iCfKtmReceivingOrderService;

    @Autowired
    private ICfNextNumberService iCfNextNumberService;

    @Autowired
    private UserFeignService userFeignService;


    //订单数据数据组装
    public List<PurchaseOrOutsourceOrderVo> assembleData(List<Map<String, Object>> etDataList) {

        //EBELN===采购订单 BSART===订单类型 LIFNR===供应商代码 NAME1===供应商描述 EBELP===行项目号 MATNR===物料代码 MAKTX===物料名称 MENGE===订单数量
        // LGFSB===默认仓库 WEMNG===未清数量 FERTH===规格型号 EKGRP===采购组 BEDNR===用途 AEDAT===制单日期 STATU===订单状态
        List<PurchaseOrOutsourceOrderVo> purchaseOrOutsourceOrderVoList = new ArrayList<>();
        PurchaseOrOutsourceOrderVo purchaseOrOutsourceOrderVo;
        Map<String, Object> jcoDataMap;
        for (int i = 0, len = etDataList.size(); i < len; i++) {
            jcoDataMap = etDataList.get(i);
            purchaseOrOutsourceOrderVo = new PurchaseOrOutsourceOrderVo();
            purchaseOrOutsourceOrderVo.setOrderNo((String) jcoDataMap.get("EBELN"));
            purchaseOrOutsourceOrderVo.setOrderType((String) jcoDataMap.get("BSART"));
            purchaseOrOutsourceOrderVo.setItem((String) jcoDataMap.get("MATNR"));
            purchaseOrOutsourceOrderVo.setItemDesc((String) jcoDataMap.get("MAKTX"));
            purchaseOrOutsourceOrderVo.setRowItem((String) jcoDataMap.get("EBELP"));
            purchaseOrOutsourceOrderVo.setVendor((String) jcoDataMap.get("LIFNR"));
            purchaseOrOutsourceOrderVo.setVendorDesc((String) jcoDataMap.get("NAME1"));
            purchaseOrOutsourceOrderVo.setItemPurpose((String) jcoDataMap.get("BEDNR"));
            purchaseOrOutsourceOrderVo.setOrderDate(DateUtil.parse((String) jcoDataMap.get("AEDAT"), "yyyy-MM-dd"));
            purchaseOrOutsourceOrderVo.setQty(new BigDecimal((String) jcoDataMap.get("MENGE")));
            purchaseOrOutsourceOrderVo.setMode((String) jcoDataMap.get("FERTH"));
            purchaseOrOutsourceOrderVo.setPurchaseGroup((String) jcoDataMap.get("EKGRP"));
            purchaseOrOutsourceOrderVo.setStatus((String) jcoDataMap.get("STATU"));
            purchaseOrOutsourceOrderVo.setDemandQty(new BigDecimal((String) jcoDataMap.get("WEMNG")));
            purchaseOrOutsourceOrderVo.setDefaultStorageLocation((String) jcoDataMap.get("LGFSB"));
            purchaseOrOutsourceOrderVoList.add(purchaseOrOutsourceOrderVo);
        }
        return purchaseOrOutsourceOrderVoList;
    }


    /**
     * 是否标准订单
     *
     * @param orderType
     * @return
     */
    public boolean isStandOrder(String orderType) {
        // Z001==生产物料采购订单 Z002==非生产物资采购订单 Z003==固定资产采购订单 Z005==寄售采购订单
        //Z006==费用类采购订单 Z008==广宣品采购订单 Z009==后市场用品采购订单 Z011==模具费用分摊采购订单 Z016==研发试制费采购订单
        Map<String, Object> standMap = new HashMap<>();
        standMap.put("Z001", "Z001");
        standMap.put("Z002", "Z002");
        standMap.put("Z003", "Z003");
        standMap.put("Z005", "Z005");
        standMap.put("Z006", "Z006");
        standMap.put("Z008", "Z008");
        standMap.put("Z009", "Z009");
        standMap.put("Z011", "Z011");
        standMap.put("Z016", "Z016");
        return standMap.containsKey(orderType);
    }

    /**
     * 获取采购或委外订单数据
     *
     * @param orderNo
     * @return
     * @throws Exception
     */
    public R getOrderData(String orderNo) throws Exception {

        Map<String, Object> callParamMap = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("IV_EBELN", orderNo);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_002");
        callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);
        return sapFeignService.executeJcoFunction(callParamMap);

    }

    /**
     * 委外收货、采购收货（零部件）、KTM
     *
     * @param orderReceiveVoList
     * @return
     */
    public R sendOrderData(List<OrderReceiveVo> orderReceiveVoList) throws Exception {

        //EBELN==采购订单 EBELP==行项目 MATNR==物料代码 ERFMG==收货数量 LGORT==仓库 ABLAD==待检区域 SGTXT==状态 CHARG==批次 LICHA==加工贸易手册号
        //发送SAP数据
        Map<String, Object> callParamMap = new HashMap<>();
        List<Map<String, Object>> tableList = new ArrayList<>();
        Map<String, Object> paramMap;
        for (int i = 0, len = orderReceiveVoList.size(); i < len; i++) {
            paramMap = new HashMap<>();
            paramMap.put("EBELN", orderReceiveVoList.get(i).getOrderNo());
            paramMap.put("EBELP", orderReceiveVoList.get(i).getRowItem());
            paramMap.put("MATNR", orderReceiveVoList.get(i).getItem());
            paramMap.put("ERFMG", orderReceiveVoList.get(i).getQty());
            paramMap.put("LGORT", orderReceiveVoList.get(i).getStorageLocation());
            paramMap.put("ABLAD", orderReceiveVoList.get(i).getInspectArea());
            paramMap.put("SGTXT", orderReceiveVoList.get(i).getStatus());
            paramMap.put("CHARG", orderReceiveVoList.get(i).getBatchNo());
            paramMap.put("LICHA", orderReceiveVoList.get(i).getHandbook());
            tableList.add(paramMap);
        }
        Map<String, Object> pMap = new HashMap<>();
        pMap.put("IT_DATA", tableList);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_003");
        callParamMap.put(HandleRefConstants.PARAM_MAP, pMap);
        return sapFeignService.executeJcoFunction(callParamMap);

    }

    /**
     * 获取委外采购订单数据
     *
     * @param userId           用户
     * @param orderNo          订单
     * @param requireOrderType 需要的订单类型
     * @return
     * @throws Exception
     */
    @Override
    public List<PurchaseOrOutsourceOrderVo> getOutsourcePurchaseOrder(int userId, String orderNo, String requireOrderType) throws Exception {

        R returnR = getOrderData(orderNo); //获取委外采购订单数据
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }
        List<Map<String, Object>> etDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
        if (etDataList.size() == 0) {
            throw new Exception("委外采购订单不存在");
        }
        if (!StrUtil.equalsIgnoreCase((String) etDataList.get(0).get("BSART"), requireOrderType)) {
            throw new Exception("该订单不是委外采购订单，请注意！");
        }
        return assembleData(etDataList);

    }


    /**
     * 采购收货（零部件）采购订单数据
     *
     * @param userId
     * @param orderNo
     * @param requireOrderType
     * @return
     */
    @Override
    public List<PurchaseOrOutsourceOrderVo> getPartsPurchaseOrder(int userId, String orderNo, String requireOrderType) throws Exception {

        R returnR = getOrderData(orderNo); //获取采购订单数据
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }
        List<Map<String, Object>> etDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
        if (etDataList.size() == 0) {
            throw new Exception("采购订单不存在");
        }
        //判断是否标准订单
        if (!isStandOrder((String) etDataList.get(0).get("BSART"))) {
            throw new Exception("该订单不是标准采购订单，请注意！");
        }
        return assembleData(etDataList);

    }


    /**
     * 采购收货（KTM整车） 获取订单数据
     *
     * @param userId
     * @param orderNo
     * @param requireOrderType
     * @return
     * @throws Exception
     */
    @Override
    public List<PurchaseOrOutsourceOrderVo> getKtmPurchaseOrder(int userId, String orderNo, String requireOrderType) throws Exception {

        R returnR = getOrderData(orderNo); //获取KTM采购订单数据
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }
        List<Map<String, Object>> etDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
        if (etDataList.size() == 0) {
            throw new Exception("KTM采购订单不存在");
        }
        //判断是否标准订单
        if (!StrUtil.equalsIgnoreCase((String) etDataList.get(0).get("BSART"), requireOrderType)) {
            throw new Exception("该订单不是KTM采购订单，请注意！");
        }
        return assembleData(etDataList);

    }


    /**
     * 采购收货（零部件）-收货确认
     *
     * @param userId
     * @param orderReceiveVo
     * @return
     * @throws Exception
     */
    @Override
    public List<PurchaseOrOutsourceOrderVo> purchasePartsReceiveGoods(int userId, OrderReceiveVo orderReceiveVo) throws Exception {

        orderReceiveVo.setBatchNo(iCfNextNumberService.generateNextNumber("BATCH_NO"));
        R returnR = sendOrderData(new ArrayList<OrderReceiveVo>() {
            {
                add(orderReceiveVo);
            }
        }); //发送sap数据
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }
        List<Map<String, Object>> etDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
        return assembleData(etDataList);
    }


    /**
     * 采购收货（KTM整车）-收货确认
     *
     * @param userId
     * @param ktmOrderVo
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PurchaseOrOutsourceOrderVo> ktmPurchasePartsReceiveGoods(int userId, KtmOrderVo ktmOrderVo) throws Exception {

        String barcode = ktmOrderVo.getBarcode();
        EntityWrapper<CfKtmReceivingOrder> wrapper = new EntityWrapper<>();
        CfKtmReceivingOrder cfktmReceivingOrder = new CfKtmReceivingOrder();
        cfktmReceivingOrder.setFrameNo(barcode);
        wrapper.setEntity(cfktmReceivingOrder);
        List<CfKtmReceivingOrder> cfKtmReceivingOrderList = iCfKtmReceivingOrderService.selectList(wrapper);
        if (cfKtmReceivingOrderList.size() > 0) {
            throw new Exception("条码" + barcode + "有入库记录，请注意！");
        }

        //查询sap车辆条码数据
        Map<String, Object> callParamMap = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("IV_ZCJHM", barcode);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_017");
        callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);
        R returnR = sapFeignService.executeJcoFunction(callParamMap);
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }
        List<Map<String, Object>> retDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
        if (retDataList.size() == 0) {
            throw new Exception("车辆条码" + barcode + "不存在");
        }
        Map<String, Object> rMap = retDataList.get(0);
        if (!ktmOrderVo.getItem().equals(rMap.get("MATNR"))) {
            throw new Exception("条码对应的物料代码与锁定的物料代码不一致，请注意！");
        }
        if (!ktmOrderVo.getOrderNo().equals(rMap.get("EBELN"))) {
            throw new Exception("条码对应的采购订单不一致，请注意！");
        }

        Date date = new Date();
        //保存KTM数据
        CfKtmReceivingOrder cfKtmReceivingOrderModel = new CfKtmReceivingOrder();
        cfKtmReceivingOrderModel.setPurchaseOrderNo(ktmOrderVo.getOrderNo());
        cfKtmReceivingOrderModel.setMaterialsNo(ktmOrderVo.getItem());
        cfKtmReceivingOrderModel.setMaterialsName(ktmOrderVo.getItemDesc());
        cfKtmReceivingOrderModel.setFrameNo(ktmOrderVo.getBarcode());
        cfKtmReceivingOrderModel.setBatchNo(iCfNextNumberService.generateNextNumber("BATCH_NO"));
        cfKtmReceivingOrderModel.setBarCodeNumber(new BigDecimal(1));
        cfKtmReceivingOrderModel.setRepository(ktmOrderVo.getStorageLocation());
        cfKtmReceivingOrderModel.setKtmReceivingDate(date);
        cfKtmReceivingOrderModel.setObjectVersionNumber(1);
        cfKtmReceivingOrderModel.setObjectSetBasicAttribute(userId, date);
        iCfKtmReceivingOrderService.insert(cfKtmReceivingOrderModel);


        OrderReceiveVo orderReceiveVo = new OrderReceiveVo();
        orderReceiveVo.setOrderNo(ktmOrderVo.getOrderNo());
        orderReceiveVo.setBatchNo(cfKtmReceivingOrderModel.getBatchNo());
        orderReceiveVo.setRowItem(ktmOrderVo.getRowItem());
        orderReceiveVo.setHandbook("");
        orderReceiveVo.setInspectArea(ktmOrderVo.getReceiveArea());
        orderReceiveVo.setItem(ktmOrderVo.getItem());
        orderReceiveVo.setQty(cfKtmReceivingOrderModel.getBarCodeNumber().toString());
        orderReceiveVo.setStatus("");
        orderReceiveVo.setStorageLocation(ktmOrderVo.getStorageLocation());
        R rr = sendOrderData(new ArrayList<OrderReceiveVo>() {
            {
                add(orderReceiveVo);
            }
        }); //发送sap数据
        if (rr.getCode() != 0) {
            throw new Exception(rr.getMsg());
        }
        Map<String, Object> jcoResultMap = (Map<String, Object>) rr.getData();
        Integer coResultStatus = (Integer) jcoResultMap.get("EV_STATUS");
        if (coResultStatus == 0) {
            throw new Exception((String) jcoResultMap.get("EV_MESSAGE"));
        }
        List<Map<String, Object>> etDataList = (List<Map<String, Object>>) jcoResultMap.get("ET_DATA");
        return assembleData(etDataList);
    }

    /**
     * 委外采购订单收货确认
     *
     * @param userId
     * @param orderReceiveVo
     * @return
     * @throws Exception
     */
    @Override
    public List<PurchaseOrOutsourceOrderVo> outsourceReceiveGoods(int userId, OrderReceiveVo orderReceiveVo) throws Exception {

        orderReceiveVo.setBatchNo(iCfNextNumberService.generateNextNumber("BATCH_NO"));
        R returnR = sendOrderData(new ArrayList<OrderReceiveVo>() {
            {
                add(orderReceiveVo);
            }
        }); //发送sap数据
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }
        List<Map<String, Object>> etDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
        return assembleData(etDataList);

    }


    /**
     * 采购退货-提交
     *
     * @param userId
     * @param orderNo
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderFullVo purchaseSendOutGoods(int userId, String orderNo) throws Exception {

        UserVO user = userFeignService.user(userId);
        String orderTempId = StringUtils.genHandle(HandleRefConstants.ORDER_ID, user.getSite(), orderNo);
        CfOrderTemp cfOrderTemp = iCfOrderTempService.selectById(orderTempId);
        if (cfOrderTemp == null) { //代表可能其他人已经提退货订单，数据被清空，所以得重新加载数据
            cfOrderTemp = iCfOrderTempService.outSourceOrderData(userId, orderNo);
        }

        //获取扫描数据
        EntityWrapper<CfOrderScanTemp> scanTempWrapper = new EntityWrapper<>();
        CfOrderScanTemp orderScanTemp = new CfOrderScanTemp();
        orderScanTemp.setOrderTempIdRef(orderTempId);
        orderScanTemp.setCreatedBy(userId);
        scanTempWrapper.setEntity(orderScanTemp);
        List<CfOrderScanTemp> cfOrderScanTempList = iCfOrderScanTempService.selectList(scanTempWrapper);
        if (cfOrderScanTempList.size() > 0) { //扫描表有数据

            List<OrderReceiveVo> callList = new ArrayList<>();
            OrderReceiveVo orderReceiveVo;

            CfOrderScanTemp scanTempModel;
            //计算条码需要扣除库存数量
            Map<String, BigDecimal> barcodeQtyMap = new HashMap<>();
            for (int i = 0, len = cfOrderScanTempList.size(); i < len; i++) {
                scanTempModel = cfOrderScanTempList.get(i);
                orderReceiveVo = new OrderReceiveVo();
                orderReceiveVo.setOrderNo(cfOrderTemp.getOrderNo());
                orderReceiveVo.setRowItem(scanTempModel.getRowItem());
                orderReceiveVo.setItem(scanTempModel.getItem());
                orderReceiveVo.setQty(scanTempModel.getQuantity().toString());
                orderReceiveVo.setStorageLocation(scanTempModel.getStorageLocation());
                orderReceiveVo.setInspectArea("");
                orderReceiveVo.setStatus("");
                orderReceiveVo.setBatchNo(scanTempModel.getBatchNo());
                orderReceiveVo.setHandbook("");
                callList.add(orderReceiveVo);
                if (barcodeQtyMap.containsKey(scanTempModel.getBarcode())) {
                    barcodeQtyMap.put(scanTempModel.getBarcode(), barcodeQtyMap.get(scanTempModel.getBarcode())
                            .add(scanTempModel.getQuantity()));
                } else {
                    barcodeQtyMap.put(scanTempModel.getBarcode(), scanTempModel.getQuantity());
                }
            }
            //删除扫描表数据
            iCfOrderScanTempService.delete(scanTempWrapper);

            //删除订单临时表数据
            CfOrderTemp cfOrderTempModel = new CfOrderTemp();
            cfOrderTempModel.setOrderTempId(cfOrderTemp.getOrderTempId());
            cfOrderTempModel.setLastUpdateDate(cfOrderTemp.getLastUpdateDate());
            Integer delInt = iCfOrderTempService.deleteByCfOrderTemp(cfOrderTempModel);
            if (delInt == 0) {
                throw new Exception("数据已修改，请重新获取数据");
            }

            //删除汇总表数据
            EntityWrapper<CfOrderSumTemp> orderSumEntity = new EntityWrapper<>();
            CfOrderSumTemp cfOrderSumTemp = new CfOrderSumTemp();
            cfOrderSumTemp.setOrderTempIdRef(orderTempId);
            orderSumEntity.setEntity(cfOrderSumTemp);
            iCfOrderSumTempService.delete(orderSumEntity);

            //更新条码库存数量
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> iMap;
            for (String barcodeKey : barcodeQtyMap.keySet()) {
                iMap = new HashMap<>();
                iMap.put("userId", userId);
                iMap.put("barcode", barcodeKey);
                iMap.put("qty", barcodeQtyMap.get(barcodeKey));
                iMap.put("lastUpdateDate", new Date());
                mapList.add(iMap);
            }
            iCfBarcodeInventoryService.reduceInventoryQtyByBarcodeList(mapList);

            //发送SAP数据
            R rr = sendOrderData(callList);
            Map<String, Object> returnMap = (Map<String, Object>) rr.getData();
            if (rr.getCode() == 1) {
                throw new Exception(rr.getMsg());
            }
            Integer evStatus = (Integer) returnMap.get("EV_STATUS");
            if (evStatus == 0) {
                throw new Exception((String) returnMap.get("EV_MESSAGE"));
            }
            List<Map<String, Object>> reEtDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
            if (reEtDataList.size() == 0) { //ERP处理成功、但是返回清单无数据，返回null，前端必须再次请求单号数据，才能加载页面数据
                return null;
            }
            OrderFullVo orderFullVo = iCfOrderTempService.saveOrderData(userId, orderTempId, reEtDataList);
            return orderFullVo;

        } else {
            throw new Exception("无数据可提交");
        }

    }


    /**
     * 委外出库-提交
     *
     * @param userId
     * @param orderNo
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outsourceSendOutGoods(int userId, String orderNo) throws Exception {
        UserVO user = userFeignService.user(userId);
        String orderTempId = StringUtils.genHandle(HandleRefConstants.ORDER_ID, user.getSite(), orderNo);
        CfOrderTemp cfOrderTemp = iCfOrderTempService.selectById(orderTempId);
        if (cfOrderTemp == null) { //代表可能其他人已经提委外出库单，数据被清空，所以得重新加载数据
            cfOrderTemp = iCfOrderTempService.getOutSourceOutOrderData(userId, orderNo);
        }

        //获取扫描数据
        EntityWrapper<CfOrderScanTemp> scanTempWrapper = new EntityWrapper<>();
        CfOrderScanTemp orderScanTemp = new CfOrderScanTemp();
        orderScanTemp.setOrderTempIdRef(orderTempId);
        orderScanTemp.setCreatedBy(userId);
        scanTempWrapper.setEntity(orderScanTemp);
        List<CfOrderScanTemp> cfOrderScanTempList = iCfOrderScanTempService.selectList(scanTempWrapper);
        if (cfOrderScanTempList.size() > 0) { //扫描表有数据

            List<OutSourceOutVo> callList = new ArrayList<>();
            OutSourceOutVo outSourceOutVo;

            CfOrderScanTemp scanTempModel;
            //计算条码需要扣除库存数量
            Map<String, BigDecimal> barcodeQtyMap = new HashMap<>();
            for (int i = 0, len = cfOrderScanTempList.size(); i < len; i++) {
                scanTempModel = cfOrderScanTempList.get(i);
                outSourceOutVo = new OutSourceOutVo();
                outSourceOutVo.setOrderNo(cfOrderTemp.getOrderNo());
                outSourceOutVo.setRowItem(scanTempModel.getRowItem());
                outSourceOutVo.setItem(scanTempModel.getItem());
                outSourceOutVo.setQty(scanTempModel.getQuantity().toString());
                outSourceOutVo.setStorageLocation(scanTempModel.getStorageLocation() == null ? "" : scanTempModel.getStorageLocation());
                outSourceOutVo.setStorageLocationArea(scanTempModel.getStorageArea() == null ? "" : scanTempModel.getStorageArea());
                outSourceOutVo.setStoragePosition(scanTempModel.getStoragePosition() == null ? "" : scanTempModel.getStoragePosition());
                outSourceOutVo.setBatchNo(scanTempModel.getBatchNo());
                callList.add(outSourceOutVo);
                if (barcodeQtyMap.containsKey(scanTempModel.getBarcode())) {
                    barcodeQtyMap.put(scanTempModel.getBarcode(), barcodeQtyMap.get(scanTempModel.getBarcode())
                            .add(scanTempModel.getQuantity()));
                } else {
                    barcodeQtyMap.put(scanTempModel.getBarcode(), scanTempModel.getQuantity());
                }
            }
            //删除扫描表数据
            iCfOrderScanTempService.delete(scanTempWrapper);

            //删除订单临时表数据
            CfOrderTemp cfOrderTempModel = new CfOrderTemp();
            cfOrderTempModel.setOrderTempId(cfOrderTemp.getOrderTempId());
            cfOrderTempModel.setLastUpdateDate(cfOrderTemp.getLastUpdateDate());
            Integer delInt = iCfOrderTempService.deleteByCfOrderTemp(cfOrderTempModel);
            if (delInt == 0) {
                throw new Exception("数据已修改，请重新获取数据");
            }

            //删除汇总表数据
            EntityWrapper<CfOrderSumTemp> orderSumEntity = new EntityWrapper<>();
            CfOrderSumTemp cfOrderSumTemp = new CfOrderSumTemp();
            cfOrderSumTemp.setOrderTempIdRef(orderTempId);
            orderSumEntity.setEntity(cfOrderSumTemp);
            iCfOrderSumTempService.delete(orderSumEntity);

            //更新条码库存数量
            for (String barcodeKey : barcodeQtyMap.keySet()) {

                iCfBarcodeInventoryService.reduceInventoryQtyByBarcode(userId, barcodeKey, barcodeQtyMap.get(barcodeKey));
            }

            //发送SAP数据
            R rr = sendOutSourceOrderData(callList);
            Map<String, Object> returnMap = (Map<String, Object>) rr.getData();
            Integer evStatus = (Integer) returnMap.get("EV_STATUS");
            if (evStatus == 0) {
                throw new Exception((String) returnMap.get("EV_MESSAGE"));
            }

        } else {
            throw new Exception("无数据可提交");
        }

    }


    //委外出库数据提交
    public R sendOutSourceOrderData(List<OutSourceOutVo> outSourceVoList) throws Exception {

        //EBELN==采购订单 RSPOS==行项目 MATNR==物料代码 CHARG==批次 ERFMG==数量 LGORT==仓库 VLTYP==存储区域 VLPLA==仓位
        //发送SAP数据
        Map<String, Object> callParamMap = new HashMap<>();
        List<Map<String, Object>> tableList = new ArrayList<>();
        Map<String, Object> paramMap;
        for (int i = 0, len = outSourceVoList.size(); i < len; i++) {
            paramMap = new HashMap<>();
            paramMap.put("EBELN", outSourceVoList.get(i).getOrderNo());
            paramMap.put("RSPOS", outSourceVoList.get(i).getRowItem());
            paramMap.put("MATNR", outSourceVoList.get(i).getItem());
            paramMap.put("CHARG", outSourceVoList.get(i).getBatchNo());
            paramMap.put("ERFMG", outSourceVoList.get(i).getQty());
            paramMap.put("LGORT", outSourceVoList.get(i).getStorageLocation());
            paramMap.put("VLTYP", outSourceVoList.get(i).getStorageLocationArea());
            paramMap.put("VLPLA", outSourceVoList.get(i).getStoragePosition());
            tableList.add(paramMap);
        }
        Map<String, Object> pMap = new HashMap<>();
        pMap.put("IT_DATA", tableList);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_022");
        callParamMap.put(HandleRefConstants.PARAM_MAP, pMap);
        return sapFeignService.executeJcoFunction(callParamMap);

    }

}
