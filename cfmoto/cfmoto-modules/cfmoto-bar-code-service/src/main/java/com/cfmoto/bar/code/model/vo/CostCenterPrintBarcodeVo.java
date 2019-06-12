package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="成本中心退料入库标签打印",description="库存条码返回")
public class CostCenterPrintBarcodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "条码")
    private String barcode;

    @ApiModelProperty(value = "数量")
    private String qty;


}
