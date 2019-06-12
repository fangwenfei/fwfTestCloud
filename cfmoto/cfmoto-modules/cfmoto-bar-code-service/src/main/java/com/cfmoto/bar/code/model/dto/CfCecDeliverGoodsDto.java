package com.cfmoto.bar.code.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部品网购发货DTO对象
 *
 * @author ye
 */
@ApiModel(value = "CfCecDeliverGoodsDto", description = "部品网购发货DTO对象")
@Data
public class CfCecDeliverGoodsDto {
    /**
     * 交货单号
     */
    @ApiModelProperty(value = "交货单号")
    private String deliverOrderNo;


    /**
     * 条码
     */
    @ApiModelProperty(value = "条码")
    private String barcode;


    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    private String warehouse;


    /**
     * 工厂
     */
    @ApiModelProperty(value = "工厂")
    private String factory;


    /**
     * 运单号
     */
    @ApiModelProperty(value = "运单号")
    private String trackingNo;


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private int userId;

}
