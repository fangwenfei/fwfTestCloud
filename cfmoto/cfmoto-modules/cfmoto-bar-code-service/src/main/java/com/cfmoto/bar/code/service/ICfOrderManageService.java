package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.vo.KtmOrderVo;
import com.cfmoto.bar.code.model.vo.OrderFullVo;
import com.cfmoto.bar.code.model.vo.OrderReceiveVo;
import com.cfmoto.bar.code.model.vo.PurchaseOrOutsourceOrderVo;

import java.util.List;

public interface ICfOrderManageService {

    List<PurchaseOrOutsourceOrderVo> outsourceReceiveGoods( int userId, OrderReceiveVo orderReceiveVo ) throws Exception;

    void outsourceSendOutGoods(int userId, String orderNo) throws Exception;

    List<PurchaseOrOutsourceOrderVo> getOutsourcePurchaseOrder( int userId, String orderNo, String requireOrderType ) throws Exception;

    List<PurchaseOrOutsourceOrderVo> getPartsPurchaseOrder(int userId, String orderNo, String requireOrderType ) throws Exception;

    List<PurchaseOrOutsourceOrderVo> purchasePartsReceiveGoods( int userId, OrderReceiveVo orderReceiveVo ) throws Exception;

    List<PurchaseOrOutsourceOrderVo> getKtmPurchaseOrder( int userId, String orderNo, String requireOrderType ) throws Exception;

    List<PurchaseOrOutsourceOrderVo> ktmPurchasePartsReceiveGoods(int userId, KtmOrderVo ktmOrderVo) throws Exception;

    OrderFullVo purchaseSendOutGoods(int userId, String orderNo ) throws Exception;
}
