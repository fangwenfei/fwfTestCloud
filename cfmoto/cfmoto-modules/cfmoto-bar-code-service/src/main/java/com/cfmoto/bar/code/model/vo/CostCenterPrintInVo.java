package com.cfmoto.bar.code.model.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="成本中心退料入库标签打印",description="成本中心退料入库标签打印-接受打印数据")
public class CostCenterPrintInVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "功能模块")
    private String functionName;

    @ApiModelProperty(value = "打印模板")
    private String printLodopTemplateName;

    @ApiModelProperty(value = "数量")
    private int printQty;

    @ApiModelProperty(value = "拆分数量")
    private int splitQty;

    @ApiModelProperty(value = "物料代码")
    private String item;

    @ApiModelProperty(value = "物料名称")
    private String itemDesc;

    @ApiModelProperty(value = "规格")
    private String mode;

    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @ApiModelProperty(value = "供应商")
    private String suppler;


    /**
     * 领料单号或退料单号
     */
    @ApiModelProperty(value="领料单号或退料单号")
    private String orderNo;

}
