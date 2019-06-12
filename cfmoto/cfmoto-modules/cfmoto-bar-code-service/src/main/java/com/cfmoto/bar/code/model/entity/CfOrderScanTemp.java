package com.cfmoto.bar.code.model.entity;

import java.math.BigDecimal;
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
 * 订单扫描临时表
 * </p>
 *
 * @author  space
 * @since 2019-03-15
 */
@TableName("cf_order_scan_temp")
@ApiModel(value="CfOrderScanTemp",description="订单扫描临时表")
public class CfOrderScanTemp extends Model<CfOrderScanTemp> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId("order_scan_temp_id")
    private String orderScanTempId;
    /**
     * 订单汇总临时表-主键
     */
    @ApiModelProperty(value="订单汇总临时表-主键")
    @TableField("order_sum_temp_id_ref")
    private String orderSumTempIdRef;
    /**
     * 行项目
     */
    @ApiModelProperty(value="行项目")
    @TableField("row_item")
    private String rowItem;
    /**
     * 订单临时表-主键
     */
    @ApiModelProperty(value="订单临时表-主键")
    @TableField("order_temp_id_ref")
    private String orderTempIdRef;
    /**
     * 物料
     */
    @ApiModelProperty(value="物料")
    private String item;
    /**
     * 物料描述
     */
    @ApiModelProperty(value="物料描述")
    @TableField("item_desc")
    private String itemDesc;
    /**
     * 物料用途
     */
    @ApiModelProperty(value="物料用途")
    @TableField("item_purpose")
    private String itemPurpose;
    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String mode;
    /**
     * 条码
     */
    @ApiModelProperty(value="条码")
    private String barcode;
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
    private BigDecimal quantity;
    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    @TableField("storage_location")
    private String storageLocation;
    /**
     * 存储区域
     */
    @ApiModelProperty(value="存储区域")
    @TableField("storage_area")
    private String storageArea;
    /**
     * 仓位
     */
    @ApiModelProperty(value="仓位")
    @TableField("storage_position")
    private String storagePosition;
    /**
     * 供应商
     */
    @ApiModelProperty(value="供应商")
    private String vendor;
    /**
     * 状态
     */
    @ApiModelProperty(value="状态")
    private String status;
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
     * 最后更改人
     */
    @ApiModelProperty(value="最后更改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    @TableField("last_update_date")
    private Date lastUpdateDate;


    public String getOrderScanTempId() {
        return orderScanTempId;
    }

    public void setOrderScanTempId(String orderScanTempId) {
        this.orderScanTempId = orderScanTempId;
    }

    public String getOrderSumTempIdRef() {
        return orderSumTempIdRef;
    }

    public void setOrderSumTempIdRef(String orderSumTempIdRef) {
        this.orderSumTempIdRef = orderSumTempIdRef;
    }

    public String getRowItem() {
        return rowItem;
    }

    public void setRowItem(String rowItem) {
        this.rowItem = rowItem;
    }

    public String getOrderTempIdRef() {
        return orderTempIdRef;
    }

    public void setOrderTempIdRef(String orderTempIdRef) {
        this.orderTempIdRef = orderTempIdRef;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemPurpose() {
        return itemPurpose;
    }

    public void setItemPurpose(String itemPurpose) {
        this.itemPurpose = itemPurpose;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getStorageArea() {
        return storageArea;
    }

    public void setStorageArea(String storageArea) {
        this.storageArea = storageArea;
    }

    public String getStoragePosition() {
        return storagePosition;
    }

    public void setStoragePosition(String storagePosition) {
        this.storagePosition = storagePosition;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    protected Serializable pkVal() {
        return this.orderScanTempId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfOrderScanTemp{" +
                ", orderScanTempId=" + orderScanTempId +
                ", orderSumTempIdRef=" + orderSumTempIdRef +
                ", rowItem=" + rowItem +
                ", orderTempIdRef=" + orderTempIdRef +
                ", item=" + item +
                ", itemDesc=" + itemDesc +
                ", itemPurpose=" + itemPurpose +
                ", mode=" + mode +
                ", barcode=" + barcode +
                ", batchNo=" + batchNo +
                ", quantity=" + quantity +
                ", storageLocation=" + storageLocation +
                ", storageArea=" + storageArea +
                ", storagePosition=" + storagePosition +
                ", vendor=" + vendor +
                ", status=" + status +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
