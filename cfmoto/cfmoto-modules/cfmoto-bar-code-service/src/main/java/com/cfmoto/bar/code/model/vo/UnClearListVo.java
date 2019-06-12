package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value="未清清单",description="部品发货-未清清单查询使用")
@Data
public class UnClearListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="单号",name="orderNo")
    private String orderNo;
    @ApiModelProperty(value="销售订单",name="saleOrder")
    private String saleOrder;
    @ApiModelProperty(value="客户",name="customer")
    private String customer;
    @ApiModelProperty(value="制单日期",name="orderDate")
    private BigDecimal orderDate;
    @ApiModelProperty(value="发运方式",name="sendMethod")
    private String sendMethod;
}
