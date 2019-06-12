package com.cfmoto.bar.code.service.allotmanagement.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.bo.CfAllotOnWayDataBo;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayData;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayLabel;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotOnWayDataService;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotOnWayLabelPrintService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 在途标签打印业务层接口实现类
 *
 * @author ye
 */
@Service
public class CfAllotOnWayLabelPrintServiceImpl implements ICfAllotOnWayLabelPrintService {

    @Autowired
    private ICfAllotOnWayDataService cfAllotOnWayDataService;

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;

    @Autowired
    private ICfNextNumberService nextNumberService;

    @Override
    public List<CfAllotOnWayData> getOnWayByOrderNo(String orderNo) {
        return cfAllotOnWayDataService.selectList(new EntityWrapper<CfAllotOnWayData>().eq("order_no", orderNo));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<CfAllotOnWayLabel> print(CfAllotOnWayDataBo bo, int userId) throws Exception {

        CfAllotOnWayData cfAllotOnWayData = bo.getCfAllotOnWayData();

        int tempNumber = bo.getToBePrintedNumber();

        List<CfAllotOnWayLabel> labelList = new ArrayList<>();

        while (tempNumber > 0) {
            //生成条码
            String barcode = nextNumberService.generateNextNumber("ZT_BARCODE_NO");

            int splitUnitNumber = bo.getSplitUnit() > tempNumber ? tempNumber : bo.getSplitUnit();

            //插入数据到库存表
            CfBarcodeInventory barcodeInventory = new CfBarcodeInventory();
            barcodeInventory.setBarcode(StringUtils.trimToEmpty(barcode));
            barcodeInventory.setMaterialsName(StringUtils.trimToEmpty(cfAllotOnWayData.getMaterialsName()));
            barcodeInventory.setMaterialsNo(StringUtils.trimToEmpty(cfAllotOnWayData.getMaterialsNo()));
            barcodeInventory.setMode(StringUtils.trimToEmpty(cfAllotOnWayData.getSpec()));
            barcodeInventory.setBarcodeType("OT");
            barcodeInventory.setBatchNo(StringUtils.trimToEmpty(cfAllotOnWayData.getBatchNo()));
            barcodeInventory.setBarCodeNumber(new BigDecimal(splitUnitNumber));
            barcodeInventory.setWarehouse(StringUtils.trimToEmpty(cfAllotOnWayData.getAllotInWarehouse()));
            barcodeInventory.setSuppler(StringUtils.trimToEmpty(cfAllotOnWayData.getSupplier()));
            barcodeInventory.setProductionTaskOrder(cfAllotOnWayData.getOrderNo());
            barcodeInventory.setState("N");
            barcodeInventory.setObjectSetBasicAttribute(userId, new Date());
            barcodeInventory.setPrintingDate(new Date());
            barcodeInventory.setPrintingBy(userId);
            barcodeInventory.setFactory(cfAllotOnWayData.getFactory());
            barcodeInventoryService.insert(barcodeInventory);


            //生成标签打印模板
            CfAllotOnWayLabel label = new CfAllotOnWayLabel();
            label.setBarcode(StringUtils.trimToEmpty(barcode));
            label.setBatchNo(StringUtils.trimToEmpty(cfAllotOnWayData.getBatchNo()));
            label.setMaterialsName(StringUtils.trimToEmpty(cfAllotOnWayData.getMaterialsName()));
            label.setMaterialsNo(StringUtils.trimToEmpty(cfAllotOnWayData.getMaterialsNo()));
            label.setNumber(splitUnitNumber);
            label.setPrintOrderInvoice(StringUtils.trimToEmpty(cfAllotOnWayData.getOrderNo()));
            label.setSpec(StringUtils.trimToEmpty(cfAllotOnWayData.getSpec()));
            label.setSupplier(StringUtils.trimToEmpty(cfAllotOnWayData.getSupplier()));
            labelList.add(label);

            tempNumber -= bo.getSplitUnit();
        }

        return labelList;
    }

}
