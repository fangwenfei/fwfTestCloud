package com.cfmoto.bar.code.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * ktm订单表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@TableName("cf_ktm_purchase_order")
@Data
@ApiModel(value=" CfKtmPurchaseOrder",description="ktm订单表")
public class CfKtmPurchaseOrder extends Model<CfKtmPurchaseOrder> {

    private static final long serialVersionUID = 1L;

    public static final  String  CF_KTM_PURCHASE_SQL_ADD="ktm订单表数据插入有问题，请联系管理员";
    /**
     * ktm订单表主键
     */
    @TableId("ktm_purchase_id")
    @ApiModelProperty(value="ktm订单表主键(不必填)",example="1")
    private Integer ktmPurchaseId;
    /**
     * 采购订单编码 
     */
    @TableField("purchase_order_no")
    @ApiModelProperty(value="采购订单编码")
    private String purchaseOrderNo;
    /**
     * 供应商
     */
    @ApiModelProperty(value="供应商")
    private String supplier;
    /**
     * 物料编码
     */
    @TableField("materials_no")
    @ApiModelProperty(value="物料编码")
    private String materialsNo;
    /**
     * 物料名称
     */
    @TableField("materials_name")
    @ApiModelProperty(value="物料名称")
    private String materialsName;
    /**
     * 车架号
     */
    @TableField("frame_no")
    @ApiModelProperty(value="车架号")
    private String frameNo;
    /**
     * 发动机号
     */
    @TableField("engine_no")
    @ApiModelProperty(value="发动机号")
    private String engineNo;
    /**
     * 数据版本号
     */
    @TableField("object_version_number")
    private Integer objectVersionNumber;
    /**
     * 数据创建人
     */
    @TableField("created_by")
    private Integer createdBy;
    /**
     * 数据创建时间
     */
    @TableField("creation_date")
    private Date creationDate;
    /**
     * 最后更改人
     */
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后更改时间
     */
    @TableField("last_update_date")
    private Date lastUpdateDate;


    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }

    @Override
    protected Serializable pkVal() {
        return this.ktmPurchaseId;
    }

    @Override
    public String toString() {
        return "CfKtmPurchaseOrder{" +
        ", ktmPurchaseId=" + ktmPurchaseId +
        ", purchaseOrderNo=" + purchaseOrderNo +
        ", supplier=" + supplier +
        ", materialsNo=" + materialsNo +
        ", materialsName=" + materialsName +
        ", frameNo=" + frameNo +
        ", engineNo=" + engineNo +
        ", objectVersionNumber=" + objectVersionNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
