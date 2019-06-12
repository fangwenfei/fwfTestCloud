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
 * 
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-20
 */
@TableName("cf_cost_center_pick_or_withdraw_inventory")
@ApiModel(value="CfCostCenterPickOrWithdrawInventory",description="")
public class CfCostCenterPickOrWithdrawInventory extends Model<CfCostCenterPickOrWithdrawInventory> {

    private static final long serialVersionUID = 1L;

    /**
     * 成本中心领退料清单表主键ID
     */
    @ApiModelProperty(value="成本中心领退料清单表主键ID")
    @TableId(value = "cost_center_pick_or_withdraw_inventory_id", type = IdType.AUTO)
    private Integer costCenterPickOrWithdrawInventoryId;
    /**
     * 领料单号或退料单号
     */
    @ApiModelProperty(value="领料单号或退料单号")
    @TableField("order_no")
    private String orderNo;
    /**
     * 单据类型
     */
    @ApiModelProperty(value="单据类型")
    @TableField("order_type")
    private String orderType;
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
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String spec;
    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    private String warehouse;
    /**
     * 存储区域
     */
    @ApiModelProperty(value="存储区域")
    @TableField("storage_area")
    private String storageArea;
    /**
     * 退货仓库
     */
    @ApiModelProperty(value="退货仓库")
    @TableField("withdraw_warehouse")
    private String withdrawWarehouse;
    /**
     * 应领/退数
     */
    @ApiModelProperty(value="应领/退数")
    @TableField("should_pick_or_withdraw_number")
    private Integer shouldPickOrWithdrawNumber;
    /**
     * 已扫描数量
     */
    @ApiModelProperty(value="已扫描数量")
    @TableField("scanned_number")
    private Integer scannedNumber;
    /**
     * sp储位号
     */
    @ApiModelProperty(value="sp储位号")
    @TableField("sp_store_position_no")
    private String spStorePositionNo;
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
     * 数据最后修改人
     */
    @ApiModelProperty(value="数据最后修改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 数据最后修改时间
     */
    @ApiModelProperty(value="数据最后修改时间")
    @TableField("last_update_date")
    private Date lastUpdateDate;
    /**
     * 成本中心领退料信息表主键ID
     */
    @ApiModelProperty(value="成本中心领退料信息表主键ID")
    @TableField("cost_center_pick_or_withdraw_info_id")
    private Integer costCenterPickOrWithdrawInfoId;


    public Integer getCostCenterPickOrWithdrawInventoryId() {
        return costCenterPickOrWithdrawInventoryId;
    }

    public void setCostCenterPickOrWithdrawInventoryId(Integer costCenterPickOrWithdrawInventoryId) {
        this.costCenterPickOrWithdrawInventoryId = costCenterPickOrWithdrawInventoryId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getStorageArea() {
        return storageArea;
    }

    public void setStorageArea(String storageArea) {
        this.storageArea = storageArea;
    }

    public String getWithdrawWarehouse() {
        return withdrawWarehouse;
    }

    public void setWithdrawWarehouse(String withdrawWarehouse) {
        this.withdrawWarehouse = withdrawWarehouse;
    }

    public Integer getShouldPickOrWithdrawNumber() {
        return shouldPickOrWithdrawNumber;
    }

    public void setShouldPickOrWithdrawNumber(Integer shouldPickOrWithdrawNumber) {
        this.shouldPickOrWithdrawNumber = shouldPickOrWithdrawNumber;
    }

    public Integer getScannedNumber() {
        return scannedNumber;
    }

    public void setScannedNumber(Integer scannedNumber) {
        this.scannedNumber = scannedNumber;
    }

    public String getSpStorePositionNo() {
        return spStorePositionNo;
    }

    public void setSpStorePositionNo(String spStorePositionNo) {
        this.spStorePositionNo = spStorePositionNo;
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

    public Integer getCostCenterPickOrWithdrawInfoId() {
        return costCenterPickOrWithdrawInfoId;
    }

    public void setCostCenterPickOrWithdrawInfoId(Integer costCenterPickOrWithdrawInfoId) {
        this.costCenterPickOrWithdrawInfoId = costCenterPickOrWithdrawInfoId;
    }

    @Override
    protected Serializable pkVal() {
        return this.costCenterPickOrWithdrawInventoryId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfCostCenterPickOrWithdrawInventory{" +
        ", costCenterPickOrWithdrawInventoryId=" + costCenterPickOrWithdrawInventoryId +
        ", orderNo=" + orderNo +
        ", orderType=" + orderType +
        ", materialsName=" + materialsName +
        ", materialsNo=" + materialsNo +
        ", spec=" + spec +
        ", warehouse=" + warehouse +
        ", storageArea=" + storageArea +
        ", withdrawWarehouse=" + withdrawWarehouse +
        ", shouldPickOrWithdrawNumber=" + shouldPickOrWithdrawNumber +
        ", scannedNumber=" + scannedNumber +
        ", spStorePositionNo=" + spStorePositionNo +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        ", costCenterPickOrWithdrawInfoId=" + costCenterPickOrWithdrawInfoId +
        "}";
    }

    public void setObjectSetBasicAttributeForUpdate(int userId, Date date) {
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
    }
}
