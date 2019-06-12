package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value="成本中心退料入库标签打印",description="成本中心退料入库标签打印-返回打印数据")
public class CostCenterPrintOutVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "打印模板内容")
    private String printLodopTemplate;

    @ApiModelProperty(value = "批次")
    private String batchNo;

    @ApiModelProperty(value = "库存条码")
    private List<CostCenterPrintBarcodeVo> costCenterPrintBarcodeVoList;


}
