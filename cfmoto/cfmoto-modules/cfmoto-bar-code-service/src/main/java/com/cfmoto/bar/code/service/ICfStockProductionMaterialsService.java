package com.cfmoto.bar.code.service;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfStockScanLine;

import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/18.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
public interface ICfStockProductionMaterialsService  extends IService<CfStockScanLine> {
    Map<String, Object> submitCfStockProductionMaterialsData(int userId, Map<String, Object> params) throws Exception;
}
