package com.cfmoto.bar.code.model.entity;

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
 * 
 * </p>
 *
 * @author  space
 * @since 2019-04-08
 */
@TableName("cf_deliver_goods_scan")
@ApiModel(value="CfDeliverGoodsScan",description="部品发货扫描表")
public class CfDeliverGoodsScan extends Model<CfDeliverGoodsScan> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId("deliver_goods_scan_id")
    private String deliverGoodsScanId;
    /**
     * 部品发货汇总临时表-主键
     */
    @ApiModelProperty(value="部品发货汇总临时表-主键")
    @TableField("deliver_goods_sum_id_ref")
    private String deliverGoodsSumIdRef;
    /**
     * 行项目
     */
    @ApiModelProperty(value="行项目")
    @TableField("row_item")
    private Integer rowItem;
    /**
     * 部品发货临时表-主键
     */
    @ApiModelProperty(value="部品发货临时表-主键")
    @TableField("deliver_goods_id_ref")
    private String deliverGoodsIdRef;
    /**
     * 箱号
     */
    @ApiModelProperty(value="箱号")
    @TableField("case_no")
    private String caseNo;
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


    public String getDeliverGoodsScanId() {
        return deliverGoodsScanId;
    }

    public void setDeliverGoodsScanId(String deliverGoodsScanId) {
        this.deliverGoodsScanId = deliverGoodsScanId;
    }

    public String getDeliverGoodsSumIdRef() {
        return deliverGoodsSumIdRef;
    }

    public void setDeliverGoodsSumIdRef(String deliverGoodsSumIdRef) {
        this.deliverGoodsSumIdRef = deliverGoodsSumIdRef;
    }

    public Integer getRowItem() {
        return rowItem;
    }

    public void setRowItem(Integer rowItem) {
        this.rowItem = rowItem;
    }

    public String getDeliverGoodsIdRef() {
        return deliverGoodsIdRef;
    }

    public void setDeliverGoodsIdRef(String deliverGoodsIdRef) {
        this.deliverGoodsIdRef = deliverGoodsIdRef;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
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
        return this.deliverGoodsScanId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfDeliverGoodsScan{" +
        ", deliverGoodsScanId=" + deliverGoodsScanId +
        ", deliverGoodsSumIdRef=" + deliverGoodsSumIdRef +
        ", rowItem=" + rowItem +
        ", deliverGoodsIdRef=" + deliverGoodsIdRef +
        ", caseNo=" + caseNo +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
