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
import lombok.Data;

/**
 * <p>
 * 调拨信息表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-04-07
 */
@TableName("cf_allot_info")
@ApiModel(value = "CfAllotInfo", description = "调拨信息表")
@Data
public class CfAllotInfo extends Model<CfAllotInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨信息表主键ID
     */
    @ApiModelProperty(value="调拨信息表主键ID")
    @TableId(value = "allot_info_id", type = IdType.AUTO)
    private Integer allotInfoId;
    /**
     * 单号
     */
    @ApiModelProperty(value="单号")
    @TableField("order_no")
    private String orderNo;
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

    @Override
    protected Serializable pkVal() {
        return this.allotInfoId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfAllotInfo{" +
        ", allotInfoId=" + allotInfoId +
        ", orderNo=" + orderNo +
        ", madeOrderDate=" + madeOrderDate +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
