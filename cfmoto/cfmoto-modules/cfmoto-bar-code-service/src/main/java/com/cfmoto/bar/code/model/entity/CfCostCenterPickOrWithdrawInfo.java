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
@TableName("cf_cost_center_pick_or_withdraw_info")
@ApiModel(value="CfCostCenterPickOrWithdrawInfo",description="")
public class CfCostCenterPickOrWithdrawInfo extends Model<CfCostCenterPickOrWithdrawInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 成本中心领退料信息表主键id
     */
    @ApiModelProperty(value="成本中心领退料信息表主键id")
    @TableId(value = "cost_center_pick_or_withdraw_info_id", type = IdType.AUTO)
    private Integer costCenterPickOrWithdrawInfoId;
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
     * 制单日期
     */
    @ApiModelProperty(value="制单日期")
    @TableField("made_order_date")
    private Date madeOrderDate;
    /**
     * 部门
     */
    @ApiModelProperty(value="部门")
    private String dept;
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


    public Integer getCostCenterPickOrWithdrawInfoId() {
        return costCenterPickOrWithdrawInfoId;
    }

    public void setCostCenterPickOrWithdrawInfoId(Integer costCenterPickOrWithdrawInfoId) {
        this.costCenterPickOrWithdrawInfoId = costCenterPickOrWithdrawInfoId;
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

    public Date getMadeOrderDate() {
        return madeOrderDate;
    }

    public void setMadeOrderDate(Date madeOrderDate) {
        this.madeOrderDate = madeOrderDate;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
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
        return this.costCenterPickOrWithdrawInfoId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfCostCenterPickOrWithdrawInfo{" +
        ", costCenterPickOrWithdrawInfoId=" + costCenterPickOrWithdrawInfoId +
        ", orderNo=" + orderNo +
        ", orderType=" + orderType +
        ", madeOrderDate=" + madeOrderDate +
        ", dept=" + dept +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
