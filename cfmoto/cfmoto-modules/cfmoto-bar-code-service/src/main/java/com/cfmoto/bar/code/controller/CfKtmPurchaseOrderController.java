package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfKtmPurchaseOrder;
import com.cfmoto.bar.code.service.ICfKtmPurchaseOrderService;
import com.github.pig.common.util.Query;
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
 * ktm订单表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@RestController
@RequestMapping("/cfKtmPurchaseOrder")
@Api(tags = "ktm订单表")
public class CfKtmPurchaseOrderController extends BaseController {
    @Autowired private ICfKtmPurchaseOrderService cfKtmPurchaseOrderService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfKtmPurchaseOrder
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询", notes="通过ID查询")
    public R<CfKtmPurchaseOrder> get(@RequestParam Integer id) {
        return new R<>(cfKtmPurchaseOrderService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询ktm订单信息", notes="分页查询ktm订单信息")
    public  R<Page> page(@RequestParam Map<String, Object> params, CfKtmPurchaseOrder cfKtmPurchaseOrder) {
        return new R<>(cfKtmPurchaseOrderService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfKtmPurchaseOrder)));
    }

    /**
     * 添加
     * @param  cfKtmPurchaseOrder  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加信息ktm订单", notes="添加信息ktm订单")
    public R<Boolean> add(@RequestBody CfKtmPurchaseOrder cfKtmPurchaseOrder , HttpServletRequest httpServletRequest) {
       try {
           int userId= UserUtils.getUserId(httpServletRequest);
           cfKtmPurchaseOrder.setObjectSetBasicAttribute(userId,new Date());
           return new R<>(cfKtmPurchaseOrderService.insert(cfKtmPurchaseOrder));
       }catch (Exception e){
           e.printStackTrace();
           logger.info("/cfKtmPurchaseOrder/add 异常：",e);
           return new R<>(R.FAIL, CfKtmPurchaseOrder.CF_KTM_PURCHASE_SQL_ADD);
       }

    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除信息ktm订单通过ID", notes="删除信息ktm订单通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfKtmPurchaseOrder cfKtmPurchaseOrder = new CfKtmPurchaseOrder();
        cfKtmPurchaseOrder.setKtmPurchaseId(id);
        cfKtmPurchaseOrder.setLastUpdateDate(new Date());
        return new R<>(cfKtmPurchaseOrderService.updateById(cfKtmPurchaseOrder));
    }

    /**
     * 编辑
     * @param  cfKtmPurchaseOrder  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑信息ktm订单", notes="编辑信息ktm订单")
    public R<Boolean> edit(@RequestBody CfKtmPurchaseOrder cfKtmPurchaseOrder) {
        return new R<>(cfKtmPurchaseOrderService.updateById(cfKtmPurchaseOrder));
    }
}
