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
 * 采购退货订单表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-21
 */
@TableName("cf_returned_order")
@Data
@ApiModel(value=" CfReturnedOrder",description="采购退货订单表")
public class CfReturnedOrder extends Model<CfReturnedOrder> {

    private static final long serialVersionUID = 1L;

    public static final  String  CF_RETURNED_ORDER_SQL_ADD="采购退货订单表数据插入有问题，请联系管理员";
    /**
     * 退货订单  
     */
    @TableField("returned_purchase_order")
    @ApiModelProperty(value="退货订单")
    private String returnedPurchaseOrder;

    @TableId("returned_purchase_id")
    @ApiModelProperty(value="采购退货订单表主键(不必填)",example="1")
    private Integer returnedPurchaseId;
    /**
     * 物料编码
     */
    @TableField("materials_no")
    @ApiModelProperty(value="物料编码")
    private String materialsNo;
    /**
     * 实收数量
     */
    @TableField("actual_quantity")
    @ApiModelProperty(value="实收数量")
    private String actualQuantity;
    /**
     * 物料名称
     */
    @TableField("materials_name")
    @ApiModelProperty(value="物料名称")
    private String materialsName;
    /**
     * 供应商
     */
    @ApiModelProperty(value="供应商")
    private String supplier;
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


    public String getReturnedPurchaseOrder() {
        return returnedPurchaseOrder;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    protected Serializable pkVal() {
        return this.returnedPurchaseId;
    }

    @Override
    public String toString() {
        return "CfReturnedOrder{" +
        ", returnedPurchaseOrder=" + returnedPurchaseOrder +
        ", returnedPurchaseId=" + returnedPurchaseId +
        ", materialsNo=" + materialsNo +
        ", actualQuantity=" + actualQuantity +
        ", materialsName=" + materialsName +
        ", supplier=" + supplier +
        ", objectVersionNumber=" + objectVersionNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
