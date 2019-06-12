package com.cfmoto.bar.code.controller;


import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.EnginePutVo;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.service.ICfCustomService;
import com.cfmoto.bar.code.service.ICfStorageLocationService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cfProductionPutStorage")
@Api(tags = " 产品入库管理")
@Slf4j
public class CfProductionPutStorageController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ICfCustomService iCfCustomService;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private ICfStorageLocationService iCfStorageLocationService;

    @GetMapping("getStorageLocation")
    @ApiOperation(value = "获取仓库数据")
    @ApiImplicitParam(name = "storageLocation", value = "仓库-模糊查询", dataType = "string", paramType = "query")
    public R<List<CfStorageLocation>> getStorageLocation(String storageLocation) {

        Wrapper<CfStorageLocation> wrapper = new EntityWrapper<>();
        if (StrUtil.isBlank(storageLocation)) {
            storageLocation = "";
        }
        wrapper.like("warehouse", storageLocation, SqlLike.RIGHT);
        List<CfStorageLocation> cfStorageLocationList;
        try {
            cfStorageLocationList = iCfStorageLocationService.selectList(wrapper);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(cfStorageLocationList);
    }


    @GetMapping("/getTaskNo")
    @ApiOperation(value = "产品入库管理-获取生产任务单")
    @ApiImplicitParam(name = "taskNo", value = "生产任务单", dataType = "string", paramType = "query")
    public R<ProductionTaskVo> getTaskNo(String taskNo) {

        Map<String, Object> param = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("IV_AUFNR", taskNo.trim());
        param.put(HandleRefConstants.PARAM_MAP, paramMap);
        param.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_004");
        ProductionTaskVo productionTaskVo;
        try {
            R returnR = sapFeignService.executeJcoFunction(param);
            if (returnR.getCode() != 0) {
                return new R<>(R.FAIL, returnR.getMsg());
            }
            Map<String, Object> esDataMap = (Map<String, Object>) returnR.getData();
            if ((Integer) esDataMap.get("EV_STATUS") == 0) {
                return new R<>(R.FAIL, (String) esDataMap.get("EV_MESSAGE"));
            }
            Map<String, Object> dataMap = (Map<String, Object>) esDataMap.get("ES_DATA");
            //AUFNR==生产订单 DAUAT==订单类型 MATNR==物料代码 MAKTX==物料名称 PSMNG==任务单数量 WEMNG==已入库数量 FERTH==车型 WRKST==产品规格 KDAUF==销售订单
            //KDPOS==销售订单行项目 KUNNR==客户 ZTEXT==销售订单年份 ZHTH==合同号 LGPRO==生产仓储地点
            productionTaskVo = new ProductionTaskVo();
            productionTaskVo.setTaskNo((String) dataMap.get("AUFNR"));
            productionTaskVo.setOrderType((String) dataMap.get("DAUAT"));
            productionTaskVo.setItem((String) dataMap.get("MATNR"));
            productionTaskVo.setItemDesc((String) dataMap.get("MAKTX"));
            productionTaskVo.setQuantity(new BigDecimal(dataMap.get("PSMNG").toString()));
            productionTaskVo.setReceivedQty(new BigDecimal(dataMap.get("WEMNG").toString()));
            productionTaskVo.setCarType((String) dataMap.get("FERTH"));
            productionTaskVo.setMode((String) dataMap.get("WRKST"));
            productionTaskVo.setSaleOrder((String) dataMap.get("KDAUF"));
            productionTaskVo.setSaleOrderRowItem((String) dataMap.get("KDPOS"));
            productionTaskVo.setCustomer((String) dataMap.get("KUNNR"));
            productionTaskVo.setSaleOrderYear((String) dataMap.get("ZTEXT"));
            productionTaskVo.setContract((String) dataMap.get("ZHTH"));
            productionTaskVo.setStorageLocation((String) dataMap.get("LGPRO"));

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(productionTaskVo);
    }


    @PostMapping("/productionPutStorage")
    @ApiOperation(value = "产品入库扫码")
    public R<ProductionTaskVo> productionPutStorage(@RequestBody EnginePutVo enginePutVo) {

        ProductionTaskVo productionTaskVo;
        try {

            int userId = UserUtils.getUserId(httpServletRequest);

            /*if (StrUtil.isBlank(enginePutVo.getStorageLocation())) {
                return new R<>(R.FAIL, "仓库不能为空");
            }*/
            if (StrUtil.isBlank(enginePutVo.getBarCode())) {
                return new R<>(R.FAIL, "条码不能为空");
            }
            productionTaskVo = iCfCustomService.productionPutStorage(userId, enginePutVo);

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(productionTaskVo, "条码" + enginePutVo.getBarCode() + "入库成功");
    }

}
