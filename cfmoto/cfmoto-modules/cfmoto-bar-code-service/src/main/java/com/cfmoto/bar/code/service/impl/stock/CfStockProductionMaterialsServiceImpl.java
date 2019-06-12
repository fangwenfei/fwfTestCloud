package com.cfmoto.bar.code.service.impl.stock;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.mapper.*;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.model.vo.CfStockSplitVo;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.ICfStockProductionMaterialsService;
import com.cfmoto.bar.code.service.ICfStockScanLineService;
import com.cfmoto.bar.code.service.ICfStockSplitService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.github.pig.common.vo.UserVO;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/18.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Service
public class CfStockProductionMaterialsServiceImpl  extends ServiceImpl<CfStockScanLineMapper, CfStockScanLine> implements ICfStockProductionMaterialsService {
    @Autowired
    CfKtmReceivingOrderMapper cfKtmReceivingOrderMapper ;

    @Autowired
    CfBarcodeInventoryMapper cfBarcodeInventoryMapper ;

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    CfStockInventoryMapper cfStockInventoryMapper ;

    @Autowired
    SapFeignService sapFeignService ;

    @Autowired
    CfStockListInfoMapper cfStockListInfoMapper;
    @Autowired
    ICfStockScanLineService iCfStockScanLineService ;
    @Autowired
    private ICfNextNumberService cfNextNumberService;
    @Autowired
    ICfStockSplitService iCfStockSplitService ;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitCfStockProductionMaterialsData(int userId, Map<String, Object> params) throws Exception {
        String stockListIdSt= params.getOrDefault("stockListId", "").toString();
        String printCheck= params.getOrDefault("printCheck", "N").toString();
        Date newDate=new Date();
        if(!StringUtils.isNotBlank(stockListIdSt)){
            throw  new ValidateCodeException(CfStockScanLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        int stockListId=Integer.parseInt(stockListIdSt);
        CfStockListInfo cfStockListInfoRoot =cfStockListInfoMapper.selectById(stockListId);
        //获取用户的绑定的厂库
        UserVO userVO= userFeignService.user(userId);
        if(userVO==null){
            throw  new ValidateCodeException(CfStockScanLine.EX_NOT_USER_REPOSITORY);
        }/* TODO else if ((!StringUtils.isNotBlank(userVO.getWarehouse()))){
            throw  new ValidateCodeException(CfStockScanLine.EX_NOT_USER_REPOSITORY);
        }*/
        Wrapper entityWrapper= new EntityWrapper<CfStockInventory>()
                .eq(CfStockScanLine.STOCK_LIST_ID_SQL,stockListId)
               /* TODO .eq(CfStockScanLine.PARAMS_REPOSITORY,userVO.getWarehouse())*/;
       /* TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
            entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
        }*/
        //更新实发数量=备料交接数量=领料交接数量
        List<CfStockInventory> cfStockInventoryList=cfStockInventoryMapper.selectList(entityWrapper);
        for (CfStockInventory cfStockInventory: cfStockInventoryList) {
            cfStockInventory.setStockHandoverNumber(cfStockInventory.getActualSendNumber());
            cfStockInventory.setRemark(cfStockInventory.REMARK_PRODUCTION_MATERIALS);
            cfStockInventoryMapper.updateById(cfStockInventory);
        }
        //删除自己的数据
        List<CfStockScanLine> cfStockScanLineList=this.selectList(new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL,stockListId).eq(CfStockScanLine.CREATED_BY_SQL,userId));
        //扣除数量以及sap数据封装
        CfKtmReceivingOrder cfKtmReceivingOrder=null;
        BigDecimal number0=new BigDecimal(0);
        ArrayList< Map<String,String>> sapArrayData=new ArrayList<>();
        for (CfStockScanLine cfStockScanLine:cfStockScanLineList){
            if(CfStockScanLine.BARCODE_TYPE_KTM.equals(cfStockScanLine.getBarcodeType())){
                cfKtmReceivingOrder=new CfKtmReceivingOrder();
                cfKtmReceivingOrder.setBarCodeNumber(number0);
                cfKtmReceivingOrder.setKtmReceivingId(cfStockScanLine.getOtherTableId());
                cfKtmReceivingOrderMapper.updateById(cfKtmReceivingOrder);
            }else{
                CfBarcodeInventory cfBarcodeInventory =cfBarcodeInventoryMapper.selectById(cfStockScanLine.getOtherTableId());
                cfBarcodeInventory.setBarCodeNumber(cfBarcodeInventory.getBarCodeNumber().subtract(cfStockScanLine.getNumber()));
                cfBarcodeInventoryMapper.updateById(cfBarcodeInventory);
            }
            //TODO 需要sap的处理
            Map<String,String> tableMap=new HashedMap();
            tableMap.put("ZLIST",StringUtils.trimToEmpty(cfStockScanLine.getStockListNo()));//备料单号
            tableMap.put("MATNR",StringUtils.trimToEmpty(cfStockScanLine.getMaterialsNo()));//物料编号
            tableMap.put("MAKTX",StringUtils.trimToEmpty(cfStockScanLine.getMaterialsName()));//物料名称
            tableMap.put("WRKST",StringUtils.trimToEmpty(cfStockScanLine.getMode()));//规格型号
            tableMap.put("TMLX",StringUtils.trimToEmpty(cfStockScanLine.getBarcodeType()));//条码类型
            tableMap.put("GERNR",StringUtils.trimToEmpty(cfStockScanLine.getBarcode()));//条码
            tableMap.put("CHARG",StringUtils.trimToEmpty(cfStockScanLine.getBatchNo()));//批次
            tableMap.put("BDMNG",StringUtils.trimToEmpty(cfStockScanLine.getNumber().toPlainString()));//数量
            tableMap.put("LGPRO",StringUtils.trimToEmpty(cfStockScanLine.getRepository()));//发货仓库
            tableMap.put("LGTYP",StringUtils.trimToEmpty(cfStockScanLine.getStorageArea()));//存储区域
            tableMap.put("LGPLA",StringUtils.trimToEmpty(cfStockScanLine.getWarehousePosition()));//仓位
            tableMap.put("LGORT",StringUtils.trimToEmpty(cfStockListInfoRoot.getStockRepository()));//备料仓库
            sapArrayData.add(tableMap);
        }
        Map<String,Object> paramMapSap =new HashedMap();
        paramMapSap.put("functionName","ZMM_BC_033");
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IT_DATA",sapArrayData);
        dataMap.put("IV_ZLIST",cfStockListInfoRoot.getStockListNo());
        paramMapSap.put("paramMap",dataMap);
        R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMapSap);
        if(result.getCode()!=0){
            throw  new ValidateCodeException(result.getMsg());
        }
        Map<String,Object> resultMapData=result.getData();
        JSONObject jsonObject =new JSONObject(resultMapData);
        if(!jsonObject.getString("EV_STATUS").equals("1")){
            throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }
        Page cfStockInventoryHeaderPage=  iCfStockScanLineService.getCfStockInventoryPage(userId,params);
        List<CfStockSplit> cfStockSplitList=new ArrayList<>();
        if(printCheck.equals(CfStockScanLine.PARAMS_Y)){
            List<CfStockScanLine> splitList=this.selectList(new EntityWrapper<CfStockScanLine>().
                    eq(CfStockScanLine.STOCK_LIST_ID_SQL,stockListId)
                    .eq(CfStockScanLine.CREATED_BY_SQL,userId)
                    .setSqlSelect(" materials_no , materials_name ,batch_no,sum(number) number ")
                    .groupBy("materials_no ,materials_name ,batch_no")
                    .orderBy("materials_no")
            );
            List<CfStockSplitVo> cfStockSplitVoList=new ArrayList<>();
            String materialsNo="MATERIALS_NO";
            BigDecimal bigDecimalNumber=new BigDecimal(0);
            for(int i=0;i<splitList.size();i++){
                CfStockScanLine cfStockScanLineSplit=splitList.get(i);
                CfStockSplitVo cfStockSplitVo=new CfStockSplitVo();
                cfStockSplitVo.setMaterialsNo(cfStockScanLineSplit.getMaterialsNo());
                cfStockSplitVo.setMaterialsName(cfStockScanLineSplit.getMaterialsName());
                cfStockSplitVo.setBatchNo(cfStockScanLineSplit.getBatchNo());
                cfStockSplitVo.setNumber(cfStockScanLineSplit.getNumber());
                if(cfStockScanLineSplit.getMaterialsNo().equals(materialsNo)||i==0){
                    bigDecimalNumber= bigDecimalNumber.add(cfStockScanLineSplit.getNumber());
                    materialsNo=cfStockScanLineSplit.getMaterialsNo();
                    cfStockSplitVoList.add(cfStockSplitVo);
                }else{
                    //生成物料拆分
                    CfStockScanLine cfStockScanLineSplitBefore=splitList.get(i-1);
                    CfStockSplit cfStockSplit =new CfStockSplit();
                    String NextNumber= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                    cfStockSplit.setSplitNo(NextNumber);
                    cfStockSplit.setStockListNo(cfStockListInfoRoot.getStockListNo());
                    cfStockSplit.setMaterialsName(cfStockScanLineSplitBefore.getMaterialsName());
                    cfStockSplit.setObjectSetBasicAttribute(userId,newDate);
                    cfStockSplit.setFlag(0);
                    cfStockSplit.setMaterialsNo(cfStockScanLineSplitBefore.getMaterialsNo());
                    cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoList));
                    cfStockSplit.setNumber(bigDecimalNumber);
                    cfStockSplit.setMode(cfStockScanLineSplitBefore.getMode());
                    cfStockSplitList.add(cfStockSplit);
                    cfStockSplitVoList=new ArrayList<>();
                    bigDecimalNumber=new BigDecimal(0);
                    bigDecimalNumber= bigDecimalNumber.add(cfStockScanLineSplit.getNumber());
                    materialsNo=cfStockScanLineSplit.getMaterialsNo();
                    cfStockSplitVoList.add(cfStockSplitVo);
                }
                if(i==splitList.size()-1){
                    //生成物料拆分
                    CfStockSplit cfStockSplit =new CfStockSplit();
                    String NextNumber= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                    cfStockSplit.setSplitNo(NextNumber);
                    cfStockSplit.setStockListNo(cfStockListInfoRoot.getStockListNo());
                    cfStockSplit.setObjectSetBasicAttribute(userId,newDate);
                    cfStockSplit.setFlag(0);
                    cfStockSplit.setMaterialsNo(cfStockScanLineSplit.getMaterialsNo());
                    cfStockSplit.setMaterialsName(cfStockScanLineSplit.getMaterialsName());
                    cfStockSplit.setMode(cfStockScanLineSplit.getMode());
                    cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoList));
                    cfStockSplit.setNumber(bigDecimalNumber);
                    cfStockSplitList.add(cfStockSplit);
                }
            }
            iCfStockSplitService.insertBatch(cfStockSplitList);
        }

        //删除临时表数据行数据
        this.delete(new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL,stockListId).eq(CfStockScanLine.CREATED_BY_SQL,userId));
        Map<String,Object> resultMap =new HashedMap();
        resultMap.put("cfStockListInfoRoot",cfStockListInfoRoot);
        resultMap.put("cfStockInventoryHeaderPage",cfStockInventoryHeaderPage);
        resultMap.put("cfStockSplitList",cfStockSplitList);
        resultMap.put("cfStockScanLinePage",null);
        return resultMap;
    }
}
