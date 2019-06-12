package com.cfmoto.bar.code.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value = "productionTaskVo生产任务单对象", description = "获取生产任务订单数据,发动机关联入库使用")
@Data
public class ProductionTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "生产订单", name = "taskNo")
    private String taskNo;
    @ApiModelProperty(value = "订单类型", name = "orderType")
    private String orderType;
    @ApiModelProperty(value = "物料编码", name = "item")
    private String item;
    @ApiModelProperty(value = "物料名称", name = "itemDesc")
    private String itemDesc;
    @ApiModelProperty(value = "任务单数量", name = "quantity")
    private BigDecimal quantity;
    @ApiModelProperty(value = "已入数量", name = "receivedQty")
    private BigDecimal receivedQty;
    @ApiModelProperty(value = "车型", name = "carType")
    private String carType;
    @ApiModelProperty(value = "产品规格", name = "mode")
    private String mode;
    @ApiModelProperty(value = "销售订单", name = "saleOrder")
    private String saleOrder;
    @ApiModelProperty(value = "销售订单行项目", name = "saleOrderRowItem")
    private String saleOrderRowItem;
    @ApiModelProperty(value = "客户", name = "customer")
    private String customer;
    @ApiModelProperty(value = "销售订单年份", name = "saleOrderYear")
    private String saleOrderYear;
    @ApiModelProperty(value = "合同号", name = "contract")
    private String contract;
    @ApiModelProperty(value = "生产仓储地点", name = "storage")
    private String storageLocation;
    @ApiModelProperty(value = "宣传颜色", name = "publicityColor")
    private String publicityColor;
    @ApiModelProperty(value = "美国名称", name = "usaName")
    private String usaName;
    @ApiModelProperty(value = "工厂", name = "factory")
    private String factory;
    /**
     * 添加已绑定数量属性(发动机绑定报工功能需要)
     *
     * @author yezi
     * @date 2019-06-11
     */
    @ApiModelProperty(value = "已绑定数量", name = "boundNumber")
    private Integer boundNumber;
    /**
     * 发动机条码
     *
     * @author yezi
     * @date 2019-06-11
     */
    @ApiModelProperty(value = "发动机条码", name = "engineBarcode")
    private String engineBarcode;
}
