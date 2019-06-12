package com.cfmoto.bar.code.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfCftRelationship;
import com.cfmoto.bar.code.service.ICfCftRelationshipService;
import com.cfmoto.bar.code.utiles.ExcelUtiles;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车架与车型对应表 前端控制器
 * </p>
 *
 * @author space
 * @since 2019-02-26
 */
@RestController
@RequestMapping("/cfCftRelationship")
@Api(tags=" 车架与车型对应表")
@Slf4j
public class CfCftRelationshipController extends BaseController {

    @Autowired
    private ICfCftRelationshipService cfCftRelationshipService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfCftRelationship
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfCftRelationship> get(@RequestParam Integer id) {
        return new R<>(cfCftRelationshipService.selectById(id));
    }

    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询车架与车型对应表")
    public R<Page> page( @RequestBody Map<String, Object> params ) {

        EntityWrapper< CfCftRelationship > entityWrapper = new EntityWrapper< CfCftRelationship >();
        entityWrapper.like( "car_frame",(String)params.get( "field" ) ).or()
                .like( "car_type",(String)params.get( "field" )).or()
                .like( "remarks",(String)params.get( "field" ) );
        return new R<>(cfCftRelationshipService.selectPage(new QueryPage<>(params), entityWrapper ) );

    }

    /**
     * 添加
     * @param  cfCftRelationship  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加车架与车型对应表")
    public R<Boolean> add(@RequestBody CfCftRelationship cfCftRelationship,HttpServletRequest httpServletRequest) {
        try{

            int userId= UserUtils.getUserId( httpServletRequest );
            cfCftRelationship.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfCftRelationshipService.insert(cfCftRelationship));

       }catch (Exception e){

            log.error( ExceptionUtils.getFullStackTrace( e ) );
            if( e.getMessage().contains( "MySQLIntegrityConstraintViolationException" ) ){
                return new R<>(R.FAIL, "车架代码已维护" );
            }
            return new R<>(R.FAIL, e.getMessage() );
        }

    }


    /**
     *
     * @param file
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/importExcel")
    @ApiOperation(value="导入")
    public R<String> importExcel( @RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest ){
        try {

            int userId= UserUtils.getUserId( httpServletRequest );
            List<CfCftRelationship> cfCftRelationshipList = ExcelUtiles.importExcel(file,1,1,CfCftRelationship.class );
            CfCftRelationship cftRelationship = null;
            for( int i=0,len=cfCftRelationshipList.size(); i<len; i++ ){
                cftRelationship = cfCftRelationshipList.get( i );
                if(StrUtil.isBlank( cftRelationship.getCarFrame() ) ){
                    throw new Exception( "第"+( i+3 )+"行数据车架不能为空" );
                }
                if(StrUtil.isBlank( cftRelationship.getCarType() ) ){
                    throw new Exception( "第"+( i+3 )+"行数据车型不能为空" );
                }
                cftRelationship.setObjectSetBasicAttribute( userId, new Date() );
            }
            cfCftRelationshipService.customInsertOrSaveBatch( cfCftRelationshipList );

        }catch ( Exception e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<>(R.FAIL, e.getMessage() );
        }
        return new R<>( "上传成功" );
    }


    /**
     *
     * @param pVal
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation( value="导出" )
    public void export( String pVal, HttpServletResponse response ){
        //模拟从数据库获取需要导出的数据
        EntityWrapper< CfCftRelationship > entityWrapper = new EntityWrapper< CfCftRelationship >();
        entityWrapper.like( "car_frame",pVal ).or()
                .like( "car_type",pVal ).or()
                .like( "remarks",pVal );
        List<CfCftRelationship> cfCftRelationshipList = cfCftRelationshipService.selectList( entityWrapper );
        //导出操作
        ExcelUtiles.exportExcel( cfCftRelationshipList,"车架与车型对应关系维护","车架与车型对应关系维护",
                CfCftRelationship.class,"车架与车型对应关系.xls",response );
    }



    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除车架与车型对应表通过ID")
    public R<Boolean> delete( @RequestParam Integer id ) {
        try {

            if( id==null ){
                throw new Exception( "id不能为空" );
            }
            CfCftRelationship cfCftRelationship = cfCftRelationshipService.selectById( id );
            if ( cfCftRelationship==null ){
                throw new Exception( "删除数据不存在" );
            }
            cfCftRelationshipService.deleteById( id );
        }catch ( Exception e ){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<>(R.FAIL, e.getMessage() );
        }
        return new R<>( R.SUCCESS, "删除成功" );
    }

    /**
     * 编辑
     * @param  cfCftRelationship  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除车架与车型对应表")
    public R<Boolean> edit(@RequestBody CfCftRelationship cfCftRelationship) {
        return new R<>(cfCftRelationshipService.updateById(cfCftRelationship));
    }
}
