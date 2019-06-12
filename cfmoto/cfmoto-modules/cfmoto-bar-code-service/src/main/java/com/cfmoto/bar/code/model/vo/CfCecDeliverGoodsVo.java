package com.cfmoto.bar.code.model.vo;

import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsInfo;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsInventory;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsScanRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 部品网购发货-视图层对象
 *
 * @author yezi
 * @date 2019/6/4
 */
@Component
@ApiModel(value = "CfCecDeliverGoodsVo", description = "部品网购发货-视图层对象")
@Data
public class CfCecDeliverGoodsVo {

    @ApiModelProperty(value = "部品网购发货-信息表对象", name = "info")
    private CfCecDeliverGoodsInfo info;

    @ApiModelProperty(value = "部品网购发货-清单表对象集合", name = "inventoryList")
    private List<CfCecDeliverGoodsInventory> inventoryList;

    @ApiModelProperty(value = "部品网购发货-扫描表对象集合", name = "scanRecordList")
    private List<CfCecDeliverGoodsScanRecord> scanRecordList;

    @ApiModelProperty(value = "物料批次匹配集合", name = "batchMatchList")
    private List<Map<String, Object>> batchMatchList;

    @ApiModelProperty(value = "物料代码", name = "materialsNo")
    private String materialsNo;

    @ApiModelProperty(value = "物料名称", name = "materialsName")
    private String materialsName;

    @ApiModelProperty(value = "未清数量", name = "unClearedNumber")
    private Integer unClearedNumber;

    @ApiModelProperty(value = "条码数量", name = "barcodeNumber")
    private Integer barcodeNumber;

    @ApiModelProperty(value = "交货单号", name = "deliverOrderNo")
    private String deliverOrderNo;

    @ApiModelProperty(value = "行项目", name = "rowItem")
    private String rowItem;

    @ApiModelProperty(value = "运单号", name = "trackingNo")
    private String trackingNo;

    @ApiModelProperty(value = "条码", name = "barcode")
    private String barcode;

    @ApiModelProperty(value = "销售订单", name = "salesOrderNo")
    private String salesOrderNo;

    @ApiModelProperty(value = "订单类型", name = "orderType")
    private String orderType;

    @ApiModelProperty(value = "订单状态:C-已完成 U-未完成", name = "orderStatus")
    private String orderStatus;

    @ApiModelProperty(value = "工厂", name = "factory")
    private String factory;
}
