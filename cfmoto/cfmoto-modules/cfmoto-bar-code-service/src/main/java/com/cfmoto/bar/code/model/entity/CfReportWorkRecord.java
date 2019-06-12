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

/**
 * <p>
 * 扫描报工记录表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-06-11
 */
@TableName("cf_report_work_record")
@ApiModel(value="CfReportWorkRecord",description="扫描报工记录表")
public class CfReportWorkRecord extends Model<CfReportWorkRecord> {

    private static final long serialVersionUID = 1L;
    public static final String BARCODE_SQL="barcode";

    /**
     * 报工信息记录表主键ID
     */
    @ApiModelProperty(value="报工信息记录表主键ID")
    @TableId(value = "report_work_record_id", type = IdType.AUTO)
    private Integer reportWorkRecordId;
    /**
     * 条码
     */
    @ApiModelProperty(value="条码")
    private String barcode;
    /**
     * 条码类型EG/OP
     */
    @ApiModelProperty(value="条码类型EG/OP")
    @TableField("barcode_type")
    private String barcodeType;
    /**
     * 生产订单
     */
    @ApiModelProperty(value="生产订单")
    @TableField("production_task_order")
    private String productionTaskOrder;
    /**
     * 工序号（更新此处工序编号）
     */
    @ApiModelProperty(value="工序号（更新此处工序编号）")
    @TableField("work_number")
    private String workNumber;
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


    public Integer getReportWorkRecordId() {
        return reportWorkRecordId;
    }

    public void setReportWorkRecordId(Integer reportWorkRecordId) {
        this.reportWorkRecordId = reportWorkRecordId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(String barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getProductionTaskOrder() {
        return productionTaskOrder;
    }

    public void setProductionTaskOrder(String productionTaskOrder) {
        this.productionTaskOrder = productionTaskOrder;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Integer lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    protected Serializable pkVal() {
        return this.reportWorkRecordId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfReportWorkRecord{" +
        ", reportWorkRecordId=" + reportWorkRecordId +
        ", barcode=" + barcode +
        ", barcodeType=" + barcodeType +
        ", productionTaskOrder=" + productionTaskOrder +
        ", workNumber=" + workNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
