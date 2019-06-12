package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="采购或委外确认收货对象",description="采购或委外确认收货对象")
@Data
public class OrderReceiveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="订单",name="orderNo")
    private String orderNo;
    @ApiModelProperty(value="行项目",name="rowItem")
    private String rowItem;
    @ApiModelProperty(value="物料代码",name="item")
    private String item;
    @ApiModelProperty(value="收货数量",name="qty")
    private String qty;
    @ApiModelProperty(value="仓库",name="storageLocation")
    private String storageLocation ;
    @ApiModelProperty(value="收货区域",name="inspectArea")
    private String inspectArea ;
    @ApiModelProperty(value="状态:普通-N,加急：-Y",name="status")
    private String status ;
    @ApiModelProperty(value="批次",name="batchNo")
    private String batchNo ;
    @ApiModelProperty(value="加工手册",name="handbook")
    private String handbook ;

}
