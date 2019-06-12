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
@TableName("cf_cost_center_pick_or_withdraw_scan_record")
@ApiModel(value="CfCostCenterPickOrWithdrawScanRecord",description="")
public class CfCostCenterPickOrWithdrawScanRecord extends Model<CfCostCenterPickOrWithdrawScanRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 成本中心领退料扫描记录表主键ID
     */
    @ApiModelProperty(value="成本中心领退料扫描记录表主键ID")
    @TableId(value = "cost_center_pick_or_withdraw_scan_record_id", type = IdType.AUTO)
    private Integer costCenterPickOrWithdrawScanRecordId;
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
     * 条码
     */
    @ApiModelProperty(value="条码")
    private String barcode;
    /**
     * 条码类型(KTM：KTM,CP：CP,EG:发动机 ，OT：WP车架条码（库存条码）配件条码
     */
    @ApiModelProperty(value="条码类型(KTM：KTM,CP：CP,EG:发动机 ，OT：WP车架条码（库存条码）配件条码")
    @TableField("barcode_type")
    private String barcodeType;
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
     * 仓位
     */
    @ApiModelProperty(value="仓位")
    @TableField("warehouse_position")
    private String warehousePosition;
    /**
     * 状态（不可使用N）
     */
    @ApiModelProperty(value="状态（不可使用N）")
    private String state;
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
    /**
     * 成本中心领退料清单表主键ID
     */
    @ApiModelProperty(value="成本中心领退料清单表主键ID")
    @TableField("cost_center_pick_or_withdraw_inventory_id")
    private Integer costCenterPickOrWithdrawInventoryId;


    public Integer getCostCenterPickOrWithdrawScanRecordId() {
        return costCenterPickOrWithdrawScanRecordId;
    }

    public void setCostCenterPickOrWithdrawScanRecordId(Integer costCenterPickOrWithdrawScanRecordId) {
        this.costCenterPickOrWithdrawScanRecordId = costCenterPickOrWithdrawScanRecordId;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(String barcodeType) {
        this.barcodeType = barcodeType;
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

    public String getWarehousePosition() {
        return warehousePosition;
    }

    public void setWarehousePosition(String warehousePosition) {
        this.warehousePosition = warehousePosition;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Integer getCostCenterPickOrWithdrawInventoryId() {
        return costCenterPickOrWithdrawInventoryId;
    }

    public void setCostCenterPickOrWithdrawInventoryId(Integer costCenterPickOrWithdrawInventoryId) {
        this.costCenterPickOrWithdrawInventoryId = costCenterPickOrWithdrawInventoryId;
    }

    @Override
    protected Serializable pkVal() {
        return this.costCenterPickOrWithdrawScanRecordId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfCostCenterPickOrWithdrawScanRecord{" +
        ", costCenterPickOrWithdrawScanRecordId=" + costCenterPickOrWithdrawScanRecordId +
        ", orderNo=" + orderNo +
        ", orderType=" + orderType +
        ", materialsName=" + materialsName +
        ", materialsNo=" + materialsNo +
        ", spec=" + spec +
        ", barcode=" + barcode +
        ", barcodeType=" + barcodeType +
        ", batchNo=" + batchNo +
        ", number=" + number +
        ", warehouse=" + warehouse +
        ", storageArea=" + storageArea +
        ", warehousePosition=" + warehousePosition +
        ", state=" + state +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        ", costCenterPickOrWithdrawInfoId=" + costCenterPickOrWithdrawInfoId +
        ", costCenterPickOrWithdrawInventoryId=" + costCenterPickOrWithdrawInventoryId +
        "}";
    }
}
