package com.cfmoto.bar.code.model.bo;

import com.cfmoto.bar.code.model.entity.CfAllotOnWayData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 在途数据Bo类
 *
 * @author ye
 */
@ApiModel(value = "cfAllotOnWayDataBo", description = "在途数据业务类")
@Data
public class CfAllotOnWayDataBo {


    /**
     * 在途数据对象
     */
    @ApiModelProperty(value = "在途数据对象")
    private CfAllotOnWayData cfAllotOnWayData;

    /**
     * 待打印数量
     */
    @ApiModelProperty(value = "待打印数量")
    private Integer toBePrintedNumber;

    /**
     * 拆分单位
     */
    @ApiModelProperty(value = "拆分单位")
    private Integer splitUnit;
}
