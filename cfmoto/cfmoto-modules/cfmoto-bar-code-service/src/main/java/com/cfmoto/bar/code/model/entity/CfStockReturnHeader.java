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
 * @since 2019-03-19
 */
@TableName("cf_stock_return_header")
@ApiModel(value="CfStockReturnHeader",description="备料退料头表")
@Data
public class CfStockReturnHeader extends Model<CfStockReturnHeader> {

    private static final long serialVersionUID = 1L;

    /**
     * 备料退料清单表主键
     */
    @ApiModelProperty(value="备料退料清单表主键")
    @TableId(value = "stock_header_id", type = IdType.AUTO)
    private Integer stockHeaderId;
    /**
     * 备料退料根表表主键
     */
    @ApiModelProperty(value="备料退料根表表主键")
    @TableField("stock_root_id")
    private Integer stockRootId;
    /**
     * 备料退料单号
     */
    @ApiModelProperty(value="备料退料单号")
    @TableField("stock_list_no")
    private String stockListNo;
    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("materials_name")
    private String materialsName;
    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    @TableField("materials_no")
    private String materialsNo;
    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String spec;
    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    private String repository;
    /**
     * 存储区域
     */
    @ApiModelProperty(value="存储区域")
    @TableField("storage_area")
    private String storageArea;
    /**
     * 应发数量
     */
    @ApiModelProperty(value="应发数量")
    @TableField("should_send_number")
    private BigDecimal shouldSendNumber;
    /**
     * 实发数量
     */
    @ApiModelProperty(value="实发数量")
    @TableField("actual_send_number")
    private BigDecimal actualSendNumber;
    /**
     * 备料交接数量
     */
    @ApiModelProperty(value="备料交接数量")
    @TableField("stock_handover_number")
    private BigDecimal stockHandoverNumber;
    /**
     * 领料交接数量
     */
    @ApiModelProperty(value="领料交接数量")
    @TableField("picked_handover_number")
    private BigDecimal pickedHandoverNumber;
    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    private String remark;
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




    @Override
    protected Serializable pkVal() {
        return this.stockHeaderId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfStockReturnHeader{" +
        ", stockHeaderId=" + stockHeaderId +
        ", stockRootId=" + stockRootId +
        ", stockListNo=" + stockListNo +
        ", materialsName=" + materialsName +
        ", materialsNo=" + materialsNo +
        ", spec=" + spec +
        ", repository=" + repository +
        ", storageArea=" + storageArea +
        ", shouldSendNumber=" + shouldSendNumber +
        ", actualSendNumber=" + actualSendNumber +
        ", stockHandoverNumber=" + stockHandoverNumber +
        ", pickedHandoverNumber=" + pickedHandoverNumber +
        ", remark=" + remark +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
