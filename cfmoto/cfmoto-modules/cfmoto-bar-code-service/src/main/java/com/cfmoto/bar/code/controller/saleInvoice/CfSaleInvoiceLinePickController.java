package com.cfmoto.bar.code.controller.saleInvoice;

import com.cfmoto.bar.code.model.entity.CfSaleInvoiceLine;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceLinePickService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 销售集中捡配（国内整车）（PDA）
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
@RestController
@RequestMapping("/cfSaleInvoiceLinePick")
@Api(tags="销售集中捡配（国内整车）（PDA）")
public class CfSaleInvoiceLinePickController extends BaseController {



    @Autowired private ICfSaleInvoiceLinePickService cfSaleInvoiceLinePickService;



    /**
     * 添加扫描数据，并更新汇总数据
     *
     */
    @PostMapping("/addCfSaleInvoiceData")
    @ApiOperation(value="添加扫描数据，并更新汇总数据")
    public R<Map<String, Object>> addCfSaleInvoiceData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfSaleInvoiceLinePickService.addCfSaleInvoiceData(userId,params));
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
    @ApiOperation(value="提交销售集中捡配数据")
    public R<Map<String, Object>> submitCfSaleInvoiceData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            cfSaleInvoiceLinePickService.submitCfSaleInvoiceData(userId,params);
            return new R<>(R.SUCCESS,CfSaleInvoiceLine.SUCCESS_SUBMIT);
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfSaleInvoiceLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

}