package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="委外出库",description="委外出库")
@Data
public class OutSourceOutVo implements Serializable {

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
    @ApiModelProperty(value="存储区域",name="storageLocationArea")
    private String storageLocationArea ;
    @ApiModelProperty(value="仓位",name="storagePosition")
    private String storagePosition ;
    @ApiModelProperty(value="批次",name="batchNo")
    private String batchNo ;

}
