package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfWorkProductionMap;
import com.cfmoto.bar.code.model.vo.OperationWorkVo;
import com.cfmoto.bar.code.service.workProductionMap.ICfWorkProductionMapService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-10
 */
@RestController
@RequestMapping("/cfWorkProductionMap")
@Api(tags=" 线体报工")
public class CfWorkProductionMapController extends BaseController {
    @Autowired private ICfWorkProductionMapService cfWorkProductionMapService;

    /**
     * 查询所有线体
     *
     *
     * @return SelectList
     */
    @PostMapping("/selectWorkProductionMapByAll")
    @ApiOperation(value="查询所有销线体")
    public R<List<SelectList>> selectAllSalesOrderNo() {
        try {
            return new R<>(cfWorkProductionMapService.selectWorkProductionMapByAll());
        }catch (Exception e){
            return new R<>(R.FAIL, CfWorkProductionMap.CF_WORK_PRODUCTION_MAP_EX);
        }

    }


    /**
     * 查询所有线体的详细信息
     *
     *
     * @return SelectList
     */
    @PostMapping("/selectAllDataByAllProduction")
    @ApiOperation(value="查询所有线体的详细信息")
    public R<List<CfWorkProductionMap>> selectAllDataByAllProduction(@RequestBody CfWorkProductionMap cfWorkProductionMap) {
        try {
            return new R<>(cfWorkProductionMapService.selectList(new EntityWrapper<CfWorkProductionMap>(cfWorkProductionMap)));
        }catch (Exception e){
            return new R<>(R.FAIL, CfWorkProductionMap.CF_WORK_PRODUCTION_MAP_EX);
        }

    }

    /**
     *
     *
     *
     * @return OperationWorkVo
     */
    @PostMapping("/submitAllDataThreeBarCode")
    @ApiOperation(value="三码报工提交")
    @ApiImplicitParams({
            @ApiImplicitParam( name="barCode",value="确认码",dataType="string", paramType = "query" )
    })
    public R<OperationWorkVo> submitAllDataThreeBarCode(String barCode, HttpServletRequest request) {
        try {
            return new R<>(cfWorkProductionMapService.submitAllDataThreeBarCode(barCode,CfWorkProductionMap.WORK_NO_0010,request));
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL,e.getMessage());
        }

    }

    /**
     *
     *
     *
     * @return OperationWorkVo
     */
    @PostMapping("/submitAllData")
    @ApiOperation(value="序列号报工提交")
    @ApiImplicitParams({
            @ApiImplicitParam( name="barCode",value="确认码",dataType="string", paramType = "query" ),
            @ApiImplicitParam( name="workNo",value="报工序列号",dataType="string", paramType = "query" )
    })
    public R<OperationWorkVo> submitAllData(String barCode, String workNo , HttpServletRequest request) {
        try {
            return new R<>(cfWorkProductionMapService.submitAllData(barCode,workNo,request));
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL,e.getMessage());
        }

    }

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfWorkProductionMap
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfWorkProductionMap> get(@RequestParam Integer id) {
        return new R<>(cfWorkProductionMapService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfWorkProductionMap cfWorkProductionMap) {

        EntityWrapper entityWrapper=  new EntityWrapper<>();
        entityWrapper.like(StringUtils.isNotBlank(cfWorkProductionMap.getMark()),"mark",cfWorkProductionMap.getMark())
                .like(StringUtils.isNotBlank(cfWorkProductionMap.getProductionName()),"production_name",cfWorkProductionMap.getProductionName())
                .like(StringUtils.isNotBlank(cfWorkProductionMap.getWorkNo()),"work_no",cfWorkProductionMap.getWorkNo());

        return new R<>(cfWorkProductionMapService.selectPage(new QueryPage<>(params), entityWrapper));
    }

    /**
     * 添加
     * @param  cfWorkProductionMap  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfWorkProductionMap cfWorkProductionMap,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfWorkProductionMap.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfWorkProductionMapService.insert(cfWorkProductionMap));
       }catch (Exception e){
            if(e.getMessage().contains("MySQLIntegrityConstraintViolationException")){
                return new R<>(R.FAIL, "存在相同的线体名称和报工序号组合");
            }else{
                return new R<>(R.FAIL, e.getMessage() );
            }

        }
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        return new R<>(cfWorkProductionMapService.deleteById(id));
    }

    /**
     * 编辑
     * @param  cfWorkProductionMap  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfWorkProductionMap cfWorkProductionMap) {
        try{
            return new R<>(cfWorkProductionMapService.updateById(cfWorkProductionMap));
        }catch (Exception e){
            if(e.getMessage().contains("MySQLIntegrityConstraintViolationException")){
                return new R<>(R.FAIL, "存在相同的线体名称和报工序号组合");
            }else{
                return new R<>(R.FAIL, e.getMessage() );
            }
        }

    }
}
