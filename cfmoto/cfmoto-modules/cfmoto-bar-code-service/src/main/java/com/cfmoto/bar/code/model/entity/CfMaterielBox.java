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
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-02-18
 */
@TableName("cf_materiel_box")
@ApiModel(value="CfMaterielBox",description="")
@Data
public class CfMaterielBox extends Model<CfMaterielBox> {

    private static final long serialVersionUID = 1L;
    public static final String CF_MATERIEL_BOX_SQL_ADD = "物料装箱插入失败，请联系管理员";
    public static final String CF_MATERIEL_BOX_GET_DATA = "获取数据，请联系管理员";
    public static final String CF_MATERIEL_BOX_UNBIND_DATA = "解箱/解托失败，请联系管理员";
    //1是箱子，2是物料,3是托，4是柜
    public static final int CF_TYPE_1=1;//是箱子
    public static final int CF_TYPE_2=2;//是物料
    public static final int CF_TYPE_3=3;//是托
    public static final int CF_TYPE_4=4;//是柜
    public static final String MATERIEL_BOX_NO_HEADER="materielBoxNoHeader";//前缀 wl xh th gh
    public static final String MATERIEL_BOX_NO_HEADER_WL="WL";//物料
    public static final String MATERIEL_BOX_NO_HEADER_XH="XH";//箱盒
    public static final String MATERIEL_BOX_NO_HEADER_TH="TH";//装托
    public static final String MATERIEL_BOX_NO_HEADER_GH="GH";//装托
    /**
     * sql条件条码
     */
    public static final String SQL_BAR_CODE_NO="bar_code_no";

    public static final String SQL_TYPE="type";
    /**
     * 条码
     */
    @ApiModelProperty(value="条码")
    @TableField("bar_code_no")
    private String barCodeNo;
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sales_order")
    private String salesOrder;
    /**
     * 单据号
     */
    @ApiModelProperty(value="单据号")
    @TableField("document_no")
    private String documentNo;
    /**
     * 规格
     */
    @ApiModelProperty(value="规格")
    private String model;
    /**
     * 毛重
     */
    @ApiModelProperty(value="毛重")
    private BigDecimal weight;
    /**
     * 装箱时间
     */
    @ApiModelProperty(value="装箱时间")
    @TableField("boxing_time")
    private Date boxingTime;
    /**
     * 装箱人
     */
    @ApiModelProperty(value="装箱人")
    @TableField("boxing_user")
    private Integer boxingUser;
    /**
     * 类型（1是箱子，2是物料）
     */
    @ApiModelProperty(value="类型（1是箱子，2是物料）")
    private Integer type;
    /**
     * 父类条码号
     */
    @ApiModelProperty(value="父类条码号")
    @TableField("parent_no")
    private String parentNo;
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
    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    @TableId(value = "bar_code_id", type = IdType.AUTO)
    private Integer barCodeId;


    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    @TableField("material_no")
    private String materialNo;

    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("material_name")
    private String materialName;

    /**
     * 英文名称
     */
    @ApiModelProperty(value="英文名称")
    @TableField("english_name")
    private String englishName;


    /**
     * 数量
     */
    @ApiModelProperty(value="数量")
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 车型
     */
    @ApiModelProperty(value="车型")
    @TableField("car_model")
    private String carModel;

    /**
     * 合同号
     */
    @ApiModelProperty(value="合同号")
    @TableField("contract_no")
    private String contractNo;

    /**
     * 国家
     */
    @ApiModelProperty(value="国家")
    private String country;

    @Override
    protected Serializable pkVal() {
        return this.barCodeId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfMaterielBox{" +
        ", barCodeNo=" + barCodeNo +
        ", salesOrder=" + salesOrder +
        ", documentNo=" + documentNo +
        ", model=" + model +
        ", weight=" + weight +
        ", boxingTime=" + boxingTime +
        ", boxingUser=" + boxingUser +
        ", type=" + type +
        ", parentNo=" + parentNo +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        ", barCodeId=" + barCodeId +
        "}";
    }
}
