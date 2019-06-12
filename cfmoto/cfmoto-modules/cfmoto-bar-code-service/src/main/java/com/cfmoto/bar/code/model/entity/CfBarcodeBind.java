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
 * 三码绑定
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-25
 */
@TableName("cf_barcode_bind")
@ApiModel(value = "CfBarcodeBind", description = "三码绑定")
public class CfBarcodeBind extends Model<CfBarcodeBind> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "barcode_bind_id", type = IdType.AUTO)
    private Integer barcodeBindId;
    /**
     * 整车条码
     */
    @ApiModelProperty(value = "整车条码")
    @Excel(name = "整车条码", orderNum = "3")
    private String car;
    /**
     * 车架条码
     */
    @ApiModelProperty(value = "车架条码")
    @Excel(name = "车架号", orderNum = "4")
    private String frame;
    /**
     * 发动机条码
     */
    @ApiModelProperty(value = "发动机条码")
    @Excel(name = "发动机号", orderNum = "5")
    private String engine;
    /**
     * 整车条码对应的生产订单
     */
    @ApiModelProperty(value = "整车条码对应的生产订单")
    @TableField("production_order")
    @Excel(name = "生产订单", orderNum = "6")
    private String productionOrder;
    /**
     * 绑定状态:1绑定，0解绑？
     */
    @ApiModelProperty(value = "绑定状态:1绑定，0解绑？")
    private Integer status;
    /**
     * T-BOXcode
     */
    @ApiModelProperty(value = "T-BOXcode")
    @TableField("t_box_code")
    @Excel(name = "T-BOX", orderNum = "20")
    private String tBoxCode;
    /**
     * T-BOX绑定账号
     */
    @ApiModelProperty(value = "T-BOX绑定账号")
    @TableField("t_box_bind_by")
    private Integer tBoxBindBy;
    /**
     * T-BOX绑定日期
     */
    @ApiModelProperty(value = "T-BOX绑定日期")
    @TableField("t_box_bind_date")
    private Date tBoxBindDate;
    /**
     * 箱外贴打印日期
     */
    @ApiModelProperty(value = "箱外贴打印日期")
    @TableField("box_label_print_date")
    @Excel(name = "箱外贴打印日期", orderNum = "0", exportFormat = "yyyy-MM-dd hh:mm:ss")
    private Date boxLabelPrintDate;
    /**
     * 箱外贴打印方式
     */
    @ApiModelProperty(value = "箱外贴打印方式")
    @TableField("print_type")
    @Excel(name = "打印方式", orderNum = "1")
    private String printType;
    /**
     * 箱外贴车辆类型
     */
    @ApiModelProperty(value = "箱外贴车辆类型")
    @TableField("car_type")
    @Excel(name = "车辆类型", orderNum = "2")
    private String carType;
    /**
     * 箱外贴车型
     */
    @ApiModelProperty(value = "箱外贴车型")
    @TableField("car_model")
    @Excel(name = "车型", orderNum = "7")
    private String carModel;
    /**
     * 箱外贴物料代码
     */
    @ApiModelProperty(value = "箱外贴物料代码")
    @TableField("material_code")
    @Excel(name = "物料代码", orderNum = "8")
    private String materialCode;
    /**
     * 箱外贴美国名称(商品名称)
     */
    @ApiModelProperty(value = "箱外贴美国名称(商品名称)")
    @TableField("usa_name")
    @Excel(name = "商品名称", orderNum = "18")
    private String usaName;
    /**
     * 箱外贴生产日期
     */
    @ApiModelProperty(value = "箱外贴生产日期")
    @TableField("product_date")
    @Excel(name = "生产日期", orderNum = "9", exportFormat = "yyyy-MM-dd HHmmss")
    private Date productDate;
    /**
     * 箱外贴合格证颜色
     */
    @ApiModelProperty(value = "箱外贴合格证颜色")
    @TableField("ok_color")
    @Excel(name = "合格证颜色", orderNum = "10")
    private String okColor;
    /**
     * 箱外贴宣传颜色
     */
    @ApiModelProperty(value = "箱外贴宣传颜色")
    @TableField("publicity_color")
    @Excel(name = "宣传颜色", orderNum = "11")
    private String publicityColor;
    /**
     * 箱外贴英文颜色
     */
    @ApiModelProperty(value = "箱外贴英文颜色")
    @TableField("english_color")
    @Excel(name = "英文颜色", orderNum = "12")
    private String englishColor;
    /**
     * 箱外贴生产批次
     */
    @ApiModelProperty(value = "箱外贴生产批次")
    @TableField("product_lot")
    @Excel(name = "生产批号", orderNum = "15")
    private String productLot;
    /**
     * 箱外贴销售订单合同号
     */
    @ApiModelProperty(value = "箱外贴销售订单合同号")
    @TableField("contract_number")
    @Excel(name = "订单合同号", orderNum = "14")
    private String contractNumber;
    /**
     * 箱外贴车辆特殊配置
     */
    @ApiModelProperty(value = "箱外贴车辆特殊配置")
    @TableField("special_car_configuration")
    @Excel(name = "车辆特殊配置", orderNum = "13")
    private String specialCarConfiguration;
    /**
     * 箱外贴箱号
     */
    @ApiModelProperty(value = "箱外贴箱号")
    @TableField("box_code")
    @Excel(name = "箱号", orderNum = "16")
    private Integer boxCode;
    /**
     * 箱外贴美国州区
     */
    @ApiModelProperty(value = "箱外贴美国州区")
    @Excel(name = "州区", orderNum = "19")
    private String obviously;
    /**
     * 箱外贴备注
     */
    @ApiModelProperty(value = "箱外贴备注")
    @Excel(name = "备注", orderNum = "17")
    private String remark;
    /**
     * 绑定账号
     */
    @ApiModelProperty(value = "绑定账号")
    @TableField("created_by")
    private Integer createdBy;
    /**
     * 绑定日期
     */
    @ApiModelProperty(value = "绑定日期")
    @TableField("creation_date")
    private Date creationDate;
    /**
     * 最后更改人？
     */
    @ApiModelProperty(value = "最后更改人？")
    @TableField("last_updated_by")
    private Integer lastUpdatedBy;
    /**
     * 最后更改时间？
     */
    @ApiModelProperty(value = "最后更改时间？")
    @TableField("last_update_date")
    private Date lastUpdateDate;


    public Integer getBarcodeBindId() {
        return barcodeBindId;
    }

    public void setBarcodeBindId(Integer barcodeBindId) {
        this.barcodeBindId = barcodeBindId;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getProductionOrder() {
        return productionOrder;
    }

    public void setProductionOrder(String productionOrder) {
        this.productionOrder = productionOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String gettBoxCode() {
        return tBoxCode;
    }

    public void settBoxCode(String tBoxCode) {
        this.tBoxCode = tBoxCode;
    }

    public Integer gettBoxBindBy() {
        return tBoxBindBy;
    }

    public void settBoxBindBy(Integer tBoxBindBy) {
        this.tBoxBindBy = tBoxBindBy;
    }

    public Date gettBoxBindDate() {
        return tBoxBindDate;
    }

    public void settBoxBindDate(Date tBoxBindDate) {
        this.tBoxBindDate = tBoxBindDate;
    }

    public Date getBoxLabelPrintDate() {
        return boxLabelPrintDate;
    }

    public void setBoxLabelPrintDate(Date boxLabelPrintDate) {
        this.boxLabelPrintDate = boxLabelPrintDate;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getUsaName() {
        return usaName;
    }

    public void setUsaName(String usaName) {
        this.usaName = usaName;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public String getOkColor() {
        return okColor;
    }

    public void setOkColor(String okColor) {
        this.okColor = okColor;
    }

    public String getPublicityColor() {
        return publicityColor;
    }

    public void setPublicityColor(String publicityColor) {
        this.publicityColor = publicityColor;
    }

    public String getEnglishColor() {
        return englishColor;
    }

    public void setEnglishColor(String englishColor) {
        this.englishColor = englishColor;
    }

    public String getProductLot() {
        return productLot;
    }

    public void setProductLot(String productLot) {
        this.productLot = productLot;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getSpecialCarConfiguration() {
        return specialCarConfiguration;
    }

    public void setSpecialCarConfiguration(String specialCarConfiguration) {
        this.specialCarConfiguration = specialCarConfiguration;
    }

    public Integer getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(Integer boxCode) {
        this.boxCode = boxCode;
    }

    public String getObviously() {
        return obviously;
    }

    public void setObviously(String obviously) {
        this.obviously = obviously;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        return this.barcodeBindId;
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
        return "CfBarcodeBind{" +
                ", barcodeBindId=" + barcodeBindId +
                ", car=" + car +
                ", frame=" + frame +
                ", engine=" + engine +
                ", productionOrder=" + productionOrder +
                ", status=" + status +
                ", tBoxCode=" + tBoxCode +
                ", tBoxBindBy=" + tBoxBindBy +
                ", tBoxBindDate=" + tBoxBindDate +
                ", boxLabelPrintDate=" + boxLabelPrintDate +
                ", printType=" + printType +
                ", carType=" + carType +
                ", carModel=" + carModel +
                ", materialCode=" + materialCode +
                ", usaName=" + usaName +
                ", productDate=" + productDate +
                ", okColor=" + okColor +
                ", publicityColor=" + publicityColor +
                ", englishColor=" + englishColor +
                ", productLot=" + productLot +
                ", contractNumber=" + contractNumber +
                ", specialCarConfiguration=" + specialCarConfiguration +
                ", boxCode=" + boxCode +
                ", obviously=" + obviously +
                ", remark=" + remark +
                ", createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdateDate=" + lastUpdateDate +
                "}";
    }
}
