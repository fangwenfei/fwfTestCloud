package com.cfmoto.bar.code.model.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
 * 车架与车型对应表
 * </p>
 *
 * @author  space
 * @since 2019-02-27
 */
@TableName("cf_cft_relationship")
@ApiModel(value="CfCftRelationship",description="车架与车型对应表")
public class CfCftRelationship extends Model<CfCftRelationship> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId(value = "cft_relationship_id", type = IdType.AUTO)
    private Integer cftRelationshipId;
    /**
     * 车架
     */
    @ApiModelProperty(value="车架")
    @TableField("car_frame")
    @Excel( name="车架", orderNum="0" )
    private String carFrame;
    /**
     * 车型
     */
    @ApiModelProperty(value="车型")
    @TableField("car_type")
    @Excel( name="车型", orderNum="1" )
    private String carType;
    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    @Excel( name="备注", orderNum="2" )
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


    public Integer getCftRelationshipId() {
        return cftRelationshipId;
    }

    public void setCftRelationshipId(Integer cftRelationshipId) {
        this.cftRelationshipId = cftRelationshipId;
    }

    public String getCarFrame() {
        return carFrame;
    }

    public void setCarFrame(String carFrame) {
        this.carFrame = carFrame;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
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
        return this.cftRelationshipId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfCftRelationship{" +
                ", cftRelationshipId=" + cftRelationshipId +
                ", carFrame=" + carFrame +
                ", carType=" + carType +
                ", remarks=" + remarks +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
