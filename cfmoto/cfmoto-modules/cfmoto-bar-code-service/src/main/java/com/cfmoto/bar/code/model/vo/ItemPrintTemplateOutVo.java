package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="物料打印末班",description="部品零部件标签打印-物料查询标签模板返回")
public class ItemPrintTemplateOutVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value = "物料代码" )
    private String item;

    @ApiModelProperty( value = "打印模板名称" )
    private String printLodopTemplateName;

    @ApiModelProperty( value = "打印模板内容" )
    private String printLodopTemplate;
}
