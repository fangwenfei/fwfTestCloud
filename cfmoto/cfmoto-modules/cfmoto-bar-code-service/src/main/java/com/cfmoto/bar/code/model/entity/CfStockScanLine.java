package com.cfmoto.bar.code.model.entity;

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
 * 备料扫描记录
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-12
 */
@TableName("cf_stock_scan_line")
@ApiModel(value="CfStockScanLine",description="备料扫描记录")
@Data
public class CfStockScanLine extends Model<CfStockScanLine> {

    private static final long serialVersionUID = 1L;
    public static final String  STOCK_SCAN_LINE_STATE = "INVOICE_STATE";//SUBMIT:提交

    public static final String  STOCK_LIST_ID_SQL = "stock_list_id";//Sstock_list_id

    public static final String  STOCK_LINE_ID_SQL = "stock_line_id";//备料清单表主键

    public static final String  CREATED_BY_SQL = "created_by";//创建人

    public static final String  LAST_UPDATED_DATE_SQL = "last_updated_date";//备料清单表主键

    public static final String  STOCK_SPLIT = "STOCK_SPLIT";//拆分CODE

    public static final String  BARCODE_TYPE_KTM = "KTM";

    public static final String  BARCODE_TYPE_CP = "CP";

    public static final String  BARCODE_TYPE_EG = "EG";

    public static final String  BARCODE_TYPE_OT = "OT";

    public static final String  EX_BAR_CODE_NO = "扫描的条码不可为空";

    public static final String  EX_STOCK_LIST_NO_NOT_HAVING = "备料单号不存在";

    public static final String  EX_BAR_CODE_NUMBER = "该条码已被扣除";

    public static final String  EX_MATERIALS_NOT_HAVING = "该条码的物料不正确";

    public static final String  EX_NOT_USER_REPOSITORY = "该用户没有对应的仓库";

    public static final String  EX_BARCODE_NUMBER = "该物料数量已满发货数量，请注意";

    public static final String  EX_BAR_CODE_NO_NOT_HAVING = "扫描的条码不存在";

    public static final String  EX_BAR_CODE_CANT_USER = "该条码不可使用";

    public static final String  SUCCESS_SUBMIT = "提交成功";

    public static final String  EX_DOUBLE_DATA = "重复数据提交，请注意检查";

    public static final String  PARAMS_N = "N";
    public static final String  PARAMS_Y = "Y";

    public static final String  PARAMS_REPOSITORY = "repository";

    public static final String  PARAMS_STORAGE_AREA = "storage_area";

    public static final String  EX_STOCK_LINE_ID = "备料扫描记录主键";

    public static final String  EX_CHANGE_NUMBER = "修改数据不正确";

    public static final String  EX_CHANGE_NUMBER_TOO_BIG = "修改数据不可以大于该行数据数量";

    public static final String  EX_FUNCTION_JUDGE = "功能界面不正确，请到";

    public static final String  EX_STATUS_JUDGE = "订单状态已被关闭，请注意";

    public static final String  STATUS_COMPLETE ="C";//已完成
    public static final String  STATUS_UNCOMPLETE ="U";//进行中

    public static final String  PARAMS_BARCODE ="barcode";//sql代码

    public static final String  EX_BAR_CODE_DOUBLE = "该条码已被扫描";



    /**
     * 备料扫描记录主键
     */
    @ApiModelProperty(value="备料扫描记录主键")
    @TableId(value = "stock_line_id", type = IdType.AUTO)
    private Integer stockLineId;
    /**
     * 备料单号
     */
    @ApiModelProperty(value="备料单号")
    @TableField("stock_list_no")
    private String stockListNo;
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
    private String mode;
    /**
     * 条码
     */
    @ApiModelProperty(value="条码")
    private String barcode;
    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    private String repository;
    /**
     * 仓位
     */
    @ApiModelProperty(value="仓位")
    @TableField("warehouse_position")
    private String warehousePosition;
    /**
     * 存储区域
     */
    @ApiModelProperty(value="存储区域")
    @TableField("storage_area")
    private String storageArea;
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
    private BigDecimal number;
    /**
     * 状态
     */
    @ApiModelProperty(value="状态")
    private String status;

    /**
     * 其他表ID
     */
    @ApiModelProperty(value="其他表ID")
    @TableField("other_table_id")
    private Integer otherTableId;

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
     * 最后修改人
     */
    @ApiModelProperty(value="最后修改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后修改时间
     */
    @ApiModelProperty(value="最后修改时间")
    @TableField("last_updated_date")
    private Date lastUpdatedDate;
    /**
     * 备料单信息表主键
     */
    @ApiModelProperty(value="备料单信息表主键")
    @TableField("stock_list_id")
    private Integer stockListId;
    /**
     * 备料清单表主键
     */
    @ApiModelProperty(value="备料清单表主键")
    @TableField("stock_inventory_id")
    private Integer stockInventoryId;

    /**
     * 条码类型
     */
    @ApiModelProperty(value="条码类型(KTM：KTM,CP：CP,EG:发动机 ，OT：WP车架条码（库存条码）配件条码)")
    @TableField(value = "barcode_type")
    private String barcodeType;


    @Override
    protected Serializable pkVal() {
        return this.stockLineId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdatedDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfStockScanLine{" +
        ", stockLineId=" + stockLineId +
        ", stockListNo=" + stockListNo +
        ", materialsName=" + materialsName +
        ", materialsNo=" + materialsNo +
        ", mode=" + mode +
        ", barcode=" + barcode +
        ", repository=" + repository +
        ", warehousePosition=" + warehousePosition +
        ", storageArea=" + storageArea +
        ", batchNo=" + batchNo +
        ", number=" + number +
        ", status=" + status +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdatedDate=" + lastUpdatedDate +
        ", stockListId=" + stockListId +
        ", stockInventoryId=" + stockInventoryId +
        "}";
    }
}
