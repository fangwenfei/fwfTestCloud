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
 * 销售发货单
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-04
 */
@TableName("cf_sale_invoice_header")
@ApiModel(value="CfSaleInvoiceHeader",description="销售发货单")
@Data
public class CfSaleInvoiceHeader extends Model<CfSaleInvoiceHeader> {

    private static final long serialVersionUID = 1L;

    public static final String  INVOICE_CHANNEL_1000 = "1000";//1000代表国内

    public static final String  INVOICE_ROOT_ID_SQL = "invoice_root_id";

    public static final String  INVOICE_MATERIAL_CODE_SQL = "material_code";

    public static final String  INVOICE_SALE_ORDER_NO_SQL = "sale_order_no";

    public static final String  INVOICE_WAREHOUSE_SQL = "warehouse";

    public static final String  INVOICE_SALES_ITEM_SQL = "sales_item";

    public static final String  INVOICE_ID_SQL = "invoice_id";

    public static final String  PARAM_INVOICE_STATE_SAP_A= "A";//进行中

    public static final String  PARAM_INVOICE_STATE_SAP_C= "C";//已完成

    public static final String  CF_SALE_INVOICE_HEADER_SAP = "接口获取数据失败";

    public static final String  INVOICE_CHANNEL_EX = "分销渠道数据丢失";//1000代表国内

    public static final String  CF_SALE_INVOICE_STATE_SAP_ERROR = "通知单处于已完成，请注意！";

    public static final String  PARAM_INVOICE_TYPE_SAP_ZD= "ZD";//销售发货


    public static final String  PARAM_INVOICE_TYPE_SAP_ZR= "ZR";//销售发货


    public static final String  PARAM_INVOICE_TYPE_SAP_ERROR= "打开功能界面错误，请注意！";//已完成

    public static final String  OBJECT_VERSION_NUMBER= "object_version_number";//数据版版本号

    public static final String  EX_MATERIALS_CAN_NOT_INSERT = "物料代码+仓库+销售订单+销售行项目，库存没有找到数据，请注意";

    public static final String  EX_MATERIALS_INSERT_FULL = "该物料的汇总数量已满，请注意";

    public static final String  EX_MATERIALS_ERROR = "物料代码不正确，请注意";

    public static final String  EX_MATERIALS_WAREHOUSE_ERROR = "物料代码和仓库不正确，请注意";
    /**
     * 单据主键
     */
    @ApiModelProperty(value="单据主键")
    @TableId(value = "invoice_id", type = IdType.AUTO)
    private Integer invoiceId;

    /**
     * 单据头主键
     */
    @ApiModelProperty(value="单据头主键")
    @TableField(value = "invoice_root_id")
    private Integer invoiceRootId;
    /**
     * 单据号
     */
    @ApiModelProperty(value="单据号")
    @TableField("invoice_no")
    private String invoiceNo;
    /**
     * 单据类型
     */
    @ApiModelProperty(value="单据类型")
    @TableField("invoice_type")
    private String invoiceType;
    /**
     * 购货单位
     */
    @ApiModelProperty(value="购货单位")
    @TableField("purchase_unit")
    private String purchaseUnit;
    /**
     * 部门
     */
    @ApiModelProperty(value="部门")
    private String department;
    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("material_name")
    private String materialName;
    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    @TableField("material_code")
    private String materialCode;
    /**
     * 已扫描数量
     */
    @ApiModelProperty(value="已扫描数量")
    @TableField("scanning_number")
    private BigDecimal scanningNumber;
    /**
     * 需求数量
     */
    @ApiModelProperty(value="需求数量")
    @TableField("need_number")
    private BigDecimal needNumber;
    /**
     * 批次号
     */
    @ApiModelProperty(value="批次号")
    @TableField("batch_no")
    private String batchNo;
    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String mode;
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
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sale_order_no")
    private String saleOrderNo;

    /**
     * 销售行项目
     */
    @ApiModelProperty(value="销售行项目")
    @TableField("sales_item")
    private String salesItem;

    /**
     * 通知单行项目
     */
    @ApiModelProperty(value="通知单行项目")
    @TableField("invoice_item")
    private String invoiceItem;

    /**
     * 合同号
     */
    @ApiModelProperty(value="合同号")
    @TableField("contract_no")
    private String contractNo;


    /**
     * 数据版本号
     */
    @ApiModelProperty(value="数据版本号")
    @TableField("object_version_number")
    private Integer objectVersionNumber;

    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    @TableField("warehouse")
    private String warehouse;


    /**
     * 分销渠道
     */
    @ApiModelProperty(value="分销渠道")
    @TableField("channel")
    private String channel;


    @Override
    protected Serializable pkVal() {
        return this.invoiceId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfSaleInvoiceHeader{" +
        ", invoiceId=" + invoiceId +
        ", invoiceNo=" + invoiceNo +
        ", invoiceType=" + invoiceType +
        ", purchaseUnit=" + purchaseUnit +
        ", department=" + department +
        ", materialName=" + materialName +
        ", materialCode=" + materialCode +
        ", scanningNumber=" + scanningNumber +
        ", needNumber=" + needNumber +
        ", batchNo=" + batchNo +
        ", mode=" + mode +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
