package com.cfmoto.bar.code.model.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * KTM采购收货表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@TableName("cf_ktm_receiving_order")
@Data
@ApiModel(value = " CfKtmReceivingOrder", description = "KTM采购收货对象")
public class CfKtmReceivingOrder extends Model<CfKtmReceivingOrder> {

    private static final long serialVersionUID = 1L;

    public static final String CF_KTM_RECEIVING_ORDER_SQL_ADD = "KTM采购收货表数据插入有问题，请联系管理员";

    /**
     * ktm采购收货订单表主键
     */
    @TableId(value = "ktm_receiving_id", type = IdType.AUTO)
    @ApiModelProperty(value = "ktm采购收货订单表主键(不必填)", name = "ktmReceivingId", example = "1")
    private Integer ktmReceivingId;
    /**
     * 采购订单编码
     */
    @TableField("purchase_order_no")
    @ApiModelProperty(value = "采购订单编码", name = "purchaseOrderNo")
    @Excel(name = "采购订单编码")
    private String purchaseOrderNo;
    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商", name = "supplier")
    @Excel(name = "供应商", orderNum = "1")
    private String supplier;
    /**
     * 物料编码
     */
    @TableField("materials_no")
    @ApiModelProperty(value = "物料编码", name = "materialsNo")
    @Excel(name = "物料编码", orderNum = "2")
    private String materialsNo;
    /**
     * 物料名称
     */
    @TableField("materials_name")
    @ApiModelProperty(value = "物料名称", name = "materialsName")
    @Excel(name = "物料名称", orderNum = "3")
    private String materialsName;
    /**
     * 车架号
     */
    @TableField("frame_no")
    @ApiModelProperty(value = "车架号")
    @Excel(name = "车架号", orderNum = "4")
    private String frameNo;
    /**
     * 发动机号
     */
    @TableField("engine_no")
    @ApiModelProperty(value = "发动机号")
    @Excel(name = "发动机号", orderNum = "5")
    private String engineNo;

    @ApiModelProperty(value = "采购收货时间(不必填)", example = "2018-02-14")
    @TableField("ktm_receiving_date")
    @Excel(name = "采购收获时间(不必填)", orderNum = "6")
    private Date ktmReceivingDate;
    /**
     * 数据版本号
     */
    @TableField("object_version_number")
    private Integer objectVersionNumber;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    @TableField("batch_no")
    @Excel(name = "批次号", orderNum = "7")
    private String batchNo;

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

    /**
     * 条码数量
     */
    @ApiModelProperty(value = "条码数量")
    @TableField("bar_code_number")
    @Excel(name = "条码数量", orderNum = "8")
    private BigDecimal barCodeNumber;


    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    @TableField("repository")
    @Excel(name = "仓库", orderNum = "9")
    private String repository;


    public void setObjectSetBasicAttribute(int userId, Date date) {
        this.createdBy = userId;
        this.lastUpdatedBy = userId;
        this.lastUpdateDate = date;
        this.creationDate = date;
    }

    public void setBasicAttributeForUpdate(int userId, Date date) {
        this.lastUpdatedBy = userId;
        this.lastUpdateDate = date;
    }

    @Override
    protected Serializable pkVal() {
        return this.ktmReceivingId;
    }

    @Override
    public String toString() {
        return "CfKtmReceivingOrder{" +
                ", ktmReceivingId=" + ktmReceivingId +
                ", purchaseOrderNo=" + purchaseOrderNo +
                ", supplier=" + supplier +
                ", materialsNo=" + materialsNo +
                ", materialsName=" + materialsName +
                ", frameNo=" + frameNo +
                ", engineNo=" + engineNo +
                ", ktmReceivingDate=" + ktmReceivingDate +
                ", objectVersionNumber=" + objectVersionNumber +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
