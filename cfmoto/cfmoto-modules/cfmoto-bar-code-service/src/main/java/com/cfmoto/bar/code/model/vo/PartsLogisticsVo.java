package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="PartsLogisticsVo",description="部品功能-部品物流面单打印使用")
@Data
public class PartsLogisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="单号")
    private String orderNo;

    @ApiModelProperty(value="销售订单号")
    private String saleOrderNo;

    @ApiModelProperty(value="客户名称")
    private String customerName;

    @ApiModelProperty(value="收件地址")
    private String receiveAddress;

    @ApiModelProperty(value="收件人")
    private String receiveContactName;

    @ApiModelProperty(value="收件联系方式")
    private String receiveContactPhoneNumber;

    @ApiModelProperty(value="发件人")
    private String addresser;

    @ApiModelProperty(value="发件人联系方式")
    private String addresserPhoneNumber;

}
