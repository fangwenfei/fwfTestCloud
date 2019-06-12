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
 * 部品网购发货信息表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-06-04
 */
@TableName("cf_cec_deliver_goods_info")
@ApiModel(value="CfCecDeliverGoodsInfo",description="部品网购发货信息表")
public class CfCecDeliverGoodsInfo extends Model<CfCecDeliverGoodsInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value="主键ID")
    @TableId(value = "info_id", type = IdType.AUTO)
    private Integer infoId;
    /**
     * 交货单号
     */
    @ApiModelProperty(value="交货单号")
    @TableField("deliver_order_no")
    private String deliverOrderNo;
    /**
     * 购货单位
     */
    @ApiModelProperty(value="购货单位")
    @TableField("purchase_unit")
    private String purchaseUnit;
    /**
     * 部门
     */
    @ApiModelProperty(value="部门")
    private String department;
    /**
     * 制单日期
     */
    @ApiModelProperty(value="制单日期")
    @TableField("made_order_date")
    private Date madeOrderDate;
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


    public Integer getInfoId() {
        return infoId;
    }

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public String getDeliverOrderNo() {
        return deliverOrderNo;
    }

    public void setDeliverOrderNo(String deliverOrderNo) {
        this.deliverOrderNo = deliverOrderNo;
    }

    public String getPurchaseUnit() {
        return purchaseUnit;
    }

    public void setPurchaseUnit(String purchaseUnit) {
        this.purchaseUnit = purchaseUnit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getMadeOrderDate() {
        return madeOrderDate;
    }

    public void setMadeOrderDate(Date madeOrderDate) {
        this.madeOrderDate = madeOrderDate;
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
        return this.infoId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfCecDeliverGoodsInfo{" +
        ", infoId=" + infoId +
        ", deliverOrderNo=" + deliverOrderNo +
        ", purchaseUnit=" + purchaseUnit +
        ", department=" + department +
        ", madeOrderDate=" + madeOrderDate +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
