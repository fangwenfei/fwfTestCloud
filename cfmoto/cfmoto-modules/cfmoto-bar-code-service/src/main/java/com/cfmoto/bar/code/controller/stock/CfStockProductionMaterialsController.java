package com.cfmoto.bar.code.controller.stock;

import com.cfmoto.bar.code.model.entity.CfStockListInfo;
import com.cfmoto.bar.code.model.entity.CfStockScanLine;
import com.cfmoto.bar.code.service.ICfStockProductionMaterialsService;
import com.cfmoto.bar.code.service.ICfStockScanLineService;
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

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/18.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven
 *                              生产发料**********
 * **********************************************************************
 */
@RestController
@RequestMapping("/cfStockProductionMaterials")
@Api(tags=" 生产发料")
public class CfStockProductionMaterialsController extends BaseController {

    @Autowired
    private ICfStockProductionMaterialsService cfStockProductionMaterialsService;


    @Autowired private ICfStockScanLineService cfStockScanLineService;

    /**
     * 点击提交，更新备料扫描记录表中状态为未提交的行数据改为已提交；数据通过接口发送至SAP；OT条码扣减对应库存，CP/EG/KTM扣减库存数量为0（KTM在KTM表）；
     *
     */
    @PostMapping("/submitCfStockProductionMaterialsData")
    @ApiOperation(value="提交备料扫描记录")
    public R<Map<String, Object>> submitCfStockProductionMaterialsData(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            Map<String, Object> resultMap= cfStockProductionMaterialsService.submitCfStockProductionMaterialsData(userId,params);
            return new R<>(resultMap);
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL,  CfStockScanLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }
    }

    /**
     * 先检查后台备料信息表里是否存在数据
     *
     */
    @PostMapping("/getDataByStockListNo")
    @ApiOperation(value="先检查后台备料信息表里是否存在数据")
    public R<Map<String, Object>> getDataByStockListNo(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            params.put("stockFunctionType", CfStockListInfo.STOCK_FUNCTION_TYPE_20);//合并备料：10，生产领料:20,退料: 30,超领 :40
            return new R<>(cfStockScanLineService.getDataByStockListNo(userId,params));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }

    }
}
