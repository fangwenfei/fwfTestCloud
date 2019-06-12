package com.cfmoto.bar.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfBarcodeInventoryMapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.JustInTimeInventoryPrintVo;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.ICfStorageLocationService;
import com.cfmoto.bar.code.service.JustInTimeInventoryPrintService;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/4/24.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Service
public class JustInTimeInventoryPrintServiceImpl extends ServiceImpl<CfBarcodeInventoryMapper, CfBarcodeInventory> implements JustInTimeInventoryPrintService {

    @Autowired
    private ICfNextNumberService cfNextNumberService;
    @Autowired
    ICfStorageLocationService iCfStorageLocationService ;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public  List<CfBarcodeInventory> splitSapDataPrintByParam(Map<String, Object> params, int userId) throws Exception {
        int countNumber= Integer.parseInt(params.getOrDefault("countNumber", "0").toString());
        int lastNumber= Integer.parseInt(params.getOrDefault("lastNumber", "0").toString());
        int splitNumber= Integer.parseInt(params.getOrDefault("splitNumber", "0").toString());
        String  state= params.getOrDefault("state", "Y").toString(); //1，默认合格
        String  factory= params.getOrDefault("factory", "").toString(); //工厂
        Date thisDate=new Date();

        JSON paramss =(JSON) JSONObject.toJSON(params.getOrDefault("DATA", ""));
        JustInTimeInventoryPrintVo justInTimeInventoryPrintVo  =JSON.toJavaObject( paramss, JustInTimeInventoryPrintVo.class );
        String HoseWare= justInTimeInventoryPrintVo.getLGORT();
        List<JustInTimeInventoryPrintVo> justInTimeInventoryPrintVoList=new ArrayList<>();
        List<CfBarcodeInventory> cfBarcodeInventoryList=new ArrayList<>();
        for(int i=0;i<countNumber;i++){
            JustInTimeInventoryPrintVo justInTimeInventoryPrintIn=justInTimeInventoryPrintVo;
            justInTimeInventoryPrintIn.setNUMBER(String.valueOf(splitNumber));//代码数量;
            String NextNumber= cfNextNumberService.generateNextNumber(JustInTimeInventoryPrintVo.NEXT_NUMBER_TYPE);
            //冻结是不合格，非限制是合格
            justInTimeInventoryPrintVoList.add(justInTimeInventoryPrintIn);
            CfBarcodeInventory cfBarcodeInventory=new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(NextNumber);
            cfBarcodeInventory.setBarcodeType(CfBarcodeInventory.BARCODE_TYPE_OT);
            cfBarcodeInventory.setQresult(state);//质检结果（Y：合格，N：不合格）
            cfBarcodeInventory.setBarCodeNumber(new BigDecimal(splitNumber));
            cfBarcodeInventory.setMaterialsNo(justInTimeInventoryPrintIn.getMATNR());//获取物料代码
            cfBarcodeInventory.setMaterialsName(justInTimeInventoryPrintIn.getMAKTX());//获取物料描述
            cfBarcodeInventory.setBatchNo(justInTimeInventoryPrintIn.getCHARG());//批次号
            cfBarcodeInventory.setMode(justInTimeInventoryPrintIn.getWRSKT());//规格型号
            cfBarcodeInventory.setFactory(factory);//工厂
            cfBarcodeInventory.setWarehouse(HoseWare);
            cfBarcodeInventory.setStorageArea(justInTimeInventoryPrintIn.getLGTYP());//存储区域
            cfBarcodeInventory.setWarehousePosition(justInTimeInventoryPrintIn.getLGPLA()); //仓位
            cfBarcodeInventory.setSuppler(justInTimeInventoryPrintIn.getNAME_ORG1());//供应商
            cfBarcodeInventory.setPrintingDate(thisDate);//打印时间
            cfBarcodeInventory.setPrintingBy(userId);//打印人员
            cfBarcodeInventory.setBasicAttributeForUpdate(userId,thisDate);
            cfBarcodeInventoryList.add(cfBarcodeInventory);
        }
        if(lastNumber>0){
            JustInTimeInventoryPrintVo justInTimeInventoryPrintIn=justInTimeInventoryPrintVo;
            String NextNumber= cfNextNumberService.generateNextNumber(JustInTimeInventoryPrintVo.NEXT_NUMBER_TYPE);
            CfBarcodeInventory cfBarcodeInventory=new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(NextNumber);
            cfBarcodeInventory.setBarcodeType(CfBarcodeInventory.BARCODE_TYPE_OT);
            cfBarcodeInventory.setQresult(state);//质检结果（Y：合格，N：不合格）
            cfBarcodeInventory.setBarCodeNumber(new BigDecimal(lastNumber));
            cfBarcodeInventory.setMaterialsNo(justInTimeInventoryPrintIn.getMATNR());//获取物料代码
            cfBarcodeInventory.setMaterialsName(justInTimeInventoryPrintIn.getMAKTX());//获取物料描述
            cfBarcodeInventory.setBatchNo(justInTimeInventoryPrintIn.getCHARG());//批次号
            cfBarcodeInventory.setMode(justInTimeInventoryPrintIn.getWRSKT());//规格型号
            cfBarcodeInventory.setFactory(factory);//工厂
            cfBarcodeInventory.setWarehouse(HoseWare);
            cfBarcodeInventory.setStorageArea(justInTimeInventoryPrintIn.getLGTYP());//存储区域
            cfBarcodeInventory.setWarehousePosition(justInTimeInventoryPrintIn.getLGPLA()); //仓位&
            cfBarcodeInventory.setSuppler(justInTimeInventoryPrintIn.getNAME_ORG1());//供应商
            cfBarcodeInventory.setPrintingDate(thisDate);//打印时间
            cfBarcodeInventory.setPrintingBy(userId);//打印人员
            cfBarcodeInventory.setBasicAttributeForUpdate(userId,thisDate);
            cfBarcodeInventoryList.add(cfBarcodeInventory);
        }
        this.insertBatch(cfBarcodeInventoryList);
        return cfBarcodeInventoryList;
    }
}
