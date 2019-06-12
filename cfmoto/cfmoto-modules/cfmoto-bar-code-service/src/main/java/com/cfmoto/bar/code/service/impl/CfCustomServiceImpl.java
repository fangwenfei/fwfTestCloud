package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.*;
import com.cfmoto.bar.code.service.*;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.xiaoleilu.hutool.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CfCustomServiceImpl implements ICfCustomService {

    @Autowired
    private ICfBarcodeInventoryService cfBarcodeInventoryService;

    @Autowired
    private ICfBarcodeBindService iCfBarcodeBindService;

    @Autowired
    private ICfNextNumberService iCfNextNumberService;

    @Autowired
    private ICfPrintLodopTemplateService cfPrintLodopTemplateService;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private ICfStorageLocationService cfStorageLocationService;

    /**
     *
     * @param userId
     * @param enginePutVo
     * @return
     * @throws Exception
     */
    @Override
    @Transactional( rollbackFor = {Exception.class,RuntimeException.class} )
    public ProductionTaskVo productionPutStorage( int userId, EnginePutVo enginePutVo ) throws Exception {

        boolean cpFlag = false;
        String barCode = enginePutVo.getBarCode();
        Wrapper<CfBarcodeBind> barCodeBindEntityWrapper = new EntityWrapper<CfBarcodeBind>();
        barCodeBindEntityWrapper.eq( "car",barCode ).or().eq( "engine",barCode ).or().eq( "frame",barCode ).eq( "status","1");
        List<CfBarcodeBind> cfBarcodeBindList = iCfBarcodeBindService.selectList( barCodeBindEntityWrapper );
        String cpCode = barCode;
        if( cfBarcodeBindList.size()>0 ){
            CfBarcodeBind cfBarcodeBind = cfBarcodeBindList.get( 0 );
            cpCode = cfBarcodeBind.getCar();
            cpFlag = true;
        }
        Wrapper<CfBarcodeInventory> inventoryEntityWrapper = new EntityWrapper<CfBarcodeInventory>();
        inventoryEntityWrapper.eq( "barcode",cpCode );
        List<CfBarcodeInventory> inventoryList = cfBarcodeInventoryService.selectList( inventoryEntityWrapper );
        if( inventoryList.size() <= 0 ){
            throw new Exception( "条码不正确，请注意!" );
        }
        CfBarcodeInventory cfBarcodeInventory = inventoryList.get( 0 );
        if( cpFlag  ){ //CP条码

            BigDecimal bigDecimal = cfBarcodeInventory.getBarCodeNumber();
            if( bigDecimal==null || bigDecimal.doubleValue()==0 ){
                throw new Exception( "条码"+barCode+"已入库，请注意！" );
            }
            if( !CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER.equals( cfBarcodeInventory.getState() ) ){
                throw new Exception( "条码"+barCode+"已入库，请注意！" );
            }

        }else{ //OT码/EG
            if( !CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER.equals( cfBarcodeInventory.getState() ) ){
                throw new Exception( "条码"+barCode+"已经在库" );
        }
        }

        //条码状态更新为可用
        CfBarcodeInventory updateEntity = new CfBarcodeInventory();
        updateEntity.setState( "" );
        updateEntity.setLastUpdatedBy( userId );
        updateEntity.setLastUpdateDate( new Date() );
        updateEntity.setBarcodeInventoryId( cfBarcodeInventory.getBarcodeInventoryId() );
       // updateEntity.setWarehouse(enginePutVo.getStorageLocation());//替换仓库
        updateEntity.setStorageArea("");
        updateEntity.setWarehousePosition("");
        cfBarcodeInventoryService.updateById( updateEntity );

        //发送ERP生产订单数据
        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put( "AUFNR",cfBarcodeInventory.getProductionTaskOrder() );
        paramMap.put( "MATNR",cfBarcodeInventory.getMaterialsNo() );
        paramMap.put( "TMLX",cfBarcodeInventory.getBarcodeType() );
        paramMap.put( "GERNR",enginePutVo.getBarCode() );
        paramMap.put( "CHARG",cfBarcodeInventory.getBatchNo() );
        paramMap.put( "ERFMG",cfBarcodeInventory.getBarCodeNumber().toString() );
        paramMap.put( "LGORT",cfBarcodeInventory.getWarehouse() );
        Map<String, Object> paramNameMap = new HashMap<String, Object>();
        paramNameMap.put( "IS_DATA",paramMap );
        callParamMap.put( HandleRefConstants.PARAM_MAP, paramNameMap );
        callParamMap.put( HandleRefConstants.FUNCTION_NAME,"ZMM_BC_005" );
        R returnR = sapFeignService.executeJcoFunction( callParamMap );
        if( returnR.getCode()!=0 ){
            throw new Exception( returnR.getMsg() );
        }
        Map<String,Object> esDataMap = (Map<String, Object>) returnR.getData();
        if( (Integer) esDataMap.get( "EV_STATUS" )==0 ){
            throw new Exception( (String) esDataMap.get( "EV_MESSAGE" ) );
        }
        Map< String,Object > reDataMap = (Map<String, Object>) esDataMap.get( "ES_DATA" );
        //AUFNR==生产订单 DAUAT==订单类型 MATNR==物料代码 MAKTX==物料名称 PSMNG==任务单数量 WEMNG==已入库数量 FERTH==车型 WRKST==产品规格 KDAUF==销售订单
        //KDPOS==销售订单行项目 KUNNR==客户 ZTEXT==销售订单年份 ZHTH==合同号 LGPRO==生产仓储地点
        ProductionTaskVo productionTaskVo = new ProductionTaskVo();
        productionTaskVo.setTaskNo( (String) reDataMap.get( "AUFNR" ) );
        productionTaskVo.setOrderType( (String) reDataMap.get( "DAUAT" ) );
        productionTaskVo.setItem( (String) reDataMap.get( "MATNR" ) );
        productionTaskVo.setItemDesc( (String) reDataMap.get( "MAKTX" ) );
        productionTaskVo.setQuantity( new BigDecimal( reDataMap.get( "PSMNG" ).toString() ) );
        productionTaskVo.setReceivedQty( new BigDecimal( reDataMap.get( "WEMNG" ).toString() ) );
        productionTaskVo.setCarType( (String) reDataMap.get( "FERTH" ) );
        productionTaskVo.setMode( (String) reDataMap.get( "WRKST" ) );
        productionTaskVo.setSaleOrder( (String) reDataMap.get( "KDAUF" ) );
        productionTaskVo.setSaleOrderRowItem( (String) reDataMap.get( "KDPOS" ) );
        productionTaskVo.setCustomer( (String) reDataMap.get( "KUNNR" ) );
        productionTaskVo.setSaleOrderYear( (String) reDataMap.get( "ZTEXT" ) );
        productionTaskVo.setContract( (String) reDataMap.get( "ZHTH" ) );
        productionTaskVo.setStorageLocation( (String) reDataMap.get( "LGPRO" ) );
        return productionTaskVo;

    }

    /**
     * 成本中心退料入库标签打印
     * @param costCenterPrintInVo
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public CostCenterPrintOutVo costCenterPrint( CostCenterPrintInVo costCenterPrintInVo, int userId ) throws Exception {

        //获取打印模板内容
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "functionName", costCenterPrintInVo.getFunctionName() );
        params.put( "printLodopTemplateName", costCenterPrintInVo.getPrintLodopTemplateName() );
        CfPrintLodopTemplate getPrintLodopTemplate = cfPrintLodopTemplateService.getPrintLodopTemplate( params );
        if ( getPrintLodopTemplate==null ){
            throw new Exception( "打印模板"+costCenterPrintInVo.getPrintLodopTemplateName()+"未维护" );
        }
        String printLodopTemplate = getPrintLodopTemplate.getPrintLodopTemplate();
        if( StrUtil.isBlank( printLodopTemplate ) ){
            throw new Exception( "打印模板"+costCenterPrintInVo.getPrintLodopTemplateName()+"内容未维护" );
        }
        //保存库存条码
        int printQty = costCenterPrintInVo.getPrintQty(); //数量
        int splitQty = costCenterPrintInVo.getSplitQty(); //拆分数量
        int copies = new BigDecimal( printQty ).divide( new BigDecimal( splitQty ), RoundingMode.UP ).intValue(); //份数
        String batchNo = iCfNextNumberService.generateNextNumber( "BATCH_NO" ); //生成批次
        String barcode = null; //条码
        List<CfBarcodeInventory> barcodeList = new ArrayList<CfBarcodeInventory>();
        CfBarcodeInventory cfBarcodeInventory = null;
        for ( int i=0; i<copies; i++ ){

            //生成条码
            barcode = iCfNextNumberService.generateNextNumber( "BARCODE_NO" );
            cfBarcodeInventory = new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode( barcode );
            if( i< ( copies-1 ) ){
                cfBarcodeInventory.setBarCodeNumber( new BigDecimal( splitQty ) );
            }else {
                cfBarcodeInventory.setBarCodeNumber( new BigDecimal( ( printQty - ( splitQty*i ) ) ) );
            }

            String wareHouse = costCenterPrintInVo.getWarehouse();
            cfBarcodeInventory.setWarehouse(wareHouse);

            CfStorageLocation storageLocation = cfStorageLocationService.selectOne(new EntityWrapper<CfStorageLocation>().eq("warehouse", wareHouse));

            //设置工厂
            cfBarcodeInventory.setFactory(storageLocation.getSite());

            //状态不可用
            cfBarcodeInventory.setState( "N" );
            cfBarcodeInventory.setBatchNo( batchNo );
            cfBarcodeInventory.setBarcodeType( "OT" );
            cfBarcodeInventory.setBasicAttributeForUpdate( userId, new Date() );
            cfBarcodeInventory.setMaterialsNo( costCenterPrintInVo.getItem() );
            cfBarcodeInventory.setMaterialsName( costCenterPrintInVo.getItemDesc() );
            cfBarcodeInventory.setMode( costCenterPrintInVo.getMode() );
            //领料单号或退料单号
            cfBarcodeInventory.setProductionTaskOrder(costCenterPrintInVo.getOrderNo());
            cfBarcodeInventory.setSuppler( costCenterPrintInVo.getSuppler() );
            barcodeList.add( cfBarcodeInventory );

        }
        //保存条码
        cfBarcodeInventoryService.insertBatch( barcodeList );
        //返回数据
        CostCenterPrintOutVo costCenterPrintOutVo = new CostCenterPrintOutVo();
        costCenterPrintOutVo.setPrintLodopTemplate( printLodopTemplate );
        costCenterPrintOutVo.setBatchNo( batchNo );

        List<CostCenterPrintBarcodeVo> costCenterPrintBarcodeVoList = new ArrayList<CostCenterPrintBarcodeVo>();
        CostCenterPrintBarcodeVo costCenterPrintBarcodeVo = null;
        for ( int i=0,len=barcodeList.size(); i<len; i++  ){
            costCenterPrintBarcodeVo = new CostCenterPrintBarcodeVo();
            costCenterPrintBarcodeVo.setBarcode( barcodeList.get( i ).getBarcode() );
            costCenterPrintBarcodeVo.setQty( barcodeList.get( i ).getBarCodeNumber().toString() );
            costCenterPrintBarcodeVoList.add( costCenterPrintBarcodeVo );
        }
        costCenterPrintOutVo.setCostCenterPrintBarcodeVoList( costCenterPrintBarcodeVoList );

        return costCenterPrintOutVo;
    }

    /**
     * 获取生产任务单数据
     * @param taskNo
     * @return
     * @throws Exception
     */
    @Override
    public ProductionTaskVo getTaskNo( String taskNo ) throws Exception{

        Map<String,Object> param = new HashMap<String, Object>();
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put( "IV_AUFNR",taskNo.trim() );
        param.put( HandleRefConstants.PARAM_MAP, paramMap );
        param.put( HandleRefConstants.FUNCTION_NAME , "ZMM_BC_004" );
        R returnR = sapFeignService.executeJcoFunction( param );
        if( returnR.getCode()!=0 ){
            throw new Exception( returnR.getMsg() );
        }
        Map<String,Object> esDataMap = (Map<String, Object>) returnR.getData();
        if( (Integer) esDataMap.get( "EV_STATUS" )==0 ){
            throw new Exception( ( String )esDataMap.get( "EV_MESSAGE" ) );
        }
       String factory= esDataMap.getOrDefault("EV_WERKS","").toString();

        Map<String,Object> dataMap = (Map<String, Object>) esDataMap.get( "ES_DATA" );
        //AUFNR==生产订单 DAUAT==订单类型 MATNR==物料代码 MAKTX==物料名称 PSMNG==任务单数量 WEMNG==已入库数量 FERTH==车型 WRKST==产品规格 KDAUF==销售订单
        //KDPOS==销售订单行项目 KUNNR==客户 ZTEXT==销售订单年份 ZHTH==合同号 LGPRO==生产仓储地点
        ProductionTaskVo productionTaskVo = new ProductionTaskVo();
        productionTaskVo.setTaskNo( (String) dataMap.get( "AUFNR" ) );
        productionTaskVo.setOrderType( (String) dataMap.get( "DAUAT" ) );
        productionTaskVo.setItem( (String) dataMap.get( "MATNR" ) );
        productionTaskVo.setItemDesc( (String) dataMap.get( "MAKTX" ) );
        productionTaskVo.setQuantity( new BigDecimal( dataMap.get( "PSMNG" ).toString() ) );
        productionTaskVo.setReceivedQty( new BigDecimal( dataMap.get( "WEMNG" ).toString() ) );
        productionTaskVo.setCarType( (String) dataMap.get( "FERTH" ) );
        productionTaskVo.setMode( (String) dataMap.get( "WRKST" ) );
        productionTaskVo.setSaleOrder( (String) dataMap.get( "KDAUF" ) );
        productionTaskVo.setSaleOrderRowItem( (String) dataMap.get( "KDPOS" ) );
        productionTaskVo.setCustomer( (String) dataMap.get( "KUNNR" ) );
        productionTaskVo.setSaleOrderYear( (String) dataMap.get( "ZTEXT" ) );
        productionTaskVo.setContract( (String) dataMap.get( "ZHTH" ) );
        productionTaskVo.setStorageLocation( (String) dataMap.get( "LGPRO" ) );
        productionTaskVo.setFactory(factory);
        return productionTaskVo;
    }


}
