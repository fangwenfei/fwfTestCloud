package com.cfmoto.bar.code.controller.partsmanage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.cfmoto.bar.code.model.entity.CfDeliverGoods;
import com.cfmoto.bar.code.model.entity.CfPackingList;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.*;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.ICfStorageLocationService;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotManagementCommonService;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsScanService;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsService;
import com.cfmoto.bar.code.service.partsmanage.ICfPackingListService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.vo.UserVO;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.NumberUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 部品发货临时表 前端控制器
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
@RestController
@RequestMapping("/cfDeliverGoods")
@Api(tags = " 部品部功能")
@Slf4j
public class CfDeliverGoodsController extends BaseController {

    @Autowired
    private ICfDeliverGoodsService cfDeliverGoodsService;

    @Autowired
    private ICfDeliverGoodsScanService cfDeliverGoodsScanService;

    @Autowired
    private ICfStorageLocationService cfStorageLocationService;

    @Autowired
    private ICfPackingListService cfPackingListService;

    @Autowired
    private ICfAllotManagementCommonService iCfAllotManagementCommonService;

    @Autowired
    private ICfAllotManagementCommonService cfAllotManagementCommonService;

    @Autowired
    private ICfNextNumberService iCfNextNumberService;

    @Autowired
    private ISapApiService iSapApiService;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private UserFeignService userFeignService;

    @GetMapping("/getPartAllocationData")
    @ApiOperation(value = "部品物流面单打印-调拨单数据查询")
    @ApiImplicitParam(name = "orderNo", value = "调拨单", dataType = "string", paramType = "query")
    public R<PartsLogisticsVo> getPartAllocationData(String orderNo, HttpServletRequest httpServletRequest) {

        PartsLogisticsVo partsLogisticsVo;
        try {

            if (StrUtil.isBlank(orderNo)) {
                throw new Exception("调拨单不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            CfAllotManagementVo cfAllotManagementVo = iCfAllotManagementCommonService.getDataFromDataBase(orderNo.trim(), "02", userId);
            if (cfAllotManagementVo == null) {
                throw new Exception("调拨单" + orderNo + "装箱已完成或未备料记录");
            }
            List<CfAllotInventory> cfAllotInventoryList = cfAllotManagementVo.getCfAllotInventoryList();
            if (cfAllotInventoryList == null || cfAllotInventoryList.size() == 0) {
                throw new Exception("调拨单" + orderNo + "无清单数据");
            }
            CfAllotInventory cfAllotInventory = cfAllotInventoryList.get(0);
            partsLogisticsVo = new PartsLogisticsVo();
            partsLogisticsVo.setOrderNo(cfAllotInventory.getOrderNo());
            partsLogisticsVo.setSaleOrderNo(cfAllotInventory.getSaleOrderNo());
            partsLogisticsVo.setCustomerName(cfAllotInventory.getCustomerName());
            partsLogisticsVo.setReceiveAddress(cfAllotInventory.getReceiveAddress());
            partsLogisticsVo.setReceiveContactName(cfAllotInventory.getReceiveContactName());
            partsLogisticsVo.setReceiveContactPhoneNumber(cfAllotInventory.getReceiveContactPhoneNumber());
            partsLogisticsVo.setAddresser(cfAllotInventory.getAddresser());
            partsLogisticsVo.setAddresserPhoneNumber(cfAllotInventory.getAddresserPhoneNumber());

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(partsLogisticsVo);
    }

    @GetMapping("/getWayBillNo")
    @ApiOperation(value = "部品物流面单打印-打印按钮获取运单号")
    public R<String> getWayBillNo() {

        String wayBillNo = "";
        try {
            wayBillNo = iCfNextNumberService.generateNextNumber("WAY_BILL");
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(wayBillNo);
    }

    @GetMapping("/getDeliverGoodsOrderData")
    @ApiOperation(value = "部品装箱功能-调拨单数据查询")
    @ApiImplicitParam(name = "orderNo", value = "调拨单", dataType = "string", paramType = "query")
    public R<CfAllotManagementVo> getDeliverGoodsOrderData(String orderNo, HttpServletRequest httpServletRequest) {
        try {

            if (StrUtil.isBlank(orderNo)) {
                throw new Exception("调拨单不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);

            return new R<>(cfPackingListService.getAllocationOrderData(orderNo, userId));

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }


    /**
     * 删除行
     *
     * @param orderNo
     * @param id
     * @param request
     * @return
     */
    @GetMapping("deleteRow")
    @ApiOperation(value = "部品装箱功能-删除行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "packNo", value = "包号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "行id", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> deleteRow(String orderNo, String packNo, Integer id, HttpServletRequest request) {

        //创建r对象
        R<CfAllotManagementVo> r = new R<>();
        try {
            //获取当前操作人id
            int userId = UserUtils.getUserId(request);
            //校验用户输入的数据
            if (StrUtil.isBlank(orderNo) || NumberUtil.isBlankChar(id)) {
                r.setErrorAndErrorMsg("请输入有效的数据!!!");
                return r;
            }
            //调用业务层处理删除行数据和更新清单表数量业务
            cfAllotManagementCommonService.scanDeleteRow(orderNo, id, packNo, userId);
            r.setData(cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId));
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            r.setErrorAndErrorMsg(e.getMessage());
        }
        return r;

    }

    /**
     * 修改行数据
     *
     * @param orderNo
     * @param barCode
     * @param qty
     * @param request
     * @return
     */
    @GetMapping("changeRowData")
    @ApiOperation(value = "部品装箱功能-修改行数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barCode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "qty", value = "修改数量", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> changeRowData(String orderNo, String barCode, String qty, HttpServletRequest request) {

        //创建r对象
        R<CfAllotManagementVo> r = new R<>();
        try {
            //获取当前操作人id
            int userId = UserUtils.getUserId(request);
            //校验用户输入的数据
            if (StrUtil.isBlank(orderNo)) {
                throw new Exception("调拨单不能为空");
            }
            if (StrUtil.isBlank(barCode)) {
                throw new Exception("条码不能为空");
            }
            cfPackingListService.changeRowData(orderNo, barCode, qty, userId);
            r.setData(cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId));
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            r.setErrorAndErrorMsg(e.getMessage());
        }
        return r;

    }

    /**
     * 部品装箱功能-扫描行
     *
     * @param orderNo            调拨单号
     * @param scanType           扫描类型
     * @param httpServletRequest 请求对象
     * @return R
     */
    @GetMapping("/scanRowData")
    @ApiOperation(value = "部品装箱功能-扫描行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barCode", value = "条码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "scanType", value = "扫描类型I/M:I-库存，M-物料", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> scanRowData(String orderNo, String barCode, String scanType, HttpServletRequest httpServletRequest) {

        CfAllotManagementVo cfAllotManagementVo;
        try {

            if (StrUtil.isBlank(orderNo)) {
                throw new Exception("调拨单不能为空");
            }
            if (StrUtil.isBlank(scanType)) {
                throw new Exception("条码类型不能为空");
            }
            if (StrUtil.isBlank(barCode)) {
                throw new Exception("扫描条码不能为空");
            }
            if (!("I".equals(scanType) || "M".equals(scanType))) {
                throw new Exception("扫描类型必须为:I-库存或者M-物料");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            cfPackingListService.scanRowData(orderNo.trim(), barCode.trim(), scanType.trim(), userId);
            cfAllotManagementVo = cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(cfAllotManagementVo);

    }


    @GetMapping("/closeCaseNo")
    @ApiOperation(value = "部品装箱功能-关箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "调拨单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "length", value = "长", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "wide", value = "宽", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "high", value = "高", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "weight", value = "毛重", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "express", value = "快递公司", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "wayBillNo", value = "运单号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "businessStreamOrderNo", value = "商流订单", dataType = "string", paramType = "query")
    })
    public R<CfAllotManagementVo> closeCaseNo(String orderNo, Integer length, Integer wide, Integer high, String weight,
                                              String express, String wayBillNo, String businessStreamOrderNo, HttpServletRequest httpServletRequest) {

        CfAllotManagementVo cfAllotManagementVo = null;
        try {
            if (StrUtil.isBlank(orderNo)) {
                throw new Exception("调拨单不能为空");
            }
            if (length == null) {
                throw new Exception("长不能为空");
            }
            if (wide == null) {
                throw new Exception("宽不能为空");
            }
            if (high == null) {
                throw new Exception("高不能为空");
            }
            if (StrUtil.isBlank(weight)) {
                throw new Exception("毛重不能为空");
            }
            if (StrUtil.isBlank(weight)) {
                throw new Exception("毛重不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            cfAllotManagementVo = cfPackingListService.closeCaseNo(userId, orderNo, length, wide, high, weight,
                    express, wayBillNo, businessStreamOrderNo);
            String orderStatus = cfAllotManagementVo.getOrderStatus();
            String caseNo = cfAllotManagementVo.getCaseNo();
            if (!"已完成".equalsIgnoreCase(cfAllotManagementVo.getOrderStatus())) { //预防ERP返回的汇总数据跟已存在的汇总不一致，所以执行数据查询
                cfAllotManagementVo = cfAllotManagementCommonService.getDataFromDataBase(orderNo, "02", userId);
                cfAllotManagementVo.setCaseNo(caseNo);
                cfAllotManagementVo.setOrderStatus(orderStatus);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(cfAllotManagementVo);
    }


    @GetMapping("/getUnclearDeliverGoodsNo")
    @ApiOperation(value = "部品发货-未清发货单查询")
    public R<List<UnClearListVo>> getUnclearDeliverGoodsNo() {

        List<UnClearListVo> unClearListVoList = new ArrayList<UnClearListVo>();
        try {
            Map<String, Object> callParamMap = new HashMap<String, Object>();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("IV_ZSTATUS", "U");
            callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_034");
            callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);
            R r = sapFeignService.executeJcoFunction(callParamMap);
            if (r.getCode() != 0) {
                throw new Exception(r.getMsg());
            }
            Map<String, Object> returnMap = (Map<String, Object>) r.getData();
            Integer evStatus = (Integer) returnMap.get("EV_STATUS");
            if (evStatus == 0) {
                throw new Exception((String) returnMap.get("EV_MESSAGE"));
            }
            List<Map<String, Object>> etDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
            UnClearListVo unClearListVo = null;
            for (int i = 0, len = etDataList.size(); i < len; i++) {
                unClearListVo = new UnClearListVo();
                unClearListVo.setOrderNo((String) etDataList.get(i).get("ZCELN"));
                unClearListVoList.add(unClearListVo);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(unClearListVoList);
    }

    @GetMapping("/getDeliverGoodsOrder")
    @ApiOperation(value = "部品发货-查询发货单数据")
    @ApiImplicitParam(name = "orderNo", value = "发货单", dataType = "string", paramType = "query")
    public R<DeliverGoodsFullVo> getDeliverGoodsOrder(String orderNo, HttpServletRequest httpServletRequest) {

        DeliverGoodsFullVo deliverGoodsFullVo = null;
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            deliverGoodsFullVo = cfDeliverGoodsService.getDeliverGoodsOrder(orderNo, userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(deliverGoodsFullVo);

    }

    @GetMapping("/scanCaseNo")
    @ApiOperation(value = "部品发货-箱号扫描")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "发货单", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "caseNo", value = "箱号", dataType = "string", paramType = "query")
    })
    public R<DeliverGoodsFullVo> scanCaseNo(String orderNo, String caseNo, HttpServletRequest httpServletRequest) {

        DeliverGoodsFullVo deliverGoodsFullVo = null;
        try {
            if (StrUtil.isBlank(orderNo)) {
                throw new Exception("发货单不能为空");
            }
            if (StrUtil.isBlank(caseNo)) {
                throw new Exception("箱号不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            cfDeliverGoodsScanService.scanCaseNo(orderNo.trim(), caseNo.trim(), userId);
            deliverGoodsFullVo = cfDeliverGoodsService.getDeliverGoodsOrder(orderNo.trim(), userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(deliverGoodsFullVo);
    }


    @GetMapping("/deleteScanRow")
    @ApiOperation(value = "部品发货-删除行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "发货单", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "deliverGoodsScanId", value = "扫描标识", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "deliverGoodsSumIdRef", value = "汇总标识", dataType = "string", paramType = "query")
    })
    public R<DeliverGoodsFullVo> deleteScanRow(String orderNo, String deliverGoodsScanId, String deliverGoodsSumIdRef,
                                               HttpServletRequest httpServletRequest) {
        DeliverGoodsFullVo deliverGoodsFullVo = null;
        try {
            if (StrUtil.isBlank(deliverGoodsScanId)) {
                throw new Exception("扫描标识不能为空");
            }
            if (StrUtil.isBlank(deliverGoodsSumIdRef)) {
                throw new Exception("汇总标识不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            cfDeliverGoodsScanService.deleteScanRow(deliverGoodsScanId, deliverGoodsSumIdRef, userId);
            deliverGoodsFullVo = cfDeliverGoodsService.getDeliverGoodsOrder(orderNo.trim(), userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(deliverGoodsFullVo);

    }


    @GetMapping("/doDeliverGoodsOrderSubmit")
    @ApiOperation(value = "部品发货-提交")
    @ApiImplicitParam(name = "orderNo", value = "发货单", dataType = "string", paramType = "query")
    public R<DeliverGoodsFullVo> doDeliverGoodsOrderSubmit(String orderNo, HttpServletRequest httpServletRequest) {

        DeliverGoodsFullVo deliverGoodsFullVo = null;
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            deliverGoodsFullVo = cfDeliverGoodsService.doDeliverGoodsOrderSubmit(orderNo, userId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(deliverGoodsFullVo);

    }


    @GetMapping("/getPartsInventory")
    @ApiOperation(value = "部品库存查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "item", value = "物料代码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "storageLocation", value = "仓库", dataType = "string", paramType = "query")
    })
    public R<List<PartsInventoryVo>> getPartsInventory(String item, String storageLocation, HttpServletRequest httpServletRequest) {

        List<PartsInventoryVo> partsInventoryVoList;
        try {
            if (StrUtil.isBlank(item)) {
                throw new Exception("物料代码不能为空");
            }
            if (StrUtil.isBlank(storageLocation)) {
                throw new Exception("仓库不能为空");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            //查询工厂
            EntityWrapper<CfStorageLocation> storageLocationWrapper = new EntityWrapper<>();
            CfStorageLocation storageLocationEntity = new CfStorageLocation();
            storageLocationEntity.setWareHouse(storageLocation.trim());
            storageLocationWrapper.setEntity(storageLocationEntity);
            //去仓库表中查询是否有用户输入的仓库
            List<CfStorageLocation> storageLocationList = cfStorageLocationService.selectList(storageLocationWrapper);
            if (storageLocationList.size() == 0) {
                throw new Exception("仓库" + storageLocation + "未维护");
            }

            partsInventoryVoList = new ArrayList<>();

            //调用sap接口查询对应仓库（用户输入）、物料代码（用户输入）、工厂（用户对应工厂）的即时库存信息
            UserVO user = userFeignService.user(userId);

            List<Map<String, Object>> inventoryList = iSapApiService.newGetDataFromSapApi10(storageLocation.trim(), item.trim(), user.getSite(), "");

            Map<String, Object> map;
            PartsInventoryVo partsInventoryVo;
            for (int i = 0; i < inventoryList.size(); i++) {

                map = inventoryList.get(i);
                partsInventoryVo = new PartsInventoryVo();
                partsInventoryVo.setItem((String) map.get("materialsNo"));
                partsInventoryVo.setItemDesc((String) map.get("materialsName"));
                partsInventoryVo.setMode((String) map.get("spec"));
                partsInventoryVo.setQty(new BigDecimal((String) map.get("batchNumber")));
                partsInventoryVo.setBatchNo((String) map.get("batchNo"));
                partsInventoryVo.setStorageLocation((String) map.get("wareHouse"));
                partsInventoryVo.setSpStorageLocationPosition((String) map.get("warehousePosition"));
                partsInventoryVoList.add(partsInventoryVo);

            }

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(partsInventoryVoList);

    }

    @GetMapping("/validateSaleOrder")
    @ApiOperation(value = "部品装箱外贴补打印-验证销售订单")
    @ApiImplicitParam(name = "saleOrder", value = "销售订单", dataType = "string", paramType = "query")
    public R<String> validateSaleOrder(String saleOrder) {

        try {
            if (StrUtil.isBlank(saleOrder)) {
                throw new Exception("销售订单不能为空");
            }
            EntityWrapper<CfPackingList> wrapper = new EntityWrapper<CfPackingList>();
            CfPackingList packListEntity = new CfPackingList();
            packListEntity.setSaleOrder(saleOrder);
            wrapper.setEntity(packListEntity);
            int count = cfPackingListService.selectCount(wrapper);
            if (count <= 0) {
                return new R<>(R.FAIL, "销售订单" + saleOrder + "不存在");
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>("success");
    }

    @GetMapping("/getCaseNoBySaleOrder")
    @ApiOperation(value = "部品装箱外贴补打印-获取销售订单关联的箱号")
    @ApiImplicitParam(name = "saleOrder", value = "销售订单", dataType = "string", paramType = "query")
    public R<List<String>> getCaseNoBySaleOrder(String saleOrder) {

        List<String> caseNoList = new ArrayList<String>();
        try {
            if (StrUtil.isBlank(saleOrder)) {
                throw new Exception("销售订单不能为空");
            }
            EntityWrapper<CfPackingList> wrapper = new EntityWrapper<CfPackingList>();
            CfPackingList packListEntity = new CfPackingList();
            packListEntity.setSaleOrder(saleOrder);
            wrapper.setEntity(packListEntity);
            List<CfPackingList> cfPackingListList = cfPackingListService.selectList(wrapper);
            if (cfPackingListList.size() == 0) {
                return new R<>(R.FAIL, "销售订单" + saleOrder + "不存在");
            }
            for (int i = 0, len = cfPackingListList.size(); i < len; i++) {
                if (!caseNoList.contains(cfPackingListList.get(i).getCaseNo())) {
                    caseNoList.add(cfPackingListList.get(i).getCaseNo());
                }

            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(caseNoList);
    }

    @GetMapping("/validateCaseNo")
    @ApiOperation(value = "部品装箱清单补打印-打印验证箱号")
    @ApiImplicitParam(name = "caseNo", value = "箱号", dataType = "string", paramType = "query")
    public R<List<CfPackingList>> validateCaseNo(String caseNo) {
        CfPackingList packListModel;
        try {
            if (StrUtil.isBlank(caseNo)) {
                throw new Exception("箱号不能为空");
            }

            EntityWrapper<CfPackingList> entityWrapper = new EntityWrapper<>();
            packListModel = new CfPackingList();
            packListModel.setCaseNo(caseNo.trim());
            entityWrapper.setEntity(packListModel);
            List<CfPackingList> cfPackingListList = cfPackingListService.selectList(entityWrapper);
            if (cfPackingListList.size() == 0) {
                return new R<>(R.FAIL, "箱号" + caseNo + "不存在");
            }

            return new R<>(cfPackingListList);

        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfDeliverGoods
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfDeliverGoods> get(@RequestParam Integer id) {
        return new R<>(cfDeliverGoodsService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询部品发货临时表")
    public R<Page> page(@RequestParam Map<String, Object> params, CfDeliverGoods cfDeliverGoods) {
        return new R<>(cfDeliverGoodsService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfDeliverGoods)));
    }

    /**
     * 添加
     *
     * @param cfDeliverGoods 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加部品发货临时表")
    public R<Boolean> add(@RequestBody CfDeliverGoods cfDeliverGoods, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfDeliverGoods.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfDeliverGoodsService.insert(cfDeliverGoods));
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }


    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value = "删除部品发货临时表通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfDeliverGoods cfDeliverGoods = new CfDeliverGoods();
        return new R<>(cfDeliverGoodsService.updateById(cfDeliverGoods));
    }

    /**
     * 编辑
     *
     * @param cfDeliverGoods 实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除部品发货临时表")
    public R<Boolean> edit(@RequestBody CfDeliverGoods cfDeliverGoods) {
        return new R<>(cfDeliverGoodsService.updateById(cfDeliverGoods));
    }
}
