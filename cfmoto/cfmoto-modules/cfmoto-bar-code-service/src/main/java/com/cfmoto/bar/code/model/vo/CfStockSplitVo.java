package com.cfmoto.bar.code.model.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/* **********************************************************************
 *              Created by FangWenFei on 2019/4/22.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@ApiModel(value = "CfStockSplitVo", description = "备料拆分视图")
@Data
public class CfStockSplitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料代码")
    private String materialsNo;

    @ApiModelProperty(value = "数量")
    private BigDecimal number;

    @ApiModelProperty(value="批次")
    private String batchNo;

    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    private String materialsName;
}
