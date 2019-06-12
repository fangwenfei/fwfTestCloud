package com.cfmoto.bar.code.model.entity;

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
 * 订单临时表
 * </p>
 *
 * @author  space
 * @since 2019-03-18
 */
@TableName("cf_order_temp")
@ApiModel(value="CfOrderTemp",description="订单临时表")
public class CfOrderTemp extends Model<CfOrderTemp> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId("order_temp_id")
    private String orderTempId;
    /**
     * 订单
     */
    @ApiModelProperty(value="订单")
    @TableField("order_no")
    private String orderNo;
    /**
     * 订单描述
     */
    @ApiModelProperty(value="订单描述")
    @TableField("order_desc")
    private String orderDesc;
    /**
     * 订单状态
     */
    @ApiModelProperty(value="订单状态")
    private String status;
    /**
     * 订单类型
     */
    @ApiModelProperty(value="订单类型")
    @TableField("order_type")
    private String orderType;
    /**
     * 供应商
     */
    @ApiModelProperty(value="供应商")
    private String vendor;
    /**
     * 供应商描述
     */
    @ApiModelProperty(value="供应商描述")
    @TableField("vendor_desc")
    private String vendorDesc;
    /**
     * 制单日期
     */
    @ApiModelProperty(value="制单日期")
    @TableField("order_date")
    private Date orderDate;
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


    public String getOrderTempId() {
        return orderTempId;
    }

    public void setOrderTempId(String orderTempId) {
        this.orderTempId = orderTempId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendorDesc() {
        return vendorDesc;
    }

    public void setVendorDesc(String vendorDesc) {
        this.vendorDesc = vendorDesc;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
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
        return this.orderTempId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfOrderTemp{" +
                ", orderTempId=" + orderTempId +
                ", orderNo=" + orderNo +
                ", orderDesc=" + orderDesc +
                ", status=" + status +
                ", orderType=" + orderType +
                ", vendor=" + vendor +
                ", vendorDesc=" + vendorDesc +
                ", orderDate=" + orderDate +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
