package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfKtmPurchaseOrder;
import com.cfmoto.bar.code.model.entity.CfPartsPurchaseOrder;
import com.cfmoto.bar.code.service.ICfPartsPurchaseOrderService;
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
 * 零部件采购订单表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@RestController
@RequestMapping("/cfPartsPurchaseOrder")
@Api(tags = "零部件采购订单表")
public class CfPartsPurchaseOrderController extends BaseController {
    @Autowired private ICfPartsPurchaseOrderService cfPartsPurchaseOrderService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfPartsPurchaseOrder
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询", notes="通过ID查询")
    public R<CfPartsPurchaseOrder> get(@RequestParam Integer id) {
        return new R<>(cfPartsPurchaseOrderService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询零部件采购订单信息", notes="分页查询零部件采购订单信息")
    public R<Page> page(@RequestParam Map<String, Object> params ,CfPartsPurchaseOrder cfPartsPurchaseOrder) {
        return new R<>(cfPartsPurchaseOrderService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfPartsPurchaseOrder)));
    }

    /**
     * 添加
     * @param  cfPartsPurchaseOrder  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加零部件采购订单", notes="添加零部件采购订单")
    public R<Boolean> add(@RequestBody CfPartsPurchaseOrder cfPartsPurchaseOrder, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfPartsPurchaseOrder.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfPartsPurchaseOrderService.insert(cfPartsPurchaseOrder));
        }catch (Exception e){
            e.printStackTrace();
            logger.info("/cfPartsPurchaseOrder/add 异常：",e);
            return new R<>(R.FAIL, CfPartsPurchaseOrder.CF_PARTS_PURCHASE_ORDER_SQL_ADD);
        }
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除零部件采购订单通过ID", notes="删除零部件采购订单通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfPartsPurchaseOrder cfPartsPurchaseOrder = new CfPartsPurchaseOrder();
        return new R<>(cfPartsPurchaseOrderService.updateById(cfPartsPurchaseOrder));
    }

    /**
     * 编辑
     * @param  cfPartsPurchaseOrder  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑零部件采购订单通", notes="编辑零部件采购订单通")
    public R<Boolean> edit(@RequestBody CfPartsPurchaseOrder cfPartsPurchaseOrder) {
        return new R<>(cfPartsPurchaseOrderService.updateById(cfPartsPurchaseOrder));
    }
}
