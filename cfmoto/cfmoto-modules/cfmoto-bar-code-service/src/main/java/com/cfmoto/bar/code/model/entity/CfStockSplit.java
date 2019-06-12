package com.cfmoto.bar.code.model.entity;

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
 * @since 2019-04-22
 */
@TableName("cf_stock_split")
@ApiModel(value="CfStockSplit",description="备料拆分")
@Data
public class CfStockSplit extends Model<CfStockSplit> {

    private static final long serialVersionUID = 1L;

    public static final String  EX_TOO_BIG_SPLIT_NUMBER= "拆分数量过大";

    public static final String  EX_TOO_BIG_SPLIT_UNIT= "拆分单位过大";

    public static final String  EX_DOUBLE= "该条码已经被拆分";

    public static final String  SPLIT_PARENT_ID_SQL="split_parent_id";

    public static final String  STOCK_LIST_NO_SQL="stock_list_no";

    public static final String  MATERIALS_NAME_SQL="materials_name";

    public static final String  MATERIALS_NO_SQL="materials_no";
    /**
     * 备料拆分主键
     */
    @ApiModelProperty(value="备料拆分主键")
    @TableId("split_id")
    private Integer splitId;
    /**
     * 备料单号
     */
    @ApiModelProperty(value="备料单号")
    @TableField("stock_list_no")
    private String stockListNo;
    /**
     * 物料代码
     */
    @ApiModelProperty(value="物料代码")
    @TableField("materials_no")
    private String materialsNo;

    /**
     * 物料名称
     */
    @ApiModelProperty(value="物料名称")
    @TableField("materials_name")
    private String materialsName;
    /**
     * 批次JSON数据
     */
    @ApiModelProperty(value="批次JSON数据")
    @TableField("batch_no_text")
    private String batchNoText;
    /**
     * 备料/拆分条码
     */
    @ApiModelProperty(value="备料/拆分条码")
    @TableField("split_no")
    private String splitNo;


    /**
     * 该物料的数据量
     */
    @ApiModelProperty(value="该物料的数据量")
    private BigDecimal number;

    /**
     * 是否已经被拆分
     */
    @ApiModelProperty(value="是否已经被拆分 0是未拆分，1是拆分")
    private Integer flag;
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
     * 父类节点
     */
    @ApiModelProperty(value="父类节点")
    @TableField("split_parent_id")
    private Integer splitParentId;

    /**
     * 规格型号
     */
    @ApiModelProperty(value="规格型号")
    private String mode;


    @Override
    protected Serializable pkVal() {
        return this.splitId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfStockSplit{" +
        ", splitId=" + splitId +
        ", stockListNo=" + stockListNo +
        ", materialsNo=" + materialsNo +
        ", batchNoText=" + batchNoText +
        ", splitNo=" + splitNo +
        ", flag=" + flag +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        ", splitParentId=" + splitParentId +
        "}";
    }
}
