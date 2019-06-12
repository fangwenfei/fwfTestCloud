package com.cfmoto.bar.code.model.entity;

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
 * 调拨在途数据表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-04-07
 */
@TableName("cf_allot_on_way_data")
@ApiModel(value="CfAllotOnWayData",description="调拨在途数据表")
@Data
public class CfAllotOnWayData extends Model<CfAllotOnWayData> {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨在途数据表主键ID
     */
    @ApiModelProperty(value="调拨在途数据表主键ID")
    @TableId("allot_on_way_data_id")
    private Integer allotOnWayDataId;
    /**
     * 单号
     */
    @ApiModelProperty(value="单号")
    @TableField("order_no")
    private String orderNo;
    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("materials_name")
    private String materialsName;
    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    @TableField("materials_no")
    private String materialsNo;
    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String spec;
    /**
     * 条码
     */
    @ApiModelProperty(value="条码")
    private String barcode;
    /**
     * 条码类型
     */
    @ApiModelProperty(value="条码类型")
    @TableField("barcode_type")
    private String barcodeType;
    /**
     * 批次
     */
    @ApiModelProperty(value="批次")
    @TableField("batch_no")
    private String batchNo;
    /**
     * 数量
     */
    @ApiModelProperty(value="数量")
    private Integer number;
    /**
     * 调出仓库
     */
    @ApiModelProperty(value="调出仓库")
    @TableField("allot_out_warehouse")
    private String allotOutWarehouse;
    /**
     * 调入仓库
     */
    @ApiModelProperty(value="调入仓库")
    @TableField("allot_in_warehouse")
    private String allotInWarehouse;
    /**
     * 打印数量
     */
    @ApiModelProperty(value="打印数量")
    @TableField("print_number")
    private Integer printNumber;
    /**
     * 调入数量
     */
    @ApiModelProperty(value="调入数量")
    @TableField("allot_in_number")
    private Integer allotInNumber;

    @ApiModelProperty(value = "供应商")
    @TableField("supplier")
    private String supplier;

    /**
     * 数据创建者
     */
    @ApiModelProperty(value="数据创建者")
    @TableField("created_by")
    private Integer createdBy;
    /**
     * 数据创建时间
     */
    @ApiModelProperty(value="数据创建时间")
    @TableField("creation_date")
    private Date creationDate;
    /**
     * 数据最后修改者
     */
    @ApiModelProperty(value="数据最后修改者")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 数据最后修改时间
     */
    @ApiModelProperty(value="数据最后修改时间")
    @TableField("last_update_date")
    private Date lastUpdateDate;

    @ApiModelProperty(value="工厂")
    @TableField("factory")
    private String factory;

    @Override
    protected Serializable pkVal() {
        return this.allotOnWayDataId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }

    public void setBasicAttributeForUpdate(int userId,Date date){
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
    }
    @Override
    public String toString() {
        return "CfAllotOnWayData{" +
        ", allotOnWayDataId=" + allotOnWayDataId +
        ", orderNo=" + orderNo +
        ", materialsName=" + materialsName +
        ", materialsNo=" + materialsNo +
        ", spec=" + spec +
        ", barcode=" + barcode +
        ", barcodeType=" + barcodeType +
        ", batchNo=" + batchNo +
        ", number=" + number +
        ", allotOutWarehouse=" + allotOutWarehouse +
        ", allotInWarehouse=" + allotInWarehouse +
        ", printNumber=" + printNumber +
        ", allotInNumber=" + allotInNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
