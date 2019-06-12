package com.cfmoto.bar.code.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 备料拆分数据传输对象,主要用于sap接口方法参数的传输
 *
 * @author ye
 */
@ApiModel(value = "CfStockSplitDto", description = "sap接口方法参数传输对象")
@Data
public class CfStockSplitDto {

    public static final String INVALID_BARCODE = "条码无效或不存在备料交接数据!!!";

    public static final String NO_STOCK_LIST_INFO = "该条码对应的备料信息数据不存在!!!";

    public static final String NO_STOCK_INVENTORY = "对应的备料清单数据不存在!!!";

    public static final String SAP_STATUS_CODE_SUCCESS = "1";

    public static final String SAP_STATUS_CODE_FAIL = "0";

    /**
     * 物料编号
     */
    @ApiModelProperty(value = "物料编号")
    private String materialsNo;

    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String materialsName;
    /**
     * 条码类型
     */
    @ApiModelProperty(value = "条码类型")
    private String barcodeType;
    /**
     * 备料单号
     */
    @ApiModelProperty(value = "备料单号")
    private String stockListNo;
    /**
     * 批次
     */
    @ApiModelProperty(value = "批次")
    private String batchNo;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;
    /**
     * 备料仓库
     */
    @ApiModelProperty(value = "备料仓库")
    private String stockWarehouse;

    /**
     * sap返回的执行状态（1：成功，0：失败）
     */
    @ApiModelProperty(value = "sap返回状态")
    private String sapStatus;

    /**
     * sap返回的提示信息
     */
    @ApiModelProperty(value = "sap返回提示信息")
    private String sapMsg;
}
