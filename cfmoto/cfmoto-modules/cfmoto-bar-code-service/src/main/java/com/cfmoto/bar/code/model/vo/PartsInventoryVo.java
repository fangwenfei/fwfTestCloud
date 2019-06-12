package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value="部品库存查询",description="部品功能-部品库存查询使用")
@Data
public class PartsInventoryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="物料代码",name="item")
    private String item;

    @ApiModelProperty(value="物料名称",name="itemDesc")
    private String itemDesc;

    @ApiModelProperty(value="规格型号",name="mode")
    private String mode;

    @ApiModelProperty(value="数量",name="qty")
    private BigDecimal qty;

    @ApiModelProperty(value="批次",name="batchNo")
    private String batchNo;

    @ApiModelProperty(value="仓库",name="storageLocation")
    private String storageLocation;

    @ApiModelProperty(value="SP储位号",name="spStorageLocationPosition")
    private String spStorageLocationPosition;
}
