package com.cfmoto.bar.code.model.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 模拟通过sap获取生产任务单信息
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-02-27
 */
@TableName("sap_job_order_temp")
@ApiModel(value="SapJobOrderTemp",description="模拟通过sap获取生产任务单信息")
@Data
public class SapJobOrderTemp extends Model<SapJobOrderTemp> {

    private static final long serialVersionUID = 1L;

    public static final String  SALES_ORDER_NOT_NULL = "销售订单不可为空";
    public static final String  SQL_SALES_ORDER_STR= "sales_order";

    public static final String  SQL_JOB_ORDER_NO_STR= "job_order_no";
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sales_order")
    private String salesOrder;
    /**
     * 生产任务单
     */
    @ApiModelProperty(value="生产任务单")
    @TableField("job_order_no")
    private String jobOrderNo;
    /**
     * 国家
     */
    @ApiModelProperty(value="国家")
    private String country;
    /**
     * 车型
     */
    @ApiModelProperty(value="车型")
    private String model;
    /**
     * 物料编码
     */
    @ApiModelProperty(value="物料编码")
    @TableField("material_no")
    private String materialNo;
    /**
     * 物料描述
     */
    @ApiModelProperty(value="物料描述")
    @TableField("material_disc")
    private String materialDisc;
    /**
     * 生成任务单数量
     */
    @ApiModelProperty(value="生成任务单数量")
    @TableField("job_order_number")
    private BigDecimal jobOrderNumber;


    /**
     * 合同号
     */
    @ApiModelProperty(value="合同号")
    @TableField("contract_no")
    private String contractNo;



    @Override
    protected Serializable pkVal() {
        return null;
    }


    @Override
    public String toString() {
        return "SapJobOrderTemp{" +
        ", salesOrder=" + salesOrder +
        ", jobOrderNo=" + jobOrderNo +
        ", country=" + country +
        ", model=" + model +
        ", materialNo=" + materialNo +
        ", materialDisc=" + materialDisc +
        ", jobOrderNumber=" + jobOrderNumber +
        "}";
    }
}
