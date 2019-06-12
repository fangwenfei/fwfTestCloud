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
 * 部品网购发货清单表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-04
 */
@TableName("cf_cec_deliver_goods_inventory")
@ApiModel(value = "CfCecDeliverGoodsInventory", description = "部品网购发货清单表")
public class CfCecDeliverGoodsInventory extends Model<CfCecDeliverGoodsInventory> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "inventory_id", type = IdType.AUTO)
    private Integer inventoryId;
    /**
     * 交货单(发货通知单)
     */
    @ApiModelProperty(value = "交货单(发货通知单)")
    @TableField("deliver_order_no")
    private String deliverOrderNo;
    /**
     * 销售订单
     */
    @ApiModelProperty(value = "销售订单")
    @TableField("sales_order_no")
    private String salesOrderNo;
    /**
     * 行项目
     */
    @ApiModelProperty(value = "行项目")
    @TableField("row_item")
    private String rowItem;
    /**
     * 物料代码
     */
    @ApiModelProperty(value = "物料代码")
    @TableField("materials_no")
    private String materialsNo;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    @TableField("materials_name")
    private String materialsName;
    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    private String spec;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;
    /**
     * 已扫描数量
     */
    @ApiModelProperty(value = "已扫描数量")
    @TableField("scanned_number")
    private Integer scannedNumber;
    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    private String warehouse;
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
     * 数据最后修改人
     */
    @ApiModelProperty(value = "数据最后修改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 数据最后修改时间
     */
    @ApiModelProperty(value = "数据最后修改时间")
    @TableField("last_update_date")
    private Date lastUpdateDate;


    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getDeliverOrderNo() {
        return deliverOrderNo;
    }

    public void setDeliverOrderNo(String deliverOrderNo) {
        this.deliverOrderNo = deliverOrderNo;
    }

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }

    public String getRowItem() {
        return rowItem;
    }

    public void setRowItem(String rowItem) {
        this.rowItem = rowItem;
    }

    public String getMaterialsNo() {
        return materialsNo;
    }

    public void setMaterialsNo(String materialsNo) {
        this.materialsNo = materialsNo;
    }

    public String getMaterialsName() {
        return materialsName;
    }

    public void setMaterialsName(String materialsName) {
        this.materialsName = materialsName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getScannedNumber() {
        return scannedNumber;
    }

    public void setScannedNumber(Integer scannedNumber) {
        this.scannedNumber = scannedNumber;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
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
        return this.inventoryId;
    }

    public void setObjectSetBasicAttribute(int userId, Date date) {
        this.createdBy = userId;
        this.lastUpdatedBy = userId;
        this.lastUpdateDate = date;
        this.creationDate = date;
    }

    public void setObjectSetBasicAttributeForUpdate(int userId, Date date) {
        this.lastUpdatedBy = userId;
        this.lastUpdateDate = date;
    }

    @Override
    public String toString() {
        return "CfCecDeliverGoodsInventory{" +
                ", inventoryId=" + inventoryId +
                ", deliverOrderNo=" + deliverOrderNo +
                ", salesOrderNo=" + salesOrderNo +
                ", rowItem=" + rowItem +
                ", materialsNo=" + materialsNo +
                ", materialsName=" + materialsName +
                ", spec=" + spec +
                ", number=" + number +
                ", scannedNumber=" + scannedNumber +
                ", warehouse=" + warehouse +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
