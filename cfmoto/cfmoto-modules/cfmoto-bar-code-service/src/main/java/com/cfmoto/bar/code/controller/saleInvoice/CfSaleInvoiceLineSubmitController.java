package com.cfmoto.bar.code.controller.saleInvoice;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceLine;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceLineService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 销售发货 销售发货单子表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
@RestController
@RequestMapping("/cfSaleInvoiceLineSubmit")
@Api(tags="销售发货 销售发货单子表")
public class CfSaleInvoiceLineSubmitController extends BaseController {

    @Autowired private ICfSaleInvoiceLineService cfSaleInvoiceLineService;




    /**
     * 添加扫描数据，并更新汇总数据
     *
     */
    @PostMapping("/addCfSaleInvoiceData")
    @ApiOperation(value="添加扫描数据，并更新汇总数据")
    public R<Map<String, Object>> addCfSaleInvoiceData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfSaleInvoiceLineService.addCfSaleInvoiceData(userId,params));
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfSaleInvoiceLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 14.点击提交，校验已扫描的数据中没有铅封号的行数，则通过接口发送数据到SAP，同时扣减条码系统条码对应的数量
     * （非库存条码，则数量扣减，状态改为发货；库存条码，则数量扣减      *，删除对应单据的销售发货临时表一和二的数据；否则，报错“请输入铅封号和货柜号”
     *
     */
    @PostMapping("/submitCfSaleInvoiceData")
    @ApiOperation(value="提交销售发货数据")
    public R<Map<String, Object>> submitCfSaleInvoiceData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            cfSaleInvoiceLineService.submitCfSaleInvoiceData(userId,params);
            return new R<>(R.SUCCESS,CfSaleInvoiceLine.SUCCESS_SUBMIT);
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfSaleInvoiceLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }
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
            Page<CfSaleInvoiceLine> pages=new Page<>(page,limit);
            return new R<>(cfSaleInvoiceLineService.selectPage(pages,  new EntityWrapper<CfSaleInvoiceLine>().
                    eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId)));

        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }
    }

    /**
     * 删除
     * @param cfSaleInvoiceLine
     * @return success/false
     */
    @ApiOperation(value="删除条码数据通过Id")
    @PostMapping("/deleteBarCodeNoData")
    public R<Map<String, Object>> deleteBarCodeNoData(@RequestBody CfSaleInvoiceLine cfSaleInvoiceLine, HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfSaleInvoiceLineService.deleteBarCodeNoData(userId,cfSaleInvoiceLine));
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

}
