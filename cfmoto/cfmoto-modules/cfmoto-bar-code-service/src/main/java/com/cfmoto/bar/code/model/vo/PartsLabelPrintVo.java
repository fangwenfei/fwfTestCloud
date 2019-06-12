package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value="部品零部件标签打印",description="部品零部件标签模板对照信息维护-查询功能使用")
@Data
public class PartsLabelPrintVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value="单号",name="orderNo" )
    private String orderNo;

    @ApiModelProperty( value="物料代码",name="item" )
    private String item;

    @ApiModelProperty(value="物料名称",name="itemDesc")
    private String itemDesc;

    @ApiModelProperty( value="规格型号",name="mode" )
    private String mode;

    @ApiModelProperty( value="数量",name="qty")
    private BigDecimal qty;

    @ApiModelProperty( value="SP储位号",name="spStorageLocationPosition" )
    private String spStorageLocationPosition;

    @ApiModelProperty( value="图号",name="mapNumber" )
    private String mapNumber;

    @ApiModelProperty( value="最小包装量",name ="minimumPackageNumber" )
    private Integer minimumPackageNumber;

    @ApiModelProperty( value="销售价格", name="salePrice" )
    private BigDecimal salePrice;

    @ApiModelProperty( value="英文名称", name = "englishName" )
    private String englishName;


}
