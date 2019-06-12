package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="KTM确认收货对象",description="KTM确认收货对象")
@Data
public class KtmOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="车辆条码",name="barcode")
    private String barcode;
    @ApiModelProperty(value="订单",name="orderNo")
    private String orderNo;
    @ApiModelProperty(value="行项目",name="rowItem")
    private String rowItem;
    @ApiModelProperty(value="物料代码",name="item")
    private String item;
    @ApiModelProperty(value="物料名称",name="itemDesc")
    private String itemDesc;
    @ApiModelProperty(value="收货区域",name="receiveArea")
    private String receiveArea;
    @ApiModelProperty(value="仓库",name="storageLocation")
    private String storageLocation ;
    @ApiModelProperty(value="批次",name="batchNo")
    private String batchNo ;

}
