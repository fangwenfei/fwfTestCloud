package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.mapper.CfPurchaseReceiptMapper;
import com.cfmoto.bar.code.model.entity.CfPurchaseReceipt;
import com.cfmoto.bar.code.service.ICfPurchaseReceiptService;
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
 * 采购收货表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@RestController
@RequestMapping("/cfPurchaseReceipt")
@Api(tags = "采购收货表")
public class CfPurchaseReceiptController extends BaseController {
    @Autowired private ICfPurchaseReceiptService cfPurchaseReceiptService;

    @Autowired
    private CfPurchaseReceiptMapper cfPurchaseReceiptMapper ;
    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfPurchaseReceipt
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询", notes="通过ID查询")
    public R<CfPurchaseReceipt> get(@RequestParam Integer id) {
        return new R<>(cfPurchaseReceiptService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询采购收货表", notes="分页查询采购收货表")
    public R<Page> page(@RequestParam Map<String, Object> params,CfPurchaseReceipt cfPurchaseReceipt) {
        return new R<>(cfPurchaseReceiptService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfPurchaseReceipt)));
    }

    /**
     * 添加
     * @param  cfPurchaseReceipt  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加采购收货表", notes="添加采购收货表")
    public R<Boolean> add(@RequestBody CfPurchaseReceipt cfPurchaseReceipt, HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfPurchaseReceipt.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfPurchaseReceiptService.insert(cfPurchaseReceipt));
        }catch (Exception e){
            e.printStackTrace();
            logger.info("/cfPurchaseReceipt/add 异常：",e);
            return new R<>(R.FAIL, CfPurchaseReceipt.CF_PURCHASE_RECEIPT_SQL_ADD);
        }

    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除采购收货表通过ID", notes="删除采购收货表通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfPurchaseReceipt cfPurchaseReceipt = new CfPurchaseReceipt();
        return new R<>(cfPurchaseReceiptService.updateById(cfPurchaseReceipt));
    }

    /**
     * 编辑
     * @param  cfPurchaseReceipt  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑采购收货表", notes="编辑采购收货表")
    public R<Boolean> edit(@RequestBody CfPurchaseReceipt cfPurchaseReceipt) {
        return new R<>(cfPurchaseReceiptService.updateById(cfPurchaseReceipt));
    }
}
