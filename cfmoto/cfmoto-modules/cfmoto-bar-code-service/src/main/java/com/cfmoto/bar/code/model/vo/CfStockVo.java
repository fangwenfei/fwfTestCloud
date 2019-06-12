package com.cfmoto.bar.code.model.vo;

import com.cfmoto.bar.code.model.entity.CfProductPickedHandoverScanRecord;
import com.cfmoto.bar.code.model.entity.CfStockInventory;
import com.cfmoto.bar.code.model.entity.CfStockListInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 生产物料管理
 * 生产备料交接查询中用到的视图对象
 *
 * @author ye
 * @date 2019-04-23
 */
@ApiModel(value = "CfStockVo", description = "生产备料交接查询中用到的视图对象")
@Data
public class CfStockVo {

    /**
     * 备料单信息表
     */
    @ApiModelProperty(value = "备料单信息表")
    private CfStockListInfo stockListInfo;

    /**
     * 备料单清单表（集合）
     */
    @ApiModelProperty(value = "备料单清单表")
    protected List<CfStockInventory> stockInventoryList;

    /**
     * 生产领料交接扫描记录表（集合）
     */
    @ApiModelProperty(value = "生产领料交接扫描记录表")
    private List<CfProductPickedHandoverScanRecord> productPickedHandoverScanRecordList;
}
