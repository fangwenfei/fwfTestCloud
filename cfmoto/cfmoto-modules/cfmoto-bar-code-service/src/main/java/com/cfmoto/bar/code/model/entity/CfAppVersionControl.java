package com.cfmoto.bar.code.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * APP版本控制实体类
 *
 * @author yezi
 * @date 2019/5/29
 */
@Data
@TableName("cf_app_version_control")
@ApiModel(value = "CfAllotInfo", description = "调拨信息表")
public class CfAppVersionControl implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "app_version_id", type = IdType.AUTO)
    private Integer appVersionId;

    /**
     * app版本号
     */
    @ApiModelProperty(value = "app版本号")
    @TableField(value = "app_version_no")
    private String appVersionNo;

    /**
     * app下载链接
     */
    @ApiModelProperty(value = "app下载链接")
    @TableField(value = "app_download_link")
    private String appDownloadLink;

    /**
     * 是否为最新版本的app(0:否 1:是)
     */
    @ApiModelProperty(value = "是否为最新版本的app(0:否 1:是)")
    @TableField(value = "is_up_to_date")
    private Byte isUpToDate;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    /**
     * 版本新增内容日志
     */
    @ApiModelProperty(value = "版本新增内容日志")
    @TableField(value = "new_content_log")
    private String newContentLog;

    /**
     * 版本修改内容日志
     */
    @ApiModelProperty(value = "版本修改内容日志")
    @TableField(value = "fixed_content_log")
    private String fixedContentLog;

    /**
     * 最新APP版本下载链接
     */
    @ApiModelProperty(value = "最新APP版本下载链接")
    @TableField(exist = false)
    private String latestDownloadLink;

    /**
     * 数据创建人
     */
    @ApiModelProperty(value = "数据创建人")
    @TableField(value = "created_by")
    private Integer createdBy;

    /**
     * 数据创建时间
     */
    @ApiModelProperty(value = "数据创建时间")
    @TableField(value = "creation_date")
    private Date creationDate;

    /**
     * 数据最后修改人
     */
    @ApiModelProperty(value = "数据最后修改人")
    @TableField(value = "last_updated_by")
    private Integer lastUpdatedBy;

    /**
     * 数据最后修改时间
     */
    @ApiModelProperty(value = "数据最后修改时间")
    @TableField(value = "last_update_date")
    private Date lastUpdateDate;

    public void setObjectBasicAttributes(int userId, Date date) {
        this.createdBy = userId;
        this.creationDate = date;
        this.lastUpdatedBy = userId;
        this.lastUpdateDate = date;
    }
}
