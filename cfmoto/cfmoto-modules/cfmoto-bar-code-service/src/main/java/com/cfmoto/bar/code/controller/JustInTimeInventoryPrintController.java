package com.cfmoto.bar.code.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.vo.JustInTimeInventoryPrintVo;
import com.cfmoto.bar.code.service.JustInTimeInventoryPrintService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/4/24.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@RestController
@RequestMapping("/justInTimeInventory")
@Api(tags = " 及时库存打印")
public class JustInTimeInventoryPrintController  extends BaseController {
    @Autowired
    JustInTimeInventoryPrintService justInTimeInventoryPrint;

    /**
     * 及时库存打印
     */
    @PostMapping("/splitSapDataPrintByParam")
    @ApiOperation(value = "通过拆分进行打印及时库存打印")
    public R<List<CfBarcodeInventory>> splitSapDataPrintByParam(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try {
            int userId= UserUtils.getUserId(httpServletRequest);
            List<CfBarcodeInventory> result=  justInTimeInventoryPrint.splitSapDataPrintByParam(params,userId);
            return new R<>(result);
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage());
        }
    }
}
