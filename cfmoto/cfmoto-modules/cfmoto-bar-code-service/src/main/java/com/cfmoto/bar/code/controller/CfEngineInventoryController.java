package com.cfmoto.bar.code.controller;


import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.EnginePutVo;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfCustomService;
import com.cfmoto.bar.code.service.ICfStorageLocationService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cfEngineInventory")
@Api( tags=" 发动机关联入库" )
@Slf4j
public class CfEngineInventoryController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ICfStorageLocationService iCfStorageLocationService;

    @Autowired
    private ICfBarcodeInventoryService iCfBarcodeInventoryService;

    @Autowired
    private ICfCustomService iCfCustomService;

    @GetMapping( "/getTaskNo" )
    @ApiOperation( value="获取生产任务单" )
    @ApiImplicitParam(name="taskNo",value="生产任务单",dataType="string", paramType = "query")
    public R<ProductionTaskVo> getTaskNo( String taskNo ){

        ProductionTaskVo productionTaskVo = null;
        try {

            if( StrUtil.isBlank( taskNo ) ){
                throw new Exception( "生产任务单不能为空" );
            }
            if( taskNo.trim().length() >12 ){
                throw new Exception( "生产任务单长度不能超过12个字符" );
            }
            productionTaskVo = iCfCustomService.getTaskNo( taskNo.trim() );

        }catch ( Exception e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<ProductionTaskVo>( R.FAIL,e.getMessage() );
        }
        return new R<ProductionTaskVo>( productionTaskVo );
    }


    @GetMapping( "getStorageLocation" )
    @ApiOperation( value="获取仓库数据" )
    @ApiImplicitParam(name="storageLocation",value="仓库-模糊查询",dataType="string", paramType = "query")
    public R<List< CfStorageLocation >> getStorageLocation( String storageLocation ){

        Wrapper<CfStorageLocation> wrapper = new EntityWrapper<CfStorageLocation>();
        if( StrUtil.isBlank( storageLocation ) ){
            storageLocation = "";
        }
        wrapper.like( "warehouse",storageLocation, SqlLike.RIGHT );

        List< CfStorageLocation > cfStorageLocationList = iCfStorageLocationService.selectList( wrapper );
        return new R<List< CfStorageLocation >>( cfStorageLocationList );
    }


    @PostMapping( "doPutStorage" )
    @ApiOperation( value="发动机入库" )
    public R<ProductionTaskVo> enginePutStorage( @RequestBody EnginePutVo enginePutVo ){

        if( StrUtil.isBlank( enginePutVo.getTaskNo() ) ){
            return new R<ProductionTaskVo>( R.FAIL,"生产任务单不能为空" );
        }
        if( StrUtil.isBlank( enginePutVo.getStorageLocation() ) ){
            return new R<ProductionTaskVo>( R.FAIL,"仓库不能为空" );
        }
        if( StrUtil.isBlank( enginePutVo.getBarCode() ) ){
            return new R<ProductionTaskVo>( R.FAIL,"发送机条码不能为空" );
        }
        ProductionTaskVo productionTaskVo = null;
        try {
            int userId = UserUtils.getUserId( httpServletRequest );
            productionTaskVo = iCfBarcodeInventoryService.engineWarehousing( userId, enginePutVo );
        } catch (Exception e) {
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<ProductionTaskVo>( R.FAIL, e.getMessage() );
        }
        return new R<ProductionTaskVo>( productionTaskVo );

    }


    /*
      拆分打印生产入库标签打印（非整车、发动机）（PC）
     */
    @PostMapping("/splitDataProducePrintByParam")
    @ApiOperation(value = "通过拆分进行打印及时库存打印")
    public R<List<CfBarcodeInventory>> splitDataProducePrintByParam(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId= UserUtils.getUserId(httpServletRequest);
            List<CfBarcodeInventory> result= iCfBarcodeInventoryService .splitDataProducePrintByParam(params,userId);
            return new R<>(result);
        }catch (Exception e){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /*
     拆分打印生产入库标签打印（整车、发动机）（PC）
    */
    @PostMapping("/splitDataProducePrintByParamCP")
    @ApiOperation(value = "通过拆分进行打印及时库存打印")
    public R<List<CfBarcodeInventory>> splitDataProducePrintByParamCP(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId= UserUtils.getUserId(httpServletRequest);
            List<CfBarcodeInventory> result= iCfBarcodeInventoryService .splitDataProducePrintByParamCP(params,userId);
            return new R<>(result);
        }catch (Exception e){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<>(R.FAIL, e.getMessage());
        }
    }

}
