package com.cfmoto.bar.code.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 获取下一编号
 * </p>
 *
 * @author  space
 * @since 2019-04-08
 */
@TableName("cf_next_number")
@ApiModel(value="CfNextNumber",description="获取下一编号")
public class CfNextNumber extends Model<CfNextNumber> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("handle")
    @ApiModelProperty(value="主键")
    private String handle;
    /**
     * 更新时间戳
     */
    @ApiModelProperty(value="更新时间戳")
    @TableField("change_stamp")
    private BigDecimal changeStamp;
    /**
     * 下一编号类别，用于区分不同编号规则
     */
    @ApiModelProperty(value="下一编号类别，用于区分不同编号规则")
    @TableField("next_number_type")
    private String nextNumberType;
    /**
     * 描述
     */
    @ApiModelProperty(value="描述")
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
     * 序列号进制数
     */
    @ApiModelProperty(value="序列号进制数")
    @TableField("sequence_base")
    private BigDecimal sequenceBase;
    /**
     * 最大序列号
     */
    @ApiModelProperty(value="最大序列号")
    @TableField("max_sequence")
    private BigDecimal maxSequence;
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
     * 编号格式
     */
    @ApiModelProperty(value="编号格式")
    @TableField("next_number_format")
    private String nextNumberFormat;
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
     * 报警阈值
     */
    @ApiModelProperty(value="报警阈值")
    @TableField("warning_threshold")
    private BigDecimal warningThreshold;
    /**
     * 重置规则
     */
    @ApiModelProperty(value="重置规则")
    private String reset;
    /**
     * 是否立即提交，暂未使用
     */
    @ApiModelProperty(value="是否立即提交，暂未使用")
    @TableField("commit_immediately")
    private String commitImmediately;
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


    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public BigDecimal getChangeStamp() {
        return changeStamp;
    }

    public void setChangeStamp(BigDecimal changeStamp) {
        this.changeStamp = changeStamp;
    }

    public String getNextNumberType() {
        return nextNumberType;
    }

    public void setNextNumberType(String nextNumberType) {
        this.nextNumberType = nextNumberType;
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

    public BigDecimal getSequenceBase() {
        return sequenceBase;
    }

    public void setSequenceBase(BigDecimal sequenceBase) {
        this.sequenceBase = sequenceBase;
    }

    public BigDecimal getMaxSequence() {
        return maxSequence;
    }

    public void setMaxSequence(BigDecimal maxSequence) {
        this.maxSequence = maxSequence;
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

    public String getNextNumberFormat() {
        return nextNumberFormat;
    }

    public void setNextNumberFormat(String nextNumberFormat) {
        this.nextNumberFormat = nextNumberFormat;
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

    public BigDecimal getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(BigDecimal warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public String getCommitImmediately() {
        return commitImmediately;
    }

    public void setCommitImmediately(String commitImmediately) {
        this.commitImmediately = commitImmediately;
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
        return this.handle;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfNextNumber{" +
                ", handle=" + handle +
                ", changeStamp=" + changeStamp +
                ", nextNumberType=" + nextNumberType +
                ", description=" + description +
                ", prefix=" + prefix +
                ", suffix=" + suffix +
                ", sequenceBase=" + sequenceBase +
                ", maxSequence=" + maxSequence +
                ", sequenceLength=" + sequenceLength +
                ", currentSequence=" + currentSequence +
                ", nextNumberFormat=" + nextNumberFormat +
                ", minSequence=" + minSequence +
                ", incr=" + incr +
                ", warningThreshold=" + warningThreshold +
                ", reset=" + reset +
                ", commitImmediately=" + commitImmediately +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}

