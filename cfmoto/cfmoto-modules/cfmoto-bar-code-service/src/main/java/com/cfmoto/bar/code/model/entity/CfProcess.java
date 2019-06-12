package com.cfmoto.bar.code.model.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
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
@Data
@TableName("cf_process")
public class CfProcess extends Model<CfProcess> {

    private static final long serialVersionUID = 1L;

    /**
     * 工序主键
     */
    @TableId(value = "process_id", type = IdType.AUTO)
    private Integer processId;
    /**
     * 工序编码
     */
    @TableField("process_no")
    private String processNo;
    /**
     * 工序名称
     */
    @TableField("process_name")
    private String processName;
    /**
     * 生产任务单
     */
    @TableField("production_task")
    private String productionTask;
    /**
     * 工序日期
     */
    @TableField("process_date")
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

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    @Override
    protected Serializable pkVal() {
        return this.processId;
    }

    @Override
    public String toString() {
        return "CfProcess{" +
        ", processId=" + processId +
        ", processNo=" + processNo +
        ", processName=" + processName +
        ", productionTask=" + productionTask +
        ", processDate=" + processDate +
        ", objectVersionNumber=" + objectVersionNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
