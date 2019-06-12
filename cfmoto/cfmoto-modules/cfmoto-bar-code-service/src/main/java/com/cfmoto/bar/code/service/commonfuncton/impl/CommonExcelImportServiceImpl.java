package com.cfmoto.bar.code.service.commonfuncton.impl;

import com.alibaba.fastjson.JSONArray;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfBoxStickerColorContrastInfo;
import com.cfmoto.bar.code.model.entity.CfKtmReceivingOrder;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfKtmReceivingOrderService;
import com.cfmoto.bar.code.service.commonfuncton.ICommonExcelImportService;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yezi
 * @date 2019/5/27
 */
@Service
@Log4j
public class CommonExcelImportServiceImpl implements ICommonExcelImportService {

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;

    @Autowired
    private ICfKtmReceivingOrderService ktmReceivingOrderService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void excelImport(MultipartFile file, String tableName, Integer userId) throws Exception {
        Date date = new Date();
        switch (tableName) {
            case "cf_barcode_inventory": {
                List<CfBarcodeInventory> list = ExcelUtiles.importExcel(file, 1, 1, CfBarcodeInventory.class);
                log.info("导入数据一共 :" + JSONArray.toJSON(list).toString());
                list.forEach(obj -> obj.setObjectSetBasicAttribute(userId, date));
                barcodeInventoryService.insertBatch(list);
                break;
            }
            case "cf_ktm_receiving_order": {
                List<CfKtmReceivingOrder> list = ExcelUtiles.importExcel(file, 1, 1, CfKtmReceivingOrder.class);
                log.info("导入数据一共 :" + JSONArray.toJSON(list).toString());
                list.forEach(obj -> obj.setObjectSetBasicAttribute(userId, date));
                ktmReceivingOrderService.insertBatch(list);
                break;
            }
            default: {
                throw new Exception("传入的表名暂时不支持通用导入功能,请注意!!!");
            }
        }
    }

    @Override
    public void excelExport(String tableName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        switch (tableName) {
            case "cf_barcode_inventory": {
                ExcelUtiles.exportExcel(new ArrayList<CfBarcodeInventory>(), "条码库存表导入模板", "条码库存表导入模板", CfBarcodeInventory.class, "条码库存表导入模板.xls", response);
                break;
            }
            case "cf_ktm_receiving_order": {
                ExcelUtiles.exportExcel(new ArrayList<CfKtmReceivingOrder>(), "ktm订单表导入模板", "ktm订单表导入模板", CfKtmReceivingOrder.class, "ktm订单表导入模板.xls", response);
                break;
            }
            default: {
                throw new Exception("传入的表名暂时不支持导出模板功能,请注意!!!");
            }
        }
    }
}
