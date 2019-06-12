package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="三码绑定验证返回对象",description="三码绑定验证返回对象")
@Data
public class ThreeCodeBindVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="单号")
    private String carType;

    @ApiModelProperty(value="返回消息")
    private String message;

    @ApiModelProperty(value="生产任务单")
    private String productionTaskOrder;

}
