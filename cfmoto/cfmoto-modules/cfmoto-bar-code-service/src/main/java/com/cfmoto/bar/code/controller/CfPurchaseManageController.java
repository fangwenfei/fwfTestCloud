package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.KtmOrderVo;
import com.cfmoto.bar.code.model.vo.OrderFullVo;
import com.cfmoto.bar.code.model.vo.OrderReceiveVo;
import com.cfmoto.bar.code.model.vo.PurchaseOrOutsourceOrderVo;
import com.cfmoto.bar.code.service.*;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.vo.UserVO;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cfPurchaseManage")
@Api(tags = " 采购管理")
@Slf4j
public class CfPurchaseManageController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ICfStorageLocationService iCfStorageLocationService;

    @Autowired
    private ICfOrderManageService iCfOrderManageService;

    @Autowired
    private ICfOrderTempService iCfOrderTempService;

    @Autowired
    private ICfOrderScanTempService iCfOrderScanTempService;

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private PurchaseOrderSearchService purchaseOrderSearchService;

    @GetMapping("getStorageLocation")
    @ApiOperation(value = "采购收货-获取仓库数据")
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

    @GetMapping("/getPartsPurchaseOrder")
    @ApiOperation(value = "采购收货（零部件）-获取采购订单数据")
    @ApiImplicitParam(name = "orderNo", value = "采购订单", dataType = "string", paramType = "query")
    public R<List<PurchaseOrOutsourceOrderVo>> getPartsPurchaseOrder(String orderNo) {

        List<PurchaseOrOutsourceOrderVo> outsourceOrderList;
        try {
            if (StrUtil.isBlank(orderNo)) {
                return new R<>(R.FAIL, "采购订单不能为空");
            }
            if (orderNo.trim().length() > 10) {
                return new R<>(R.FAIL, "采购订单长度不能超过10位");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            outsourceOrderList = iCfOrderManageService.getPartsPurchaseOrder(userId, orderNo.trim(), "");
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(outsourceOrderList);
    }


    @PostMapping("/purchasePartsReceiveGoods")
    @ApiOperation(value = "采购收货（零部件）-收货确认")
    public R<List<PurchaseOrOutsourceOrderVo>> purchasePartsReceiveGoods(@RequestBody OrderReceiveVo orderReceiveVo) {

        List<PurchaseOrOutsourceOrderVo> outsourceOrderList;
        try {

            int userId = UserUtils.getUserId(httpServletRequest);
            String orderNo = orderReceiveVo.getOrderNo();
            if (StrUtil.isBlank(orderNo)) {
                return new R<>(R.FAIL, "采购订单不能为空");
            }
            if (orderNo.trim().length() > 10) {
                return new R<>(R.FAIL, "采购订单长度不能超过10位");
            }
            outsourceOrderList = iCfOrderManageService.purchasePartsReceiveGoods(userId, orderReceiveVo);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(outsourceOrderList);
    }


    @GetMapping("/getKtmPurchaseOrder")
    @ApiOperation(value = "采购收货（KTM整车）-获取采购订单数据")
    @ApiImplicitParam(name = "orderNo", value = "采购订单", dataType = "string", paramType = "query")
    public R<List<PurchaseOrOutsourceOrderVo>> getKtmPurchaseOrder(String orderNo) {

        List<PurchaseOrOutsourceOrderVo> outsourceOrderList;
        try {
            if (StrUtil.isBlank(orderNo)) {
                return new R<>(R.FAIL, "KTM采购订单不能为空");
            }
            if (orderNo.trim().length() > 10) {
                return new R<>(R.FAIL, "KTM采购订单长度不能超过10位");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            //Z007 KTM采购订单
            outsourceOrderList = iCfOrderManageService.getKtmPurchaseOrder(userId, orderNo.trim(), "Z007");
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(outsourceOrderList);
    }


    @PostMapping("/ktmPurchaseOrderReceiveGoods")
    @ApiOperation(value = "采购收货（KTM整车）-收货确认")
    public R<List<PurchaseOrOutsourceOrderVo>> ktmPurchaseOrderReceiveGoods(@RequestBody KtmOrderVo ktmOrderVo) {

        List<PurchaseOrOutsourceOrderVo> outsourceOrderList;
        try {

            int userId = UserUtils.getUserId(httpServletRequest);
            String orderNo = ktmOrderVo.getOrderNo();
            if (StrUtil.isBlank(orderNo)) {
                return new R<>(R.FAIL, "KTM采购订单不能为空");
            }
            if (orderNo.trim().length() > 10) {
                return new R<>(R.FAIL, "KTM采购订单长度不能超过10位");
            }
            outsourceOrderList = iCfOrderManageService.ktmPurchasePartsReceiveGoods(userId, ktmOrderVo);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(outsourceOrderList);
    }


    @GetMapping("/getReturnPurchaseOrder")
    @ApiOperation(value = "采购退货-获取采购订单数据")
    @ApiImplicitParam(name = "outSourceOrder", value = "采购退货单", dataType = "string", paramType = "query")
    public R<OrderFullVo> getReturnPurchaseOrder(String outSourceOrder) {

        OrderFullVo orderFullVo;
        try {
            if (StrUtil.isBlank(outSourceOrder)) {
                return new R<>(R.FAIL, "采购退货单不能为空");
            }
            if (outSourceOrder.trim().length() > 10) {
                return new R<>(R.FAIL, "采购退货单长度不能超过10位");
            }
            int userId = UserUtils.getUserId(httpServletRequest);
            orderFullVo = iCfOrderTempService.outSourceOrderData(userId, outSourceOrder.trim());
            if (!"Z010".equalsIgnoreCase(orderFullVo.getOrderType())) {
                throw new Exception("该采购订单不是退货采购订单，请注意！");
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(orderFullVo);
    }


    @GetMapping("/barcodeScan")
    @ApiOperation(value = "采购退货-条码扫描")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "outSourceOrder", value = "采购单", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "barcode", value = "条码", dataType = "string", paramType = "query")
    })
    public R<OrderFullVo> barcodeScan(String outSourceOrder, String barcode) {

        OrderFullVo orderFullVo;
        try {

            int userId = UserUtils.getUserId(httpServletRequest);
            if (StrUtil.isBlank(outSourceOrder)) {
                return new R(R.FAIL, "采购退货单不能为空");
            }

            if (outSourceOrder.trim().length() > 10) {
                return new R<>(R.FAIL, "采购退货长度不能超过10位");
            }

            if (StrUtil.isBlank(barcode)) {
                return new R<>(R.FAIL, "条码不能为空");
            }
            iCfOrderScanTempService.scanBarcode(userId, outSourceOrder.trim(), barcode.trim());
            orderFullVo = iCfOrderTempService.outSourceOrderData(userId, outSourceOrder.trim());
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(orderFullVo);
    }

    @GetMapping("/getBarCodeByItem")
    @ApiOperation(value = "通过物料获取库存")
    @ApiImplicitParam(name = "item", value = "物料", dataType = "string", paramType = "query")
    public R<List<CfBarcodeInventory>> getBarCodeByItem(String item, HttpServletRequest request) {

        //校验前端输入数据
        if (StrUtil.isBlank(item)) {
            return new R<>(R.FAIL, "请输入物料代码!!!");
        }

        try {
            UserVO user = userFeignService.user(UserUtils.getUserId(request));
            List<CfBarcodeInventory> inventoryList = barcodeInventoryService.getInventoryFromSap(item.trim(), "", user.getUserId(), request.getRequestURI());
            return new R<>(inventoryList);
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    @PostMapping("/purchaseModifyRow")
    @ApiOperation(value = "采购退货-修改行")
    @ApiImplicitParam(name = "paramMap", value = "paramMap包含参数：orderNo 采购退货单，orderSumTempIdRef 汇总标识，orderScanTempId 扫描标识" +
            "，remarks 备注 可为空，qty 数量", dataType = "string", paramType = "query")
    public R<OrderFullVo> purchaseModifyRow(@RequestBody Map<String, Object> paramMap) {

        OrderFullVo orderFullVo;
        try {

            String orderNo = (String) paramMap.get("orderNo");
            String orderSumTempIdRef = (String) paramMap.get("orderSumTempIdRef");
            String orderScanTempId = (String) paramMap.get("orderScanTempId");
            String remarks = (String) paramMap.get("remarks");
            String qty = (String) paramMap.get("qty");
            int userId = UserUtils.getUserId(httpServletRequest);
            if (StrUtil.isBlank(orderNo)) {
                return new R<>(R.FAIL, "采购退货单不能为空");
            }
            if (orderNo.trim().length() > 10) {
                return new R<>(R.FAIL, "采购退货单长度不能超过10位");
            }
            if (StrUtil.isBlank(orderScanTempId)) {
                return new R<>(R.FAIL, "扫描标识不能为空");
            }
            if (StrUtil.isBlank(orderSumTempIdRef)) {
                return new R<>(R.FAIL, "汇总标识不能为空");
            }
            if (StrUtil.isBlank(qty)) {
                return new R<>(R.FAIL, "更改数量不能为空");
            }
            iCfOrderScanTempService.ourSourceModifyRow(userId, orderNo.trim(), orderSumTempIdRef.trim(),
                    orderScanTempId.trim(), remarks.trim(), qty.trim());
            orderFullVo = iCfOrderTempService.outSourceOrderData(userId, orderNo.trim());
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(orderFullVo);
    }


    @GetMapping("/purchaseDeleteRow")
    @ApiOperation(value = "采购退货单删除行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "采购退货单", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderSumTempIdRef", value = "扫描标识", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "orderScanTempId", value = "汇总标识", dataType = "string", paramType = "query")
    })
    public R<OrderFullVo> purchaseDeleteRow(String orderNo, String orderSumTempIdRef, String orderScanTempId) {

        OrderFullVo orderFullVo;
        try {

            int userId = UserUtils.getUserId(httpServletRequest);
            if (StrUtil.isBlank(orderNo)) {
                return new R<>(R.FAIL, "采购退货单不能为空");
            }
            if (orderNo.trim().length() > 10) {
                return new R<>(R.FAIL, "采购退货单长度不能超过10位");
            }
            if (StrUtil.isBlank(orderScanTempId)) {
                return new R<>(R.FAIL, "扫描标识不能为空");
            }
            if (StrUtil.isBlank(orderSumTempIdRef)) {
                return new R<>(R.FAIL, "汇总标识不能为空");
            }
            iCfOrderScanTempService.ourSourceDeleteRow(userId, orderNo, orderSumTempIdRef, orderScanTempId);
            orderFullVo = iCfOrderTempService.outSourceOrderData(userId, orderNo.trim());
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(orderFullVo);
    }

    @GetMapping("/purchaseSendOutGoods")
    @ApiOperation(value = "采购退货单-提交")
    @ApiImplicitParam(name = "orderNo", value = "出库单", dataType = "string", paramType = "query")
    public R<OrderFullVo> purchaseSendOutGoods(String orderNo) {

        OrderFullVo orderFullVo;
        try {

            int userId = UserUtils.getUserId(httpServletRequest);
            if (StrUtil.isBlank(orderNo)) {
                return new R(R.FAIL, "采购退货单不能为空");
            }
            if (orderNo.trim().length() > 10) {
                return new R<>(R.FAIL, "采购退货单长度不能超过10位");
            }
            orderFullVo = iCfOrderManageService.purchaseSendOutGoods(userId, orderNo.trim());
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(orderFullVo);
    }


    @GetMapping("/searchPurchaseOrderBySap")
    @ApiOperation(value = "采购订单查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "采购订单", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vendor", value = "供应商", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "item", value = "物料代码", dataType = "string", paramType = "query")
    })
    public R<List<PurchaseOrOutsourceOrderVo>> searchPurchaseOrderBySap(String orderNo, String vendor, String item) {
        try {
            List<PurchaseOrOutsourceOrderVo> purchaseOrOutsourceOrderVoList = purchaseOrderSearchService.searchPurchaseOrderBySap(orderNo, vendor, item);
            return new R<>(purchaseOrOutsourceOrderVoList);
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }
    }

}
