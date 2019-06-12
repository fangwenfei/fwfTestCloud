package com.cfmoto.bar.code.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.service.*;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成本中心领料 前端控制器
 *
 * @author ye
 */
@RestController
@RequestMapping("costCenterPick")
@Api(tags = " 成本中心领料")
public class CfCostCenterPickController {

    @Autowired
    private ICfCostCenterPickOrWithdrawInfoService cfCostCenterPickOrWithdrawInfoService;

    @Autowired
    private ICfCostCenterPickService pickService;

    @Autowired
    private ICfKtmReceivingOrderService ktmService;

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;

    @Autowired
    private ICfCostCenterPickOrWithdrawInventoryService inventoryService;

    @Autowired
    private ICfCostCenterPickService cfCostCenterPickService;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private ICfCostCenterPickOrWithdrawScanRecordService scanRecordService;

    private Logger logger = LoggerFactory.getLogger(CfCostCenterPickController.class);


    /**
     * 根据领料单去数据库查询数据，如有结果则返回相关信息，无则调用SAP接口获取信息返回并插入到数据库
     * 此接口对领料、部品、退料通用
     *
     * @param orderNo
     * @param rightType 订单类型（1：出库 2：入库）
     * @return map
     * @author ye
     */
    @GetMapping("loadDataByOrderNo")
    @ApiOperation("根据领退料单调用SAP接口或从本地数据库拉取数据")
    public R<Map<String, Object>> loadDataByOrderNo(String orderNo, HttpServletRequest request, String rightType) {

        if (StrUtil.isBlank(orderNo)) {
            return new R<>(R.FAIL, "请输入有效的数据！！！");
        }

        int userId = UserUtils.getUserId(request);
        //从数据库加载信息表和清单表数据
        CfCostCenterPickOrWithdrawInfo dataBaseInfo = cfCostCenterPickOrWithdrawInfoService.getByOrderNo(orderNo);
        List<CfCostCenterPickOrWithdrawInventory> dataBaseInventoryList = inventoryService.selectList(new EntityWrapper<CfCostCenterPickOrWithdrawInventory>().eq("order_no", orderNo));


        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("functionName", "ZMM_BC_008");

        Map<String, Object> dataMap = new HashMap<>(3);
        dataMap.put("IV_RSNUM", orderNo);

        paramMap.put("paramMap", dataMap);

        //通过feign调用sap微服务从而访问到sap接口
        R<Map<String, Object>> r = sapFeignService.executeJcoFunction(paramMap);

        //判断结果是否正确
        if (r.getCode() == R.FAIL) {
            return r;
        }

        //调用sap接口成功，获取sap返回的信息

        Map<String, Object> data = r.getData();

        JSONObject jsonObject = new JSONObject(data);

        //获取sap接口返回的状态并判断
        String sapStatus = jsonObject.getString("EV_STATUS");

        //sap接口返回的状态码为1，即错误码
        if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
            return new R<>(R.FAIL, jsonObject.getString("EV_MESSAGE"));
        }

        JSONObject etData = jsonObject.getJSONArray("ET_DATA").getJSONObject(0);


        //判断订单类型,如果不为1，则报错
        String bwart = etData.getString("BWART");

        if (!bwart.equals(rightType)) {
            return new R<>(R.FAIL, "打开功能界面错误，请注意！");
        }

        String orderStatus = etData.getString("KZEAR");
        //判断订单状态 U未完成，C为已完成
        if ("C".equals(orderStatus)) {
            return new R<>(R.FAIL, "该单据已完成，请注意！");
        }


        //否则，说明sap接口调用正常，开始处理业务逻辑

        Map<String, Object> map = pickService.getDataFromSapAndReturnData(jsonObject, userId);
        CfCostCenterPickOrWithdrawInfo sapInfo = (CfCostCenterPickOrWithdrawInfo) map.get("invoiceHeaderView");
        List<CfCostCenterPickOrWithdrawInventory> sapInventoryList = (List<CfCostCenterPickOrWithdrawInventory>) map.get("gatherView");


        //对比结果有三种情况：
        //    1.从数据库获取的信息表数据为空，则直接将SAP返回的数据插入到数据库
        if (dataBaseInfo == null) {
            //插入信息表和清单表
            try {
                cfCostCenterPickOrWithdrawInfoService.insert(sapInfo);
                for (CfCostCenterPickOrWithdrawInventory cfCostCenterPickOrWithdrawInventory : sapInventoryList) {
                    cfCostCenterPickOrWithdrawInventory.setCostCenterPickOrWithdrawInfoId(sapInfo.getCostCenterPickOrWithdrawInfoId());
                }
                inventoryService.insertBatch(sapInventoryList);
                //从数据库查询数据并返回
                return new R<>(pickService.loadDataFromLocalDataBase(orderNo, userId));
            } catch (Exception e) {
                e.printStackTrace();
                //打印错误日志
                logger.info(e.getMessage());
                return new R<>(R.FAIL, e.getMessage());
            }
        } else if (dataBaseInventoryList.size() == 0) {
            //插入清单表
            try {
                for (CfCostCenterPickOrWithdrawInventory cfCostCenterPickOrWithdrawInventory : sapInventoryList) {
                    cfCostCenterPickOrWithdrawInventory.setCostCenterPickOrWithdrawInfoId(dataBaseInfo.getCostCenterPickOrWithdrawInfoId());
                }
                inventoryService.insertBatch(sapInventoryList);
                //从数据库查询数据并返回
                return new R<>(pickService.loadDataFromLocalDataBase(orderNo, userId));
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
                return new R<>(R.FAIL, e.getMessage());
            }
        }


        //    2.SAP获取的数据与数据库获取的数据一致（条数，每条数据的物料条码和数量都一致），那么则不做操作,查询数据库数据并返回
        //    3.SAP获取的数据与数据库获取的数据不一致：删除数据库中的清单表数据和扫描表数据，然后将SAP获取的数据插入到数据库中

        //不一致
        if (sapInventoryList.size() != dataBaseInventoryList.size()) {
            //删除数据库清单表
            inventoryService.delete(new EntityWrapper<CfCostCenterPickOrWithdrawInventory>().eq("order_no", orderNo));
            //插入sap的清单表数据
            for (CfCostCenterPickOrWithdrawInventory cfCostCenterPickOrWithdrawInventory : sapInventoryList) {
                cfCostCenterPickOrWithdrawInventory.setCostCenterPickOrWithdrawInfoId(dataBaseInfo.getCostCenterPickOrWithdrawInfoId());
            }
            inventoryService.insertBatch(sapInventoryList);
            //比对数据库扫描表数据并修改清单表的数量
            List<CfCostCenterPickOrWithdrawInventory> inventoryList = inventoryService.selectList(new EntityWrapper<CfCostCenterPickOrWithdrawInventory>().eq("order_no", orderNo));
            //比对清单表和扫描表
            for (CfCostCenterPickOrWithdrawInventory inventory : inventoryList) {
                //根据清单表的物料代码和单号找到对应的扫描表数据
                List<CfCostCenterPickOrWithdrawScanRecord> scanRecords = scanRecordService.selectList(new EntityWrapper<CfCostCenterPickOrWithdrawScanRecord>().eq("materials_no", inventory.getMaterialsNo()).eq("order_no", inventory.getOrderNo()));
                for (CfCostCenterPickOrWithdrawScanRecord scanRecord : scanRecords) {
                    inventory.setScannedNumber(inventory.getScannedNumber() + scanRecord.getNumber());
                    inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
                    inventoryService.updateById(inventory);
                }

            }
        } else {

            boolean flag = true;

            //将sap返回的数据和数据库查找到数据逐个匹配，判断是否一致
            for (int i = 0; i < sapInventoryList.size(); i++) {
                if (sapInventoryList.get(i).getMaterialsNo().equals(dataBaseInventoryList.get(i).getMaterialsNo())) {
                    if (sapInventoryList.get(i).getShouldPickOrWithdrawNumber() - dataBaseInventoryList.get(i).getShouldPickOrWithdrawNumber() != 0) {
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
                inventoryService.delete(new EntityWrapper<CfCostCenterPickOrWithdrawInventory>().eq("order_no", orderNo));
                //插入sap的清单表数据
                for (CfCostCenterPickOrWithdrawInventory cfCostCenterPickOrWithdrawInventory : sapInventoryList) {
                    cfCostCenterPickOrWithdrawInventory.setCostCenterPickOrWithdrawInfoId(sapInfo.getCostCenterPickOrWithdrawInfoId());
                }
                inventoryService.insertBatch(sapInventoryList);
                //比对数据库扫描表数据并修改清单表的数量
                List<CfCostCenterPickOrWithdrawInventory> inventoryList = inventoryService.selectList(new EntityWrapper<CfCostCenterPickOrWithdrawInventory>().eq("order_no", orderNo));
                //比对清单表和扫描表
                for (CfCostCenterPickOrWithdrawInventory inventory : inventoryList) {
                    //根据清单表的物料代码和单号找到对应的扫描表数据
                    List<CfCostCenterPickOrWithdrawScanRecord> scanRecords = scanRecordService.selectList(new EntityWrapper<CfCostCenterPickOrWithdrawScanRecord>().eq("materials_no", inventory.getMaterialsNo()).eq("order_no", inventory.getOrderNo()));
                    for (CfCostCenterPickOrWithdrawScanRecord scanRecord : scanRecords) {
                        inventory.setScannedNumber(inventory.getScannedNumber() + scanRecord.getNumber());
                        inventory.setObjectSetBasicAttributeForUpdate(userId, new Date());
                        inventoryService.updateById(inventory);
                    }

                }
            }
        }
        //从数据库查询数据并返回
        return new R<>(pickService.loadDataFromLocalDataBase(orderNo, userId));
    }


    /**
     * 扫描条码，去KTM或条码库存表中查找对应条码，带出相关信息，更新相关数据
     *
     * @param barcode
     */
    @GetMapping("scanBarcode")
    @ApiOperation(value = "扫描条码")
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

        /**
         * A:检验条码是否存在
         */
        //根据条码检验是否存在KTM表
        CfKtmReceivingOrder ktm = new CfKtmReceivingOrder();
        ktm.setFrameNo(barcode);
        CfKtmReceivingOrder newKtm = ktmService.selectOne(new EntityWrapper<>(ktm));

        CfBarcodeInventory newBarcodeInventory = new CfBarcodeInventory();

        //存在ktm
        if (newKtm != null) {

            //校验数据是否为0
            if (newKtm.getBarCodeNumber().equals(new BigDecimal(0))) {
                r.setCode(R.FAIL);
                r.setMsg("条码不在库，请检查！");
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
                        //校验数据是否为0
                        if (newBarcodeInventory.getBarCodeNumber().equals(new BigDecimal(0))) {
                            r.setCode(R.FAIL);
                            r.setMsg("条码不在库，请注意！");
                            return r;
                        }
                        break;
                    }
                    case "OT": {
                        //校验状态是否可用
                        String state = "N";
                        if (state.equals(newBarcodeInventory.getState())) {
                            r.setCode(R.FAIL);
                            r.setMsg("状态为条码不可用，请注意！");
                            return r;
                        }
                        if (newBarcodeInventory.getBarCodeNumber().equals(new BigDecimal(0))) {
                            r.setCode(R.FAIL);
                            r.setMsg("条码不在库，请注意！");
                            return r;
                        }
                        break;
                    }
                    default: {
                        break;
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
        String spec;
        String wareHouse;
        String storageArea;

        if (newKtm != null) {
            materialsName = newKtm.getMaterialsName();
            materialsNo = newKtm.getMaterialsNo();
            barcodeNumber = newKtm.getBarCodeNumber();
            spec = "";
            storageArea = "";
            wareHouse = newKtm.getRepository();
        } else {
            materialsName = newBarcodeInventory.getMaterialsName();
            materialsNo = newBarcodeInventory.getMaterialsNo();
            barcodeNumber = newBarcodeInventory.getBarCodeNumber();
            spec = newBarcodeInventory.getMode();
            storageArea = newBarcodeInventory.getStorageArea();
            wareHouse = newBarcodeInventory.getWarehouse();
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
            r.setMsg("该物料已经领料完成，请注意");
            return r;
        } else {//实发数量<应发数量


            CfCostCenterPickOrWithdrawScanRecord scanRecord = new CfCostCenterPickOrWithdrawScanRecord();
            int updatedActualNumber;

            //条码数量<=应发数量-实发数量
            if (barcodeNumber.intValue() <= gatherView.getShouldPickOrWithdrawNumber() - gatherView.getScannedNumber()) {

                scanRecord.setNumber(barcodeNumber.intValue());

                updatedActualNumber = gatherView.getScannedNumber() + barcodeNumber.intValue();

            } else {//条码数量>应发数量-实发数量

                scanRecord.setNumber(gatherView.getShouldPickOrWithdrawNumber() - gatherView.getScannedNumber());
                updatedActualNumber = gatherView.getShouldPickOrWithdrawNumber();
            }

            scanRecord.setOrderNo(orderNo);
            //设置单据类型为领料
            scanRecord.setOrderType("1");
            scanRecord.setMaterialsName(materialsName);
            scanRecord.setMaterialsNo(materialsNo);
            scanRecord.setSpec(spec);
            scanRecord.setBarcode(barcode);
            scanRecord.setBarcodeType(newKtm == null ? newBarcodeInventory.getBarcodeType() : "KTM");
            scanRecord.setBatchNo(newKtm == null ? newBarcodeInventory.getBatchNo() : newKtm.getBatchNo());
            scanRecord.setWarehouse(wareHouse);
            scanRecord.setStorageArea(storageArea);
            scanRecord.setWarehousePosition(newKtm == null ? newBarcodeInventory.getWarehousePosition() : "");
            //状态为空或空字符串则代表可用，即未提交
            scanRecord.setState("");
            scanRecord.setObjectSetBasicAttribute(userId, new Date());
            scanRecord.setCostCenterPickOrWithdrawInfoId(gatherView.getCostCenterPickOrWithdrawInfoId());
            scanRecord.setCostCenterPickOrWithdrawInventoryId(gatherView.getCostCenterPickOrWithdrawInventoryId());

            gatherView.setScannedNumber(updatedActualNumber);
            gatherView.setObjectSetBasicAttribute(userId, new Date());

            //更新数据（插入记录表和更新清单表）
            cfCostCenterPickService.updateDataAfterScanBarcode(scanRecord, gatherView);

            Map<String, Object> map = pickService.loadDataFromLocalDataBase(orderNo, userId);
            r.setData(map);
            return r;

        }
    }

}
