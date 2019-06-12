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
 * 
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-04
 */
@TableName("cf_sale_invoice_root")
@ApiModel(value="CfSaleInvoiceRoot",description="")
@Data
public class CfSaleInvoiceRoot extends Model<CfSaleInvoiceRoot> {

    private static final long serialVersionUID = 1L;
    public static final String  INVOICE_STATE = "INVOICE_STATE";//SUBMIT:提交，RETURN：退货，PICK：销售集中捡配
    public static final String  INVOICE_STATE_SUBMIT = "SUBMIT";//SUBMIT:提交，RETURN：退货，PICK：销售集中捡配
    public static final String  INVOICE_STATE_RETURN = "RETURN";//SUBMIT:提交，RETURN：退货，PICK：销售集中捡配
    public static final String  INVOICE_STATE_PICK = "PICK";//SUBMIT:提交，RETURN：退货，PICK：销售集中捡配

    /**
     * 单据主键
     */
    @ApiModelProperty(value="单据主键")
    @TableId(value = "invoice_root_id", type = IdType.AUTO)
    private Integer invoiceRootId;
    /**
     * 部门
     */
    @ApiModelProperty(value="部门")
    private String department;
    /**
     * 单据号
     */
    @ApiModelProperty(value="单据号")
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 状态（SUBMIT:提交，RETURN：退货，PICK：销售集中捡配）
     */
    @ApiModelProperty(value="状态（SUBMIT:提交，RETURN：退货，PICK：销售集中捡配）")
    @TableField("invoice_state")
    private String invoiceState;

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
     * 最后更改人
     */
    @ApiModelProperty(value="最后更改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后更改时间
     */
    @ApiModelProperty(value="最后更改时间")
    @TableField("last_update_date")
    private Date lastUpdateDate;

    /**
     * 分销渠道
     */
    @ApiModelProperty(value="分销渠道")
    @TableField("channel")
    private String channel;

    /**
     * 数据版本号
     */
    @ApiModelProperty(value="数据版本号")
    @TableField("object_version_number")
    private Integer objectVersionNumber;


    /**
     * 购货单位
     */
    @ApiModelProperty(value="购货单位")
    @TableField("purchase_unit")
    private String purchaseUnit;


    @Override
    protected Serializable pkVal() {
        return this.invoiceRootId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfSaleInvoiceRoot{" +
        ", invoiceRootId=" + invoiceRootId +
        ", department=" + department +
        ", invoiceNo=" + invoiceNo +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
