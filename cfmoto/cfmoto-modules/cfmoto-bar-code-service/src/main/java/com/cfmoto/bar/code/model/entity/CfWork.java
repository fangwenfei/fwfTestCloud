package com.cfmoto.bar.code.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-18
 */
@TableName("cf_work")
@Data
@ApiModel(value=" CfWork",description="工序对象")
public class CfWork extends Model<CfWork> {

    private static final long serialVersionUID = 1L;

    public static final String FAIL_SQL_UNIQUE_CF_WORK="该确认报工进库失败";

    public static final String  CF_WORK_SQL_ADD="确认报工进库数据插入有问题，请联系管理员";

    @TableId("work_id")
    @ApiModelProperty(value="工序主键(不必填)",example="1")
    private Integer workId;
    /**
     * 工序编码
     */
    @TableField("process_no")
    @ApiModelProperty(value="工序编码")
    private String processNo;
    /**
     * 生产任务单
     */
    @TableField("production_task")
    @ApiModelProperty(value="生产任务单")
    private String productionTask;
    /**
     * 工序日期
     */
    @TableField("process_date")
    @ApiModelProperty(value="工序日期")
    private Date processDate;
    /**
     * 数据版本号
     */
    @TableField("object_version_number")
    private Integer objectVersionNumber;
    /**
     * 数据创建人
     */
    @TableField("created_by")
    private Integer createdBy;
    /**
     * 数据创建时间
     */
    @TableField("creation_date")
    private Date creationDate;
    /**
     * 最后更改人
     */
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后更改时间
     */
    @TableField("last_update_date")
    private Date lastUpdateDate;
    /**
     * 报工数量
     */
    @TableField("work_number")
    private Integer workNumber;
    /**
     * 报废数量
     */
    @TableField("scrap_number")
    @ApiModelProperty(value="报废数量")
    private Integer scrapNumber;
    /**
     * 确认码
     */
    @TableField("confirm_number")
    @ApiModelProperty(value="确认码")
    private Integer confirmNumber;


    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }

    @Override
    protected Serializable pkVal() {
        return this.workId;
    }

    @Override
    public String toString() {
        return "CfWork{" +
        ", workId=" + workId +
        ", processNo=" + processNo +
        ", productionTask=" + productionTask +
        ", processDate=" + processDate +
        ", objectVersionNumber=" + objectVersionNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        ", workNumber=" + workNumber +
        ", scrapNumber=" + scrapNumber +
        ", confirmNumber=" + confirmNumber +
        "}";
    }
}
