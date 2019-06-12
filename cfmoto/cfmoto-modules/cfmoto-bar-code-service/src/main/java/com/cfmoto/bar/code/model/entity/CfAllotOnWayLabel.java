package com.cfmoto.bar.code.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 调拨在途标签打印模板
 *
 * @author ye
 */
@ApiModel(value = "cfAllotOnWayLabel", description = "调拨在途标签打印模板对象")
@Data
public class CfAllotOnWayLabel {

    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String materialsName;
    /**
     * 物料代码
     */
    @ApiModelProperty(value = "物料代码")
    private String materialsNo;
    /**
     * 批次
     */
    @ApiModelProperty(value = "批次")
    private String batchNo;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;
    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    private String spec;
    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private String supplier;
    /**
     * 打印单据 调拨单号
     */
    @ApiModelProperty(value = "打印单据")
    private String printOrderInvoice;
    /**
     * 条码
     */
    @ApiModelProperty(value = "条码")
    private String barcode;
}
