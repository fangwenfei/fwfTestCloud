package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/* **********************************************************************
 *              Created by FangWenFei on 2019/4/24.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Data
public class JustInTimeInventoryPrintVo implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String  NEXT_NUMBER_TYPE ="JUST_IN_TIME_INVENTORY_PRINT";

    public static final String  EX_FACTORY ="该数据的仓库无法在条码的仓库中找到，请及时维护";

    public static final String  STATE_OK_MASSAGE ="合格";

    public static final String  STATE_NO_MASSAGE ="不合格";
    @ApiModelProperty(value="物料代码",name="MATNR")
    private String MATNR;

    @ApiModelProperty(value="物料描述",name="MAKTX")
    private String MAKTX;

    @ApiModelProperty(value="基本计量单位",name="MATNR")
    private String MEINS;

    @ApiModelProperty(value="规格型号",name="WRSKT")
    private String WRSKT;

    @ApiModelProperty(value="批次",name="CHARG")
    private String CHARG;

    @ApiModelProperty(value="仓库代码",name="LGORT")
    private String LGORT;

    @ApiModelProperty(value="仓库名称",name="LGOBE")
    private String LGOBE;

    @ApiModelProperty(value="非限制库存",name="ZFXKC")
    private String ZFXKC;

    @ApiModelProperty(value="质检库存",name="ZZJKC")
    private String ZZJKC;

    @ApiModelProperty(value="调拨在途库存",name="CUMLM")
    private String CUMLM;

    @ApiModelProperty(value="存储区域",name="LGTYP")
    private String LGTYP;

    @ApiModelProperty(value="存储区域名称",name="LTYPT")
    private String LTYPT;

    @ApiModelProperty(value="仓位代码",name="LGPLA")
    private String LGPLA;

    @ApiModelProperty(value="生产/采购日期",name="LWEDT")
    private String LWEDT;

    @ApiModelProperty(value="最小包装数",name="SCMNG")
    private String SCMNG;

    @ApiModelProperty(value="供应商",name="NAME_ORG1")
    private String NAME_ORG1;

    @ApiModelProperty(value="采购组",name="EKGRP")
    private String EKGRP;

    @ApiModelProperty(value="状态",name="STATE")
    private String STATE;

    @ApiModelProperty(value="数量",name="NUMBER")
    private String NUMBER;

    @ApiModelProperty(value="工厂",name="WERKS")
    private String WERKS;


}
