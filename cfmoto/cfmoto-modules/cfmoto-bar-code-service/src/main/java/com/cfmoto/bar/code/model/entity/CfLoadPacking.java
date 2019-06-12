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
 *
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-02-26
 */
@TableName("cf_load_packing")
@ApiModel(value="CfLoadPacking",description="")
@Data
public class CfLoadPacking extends Model<CfLoadPacking> {

    private static final long serialVersionUID = 1L;
    public static final  String  CF_LOAD_PACKING_EX="系统异常请联系管理员";
    public static final  String  CF_DOUBLE_DELETE_EX="该物料已经删除，不可以再删除";
    public static final  String  CF_LOAD_NUMBER_EX="该物料已经存在装箱，不可以再删除";
    public static final  String  CF_TOO_LOAD_NUMBER_EX="物料数量不可以小于装箱数量";
    public static final  String  CF_MATERIAL_NO_SQL="material_no";
    public static final  String  CF_MATERIAL_NAME_SQL="material_name";
    public static final  String  CF_SALES_ORDER_NO_SQL="sales_order_no";
    public static final  String  CF_DOCUMENT_NO_SQL="document_no";
    public static final  String  CF_LOAD_NUMBER_SQL="load_number";
    /**
     * 主键编号
     */
    @ApiModelProperty(value="主键编号")
    @TableId(value = "load_packing_id", type = IdType.AUTO)
    private Integer loadPackingId;
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sales_order_no")
    @Excel(name = "销售订单" ,orderNum = "0")
    private String salesOrderNo;
    /**
     * 单据号
     */
    @ApiModelProperty(value="单据号")
    @TableField("document_no")
    @Excel(name = "单据号" ,orderNum = "1")
    private String documentNo;
    /**
     * 国家
     */
    @ApiModelProperty(value="国家")
    @Excel(name = "国家" ,orderNum = "2")
    private String country;
    /**
     * 车型
     */
    @ApiModelProperty(value="车型")
    @Excel(name = "车型" ,orderNum = "3")
    private String model;
    /**
     * 物料编码
     */
    @ApiModelProperty(value="物料编码")
    @TableField("material_no")
    @Excel(name = "物料编码" ,orderNum = "4")
    private String materialNo;
    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("material_name")
    @Excel(name = "物料名称" ,orderNum = "5")
    private String materialName;

    /**
     * 英文名称
     */
    @ApiModelProperty(value="英文名称")
    @TableField("english_name")
    @Excel(name = "英文名称" ,orderNum = "6")
    private String englishName;
    /**
     * 合同号
     */
    @ApiModelProperty(value="合同号")
    @TableField("contract_no")
    @Excel(name = "合同号" ,orderNum = "7")
    private String contractNo;
    /**
     * 物料数量
     */
    @ApiModelProperty(value="物料数量")
    @TableField("material_number")
    @Excel(name = "物料数量" ,orderNum = "8")
    private BigDecimal materialNumber;
    /**
     * 以装数量
     */
    @ApiModelProperty(value="以装数量")
    @TableField("load_number")
    @Excel(name = "以装数量" ,orderNum = "9")
    private BigDecimal loadNumber;
    /**
     * 已打印数据
     */
    @ApiModelProperty(value="已打印数据")
    @TableField("printing_number")
    @Excel(name = "已打印数据" ,orderNum = "10")
    private BigDecimal printingNumber;
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
        return this.loadPackingId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    public String toString() {
        return "CfLoadPacking{" +
                ", loadPackingId=" + loadPackingId +
                ", salesOrderNo=" + salesOrderNo +
                ", documentNo=" + documentNo +
                ", country=" + country +
                ", model=" + model +
                ", materialNo=" + materialNo +
                ", materialNumber=" + materialNumber +
                ", loadNumber=" + loadNumber +
                ", printingNumber=" + printingNumber +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
