package com.cfmoto.bar.code.model.vo;

import com.cfmoto.bar.code.model.entity.CfAllotInfo;
import com.cfmoto.bar.code.model.entity.CfAllotInventory;
import com.cfmoto.bar.code.model.entity.CfAllotOnWayData;
import com.cfmoto.bar.code.model.entity.CfAllotScanRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 调拨管理Vo对象，用于返回给视图层展示
 *
 * @author ye
 * @date 2019-04-02
 */
@ApiModel(value = "cfAllotManagementVo", description = "调拨管理Vo对象")
@Data
public class CfAllotManagementVo {

    @ApiModelProperty(value = "调拨信息对象", name = "cfAllotInfo")
    private CfAllotInfo cfAllotInfo;

    @ApiModelProperty(value = "调拨清单集合对象", name = "cfAllotInventoryList")
    private List<CfAllotInventory> cfAllotInventoryList;

    @ApiModelProperty(value = "调拨扫描记录集合对象", name = "cfAllotScanRecordList")
    private List<CfAllotScanRecord> cfAllotScanRecordList;

    @ApiModelProperty(value = "调拨在途数据集合对象", name = "cfAllotOnWayDataList")
    private List<CfAllotOnWayData> cfAllotOnWayDataList;

    @ApiModelProperty(value = "sap接口返回状态", name = "sapStatus")
    private String sapStatus;

    @ApiModelProperty(value = "sap接口返回信息", name = "sapStatus")
    private String sapMsg;

    @ApiModelProperty(value = "sap接口返回的订单状态", name = "orderStatus")
    private String orderStatus;

    @ApiModelProperty(value = "物料代码", name = "materialsNo")
    private String materialsNo;

    @ApiModelProperty(value = "物料名称", name = "materialsName")
    private String materialsName;

    @ApiModelProperty(value = "未清数量", name = "unClearedNumber")
    private Integer unClearedNumber;

    @ApiModelProperty(value = "条码数量", name = "barcodeNumber")
    private Integer barcodeNumber;

    @ApiModelProperty(value = "调拨单号", name = "orderNo")
    private String orderNo;

    @ApiModelProperty(value = "条码", name = "barcode")
    private String barcode;

    @ApiModelProperty(value = "箱号", name = "caseNo")
    private String caseNo;

    @ApiModelProperty(value = "操作类型", name = "opType")
    private String opType;

    @ApiModelProperty(value = "物料批次匹配集合", name = "batchMatchList")
    private List<Map<String, Object>> batchMatchList;

}
