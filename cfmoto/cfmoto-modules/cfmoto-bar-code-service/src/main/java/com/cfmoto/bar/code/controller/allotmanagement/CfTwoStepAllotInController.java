package com.cfmoto.bar.code.controller.allotmanagement;

import cn.hutool.core.util.StrUtil;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotManagementCommonService;
import com.cfmoto.bar.code.service.allotmanagement.ICfTwoStepAllotInService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 两步调拨入库——前端控制器
 *
 * @author ye
 */
@RestController
@RequestMapping("allot/twoStep/allotIn")
@Api(tags = " 两步调拨入库")
@Log4j
public class CfTwoStepAllotInController {

    @Autowired
    private ICfAllotManagementCommonService cfAllotManagementCommonService;

    @Autowired
    private ICfTwoStepAllotInService twoStepAllotInService;


    /**
     * 二步调拨入库的扫描条码功能
     *
     * @param barcode     条码
     * @param barcodeType 条码类型(1:库存条码/2:物料条码)
     * @param orderNo     调拨单号
     * @return r
     */
    @PostMapping("scanBarcode")
    @ApiOperation(value = "扫描条码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barcodeType", value = "条码类型(1:库存条码/2:物料条码)", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
    })
    public R<CfAllotManagementVo> scanBarcode(String barcode, String barcodeType, String orderNo, HttpServletRequest request) {
        //创建R对象
        R<CfAllotManagementVo> r = new R<>();

        //获取当前用户id
        int userId = UserUtils.getUserId(request);

        //校验数据
        if (StrUtil.isBlank(barcode) || StrUtil.isBlank(barcodeType) || StrUtil.isBlank(orderNo)) {
            r.setErrorAndErrorMsg("请输入有效的数据!!!");
            return r;
        }

        try {
            CfAllotManagementVo vo = twoStepAllotInService.scanBarcode(barcode, barcodeType, orderNo, userId);
            r.setData(vo);
        } catch (Exception e) {
            //打印错误日志
            log.error(e.getMessage());
            //输出错误信息到控制台
            e.printStackTrace();
            //封装错误信息
            r.setErrorAndErrorMsg(e.getMessage());
        }
        return r;
    }


    /**
     * 提交
     *
     * @param orderNo 调拨单号
     * @param request 请求对象
     * @return r
     */
    @PostMapping("finalCommit")
    @ApiOperation(value = " 提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> finalCommit(String orderNo, HttpServletRequest request) {
        //创建r对象
        R<CfAllotManagementVo> r = new R<>();

        //获取用户ID
        int userId = UserUtils.getUserId(request);

        //校验输入数据
        if (StrUtil.isBlank(orderNo)) {
            r.setErrorAndErrorMsg("请输入有效的调拨单号!!!");
            return r;
        }

        try {
            //业务层提交逻辑处理
            String msg = twoStepAllotInService.finalCommit(orderNo, userId);

            CfAllotManagementVo vo = cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId);
            vo.setOrderStatus(msg);

            //从数据库中拉取数据
            r.setData(vo);

        } catch (Exception e) {
            //打印错误日志
            log.error(e.getMessage());
            //输出错误信息到控制台
            e.printStackTrace();
            r.setErrorAndErrorMsg(e.getMessage());
        }
        return r;

    }

}
