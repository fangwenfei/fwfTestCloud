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
 * 柜子信息
 * </p>
 *
 * @author  FangWenFei
 * @since 2019-02-20
 */
@TableName("cf_cabinet")
@ApiModel(value="CfCabinet",description="柜子信息")
public class CfCabinet extends Model<CfCabinet> {

    private static final long serialVersionUID = 1L;
    public static final String CF_CABINET_SQL_ADD = "柜子插入失败，请联系管理员";
    public static final String CF_CABINET_GET_DATA = "获取数据，请联系管理员";
    public static final String CABINET_NO_HEADER_GH="GH";//装柜
    public static final String  SEND_GOODS_NO_NOT_NULL = "发货通知单不可为空";
    /**
     * 柜子的主键
     */
    @ApiModelProperty(value="柜子的主键")
    @TableId(value = "cabinet_id", type = IdType.AUTO)
    private Integer cabinetId;
    /**
     * 柜子的编码
     */
    @ApiModelProperty(value="柜子的编码")
    @TableField("cabinet_no")
    private String cabinetNo;
    /**
     * 发货通知单
     */
    @ApiModelProperty(value="发货通知单")
    @TableField("send_goods_no")
    private String sendGoodsNo;
    /**
     * 销售订单
     */
    @ApiModelProperty(value="销售订单")
    @TableField("sales_order")
    private String salesOrder;
    /**
     * 实物柜号
     */
    @ApiModelProperty(value="实物柜号")
    @TableField("real_cabinet_no")
    private String realCabinetNo;
    /**
     * 货柜铅封号
     */
    @ApiModelProperty(value="货柜铅封号")
    @TableField("container_seal_no")
    private String containerSealNo;
    /**
     * 箱子的条码
     */
    @ApiModelProperty(value="箱子的条码")
    @TableField("bar_code_no")
    private String barCodeNo;
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


    public Integer getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Integer cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getCabinetNo() {
        return cabinetNo;
    }

    public void setCabinetNo(String cabinetNo) {
        this.cabinetNo = cabinetNo;
    }

    public String getSendGoodsNo() {
        return sendGoodsNo;
    }

    public void setSendGoodsNo(String sendGoodsNo) {
        this.sendGoodsNo = sendGoodsNo;
    }

    public String getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(String salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getRealCabinetNo() {
        return realCabinetNo;
    }

    public void setRealCabinetNo(String realCabinetNo) {
        this.realCabinetNo = realCabinetNo;
    }

    public String getContainerSealNo() {
        return containerSealNo;
    }

    public void setContainerSealNo(String containerSealNo) {
        this.containerSealNo = containerSealNo;
    }

    public String getBarCodeNo() {
        return barCodeNo;
    }

    public void setBarCodeNo(String barCodeNo) {
        this.barCodeNo = barCodeNo;
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
        return this.cabinetId;
    }

    public void setObjectSetBasicAttribute(int userId,Date date){
            this.createdBy=userId;
            this.lastUpdatedBy=userId;
            this.lastUpdateDate=date;
            this.creationDate=date;
            }
    @Override
    public String toString() {
        return "CfCabinet{" +
        ", cabinetId=" + cabinetId +
        ", cabinetNo=" + cabinetNo +
        ", sendGoodsNo=" + sendGoodsNo +
        ", salesOrder=" + salesOrder +
        ", realCabinetNo=" + realCabinetNo +
        ", containerSealNo=" + containerSealNo +
        ", barCodeNo=" + barCodeNo +
        ", createdBy=" + createdBy +
        ", creationDate=" + creationDate +
        ", lastUpdatedBy=" + lastUpdatedBy +
        ", lastUpdateDate=" + lastUpdateDate +
        "}";
    }
}
