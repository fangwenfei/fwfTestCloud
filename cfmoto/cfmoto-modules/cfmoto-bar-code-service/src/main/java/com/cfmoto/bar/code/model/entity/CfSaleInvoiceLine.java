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
 * 销售发货单子表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-04
 */
@TableName("cf_sale_invoice_line")
@ApiModel(value="CfSaleInvoiceLine",description="销售发货单子表")
@Data
public class CfSaleInvoiceLine extends Model<CfSaleInvoiceLine> {

    private static final long serialVersionUID = 1L;

    public static final String  INVOICE_ROOT_ID_SQL = "invoice_root_id";

    public static final String  INVOICE_LINE_ID_SQL = "invoice_line_id";

    public static final String  INVOICE_LINE_BARCODE_NUMBER_SQL = "barCodeNumber";

    public static final String  EX_BAR_CODE_NO = "扫描的条码不可为空";

    public static final String  EX_BAR_CODE_NO_NOT_HAVING = "扫描的条码不存在";

    public static final String  EX_SALE_INVOICE_NO = "不正确的发货通知单,或者发货通知单为空";

    public static final String  EX_BARCODE_NUMBER = "该物料数量已满发货数量，请注意";

    public static final String  EX_NOT_NULL = "货物柜号，货柜铅封号不可以为空，请注意";

    public static final String  EX_BAR_CODE_NUMBER = "该条码已被扣除";

    public static final String  EX_BAR_CODE_CANT_USER = "该条码不可使用";

    public static final String  EX_DOUBLE_DATA = "重复数据提交，请注意检查";

    public static final String  BARCODE_TYPE_KTM = "KTM";

    public static final String  BARCODE_TYPE_CP = "CP";

    public static final String  BARCODE_TYPE_EG = "EG";

    public static final String  BARCODE_TYPE_OT = "OT";

    public static final String  SUCCESS_SUBMIT = "提交成功";

    public static final String  EX_BAR_CODE_IN_WAREHOUSE = "该条码已经在库，请注意";

    public static final String  EX_FAIL = "数据处理失败，请联系管理员";

    public static final String  EX_MATERIALS_NOT_HAVING = "该条码的物料不正确";

    public static final String  EX_BARCODE_DOUBLE = "该条码已经被扫描，请注意";

    public static final String  EX_BAR_CODE_BARCODE_TYPE_NULL = "该条码类型不存在，请注意";

    public static final String  EX_BAR_CODE_BARCODE_TYPE_ERROR = "销售退货只支持发动机，整车退货，请注意";
    /**
     * 单据行主键
     */
    @ApiModelProperty(value="单据行主键")
    @TableId(value = "invoice_line_id", type = IdType.AUTO)
    private Integer invoiceLineId;
    /**
     * 单据头主键
     */
    @ApiModelProperty(value="单据头主键")
    @TableField(value = "invoice_root_id")
    private Integer invoiceRootId;

    /**
     * 条码类型
     */
    @ApiModelProperty(value="条码类型(KTM：KTM,CP：CP,EG:发动机 ，OT：WP车架条码（库存条码）配件条码)")
    @TableField(value = "barcode_type")
    private String barcodeType;


    /**
     * 其他表ID
     */
    @ApiModelProperty(value="其他表ID")
    @TableField("other_table_id")
    private Integer otherTableId;

    /**
     * 单据汇总主键
     */
    @ApiModelProperty(value="单据汇总主键")
    @TableField("invoice_id")
    private Integer invoiceId;
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
     * 条码
     */
    @ApiModelProperty(value="条码")
    @TableField("bar_code_no")
    private String barCodeNo;
    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String mode;
    /**
     * 批次号
     */
    @ApiModelProperty(value="批次号")
    @TableField("batch_no")
    private String batchNo;
    /**
     * 条码数量
     */
    @ApiModelProperty(value="条码数量")
    @TableField("bar_code_number")
    private BigDecimal barCodeNumber;
    /**
     * 仓位
     */
    @ApiModelProperty(value="仓位")
    @TableField("warehouse_position")
    private String warehousePosition;
    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    private String warehouse;
    /**
     * 货物柜号
     */
    @ApiModelProperty(value="货物柜号")
    @TableField("cabinet_no")
    private String cabinetNo;
    /**
     * 货柜铅封号
     */
    @ApiModelProperty(value="货柜铅封号")
    @TableField("container_seal_no")
    private String containerSealNo;
    /**
     * 运单号
     */
    @ApiModelProperty(value="运单号")
    @TableField("waybill_no")
    private String waybillNo;
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
     * 合同号
     */
    @ApiModelProperty(value="合同号")
    @TableField("contract_no")
    private String contractNo;


    /**
     * 通知单行项目
     */
    @ApiModelProperty(value="通知单行项目")
    @TableField("invoice_item")
    private String invoiceItem;

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
     * 存储区域
     */
    @ApiModelProperty(value="存储区域")
    @TableField("storage_area")
    private String storageArea;


    @Override
    protected Serializable pkVal() {
        return this.invoiceLineId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfSaleInvoiceLine{" +
        ", invoiceLineId=" + invoiceLineId +
        ", invoiceId=" + invoiceId +
        ", materialName=" + materialName +
        ", materialCode=" + materialCode +
        ", barCodeNo=" + barCodeNo +
        ", mode=" + mode +
        ", batchNo=" + batchNo +
        ", barCodeNumber=" + barCodeNumber +
        ", warehousePosition=" + warehousePosition +
        ", warehouse=" + warehouse +
        ", cabinetNo=" + cabinetNo +
        ", containerSealNo=" + containerSealNo +
        ", waybillNo=" + waybillNo +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
