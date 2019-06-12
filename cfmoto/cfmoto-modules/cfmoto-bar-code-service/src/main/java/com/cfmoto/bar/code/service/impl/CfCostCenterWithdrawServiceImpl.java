package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawScanRecord;
import com.cfmoto.bar.code.model.entity.CfKtmReceivingOrder;
import com.cfmoto.bar.code.service.*;
import com.github.pig.common.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 成本中心退料业务层实现类
 *
 * @author ye
 */
@Service
public class CfCostCenterWithdrawServiceImpl implements ICfCostCenterWithdrawService {

    private final ICfCostCenterPickOrWithdrawScanRecordService scanRecordService;

    private final SapFeignService sapFeignService;

    private final ICfBarcodeInventoryService barcodeInventoryService;

    private final ICfKtmReceivingOrderService ktmReceivingOrderService;

    private final ICfCostCenterPickOrWithdrawInventoryService inventoryService;

    @Autowired
    public CfCostCenterWithdrawServiceImpl(ICfCostCenterPickOrWithdrawScanRecordService scanRecordService,
                                           SapFeignService sapFeignService,
                                           ICfBarcodeInventoryService barcodeInventoryService,
                                           ICfKtmReceivingOrderService ktmReceivingOrderService,
                                           ICfCostCenterPickOrWithdrawInventoryService inventoryService) {
        this.scanRecordService = scanRecordService;
        this.sapFeignService = sapFeignService;
        this.barcodeInventoryService = barcodeInventoryService;
        this.ktmReceivingOrderService = ktmReceivingOrderService;
        this.inventoryService = inventoryService;
    }

    /**
     * @param orderNo   订单号
     * @param expressNo 快递单号
     * @param apiNo     接口号(1:领料 || 2:部品 || 3:退料)
     * @return
     */
    @Override
    public R<Map<String, Object>> commitToSapAndGetReturnData(String orderNo, String expressNo, String apiNo, int userId) {

        //根据单号查找成本中心领退料记录表中所有对应单号的状态为未提交的行数据
        List<CfCostCenterPickOrWithdrawScanRecord> scanRecordList = scanRecordService.getUnCommitedDataByOrderNo(orderNo, userId);

        if (scanRecordList == null) {
            R<Map<String, Object>> r = new R<>();
            r.setErrorAndErrorMsg("当前单号没有对应的未提交扫描记录表!");
            return r;
        }

        //封装数据，发送给SAP接口07
        Map<String, Object> paramMap = new HashMap<>(4);

        paramMap.put("functionName", "ZMM_BC_009");

        List<Map<String, Object>> list = new ArrayList<>();

        for (CfCostCenterPickOrWithdrawScanRecord scanRecord : scanRecordList) {
            Map<String, Object> map = new HashMap<>();
            map.put("MATNR", scanRecord.getMaterialsNo());
            map.put("MAKTX", scanRecord.getMaterialsName());
            map.put("TMLX", scanRecord.getBarcodeType());
            map.put("GERNR", scanRecord.getBarcode());
            map.put("CHARG", scanRecord.getBatchNo());
            map.put("LGORT", scanRecord.getWarehouse());
            map.put("MENGE", scanRecord.getNumber() + "");
            map.put("ZCCCW", scanRecord.getWarehousePosition());
            map.put("ZCCLX", scanRecord.getStorageArea());
            if ("3".equals(apiNo)) {
                map.put("ABLAD", "");
            } else {
                map.put("ABLAD", expressNo);
            }
            list.add(map);
        }

        Map<String, Object> dataMap = new HashMap<>(3);

        dataMap.put("IT_DATA", list);
        dataMap.put("IV_RSNUM", orderNo);


        paramMap.put("paramMap", dataMap);

        //连接SAP07接口
        R<Map<String, Object>> r = sapFeignService.executeJcoFunction(paramMap);
        return r;
    }


    /**
     * 退料
     * 判断条码类型：
     * OT:条码更改状态为可用
     * CP/EG/KTM:增加库存数量为1（KTM在KTM表），更新仓库信息；
     * 删除成本中心领退扫描记录表中对应账号的对应单号的未提交的行数据、删除清单表。
     *
     * @param orderNo 订单号
     * @param userId  用户ID
     * @param apiNo   接口号（1：领料 || 2：部品 || 3：退料）
     */
    @Override
    public void withdraw(String orderNo, int userId, String apiNo) throws Exception {


        //判断得到条码类型
        List<CfCostCenterPickOrWithdrawScanRecord> records = scanRecordService.selectList(new EntityWrapper<CfCostCenterPickOrWithdrawScanRecord>().
                eq("order_no", orderNo).eq("created_by", userId).andNew().eq("state", "").or().isNull("state"));

        for (CfCostCenterPickOrWithdrawScanRecord record : records) {
            String barcode = record.getBarcode();

            //去条码库和ktm中查找条码
            CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", barcode));

            CfKtmReceivingOrder ktmReceivingOrder = ktmReceivingOrderService.selectOne(new EntityWrapper<CfKtmReceivingOrder>().eq("frame_no", barcode));

            String barcodeType = record.getBarcodeType();

            switch (barcodeType) {
                case "OT": {

                    //退料则更改条码状态为可用
                    if ("3".equals(apiNo)) {

                        barcodeInventory.setState("");
                        barcodeInventory.setObjectSetBasicAttribute(userId, new Date());
                        barcodeInventoryService.updateById(barcodeInventory);

                        //领料则修改条码的数量、部品不采取任何操作
                    } else if ("1".equals(apiNo)) {

                        CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
                        cfBarcodeInventory.setBarcode(record.getBarcode());
                        CfBarcodeInventory newBarcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<>(cfBarcodeInventory));
                        newBarcodeInventory.setBarCodeNumber(newBarcodeInventory.getBarCodeNumber().subtract(new BigDecimal(record.getNumber())));
                        newBarcodeInventory.setObjectSetBasicAttribute(userId, new Date());
                        barcodeInventoryService.updateById(newBarcodeInventory);
                    }
                    break;
                }
                case "CP":
                case "EG": {
                    //退料
                    if ("3".equals(apiNo)) {

                        barcodeInventory.setBarCodeNumber(new BigDecimal(1));

                    } else if ("1".equals(apiNo)) {

                        barcodeInventory.setBarCodeNumber(new BigDecimal(0));
                    }

                    barcodeInventory.setObjectSetBasicAttribute(userId, new Date());
                    barcodeInventoryService.updateById(barcodeInventory);
                    break;
                }
                case "KTM": {
                    if ("1".equals(apiNo)) {

                        ktmReceivingOrder.setBarCodeNumber(new BigDecimal(0));

                    } else if ("3".equals(apiNo)) {

                        ktmReceivingOrder.setBarCodeNumber(new BigDecimal(1));

                    }

                    ktmReceivingOrder.setObjectSetBasicAttribute(userId, new Date());
                    ktmReceivingOrderService.updateById(ktmReceivingOrder);
                    break;
                }
                default: {
                    break;
                }
            }

        }


        //删除成本中心领退扫描记录表中对应账号的对应单号的未提交的行数据、删除清单表。
        scanRecordService.delete(new EntityWrapper<CfCostCenterPickOrWithdrawScanRecord>().eq("order_no", orderNo).andNew().eq("state", "").or().isNull("state"));

        inventoryService.delete(new EntityWrapper<CfCostCenterPickOrWithdrawInventory>().eq("order_no", orderNo));
    }
}
