package com.cfmoto.bar.code.model.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.cfmoto.bar.code.model.entity.CfDeliverGoods;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsScan;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsSum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value="DeliverGoodsFullVo",description="部品发货数据")
public class DeliverGoodsFullVo extends CfDeliverGoods {

    @ApiModelProperty( value = "汇总数据" )
    private List<CfDeliverGoodsSum> cfDeliverGoodsSumList = new ArrayList<CfDeliverGoodsSum>();

    @ApiModelProperty( value = "扫描数据" )
    private List<CfDeliverGoodsScan> cfDeliverGoodsScanList = new ArrayList<CfDeliverGoodsScan>();

    @ApiModelProperty( value = "提示内容" )
    @TableField(exist = false)
    private String message;

    @ApiModelProperty( value = "失败箱号列表" )
    @TableField(exist = false)
    private List<String> failCaseNoList = new ArrayList<String>();

    public List<CfDeliverGoodsSum> getCfDeliverGoodsSumList() {
        return cfDeliverGoodsSumList;
    }

    public void setCfDeliverGoodsSumList(List<CfDeliverGoodsSum> cfDeliverGoodsSumList) {
        this.cfDeliverGoodsSumList = cfDeliverGoodsSumList;
    }

    public List<CfDeliverGoodsScan> getCfDeliverGoodsScanList() {
        return cfDeliverGoodsScanList;
    }

    public void setCfDeliverGoodsScanList(List<CfDeliverGoodsScan> cfDeliverGoodsScanList) {
        this.cfDeliverGoodsScanList = cfDeliverGoodsScanList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getFailCaseNoList() {
        return failCaseNoList;
    }

    public void setFailCaseNoList(List<String> failCaseNoList) {
        this.failCaseNoList = failCaseNoList;
    }
}
