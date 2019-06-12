package com.cfmoto.bar.code.service.impl;

import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.model.vo.PurchaseOrOutsourceOrderVo;
import com.cfmoto.bar.code.service.PurchaseOrderSearchService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/5/8.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Service
public class PurchaseOrderSearchServiceImpl implements PurchaseOrderSearchService {
    @Autowired
    private SapFeignService sapFeignService;

    @Override
    public List<PurchaseOrOutsourceOrderVo> searchPurchaseOrderBySap(String orderNo, String vendor, String item) throws Exception{
        //查询sap车辆条码数据
        Map<String,Object> callParamMap = new HashMap<>();
        Map<String,Object> paramMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        paramMap.put( "IV_EBELN", orderNo  );
        paramMap.put( "IV_LIFNR", vendor  );
        paramMap.put( "IV_MATNR", item  );
        callParamMap.put( HandleRefConstants.FUNCTION_NAME,"ZMM_BC_037" );
        callParamMap.put( HandleRefConstants.PARAM_MAP,paramMap );
        R returnR = sapFeignService.executeJcoFunction( callParamMap );
        if( returnR.getCode()!=0 ){
            throw new Exception( returnR.getMsg() );
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get( "EV_STATUS" );
        if( evStatus==0 ){
            throw new Exception( (String) returnMap.get( "EV_MESSAGE" ) );
        }
        List< Map<String,Object> > retDataList = (List<Map<String, Object>>) returnMap.get( "ET_DATA" );
        List<PurchaseOrOutsourceOrderVo> purchaseOrOutsourceOrderVoList=new ArrayList<>();
        for (Map<String,Object> resultMap:retDataList) {
            PurchaseOrOutsourceOrderVo purchaseOrOutsourceOrderVo=new PurchaseOrOutsourceOrderVo();
            purchaseOrOutsourceOrderVo.setOrderNo(resultMap.get("EBELN").toString());//EBELN 采购订单
            purchaseOrOutsourceOrderVo.setOrderType(resultMap.get("BSART").toString()); //BSART  订单类型
            purchaseOrOutsourceOrderVo.setVendorDesc(resultMap.get("NAME1").toString());//NAME1  供应商名称
            purchaseOrOutsourceOrderVo.setRowItem(resultMap.get("EBELP").toString());//EBELP 行项目号
            purchaseOrOutsourceOrderVo.setItem(resultMap.get("MATNR").toString());// MATNR   物料代码
            purchaseOrOutsourceOrderVo.setItemDesc(resultMap.get("MAKTX").toString());//MAKTX 物料名称
            purchaseOrOutsourceOrderVo.setQty(new BigDecimal(resultMap.get("MENGE").toString()));//MENGE 订单数量
            purchaseOrOutsourceOrderVo.setDefaultStorageLocation(resultMap.get("LGFSB").toString());//LGFSB 默认仓库
            purchaseOrOutsourceOrderVo.setDemandQty(new BigDecimal(resultMap.get("WEMNG").toString()));//WEMNG 未清数量
            purchaseOrOutsourceOrderVo.setMode(resultMap.get("FERTH").toString());//WRKST 规格型号
            purchaseOrOutsourceOrderVo.setPurchaseGroup(resultMap.get("EKGRP").toString());//EKGRP 采购组

            purchaseOrOutsourceOrderVo.setItemPurpose(resultMap.get("BEDNR").toString());//BEDNR 用途
            purchaseOrOutsourceOrderVo.setOrderDate(DateUtil.parseDate(resultMap.get("AEDAT").toString()));//AEDAT 制单日期
            purchaseOrOutsourceOrderVo.setStatus(resultMap.get("STATU").toString());//STATU 订单状态
            purchaseOrOutsourceOrderVoList.add(purchaseOrOutsourceOrderVo);
        }
        return purchaseOrOutsourceOrderVoList;
    }
}
