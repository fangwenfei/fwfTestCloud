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
 * 零部件采购订单表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@TableName("cf_parts_purchase_order")
@Data
@ApiModel(value=" CfPartsPurchaseOrder",description="零部件采购订单表")
public class CfPartsPurchaseOrder extends Model<CfPartsPurchaseOrder> {

    private static final long serialVersionUID = 1L;
    public static final  String  CF_PARTS_PURCHASE_ORDER_SQL_ADD="零部件采购订单表数据插入有问题，请联系管理员";
    /**
     * 采购订单编码主键
     */
    @TableId("purchase_order_id")
    @ApiModelProperty(value="采购订单编码主键(不必填)",example="1")
    private Integer purchaseOrderId;
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
     *  物料名称
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
     * 单位
     */
    @ApiModelProperty(value="单位")
    private String company;
    /**
     * 供应商
     */
    @ApiModelProperty(value="供应商")
    private String supplier;
    /**
     * 公司代码
     */

    @TableField("company_code")
    @ApiModelProperty(value="公司代码")
    private String companyCode;
    /**
     * 采购组织
     */
    @TableField("purchasing_organization")
    @ApiModelProperty(value="采购组织")
    private String purchasingOrganization;
    /**
     * 采购组
     */
    @TableField("purchasing_group")
    @ApiModelProperty(value="采购组")
    private String purchasingGroup;
    /**
     * 项目类别
     */
    @TableField("item_category")
    @ApiModelProperty(value="项目类别")
    private String itemCategory;
    /**
     * 计划交货时间
     */
    @TableField("scheduled_time")
    @ApiModelProperty(value="计划交货时间")
    private Date scheduledTime;
    /**
     * 凭证日期
     */
    @TableField("document_date")
    @ApiModelProperty(value="凭证日期")
    private Date documentDate;
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
        return this.purchaseOrderId;
    }

    @Override
    public String toString() {
        return "CfPartsPurchaseOrder{" +
        ", purchaseOrderId=" + purchaseOrderId +
        ", purchaseOrderNo=" + purchaseOrderNo +
        ", materialsNo=" + materialsNo +
        ", materialsName=" + materialsName +
        ", depository=" + depository +
        ", orderNumber=" + orderNumber +
        ", receivedNumber=" + receivedNumber +
        ", company=" + company +
        ", supplier=" + supplier +
        ", companyCode=" + companyCode +
        ", purchasingOrganization=" + purchasingOrganization +
        ", purchasingGroup=" + purchasingGroup +
        ", itemCategory=" + itemCategory +
        ", scheduledTime=" + scheduledTime +
        ", documentDate=" + documentDate +
        ", objectVersionNumber=" + objectVersionNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
