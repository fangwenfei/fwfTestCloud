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
 * 部品发货表
 * </p>
 *
 * @author  space
 * @since 2019-04-09
 */
@TableName("cf_deliver_goods")
@ApiModel(value="CfDeliverGoods",description="部品发货表")
public class CfDeliverGoods extends Model<CfDeliverGoods> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId("deliver_goods_id")
    private String deliverGoodsId;
    /**
     * 发货单
     */
    @ApiModelProperty(value="发货单")
    @TableField("order_no")
    private String orderNo;
    /**
     * 订单状态U未完成、C完成
     */
    @ApiModelProperty(value="订单状态U未完成、C完成")
    private String status;
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sale_order")
    private String saleOrder;
    /**
     * 客户
     */
    @ApiModelProperty(value="客户")
    private String customer;
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


    public String getDeliverGoodsId() {
        return deliverGoodsId;
    }

    public void setDeliverGoodsId(String deliverGoodsId) {
        this.deliverGoodsId = deliverGoodsId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSaleOrder() {
        return saleOrder;
    }

    public void setSaleOrder(String saleOrder) {
        this.saleOrder = saleOrder;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
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
        return this.deliverGoodsId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfDeliverGoods{" +
                ", deliverGoodsId=" + deliverGoodsId +
                ", orderNo=" + orderNo +
                ", status=" + status +
                ", saleOrder=" + saleOrder +
                ", customer=" + customer +
                ", orderDate=" + orderDate +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
