package com.cfmoto.bar.code.controller.cecdelivergoods;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsInfo;
import com.cfmoto.bar.code.service.cecdelivergoods.ICfCecDeliverGoodsInfoService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 部品网购发货信息表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-04
 */
@RestController
@RequestMapping("/cfCecDeliverGoodsInfo")
@Api(tags=" 部品网购发货信息表")
public class CfCecDeliverGoodsInfoController extends BaseController {
    @Autowired private ICfCecDeliverGoodsInfoService cfCecDeliverGoodsInfoService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfCecDeliverGoodsInfo
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfCecDeliverGoodsInfo> get(@RequestParam Integer id) {
        return new R<>(cfCecDeliverGoodsInfoService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询部品网购发货信息表")
    public R<Page> page(@RequestParam Map<String, Object> params,CfCecDeliverGoodsInfo cfCecDeliverGoodsInfo) {
        return new R<>(cfCecDeliverGoodsInfoService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfCecDeliverGoodsInfo)));
    }

    /**
     * 添加
     * @param  cfCecDeliverGoodsInfo  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加部品网购发货信息表")
    public R<Boolean> add(@RequestBody CfCecDeliverGoodsInfo cfCecDeliverGoodsInfo,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfCecDeliverGoodsInfo.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfCecDeliverGoodsInfoService.insert(cfCecDeliverGoodsInfo));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除部品网购发货信息表通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfCecDeliverGoodsInfo cfCecDeliverGoodsInfo = new CfCecDeliverGoodsInfo();
        return new R<>(cfCecDeliverGoodsInfoService.updateById(cfCecDeliverGoodsInfo));
    }

    /**
     * 编辑
     * @param  cfCecDeliverGoodsInfo  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除部品网购发货信息表")
    public R<Boolean> edit(@RequestBody CfCecDeliverGoodsInfo cfCecDeliverGoodsInfo) {
        return new R<>(cfCecDeliverGoodsInfoService.updateById(cfCecDeliverGoodsInfo));
    }
}
