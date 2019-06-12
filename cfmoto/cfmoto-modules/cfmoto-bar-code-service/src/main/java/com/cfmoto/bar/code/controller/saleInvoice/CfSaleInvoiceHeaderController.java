package com.cfmoto.bar.code.controller.saleInvoice;
import java.util.Map;
import java.util.Date;

import com.cfmoto.bar.code.model.entity.CfSaleInvoiceLine;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceRoot;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceLinePickService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceHeader;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceHeaderService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 * 销售发货单 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
@RestController
@RequestMapping("/cfSaleInvoiceHeader")
@Api(tags=" 销售单")
public class CfSaleInvoiceHeaderController extends BaseController {
    @Autowired private ICfSaleInvoiceHeaderService cfSaleInvoiceHeaderService;

    @Autowired private ICfSaleInvoiceLinePickService cfSaleInvoiceLinePickService;

    /**
     * 通过单据号获取已扫描数据，汇总数据，单据头->销售发货
     *
     */
    @PostMapping("/getCfSaleInvoiceDataSubmit")
    @ApiOperation(value="通过单据号获取已扫描数据，汇总数据，单据头->销售发货")
    public R<Map<String, Object>> getCfSaleInvoiceDataSubmit(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            params.put(CfSaleInvoiceRoot.INVOICE_STATE, CfSaleInvoiceRoot.INVOICE_STATE_SUBMIT);
            return new R<>(cfSaleInvoiceHeaderService.getCfSaleInvoiceData(userId,params));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 通过单据号获取已扫描数据，汇总数据，单据头->销售退货
     *
     */
    @PostMapping("/getCfSaleInvoiceDataReturn")
    @ApiOperation(value="通过单据号获取已扫描数据，汇总数据，单据头->销售退货")
    public R<Map<String, Object>> getCfSaleInvoiceDataReturn(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            params.put(CfSaleInvoiceRoot.INVOICE_STATE, CfSaleInvoiceRoot.INVOICE_STATE_RETURN);
            return new R<>(cfSaleInvoiceHeaderService.getCfSaleInvoiceData(userId,params));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 通过单据号获取已扫描数据，汇总数据，单据头—>销售集中捡配
     *
     */
    @PostMapping("/getCfSaleInvoiceDataPick")
    @ApiOperation(value="通过单据号获取已扫描数据，汇总数据，单据头—>销售集中捡配")
    public R<Map<String, Object>> getCfSaleInvoiceDataPick(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            params.put(CfSaleInvoiceRoot.INVOICE_STATE, CfSaleInvoiceRoot.INVOICE_STATE_PICK);
            return new R<>(cfSaleInvoiceLinePickService.getCfSaleInvoiceDataPick(userId,params));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }

    }



    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfSaleInvoiceHeader
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfSaleInvoiceHeader> get(@RequestParam Integer id) {
        return new R<>(cfSaleInvoiceHeaderService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params) {
        try{
            Integer  page= Integer.parseInt(params.getOrDefault("page", 1).toString());
            Integer  limit= Integer.parseInt(params.getOrDefault("limit", QueryPage.LIMIT_10000).toString());
            Integer  invoiceRootId= Integer.parseInt(params.getOrDefault("invoiceRootId", "").toString());
            Page<CfSaleInvoiceHeader> pages=new Page<>(page,limit);
            return new R<>(cfSaleInvoiceHeaderService.selectPage(pages,  new EntityWrapper<CfSaleInvoiceHeader>().
                    eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId)));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }
    }

    /**
     * 添加
     * @param  cfSaleInvoiceHeader  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加销售发货单")
    public R<Boolean> add(@RequestBody CfSaleInvoiceHeader cfSaleInvoiceHeader,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfSaleInvoiceHeader.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfSaleInvoiceHeaderService.insert(cfSaleInvoiceHeader));
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
    @ApiOperation(value="删除销售发货单通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfSaleInvoiceHeader cfSaleInvoiceHeader = new CfSaleInvoiceHeader();
        return new R<>(cfSaleInvoiceHeaderService.updateById(cfSaleInvoiceHeader));
    }

    /**
     * 编辑
     * @param  cfSaleInvoiceHeader  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除销售发货单")
    public R<Boolean> edit(@RequestBody CfSaleInvoiceHeader cfSaleInvoiceHeader) {
        return new R<>(cfSaleInvoiceHeaderService.updateById(cfSaleInvoiceHeader));
    }
}
