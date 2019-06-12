package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value="采购或委外收货对象",description="采购或委外收货对象")
@Data
public class PurchaseOrOutsourceOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="订单",name="orderNo")
    private String orderNo;
    @ApiModelProperty(value="订单类型",name="orderType")
    private String orderType;
    @ApiModelProperty(value="物料代码",name="item")
    private String item;
    @ApiModelProperty(value="物料名称",name="itemDesc")
    private String itemDesc;
    @ApiModelProperty(value="供应商代码",name="vendor")
    private String vendor;
    @ApiModelProperty(value="供应商描述",name="vendorDesc")
    private String vendorDesc;
    @ApiModelProperty(value="行项目",name="rowItem")
    private String rowItem;
    @ApiModelProperty(value="订单数量",name="qty")
    private BigDecimal qty;
    @ApiModelProperty(value="默认仓库",name="defaultStorageLocation")
    private String defaultStorageLocation ;
    @ApiModelProperty(value="未清数量",name="demandQty")
    private BigDecimal demandQty ;
    @ApiModelProperty(value="规格型号",name="mode")
    private String mode ;
    @ApiModelProperty(value="采购组",name="purchaseGroup")
    private String purchaseGroup ;
    @ApiModelProperty(value="用途",name="itemPurpose")
    private String itemPurpose ;
    @ApiModelProperty(value="制单日期",name="orderDate")
    private Date orderDate ;
    @ApiModelProperty(value="状态",name="status")
    private String status ;

}
