package com.cfmoto.bar.code.controller.allotmanagement;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotInventoryService;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotManagementCommonService;
import com.cfmoto.bar.code.utiles.BarcodeUtils;
import com.cfmoto.bar.code.utiles.ValidateUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.xiaoleilu.hutool.util.NumberUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 调拨管理通用接口
 *
 * @author ye
 */
@RestController
@RequestMapping("allot/common")
@Api(tags = " 调拨管理通用接口")
public class CfAllotManagementCommonController {

    @Autowired
    private ICfAllotManagementCommonService cfAllotManagementCommonService;

    @Autowired
    private ICfAllotInventoryService cfAllotInventoryService;

    private Logger logger = LoggerFactory.getLogger(CfAllotManagementCommonController.class);

    private ValidateUtils<String> validateUtils = new ValidateUtils<>();

    /**
     * 功能概括：两步调拨(出库、入库)的 "调拨单" 接口
     * 业务逻辑：
     * 1.首先从数据库中根据订单号查询调拨信息表中是否存在数据
     * 否：调用SAP08接口获取调拨清单数据(包含调拨信息表（一条）和调拨清单表（多条）)，插入到数据库中并返回给PDA终端
     * 是：则查找调拨清单表（多条）和调拨扫描记录表（多条）数据并返回给PDA终端，其中，扫描记录表数据要对应操作类型和用户id
     *
     * @param orderNo 调拨单号
     * @param opType  操作类型
     */
    @GetMapping("getDataByOrderNo")
    @ApiOperation(value = "调拨单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "opType", value = "操作类型(01:二步调拨出库,02:二步调拨入库)", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> getDataByOrderNo(String orderNo, String opType, HttpServletRequest request) {
        R<CfAllotManagementVo> r = new R<>();

        //校验用户输入的数据
        if (StrUtil.isBlank(orderNo) || StrUtil.isBlank(opType)) {
            r.setErrorAndErrorMsg("请输入有效的数据!!!");
            return r;
        }

        if (orderNo.length() != 10) {
            r.setErrorAndErrorMsg("二步调拨单格式有误,必须为10位!!!");
            return r;
        }

        if (orderNo.charAt(0) != 'D' && orderNo.charAt(1) != 'B') {
            r.setErrorAndErrorMsg("二步调拨单格式有误,必须以DB开头!!!");
            return r;
        }


        //获取当前登陆的用户ID
        int userId = UserUtils.getUserId(request);

        //根据调拨单加载数据，并捕获异常
        try {
            CfAllotManagementVo allotManagementVo = cfAllotManagementCommonService.getDataByOrderNo(orderNo, opType, userId);
            r.setData(allotManagementVo);
        } catch (Exception e) {
            r.setErrorAndErrorMsg(e.getMessage());
            //打印错误日志
            logger.info(e.getMessage());
        }
        return r;

    }


    /**
     * 未录完 接口
     * 两步调拨出库             适用
     * 两步调拨入库             适用
     * 一步调拨(出库)           适用
     *
     * @param orderNo   订单号
     * @param state     状态
     * @param allotType 调拨类型
     * @return R
     */
    @GetMapping("unRecorded")
    @ApiOperation(value = "未录完")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态（true代表未录完,false已录完）", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "allotType", value = "调拨类型（in:入库/out:出库）", dataType = "string", paramType = "query")})
    public R<List<CfAllotInventory>> getListByMaterialsAndState(String orderNo, Boolean state, String allotType) {

        CfAllotInventory inventory = new CfAllotInventory();

        inventory.setOrderNo(orderNo);

        //创建包装对象
        EntityWrapper<CfAllotInventory> wrapper = new EntityWrapper<>(inventory);

        //判断录入状态
        //查找未录完的数据
        if (state) {

            //拼接查询条件
            if (allotType.equals("in")) {
                wrapper.where(" number != allot_in_scanned_number ");
            } else if (allotType.equals("out")) {
                wrapper.where(" number != allot_out_scanned_number ");
            }

        }

        List<CfAllotInventory> list = cfAllotInventoryService.selectList(wrapper);
        return new R<>(list);
    }


    /**
     * 删除行
     * 二步调拨出入库、一步调拨都适用
     *
     * @param orderNo 调拨单号
     * @param id      行id（调拨扫描记录表主键Id）
     * @param opType  操作类型(01:二步调拨出库/02:二步调拨入库/03:一步调拨)
     * @param request request对象
     * @return r
     */
    @GetMapping("deleteRow")
    @ApiOperation(value = "删除行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "行id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "apiNo", value = "接口号(01:二步调拨出库/02:二步调拨入库/03:一步调拨)", dataType = "string", paramType = "query")})
    public R<CfAllotManagementVo> deleteRow(String orderNo, Integer id, String opType, HttpServletRequest request) {

        //创建r对象
        R<CfAllotManagementVo> r = new R<>();

        //获取当前操作人id
        int userId = UserUtils.getUserId(request);

        //校验前端传过来的数据
        if (StrUtil.isBlank(orderNo) || NumberUtil.isBlankChar(id) || StrUtil.isBlank(opType)) {
            r.setErrorAndErrorMsg("请输入有效的数据!!!");
            return r;
        }

        try {
            //调用业务层处理删除行数据和更新清单表数量业务
            cfAllotManagementCommonService.deleteRow(orderNo, id, opType, userId);
            r.setData(cfAllotManagementCommonService.getDataFromDataBase(orderNo, opType, userId));
        } catch (Exception e) {
            r.setErrorAndErrorMsg(e.getMessage());
            //打印详细错误信息
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }

        return r;
    }

    /**
     * 两步调拨出库和一步调拨的物料批次匹配提交接口
     *
     * @param cfAllotManagementVo vo对象
     * @param request             请求对象
     * @return
     */
    @PostMapping("commitBatchMatchedData")
    @ApiOperation(value = "物料批次匹配提交")
    public R<CfAllotManagementVo> commitBatchMatchedData(@RequestBody CfAllotManagementVo cfAllotManagementVo, HttpServletRequest request) throws Exception {

        R<CfAllotManagementVo> r = new R<>();

        int userId = UserUtils.getUserId(request);

        //从jsonObject中取出所需数据
        List<Map<String, Object>> list = cfAllotManagementVo.getBatchMatchList();

        ValidateUtils<Map<String, Object>> validateUtils = new ValidateUtils<>();
        boolean notNull = validateUtils.isNotNull(list);
        if (!notNull) {
            r.setErrorAndErrorMsg("暂无可提交的数据！");
            return r;
        }

        //获取订单号
        String orderNo = cfAllotManagementVo.getOrderNo();

        //获取物料代码
        String materialsNo = cfAllotManagementVo.getMaterialsNo();

        //获取条码
        String barcode = cfAllotManagementVo.getBarcode();

        //获取操作类型
        String opType = cfAllotManagementVo.getOpType();

        if (!validateUtils.isNotNull(orderNo, materialsNo, barcode)) {
            r.setErrorAndErrorMsg("请输入有效的数据！");
            return r;
        }


        //判断条码数量
        CfAllotInventory inventory = cfAllotInventoryService.selectOne(new EntityWrapper<CfAllotInventory>().eq("order_no", orderNo).eq("materials_no", materialsNo));
        Map<String, Object> map = BarcodeUtils.anaylysisAndSplitBarcodeThrowException(barcode);
        int number = (int) map.get("number");
        if (number > inventory.getNumber() - inventory.getAllotOutScannedNumber()) {
            r.setErrorAndErrorMsg("条码数量不能大于未清数量!!!");
            return r;
        }


        //插入扫描数据和更新清单表数据
        try {
            cfAllotManagementCommonService.insertRecordAndUpdateInventory(orderNo, opType, materialsNo, barcode, list, userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            r.setErrorAndErrorMsg(e.getMessage());
            return r;
        }


        //从数据库查询数据并返回
        try {
            r.setData(cfAllotManagementCommonService.getDataFromDataBase(orderNo, opType, userId));
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            r.setErrorAndErrorMsg(e.getMessage());
            return r;
        }
    }


    /**
     * 调拨模块的删除功能（通用于一步、两步出库、两步入库）
     *
     * @param orderNo     单号
     * @param barcode     条码
     * @param materialsNo 物料代码
     * @param apiNo       接口号（1:库存 2:物料）
     * @param opType      操作类型(01:两步出库，02：两步入库，03：一步调拨)
     * @param request     请求对象
     * @return CfAllowManagementVo
     */
    @GetMapping("delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "materialsNo", value = "物料代码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "apiNo", value = "接口号（1:库存 2:物料）", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "opType", value = "操作类型(01:两步出库，02：两步入库，03：一步调拨)", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> delete(String orderNo, String barcode, String materialsNo, String apiNo, String opType, HttpServletRequest request) {
        //校验前端传过来的数据
        if (!validateUtils.isNotNull(orderNo, barcode, materialsNo, apiNo, opType)) {
            return new R<>(R.FAIL, "单号或条码或物料代码为空,请注意!!!");
        }
        try {
            //删除
            cfAllotManagementCommonService.delete(orderNo, barcode, materialsNo, apiNo, opType, UserUtils.getUserId(request));
            //返回单号对应的数据
            return new R<>(cfAllotManagementCommonService.getDataFromDataBase(orderNo, opType, UserUtils.getUserId(request)));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }


    /**
     * 根据单号、物料代码、操作类型获取扫描表中的物料总数
     *
     * @param orderNo     单号
     * @param materialsNo 物料代码
     * @param opType      操作类型（01:两步出库，02：两步入库，03：一步调拨）
     * @param request     请求对象
     * @return
     */
    @GetMapping("getTotal")
    @ApiOperation(value = "物料总数获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "materialsNo", value = "物料代码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "opType", value = "操作类型(01:两步出库，02：两步入库，03：一步调拨)", dataType = "string", paramType = "query")
    })
    public R<Integer> getTotal(String orderNo, String materialsNo, String opType, HttpServletRequest request) {
        //校验前端传过来的数据
        if (!validateUtils.isNotNull(orderNo, materialsNo)) {
            return new R<>(R.FAIL, "单号或物料代码为空,请注意!!!");
        }
        try {
            Integer total = cfAllotManagementCommonService.getTotal(orderNo, materialsNo, opType, UserUtils.getUserId(request));
            return new R<>(total);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }
    }


    /**
     * 修改扫描表行数据
     *
     * @param orderNo     单号
     * @param barcode     条码
     * @param materialsNo 物料代码
     * @param opType      操作类型
     * @param apiNo       接口号
     * @param number      修改数量
     * @param total       物料总数
     * @param request     请求对象
     * @return r
     */
    @GetMapping("update")
    @ApiOperation(value = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "materialsNo", value = "物料代码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "opType", value = "操作类型(01:两步出库，02：两步入库，03：一步调拨)", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "apiNo", value = "接口号（1：库存 2：物料）", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "number", value = "修改数量", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "total", value = "物料总数", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> update(String orderNo, String barcode, String materialsNo, String opType, String apiNo, Integer number, Integer total, HttpServletRequest request) {
        //校验前端传过来的数据
        if (!validateUtils.isNotNull(orderNo, barcode,materialsNo) || NumberUtil.isBlankChar(number)) {
            return new R<>(R.FAIL, "单号或条码或物料代码或修改数量为空,请注意!!!");
        }

        try {
            cfAllotManagementCommonService.update(orderNo, barcode, materialsNo, opType, apiNo, number, total, UserUtils.getUserId(request));
            //重新加载数据
            CfAllotManagementVo allotManagementVo = cfAllotManagementCommonService.getDataFromDataBase(orderNo, opType, UserUtils.getUserId(request));
            return new R<>(allotManagementVo);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }

}
