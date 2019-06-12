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
 * 备料退料
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-03-19
 */
@TableName("cf_stock_return_root")
@ApiModel(value="CfStockReturnRoot",description="备料退料根表")
@Data
public class CfStockReturnRoot extends Model<CfStockReturnRoot> {

    private static final long serialVersionUID = 1L;

    /**
     * 备料退料表主键
     */
    @ApiModelProperty(value="备料退料表主键")
    @TableId(value = "stock_root_id", type = IdType.AUTO)
    private Integer stockRootId;
    /**
     * 备料退料单号
     */
    @ApiModelProperty(value="备料退料单号")
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
    private BigDecimal number;
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
    @TableField("last_update_date")
    private Date lastUpdateDate;
    /**
     * 功能模块类型
     */
    @ApiModelProperty(value="功能模块类型")
    @TableField("stock_function_type")
    private String stockFunctionType;



    @Override
    protected Serializable pkVal() {
        return this.stockRootId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfStockReturnRoot{" +
        ", stockRootId=" + stockRootId +
        ", stockListNo=" + stockListNo +
        ", productLine=" + productLine +
        ", carModel=" + carModel +
        ", number=" + number +
        ", stockDate=" + stockDate +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        ", stockFunctionType=" + stockFunctionType +
        "}";
    }
}
