package com.cfmoto.bar.code.model.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 部品零部件标签模板对照信息维护
 * </p>
 *
 * @author  space
 * @since 2019-04-20
 */
@TableName("cf_part_label_relationship")
@ApiModel(value="CfPartLabelRelationship",description="部品零部件标签模板对照信息维护")
public class CfPartLabelRelationship extends Model<CfPartLabelRelationship> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId(value = "cf_part_label_relationship_id", type = IdType.AUTO)
    private Integer cfPartLabelRelationshipId;
    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    @Excel( name = "物料代码", orderNum = "0" )
    private String item;
    /**
     * 模板名称
     */
    @ApiModelProperty(value="模板名称")
    @TableField("label_template")
    @Excel( name = "模板名称", orderNum = "1" )
    private String labelTemplate;
    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    @Excel( name = "备注", orderNum = "2" )
    private String remarks;
    /**
     * 数据创建人
     */
    @ApiModelProperty(value="数据创建人")
    @TableField("created_by")
    private Integer createdBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    @TableField("creation_date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;


    public Integer getCfPartLabelRelationshipId() {
        return cfPartLabelRelationshipId;
    }

    public void setCfPartLabelRelationshipId(Integer cfPartLabelRelationshipId) {
        this.cfPartLabelRelationshipId = cfPartLabelRelationshipId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLabelTemplate() {
        return labelTemplate;
    }

    public void setLabelTemplate(String labelTemplate) {
        this.labelTemplate = labelTemplate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
        return this.cfPartLabelRelationshipId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfPartLabelRelationship{" +
                ", cfPartLabelRelationshipId=" + cfPartLabelRelationshipId +
                ", item=" + item +
                ", labelTemplate=" + labelTemplate +
                ", remarks=" + remarks +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
