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
 * 装箱清单数据表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-04-09
 */
@TableName("cf_packing_list")
@ApiModel(value="CfPackingList",description="装箱清单数据表")
public class CfPackingList extends Model<CfPackingList> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId("packing_list_id")
    private String packingListId;
    /**
     * 调拨单
     */
    @ApiModelProperty(value="调拨单")
    @TableField("order_no")
    private String orderNo;
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sale_order")
    private String saleOrder;
    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    private String item;
    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("item_desc")
    private String itemDesc;
    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String mode;
    /**
     * 箱号
     */
    @ApiModelProperty(value="箱号")
    @TableField("case_no")
    private String caseNo;
    /**
     * 数量
     */
    @ApiModelProperty(value="数量")
    private BigDecimal qty;
    /**
     * 商流订单
     */
    @ApiModelProperty(value="商流订单")
    @TableField("business_order")
    private String businessOrder;
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


    public String getPackingListId() {
        return packingListId;
    }

    public void setPackingListId(String packingListId) {
        this.packingListId = packingListId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSaleOrder() {
        return saleOrder;
    }

    public void setSaleOrder(String saleOrder) {
        this.saleOrder = saleOrder;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public String getBusinessOrder() {
        return businessOrder;
    }

    public void setBusinessOrder(String businessOrder) {
        this.businessOrder = businessOrder;
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
        return this.packingListId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfPackingList{" +
        ", packingListId=" + packingListId +
        ", orderNo=" + orderNo +
        ", saleOrder=" + saleOrder +
        ", item=" + item +
        ", itemDesc=" + itemDesc +
        ", mode=" + mode +
        ", caseNo=" + caseNo +
        ", qty=" + qty +
        ", businessOrder=" + businessOrder +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
