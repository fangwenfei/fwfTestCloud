package com.cfmoto.bar.code.controller;


import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.OrderFullVo;
import com.cfmoto.bar.code.model.vo.OrderReceiveVo;
import com.cfmoto.bar.code.model.vo.PurchaseOrOutsourceOrderVo;
import com.cfmoto.bar.code.service.*;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping( "/cfOutsource" )
@Api( tags = " 委外管理" )
@Slf4j
public class CfOutsourceManageController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ICfOrderManageService iCfOrderManageService;

    @Autowired
    private ICfStorageLocationService iCfStorageLocationService;

    @Autowired
    private ICfOrderTempService iCfOrderTempService;

    @Autowired
    private ICfOrderScanTempService iCfOrderScanTempService;

    @Autowired
    private ICfBarcodeInventoryService iCfBarcodeInventoryService;


    @GetMapping( "getStorageLocation" )
    @ApiOperation( value="获取仓库数据" )
    @ApiImplicitParam(name="storageLocation",value="仓库-模糊查询",dataType="string", paramType = "query")
    public R<List<CfStorageLocation>> getStorageLocation(String storageLocation ){

        Wrapper<CfStorageLocation> wrapper = new EntityWrapper<CfStorageLocation>();
        if ( StrUtil.isBlank( storageLocation ) ){
            storageLocation = "";
        }
        wrapper.like( "warehouse",storageLocation, SqlLike.RIGHT );
        List< CfStorageLocation > cfStorageLocationList = new ArrayList<CfStorageLocation>();
        try{
            cfStorageLocationList = iCfStorageLocationService.selectList( wrapper );
        }catch ( Exception e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R< List<CfStorageLocation> >( R.FAIL,e.getMessage() );
        }
        return new R< List< CfStorageLocation > >( cfStorageLocationList );
    }


    @GetMapping( "/getOutsourcePurchaseOrder" )
    @ApiOperation( value="获取-委外采购订单数据" )
    @ApiImplicitParam( name="orderNo",value="委外采购订单",dataType="string", paramType = "query" )
    public R< List<PurchaseOrOutsourceOrderVo> > getOutsourcePurchaseOrder( String orderNo ){

        List<PurchaseOrOutsourceOrderVo> outsourceOrderList = new ArrayList< PurchaseOrOutsourceOrderVo >();
        try {
            if( StrUtil.isBlank( orderNo ) ){
                return new R<>( R.FAIL, "委外采购订单不能为空" );
            }
            if( orderNo.trim().length()>10 ){
                return new R<List<PurchaseOrOutsourceOrderVo>>( R.FAIL, "委外出库单长度不能超过10位" );
            }
            int userId = UserUtils.getUserId( httpServletRequest );
            //Z004代表 委外物资采购订单
            outsourceOrderList = iCfOrderManageService.getOutsourcePurchaseOrder( userId, orderNo.trim(), "Z004" );
        } catch (Exception e) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R< List<PurchaseOrOutsourceOrderVo> >( R.FAIL,e.getMessage() );
        }
        return new R< List<PurchaseOrOutsourceOrderVo> >( outsourceOrderList );
    }

    @PostMapping( "/outsourceReceiveGoods" )
    @ApiOperation( value="委外采购订单-收货确认" )
    public R< List<PurchaseOrOutsourceOrderVo> > outsourceReceiveGoods( @RequestBody OrderReceiveVo orderReceiveVo ){

        List<PurchaseOrOutsourceOrderVo> outsourceOrderList = new ArrayList<PurchaseOrOutsourceOrderVo>();
        try {

            int userId = UserUtils.getUserId( httpServletRequest );
            String orderNo = orderReceiveVo.getOrderNo();
            if( StrUtil.isBlank( orderNo ) ){
                return new R<>( R.FAIL, "委外采购订单不能为空" );
            }
            if( orderNo.trim().length()>10 ){
                return new R<List<PurchaseOrOutsourceOrderVo>>( R.FAIL, "委外出库单长度不能超过10位" );
            }
            outsourceOrderList = iCfOrderManageService.outsourceReceiveGoods( userId,orderReceiveVo );
        } catch (Exception e) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R< List<PurchaseOrOutsourceOrderVo> >( R.FAIL,e.getMessage() );
        }
        return new R< List<PurchaseOrOutsourceOrderVo> >( outsourceOrderList );
    }


    @GetMapping( "/getOutSourceOrder" )
    @ApiOperation( value="获取委外出库单数据" )
    @ApiImplicitParam(name="outSourceOrder",value="委外出库单",dataType="string", paramType = "query")
    public R<OrderFullVo> getOutSourceOrderData( String outSourceOrder ){

        OrderFullVo orderFullVo = null;
        try {
            if( StrUtil.isBlank( outSourceOrder ) ){
                return new R<OrderFullVo>( R.FAIL, "委外出库单不能为空" );
            }
            if( outSourceOrder.trim().length()>10 ){
                return new R<OrderFullVo>( R.FAIL, "委外出库单长度不能超过10位" );
            }
            int userId = UserUtils.getUserId( httpServletRequest );
            orderFullVo = iCfOrderTempService.getOutSourceOutOrderData( userId,outSourceOrder.trim() );
        } catch ( Exception e ) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<OrderFullVo>( R.FAIL, e.getMessage() );
        }
        return new R<OrderFullVo>( orderFullVo );
    }

    @GetMapping( "/barcodeScan" )
    @ApiOperation( value="委外出库条码扫描" )
    @ApiImplicitParams({
            @ApiImplicitParam(name="outSourceOrder",value="委外出库单",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="barcode",value="条码",dataType="string", paramType = "query")
    })
    public R<OrderFullVo> barcodeScan( String outSourceOrder, String barcode ){

        OrderFullVo orderFullVo = null;
        try {

            int userId = UserUtils.getUserId( httpServletRequest );
            if( StrUtil.isBlank( outSourceOrder ) ){
                return new R( R.FAIL, "委外出库单不能为空" );
            }

            if( outSourceOrder.trim().length()>10 ){
                return new R<OrderFullVo>( R.FAIL, "委外出库单长度不能超过10位" );
            }

            if( StrUtil.isBlank( barcode ) ){
                return new R( R.FAIL, "条码不能为空" );
            }
            iCfOrderScanTempService.scanBarcode( userId,outSourceOrder.trim(), barcode.trim() );
            orderFullVo = iCfOrderTempService.getOutSourceOutOrderData( userId,outSourceOrder.trim() );
        } catch ( Exception e ) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R( R.FAIL, e.getMessage() );
        }
        return new R( orderFullVo );
    }

    @GetMapping( "/getBarCodeByItem" )
    @ApiOperation( value = "通过物料获取库存" )
    @ApiImplicitParam(name="item",value="物料",dataType="string", paramType = "query")
    public R< List<CfBarcodeInventory> > getBarCodeByItem( String item ){

        if( StrUtil.isBlank( item ) ){
            return new R( R.FAIL, "物料参数不能为空" );
        }
        List<CfBarcodeInventory> cfBarcodeInventoryList = null;

        EntityWrapper<CfBarcodeInventory> wrapperEntity = new EntityWrapper<CfBarcodeInventory>();
        CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
        cfBarcodeInventory.setMaterialsNo( item.trim() );
        wrapperEntity.setEntity( cfBarcodeInventory );
        try {
            cfBarcodeInventoryList = iCfBarcodeInventoryService.selectList( wrapperEntity );
        }catch ( Exception e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R( R.FAIL, e.getMessage() );
        }
        return new  R< List<CfBarcodeInventory> >( cfBarcodeInventoryList );

    }

    @PostMapping( "/ourSourceModifyRow" )
    @ApiOperation( value="委外出库修改行" )
    @ApiImplicitParam(name="paramMap",value="paramMap包含参数：orderNo 委外出库单，orderSumTempIdRef 汇总标识，orderScanTempId 扫描标识" +
            "，remarks 备注 可为空，qty 数量",dataType="string", paramType = "query")
    public R<OrderFullVo> ourSourceModifyRow ( @RequestBody Map<String,Object> paramMap ){

        OrderFullVo orderFullVo = null;
        try {

            String orderNo = (String) paramMap.get( "orderNo" );
            String orderSumTempIdRef = (String) paramMap.get( "orderSumTempIdRef" );
            String orderScanTempId = (String) paramMap.get( "orderScanTempId" );
            String remarks = (String) paramMap.get( "remarks" );
            String qty = (String) paramMap.get( "qty" );
            int userId = UserUtils.getUserId( httpServletRequest );
            if( StrUtil.isBlank( orderNo ) ){
                return new R( R.FAIL, "委外出库单不能为空" );
            }
            if( orderNo.trim().length()>10 ){
                return new R<OrderFullVo>( R.FAIL, "委外出库单长度不能超过10位" );
            }
            if( StrUtil.isBlank( orderScanTempId ) ){
                return new R( R.FAIL, "委外扫描标识不能为空" );
            }
            if( StrUtil.isBlank( orderSumTempIdRef ) ){
                return new R( R.FAIL, "汇总标识不能为空" );
            }
            if( StrUtil.isBlank( qty ) ){
                return new R( R.FAIL, "更改数量不能为空" );
            }
            iCfOrderScanTempService.ourSourceModifyRow( userId,orderNo.trim(), orderSumTempIdRef.trim(),
                    orderScanTempId.trim(), remarks.trim(), qty.trim() );
            orderFullVo = iCfOrderTempService.getOutSourceOutOrderData( userId,orderNo.trim() );
        } catch ( Exception e ) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R( R.FAIL, e.getMessage() );
        }
        return new R( orderFullVo );
    }


    @GetMapping( "/ourSourceDeleteRow" )
    @ApiOperation( value="委外出库删除行" )
    @ApiImplicitParams({
            @ApiImplicitParam( name="orderNo",value="出库单",dataType="string", paramType = "query" ),
            @ApiImplicitParam(name="orderSumTempIdRef",value="扫描标识",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="orderScanTempId",value="汇总标识",dataType="string", paramType = "query")
    })
    public R<OrderFullVo> ourSourceDeleteRow( String orderNo, String orderSumTempIdRef,String orderScanTempId ){

        OrderFullVo orderFullVo = null;
        try {

            int userId = UserUtils.getUserId( httpServletRequest );
            if( StrUtil.isBlank( orderNo ) ){
                return new R( R.FAIL, "委外出库单不能为空" );
            }
            if( orderNo.trim().length()>10 ){
                return new R<OrderFullVo>( R.FAIL, "委外出库单长度不能超过10位" );
            }
            if( StrUtil.isBlank( orderScanTempId ) ){
                return new R( R.FAIL, "委外扫描标识不能为空" );
            }
            if( StrUtil.isBlank( orderSumTempIdRef ) ){
                return new R( R.FAIL, "汇总标识不能为空" );
            }
            iCfOrderScanTempService.ourSourceDeleteRow( userId, orderNo, orderSumTempIdRef, orderScanTempId );
            orderFullVo = iCfOrderTempService.getOutSourceOutOrderData( userId,orderNo.trim() );
        } catch ( Exception e ) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<OrderFullVo>( R.FAIL, e.getMessage() );
        }
        return new R<OrderFullVo>( orderFullVo );
    }

    @GetMapping( "/outsourceSendOutGoods" )
    @ApiOperation( value="委外出库单-提交" )
    @ApiImplicitParam( name="orderNo",value="出库单",dataType="string", paramType = "query" )
    public R< OrderFullVo > outsourceSendOutGoods( String orderNo ){

        OrderFullVo orderFullVo = null;
        try {

            int userId = UserUtils.getUserId( httpServletRequest );
            if( StrUtil.isBlank( orderNo ) ){
                return new R( R.FAIL, "委外出库单不能为空" );
            }
            if( orderNo.trim().length()>10 ){
                return new R<OrderFullVo>( R.FAIL, "委外出库单长度不能超过10位" );
            }
            iCfOrderManageService.outsourceSendOutGoods( userId, orderNo.trim() );
            orderFullVo = iCfOrderTempService.getOutSourceOutOrderData( userId,orderNo.trim() );
        }catch ( Exception e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<OrderFullVo>( R.FAIL, e.getMessage() );
        }
        return new R<OrderFullVo>( orderFullVo );
    }



}
