package com.cfmoto.bar.code.model.entity;

import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-12
 */
@TableName("cf_stock_handover_scan_record")
@ApiModel(value = "CfStockHandoverScanRecord", description = "备料交接扫描记录表")
@Data
public class CfStockHandoverScanRecord extends Model<CfStockHandoverScanRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 备料交接扫描记录表主键
     */
    @ApiModelProperty(value = "备料交接扫描记录表主键")
    @TableId(value = "stock_handover_scan_record_id", type = IdType.AUTO)
    private Integer stockHandoverScanRecordId;
    /**
     * 备料单号
     */
    @ApiModelProperty(value = "备料单号")
    @TableField("stock_list_no")
    private String stockListNo;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    @TableField("materials_name")
    private String materialsName;
    /**
     * 物料代码
     */
    @ApiModelProperty(value = "物料代码")
    @TableField("materials_no")
    private String materialsNo;
    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    private String spec;
    /**
     * 条码
     */
    @ApiModelProperty(value = "条码")
    private String barcode;
    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    private String repository;
    /**
     * 存储区域
     */
    @ApiModelProperty(value = "存储区域")
    @TableField("storage_area")
    private String storageArea;
    /**
     * 批次
     */
    @ApiModelProperty(value = "批次")
    @TableField("batch_no")
    private String batchNo;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;
    /**
     * 数据创建人
     */
    @ApiModelProperty(value = "数据创建人")
    @TableField("created_by")
    private Integer createdBy;
    /**
     * 数据创建时间
     */
    @ApiModelProperty(value = "数据创建时间")
    @TableField("creation_date")
    private Date creationDate;
    /**
     * 最后修改人
     */
    @ApiModelProperty(value = "最后修改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后修改时间
     */
    @ApiModelProperty(value = "最后修改时间")
    @TableField("last_updated_date")
    private Date lastUpdatedDate;
    /**
     * 备料单信息表主键
     */
    @ApiModelProperty(value = "备料单信息表主键")
    @TableField("stock_list_id")
    private Integer stockListId;
    /**
     * 备料清单表主键
     */
    @ApiModelProperty(value = "备料清单表主键")
    @TableField("stock_inventory_id")
    private Integer stockInventoryId;

    @Override
    protected Serializable pkVal() {
        return this.stockHandoverScanRecordId;
    }

    public void setObjectSetBasicAttribute(int userId, Date date) {
        this.createdBy = userId;
        this.lastUpdatedBy = userId;
        this.lastUpdatedDate = date;
        this.creationDate = date;
    }

}
