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

/**
 * <p>
 * 物料条码和货位条码绑定
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-01-24
 */
@TableName("cf_location_binding")
@ApiModel(value="CfLocationBinding",description="物料条码和货位条码绑定")
public class CfLocationBinding extends Model<CfLocationBinding> {

    private static final long serialVersionUID = 1L;

    public static final String CF_LOCATION_BINDING_SQL_ADD = "物料条码和货位条码绑定数据插入失败，请联系管理员";

    public static final String CF_BARCODE_NOT_NULL = "请输入物料条码";

    public static final String CF_BARCODE_NOT_HAVING = "条码库存里没有该条码";

    public static final String CF_BARCODE_SAP_ERROR = "SAP有异常数据处理错误";

    public static final String CF_WAREHOUSE_ERROR = "条码仓库与目标仓库不匹配，请注意";

    public static final String CF_WAREHOUSE_CHANGE_ERROR = "货位转换必须是同一仓库，请注意";

    public static final String CF_BARCODE_PARAMS_ERROR = "数据格式或者参数有问题";

    public static final String CF_FACTORY_ERROR = "该条码工厂有问题";

    public static final String CF_WAREHOUSE_POSITION_ERROR = "目标仓位和原有仓位不可以相同";

    public static final String CF_BARCODE_TYPE_ADD_SHELVES = "0";  //0，上架；

    public static final String CF_BARCODE_TYPE_CHANGE_LOCATION = "1"; //1，仓位转换
    /**
     * 货物绑定主键
     */
    @ApiModelProperty(value="货物绑定主键")
    @TableId(value = "location_binding_id", type = IdType.AUTO)
    private Integer locationBindingId;
    /**
     * 物料条码
     */
    @ApiModelProperty(value="物料条码")
    @TableField("material_barcode")
    private String materialBarcode;
    /**
     * 货位条码
     */
    @ApiModelProperty(value="货位条码")
    @TableField("ocation_barcode")
    private String ocationBarcode;
    /**
     * 数据版本号
     */
    @ApiModelProperty(value="数据版本号")
    @TableField("object_version_number")
    private Integer objectVersionNumber;
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

    public void setObjectSetBasicAttribute(int userId,Date date){
        this.createdBy=userId;
        this.lastUpdatedBy=userId;
        this.lastUpdateDate=date;
        this.creationDate=date;
    }
    public Integer getLocationBindingId() {
        return locationBindingId;
    }

    public void setLocationBindingId(Integer locationBindingId) {
        this.locationBindingId = locationBindingId;
    }

    public String getMaterialBarcode() {
        return materialBarcode;
    }

    public void setMaterialBarcode(String materialBarcode) {
        this.materialBarcode = materialBarcode;
    }

    public String getOcationBarcode() {
        return ocationBarcode;
    }

    public void setOcationBarcode(String ocationBarcode) {
        this.ocationBarcode = ocationBarcode;
    }

    public Integer getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Integer objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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
        return this.locationBindingId;
    }

    @Override
    public String toString() {
        return "CfLocationBinding{" +
        ", locationBindingId=" + locationBindingId +
        ", materialBarcode=" + materialBarcode +
        ", ocationBarcode=" + ocationBarcode +
        ", objectVersionNumber=" + objectVersionNumber +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
