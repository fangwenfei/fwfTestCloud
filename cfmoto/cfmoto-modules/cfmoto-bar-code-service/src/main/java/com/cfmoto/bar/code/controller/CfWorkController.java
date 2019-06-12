package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.model.entity.CfWork;
import com.cfmoto.bar.code.model.vo.OperationWorkVo;
import com.cfmoto.bar.code.service.ICfWorkService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-18
 */
@RestController
@RequestMapping("/cfWork")
@Api(tags = " 报工接口")
public class CfWorkController extends BaseController {

    @Autowired
    private ICfWorkService cfWorkService;

    @Autowired
    private SapFeignService sapFeignService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfWork
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询", notes="通过ID查询")
    public R<CfWork> get(@RequestParam Integer id) {
        return new R<>(cfWorkService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询报工信息", notes="分页查询报工信息")
    public R<Page> page(@RequestParam Map<String, Object> params,CfWork cfWork) {
        return new R<>(cfWorkService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfWork)));
    }

    /**
     * 添加
     * @param  cfWork  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加信息报工", notes="添加信息报工")
    public R<Boolean> add(@RequestBody CfWork cfWork, HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfWork.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfWorkService.insert(cfWork));
        }catch (Exception e){
            e.printStackTrace();
            logger.info("/cfWork/add 异常：",e);
            return new R<>(R.FAIL, CfWork.CF_WORK_SQL_ADD);
        }
    }


    /**
     * 验证确认码
     * @return
     */
    @GetMapping( "/scanBarcode" )
    @ApiOperation( value="扫描确认码" )
    @ApiImplicitParam( name="barCode",value="确认码",dataType="string", paramType = "query" )
    public R<OperationWorkVo> scanBarcode( String barCode,HttpServletRequest httpServletRequest ){

        if( StrUtil.isBlank( barCode ) ){
            return new R<OperationWorkVo>( R.FAIL,"确认码不能为空" );
        }

        Pattern pattern = Pattern.compile( "^\\w+-\\w+" );
        Matcher matcher = pattern.matcher( barCode.trim() );
        if ( !matcher.matches() ) {
            return new R<OperationWorkVo>( R.FAIL, "条码规则：生产任务单-工序编号，列如：TASK00001-OP" );
        }
        String[] barCodes = barCode.trim().split( "-" );
        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put( "IV_AUFNR",barCodes[0] ); //订单号
        paramMap.put( "IV_VORNR",barCodes[1] ); //操作/活动编号
        paramMap.put( "IV_STATU","0" ); //标识码（0：返回未清数量；1：报工操作）
        callParamMap.put( HandleRefConstants.FUNCTION_NAME, "ZPP_BC_001" );
        callParamMap.put( HandleRefConstants.PARAM_MAP, paramMap );
        OperationWorkVo operationWorkVo = null;
        try{
            R returnR = sapFeignService.executeJcoFunction( callParamMap );
            if( returnR.getCode()!=0 ){
                return new R<OperationWorkVo>( R.FAIL, returnR.getMsg() );
            }
            Map<String, Object> dataMap = (Map<String, Object>) returnR.getData();
            if( (Integer) dataMap.get( "EV_STATUS" )==0 ){
                return new R<OperationWorkVo>( R.FAIL,(String) dataMap.get( "EV_MESSAGE" ) );
            }
            operationWorkVo = new OperationWorkVo();
            operationWorkVo.setTaskNo( barCodes[0] );
            operationWorkVo.setOperation( barCodes[1] );
            operationWorkVo.setOperationUnclearQty( dataMap.get( "EV_LMNGA" ).toString() );
        }catch ( Exception e ){
            return new R<OperationWorkVo>( R.FAIL, e.getMessage() );
        }
        return new R<OperationWorkVo>( operationWorkVo );
    }


    /**
     * 确认报工
     * @return
     */
    @GetMapping( "/confirmOperation" )
    @ApiOperation( value="确认报工" )
    @ApiImplicitParams({
            @ApiImplicitParam( name="barCode",value="确认码",dataType="string", paramType = "query" ),
            @ApiImplicitParam( name="workQty",value="报工数量",dataType="string", paramType = "query" ),
            @ApiImplicitParam( name="unclearQty",value="工废数量",dataType="string", paramType = "query" )
    })
    public R<OperationWorkVo> confirmOperation( String barCode,String workQty,String unclearQty,HttpServletRequest httpServletRequest ){

        if( StrUtil.isBlank( barCode ) ){
            return new R<OperationWorkVo>( R.FAIL,"确认码不能为空" );
        }
        if( StrUtil.isBlank( workQty ) && StrUtil.isBlank( unclearQty ) ){
            return new R<OperationWorkVo>( R.FAIL,"报工数量、工废数量至少一项不能为空" );
        }
        Pattern pattern = Pattern.compile( "^\\w+-\\w+" );
        Matcher matcher = pattern.matcher( barCode.trim() );
        if ( !matcher.matches() ) {
            return new R<OperationWorkVo>( R.FAIL, "条码规则：生产任务单-工序编号，列如：TASK00001-OP" );
        }
        OperationWorkVo operationWorkVo = null;
        try{

            int userId= UserUtils.getUserId(httpServletRequest);
            String[] barCodes = barCode.trim().split( "-" );
            Map<String, Object> callParamMap = new HashMap<String, Object>();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put( "IV_AUFNR",barCodes[0] ); //订单号
            paramMap.put( "IV_VORNR",barCodes[1] ); //操作/活动编号
            if( !StrUtil.isBlank( workQty ) ){
                paramMap.put( "IV_LMNGA",workQty ); //报工数量
            }
            if( !StrUtil.isBlank( unclearQty ) ){
                paramMap.put( "IV_XMNGA",unclearQty ); //工废数量
            }
            paramMap.put( "IV_STATU","1" ); //标识码（0：返回未清数量；1：报工操作）
            callParamMap.put( HandleRefConstants.FUNCTION_NAME, "ZPP_BC_001" );
            callParamMap.put( HandleRefConstants.PARAM_MAP, paramMap );
            R returnR = sapFeignService.executeJcoFunction( callParamMap );
            if( returnR.getCode()!=0 ){
                return new R<OperationWorkVo>( R.FAIL, returnR.getMsg() );
            }
            Map<String, Object> dataMap = (Map<String, Object>) returnR.getData();
            if( (Integer) dataMap.get( "EV_STATUS" )==0 ){
                return new R<OperationWorkVo>( R.FAIL,(String) dataMap.get( "EV_MESSAGE" ) );
            }
            operationWorkVo = new OperationWorkVo();
            operationWorkVo.setTaskNo( barCodes[0] );
            operationWorkVo.setOperation( barCodes[1] );
            operationWorkVo.setOperationUnclearQty( dataMap.get( "EV_LMNGA" ).toString() );
        }catch ( Exception e ){
            return new R<OperationWorkVo>( R.FAIL, e.getMessage() );
        }
        return new R<OperationWorkVo>( operationWorkVo );
    }


    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除报工通过ID", notes="删除报工通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfWork cfWork = new CfWork();
        return new R<>(cfWorkService.updateById(cfWork));
    }

    /**
     * 编辑
     * @param  cfWork  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑信息报工", notes="编辑信息报工")
    public R<Boolean> edit(@RequestBody CfWork cfWork) {
        return new R<>(cfWorkService.updateById(cfWork));
    }
}
