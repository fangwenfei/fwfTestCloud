package com.cfmoto.bar.code.model.entity;

import com.baomidou.mybatisplus.enums.IdType;

import java.math.BigDecimal;
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
 * 调拨清单表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-07
 */
@TableName("cf_allot_inventory")
@ApiModel(value = "CfAllotInventory", description = "调拨清单表")
@Data
public class CfAllotInventory extends Model<CfAllotInventory> {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨清单表主键ID
     */
    @ApiModelProperty(value = "调拨清单表主键ID")
    @TableId(value = "allot_inventory_id", type = IdType.AUTO)
    private Integer allotInventoryId;
    /**
     * 单号
     */
    @ApiModelProperty(value = "单号")
    @TableField("order_no")
    private String orderNo;
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
     * 调出仓库
     */
    @ApiModelProperty(value = "调出仓库")
    @TableField("allot_out_warehouse")
    private String allotOutWarehouse;
    /**
     * 调入仓库
     */
    @ApiModelProperty(value = "调入仓库")
    @TableField("allot_in_warehouse")
    private String allotInWarehouse;
    /**
     * SP储位号
     */
    @ApiModelProperty(value = "SP储位号")
    @TableField("sp_store_position_no")
    private String spStorePositionNo;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;
    /**
     * 调出已扫描数量
     */
    @ApiModelProperty(value = "调出已扫描数量")
    @TableField("allot_out_scanned_number")
    private Integer allotOutScannedNumber;
    /**
     * 调入已扫描数量
     */
    @ApiModelProperty(value = "调入已扫描数量")
    @TableField("allot_in_scanned_number")
    private Integer allotInScannedNumber;
    /**
     * 部门
     */
    @ApiModelProperty(value = "部门")
    private String dept;
    /**
     * 制单日期
     */
    @ApiModelProperty(value = "制单日期")
    @TableField("made_order_date")
    private Date madeOrderDate;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    @TableField("customer_name")
    private String customerName;
    /**
     * 收件地址
     */
    @ApiModelProperty(value = "收件地址")
    @TableField("receive_address")
    private String receiveAddress;
    /**
     * 收件联系人
     */
    @ApiModelProperty(value = "收件联系人")
    @TableField("receive_contact_name")
    private String receiveContactName;
    /**
     * 收件联系方式
     */
    @ApiModelProperty(value = "收件联系方式")
    @TableField("receive_contact_phone_number")
    private String receiveContactPhoneNumber;
    /**
     * 销售订单号
     */
    @ApiModelProperty(value = "销售订单号")
    @TableField("sale_order_no")
    private String saleOrderNo;
    /**
     * 发件人
     */
    @ApiModelProperty(value = "发件人")
    private String addresser;
    /**
     * 发件人联系方式
     */
    @ApiModelProperty(value = "发件人联系方式")
    @TableField("addresser_phone_number")
    private String addresserPhoneNumber;
    /**
     * 国内/国外
     */
    @ApiModelProperty(value = "国内/国外")
    @TableField("inland_or_abroad")
    private String inlandOrAbroad;
    /**
     * 最小包装量
     */
    @ApiModelProperty(value = "最小包装量")
    @TableField("minimum_package_number")
    private Integer minimumPackageNumber;
    /**
     * 销售价格
     */
    @ApiModelProperty(value = "销售价格")
    @TableField("sale_price")
    private BigDecimal salePrice;
    /**
     * 商流订单
     */
    @ApiModelProperty(value = "商流订单")
    @TableField("business_stream_order_no")
    private String businessStreamOrderNo;
    /**
     * 英文名称
     */
    @ApiModelProperty(value = "英文名称")
    @TableField("english_name")
    private String englishName;
    /**
     * 调拨信息表主键ID
     */
    @ApiModelProperty(value = "调拨信息表主键ID")
    @TableField("allot_info_id")
    private Integer allotInfoId;
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

    @Override
    protected Serializable pkVal() {
        return this.allotInventoryId;
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
        return "CfAllotInventory{" +
                ", allotInventoryId=" + allotInventoryId +
                ", orderNo=" + orderNo +
                ", materialsName=" + materialsName +
                ", materialsNo=" + materialsNo +
                ", spec=" + spec +
                ", allotOutWarehouse=" + allotOutWarehouse +
                ", allotInWarehouse=" + allotInWarehouse +
                ", spStorePositionNo=" + spStorePositionNo +
                ", number=" + number +
                ", allotOutScannedNumber=" + allotOutScannedNumber +
                ", allotInScannedNumber=" + allotInScannedNumber +
                ", dept=" + dept +
                ", madeOrderDate=" + madeOrderDate +
                ", customerName=" + customerName +
                ", receiveAddress=" + receiveAddress +
                ", receiveContactName=" + receiveContactName +
                ", receiveContactPhoneNumber=" + receiveContactPhoneNumber +
                ", saleOrderNo=" + saleOrderNo +
                ", addresser=" + addresser +
                ", addresserPhoneNumber=" + addresserPhoneNumber +
                ", inlandOrAbroad=" + inlandOrAbroad +
                ", minimumPackageNumber=" + minimumPackageNumber +
                ", salePrice=" + salePrice +
                ", businessStreamOrderNo=" + businessStreamOrderNo +
                ", englishName=" + englishName +
                ", allotInfoId=" + allotInfoId +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
