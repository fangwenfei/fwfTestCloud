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
 * 订单汇总临时表
 * </p>
 *
 * @author  space
 * @since 2019-03-14
 */
@TableName("cf_order_sum_temp")
@ApiModel(value="CfOrderSumTemp",description="订单汇总临时表")
public class CfOrderSumTemp extends Model<CfOrderSumTemp> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId("order_sum_temp_id")
    private String orderSumTempId;
    /**
     * 订单临时表-主键
     */
    @ApiModelProperty(value="订单临时表-主键")
    @TableField("order_temp_id_ref")
    private String orderTempIdRef;
    /**
     * 行项目
     */
    @ApiModelProperty(value="行项目")
    @TableField("row_item")
    private String rowItem;
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
     * 数量
     */
    @ApiModelProperty(value="数量")
    private BigDecimal quantity;
    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String mode;
    /**
     * 未清数量
     */
    @ApiModelProperty(value="未清数量")
    @TableField("demand_qty")
    private BigDecimal demandQty;
    /**
     * 默认仓库
     */
    @ApiModelProperty(value="默认仓库")
    @TableField("storage_location")
    private String storageLocation;
    /**
     * 存储区域
     */
    @ApiModelProperty(value="存储区域")
    @TableField("storage_area")
    private String storageArea;
    /**
     * 应出数量
     */
    @ApiModelProperty(value="应出数量")
    @TableField("payable_qty")
    private BigDecimal payableQty;
    /**
     * 实出数量
     */
    @ApiModelProperty(value="实出数量")
    @TableField("output_qty")
    private BigDecimal outputQty;
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
    /**
     * 最后更改时间
     */
    @ApiModelProperty(value="最后更改时间")
    @TableField("last_update_date")
    private Date lastUpdateDate;


    public String getOrderSumTempId() {
        return orderSumTempId;
    }

    public void setOrderSumTempId(String orderSumTempId) {
        this.orderSumTempId = orderSumTempId;
    }

    public String getOrderTempIdRef() {
        return orderTempIdRef;
    }

    public void setOrderTempIdRef(String orderTempIdRef) {
        this.orderTempIdRef = orderTempIdRef;
    }

    public String getRowItem() {
        return rowItem;
    }

    public void setRowItem(String rowItem) {
        this.rowItem = rowItem;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public BigDecimal getDemandQty() {
        return demandQty;
    }

    public void setDemandQty(BigDecimal demandQty) {
        this.demandQty = demandQty;
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

    public BigDecimal getPayableQty() {
        return payableQty;
    }

    public void setPayableQty(BigDecimal payableQty) {
        this.payableQty = payableQty;
    }

    public BigDecimal getOutputQty() {
        return outputQty;
    }

    public void setOutputQty(BigDecimal outputQty) {
        this.outputQty = outputQty;
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
        return this.orderSumTempId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfOrderSumTemp{" +
                ", orderSumTempId=" + orderSumTempId +
                ", orderTempIdRef=" + orderTempIdRef +
                ", rowItem=" + rowItem +
                ", item=" + item +
                ", itemDesc=" + itemDesc +
                ", itemPurpose=" + itemPurpose +
                ", quantity=" + quantity +
                ", mode=" + mode +
                ", demandQty=" + demandQty +
                ", storageLocation=" + storageLocation +
                ", storageArea=" + storageArea +
                ", payableQty=" + payableQty +
                ", outputQty=" + outputQty +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
