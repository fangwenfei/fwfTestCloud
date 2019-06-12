package com.cfmoto.bar.code.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.service.*;
import com.cfmoto.bar.code.utiles.BigDecimalUtils;
import com.cfmoto.bar.code.utiles.ValidateUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.StringUtils;
import com.github.pig.common.util.UserUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 成本中心退料（PDA） 前端控制器
 *
 * @author ye
 */
@RestController
@RequestMapping("costCenterWithdraw")
@Api(tags = " 成本中心退料")
public class CfCostCenterWithdrawController {

    private final ICfKtmReceivingOrderService ktmService;

    private final ICfBarcodeInventoryService barcodeInventoryService;

    private final ICfCostCenterPickOrWithdrawInventoryService inventoryService;

    private final ICfCostCenterPickService cfCostCenterPickService;

    private final ICfCostCenterPickOrWithdrawScanRecordService scanRecordService;

    private final ICfCostCenterWithdrawService withdrawService;

    private final ICfCostCenterPickService pickService;

    private final ICfCostCenterPickOrWithdrawInfoService infoService;

    Logger logger = LoggerFactory.getLogger(CfCostCenterPickController.class);

    @Autowired
    public CfCostCenterWithdrawController(ICfKtmReceivingOrderService ktmService, ICfBarcodeInventoryService barcodeInventoryService,
                                          ICfCostCenterPickOrWithdrawInventoryService inventoryService,
                                          ICfCostCenterPickService cfCostCenterPickService,
                                          ICfCostCenterPickOrWithdrawScanRecordService scanRecordService,
                                          ICfCostCenterWithdrawService withdrawService,
                                          ICfCostCenterPickService pickService,
                                          ICfCostCenterPickOrWithdrawInfoService infoService) {
        this.ktmService = ktmService;
        this.barcodeInventoryService = barcodeInventoryService;
        this.inventoryService = inventoryService;
        this.cfCostCenterPickService = cfCostCenterPickService;
        this.scanRecordService = scanRecordService;
        this.withdrawService = withdrawService;
        this.pickService = pickService;
        this.infoService = infoService;
    }


    /**
     * 成本中心退料模块的 扫描条码 接口
     *
     * @author ye
     */
    @GetMapping("scanBarcode")
    @ApiOperation(value = "成本中心退料的扫描条码接口")
    public R<Map<String, Object>> scanBarcode(String barcode, String orderNo, HttpServletRequest request) {
        R<Map<String, Object>> r = new R<>();
        //检验穿过来的参数格式
        if (StrUtil.isBlank(barcode) || StrUtil.isBlank(orderNo)) {
            r.setCode(R.FAIL);
            r.setMsg("请输入有效的条码数据！");
            return r;
        }

        //获取当前登陆的用户id
        int userId = UserUtils.getUserId(request);


        //A:检验条码是否存在

        //根据条码检验是否存在KTM表
        CfKtmReceivingOrder ktm = new CfKtmReceivingOrder();
        ktm.setFrameNo(barcode);
        CfKtmReceivingOrder newKtm = ktmService.selectOne(new EntityWrapper<>(ktm));

        CfBarcodeInventory newBarcodeInventory = new CfBarcodeInventory();

        //存在ktm
        if (newKtm != null) {

            //校验数据是否为0,不为0
            if (!newKtm.getBarCodeNumber().equals(new BigDecimal(0))) {
                r.setCode(R.FAIL);
                r.setMsg("条码已在库，请注意！");
                return r;
            }

        } else {//不存在ktm，根据条码检验是否存在条码库存表

            CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(barcode);

            newBarcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<>(cfBarcodeInventory));


            //判断数据是否为null
            if (newBarcodeInventory != null) {

                //判断条码类型
                switch (newBarcodeInventory.getBarcodeType()) {
                    case "CP":
                    case "EG": {
                        //校验数据是否为0,不为0，返回错误信息
                        if (!newBarcodeInventory.getBarCodeNumber().equals(new BigDecimal(0))) {
                            r.setCode(R.FAIL);
                            r.setMsg("条码已在库，请注意！");
                            return r;
                        }
                        break;
                    }
                    case "OT": {
                        //校验数据打印标签的单据是否与界面的单据相符、状态为不可用，不符报错“单据不符或条码已经在库”；相符，带出相关数据
                        String state = "N";
                        if (!state.equals(newBarcodeInventory.getState()) || !newBarcodeInventory.getProductionTaskOrder().equals(orderNo)) {
                            r.setCode(R.FAIL);
                            r.setMsg("单据不符或条码已经在库");
                            return r;
                        }
                        if (newBarcodeInventory.getBarCodeNumber().equals(new BigDecimal(0))) {
                            r.setCode(R.FAIL);
                            r.setMsg("条码已在库，请注意！");
                            return r;
                        }
                        break;
                    }
                    default: {
                    }
                }


            } else {//不存在
                r.setCode(R.FAIL);
                r.setMsg("条码不存在，请注意！");
                return r;
            }
        }

        String materialsName;
        String materialsNo;
        BigDecimal barcodeNumber;

        if (newKtm != null) {
            materialsName = newKtm.getMaterialsName();
            materialsNo = newKtm.getMaterialsNo();
            barcodeNumber = newKtm.getBarCodeNumber();
        } else {
            materialsName = newBarcodeInventory.getMaterialsName();
            materialsNo = newBarcodeInventory.getMaterialsNo();
            barcodeNumber = newBarcodeInventory.getBarCodeNumber();
        }


        //根据条码、单号、物料号去扫描表中查找是否有已扫描数据
        CfCostCenterPickOrWithdrawScanRecord scanRecord1 = scanRecordService.selectOne(new EntityWrapper<CfCostCenterPickOrWithdrawScanRecord>().eq("order_no", orderNo).eq("barcode", barcode).eq("materials_no", materialsNo));
        if (scanRecord1 != null) {
            r.setErrorAndErrorMsg("扫描失败，该条码已经扫描过！");
            return r;
        }


        //根据领料单号加载成本中心领退料清单表信息
        CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();
        inventory.setOrderNo(orderNo);

        List<CfCostCenterPickOrWithdrawInventory> inventoryList = inventoryService.selectList(new EntityWrapper<>(inventory));

        //B:检验物料是否存在汇总


        boolean flag = false;

        CfCostCenterPickOrWithdrawInventory gatherView = new CfCostCenterPickOrWithdrawInventory();

        for (CfCostCenterPickOrWithdrawInventory temp : inventoryList) {

            if (materialsNo.equals(temp.getMaterialsNo())) {
                flag = true;
                gatherView = temp;
                break;
            }
        }

        //不存在
        if (!flag) {
            r.setCode(R.FAIL);
            r.setMsg("该物料不在汇总清单，请注意！");
            return r;
        }

        //实发数量=应发数量
        if (gatherView.getScannedNumber().equals(gatherView.getShouldPickOrWithdrawNumber())) {
            r.setCode(R.FAIL);
            r.setMsg("该物料已经退料完成，请注意");
            return r;
        } else {//实发数量<应发数量


            CfCostCenterPickOrWithdrawScanRecord scanRecord = new CfCostCenterPickOrWithdrawScanRecord();
            int updatedActualNumber;

            //条码数量<=应发数量-实发数量
            if (barcodeNumber.intValue() <= gatherView.getShouldPickOrWithdrawNumber() - gatherView.getScannedNumber()) {

                scanRecord.setNumber(barcodeNumber.intValue());
                scanRecord.setOrderNo(orderNo);
                //设置单据类型为领料
                scanRecord.setOrderType("P");
                scanRecord.setMaterialsName(materialsName);
                scanRecord.setMaterialsNo(materialsNo);
                scanRecord.setSpec(gatherView.getSpec());
                scanRecord.setBarcode(barcode);
                scanRecord.setBarcodeType(newKtm == null ? newBarcodeInventory.getBarcodeType() : "KTM");
                scanRecord.setBatchNo(newKtm == null ? newBarcodeInventory.getBatchNo() : newKtm.getBatchNo());
                scanRecord.setWarehouse(gatherView.getWithdrawWarehouse());
                scanRecord.setStorageArea(gatherView.getStorageArea());
                scanRecord.setWarehousePosition(newKtm == null ? newBarcodeInventory.getWarehousePosition() : "");
                //状态为空或空字符串则代表可用，即未提交
                scanRecord.setState("");
                scanRecord.setObjectSetBasicAttribute(userId, new Date());
                scanRecord.setCostCenterPickOrWithdrawInfoId(gatherView.getCostCenterPickOrWithdrawInfoId());
                scanRecord.setCostCenterPickOrWithdrawInventoryId(gatherView.getCostCenterPickOrWithdrawInventoryId());


                updatedActualNumber = gatherView.getScannedNumber() + barcodeNumber.intValue();

                gatherView.setScannedNumber(updatedActualNumber);
                gatherView.setObjectSetBasicAttribute(userId, new Date());

                //更新数据（插入记录表和更新清单表）
                cfCostCenterPickService.updateDataAfterScanBarcode(scanRecord, gatherView);


                r.setData(pickService.loadDataFromLocalDataBase(orderNo, userId));


            } else {//如果 条码数量>应发数量-实发数量,报错
                r.setCode(R.FAIL);
                r.setMsg("条码异常，请注意！");
            }

            return r;

        }
    }

    /**
     * 提交成本中心领退料扫描记录表中状态为未提交的数据，以此达到退料
     * 此接口对领料、部品、退料通用
     *
     * @param expressNo 快递单号
     * @param orderNo   订单号
     * @param apiNo     接口号（1：领料 || 2：部品 || 3：退料）
     */
    @GetMapping("submit")
    @ApiOperation(value = "提交扫描记录表中数据，退料")
    public R<Map<String, Object>> submit(String orderNo, @RequestParam(required = false, defaultValue = "") String expressNo, String apiNo, HttpServletRequest request) {

        if (!new ValidateUtils<String>().isNotNull(orderNo, apiNo)) {
            return new R<>(R.FAIL, "请输入有效的数据！！！");
        }

        //获取用户id
        int userId = UserUtils.getUserId(request);

        //发送数据给SAP接口07并接受返回信息
        R<Map<String, Object>> r = withdrawService.commitToSapAndGetReturnData(orderNo, expressNo, apiNo,userId);

        if (r.getCode() == R.FAIL) {
            logger.info("调用SAP接口07出现错误，错误信息为：" + r.getMsg());
            r.setErrorAndErrorMsg("调用SAP接口出现错误,请联系管理员!");
            return r;
        }

        //解析sap接口传回的数据
        JSONObject jsonObject = new JSONObject(r.getData());

        List<Map<String, Object>> dataList = jsonObject.getObject("ET_DATA", List.class);

        //获取sap接口返回的状态
        String sapStatus = jsonObject.getString("EV_STATUS");
        //获取sap接口返回的消息
        String sapMsg = jsonObject.getString("EV_MESSAGE");

        //判断状态是否为0（0表示失败，1表示成功）
        if (sapStatus.equals("0")) {
            r.setErrorAndErrorMsg(sapMsg);
            return r;
        }

        r = new R<>();


        try {

            withdrawService.withdraw(orderNo, userId, apiNo);
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            r.setErrorAndErrorMsg(e.getMessage());
            return r;
        }

        //获取订单状态
        String orderStatus = (String) dataList.get(0).get("KZEAR");

        //判断订单状态（U为进行中 C为已完成）
        //当订单状态为进行中，则重新加载后台清单表；否则，不加载。
        if ("U".equals(orderStatus)) {

            for (int i = 0; i < dataList.size(); i++) {
                CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();
                inventory.setOrderNo((String) dataList.get(i).getOrDefault("RSNUM", ""));

                //获取订单类型
                String orderType = (String) dataList.get(i).getOrDefault("BWART", "");

                inventory.setOrderType(orderType);
                inventory.setMaterialsName((String) dataList.get(i).getOrDefault("MAKTX", ""));
                inventory.setMaterialsNo((String) dataList.get(i).getOrDefault("MATNR", ""));
                inventory.setSpec((String) dataList.get(i).getOrDefault("WRKST", ""));

                inventory.setStorageArea((String) dataList.get(i).getOrDefault("BLATT", ""));

                if ("1".equals(orderType)) {
                    inventory.setWarehouse((String) dataList.get(i).getOrDefault("LGORT", ""));
                } else if ("2".equals(orderType)) {
                    inventory.setWithdrawWarehouse((String) dataList.get(i).getOrDefault("LGORT", ""));
                } else {
                    r.setErrorAndErrorMsg("订单类型错误！！！");
                    return r;
                }

                inventory.setShouldPickOrWithdrawNumber(BigDecimalUtils.numberObjectToInteger(dataList.get(i).getOrDefault("ERFMG", 0)));
                inventory.setScannedNumber(BigDecimalUtils.numberObjectToBigDecimal(dataList.get(i).getOrDefault("ERFMG", 0)).subtract(BigDecimalUtils.numberObjectToBigDecimal(dataList.get(i).getOrDefault("ZWQSL", 0))).intValue());
                inventory.setSpStorePositionNo((String) dataList.get(i).getOrDefault("ZSPCWH", ""));

                CfCostCenterPickOrWithdrawInfo info = infoService.selectOne(new EntityWrapper<CfCostCenterPickOrWithdrawInfo>().eq("order_no", orderNo));

                if (info == null) {
                    r.setErrorAndErrorMsg("未找到单据头数据!!!");
                    return r;
                }

                inventory.setCostCenterPickOrWithdrawInfoId(info.getCostCenterPickOrWithdrawInfoId());

                inventory.setObjectSetBasicAttribute(userId, new Date());

                inventoryService.insert(inventory);
            }

        } else {
            //订单状态为已完成，则删除信息表
            infoService.delete(new EntityWrapper<CfCostCenterPickOrWithdrawInfo>().eq("order_no", orderNo));
        }

        Map<String, Object> dataMap = pickService.loadDataFromLocalDataBase(orderNo, userId);

        r.setData(dataMap);

        return r;
    }

}
