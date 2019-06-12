package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfReturnedOrder;
import com.cfmoto.bar.code.service.ICfReturnedOrderService;
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
 * 采购退货订单表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@RestController
@RequestMapping("/cfReturnedOrder")
@Api(tags = "采购退货订单表")
public class CfReturnedOrderController extends BaseController {
    @Autowired private ICfReturnedOrderService cfReturnedOrderService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfReturnedOrder
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询", notes="通过ID查询")
    public R<CfReturnedOrder> get(@RequestParam Integer id) {
        return new R<>(cfReturnedOrderService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询采购退货订单表", notes="分页查询采购退货订单表")
    public R<Page> page(@RequestParam Map<String, Object> params,CfReturnedOrder cfReturnedOrder) {
        return new R<>(cfReturnedOrderService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfReturnedOrder)));
    }

    /**
     * 添加
     * @param  cfReturnedOrder  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加采购退货订单", notes="添加采购退货订单")
    public R<Boolean> add(@RequestBody CfReturnedOrder cfReturnedOrder, HttpServletRequest httpServletRequest) {
        try {
            int userId= UserUtils.getUserId(httpServletRequest);
            cfReturnedOrder.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfReturnedOrderService.insert(cfReturnedOrder));
        }catch (Exception e){
            e.printStackTrace();
            logger.info("/cfReturnedOrder/add 异常：",e);
            return new R<>(R.FAIL, CfReturnedOrder.CF_RETURNED_ORDER_SQL_ADD);
        }

    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除采购退货订单通过ID", notes="删除采购退货订单通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfReturnedOrder cfReturnedOrder = new CfReturnedOrder();
        return new R<>(cfReturnedOrderService.updateById(cfReturnedOrder));
    }

    /**
     * 编辑
     * @param  cfReturnedOrder  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑采购退货订单", notes="编辑采购退货订单")
    public R<Boolean> edit(@RequestBody CfReturnedOrder cfReturnedOrder) {
        return new R<>(cfReturnedOrderService.updateById(cfReturnedOrder));
    }
}
