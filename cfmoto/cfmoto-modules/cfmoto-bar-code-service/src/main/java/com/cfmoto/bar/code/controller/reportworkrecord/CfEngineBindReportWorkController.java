package com.cfmoto.bar.code.controller.reportworkrecord;

import cn.hutool.core.util.StrUtil;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.service.reportworkrecord.ICfEngineBindReportWorkService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 发动机绑定报工 前端控制器
 *
 * @author yezi
 * @date 2019/6/11
 */
@RestController
@RequestMapping("/engineBindReportWork")
@Api(tags = "发动机绑定/解绑 报工")
@Log4j
public class CfEngineBindReportWorkController {

    @Autowired
    private ICfEngineBindReportWorkService engineBindReportWorkService;

    @GetMapping("/productOrder")
    @ApiOperation(value = "生产订单接口")
    @ApiImplicitParam(name = "productOrderNo", value = "生产订单号", dataType = "string", paramType = "query")
    public R<ProductionTaskVo> productOrder(String productOrderNo) {
        //校验前端传入的数据
        if (StrUtil.isBlank(StrUtil.trim(productOrderNo))) {
            return new R<>(R.FAIL, "请输入生产订单号!!!");
        }

        try {
            //调用业务层接口获取订单数量和待绑定数量
            ProductionTaskVo productionTaskVo = engineBindReportWorkService.productOrder(productOrderNo);
            return new R<>(productionTaskVo);

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    @PostMapping("/engineBarcodeScan")
    @ApiOperation(value = "发动机条码扫描")
    public R<ProductionTaskVo> engineBarcodeScan(@RequestBody ProductionTaskVo productionTaskVo, HttpServletRequest request) {
        //校验前端传入的数据
        String engineBarcode;
        String productOrderNo;
        if (productionTaskVo != null) {
            engineBarcode = productionTaskVo.getEngineBarcode();
            productOrderNo = productionTaskVo.getTaskNo();
            if (StrUtil.isBlank(StrUtil.trim(engineBarcode))) {
                return new R<>(R.FAIL, "请输入发动机条码!!!");
            }
            if (StrUtil.isBlank(StrUtil.trim(productOrderNo))) {
                return new R<>(R.FAIL, "请输入生产订单号!!!");
            }

        } else {
            return new R<>(R.FAIL, "请输入有效数据!!!");
        }

        try {

            //调用业务层对条码进行扫描
            engineBindReportWorkService.engineBarcodeScan(productionTaskVo, UserUtils.getUserId(request));
            return new R<>(engineBindReportWorkService.productOrder(productOrderNo), "发动机条码绑定成功!!!");
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 1、校验条码是否存在库存；
     * 不存在报错“条码不存在”；
     * 存在，校验状态是否为N，不为N报错“条码有入库记录，不能解绑”，为N，删除库存表数据，删除报工记录表数据
     *
     * @param engineBarcode 发动机条码
     * @return R
     */
    @GetMapping("/unbindEngineBarcode")
    @ApiOperation(value = "发动机解绑")
    @ApiImplicitParam(name = "engineBarcode", value = "发动机条码", dataType = "string", paramType = "query")
    public R unbindEngineBarcode(String engineBarcode) {
        //校验前端传入数据
        if (StrUtil.isBlank(StrUtil.trim(engineBarcode))) {
            return new R(R.FAIL, "请输入发动机条码!!!");
        }

        try {

            engineBindReportWorkService.unbindEngineBarcode(engineBarcode);
            return new R(R.SUCCESS, "发动机条码解绑成功!!!");

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }
    }
}
