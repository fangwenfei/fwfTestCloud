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

/**
 * <p>
 * 获取销售订单下一编号
 * </p>
 *
 * @author  space
 * @since 2019-04-12
 */
@TableName("cf_sale_next_number")
@ApiModel(value="CfSaleNextNumber",description="获取销售订单下一编号")
public class CfSaleNextNumber extends Model<CfSaleNextNumber> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sale_next_number_id", type = IdType.AUTO)
    private Integer saleNextNumberId;
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sale_order")
    private String saleOrder;
    /**
     * 销售订单描述
     */
    @ApiModelProperty(value="销售订单描述")
    private String description;
    /**
     * 前缀
     */
    @ApiModelProperty(value="前缀")
    private String prefix;
    /**
     * 后缀
     */
    @ApiModelProperty(value="后缀")
    private String suffix;
    /**
     * 序列号长度
     */
    @ApiModelProperty(value="序列号长度")
    @TableField("sequence_length")
    private BigDecimal sequenceLength;
    /**
     * 当前序列号，根据当前编号以此生成
     */
    @ApiModelProperty(value="当前序列号，根据当前编号以此生成")
    @TableField("current_sequence")
    private BigDecimal currentSequence;
    /**
     * 最小序列号，重置序列号时的初始序号
     */
    @ApiModelProperty(value="最小序列号，重置序列号时的初始序号")
    @TableField("min_sequence")
    private BigDecimal minSequence;
    /**
     * 序号增长步长
     */
    @ApiModelProperty(value="序号增长步长")
    private BigDecimal incr;
    /**
     * 重置规则
     */
    @ApiModelProperty(value="重置规则")
    private String reset;
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


    public Integer getSaleNextNumberId() {
        return saleNextNumberId;
    }

    public void setSaleNextNumberId(Integer saleNextNumberId) {
        this.saleNextNumberId = saleNextNumberId;
    }

    public String getSaleOrder() {
        return saleOrder;
    }

    public void setSaleOrder(String saleOrder) {
        this.saleOrder = saleOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public BigDecimal getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(BigDecimal sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public BigDecimal getCurrentSequence() {
        return currentSequence;
    }

    public void setCurrentSequence(BigDecimal currentSequence) {
        this.currentSequence = currentSequence;
    }

    public BigDecimal getMinSequence() {
        return minSequence;
    }

    public void setMinSequence(BigDecimal minSequence) {
        this.minSequence = minSequence;
    }

    public BigDecimal getIncr() {
        return incr;
    }

    public void setIncr(BigDecimal incr) {
        this.incr = incr;
    }

    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
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
        return this.saleNextNumberId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfSaleNextNumber{" +
        ", saleNextNumberId=" + saleNextNumberId +
        ", saleOrder=" + saleOrder +
        ", description=" + description +
        ", prefix=" + prefix +
        ", suffix=" + suffix +
        ", sequenceLength=" + sequenceLength +
        ", currentSequence=" + currentSequence +
        ", minSequence=" + minSequence +
        ", incr=" + incr +
        ", reset=" + reset +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
