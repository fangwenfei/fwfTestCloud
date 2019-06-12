package com.cfmoto.bar.code.service.impl.stock;

import com.alibaba.fastjson.JSONArray;
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
import com.cfmoto.bar.code.service.ICfStockSplitService;
import com.cfmoto.bar.code.service.ICfStockTooReceiveHeaderService;
import com.cfmoto.bar.code.service.ICfTooReceiveStockService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
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
 * **********Thunderstorm, rain and dew are all grace of heaven
 * 生产超领**********
 * **********************************************************************
 */
@Service
public class ICfTooReceiveStockServiceImpl   extends ServiceImpl<CfStockTooReceiveLineMapper, CfStockTooReceiveLine> implements ICfTooReceiveStockService {


    @Autowired
    CfStockTooReceiveRootMapper cfStockTooReceiveRootMapper;

    @Autowired
    CfStockTooReceiveHeaderMapper cfStockTooReceiveHeaderMapper ;

    @Autowired
    CfKtmReceivingOrderMapper cfKtmReceivingOrderMapper ;

    @Autowired
    CfBarcodeBindMapper cfBarcodeBindMapper ;

    @Autowired
    CfBarcodeInventoryMapper cfBarcodeInventoryMapper ;

    @Autowired
    SapFeignService sapFeignService ;

    @Autowired
    ICfStockTooReceiveHeaderService iCfStockTooReceiveHeaderService;

    @Autowired
    ICfStockSplitService iCfStockSplitService ;

    @Autowired
    private ICfNextNumberService cfNextNumberService;
    /***
     * 通过stockListNo获取备料超领数据
     * @param userId
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getDataByStockByNo(int userId, Map<String, Object> params) throws Exception {
        //通过stockListNo查询单据头
        String stockListNo= params.getOrDefault("stockListNo", "").toString();
        String stockFunctionTypeStr= params.getOrDefault("stockFunctionType", "").toString();
        CfStockTooReceiveRoot cfStockTooReceiveRoot =new CfStockTooReceiveRoot();
        cfStockTooReceiveRoot.setStockListNo(stockListNo);
        cfStockTooReceiveRoot=cfStockTooReceiveRootMapper.selectOne(cfStockTooReceiveRoot);
        Map<String, Object> resultMap=new HashedMap();
        Date newDate=new Date();
        // TODO SAP 接口获取数据 备料单传输接口
        Map<String,Object> paramMap =new HashedMap();
        paramMap.put("functionName","ZMM_BC_027");
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IV_ZLIST",stockListNo);
        paramMap.put("paramMap",dataMap);
        R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMap);
        if(result.getCode()!=0){
            throw  new ValidateCodeException(result.getMsg());
        }

        Map<String,Object> resultMapData=result.getData();
        JSONObject jsonObject =new JSONObject(resultMapData);
        if(!jsonObject.getString("EV_STATUS").equals("1")){
            throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }
        String stockFunctionTypeName=jsonObject.getString("EV_ZBLLX");
        String stockFunctionType=jsonObject.getString("EV_BLLXBM");
        if(!stockFunctionTypeStr.equals(stockFunctionType)){
            throw  new ValidateCodeException(CfStockScanLine.EX_FUNCTION_JUDGE+stockFunctionTypeName);
        }
        //判断是否是可进行
        if(!CfStockScanLine.STATUS_UNCOMPLETE.equals(jsonObject.getString("EV_ZSTATUS"))){
            throw  new ValidateCodeException(CfStockScanLine.EX_STATUS_JUDGE);
        }
        if(cfStockTooReceiveRoot==null){
            //添加根信息
            cfStockTooReceiveRoot =new CfStockTooReceiveRoot();
            cfStockTooReceiveRoot.setStockListNo(stockListNo);
            cfStockTooReceiveRoot.setObjectSetBasicAttribute(userId,newDate);
            cfStockTooReceiveRoot.setStockFunctionType(stockFunctionType);
            cfStockTooReceiveRoot.setStockFunctionTypeName(stockFunctionTypeName);
            // ET_DATA
            JSONArray jsonArray=jsonObject.getJSONArray("ET_DATA");
            List<CfStockTooReceiveLine> cfStockTooReceiveLineList=new ArrayList<>();
            List<CfStockTooReceiveHeader> cfStockTooReceiveHeaderList=new ArrayList<>();
            for (int i=0;i<jsonArray.size();i++) {
                JSONObject etData=jsonArray.getJSONObject(i);
                if(i==0){
                    String stockRepository=  etData.getString("LGORT");
                    cfStockTooReceiveRoot.setStockRepository(stockRepository);
                    cfStockTooReceiveRootMapper.insert(cfStockTooReceiveRoot);
                }
                CfStockTooReceiveHeader cfStockInventory=new CfStockTooReceiveHeader();
                cfStockInventory.setObjectSetBasicAttribute(userId,newDate);
                cfStockInventory.setStockRootId(cfStockTooReceiveRoot.getStockRootId());
                cfStockInventory.setStockListNo(stockListNo);
                cfStockInventory.setMaterialsNo(etData.getString("MATNR"));//物料编号
                cfStockInventory.setMaterialsName(etData.getString("MAKTX"));//物料名称
                cfStockInventory.setSpec(etData.getString("WRKST"));//规格型号
                cfStockInventory.setRepository(etData.getString("LGPRO"));//发货仓库
                cfStockInventory.setStorageArea(etData.getString("LGTYP"));//存储区域
                cfStockInventory.setShouldSendNumber(etData.getBigDecimal("BDMNG"));//数量
                cfStockInventory.setActualSendNumber(etData.getBigDecimal("BDMNG").subtract(etData.getBigDecimal("ZBLWQSL")));//未清数量
                cfStockTooReceiveHeaderList.add(cfStockInventory);
            }
            iCfStockTooReceiveHeaderService.insertBatch(cfStockTooReceiveHeaderList);
        }

        if(!stockFunctionTypeStr.equals(cfStockTooReceiveRoot.getStockFunctionType())){
            throw  new ValidateCodeException(CfStockScanLine.EX_FUNCTION_JUDGE+cfStockTooReceiveRoot.getStockFunctionTypeName());
        }
        int current=1;
        int size=QueryPage.LIMIT_10000;
        //获取已扫描数据
        Page<CfStockTooReceiveLine> headerPage=new Page<>( current,  size,  CfStockTooReceiveLine.STOCK_LINE_ID_SQL,false);
        Page  cfStockTooReceiveLinePage=this.selectPage(headerPage, new EntityWrapper<CfStockTooReceiveLine>().
                eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,cfStockTooReceiveRoot.getStockRootId()));

        //通过单据头获取汇总数据
        Page<CfStockTooReceiveHeader> cfStockTooReceiveHeaderPage=new Page<>( current,  size,  CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,false);

        // CfStockTooReceiveHeader
        RowBounds rowBounds =new RowBounds( 0,  size);
        Wrapper entityWrapper= new EntityWrapper<CfStockTooReceiveHeader>()
                .eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,cfStockTooReceiveRoot.getStockRootId())
                .orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false);
        List<CfStockTooReceiveHeader> cfStockTooReceiveHeaderList=cfStockTooReceiveHeaderMapper.selectPage(rowBounds,entityWrapper);
        cfStockTooReceiveHeaderPage.setRecords(cfStockTooReceiveHeaderList);
        //将数据封装到界面
        resultMap.put("cfStockTooReceiveRoot",cfStockTooReceiveRoot);
        resultMap.put("cfStockTooReceiveHeaderPage",cfStockTooReceiveHeaderPage);
        resultMap.put("cfStockTooReceiveLinePage",cfStockTooReceiveLinePage);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addScanLineData(int userId, Map<String, Object> params) throws Exception {
        String barCodeNo= params.getOrDefault("barCodeNo", "").toString();
        String stockRootIdSt= params.getOrDefault("stockRootId", "").toString();
        String checkRecord= params.getOrDefault("checkRecord", "N").toString();
        Date thisNewDate=new Date() ;
        if(!StringUtils.isNotBlank(barCodeNo)){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BAR_CODE_NO);
        }
        if(!StringUtils.isNotBlank(stockRootIdSt)){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        //判断条码是否被重复扫描
        int count= this.selectCount( new EntityWrapper<CfStockTooReceiveLine>().eq(CfStockTooReceiveLine.PARAMS_BARCODE,barCodeNo));
        if(count>0){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BAR_CODE_DOUBLE);
        }

        int stockRootId=Integer.parseInt(stockRootIdSt);
        CfKtmReceivingOrder cfKtmReceivingOrder=new CfKtmReceivingOrder();
        cfKtmReceivingOrder.setFrameNo(barCodeNo);
        cfKtmReceivingOrder= cfKtmReceivingOrderMapper.selectOne(cfKtmReceivingOrder);


        if(cfKtmReceivingOrder!=null){
            //判断条码数量是否还有
            if(cfKtmReceivingOrder.getBarCodeNumber().compareTo(new BigDecimal(1))<0){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BAR_CODE_NUMBER);
            }
            //拿取销售发货单数据
            CfStockTooReceiveHeader cfStockTooReceiveHeader=new CfStockTooReceiveHeader();
            cfStockTooReceiveHeader.setMaterialsNo(cfKtmReceivingOrder.getMaterialsNo());
            cfStockTooReceiveHeader.setStockRootId(stockRootId);
            //加入人员的过滤
            
            cfStockTooReceiveHeader=cfStockTooReceiveHeaderMapper.selectOne(cfStockTooReceiveHeader);
            if(cfStockTooReceiveHeader==null){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_MATERIALS_NOT_HAVING);
            }
            BigDecimal scanningNumber =cfStockTooReceiveHeader.getActualSendNumber().add(new BigDecimal(1));
            if(scanningNumber.compareTo(cfStockTooReceiveHeader.getShouldSendNumber())>0){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BARCODE_NUMBER);
            }
            cfStockTooReceiveHeader.setActualSendNumber(scanningNumber);
            cfStockTooReceiveHeader.setLastUpdatedBy(userId);
            cfStockTooReceiveHeader.setLastUpdatedDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfStockTooReceiveHeaderMapper.updateById(cfStockTooReceiveHeader);
            //加入已扫描条码
            CfStockTooReceiveLine cfStockTooReceiveLine =new CfStockTooReceiveLine();
            cfStockTooReceiveLine.setObjectSetBasicAttribute(userId,thisNewDate);
            cfStockTooReceiveLine.setStockListNo(cfStockTooReceiveHeader.getStockListNo());
            cfStockTooReceiveLine.setBarcode(barCodeNo);
            cfStockTooReceiveLine.setStockRootId(stockRootId);
            cfStockTooReceiveLine.setStockHeaderId(cfStockTooReceiveHeader.getStockHeaderId());
            cfStockTooReceiveLine.setMaterialsName(cfStockTooReceiveHeader.getMaterialsName());
            cfStockTooReceiveLine.setMaterialsNo(cfStockTooReceiveHeader.getMaterialsNo());
            cfStockTooReceiveLine.setNumber(new BigDecimal(1));
            cfStockTooReceiveLine.setBatchNo(cfKtmReceivingOrder.getBatchNo());
            cfStockTooReceiveLine.setBarcodeType(CfStockTooReceiveLine.BARCODE_TYPE_KTM);
            cfStockTooReceiveLine.setOtherTableId(cfKtmReceivingOrder.getKtmReceivingId());
            this.insert(cfStockTooReceiveLine);
        }else{
            //判断是否是整车CP码
            CfBarcodeInventory cfBarcodeInventory=new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(barCodeNo);
            cfBarcodeInventory=cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
            if(cfBarcodeInventory==null){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BAR_CODE_NO_NOT_HAVING);
            }

            //如果整车CP码没有数据，就取库存的数据类型
            String barcodeType=cfBarcodeInventory.getBarcodeType();
            //判断条码数量是否还有
            if(cfBarcodeInventory.getBarCodeNumber().compareTo(new BigDecimal(1))<0){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BAR_CODE_NUMBER);
            }
            //判断条码状态是否可用
            if(CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER.equals(cfBarcodeInventory.getState())){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BAR_CODE_CANT_USER);
            }
            //拿取销售发货单数据
            CfStockTooReceiveHeader cfStockTooReceiveHeader=new CfStockTooReceiveHeader();
            cfStockTooReceiveHeader.setMaterialsNo(cfBarcodeInventory.getMaterialsNo());
            cfStockTooReceiveHeader.setStockRootId(stockRootId);
            cfStockTooReceiveHeader=cfStockTooReceiveHeaderMapper.selectOne(cfStockTooReceiveHeader);
            if(cfStockTooReceiveHeader==null){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_MATERIALS_NOT_HAVING);
            }
            BigDecimal scanningNumberAddOne =cfStockTooReceiveHeader.getActualSendNumber().add(new BigDecimal(1));
            //判断是否已经满仓
            if(scanningNumberAddOne.compareTo(cfStockTooReceiveHeader.getShouldSendNumber())>0){
                throw  new ValidateCodeException(CfStockTooReceiveLine.EX_BARCODE_NUMBER);
            }
            //添加数据
            BigDecimal scanningNumber =cfStockTooReceiveHeader.getActualSendNumber().add(cfBarcodeInventory.getBarCodeNumber());
            BigDecimal barCodeNumber=cfBarcodeInventory.getBarCodeNumber();
            //加入的数据是否超过满仓需求数量
            if(scanningNumber.compareTo(cfStockTooReceiveHeader.getShouldSendNumber())>0){
                //如果大于就等于需求数量
                scanningNumber=cfStockTooReceiveHeader.getShouldSendNumber();
                //额外添加数量
                barCodeNumber=cfStockTooReceiveHeader.getShouldSendNumber().subtract(cfStockTooReceiveHeader.getActualSendNumber());
            }

            cfStockTooReceiveHeader.setActualSendNumber(scanningNumber);
            cfStockTooReceiveHeader.setLastUpdatedBy(userId);
            cfStockTooReceiveHeader.setLastUpdatedDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfStockTooReceiveHeaderMapper.updateById(cfStockTooReceiveHeader);
            //加入已扫描条码
            CfStockTooReceiveLine cfStockTooReceiveLine=new CfStockTooReceiveLine();
            cfStockTooReceiveLine.setObjectSetBasicAttribute(userId,thisNewDate);
            cfStockTooReceiveLine.setStockListNo(cfStockTooReceiveHeader.getStockListNo());
            cfStockTooReceiveLine.setBarcode(barCodeNo);
            cfStockTooReceiveLine.setStockRootId(stockRootId);
            cfStockTooReceiveLine.setStockHeaderId(cfStockTooReceiveHeader.getStockHeaderId());
            cfStockTooReceiveLine.setMaterialsName(cfStockTooReceiveHeader.getMaterialsName());
            cfStockTooReceiveLine.setMaterialsNo(cfStockTooReceiveHeader.getMaterialsNo());
            cfStockTooReceiveLine.setNumber(barCodeNumber);
            cfStockTooReceiveLine.setBatchNo(cfBarcodeInventory.getBatchNo());
            cfStockTooReceiveLine.setBarcodeType(barcodeType);
            cfStockTooReceiveLine.setRepository(cfBarcodeInventory.getWarehouse());
            cfStockTooReceiveLine.setWarehousePosition(cfBarcodeInventory.getWarehousePosition());
            cfStockTooReceiveLine.setStorageArea(cfBarcodeInventory.getStorageArea());
            cfStockTooReceiveLine.setOtherTableId(cfBarcodeInventory.getBarcodeInventoryId());
            this.insert(cfStockTooReceiveLine);
        }
        //获取更新过的数据
        Map<String, Object> resultMap=new HashedMap();
        int current=1;
        int size=QueryPage.LIMIT_10000;
        //通过单据头获取汇总数据
        Page<CfStockTooReceiveLine> headerPage=new Page<>( current,  size);
        Page  cfStockTooReceiveLinePage=this.selectPage(headerPage, new EntityWrapper<CfStockTooReceiveLine>().
                eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId).orderBy(CfStockTooReceiveLine.STOCK_LINE_ID_SQL,false));
        //获取已扫描数据
        Page<CfStockTooReceiveHeader> cfStockTooReceiveHeaderPage=new Page<>( current,  size);
        //设置查询条件
        Wrapper  entityWrapper= new EntityWrapper<CfStockTooReceiveHeader>()
                .eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId).orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false);
               ;
        if(!CfStockTooReceiveLine.PARAMS_N.equals(checkRecord)){
              entityWrapper=entityWrapper.addFilter("should_send_number>actual_send_number");
        }
        // CfStockTooReceiveHeader
        RowBounds rowBounds =new RowBounds( 0,  size);
        List<CfStockTooReceiveHeader> cfStockTooReceiveHeaderList=cfStockTooReceiveHeaderMapper.selectPage(rowBounds,entityWrapper);

        cfStockTooReceiveHeaderPage.setRecords(cfStockTooReceiveHeaderList);
        //将数据封装到界面
        resultMap.put("cfStockTooReceiveHeaderPage",cfStockTooReceiveHeaderPage);
        resultMap.put("cfStockTooReceiveLinePage",cfStockTooReceiveLinePage);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteDataByBarCodeNo(int userId, Map<String, Object> params) throws Exception {

        String checkRecord= params.getOrDefault("checkRecord", "N").toString();
        Integer  stockLineId= Integer.parseInt(params.getOrDefault("stockLineId", "").toString());
        Integer  stockRootId= Integer.parseInt(params.getOrDefault("stockRootId", "").toString());
        Integer  stockHeaderId= Integer.parseInt(params.getOrDefault("stockHeaderId", "").toString());
        BigDecimal  number= new BigDecimal(params.getOrDefault("number", "").toString());
        CfStockTooReceiveHeader cfStockTooReceiveHeader= cfStockTooReceiveHeaderMapper.selectById(stockHeaderId);
        cfStockTooReceiveHeader.setActualSendNumber(cfStockTooReceiveHeader.getActualSendNumber().subtract(number));
        cfStockTooReceiveHeaderMapper.updateById(cfStockTooReceiveHeader);
        this.deleteById(stockLineId);
        //获取更新过的数据
        Map<String, Object> resultMap=new HashedMap();
        int current=1;
        int size=QueryPage.LIMIT_10000;
        //通过单据头获取汇总数据
        Page<CfStockTooReceiveLine> linePage=new Page<>( current,  size,  CfStockTooReceiveLine.STOCK_LINE_ID_SQL,false);
        Page  cfStockTooReceiveLinePage=this.selectPage(linePage, new EntityWrapper<CfStockTooReceiveLine>().
                eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId).orderBy(CfStockTooReceiveLine.STOCK_LINE_ID_SQL,false));
        //获取已扫描数据
        Page<CfStockTooReceiveHeader> cfStockTooReceiveHeaderPage=new Page<>( current,  size);

        // CfStockTooReceiveHeader
        RowBounds rowBounds =new RowBounds( 0,  size);
        Wrapper  entityWrapper= new EntityWrapper<CfStockTooReceiveHeader>()
                .eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId);;
        if(CfStockTooReceiveLine.PARAMS_N.equals(checkRecord)){
            entityWrapper=entityWrapper.orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false);

        }else{
            entityWrapper=entityWrapper.orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false).addFilter("should_send_number>actual_send_number");
        }

        List<CfStockTooReceiveHeader> cfStockTooReceiveHeaderList=cfStockTooReceiveHeaderMapper.selectPage(rowBounds,entityWrapper);

        cfStockTooReceiveHeaderPage.setRecords(cfStockTooReceiveHeaderList);
        //将数据封装到界面
        resultMap.put("cfStockTooReceiveHeaderPage",cfStockTooReceiveHeaderPage);
        resultMap.put("cfStockTooReceiveLinePage",cfStockTooReceiveLinePage);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitICfStockTooReceiveLineData(int userId, Map<String, Object> params) throws Exception {
        Date newDate=new Date();
        String stockRootIdSt= params.getOrDefault("stockRootId", "").toString();
        String printCheck= params.getOrDefault("printCheck", "N").toString();
        if(!StringUtils.isNotBlank(stockRootIdSt)){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        int stockRootId=Integer.parseInt(stockRootIdSt);
        CfStockTooReceiveRoot cfStockTooReceiveRoot =cfStockTooReceiveRootMapper.selectById(stockRootId);
        List<CfStockTooReceiveLine> cfStockTooReceiveLineList=this.selectList(new EntityWrapper<CfStockTooReceiveLine>().
                eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId));
        //扣除数量以及sap数据封装
        CfKtmReceivingOrder cfKtmReceivingOrder=null;
        BigDecimal number0=new BigDecimal(0);
        ArrayList< Map<String,String>> sapArrayData=new ArrayList<>();
        for (CfStockTooReceiveLine cfStockTooReceiveLine:cfStockTooReceiveLineList){
            if(CfStockTooReceiveLine.BARCODE_TYPE_KTM.equals(cfStockTooReceiveLine.getBarcodeType())){
                cfKtmReceivingOrder=new CfKtmReceivingOrder();
                cfKtmReceivingOrder.setBarCodeNumber(number0);
                cfKtmReceivingOrder.setKtmReceivingId(cfStockTooReceiveLine.getOtherTableId());
                cfKtmReceivingOrderMapper.updateById(cfKtmReceivingOrder);
            }else{
                CfBarcodeInventory    cfBarcodeInventory =cfBarcodeInventoryMapper.selectById(cfStockTooReceiveLine.getOtherTableId());
                cfBarcodeInventory.setBarCodeNumber(cfBarcodeInventory.getBarCodeNumber().subtract(cfStockTooReceiveLine.getNumber()));
                cfBarcodeInventoryMapper.updateById(cfBarcodeInventory);
            }
            //TODO 需要sap的处理
            Map<String,String> tableMap=new HashedMap();
            tableMap.put("ZLIST",StringUtils.trimToEmpty(cfStockTooReceiveLine.getStockListNo()));//备料单号
            tableMap.put("MATNR",StringUtils.trimToEmpty(cfStockTooReceiveLine.getMaterialsNo()));//物料编号
            tableMap.put("MAKTX",StringUtils.trimToEmpty(cfStockTooReceiveLine.getMaterialsName()));//物料名称
            tableMap.put("WRKST",StringUtils.trimToEmpty(cfStockTooReceiveLine.getMode()));//规格型号
            tableMap.put("TMLX",StringUtils.trimToEmpty(cfStockTooReceiveLine.getBarcodeType()));//条码类型
            tableMap.put("GERNR",StringUtils.trimToEmpty(cfStockTooReceiveLine.getBarcode()));//条码
            tableMap.put("CHARG",StringUtils.trimToEmpty(cfStockTooReceiveLine.getBatchNo()));//批次
            tableMap.put("BDMNG",StringUtils.trimToEmpty(cfStockTooReceiveLine.getNumber().toPlainString()));//数量
            tableMap.put("LGPRO",StringUtils.trimToEmpty(cfStockTooReceiveLine.getRepository()));//发货仓库
            tableMap.put("LGTYP",StringUtils.trimToEmpty(cfStockTooReceiveLine.getStorageArea()));//存储区域
            tableMap.put("LGPLA",StringUtils.trimToEmpty(cfStockTooReceiveLine.getWarehousePosition()));//仓位
            tableMap.put("LGORT",StringUtils.trimToEmpty(cfStockTooReceiveRoot.getStockRepository()));//备料仓库
            sapArrayData.add(tableMap);
        }
        Map<String,Object> paramMapSap =new HashedMap();
        paramMapSap.put("functionName","ZMM_BC_033");
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IT_DATA",sapArrayData);
        dataMap.put("IV_ZLIST",cfStockTooReceiveRoot.getStockListNo());
        paramMapSap.put("paramMap",dataMap);
        R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMapSap);
        if(result==null){
            throw  new ValidateCodeException("SAP调用服务异常");
        }
        if(result.getCode()!=0){
            throw  new ValidateCodeException(result.getMsg());
        }
        Map<String,Object> resultMapData=result.getData();
        JSONObject jsonObject =new JSONObject(resultMapData);
        if(!jsonObject.getString("EV_STATUS").equals("1")){
            throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }

        List<CfStockSplit> cfStockSplitList=new ArrayList<>();
        if(printCheck.equals(CfStockScanLine.PARAMS_Y)){


            List<CfStockTooReceiveLine> splitList=this.selectList(new EntityWrapper<CfStockTooReceiveLine>().
                    eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId)
                    .setSqlSelect(" materials_no , materials_name ,batch_no,sum(number) number ,mode")
                    .groupBy("materials_no ,materials_name ,batch_no ,mode")
                    .orderBy("materials_no")
            );
            List<CfStockSplitVo> cfStockSplitVoList=new ArrayList<>();
            String materialsNo="MATERIALS_NO";
            BigDecimal bigDecimalNumber=new BigDecimal(0);
            for(int i=0;i<splitList.size();i++){
                CfStockTooReceiveLine cfStockScanLineSplit=splitList.get(i);
                CfStockSplitVo cfStockSplitVo=new CfStockSplitVo();
                cfStockSplitVo.setMaterialsNo(cfStockScanLineSplit.getMaterialsNo());
                cfStockSplitVo.setMaterialsName(cfStockScanLineSplit.getMaterialsName());
                cfStockSplitVo.setBatchNo(cfStockScanLineSplit.getBatchNo());
                cfStockSplitVo.setNumber(cfStockScanLineSplit.getNumber());
                if(cfStockScanLineSplit.getMaterialsNo().equals(materialsNo)||i==0){
                    materialsNo=cfStockScanLineSplit.getMaterialsNo();
                    bigDecimalNumber= bigDecimalNumber.add(cfStockScanLineSplit.getNumber());
                    cfStockSplitVoList.add(cfStockSplitVo);
                }else{
                    //生成物料拆分
                    CfStockTooReceiveLine cfStockScanLineSplitBefore=splitList.get(i-1);
                    String NextNumber= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                    CfStockSplit cfStockSplit =new CfStockSplit();
                    cfStockSplit.setSplitNo(NextNumber);
                    cfStockSplit.setStockListNo(cfStockTooReceiveRoot.getStockListNo());
                    cfStockSplit.setObjectSetBasicAttribute(userId,newDate);
                    cfStockSplit.setFlag(0);
                    cfStockSplit.setMaterialsNo(cfStockScanLineSplitBefore.getMaterialsNo());
                    cfStockSplit.setMaterialsName(cfStockScanLineSplitBefore.getMaterialsName());
                    cfStockSplit.setMode(cfStockScanLineSplitBefore.getMode());
                    cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoList));
                    cfStockSplit.setNumber(bigDecimalNumber);
                    cfStockSplitList.add(cfStockSplit);
                    cfStockSplitVoList=new ArrayList<>();
                    bigDecimalNumber=new BigDecimal(0);
                    bigDecimalNumber= bigDecimalNumber.add(cfStockScanLineSplit.getNumber());
                    materialsNo=cfStockScanLineSplit.getMaterialsNo();
                    cfStockSplitVoList.add(cfStockSplitVo);
                }
                if(i==splitList.size()-1){
                    String NextNumber= cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                    //生成物料拆分
                    CfStockSplit cfStockSplit =new CfStockSplit();
                    cfStockSplit.setSplitNo(NextNumber);
                    cfStockSplit.setStockListNo(cfStockTooReceiveRoot.getStockListNo());
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
        this.delete(new EntityWrapper<CfStockTooReceiveLine>().
                eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId));
        //删除汇总数据
        Wrapper  entityWrapper= new EntityWrapper<CfStockTooReceiveHeader>()
                .eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId);

        cfStockTooReceiveHeaderMapper.delete(entityWrapper);
        //删除头数据
        cfStockTooReceiveRootMapper.deleteById(stockRootId);
        Map<String,Object> resultMap=new HashedMap();
        //从新获取数据
        try{
            params.put("stockFunctionType", CfStockListInfo.STOCK_FUNCTION_TYPE_40);//合并备料：10，生产领料:20,退料: 30,超领 :40
            params.put("stockListNo",cfStockTooReceiveRoot.getStockListNo());
            resultMap =this.getDataByStockByNo(userId,params);
        }catch (Exception e){
            resultMap.put("cfStockTooReceiveRoot",cfStockTooReceiveRoot);
            resultMap.put("cfStockTooReceiveHeaderPage",null);
            resultMap.put("cfStockTooReceiveLinePage",null);
        }
        resultMap.put("cfStockSplitList",cfStockSplitList);

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page getCfStockTooReceiveHeaderPage(int userId, Map<String, Object> params) throws Exception {
        Integer  page= Integer.parseInt(params.getOrDefault("page", 1).toString());
        Integer  limit= Integer.parseInt(params.getOrDefault("limit", QueryPage.LIMIT_10000).toString());
        String stockRootIdSt= params.getOrDefault("stockRootId", "").toString();
        String checkRecord= params.getOrDefault("checkRecord", "N").toString();
        if(!StringUtils.isNotBlank(stockRootIdSt)){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
      
        int stockRootId=Integer.parseInt(stockRootIdSt);
        //设置查询条件
        //获取已扫描数据
        Page<CfStockTooReceiveHeader> cfStockTooReceiveHeaderPage=new Page<>( page,  limit,  CfStockTooReceiveLine.STOCK_LINE_ID_SQL,false);
        Wrapper  entityWrapper= new EntityWrapper<CfStockTooReceiveHeader>()
                .eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,stockRootId);
        if(CfStockTooReceiveLine.PARAMS_N.equals(checkRecord)){
            entityWrapper=entityWrapper.orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false);
        }else{
            entityWrapper=entityWrapper.orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false).addFilter("should_send_number>actual_send_number");
        }
        // CfStockTooReceiveHeader
        RowBounds rowBounds =new RowBounds( page-1,  limit);
        List<CfStockTooReceiveHeader> cfStockTooReceiveHeaderList=cfStockTooReceiveHeaderMapper.selectPage(rowBounds,entityWrapper);
        cfStockTooReceiveHeaderPage.setRecords(cfStockTooReceiveHeaderList);
        return cfStockTooReceiveHeaderPage;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateByStockScanLineData(int userId, Map<String, Object> params) throws Exception {
        String checkRecord= params.getOrDefault("checkRecord", "N").toString();
        String stockRootIdSt= params.getOrDefault("stockRootId", "").toString();
        String changeNumberSt= params.getOrDefault("changeNumber", "").toString();
        BigDecimal changeNumber;
        if(!StringUtils.isNotBlank(stockRootIdSt)){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_STOCK_LINE_ID);
        }
        if(!StringUtils.isNotBlank(changeNumberSt)){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_CHANGE_NUMBER);
        }
        try{
            changeNumber=new BigDecimal(changeNumberSt);
        }catch (Exception e){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_CHANGE_NUMBER);
        }

        int stockRootId=Integer.parseInt(stockRootIdSt);
        CfStockTooReceiveLine cfStockTooReceiveLine =this.selectById(stockRootId);
        if(changeNumber.compareTo( cfStockTooReceiveLine.getNumber())>0){
            throw  new ValidateCodeException(CfStockTooReceiveLine.EX_CHANGE_NUMBER_TOO_BIG);
        }
        BigDecimal number=cfStockTooReceiveLine.getNumber().subtract(changeNumber);
        cfStockTooReceiveLine.setNumber(changeNumber);
        this.updateById(cfStockTooReceiveLine);
        //
        CfStockTooReceiveHeader cfStockTooReceiveHeader= cfStockTooReceiveHeaderMapper.selectById(cfStockTooReceiveLine.getStockHeaderId());
        cfStockTooReceiveHeader.setActualSendNumber(cfStockTooReceiveHeader.getActualSendNumber().subtract(number));
        cfStockTooReceiveHeaderMapper.updateById(cfStockTooReceiveHeader);

        //获取更新过的数据
        Map<String, Object> resultMap=new HashedMap();
        int current=1;
        int size= QueryPage.LIMIT_10000;
        //通过单据头获取汇总数据
        Page<CfStockTooReceiveLine> headerPage=new Page<>( current,  size,  CfStockTooReceiveLine.STOCK_LINE_ID_SQL,false);
        Page  cfStockTooReceiveLinePage=this.selectPage(headerPage, new EntityWrapper<CfStockTooReceiveLine>().
                eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,cfStockTooReceiveLine.getStockRootId()));
        //获取已扫描数据
        Page<CfStockTooReceiveHeader> cfStockTooReceiveHeaderHeaderPage=new Page<>( current,  size,  CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,false);

        // CfStockTooReceiveHeader
        RowBounds rowBounds =new RowBounds( 0,  size);
        Wrapper  entityWrapper= new EntityWrapper<CfStockTooReceiveHeader>()
                .eq(CfStockTooReceiveLine.STOCK_ROOT_ID_SQL,cfStockTooReceiveLine.getStockRootId());;
        if(CfStockTooReceiveLine.PARAMS_N.equals(checkRecord)){

            entityWrapper=entityWrapper.orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false);

        }else{
            entityWrapper=entityWrapper.orderBy(CfStockTooReceiveLine.LAST_UPDATED_DATE_SQL,false).addFilter("should_send_number>actual_send_number");
        }

        List<CfStockTooReceiveHeader> cfStockTooReceiveHeaderList=cfStockTooReceiveHeaderMapper.selectPage(rowBounds,entityWrapper);

        cfStockTooReceiveHeaderHeaderPage.setRecords(cfStockTooReceiveHeaderList);
        //将数据封装到界面
        resultMap.put("cfStockTooReceiveHeaderHeaderPage",cfStockTooReceiveHeaderHeaderPage);
        resultMap.put("cfStockTooReceiveLinePage",cfStockTooReceiveLinePage);
        return resultMap;
    }
}
