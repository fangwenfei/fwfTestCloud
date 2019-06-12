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
 * 调拨扫描记录表
 * </p>
 *
 * @author space
 * @since 2019-04-15
 */
@TableName("cf_allot_scan_record")
@ApiModel(value = "CfAllotScanRecord", description = "调拨扫描记录表")
public class CfAllotScanRecord extends Model<CfAllotScanRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨扫描记录表主键ID
     */
    @ApiModelProperty(value = "调拨扫描记录表主键ID")
    @TableId(value = "allot_scan_record_id", type = IdType.AUTO)
    private Integer allotScanRecordId;
    /**
     * 单号
     */
    @ApiModelProperty(value = "单号")
    @TableField("order_no")
    private String orderNo;
    /**
     * 销售订单号
     */
    @ApiModelProperty(value = "销售订单号")
    @TableField("sale_order_no")
    private String saleOrderNo;
    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型")
    @TableField("operate_type")
    private String operateType;
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
     * 包号
     */
    @ApiModelProperty(value = "包号")
    @TableField("pack_no")
    private String packNo;
    /**
     * 条码类型
     */
    @ApiModelProperty(value = "条码类型")
    @TableField("barcode_type")
    private String barcodeType;
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
     * 仓库（调出仓库）
     */
    @ApiModelProperty(value = "仓库（调出仓库）")
    private String warehouse;
    /**
     * 存储区域
     */
    @ApiModelProperty(value = "存储区域")
    @TableField("storage_area")
    private String storageArea;
    /**
     * 仓位
     */
    @ApiModelProperty(value = "仓位")
    @TableField("warehouse_position")
    private String warehousePosition;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String state;
    /**
     * 调入仓库
     */
    @ApiModelProperty(value = "调入仓库")
    @TableField("allot_in_warehouse")
    private String allotInWarehouse;
    /**
     * 箱号
     */
    @ApiModelProperty(value = "箱号")
    @TableField("case_no")
    private String caseNo;
    /**
     * 长
     */
    @ApiModelProperty(value = "长")
    private Double length;
    /**
     * 宽
     */
    @ApiModelProperty(value = "宽")
    private Double width;
    /**
     * 高
     */
    @ApiModelProperty(value = "高")
    private Double height;
    /**
     * 毛重
     */
    @ApiModelProperty(value = "毛重")
    @TableField("rough_weight")
    private Double roughWeight;
    /**
     * 发运单号
     */
    @ApiModelProperty(value = "发运单号")
    @TableField("send_waybill_no")
    private String sendWaybillNo;
    /**
     * 快递公司
     */
    @ApiModelProperty(value = "快递公司")
    @TableField("express_company")
    private String expressCompany;
    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private String supplier;
    /**
     * 调拨信息表主键ID
     */
    @ApiModelProperty(value = "调拨信息表主键ID")
    @TableField("allot_info_id")
    private Integer allotInfoId;
    /**
     * 调拨清单表主键ID
     */
    @ApiModelProperty(value = "调拨清单表主键ID")
    @TableField("allot_inventory_id")
    private Integer allotInventoryId;
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


    public Integer getAllotScanRecordId() {
        return allotScanRecordId;
    }

    public void setAllotScanRecordId(Integer allotScanRecordId) {
        this.allotScanRecordId = allotScanRecordId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSaleOrderNo() {
        return saleOrderNo;
    }

    public void setSaleOrderNo(String saleOrderNo) {
        this.saleOrderNo = saleOrderNo;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
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

    public String getPackNo() {
        return packNo;
    }

    public void setPackNo(String packNo) {
        this.packNo = packNo;
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

    public String getAllotInWarehouse() {
        return allotInWarehouse;
    }

    public void setAllotInWarehouse(String allotInWarehouse) {
        this.allotInWarehouse = allotInWarehouse;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getRoughWeight() {
        return roughWeight;
    }

    public void setRoughWeight(Double roughWeight) {
        this.roughWeight = roughWeight;
    }

    public String getSendWaybillNo() {
        return sendWaybillNo;
    }

    public void setSendWaybillNo(String sendWaybillNo) {
        this.sendWaybillNo = sendWaybillNo;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Integer getAllotInfoId() {
        return allotInfoId;
    }

    public void setAllotInfoId(Integer allotInfoId) {
        this.allotInfoId = allotInfoId;
    }

    public Integer getAllotInventoryId() {
        return allotInventoryId;
    }

    public void setAllotInventoryId(Integer allotInventoryId) {
        this.allotInventoryId = allotInventoryId;
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
        return this.allotScanRecordId;
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
        return "CfAllotScanRecord{" +
                ", allotScanRecordId=" + allotScanRecordId +
                ", orderNo=" + orderNo +
                ", saleOrderNo=" + saleOrderNo +
                ", operateType=" + operateType +
                ", materialsName=" + materialsName +
                ", materialsNo=" + materialsNo +
                ", spec=" + spec +
                ", barcode=" + barcode +
                ", packNo=" + packNo +
                ", barcodeType=" + barcodeType +
                ", batchNo=" + batchNo +
                ", number=" + number +
                ", warehouse=" + warehouse +
                ", storageArea=" + storageArea +
                ", warehousePosition=" + warehousePosition +
                ", state=" + state +
                ", allotInWarehouse=" + allotInWarehouse +
                ", caseNo=" + caseNo +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", roughWeight=" + roughWeight +
                ", sendWaybillNo=" + sendWaybillNo +
                ", expressCompany=" + expressCompany +
                ", supplier=" + supplier +
                ", allotInfoId=" + allotInfoId +
                ", allotInventoryId=" + allotInventoryId +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
