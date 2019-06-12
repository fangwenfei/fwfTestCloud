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
 * 模板功能表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-08
 */
@TableName("cf_print_function")
@ApiModel(value="CfPrintFunction",description="模板功能表")
public class CfPrintFunction extends Model<CfPrintFunction> {

    private static final long serialVersionUID = 1L;

    public static final String CF_PRINT_FUNCTION_SUCCESS = "数据操作成功";

    public static final String CF_PRINT_FUNCTION_FAIL = "数据操作失败，请联系管理员";

    /**
     * 功能主键
     */
    @ApiModelProperty(value="功能主键")
    @TableId(value = "function_id", type = IdType.AUTO)
    private Integer functionId;
    /**
     * 打印功能名称
     */
    @ApiModelProperty(value="打印功能名称")
    @TableField("function_name")
    private String functionName;
    /**
     * 打印功能描述
     */
    @ApiModelProperty(value="打印功能描述")
    @TableField("function_depiction")
    private String functionDepiction;
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


    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionDepiction() {
        return functionDepiction;
    }

    public void setFunctionDepiction(String functionDepiction) {
        this.functionDepiction = functionDepiction;
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
        return this.functionId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfPrintFunction{" +
        ", functionId=" + functionId +
        ", functionName=" + functionName +
        ", functionDepiction=" + functionDepiction +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
