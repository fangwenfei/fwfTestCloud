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
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-08
 */
@TableName("cf_print_lodop_template")
@ApiModel(value="CfPrintLodopTemplate",description="")
@Data
public class CfPrintLodopTemplate extends Model<CfPrintLodopTemplate> {

    private static final long serialVersionUID = 1L;

    public static final String  FALSE_SQL = "false";

    public static final String  TRUE_SQL = "true";

    public static final String  FUNCTION_ID_SQL = "function_id";

    public static final String  FUNCTION_NAME_SQL = "functionName";

    /**
     * 模板主键
     */
    @ApiModelProperty(value="模板主键")
    @TableId(value = "print_lodop_id", type = IdType.AUTO)
    private Integer printLodopId;
    /**
     * 功能主键
     */
    @ApiModelProperty(value="功能主键")
    @TableField("function_id")
    private Integer functionId;


    /**
     * 是否默认
     */
    @ApiModelProperty(value="是否默认")
    @TableField("cf_check")
    private String cfCheck;

    /**
     * 模板内容
     */
    @ApiModelProperty(value="模板内容")
    @TableField("print_lodop_template")
    private String printLodopTemplate;
    /**
     * 模板名称
     */
    @ApiModelProperty(value="模板名称")
    @TableField("print_lodop_template_name")
    private String printLodopTemplateName;
    /**
     * 模板描述
     */
    @ApiModelProperty(value="模板描述")
    @TableField("print_lodop_template_depiction")
    private String printLodopTemplateDepiction;
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



    @Override
    protected Serializable pkVal() {
        return this.printLodopId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfPrintLodopTemplate{" +
        ", printLodopId=" + printLodopId +
        ", functionId=" + functionId +
        ", printLodopTemplate=" + printLodopTemplate +
        ", printLodopTemplateName=" + printLodopTemplateName +
        ", printLodopTemplateDepiction=" + printLodopTemplateDepiction +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
