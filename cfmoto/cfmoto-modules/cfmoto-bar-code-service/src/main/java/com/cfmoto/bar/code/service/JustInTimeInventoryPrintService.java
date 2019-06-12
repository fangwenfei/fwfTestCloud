package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.vo.JustInTimeInventoryPrintVo;

import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/4/24.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
public interface JustInTimeInventoryPrintService  extends IService<CfBarcodeInventory> {
    List<CfBarcodeInventory> splitSapDataPrintByParam(Map<String, Object> params, int userId) throws Exception;
}
