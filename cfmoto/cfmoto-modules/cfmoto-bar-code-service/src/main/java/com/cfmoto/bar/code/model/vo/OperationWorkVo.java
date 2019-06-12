package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="OperationWorkVo",description="工序报工对象")
@Data
public class OperationWorkVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="任务单号",name="taskNo")
    private String taskNo;
    @ApiModelProperty(value="工序",name="operation")
    private String operation;
    @ApiModelProperty(value="未清数量",name="operationUnclearQty")
    private String operationUnclearQty;

}
