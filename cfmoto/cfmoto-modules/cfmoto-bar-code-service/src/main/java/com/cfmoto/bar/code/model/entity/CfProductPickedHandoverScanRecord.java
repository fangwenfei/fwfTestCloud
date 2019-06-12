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

/**
 * <p>
 * 生产领料交接扫描记录表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-04-22
 */
@TableName("cf_product_picked_handover_scan_record")
@ApiModel(value="CfProductPickedHandoverScanRecord",description="生产领料交接扫描记录表")
public class CfProductPickedHandoverScanRecord extends Model<CfProductPickedHandoverScanRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value="主键id")
    @TableId(value = "product_picked_handover_scan_record_id", type = IdType.AUTO)
    private Integer productPickedHandoverScanRecordId;
    /**
     * 备料单号
     */
    @ApiModelProperty(value="备料单号")
    @TableField("stock_list_no")
    private String stockListNo;
    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("materials_name")
    private String materialsName;
    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    @TableField("materials_no")
    private String materialsNo;
    /**
     * 条码
     */
    @ApiModelProperty(value="条码")
    private String barcode;
    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    private String repository;
    /**
     * 批次
     */
    @ApiModelProperty(value="批次")
    @TableField("batch_no")
    private String batchNo;
    /**
     * 数量
     */
    @ApiModelProperty(value="数量")
    private Integer number;
    /**
     * 数据创建人
     */
    @ApiModelProperty(value="数据创建人")
    @TableField("created_by")
    private Integer createdBy;
    /**
     * 数据创建时间
     */
    @ApiModelProperty(value="数据创建时间")
    @TableField("creation_date")
    private Date creationDate;
    /**
     * 最后修改人
     */
    @ApiModelProperty(value="最后修改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后修改时间
     */
    @ApiModelProperty(value="最后修改时间")
    @TableField("last_updated_date")
    private Date lastUpdatedDate;
    /**
     * 备料单信息表的主键
     */
    @ApiModelProperty(value="备料单信息表的主键")
    @TableField("stock_list_id")
    private Integer stockListId;
    /**
     * 备料清单表的主键
     */
    @ApiModelProperty(value="备料清单表的主键")
    @TableField("stock_inventory_id")
    private Integer stockInventoryId;


    public Integer getProductPickedHandoverScanRecordId() {
        return productPickedHandoverScanRecordId;
    }

    public void setProductPickedHandoverScanRecordId(Integer productPickedHandoverScanRecordId) {
        this.productPickedHandoverScanRecordId = productPickedHandoverScanRecordId;
    }

    public String getStockListNo() {
        return stockListNo;
    }

    public void setStockListNo(String stockListNo) {
        this.stockListNo = stockListNo;
    }

    public String getMaterialsName() {
        return materialsName;
    }

    public void setMaterialsName(String materialsName) {
        this.materialsName = materialsName;
    }

    public String getMaterialsNo() {
        return materialsNo;
    }

    public void setMaterialsNo(String materialsNo) {
        this.materialsNo = materialsNo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Integer lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getStockListId() {
        return stockListId;
    }

    public void setStockListId(Integer stockListId) {
        this.stockListId = stockListId;
    }

    public Integer getStockInventoryId() {
        return stockInventoryId;
    }

    public void setStockInventoryId(Integer stockInventoryId) {
        this.stockInventoryId = stockInventoryId;
    }

    @Override
    protected Serializable pkVal() {
        return this.productPickedHandoverScanRecordId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdatedDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfProductPickedHandoverScanRecord{" +
        ", productPickedHandoverScanRecordId=" + productPickedHandoverScanRecordId +
        ", stockListNo=" + stockListNo +
        ", materialsName=" + materialsName +
        ", materialsNo=" + materialsNo +
        ", barcode=" + barcode +
        ", repository=" + repository +
        ", batchNo=" + batchNo +
        ", number=" + number +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdatedDate=" + lastUpdatedDate +
        ", stockListId=" + stockListId +
        ", stockInventoryId=" + stockInventoryId +
        "}";
    }
}
