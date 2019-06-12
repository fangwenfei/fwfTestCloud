package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.vo.PurchaseOrOutsourceOrderVo;

import java.util.List;

/* **********************************************************************
 *              Created by FangWenFei on 2019/5/8.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
public interface PurchaseOrderSearchService {

    List<PurchaseOrOutsourceOrderVo> searchPurchaseOrderBySap(String orderNo, String vendor,String item ) throws Exception;
}
