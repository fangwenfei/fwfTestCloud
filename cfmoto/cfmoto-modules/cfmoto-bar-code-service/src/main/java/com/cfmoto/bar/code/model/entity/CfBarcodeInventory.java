package com.cfmoto.bar.code.model.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 条形码库存表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-05
 */
@TableName("cf_barcode_inventory")
@ApiModel(value = "CfBarcodeInventory", description = "条形码库存表")
@Data
public class CfBarcodeInventory extends Model<CfBarcodeInventory> {

    private static final long serialVersionUID = 1L;

    /**
     * N为不可用状态
     */
    public static final String CF_BARCODE_INVENTORY_STATE_NOT_USER = "N";

    public static final String CF_BARCODE_INVENTORY_MATERIALS_NO_SQL = "materials_no";//物料编码
    public static final String CF_BARCODE_INVENTORY_BARCODE_SQL = "barcode";//条码
    public static final String CF_BARCODE_INVENTORY_MATERIALS_NAME_SQL = "materials_name";//物料名称
    public static final String CF_BARCODE_INVENTORY_BATCH_NO_SQL = "batch_no";//批次号
    public static final String CF_BARCODE_INVENTORY_WAREHOUSE_SQL = "warehouse";//仓库
    public static final String CF_BARCODE_INVENTORY_STORAGE_AREA_SQL = "storage_area";//存储区域
    public static final String CF_BARCODE_INVENTORY_WAREHOUSE_POSITION_SQL = "warehouse_position";//仓位
    public static final String CF_BARCODE_INVENTORY_SUPPLER_SQL = "suppler";//供应商

    public static final String CF_BARCODE_INVENTORY_PARAM_ERROR = "工厂，仓库";//参数报错提示
    public static final String CF_BARCODE_SPLIT_NUMBER_PARAM_ERROR = "拆分单位或者拆分总量不可以为0";//参数报错提示

    public static final String  BARCODE_TYPE_OT = "OT";
    public static final String  BARCODE_TYPE_CP = "CP";
    public static final String  BARCODE_TYPE_EG = "EG";

    public static final String  BARCODE_IS_TYPE_NO_CP = "PRODUCTION_INTO_WAREHOUSE_PRINT_NO_CP";
    public static final String  BARCODE_IS_TYPE_CP = "PRODUCTION_INTO_WAREHOUSE_PRINT_CP";

    public static final String  BARCODE_BATCH_NO = "BATCH_NO";

    public static final String  BARCODE_SALES_ITEM_DEFAULT = "000000";//王昌恒说写死六个零，我是反对的。

    /**
     * 条形码库存主键
     */
    @ApiModelProperty(value = "条形码库存主键")
    @TableId(value = "barcode_inventory_id", type = IdType.AUTO)
    private Integer barcodeInventoryId;

    /**
     * 条码
     */
    @ApiModelProperty(value = "条码")
    @Excel(name = "条码")
    private String barcode;

    /**
     * 条码类型
     */
    @ApiModelProperty(value = "条码类型")
    @TableField("barcode_type")
    @Excel(name = "条码类型",orderNum = "1")
    private String barcodeType;

    /**
     * 物料编码
     */
    @ApiModelProperty(value = "物料编码")
    @TableField("materials_no")
    @Excel(name = "物料编码",orderNum = "2")
    private String materialsNo;

    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    @TableField("materials_name")
    @Excel(name = "物料名称",orderNum = "3")
    private String materialsName;

    /**
     * 仓位
     */
    @ApiModelProperty(value = "仓位")
    @TableField("warehouse_position")
    @NotNull
    @Excel(name = "仓位",orderNum = "4")
    private String warehousePosition;

    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    @Excel(name = "仓库",orderNum = "5")
    private String warehouse;

    /**
     * 存储区域
     */
    @ApiModelProperty(value = "存储区域")
    @TableField("storage_area")
    @Excel(name = "存储区域",orderNum = "6")
    private String storageArea;

    /**
     * 条码数量
     */
    @ApiModelProperty(value = "条码数量")
    @TableField("bar_code_number")
    @Excel(name = "条码数量",orderNum = "7")
    private BigDecimal barCodeNumber;

    /**
     * 冻结库存数量
     */
    @ApiModelProperty(value = "冻结库存数量")
    @TableField(exist = false)
    @Excel(name = "冻结库存数量",orderNum = "8")
    private BigDecimal freezeInventoryNumber;

    /**
     * 生产任务单
     */
    @ApiModelProperty(value = "生产任务单")
    @TableField("production_task_order")
    @Excel(name = "生产任务单",orderNum = "9")
    private String productionTaskOrder;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态",orderNum = "10")
    private String state;

    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    @Excel(name = "规格型号",orderNum = "11")
    private String mode;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    @Excel(name = "批次号",orderNum = "12")
    @TableField("batch_no")
    private String batchNo;

    /**
     * 车型号
     */
    @ApiModelProperty(value = "车型号")
    @TableField("car_model")
    @Excel(name = "车型号",orderNum = "13")
    private String carModel;

    /**
     * 打印时间
     */
    @ApiModelProperty(value = "打印时间")
    @TableField("printing_date")
    private Date printingDate;

    /**
     * 打印人员
     */
    @ApiModelProperty(value = "打印人员")
    @TableField("printing_by")
    private Integer printingBy;

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
     * 最后更改人
     */
    @ApiModelProperty(value = "最后更改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;

    /**
     * 最后更改时间
     */
    @ApiModelProperty(value = "最后更改时间")
    @TableField("last_update_date")
    private Date lastUpdateDate;


    /**
     * 供应商
     */
    @ApiModelProperty(value="供应商")
    @TableField("suppler")
    @Excel(name = "供应商",orderNum = "14")
    private String suppler;

    /**
     * 质检结果（Y：合格，N：不合格）
     */
    @ApiModelProperty(value="质检结果（Y：合格，N：不合格）")
    @TableField("qresult")
    @Excel(name = "质检结果",orderNum = "15")
    private String qresult;
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sale_order_no")
    @Excel(name = "销售订单",orderNum = "16")
    private String saleOrderNo;

    /**
     * 销售行项目
     */
    @ApiModelProperty(value="销售行项目")
    @TableField("sales_item")
    @Excel(name = "销售行项目",orderNum = "17")
    private String salesItem;


    /**
     * 合同号
     */
    @ApiModelProperty(value="合同号")
    @TableField("contract_no")
    @Excel(name = "合同号",orderNum = "18")
    private String contractNo;

    /**
     * 工厂
     */
    @ApiModelProperty(value="工厂")
    @TableField("factory")
    @Excel(name = "工厂",orderNum = "19")
    private String factory;




    @Override
    protected Serializable pkVal() {
        return this.barcodeInventoryId;
    }

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
    public String toString() {
        return "CfBarcodeInventory{" +
                ", barcodeInventoryId=" + barcodeInventoryId +
                ", barcode=" + barcode +
                ", barcodeType=" + barcodeType +
                ", materialsNo=" + materialsNo +
                ", materialsName=" + materialsName +
                ", warehousePosition=" + warehousePosition +
                ", warehouse=" + warehouse +
                ", storageArea=" + storageArea +
                ", barCodeNumber=" + barCodeNumber +
                ", productionTaskOrder=" + productionTaskOrder +
                ", state=" + state +
                ", mode=" + mode +
                ", batchNo=" + batchNo +
                ", carModel=" + carModel +
                ", printingDate=" + printingDate +
                ", printingBy=" + printingBy +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
