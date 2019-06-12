package com.cfmoto.bar.code.controller;
import java.util.Map;
import java.util.Date;
import com.github.pig.common.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pig.common.constant.CommonConstant;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.model.entity.CfPrintFunction;
import com.cfmoto.bar.code.service.ICfPrintFunctionService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 * 模板功能表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-08
 */
@RestController
@RequestMapping("/cfPrintFunction")
@Api(tags=" 模板功能表")
public class CfPrintFunctionController extends BaseController {
    @Autowired private ICfPrintFunctionService cfPrintFunctionService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfPrintFunction
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfPrintFunction> get(@RequestParam Integer id) {
        return new R<>(cfPrintFunctionService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询模板功能表")
    public R<Page> page(@RequestParam Map<String, Object> params,CfPrintFunction cfPrintFunction) {
        return new R<>(cfPrintFunctionService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfPrintFunction)));
    }

    /**
     * 添加
     * @param  cfPrintFunction  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加模板功能表")
    public R<Boolean> add(@RequestBody CfPrintFunction cfPrintFunction,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfPrintFunction.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfPrintFunctionService.insert(cfPrintFunction));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }

    /**
     * 删除
     * @param
     * @return success/false
     */
    @PostMapping("/deleteCfPrintFunctionById")
    @ApiOperation(value="删除模板功能表通过ID")
    public R<Boolean> delete(@RequestParam(value = "functionId", required = false) Integer functionId) {
        try{
            return new R<>(R.SUCCESS,cfPrintFunctionService.deleteCfPrintFunctionById(functionId));
        }catch (Exception e ){
            return new R<>(R.FAIL,CfPrintFunction.CF_PRINT_FUNCTION_FAIL);
        }

    }

    /**
     * 编辑
     * @param  cfPrintFunction  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除模板功能表")
    public R<Boolean> edit(@RequestBody CfPrintFunction cfPrintFunction) {
        return new R<>(cfPrintFunctionService.updateById(cfPrintFunction));
    }
}
