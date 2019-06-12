package com.cfmoto.bar.code.controller;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.cfmoto.bar.code.model.entity.CfPrintLabel;
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
import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.service.ICfPrintLodopTemplateService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-08
 */
@RestController
@RequestMapping("/cfPrintLodopTemplate")
@Api(tags=" ")
public class CfPrintLodopTemplateController extends BaseController {
    @Autowired private ICfPrintLodopTemplateService cfPrintLodopTemplateService;

    /**
     *通过功能模块名或者模板名获取模板
     * @param params
     * @return
     */
    @PostMapping("/getPrintLodopTemplate")
    @ApiOperation(value="通过功能模块名或者模板名获取模板")
    public  R<CfPrintLodopTemplate> getPrintLodopTemplate(@RequestBody Map<String, Object> params){
        try{
            return new R<>(cfPrintLodopTemplateService.getPrintLodopTemplate(params)) ;
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }

    }
    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfPrintLodopTemplate
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfPrintLodopTemplate> get(@RequestParam Integer id) {
        return new R<>(cfPrintLodopTemplateService.selectById(id));
    }

    /**
     * 通过功能Id获取模板数据
     *
     * @return 通过功能Id获取模板数据
     */
    @PostMapping("/selectCfPrintTemplateByFunctionId")
    @ApiOperation(value="通过功能Id获取模板数据")
    public R<List<CfPrintLodopTemplate>> selectCfPrintTemplateByFunctionId(@RequestParam(value = "functionId", required = false) Integer functionId) {
        CfPrintLodopTemplate cfPrintLodopTemplate =new CfPrintLodopTemplate();
        cfPrintLodopTemplate.setFunctionId(functionId);
        List<CfPrintLodopTemplate> cfPrintLodopTemplateList=  cfPrintLodopTemplateService.selectList(new EntityWrapper<CfPrintLodopTemplate>(cfPrintLodopTemplate));
        return new R<>(cfPrintLodopTemplateList);
    }

    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfPrintLodopTemplate cfPrintLodopTemplate) {
        return new R<>(cfPrintLodopTemplateService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfPrintLodopTemplate)));
    }

    @PostMapping("/setCfCheck")
    @ApiOperation(value="修改默认选择")
    public R<Boolean> setCfCheck(@RequestBody  CfPrintLodopTemplate cfPrintLodopTemplate,HttpServletRequest httpServletRequest) {
        int userId= UserUtils.getUserId(httpServletRequest);
        return new R<>(cfPrintLodopTemplateService.setCfCheck(cfPrintLodopTemplate,userId));
    }

    /**
     * 添加
     * @param  cfPrintLodopTemplate  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfPrintLodopTemplate cfPrintLodopTemplate,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfPrintLodopTemplate.setObjectSetBasicAttribute(userId,new Date());
            cfPrintLodopTemplate.setCfCheck(CfPrintLodopTemplate.FALSE_SQL);
            return new R<>(cfPrintLodopTemplateService.insert(cfPrintLodopTemplate));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }

    /**
     * 删除
     * @param printLodopId ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除通过ID")
    public R<Boolean> delete(@RequestParam(value = "printLodopId", required = false) Integer printLodopId) {
        return new R<>(cfPrintLodopTemplateService.delete(new EntityWrapper<CfPrintLodopTemplate>().eq("print_lodop_id",printLodopId)));
    }

    /**
     * 编辑
     * @param  cfPrintLodopTemplate  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfPrintLodopTemplate cfPrintLodopTemplate) {
        return new R<>(cfPrintLodopTemplateService.updateById(cfPrintLodopTemplate));
    }
}
