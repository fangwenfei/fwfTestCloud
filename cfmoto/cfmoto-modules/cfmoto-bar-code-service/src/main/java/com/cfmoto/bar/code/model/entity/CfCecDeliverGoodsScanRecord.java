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
import java.util.Date;

/**
 * <p>
 * 部品网购发货扫描表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-04
 */
@TableName("cf_cec_deliver_goods_scan_record")
@ApiModel(value = "CfCecDeliverGoodsScanRecord", description = "部品网购发货扫描表")
@Data
public class CfCecDeliverGoodsScanRecord extends Model<CfCecDeliverGoodsScanRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "scan_record_id", type = IdType.AUTO)
    private Integer scanRecordId;
    /**
     * 交货单(发货通知单)
     */
    @ApiModelProperty(value = "交货单(发货通知单)")
    @TableField("deliver_order_no")
    private String deliverOrderNo;
    /**
     * 销售订单
     */
    @ApiModelProperty(value = "销售订单")
    @TableField("sales_order_no")
    private String salesOrderNo;
    /**
     * 行项目
     */
    @ApiModelProperty(value = "行项目")
    @TableField("row_item")
    private String rowItem;
    /**
     * 物料代码
     */
    @ApiModelProperty(value = "物料代码")
    @TableField("materials_no")
    private String materialsNo;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    @TableField("materials_name")
    private String materialsName;
    /**
     * 条码
     */
    @ApiModelProperty(value = "条码")
    private String barcode;
    /**
     * 条码类型
     */
    @ApiModelProperty(value = "条码类型")
    @TableField("barcode_type")
    private String barcodeType;
    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    private String spec;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;
    /**
     * 批号
     */
    @ApiModelProperty(value = "批号")
    @TableField("batch_no")
    private String batchNo;
    /**
     * 仓库
     */
    @ApiModelProperty(value = "仓库")
    private String warehouse;
    /**
     * 存储区域
     */
    @ApiModelProperty(value = "存储区域")
    @TableField("storage_area")
    private String storageArea;
    /**
     * 仓位
     */
    @ApiModelProperty(value = "仓位")
    @TableField("warehouse_position")
    private String warehousePosition;

    /**
     * 运单号
     */
    @ApiModelProperty(value = "运单号")
    @TableField("tracking_no")
    private String trackingNo;

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
    protected Serializable pkVal() {
        return this.scanRecordId;
    }
}
