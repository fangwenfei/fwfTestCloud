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
 * 生产超领根表
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-18
 */
@TableName("cf_stock_too_receive_root")
@ApiModel(value="CfStockTooReceiveRoot",description="生产超领根表")
@Data
public class CfStockTooReceiveRoot extends Model<CfStockTooReceiveRoot> {

    private static final long serialVersionUID = 1L;

    /**
     * 备料单信息表主键
     */
    @ApiModelProperty(value="备料单信息表主键")
    @TableId(value = "stock_root_id", type = IdType.AUTO)
    private Integer stockRootId;
    /**
     * 备料单号
     */
    @ApiModelProperty(value="备料单号")
    @TableField("stock_list_no")
    private String stockListNo;
    /**
     * 生产线
     */
    @ApiModelProperty(value="生产线")
    @TableField("product_line")
    private String productLine;
    /**
     * 车型号
     */
    @ApiModelProperty(value="车型号")
    @TableField("car_model")
    private String carModel;
    /**
     * 数量
     */
    @ApiModelProperty(value="数量")
    private Integer number;
    /**
     * 备料日期
     */
    @ApiModelProperty(value="备料日期")
    @TableField("stock_date")
    private Date stockDate;
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
     * 最后修改人
     */
    @ApiModelProperty(value="最后修改人")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后修改时间
     */
    @ApiModelProperty(value="最后修改时间")
    @TableField("last_updated_date")
    private Date lastUpdatedDate;
    /**
     * 功能模块类型（TOORECEIVE：生产超领，）
     */
    @ApiModelProperty(value="功能模块类型（TOORECEIVE：生产超领，）")
    @TableField("stock_function_type")
    private String stockFunctionType;

    /**
     * 功能模块类型（TOORECEIVE：生产超领，）
     */
    @ApiModelProperty(value="功能模块类型（合并备料：10，生产领料:20,退料: 30,超领 :40 ）")
    @TableField("stock_function_type_name")
    private String stockFunctionTypeName;

    /**
     * 备料仓库
     */
    @ApiModelProperty(value = "备料仓库")
    @TableField("stock_repository")
    private String stockRepository;




    @Override
    protected Serializable pkVal() {
        return this.stockRootId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdatedDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfStockTooReceiveRoot{" +
        ", stockRootId=" + stockRootId +
        ", stockListNo=" + stockListNo +
        ", productLine=" + productLine +
        ", carModel=" + carModel +
        ", number=" + number +
        ", stockDate=" + stockDate +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdatedDate=" + lastUpdatedDate +
        ", stockFunctionType=" + stockFunctionType +
        "}";
    }
}
