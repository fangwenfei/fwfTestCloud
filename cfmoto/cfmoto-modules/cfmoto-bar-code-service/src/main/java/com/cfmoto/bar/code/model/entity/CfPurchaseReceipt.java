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
 * 采购收货表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@TableName("cf_purchase_receipt")
@Data
@ApiModel(value=" CfPurchaseReceipt",description="采购收货表")
public class CfPurchaseReceipt extends Model<CfPurchaseReceipt> {

    private static final long serialVersionUID = 1L;

    public static final  String  CF_PURCHASE_RECEIPT_SQL_ADD="采购收货表数据插入有问题，请联系管理员";

    @TableId("spare_parts_receiving_id")
    @ApiModelProperty(value="采购收货表主键(不必填)",example="1")
    private Integer sparePartsReceivingId;
    /**
     * 采购订单编码 
     */
    @TableField("purchase_order_no")
    @ApiModelProperty(value="采购订单编码")
    private String purchaseOrderNo;
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
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    private String depository;
    /**
     * 订单数量
     */
    @TableField("order_number")
    @ApiModelProperty(value="订单数量")
    private Integer orderNumber;
    /**
     * 已收数量
     */
    @TableField("received_number")
    @ApiModelProperty(value="已收数量")
    private Integer receivedNumber;
    /**
     * 供应商
     */
    @ApiModelProperty(value="供应商")
    private String supplier;
    /**
     * 采购收货时间
     */
    @TableField("purchasing_receiving_date")
    @ApiModelProperty(value="采购收货时间")
    private Date purchasingReceivingDate;
    /**
     * 状态
     */
    @ApiModelProperty(value="状态")
    private String status;
    /**
     * 待检区
     */
    @TableField("pending_area")
    @ApiModelProperty(value="待检区")
    private String pendingArea;

    @TableField("received_total_number")
    @ApiModelProperty(value="已收总量")
    private Integer receivedTotalNumber;
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
        return this.sparePartsReceivingId;
    }

    @Override
    public String toString() {
        return "CfPurchaseReceipt{" +
        ", sparePartsReceivingId=" + sparePartsReceivingId +
        ", purchaseOrderNo=" + purchaseOrderNo +
        ", materialsNo=" + materialsNo +
        ", materialsName=" + materialsName +
        ", depository=" + depository +
        ", orderNumber=" + orderNumber +
        ", receivedNumber=" + receivedNumber +
        ", supplier=" + supplier +
        ", purchasingReceivingDate=" + purchasingReceivingDate +
        ", status=" + status +
        ", pendingArea=" + pendingArea +
        ", receivedTotalNumber=" + receivedTotalNumber +
        ", objectVersionNumber=" + objectVersionNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
