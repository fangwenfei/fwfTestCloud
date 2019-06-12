package com.cfmoto.bar.code.controller;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
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
import com.cfmoto.bar.code.model.entity.CfPrintLabel;
import com.cfmoto.bar.code.service.ICfPrintLabelService;
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
@RequestMapping("/cfPrintLabel")
@Api(tags=" ")
public class CfPrintLabelController extends BaseController {
    @Autowired private ICfPrintLabelService cfPrintLabelService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfPrintLabel
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfPrintLabel> get(@RequestParam Integer id) {
        return new R<>(cfPrintLabelService.selectById(id));
    }

    /**
     * 通过功能Id获取标签数据
     *
     *
     * @return 通过功能Id获取标签数据
     */
    @PostMapping("/selectCfPrintLabelByFunctionId")
    @ApiOperation(value="通过功能Id获取标签数据")
    public R<List<CfPrintLabel>> selectCfPrintLabelByFunctionId(@RequestParam(value = "functionId", required = false) Integer functionId) {

        CfPrintLabel cfPrintLabel =new CfPrintLabel();
        cfPrintLabel.setFunctionId(functionId);
        List<CfPrintLabel> cfPrintLabelList=  cfPrintLabelService.selectList(new EntityWrapper<CfPrintLabel>(cfPrintLabel));
        return new R<>(cfPrintLabelList);
    }



    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfPrintLabel cfPrintLabel) {
        return new R<>(cfPrintLabelService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfPrintLabel)));
    }

    /**
     * 添加
     * @param  cfPrintLabel  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfPrintLabel cfPrintLabel,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfPrintLabel.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfPrintLabelService.insert(cfPrintLabel));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }

    /**
     * 删除
     * @param labelId ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除通过ID")
    public R<Boolean> delete(@RequestParam(value = "labelId", required = false) Integer labelId) {
        return new R<>(cfPrintLabelService.delete(new EntityWrapper<CfPrintLabel>().eq("label_id",labelId)));
    }

    /**
     * 编辑
     * @param  cfPrintLabel  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfPrintLabel cfPrintLabel) {
        return new R<>(cfPrintLabelService.updateById(cfPrintLabel));
    }
}
