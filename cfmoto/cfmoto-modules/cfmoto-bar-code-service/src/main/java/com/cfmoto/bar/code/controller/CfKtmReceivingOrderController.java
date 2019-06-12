package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfKtmReceivingOrder;
import com.cfmoto.bar.code.service.ICfKtmReceivingOrderService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@RestController
@RequestMapping("/cfKtmReceivingOrder")
@Api(tags = "KTM采购收货表")
public class CfKtmReceivingOrderController extends BaseController {
    @Autowired private ICfKtmReceivingOrderService cfKtmReceivingOrderService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfKtmReceivingOrder
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询", notes="通过ID查询")
    public R<CfKtmReceivingOrder> get(@RequestParam Integer id) {
        return new R<>(cfKtmReceivingOrderService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询采购收货信息", notes="分页查询ktm采购收货信息")
    public R<Page> page(@RequestParam Map<String, Object> params,CfKtmReceivingOrder cfKtmReceivingOrder) {
        return new R<>(cfKtmReceivingOrderService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfKtmReceivingOrder)));
    }

    /**
     * 添加
     * @param  cfKtmReceivingOrder  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加信息ktm采购收货", notes="添加信息ktm采购收货")
    public R<Boolean> add(@RequestBody CfKtmReceivingOrder cfKtmReceivingOrder, HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfKtmReceivingOrder.setObjectSetBasicAttribute(userId,new Date());
            cfKtmReceivingOrder.setBarCodeNumber(new BigDecimal(1));
            return new R<>(cfKtmReceivingOrderService.insert(cfKtmReceivingOrder));
        }catch (Exception e){
            e.printStackTrace();
            logger.info("/cfKtmReceivingOrder/add 异常：",e);
            return new R<>(R.FAIL, CfKtmReceivingOrder.CF_KTM_RECEIVING_ORDER_SQL_ADD);

        }
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除信息ktm采购收货通过ID", notes="删除信息ktm采购收货通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfKtmReceivingOrder cfKtmReceivingOrder = new CfKtmReceivingOrder();
        return new R<>(cfKtmReceivingOrderService.updateById(cfKtmReceivingOrder));
    }

    /**
     * 编辑
     * @param  cfKtmReceivingOrder  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑信息ktm采购收货订单", notes="编辑信息ktm采购收货订单")
    public R<Boolean> edit(@RequestBody CfKtmReceivingOrder cfKtmReceivingOrder) {
        return new R<>(cfKtmReceivingOrderService.updateById(cfKtmReceivingOrder));
    }
}
