package com.cfmoto.bar.code.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfStockHandoverScanRecord;
import com.cfmoto.bar.code.model.entity.CfStockInventory;
import com.cfmoto.bar.code.service.ICfRepositoryStockHandoverService;
import com.cfmoto.bar.code.service.ICfStockHandoverScanRecordService;
import com.cfmoto.bar.code.service.ICfStockInventoryService;
import com.cfmoto.bar.code.utiles.BarcodeUtils;
import com.github.pig.common.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库备料交接接口实现类
 *
 * @author ye
 */
@Service
public class CfRepositoryStockHandoverServiceImpl implements ICfRepositoryStockHandoverService {


    @Autowired
    private ICfStockInventoryService cfStockInventoryService;

    @Autowired
    private ICfStockHandoverScanRecordService cfStockHandoverScanRecordService;


    /**
     *
     * 解析条码并校验，校验通过了后更新备料清单表中的备料交接数量，并向备料扫描交接记录表中新增一条记录数据
     *
     * @param jsonObject
     * @param request
     * @return map
     * @author ye
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> analysisBarcode(JSONObject jsonObject, HttpServletRequest request) {

        int userId = UserUtils.getUserId(request);

        //从JSONObject中获取所有的参数
        String barcode = jsonObject.getString("barcode");

        String inputStockListNo = jsonObject.getString("inputStockListNo");

        Map<String, Object> returnMap = new HashMap<>();

        List<Map<String, Object>> materialsList = (List<Map<String, Object>>) jsonObject.get("materialsMap");


        //先校验变量数（格式校验）
        Map<String, Object> map = BarcodeUtils.verifyBarcodeFormatBeforeSplit(barcode);

        if (((int) map.get("code")) == 1) {//校验没通过，返回错误信息
            return map;
        }


        //首先对条码进行拆分
        Map<String, Object> paramMap = BarcodeUtils.splitBarcode(barcode);

        int number = Integer.parseInt(paramMap.get("number").toString());


        //根据物料代码找到汇总界面中对应的备料清单表主键
        int stockInventoryId = 0;

        int unClearedNumber = 0;

        for (Map<String, Object> stringStringMap : materialsList) {
            if (paramMap.get("materialsNo").toString().equals(stringStringMap.get("materialsNo"))) {
                stockInventoryId = (int) stringStringMap.get("stockInventoryId");
                unClearedNumber = (int) stringStringMap.get("unClearedNumber");
                returnMap.put("stockInventoryId", stockInventoryId);
            }
        }


        //再对条码进行格式校验
        map = BarcodeUtils.verifyBarcodeFormatAfterSplit(paramMap);

        //合适校验未通过，返回错误信息
        if (((int) map.get("code")) == 1) {
            return map;
        }

        //进行业务校验

        //1.阶段码校验
        map = BarcodeUtils.verifyStageCode(Integer.parseInt(paramMap.get("stageCode").toString()), 1);

        //合适校验未通过，返回错误信息
        if (((int) map.get("code")) == 1) {
            return map;
        }

        //2.条码解析的备料单与输入框锁定的备料单号是否相同校验（需要相同）
        map = BarcodeUtils.verifyStockListNosEqulas(paramMap.get("stockListNo").toString(), inputStockListNo);

        //合适校验未通过，返回错误信息
        if (((int) map.get("code")) == 1) {
            return map;
        }

        //3.条码解析的物料代码是否存在汇总界面（需要存在）
        map = BarcodeUtils.verifyMaterialsIsExist(paramMap.get("materialsNo").toString(), materialsList);

        if (((int) map.get("code")) == 1) {
            return map;
        }


        //4.条码是否已经存在备料交接扫描记录表（不需要存在）
        CfStockHandoverScanRecord stockRecord = new CfStockHandoverScanRecord();
        stockRecord.setBarcode(barcode);

        CfStockHandoverScanRecord newScanRecord = cfStockHandoverScanRecordService.selectOne(new EntityWrapper<>(stockRecord));
        if (newScanRecord != null) {
            map.put("code", 1);
            map.put("msg", BarcodeUtils.ALREDY_HANDOVER_ERROR_MSG);
            return map;
        }

        //5.条码解析的数量是否大于汇总的未清数量（不能大于）
        if (number > unClearedNumber) {
            map.put("code", 1);
            map.put("msg", BarcodeUtils.NUMBER_INCORRECT);
            return map;
        }

        //条码校验全部通过，开始处理业务逻辑

        //1、	更新汇总界面已交接数量和未清数量，更新备料清单表的备料交接数量
        returnMap.put("unClearedNumber", unClearedNumber - number);
        //先根据物料代码和备料单号查询对应的备料清单表数据
        CfStockInventory cfStockInventory = new CfStockInventory();
        cfStockInventory.setMaterialsNo(paramMap.get("materialsNo").toString());
        cfStockInventory.setStockListNo(inputStockListNo);
        CfStockInventory newStockInventory = cfStockInventoryService.selectOne(new EntityWrapper<>(cfStockInventory));

        newStockInventory.setStockHandoverNumber(newStockInventory.getStockHandoverNumber().add(new BigDecimal(number)));
        newStockInventory.setObjectSetBasicAttributeWhileUpdate(userId, new Date());
        returnMap.put("stockHandoverNumber", newStockInventory.getStockHandoverNumber());

        boolean update = cfStockInventoryService.updateById(newStockInventory);

        //2、	数据插入备料交接扫描记录表，更新在已扫描界面
        CfStockHandoverScanRecord insertScanRecord = new CfStockHandoverScanRecord();
        insertScanRecord.setStockListNo(inputStockListNo);


        //根据主键查询备料清单表数据
        CfStockInventory selectById = cfStockInventoryService.selectById(stockInventoryId);

        //复制数据到备料交接扫描记录实体中去
        insertScanRecord.setMaterialsName(selectById.getMaterialsName());
        insertScanRecord.setMaterialsNo(selectById.getMaterialsNo());
        insertScanRecord.setSpec(selectById.getSpec());
        insertScanRecord.setBarcode(barcode);
        insertScanRecord.setRepository(selectById.getRepository());
        insertScanRecord.setStorageArea(selectById.getStorageArea());
        insertScanRecord.setBatchNo(paramMap.get("batchNo").toString());
        insertScanRecord.setNumber(number);
        insertScanRecord.setObjectSetBasicAttribute(userId, new Date());
        insertScanRecord.setStockListId(selectById.getStockListId());
        insertScanRecord.setStockInventoryId(selectById.getStockInventoryId());

        boolean insert = insertScanRecord.insert();

        returnMap.put("insertScanRecord", insertScanRecord);


        //3、	Message提示***条码交接成功

        if (!(insert && update)) {//更新和插入都失败
            map.put("code", 1);
            map.put("msg", "交接失败,请联系管理员后重试！");
        } else {
            map.put("code", 0);
            map.put("msg", "'" + barcode + "'" + "条码交接成功");
        }

        map.put("data", returnMap);
        return map;
    }
}
