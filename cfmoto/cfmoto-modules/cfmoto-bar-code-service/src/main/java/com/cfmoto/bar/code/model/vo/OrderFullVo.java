package com.cfmoto.bar.code.model.vo;

import com.cfmoto.bar.code.model.entity.CfOrderScanTemp;
import com.cfmoto.bar.code.model.entity.CfOrderSumTemp;
import com.cfmoto.bar.code.model.entity.CfOrderTemp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value="OrderFullVo",description="订单关联数据")
public class OrderFullVo extends CfOrderTemp {

    @ApiModelProperty(value="汇总数据")
    private List<CfOrderSumTemp> cfOrderSumTempList;

    @ApiModelProperty(value="扫描数据")
    private List<CfOrderScanTemp> cfOrderScanTempList;

    public List<CfOrderSumTemp> getCfOrderSumTempList() {
        return cfOrderSumTempList;
    }

    public void setCfOrderSumTempList(List<CfOrderSumTemp> cfOrderSumTempList) {
        this.cfOrderSumTempList = cfOrderSumTempList;
    }

    public List<CfOrderScanTemp> getCfOrderScanTempList() {
        return cfOrderScanTempList;
    }

    public void setCfOrderScanTempList(List<CfOrderScanTemp> cfOrderScanTempList) {
        this.cfOrderScanTempList = cfOrderScanTempList;
    }
}
