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
 * 生产超领扫描表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-18
 */
@TableName("cf_stock_too_receive_line")
@ApiModel(value="CfStockTooReceiveLine",description="生产超领扫描表")
@Data
public class CfStockTooReceiveLine extends Model<CfStockTooReceiveLine> {

    private static final long serialVersionUID = 1L;
    public static final String  STOCK_SCAN_LINE_STATE = "INVOICE_STATE";//SUBMIT:提交

    public static final String  STOCK_ROOT_ID_SQL = "stock_root_id";//Sstock_list_id

    public static final String  STOCK_LINE_ID_SQL = "stock_line_id";//备料清单表主键

    public static final String  CREATED_BY_SQL = "created_by";//创建人

    public static final String  LAST_UPDATED_DATE_SQL = "last_updated_date";//备料清单表主键

    public static final String  PARAMS_REPOSITORY = "repository";

    public static final String  PARAMS_STORAGE_AREA = "storage_area";

    public static final String  PARAMS_BARCODE ="barcode";//sql代码

    public static final String  BARCODE_TYPE_KTM = "KTM";

    public static final String  BARCODE_TYPE_CP = "CP";

    public static final String  BARCODE_TYPE_EG = "EG";

    public static final String  BARCODE_TYPE_OT = "OT";

    public static final String  PARAMS_N = "N";

    public static final String  PARAMS_Y = "Y";

    public static final String  EX_BAR_CODE_NO = "扫描的条码不可为空";

    public static final String  EX_STOCK_LIST_NO_NOT_HAVING = "备料单号不存在";

    public static final String  EX_BAR_CODE_NUMBER = "该条码已被扣除";

    public static final String  EX_MATERIALS_NOT_HAVING = "该条码的物料不正确";

    public static final String  EX_NOT_USER_REPOSITORY = "该对应的仓库不正确";

    public static final String  EX_BARCODE_NUMBER = "该物料数量已满发货数量，请注意";

    public static final String  EX_BAR_CODE_NO_NOT_HAVING = "扫描的条码不存在";

    public static final String  EX_BAR_CODE_CANT_USER = "该条码不可使用";

    public static final String  SUCCESS_SUBMIT = "提交成功";

    public static final String  EX_DOUBLE_DATA = "重复数据提交，请注意检查";

    public static final String  EX_BAR_CODE_DOUBLE = "该条码已被扫描";

    public static final String  EX_STOCK_LINE_ID = "备料扫描记录主键";

    public static final String  EX_CHANGE_NUMBER = "修改数据不正确";

    public static final String  EX_CHANGE_NUMBER_TOO_BIG = "修改数据不可以大于该行数据数量";




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
     * 备料生产超领根表表主键
     */
    @ApiModelProperty(value="备料生产超领根表表主键")
    @TableField("stock_root_id")
    private Integer stockRootId;
    /**
     * 备料生产超领头表主键
     */
    @ApiModelProperty(value="备料生产超领头表主键")
    @TableField("stock_header_id")
    private Integer stockHeaderId;
    /**
     * 条码类型(KTM：KTM,CP：CP,EG:发动机 ，OT：WP车架条码（库存条码）配件条码CP EG是序列号

     */
    @ApiModelProperty(value="条码类型(KTM：KTM,CP：CP,EG:发动机 ，OT：WP车架条码（库存条码）配件条码 CP EG是序列号 ")
    @TableField("barcode_type")
    private String barcodeType;
    /**
     * 其他表ID
     */
    @ApiModelProperty(value="其他表ID")
    @TableField("other_table_id")
    private Integer otherTableId;

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
        return "CfStockTooReceiveLine{" +
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
        ", stockRootId=" + stockRootId +
        ", stockHeaderId=" + stockHeaderId +
        ", barcodeType=" + barcodeType +
        ", otherTableId=" + otherTableId +
        "}";
    }
}
