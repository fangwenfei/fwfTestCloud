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
import lombok.Data;

/**
 * <p>
 * 仓库
 * </p>
 *
 * @author  space
 * @since 2019-03-05
 */
@TableName("cf_storage_location")
@ApiModel(value="CfStorageLocation",description="仓库")
@Data
public class CfStorageLocation extends Model<CfStorageLocation> {

    private static final long serialVersionUID = 1L;

    public static final String CF_BARCODE_WAREHOUSE_SQL = "warehouse";//仓库编码

    public static final String CF_BARCODE_WAREHOUSE_DESCRIPTION_SQL = "warehouse_description";//仓库名称

    public static final String CF_AND_SQL = ",";//并

    public static final String CF_SITE_SQL = "site";//物料编码

    public static final String  EX_WAREHOUSE_DOUBLE = "数据重复插入。请仔细检查";

    public static final String  EX_WAREHOUSE_UNDEFINED = "数据插入失败。请仔细检查";
    /**
     * 主键自增
     */
    @ApiModelProperty(value="主键自增")
    @TableId(value = "storage_location_id", type = IdType.AUTO)
    private Integer storageLocationId;
    /**
     * 工厂
     */
    @ApiModelProperty(value="工厂")
    @Excel(name = "工厂" ,orderNum = "0")
    private String site;

    /**
     * 仓库
     */
    @ApiModelProperty(value="仓库")
    @TableField("warehouse")
    @Excel(name = "仓库" ,orderNum = "1",width = 30.0D)
    private String wareHouse;

    /**
     * 库存地点描述
     */
    @ApiModelProperty(value="库存地点描述")
    @TableField("warehouse_description")
    @Excel(name = "库存地点描述" ,orderNum = "2",width = 40.0D)
    private String wareHouseDeScription;

    /**D
     * 存储区域
     */
    @ApiModelProperty(value="存储区域")
    @TableField("storage_area")
    @Excel(name = "存储区域" ,orderNum = "3",width = 30.0D)
    private String storageArea;
    /**
     * 线别
     */
    @ApiModelProperty(value="线别")
    @TableField("work_center")
    private String workCenter;
    /**
     * 仓位
     */
    @ApiModelProperty(value="仓位")
    @TableField("warehouse_position")
    private String warehousePosition;
    /**
     * 描述
     */
    @ApiModelProperty(value="描述")
    @Excel(name = "描述" ,orderNum = "4",width = 40.0D)
    private String description;
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




    @Override
    protected Serializable pkVal() {
        return this.storageLocationId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfStorageLocation{" +
        ", storageLocationId=" + storageLocationId +
        ", site=" + site +
        ", workCenter=" + workCenter +
        ", warehousePosition=" + warehousePosition +
        ", description=" + description +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
