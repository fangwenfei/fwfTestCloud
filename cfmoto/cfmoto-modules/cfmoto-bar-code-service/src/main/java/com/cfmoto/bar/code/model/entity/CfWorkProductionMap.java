package com.cfmoto.bar.code.model.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-06-10
 */
@TableName("cf_work_production_map")
@ApiModel(value="CfWorkProductionMap",description="")
public class CfWorkProductionMap extends Model<CfWorkProductionMap> {

    private static final long serialVersionUID = 1L;
    public static final String  SQL_PRODUCTION_NAME = "production_name";
    public static final String  SQL_WORK_NO = "work_no";
    public static final  String  CF_WORK_PRODUCTION_MAP_EX="系统异常请联系管理员，清注意!";

    public static final  String  BARCODE_EX="请填写条码数据，清注意!";

    public static final  String WORKNO_EX="请填写报工序列号，清注意!";

    public static final  String BARCODE_IS_NULL_EX="条码不存在，请注意！";

    public static final  String BARCODE_USERD_EX="当前已报工，清注意!";

    public static final  String WORK_NO_0010="0010";

    /**
     * 报工序号
     */
    @ApiModelProperty(value="报工序号")
    @TableField("work_no")
    private String workNo;
    /**
     * 主键Id
     */
    @ApiModelProperty(value="主键Id")
    @TableId(value = "work_production_id", type = IdType.AUTO)
    private Integer workProductionId;
    /**
     * 线体名称
     */
    @ApiModelProperty(value="线体名称")
    @TableField("production_name")
    private String productionName;
    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    private String mark;
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


    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public Integer getWorkProductionId() {
        return workProductionId;
    }

    public void setWorkProductionId(Integer workProductionId) {
        this.workProductionId = workProductionId;
    }

    public String getProductionName() {
        return productionName;
    }

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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
        return this.workProductionId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfWorkProductionMap{" +
        ", workNo=" + workNo +
        ", workProductionId=" + workProductionId +
        ", productionName=" + productionName +
        ", mark=" + mark +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
