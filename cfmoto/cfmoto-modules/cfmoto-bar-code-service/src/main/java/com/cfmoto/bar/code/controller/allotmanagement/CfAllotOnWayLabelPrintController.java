package com.cfmoto.bar.code.controller.allotmanagement;

import cn.hutool.core.util.StrUtil;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayData;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayLabel;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.bo.CfAllotOnWayDataBo;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotOnWayLabelPrintService;
import com.cfmoto.bar.code.utiles.IntegerUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 调拨在途标签打印 前端控制器
 *
 * @author ye
 */

@RestController
@RequestMapping("allot/onWay")
@Api(tags = " 调拨在途标签打印")
public class CfAllotOnWayLabelPrintController {

    @Autowired
    private ICfAllotOnWayLabelPrintService cfAllotOnWayLabelPrintService;

    /**
     * 通过调拨单号获取在途表中数据
     *
     * @param orderNo 调拨单号
     * @return r
     */
    @GetMapping("getOnWayByOrderNo")
    @ApiOperation(value = "调拨单查询")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query")
    )
    public R<List<CfAllotOnWayData>> getOnWayByOrderNo(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            return new R<>(R.FAIL, "请输入有效的数据!!!");
        }
        return new R<>(cfAllotOnWayLabelPrintService.getOnWayByOrderNo(orderNo));
    }


    @PostMapping("printLabel")
    @ApiOperation(value = "在途标签打印")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfAllotOnWayData", value = "在途数据", dataType = "object", paramType = "body")
    })
    public R<List<CfAllotOnWayLabel>> printOnWayLabel(@RequestBody CfAllotOnWayDataBo bo, HttpServletRequest request) {
        //数据校验

        if (IntegerUtils.isBlank(bo.getToBePrintedNumber()) || IntegerUtils.isBlank(bo.getSplitUnit())) {
            return new R<>(R.FAIL, "打印数量和拆分单位不能为空，请注意！");
        }

        if (bo.getToBePrintedNumber() > bo.getCfAllotOnWayData().getNumber()) {
            return new R<>(R.FAIL, "打印数量不能大于在途数据表中的数量！！！");
        }
        if (bo.getSplitUnit() > bo.getToBePrintedNumber()) {
            return new R<>(R.FAIL, "拆分单位不能大于打印数量！！！");
        }

        int userId = UserUtils.getUserId(request);

        try {
            return new R<>(cfAllotOnWayLabelPrintService.print(bo,userId));
        } catch (Exception e) {
            return new R<>(R.FAIL, "打印出现问题，请联系管理员！！！");
        }
    }

}
