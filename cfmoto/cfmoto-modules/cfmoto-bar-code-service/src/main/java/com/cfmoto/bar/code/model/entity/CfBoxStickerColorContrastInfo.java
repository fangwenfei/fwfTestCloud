package com.cfmoto.bar.code.model.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 箱外贴颜色对照信息表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-24
 */
@TableName("cf_box_sticker_color_contrast_info")
@ApiModel(value = "CfBoxStickerColorContrastInfo", description = "箱外贴颜色对照信息表")
public class CfBoxStickerColorContrastInfo extends Model<CfBoxStickerColorContrastInfo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "cf_box_sticker_color_contrast_info_id", type = IdType.AUTO)
    private Integer cfBoxStickerColorContrastInfoId;

    @TableField("publicity_color")
    @Excel(name = "宣传颜色")
    private String publicityColor;
    @Excel(name = "合格证颜色", orderNum = "1")
    @TableField("ok_color")
    private String okColor;
    @Excel(name = "英文颜色", orderNum = "2")
    @TableField("english_color")
    private String englishColor;
    @TableField("created_by")
    private Integer createdBy;
    @TableField("creation_date")
    private Date creationDate;
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    @TableField("last_update_date")
    private Date lastUpdateDate;
    /**
     * 导入时间
     */
    @ApiModelProperty(value = "导入时间")
    @TableField("import_time")
    private Date importTime;
    @TableField("import_user")
    private String importUser;


    public Date getImportTime() {
        return importTime;
    }

    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }

    public String getImportUser() {
        return importUser;
    }

    public void setImportUser(String importUser) {
        this.importUser = importUser;
    }

    public Integer getCfBoxStickerColorContrastInfoId() {
        return cfBoxStickerColorContrastInfoId;
    }

    public void setCfBoxStickerColorContrastInfoId(Integer cfBoxStickerColorContrastInfoId) {
        this.cfBoxStickerColorContrastInfoId = cfBoxStickerColorContrastInfoId;
    }

    public String getPublicityColor() {
        return publicityColor;
    }

    public void setPublicityColor(String publicityColor) {
        this.publicityColor = publicityColor;
    }

    public String getOkColor() {
        return okColor;
    }

    public void setOkColor(String okColor) {
        this.okColor = okColor;
    }

    public String getEnglishColor() {
        return englishColor;
    }

    public void setEnglishColor(String englishColor) {
        this.englishColor = englishColor;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Integer lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    protected Serializable pkVal() {
        return this.cfBoxStickerColorContrastInfoId;
    }

    public void setObjectSetBasicAttribute(int userId, Date date) {
        this.createdBy = userId;
        this.lastUpdatedBy = userId;
        this.lastUpdateDate = date;
        this.creationDate = date;
    }

    public void setObjectSetBasicAttributeForUpdate(int userId, Date date) {
        this.lastUpdatedBy = userId;
        this.lastUpdateDate = date;
    }

    @Override
    public String toString() {
        return "CfBoxStickerColorContrastInfo{" +
                ", cfBoxStickerColorContrastInfoId=" + cfBoxStickerColorContrastInfoId +
                ", publicityColor=" + publicityColor +
                ", okColor=" + okColor +
                ", englishColor=" + englishColor +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
