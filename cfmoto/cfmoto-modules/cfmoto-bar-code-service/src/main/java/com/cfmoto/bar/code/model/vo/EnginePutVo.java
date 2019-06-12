package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="EnginePutVo",description="生产入库、发动机关联入库接受数据对象")
@Data
public class EnginePutVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="任务单号",name="taskNo")
    private String taskNo;
    @ApiModelProperty(value="物料",name="item")
    private String item;
    @ApiModelProperty(value="物料描述",name="itemDesc")
    private String itemDesc;
    @ApiModelProperty( value="仓库", name = "storageLocation" )
    private String storageLocation;
    @ApiModelProperty( value="条码", name = "engineBarCode" )
    private String barCode;
    @ApiModelProperty( value="车型", name = "carType" )
    private String carType;
    @ApiModelProperty( value="规格", name = "mode" )
    private String mode;
    @ApiModelProperty( value="销售订单", name = "saleOrder" )
    private String saleOrder;
    @ApiModelProperty( value="订单年份字段", name = "orderYear" )
    private String orderYear;

    @ApiModelProperty(value = "销售订单行项目", name = "saleOrderRowItem")
    private String saleOrderRowItem;

    @ApiModelProperty(value = "合同号", name = "contract")
    private String contract;
}
