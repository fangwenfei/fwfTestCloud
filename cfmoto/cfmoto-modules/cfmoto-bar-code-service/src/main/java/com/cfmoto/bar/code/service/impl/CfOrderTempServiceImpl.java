package com.cfmoto.bar.code.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.mapper.CfOrderTempMapper;
import com.cfmoto.bar.code.model.entity.CfOrderScanTemp;
import com.cfmoto.bar.code.model.entity.CfOrderSumTemp;
import com.cfmoto.bar.code.model.entity.CfOrderTemp;
import com.cfmoto.bar.code.model.vo.OrderFullVo;
import com.cfmoto.bar.code.service.ICfOrderScanTempService;
import com.cfmoto.bar.code.service.ICfOrderSumTempService;
import com.cfmoto.bar.code.service.ICfOrderTempService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.github.pig.common.util.StringUtils;
import com.github.pig.common.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 订单临时表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
@Service
public class CfOrderTempServiceImpl extends ServiceImpl<CfOrderTempMapper, CfOrderTemp> implements ICfOrderTempService {


    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private CfOrderTempMapper cfOrderTempMapper;

    @Autowired
    private ICfOrderSumTempService iCfOrderSumTempService;

    @Autowired
    private ICfOrderScanTempService iCfOrderScanTempService;

    @Autowired
    private UserFeignService userFeignService;

    /**
     * 通过CfOrderTemp删除
     *
     * @param cfOrderTemp
     * @return
     */
    @Override
    public Integer deleteByCfOrderTemp(CfOrderTemp cfOrderTemp) throws Exception {

        EntityWrapper<CfOrderTemp> wrapper = new EntityWrapper<CfOrderTemp>();
        wrapper.setEntity(cfOrderTemp);
        return cfOrderTempMapper.delete(wrapper);

    }


    /**
     * 获取订单数据
     * 并且计算汇总数据
     *
     * @param userId
     * @param outSourceOrder
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderFullVo outSourceOrderData(int userId, String outSourceOrder) throws Exception {
        UserVO user = userFeignService.user(userId);

        String outSourceOrderId = StringUtils.genHandle(HandleRefConstants.ORDER_ID, user.getSite(), outSourceOrder);
        OrderFullVo orderFullVo1 = cfOrderTempMapper.getOrderFullVo(userId, outSourceOrderId);


        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("IV_EBELN", outSourceOrder);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_002");
        callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);

        R returnR = sapFeignService.executeJcoFunction(callParamMap);

        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }

        List<Map<String, Object>> etDataList = (List<Map<String, Object>>) returnMap.get("ET_DATA");
        if (etDataList.size() == 0) {
            throw new Exception("请求返回清单数据为空");
        }

        if (orderFullVo1 == null) { //代表临时表没有订单数据，从sap获取订单数据
            OrderFullVo orderFullVo2 = saveOrderData(userId, outSourceOrderId, etDataList);
            return orderFullVo2;
        } else {
            return orderFullVo1;
        }

    }


    /**
     * 汇总保存订单数据
     *
     * @return
     */
    @Override
    public OrderFullVo saveOrderData(int userId, String outSourceOrderId, List<Map<String, Object>> etDataList) throws Exception {

        OrderFullVo orderFullVo = new OrderFullVo();
        Map<String, Object> jcoDataMap = etDataList.get(0);
        orderFullVo = new OrderFullVo();
        orderFullVo.setOrderTempId(outSourceOrderId);
        orderFullVo.setOrderNo(outSourceOrderId.split(",")[1]);
        orderFullVo.setOrderDesc(outSourceOrderId.split(",")[1]);
        orderFullVo.setStatus((String) jcoDataMap.get("STATU"));
        orderFullVo.setOrderType((String) jcoDataMap.get("BSART"));
        orderFullVo.setVendor((String) jcoDataMap.get("LIFNR"));
        orderFullVo.setVendorDesc((String) jcoDataMap.get("NAME1"));
        orderFullVo.setOrderDate(DateUtil.parse((String) jcoDataMap.get("AEDAT"), "yyyy-MM-dd"));
        orderFullVo.setObjectSetBasicAttribute(userId, new Date());

        List<CfOrderSumTemp> cfOrderSumTempList = new ArrayList<CfOrderSumTemp>();//初始汇总数据
        //EBELN===采购订单 BSART===订单类型 LIFNR===供应商代码 NAME1===供应商描述 EBELP===行项目号 MATNR===物料代码 MAKTX===物料名称 MENGE===订单数量
        // LGFSB===默认仓库 WEMNG===未清数量 FERTH===规格型号 EKGRP===采购组 BEDNR===用途 AEDAT===制单日期 STATU===订单状态
        CfOrderSumTemp cfOrderSumTemp = null;
        for (int i = 0, len = etDataList.size(); i < len; i++) {
            jcoDataMap = etDataList.get(i);
            cfOrderSumTemp = new CfOrderSumTemp();
            cfOrderSumTemp.setRowItem((String) jcoDataMap.get("EBELP"));
            cfOrderSumTemp.setOrderSumTempId(StringUtils.genHandle(HandleRefConstants.ORDER_SUM_ID, outSourceOrderId,
                    cfOrderSumTemp.getRowItem()));
            cfOrderSumTemp.setOrderTempIdRef(outSourceOrderId);
            cfOrderSumTemp.setItem((String) jcoDataMap.get("MATNR"));
            cfOrderSumTemp.setItemDesc((String) jcoDataMap.get("MAKTX"));
            cfOrderSumTemp.setItemPurpose((String) jcoDataMap.get("BEDNR"));
            cfOrderSumTemp.setQuantity(new BigDecimal((String) jcoDataMap.get("MENGE")));
            cfOrderSumTemp.setMode((String) jcoDataMap.get("FERTH"));
            cfOrderSumTemp.setDemandQty(new BigDecimal((String) jcoDataMap.get("WEMNG")));
            cfOrderSumTemp.setStorageLocation((String) jcoDataMap.get("LGFSB"));
            cfOrderSumTemp.setStorageArea((String) jcoDataMap.get("LGFSB"));
            cfOrderSumTemp.setPayableQty(new BigDecimal((String) jcoDataMap.get("MENGE")));
            cfOrderSumTemp.setOutputQty(new BigDecimal((String) jcoDataMap.get("MENGE"))
                    .subtract(new BigDecimal((String) jcoDataMap.get("WEMNG"))));
            cfOrderSumTemp.setObjectSetBasicAttribute(userId, new Date());
            cfOrderSumTempList.add(cfOrderSumTemp);
        }

        Wrapper<CfOrderScanTemp> cfOrderScanTempWrapper = new EntityWrapper<CfOrderScanTemp>();
        cfOrderScanTempWrapper.eq("order_temp_id_ref", outSourceOrderId);
        List<CfOrderScanTemp> cfOrderScanTempList = iCfOrderScanTempService.selectList(cfOrderScanTempWrapper);

        if (cfOrderScanTempList.size() != 0) { //扫描表有未提交的数据

            Map<String, BigDecimal> itemQtyMap = new HashMap<String, BigDecimal>(); //存放多余的超出的数据
            CfOrderScanTemp icfOrderScanTemp = null; //扫描数据
            CfOrderSumTemp jcfOrderSumTemp = null; //汇总数据
            String item = null;
            BigDecimal sumQty = null; //相加和
            for (int i = 0, len = cfOrderScanTempList.size(); i < len; i++) {

                icfOrderScanTemp = cfOrderScanTempList.get(i);
                item = icfOrderScanTemp.getItem();

                for (int j = 0, jLen = cfOrderSumTempList.size(); j < jLen; j++) {

                    if (item.equals(cfOrderSumTempList.get(j).getItem())) { //匹配的物料
                        jcfOrderSumTemp = cfOrderSumTempList.get(j);
                        if (jcfOrderSumTemp.getOutputQty().subtract(jcfOrderSumTemp.getPayableQty()).doubleValue() < 0) { //实出数量小于应出数量
                            sumQty = jcfOrderSumTemp.getOutputQty().add(icfOrderScanTemp.getQuantity());
                            if (sumQty.subtract(jcfOrderSumTemp.getPayableQty()).doubleValue() <= 0 && !itemQtyMap.containsKey(item)) {
                                jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(icfOrderScanTemp.getQuantity()));
                                jcfOrderSumTemp = null; //重新赋值为空
                                break;
                            } else if (itemQtyMap.containsKey(item)) { //代表扫描数据经过计算，有剩余
                                if (jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item))
                                        .subtract(jcfOrderSumTemp.getPayableQty()).doubleValue() > 0) { //代表超过
                                    jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getPayableQty());
                                    itemQtyMap.put(item, jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item))
                                            .subtract(jcfOrderSumTemp.getPayableQty())); //重新赋值剩余数量
                                } else {//代表未超过
                                    jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item)));
                                    itemQtyMap.remove(item);
                                    jcfOrderSumTemp = null; //重新赋值为空
                                    break;
                                }
                                jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(icfOrderScanTemp.getQuantity()));
                            } else {
                                jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getPayableQty());
                                if (itemQtyMap.containsKey(item)) {
                                    itemQtyMap.put(item, itemQtyMap.get(item).add(sumQty.subtract(jcfOrderSumTemp.getPayableQty())));
                                } else {
                                    itemQtyMap.put(item, sumQty.subtract(jcfOrderSumTemp.getPayableQty()));
                                }
                            }
                        } else {
                            if (itemQtyMap.containsKey(item)) {
                                itemQtyMap.put(item, itemQtyMap.get(item).add(icfOrderScanTemp.getQuantity()));
                            } else {
                                itemQtyMap.put(item, icfOrderScanTemp.getQuantity());
                            }
                        }

                    }
                }
                if (jcfOrderSumTemp != null) {

                    jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item)));
                    jcfOrderSumTemp = null;
                }
            }

        }

        String orderStatus = (String) jcoDataMap.get("STATU");
        if (!"C".equalsIgnoreCase(orderStatus)) { //订单状态未完成则保存数据
            cfOrderTempMapper.insert(orderFullVo); //保存订单临时表数据
            iCfOrderSumTempService.insertDataByBatch(cfOrderSumTempList); //插入汇总数据
        }


        orderFullVo.setCfOrderSumTempList(cfOrderSumTempList);
        orderFullVo.setCfOrderScanTempList(cfOrderScanTempList);
        return orderFullVo;
    }


    /**
     * 获取委外出库单数据
     *
     * @param userId
     * @param outSourceOrder
     * @return
     */
    @Override
    public OrderFullVo getOutSourceOutOrderData(int userId, String outSourceOrder) throws Exception {

        String outSourceOrderId = StringUtils.genHandle(HandleRefConstants.ORDER_ID, "1000", outSourceOrder);
        OrderFullVo orderFullVo = cfOrderTempMapper.getOrderFullVo(userId, outSourceOrderId);
        if (orderFullVo == null) { //代表临时表没有订单数据，从sap获取订单数据

            Map<String, Object> callParamMap = new HashMap<String, Object>();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("IV_EBELN", outSourceOrder);
            callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_021");
            callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);
            R returnR = sapFeignService.executeJcoFunction(callParamMap);
            Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
            Integer evStatus = (Integer) returnMap.get("EV_STATUS");
            if (evStatus == 0) {
                throw new Exception((String) returnMap.get("EV_MESSAGE"));
            }
            orderFullVo = saveOurSourceOutOrderData(userId, outSourceOrderId, (List<Map<String, Object>>) returnMap.get("ET_DATA"));
        }
        return orderFullVo;

    }

    /**
     * 保存委外出库订单数据
     *
     * @param userId
     * @param outSourceOrderId
     * @param etDataList
     * @return
     * @throws Exception
     */
    public OrderFullVo saveOurSourceOutOrderData(int userId, String outSourceOrderId, List<Map<String, Object>> etDataList) throws Exception {

        OrderFullVo orderFullVo = new OrderFullVo();
        Map<String, Object> jcoDataMap = etDataList.get(0);
        orderFullVo = new OrderFullVo();
        orderFullVo.setOrderTempId(outSourceOrderId);
        orderFullVo.setOrderNo(outSourceOrderId.split(",")[1]);
        orderFullVo.setOrderDesc(outSourceOrderId.split(",")[1]);
        orderFullVo.setStatus((String) jcoDataMap.get("STATU"));
        orderFullVo.setOrderType((String) jcoDataMap.get("BSART"));
        orderFullVo.setVendorDesc((String) jcoDataMap.get("NAME1"));
        orderFullVo.setOrderDate(DateUtil.parse((String) jcoDataMap.get("AEDAT"), "yyyy-MM-dd"));
        orderFullVo.setObjectSetBasicAttribute(userId, new Date());

        List<CfOrderSumTemp> cfOrderSumTempList = new ArrayList<CfOrderSumTemp>();//初始汇总数据
        //EBELN===采购订单 BSART===订单类型 NAME1===供应商描述 RSPOS===行项目号 MATNR===物料代码 MAKTX===物料名称 BDMNG===订单数量
        // LGFSB===默认仓库 ZWQSL===未清数量 WRKST===规格型号 EKGRP===采购组 BEDNR===用途 AEDAT===制单日期 STATU===订单状态
        CfOrderSumTemp cfOrderSumTemp = null;
        for (int i = 0, len = etDataList.size(); i < len; i++) {
            jcoDataMap = etDataList.get(i);
            cfOrderSumTemp = new CfOrderSumTemp();
            cfOrderSumTemp.setRowItem((String) jcoDataMap.get("RSPOS"));
            cfOrderSumTemp.setOrderSumTempId(StringUtils.genHandle(HandleRefConstants.ORDER_SUM_ID, outSourceOrderId,
                    cfOrderSumTemp.getRowItem()));
            cfOrderSumTemp.setOrderTempIdRef(outSourceOrderId);
            cfOrderSumTemp.setItem((String) jcoDataMap.get("MATNR"));
            cfOrderSumTemp.setItemDesc((String) jcoDataMap.get("MAKTX"));
            cfOrderSumTemp.setItemPurpose((String) jcoDataMap.get("BEDNR"));
            cfOrderSumTemp.setQuantity(new BigDecimal((String) jcoDataMap.get("BDMNG")));
            cfOrderSumTemp.setMode((String) jcoDataMap.get("WRKST"));
            cfOrderSumTemp.setDemandQty(new BigDecimal((String) jcoDataMap.get("ZWQSL")));
            cfOrderSumTemp.setStorageLocation((String) jcoDataMap.get("LGFSB"));
            cfOrderSumTemp.setPayableQty(new BigDecimal((String) jcoDataMap.get("BDMNG")));
            cfOrderSumTemp.setOutputQty(cfOrderSumTemp.getQuantity().subtract(cfOrderSumTemp.getDemandQty()));
            cfOrderSumTemp.setObjectSetBasicAttribute(userId, new Date());
            cfOrderSumTempList.add(cfOrderSumTemp);
        }

        Wrapper<CfOrderScanTemp> cfOrderScanTempWrapper = new EntityWrapper<CfOrderScanTemp>();
        cfOrderScanTempWrapper.eq("order_temp_id_ref", outSourceOrderId);
        List<CfOrderScanTemp> cfOrderScanTempList = iCfOrderScanTempService.selectList(cfOrderScanTempWrapper);

        if (cfOrderScanTempList.size() != 0) { //扫描表有未提交的数据

            Map<String, BigDecimal> itemQtyMap = new HashMap<String, BigDecimal>(); //存放多余的超出的数据
            CfOrderScanTemp icfOrderScanTemp = null; //扫描数据
            CfOrderSumTemp jcfOrderSumTemp = null; //汇总数据
            String item = null;
            BigDecimal sumQty = null; //相加和
            for (int i = 0, len = cfOrderScanTempList.size(); i < len; i++) {

                icfOrderScanTemp = cfOrderScanTempList.get(i);
                item = icfOrderScanTemp.getItem();

                for (int j = 0, jLen = cfOrderSumTempList.size(); j < jLen; j++) {

                    if (item.equals(cfOrderSumTempList.get(j).getItem())) { //匹配的物料
                        jcfOrderSumTemp = cfOrderSumTempList.get(j);
                        if (jcfOrderSumTemp.getOutputQty().subtract(jcfOrderSumTemp.getPayableQty()).doubleValue() < 0) { //实出数量小于应出数量
                            sumQty = jcfOrderSumTemp.getOutputQty().add(icfOrderScanTemp.getQuantity());
                            if (sumQty.subtract(jcfOrderSumTemp.getPayableQty()).doubleValue() <= 0 && !itemQtyMap.containsKey(item)) {
                                jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(icfOrderScanTemp.getQuantity()));
                                jcfOrderSumTemp = null; //重新赋值为空
                                break;
                            } else if (itemQtyMap.containsKey(item)) { //代表扫描数据经过计算，有剩余
                                if (jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item))
                                        .subtract(jcfOrderSumTemp.getPayableQty()).doubleValue() > 0) { //代表超过
                                    jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getPayableQty());
                                    itemQtyMap.put(item, jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item))
                                            .subtract(jcfOrderSumTemp.getPayableQty())); //重新赋值剩余数量
                                } else {//代表未超过
                                    jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item)));
                                    itemQtyMap.remove(item);
                                    jcfOrderSumTemp = null; //重新赋值为空
                                    break;
                                }
                                jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(icfOrderScanTemp.getQuantity()));
                            } else {
                                jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getPayableQty());
                                if (itemQtyMap.containsKey(item)) {
                                    itemQtyMap.put(item, itemQtyMap.get(item).add(sumQty.subtract(jcfOrderSumTemp.getPayableQty())));
                                } else {
                                    itemQtyMap.put(item, sumQty.subtract(jcfOrderSumTemp.getPayableQty()));
                                }
                            }
                        } else {
                            if (itemQtyMap.containsKey(item)) {
                                itemQtyMap.put(item, itemQtyMap.get(item).add(icfOrderScanTemp.getQuantity()));
                            } else {
                                itemQtyMap.put(item, icfOrderScanTemp.getQuantity());
                            }
                        }

                    }
                }
                if (jcfOrderSumTemp != null) {

                    jcfOrderSumTemp.setOutputQty(jcfOrderSumTemp.getOutputQty().add(itemQtyMap.get(item)));
                    jcfOrderSumTemp = null;
                }
            }

        }

        String orderStatus = (String) jcoDataMap.get("STATU");
        if (!"C".equalsIgnoreCase(orderStatus)) { //订单状态未完成则保存数据
            cfOrderTempMapper.insert(orderFullVo); //保存订单临时表数据
            iCfOrderSumTempService.insertDataByBatch(cfOrderSumTempList); //插入汇总数据
        }

        orderFullVo.setCfOrderSumTempList(cfOrderSumTempList);
        orderFullVo.setCfOrderScanTempList(cfOrderScanTempList);
        return orderFullVo;
    }

}
