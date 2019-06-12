package com.cfmoto.bar.code.model.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
import lombok.Data;

/**
 * <p>
 * 总成方案数据表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-02-15
 */
@TableName("cf_assembly_plan")
@ApiModel(value="CfAssemblyPlan",description="总成方案数据表")
@Data
public class CfAssemblyPlan extends Model<CfAssemblyPlan> {

    private static final long serialVersionUID = 1L;

    public static final  String  CF_ASSEMBLY_PLAN_SQL_ADD="总成方案数据表插入有问题，请联系管理员";

    public static final  String  CF_ASSEMBLY_PLAN_EX="系统异常请联系管理员";

    public static final  String  CF_COUNTRY_NULL="请选择国家";
    public static final  String  CF_MODEL_NULL="请选择车型";
    public static final  String  CF_MATERIAL_NO_NULL="请选择物料代码";
    /**
     * 总成方案主键
     */
    @ApiModelProperty(value="总成方案主键")
    @TableId(value = "assembly_id", type = IdType.AUTO)
    private Integer assemblyId;

    /**
     * 国家
     */
    @ApiModelProperty(value="国家")
    @Excel(name = "国家" ,orderNum = "0")
    private String country;
    /**
     * 车型
     */
    @ApiModelProperty(value="车型")
    @Excel(name = "车型" ,orderNum = "1")
    private String model;

    /**
     * 物料编码
     */
    @ApiModelProperty(value="物料编码")
    @Excel(name = "物料编码" ,orderNum = "2")
    @TableField("material_no")
    private String materialNo;

    /**
     * 总成物料
     */
    @ApiModelProperty(value="总成物料")
    @TableField("assembly_materials")
    @Excel(name = "总成物料" ,orderNum = "3")
    private String assemblyMaterials;

    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("material_name")
    @Excel(name = "物料名称" ,orderNum = "4")
    private String materialName;

    /**
     * 英文名称
     */
    @ApiModelProperty(value="英文名称")
    @TableField("english_name")
    @Excel(name = "英文名称" ,orderNum = "5")
    private String englishName;

    /**
     * 总成方案主键
     */
    @ApiModelProperty(value="总成数量")
    @TableField(value = "assembly_number")
    @Excel(name = "总成数量" ,orderNum = "6")
    private Integer assemblyNumber;
    /**
     * 子阶物料号
     */
    @ApiModelProperty(value="子阶物料号")
    @TableField("son_material")
    @Excel(name = "子阶物料号" ,orderNum = "7")
    private String sonMaterial;
    /**
     * 子阶物料数量
     */
    @ApiModelProperty(value="子阶物料数量")
    @TableField("son_material_number")
    @Excel(name = "子阶物料数量" ,orderNum = "8")
    private BigDecimal sonMaterialNumber;
    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    @Excel(name = "备注" ,orderNum = "9")
    private String remarks;



    /**
     * 导入时间
     */
    @ApiModelProperty(value="导入时间")
    @TableField("import_time")
    private Date importTime;
    @TableField("import_user")
    private String importUser;
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
        return this.assemblyId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfAssemblyPlan{" +
        ", assemblyId=" + assemblyId +
        ", assemblyNumber=" + assemblyNumber +
        ", country=" + country +
        ", model=" + model +
        ", assemblyMaterials=" + assemblyMaterials +
        ", sonMaterial=" + sonMaterial +
        ", sonMaterialNumber=" + sonMaterialNumber +
        ", remarks=" + remarks +
        ", importTime=" + importTime +
        ", importUser=" + importUser +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
