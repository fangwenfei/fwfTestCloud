package com.cfmoto.bar.code.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.bo.CfAllotManagementBo;
import com.cfmoto.bar.code.model.bo.CfCecDeliverGoodsBo;
import com.cfmoto.bar.code.model.dto.CfStockSplitDto;
import com.cfmoto.bar.code.model.entity.CfAllotScanRecord;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsScanRecord;
import com.cfmoto.bar.code.model.vo.CfAllotManagementVo;
import com.cfmoto.bar.code.model.vo.CfCecDeliverGoodsVo;
import com.cfmoto.bar.code.model.vo.PartsLabelPrintVo;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.utiles.SapUtils;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用Sap接口的实现类
 *
 * @author
 */
@Service
public class SapApiServiceImpl implements ISapApiService {

    /**
     * sap接口08的方法名
     */
    private static final String SAP_08_FUNCTION_NAME = "ZMM_BC_024";

    /**
     * sap接口10的方法名
     */
    private static final String SAP_10_FUNCTION_NAME = "ZMM_BC_020";

    /**
     * sap接口09A的方法名（二步调拨出库上传接口）
     */
    private static final String SAP_09A_FUNCTION_NAME = "ZMM_BC_025";

    /**
     * sap接口09B的方法名（二步调拨入库上传接口）
     */
    private static final String SAP_09B_FUNCTION_NAME = "ZMM_BC_026";

    /**
     * sap接口09C的方法名（一步调拨上传接口）
     */
    private static final String SAP_09C_FUNCTION_NAME = "ZMM_BC_030";

    /**
     * sap接口09D的方法名（一步调拨下载接口）
     */
    private static final String SAP_09D_FUNCTION_NAME = "ZMM_BC_028";

    /**
     * sap接口32的方法名(生产领料交接数据接收接口)
     */
    private static final String SAP_032_FUNCTION_NAME = "ZMM_BC_032";

    private final Logger logger = LoggerFactory.getLogger(SapApiServiceImpl.class);

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private CfAllotManagementBo cfAllotManagementBo;

    @Autowired
    private CfCecDeliverGoodsBo deliverGoodsBo;

    @Override
    public CfAllotManagementVo getDataFromSapApi08(String orderNo) throws Exception {
        //封装方法名和方法参数
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("IV_ZDBDH", StringUtils.trimToEmpty(orderNo));

        Map<String, Object> callParamMap = SapUtils.packParamsIntoMap(SAP_08_FUNCTION_NAME, paramMap);

        R r = sapFeignService.executeJcoFunction(callParamMap);

        if (r.getCode() == 1) {
            //打印错误结果日志
            logger.info(r.getMsg());
            throw new Exception(r.getMsg());
        }

        //到这里说明调用SAP08接口正常，获取sap返回数据并封装到调拨管理vo对象
        JSONObject jsonObject = new JSONObject((Map<String, Object>) r.getData());

        //获取sap接口返回的状态并判断
        String sapStatus = jsonObject.getString("EV_STATUS");

        //sap接口返回的状态码为1，即错误码
        if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
            throw new Exception(jsonObject.getString("EV_MESSAGE"));
        }

        //将sap返回数据封装成调拨管理vo对象并返回
        return cfAllotManagementBo.sapDataToAllotVo(jsonObject);

    }

    @Override
    public List<Map<String, Object>> newGetDataFromSapApi10(String wareHouseNo, String materialsNo, String factory, String currentUrl) throws Exception {
        List<Map<String, Object>> maps = new ArrayList<>();

        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("functionName", SAP_10_FUNCTION_NAME);

        Map<String, Object> dataMap = new HashMap<>(4);


        dataMap.put("IV_WERKS", StringUtils.trimToEmpty(factory));
        dataMap.put("IV_LGORT", StringUtils.trimToEmpty(wareHouseNo));
        dataMap.put("IV_MATNR", StringUtils.trimToEmpty(materialsNo));
        paramMap.put("paramMap", dataMap);

        R<Map<String, Object>> r = sapFeignService.executeJcoFunction(paramMap);

        if (r.getCode() == R.FAIL) {
            //首先打印错误信息到日志
            logger.error(r.getMsg());
            //再返回友好的提示信息给前端用户界面
            throw new Exception(r.getMsg());
        } else {
            Map<String, Object> data = r.getData();
            JSONObject jsonObject = new JSONObject(data);

            //获取sap接口返回的状态并判断
            String sapStatus = jsonObject.getString("EV_STATUS");

            //sap接口返回的状态码为1，即错误码
            if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
                throw new Exception(jsonObject.getString("EV_MESSAGE"));
            }

            JSONArray etData = jsonObject.getJSONArray("ET_DATA");

            for (Object etDatum : etData) {
                Map<String, Object> temp = (Map<String, Object>) etDatum;

                //首先判断非限制库存是否大于0，小于0则不放入map返回
                if (new BigDecimal(temp.getOrDefault("ZFXKC", 0).toString()).intValue() <= 0) {
                    continue;
                }

                // 如果currentUrl不为空，则判断是否为采购退货，然后判断冻结库存或非限制库存是否大于0，小于0则不放入map返回
                if (StrUtil.isNotBlank(currentUrl)) {
                    if (currentUrl.equals("/cfPurchaseManage/getBarCodeByItem")) {
                        if (new BigDecimal(temp.getOrDefault("ZDJKC", 0).toString()).intValue() <= 0 && new BigDecimal(temp.getOrDefault("ZFXKC", 0).toString()).intValue() <= 0) {
                            continue;
                        }
                    }
                }


                Map<String, Object> map = new HashMap<>(12);

                map.put("batchNo", temp.getOrDefault("CHARG", ""));
                map.put("batchNumber", temp.getOrDefault("ZFXKC", 0));
                map.put("materialsNo", temp.getOrDefault("MATNR", ""));
                map.put("spStoreNo", temp.getOrDefault("ZSPCWH", ""));
                map.put("materialsName", temp.getOrDefault("MAKTX", ""));
                //冻结库存数量
                map.put("freezeInventoryNumber", temp.getOrDefault("ZDJKC", 0));
                map.put("spec", temp.getOrDefault("WRSKT", ""));
                map.put("wareHouse", temp.getOrDefault("LGORT", ""));
                map.put("storageArea", temp.getOrDefault("LGTYP", ""));
                map.put("warehousePosition", temp.getOrDefault("LGPLA", ""));
                map.put("supplier", temp.getOrDefault("NAME_ORG1", ""));
                maps.add(map);

            }
            return maps;
        }
    }

    @Override
    public List<Map<String, Object>> newGetDataFromSapApi10(String wareHouseNo, String materialsNo, String factory, String batchNo, String storageArea, String currentUrl) throws Exception {
        List<Map<String, Object>> maps = new ArrayList<>();

        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("functionName", SAP_10_FUNCTION_NAME);

        Map<String, Object> dataMap = new HashMap<>(4);

        dataMap.put("IV_WERKS", StringUtils.trimToEmpty(factory));
        dataMap.put("IV_LGORT", StringUtils.trimToEmpty(wareHouseNo));
        dataMap.put("IV_MATNR", StringUtils.trimToEmpty(materialsNo));

        //如果批号和存储区域不为空，则传给sap，为空则不传给sap
        if (!StringUtils.isBlank(batchNo)) {
            dataMap.put("IV_CHARG", StringUtils.trimToEmpty(batchNo));
        }
        if (!StringUtils.isBlank(storageArea)) {
            dataMap.put("IV_LGTYP", StringUtils.trimToEmpty(storageArea));
        }

        paramMap.put("paramMap", dataMap);

        R<Map<String, Object>> r = sapFeignService.executeJcoFunction(paramMap);

        if (r.getCode() == R.FAIL) {
            //首先打印错误信息到日志
            logger.error(r.getMsg());
            //再返回友好的提示信息给前端用户界面
            throw new Exception(r.getMsg());
        } else {
            Map<String, Object> data = r.getData();
            JSONObject jsonObject = new JSONObject(data);

            //获取sap接口返回的状态并判断
            String sapStatus = jsonObject.getString("EV_STATUS");

            //sap接口返回的状态码为1，即错误码
            if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
                throw new Exception(jsonObject.getString("EV_MESSAGE"));
            }

            JSONArray etData = jsonObject.getJSONArray("ET_DATA");

            for (Object etDatum : etData) {
                Map<String, Object> temp = (Map<String, Object>) etDatum;

                //首先判断非限制库存是否大于0，小于0则不放入map返回
                if (new BigDecimal(temp.getOrDefault("ZFXKC", 0).toString()).intValue() <= 0) {
                    continue;
                }

                // 如果currentUrl不为空，则判断是否为采购退货，然后判断冻结库存或非限制库存是否大于0，小于0则不放入map返回
                if (StrUtil.isNotBlank(currentUrl)) {
                    if (currentUrl.equals("/cfPurchaseManage/getBarCodeByItem")) {
                        if (new BigDecimal(temp.getOrDefault("ZDJKC", 0).toString()).intValue() <= 0 && new BigDecimal(temp.getOrDefault("ZFXKC", 0).toString()).intValue() <= 0) {
                            continue;
                        }
                    }
                }


                Map<String, Object> map = new HashMap<>(12);

                map.put("batchNo", temp.getOrDefault("CHARG", ""));
                map.put("batchNumber", temp.getOrDefault("ZFXKC", 0));
                map.put("materialsNo", temp.getOrDefault("MATNR", ""));
                map.put("materialsName", temp.getOrDefault("MAKTX", ""));
                //冻结库存数量
                map.put("freezeInventoryNumber", temp.getOrDefault("ZDJKC", 0));
                map.put("spec", temp.getOrDefault("WRSKT", ""));
                map.put("wareHouse", temp.getOrDefault("LGORT", ""));
                map.put("storageArea", temp.getOrDefault("LGTYP", ""));
                map.put("warehousePosition", temp.getOrDefault("LGPLA", ""));
                map.put("supplier", temp.getOrDefault("NAME_ORG1", ""));
                maps.add(map);

            }
            return maps;
        }
    }

    @Override
    public void postDataToSapApi09A(List<CfAllotScanRecord> scanRecords) throws Exception {

        //封装参数
        Map<String, Object> paramMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (CfAllotScanRecord scanRecord : scanRecords) {
            Map<String, Object> map = new HashMap<>(12);
            map.put("ZDBDH", StringUtils.trimToEmpty(scanRecord.getOrderNo()));
            map.put("MATNR", StringUtils.trimToEmpty(scanRecord.getMaterialsNo()));
            map.put("CHARG", StringUtils.trimToEmpty(scanRecord.getBatchNo()));
            map.put("ZDCCK", StringUtils.trimToEmpty(scanRecord.getWarehouse()));
            map.put("ZDCQY", StringUtils.trimToEmpty(scanRecord.getStorageArea()));
            map.put("ZDCCW", StringUtils.trimToEmpty(scanRecord.getWarehousePosition()));
            map.put("ZFHSL", scanRecord.getNumber() + "");
            map.put("ZDRCK", StringUtils.trimToEmpty(scanRecord.getAllotInWarehouse()));
            list.add(map);
        }
        paramMap.put("IT_DATA", list);
        Map<String, Object> callParamMap = SapUtils.packParamsIntoMap(SAP_09A_FUNCTION_NAME, paramMap);
        R r = sapFeignService.executeJcoFunction(callParamMap);

        if (r.getCode() == 1) {
            //打印错误结果日志
            logger.info(r.getMsg());
            throw new Exception(r.getMsg());
        }

        //到这里说明调用SAP09A接口正常，获取sap返回数据并封装到调拨管理vo对象
        JSONObject jsonObject = new JSONObject((Map<String, Object>) r.getData());

        //获取sap接口返回的状态并判断
        String sapStatus = jsonObject.getString("EV_STATUS");

        //sap接口返回的状态码为1，即错误码
        if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
            throw new Exception(jsonObject.getString("EV_MESSAGE"));
        }

    }

    /**
     * 2019-4-24 space修改从返回void修改为范围CfAllotManagementVo
     *
     * @param scanRecords 扫描数据列表
     * @return
     * @throws Exception
     */
    @Override
    public CfAllotManagementVo postDataToSapApi09B(List<CfAllotScanRecord> scanRecords) throws Exception {

        //封装参数
        Map<String, Object> paramMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (CfAllotScanRecord scanRecord : scanRecords) {
            Map<String, Object> map = new HashMap<>(12);
            //调拨单号
            map.put("ZDBDH", StringUtils.trimToEmpty(scanRecord.getOrderNo()));
            //物料编号
            map.put("MATNR", StringUtils.trimToEmpty(scanRecord.getMaterialsNo()));
            //批号
            map.put("CHARG", StringUtils.trimToEmpty(scanRecord.getBatchNo()));
            //调入仓库
            map.put("ZDRCK", StringUtils.trimToEmpty(scanRecord.getAllotInWarehouse()));
            //调出仓库
            map.put("ZDCCK", StringUtils.trimToEmpty(scanRecord.getWarehouse()));
            //调入数量
            map.put("ZRKSL", scanRecord.getNumber() + "");
            //发运单号
            map.put("ZFYDH", StringUtils.trimToEmpty(scanRecord.getSendWaybillNo()));
            //箱号
            map.put("ZZXH", StringUtils.trimToEmpty(scanRecord.getCaseNo()));
            //长
            map.put("ZLEAN", scanRecord.getLength() + "");
            //宽
            map.put("ZBRET", scanRecord.getWidth() + "");
            //高
            map.put("ZHOEE", scanRecord.getHeight() + "");
            //毛重
            map.put("ZBRGE", scanRecord.getRoughWeight() + "");
            //快递公司
            map.put("ZKDGS", scanRecord.getExpressCompany());

            list.add(map);
        }
        paramMap.put("IT_DATA", list);
        Map<String, Object> callParamMap = SapUtils.packParamsIntoMap(SAP_09B_FUNCTION_NAME, paramMap);
        R r = sapFeignService.executeJcoFunction(callParamMap);

        if (r.getCode() == 1) {
            //打印错误结果日志
            logger.info(r.getMsg());
            throw new Exception(r.getMsg());
        }

        //到这里说明调用SAP09A接口正常，获取sap返回数据并封装到调拨管理vo对象
        JSONObject jsonObject = new JSONObject((Map<String, Object>) r.getData());

        //获取sap接口返回的状态并判断
        String sapStatus = jsonObject.getString("EV_STATUS");

        //sap接口返回的状态码为1，即错误码
        if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
            throw new Exception(jsonObject.getString("EV_MESSAGE"));
        }

        //将sap返回数据封装成调拨管理vo对象并返回
        return cfAllotManagementBo.sapDataToAllotVo(jsonObject);


    }

    @Override
    public void postDataToSapApi09C(List<CfAllotScanRecord> scanRecords, String orderNo) throws Exception {
        //封装参数
        Map<String, Object> paramMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (CfAllotScanRecord scanRecord : scanRecords) {
            Map<String, Object> map = new HashMap<>(12);
            map.put("MATNR", StringUtils.trimToEmpty(scanRecord.getMaterialsNo()));
            map.put("TMLX", StringUtils.trimToEmpty(scanRecord.getBarcodeType()));
            map.put("GERNR", StringUtils.trimToEmpty(scanRecord.getBarcode()));
            map.put("CHARG", StringUtils.trimToEmpty(scanRecord.getBatchNo()));
            map.put("LGORT", StringUtils.trimToEmpty(scanRecord.getWarehouse()));
            map.put("VLTYP", StringUtils.trimToEmpty(scanRecord.getStorageArea()));
            map.put("VLPLA", StringUtils.trimToEmpty(scanRecord.getWarehousePosition()));
            map.put("ERFMG", scanRecord.getNumber() + "");
            map.put("UMLGO", StringUtils.trimToEmpty(scanRecord.getAllotInWarehouse()));

            list.add(map);
        }
        paramMap.put("IT_DATA", list);
        paramMap.put("IV_RSNUM", orderNo);
        Map<String, Object> callParamMap = SapUtils.packParamsIntoMap(SAP_09C_FUNCTION_NAME, paramMap);
        R r = sapFeignService.executeJcoFunction(callParamMap);

        if (r.getCode() == 1) {
            //打印错误结果日志
            logger.info(r.getMsg());
            throw new Exception(r.getMsg());
        }

        //到这里说明调用SAP09A接口正常，获取sap返回数据并封装到调拨管理vo对象
        JSONObject jsonObject = new JSONObject((Map<String, Object>) r.getData());

        //获取sap接口返回的状态并判断
        String sapStatus = jsonObject.getString("EV_STATUS");

        //sap接口返回的状态码为1，即错误码
        if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
            throw new Exception(jsonObject.getString("EV_MESSAGE"));
        }

    }

    @Override
    public CfAllotManagementVo getDataToSapApi09D(String orderNo) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("IV_RSNUM", StringUtils.trimToEmpty(orderNo));

        Map<String, Object> callParamMap = SapUtils.packParamsIntoMap(SAP_09D_FUNCTION_NAME, paramMap);
        R r = sapFeignService.executeJcoFunction(callParamMap);

        if (r.getCode() == 1) {
            //打印错误结果日志
            logger.info(r.getMsg());
            throw new Exception(r.getMsg());
        }


        //到这里说明调用SAP09A接口正常，获取sap返回数据并封装到调拨管理vo对象
        JSONObject jsonObject = new JSONObject((Map<String, Object>) r.getData());

        //获取sap接口返回的状态并判断
        String sapStatus = jsonObject.getString("EV_STATUS");

        //sap接口返回的状态码为0，即错误码
        if (sapStatus.equals(SapFeignService.ERROR_CODE)) {
            throw new Exception(jsonObject.getString("EV_MESSAGE"));
        }

        return cfAllotManagementBo.oneStepSapDataToAllotVo(jsonObject);

    }


    @Override
    public void getDataFromSapApi032(List<CfStockSplitDto> dtoList) throws Exception {
        //将集合中的数据装成sap所需的参数
        List<Map<String, Object>> mapList = new ArrayList<>();
        dtoList.forEach(dto -> {
            HashMap<String, Object> map = new HashMap<>(9);
            map.put("MATNR", StringUtils.trimToEmpty(dto.getMaterialsNo()));
            map.put("TMLX", StringUtils.trimToEmpty(dto.getBarcodeType()));
            map.put("ZLIST", StringUtils.trimToEmpty(dto.getStockListNo()));
            map.put("CHARG", StringUtils.trimToEmpty(dto.getBatchNo()));
            map.put("BDMNG", Integer.toString(dto.getNumber() == null ? 0 : dto.getNumber()));
            map.put("LGORT", StringUtils.trimToEmpty(dto.getStockWarehouse()));
            mapList.add(map);
        });


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("IT_DATA", mapList);
        paramMap.put("IV_ZLIST", dtoList.get(0).getStockListNo());
        Map<String, Object> callParamMap = SapUtils.packParamsIntoMap(SAP_032_FUNCTION_NAME, paramMap);
        R r = sapFeignService.executeJcoFunction(callParamMap);
        if (r.getCode() == R.FAIL) {
            //打印错误日志
            logger.error(r.getMsg());
            throw new Exception(r.getMsg());
        }
        JSONObject jsonObject = new JSONObject((Map<String, Object>) r.getData());

        if (jsonObject.getString("EV_STATUS").equals(SapFeignService.ERROR_CODE)) {
            throw new Exception(jsonObject.getString("EV_MESSAGE"));
        }
    }

    @Override
    public ProductionTaskVo getDataFromSapApi004(String orderNo) throws Exception {
        ProductionTaskVo productionTaskVo;

        Map<String, Object> param = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        if (StrUtil.isBlank(orderNo)) {
            throw new Exception("生产任务单不能为空");
        }
        if (orderNo.trim().length() > 12) {
            throw new Exception("生产任务单长度不能超过12个字符");
        }
        paramMap.put("IV_AUFNR", orderNo.trim());
        param.put(HandleRefConstants.PARAM_MAP, paramMap);
        param.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_004");
        R returnR = sapFeignService.executeJcoFunction(param);
        if (returnR.getCode() != 0) {
            throw new ValidationException(returnR.getMsg());
        }
        Map<String, Object> esDataMap = (Map<String, Object>) returnR.getData();
        if ((Integer) esDataMap.get("EV_STATUS") == 0) {
            throw new ValidationException((String) esDataMap.get("EV_MESSAGE"));
        }

        Map<String, Object> dataMap = (Map<String, Object>) esDataMap.get("ES_DATA");
        //AUFNR==生产订单 DAUAT==订单类型 MATNR==物料代码 MAKTX==物料名称 PSMNG==任务单数量 WEMNG==已入库数量 FERTH==车型 WRKST==产品规格 KDAUF==销售订单
        //KDPOS==销售订单行项目 KUNNR==客户 ZTEXT==销售订单年份 ZHTH==合同号 LGPRO==生产仓储地点
        productionTaskVo = new ProductionTaskVo();
        productionTaskVo.setTaskNo((String) dataMap.get("AUFNR"));
        productionTaskVo.setOrderType((String) dataMap.get("DAUAT"));
        productionTaskVo.setItem((String) dataMap.get("MATNR"));
        productionTaskVo.setItemDesc((String) dataMap.get("MAKTX"));
        productionTaskVo.setQuantity(new BigDecimal(dataMap.get("PSMNG").toString()));
        productionTaskVo.setReceivedQty(new BigDecimal(dataMap.get("WEMNG").toString()));
        productionTaskVo.setCarType((String) dataMap.get("FERTH"));
        productionTaskVo.setMode((String) dataMap.get("WRKST"));
        productionTaskVo.setSaleOrder((String) dataMap.get("KDAUF"));
        productionTaskVo.setSaleOrderRowItem((String) dataMap.get("KDPOS"));
        productionTaskVo.setCustomer((String) dataMap.get("KUNNR"));
        productionTaskVo.setSaleOrderYear((String) dataMap.get("ZTEXT"));
        productionTaskVo.setContract((String) dataMap.get("ZHTH"));
        productionTaskVo.setStorageLocation((String) dataMap.get("LGPRO"));
        productionTaskVo.setPublicityColor((String) dataMap.get("ZXCYS"));
        productionTaskVo.setUsaName((String) dataMap.getOrDefault("EAN11", ""));
        return productionTaskVo;
    }


    /**
     * 销售订单传输接口
     *
     * @param deliverOrderNo 交货单号(发货通知单)
     * @return CfCecDeliverGoodsVo
     * @throws Exception
     */
    @Override
    public CfCecDeliverGoodsVo getDataFromSapApi007(String deliverOrderNo) throws Exception {

        Map<String, Object> param = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        if (StrUtil.isBlank(StrUtil.trim(deliverOrderNo))) {
            throw new Exception("交货单不能为空");
        }

        //将交货单作为参数传给sap
        paramMap.put("IV_VBELN", deliverOrderNo);
        param.put(HandleRefConstants.PARAM_MAP, paramMap);
        param.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_007");
        R returnR = sapFeignService.executeJcoFunction(param);
        if (returnR.getCode() != 0) {
            throw new ValidationException(returnR.getMsg());
        }
        Map<String, Object> esDataMap = (Map<String, Object>) returnR.getData();
        if ((Integer) esDataMap.get("EV_STATUS") == 0) {
            throw new ValidationException((String) esDataMap.get("EV_MESSAGE"));
        }

        //将sap返回的数据封装到部品网购发货视图层对象中
        return deliverGoodsBo.transferSapData(esDataMap);
    }

    @Override
    public CfCecDeliverGoodsVo postDataToSapApi006(String deliverOrderNo, List<CfCecDeliverGoodsScanRecord> scanRecordList) throws Exception {
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        if (StrUtil.isBlank(StrUtil.trim(deliverOrderNo))) {
            throw new Exception("交货单不能为空");
        }

        //将交货单和扫描表集合作为参数传给sap
        paramMap.put("IV_VBELN", deliverOrderNo);

        List<Map<String, Object>> paramMapList = new ArrayList<>();
        for (CfCecDeliverGoodsScanRecord scanRecord : scanRecordList) {
            Map<String, Object> map = new HashMap<>();
            //行项目
            map.put("POSNR", scanRecord.getRowItem());
            //物料代码
            map.put("MATNR", scanRecord.getMaterialsNo());
            //物料名称
            map.put("MAKTX", scanRecord.getMaterialsName());
            //规格型号
            map.put("WRKST", scanRecord.getSpec());
            //条码类型
            map.put("TMLX", scanRecord.getBarcodeType());
            //条码
            map.put("GERNR", scanRecord.getBarcode());
            //批号
            map.put("CHARG", scanRecord.getBatchNo());
            //数量
            map.put("LFIMG", scanRecord.getNumber().toString());
            //仓库
            map.put("LGORT", scanRecord.getWarehouse());
            //存储区域
            map.put("VLTYP", scanRecord.getStorageArea());
            //仓位
            map.put("VLPLA", scanRecord.getWarehousePosition());
            //销售订单
            map.put("VGBEL", scanRecord.getSalesOrderNo());
            //制单人
            map.put("ZUSER", userFeignService.user(scanRecord.getCreatedBy()).getUsername());
            //制单时间
            map.put("ZDATE", DateUtil.format(scanRecord.getCreationDate(), DatePattern.NORM_DATE_PATTERN));
            //运单号
            map.put("ZMM03", scanRecord.getTrackingNo());
            paramMapList.add(map);
        }
        paramMap.put("IT_DATA", paramMapList);

        param.put(HandleRefConstants.PARAM_MAP, paramMap);

        param.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_006");
        R returnR = sapFeignService.executeJcoFunction(param);
        if (returnR.getCode() != 0) {
            throw new ValidationException(returnR.getMsg());
        }

        Map<String, Object> esDataMap = (Map<String, Object>) returnR.getData();
        if ((Integer) esDataMap.get("EV_STATUS") == 0) {
            throw new ValidationException((String) esDataMap.get("EV_MESSAGE"));
        }

        //封装订单状态到返回对象
        CfCecDeliverGoodsVo deliverGoodsVo = new CfCecDeliverGoodsVo();
        deliverGoodsVo.setOrderStatus((String) esDataMap.getOrDefault("EV_GBSTK", ""));

        return deliverGoodsVo;
    }

    /**
     * 物料主数据下载接口
     *
     * @param materialsNo   物料编码
     * @param materialsName 物料名称
     * @return list
     */
    @Override
    public List<PartsLabelPrintVo> getDataFromMainData001(String materialsNo, String materialsName) throws ValidationException {
        String apiName = "ZMM_BC_001";
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();

        //动态参数查询:参数不为空则传入sap方法参数调用,反则传入
        if (StrUtil.isNotBlank(materialsNo)) {
            //物料代码
            paramMap.put("IV_MATNR", materialsNo);
        }

        if (StrUtil.isNotBlank(materialsName)) {
            //物料描述
            paramMap.put("IV_MAKTX", materialsName);
        }

        param.put(HandleRefConstants.PARAM_MAP, paramMap);
        param.put(HandleRefConstants.FUNCTION_NAME, apiName);

        R returnR = sapFeignService.executeJcoFunction(param);
        if (returnR.getCode() != 0) {
            throw new ValidationException(returnR.getMsg());
        }

        Map<String, Object> esDataMap = (Map<String, Object>) returnR.getData();
        if ((Integer) esDataMap.get("EV_STATUS") == 0) {
            throw new ValidationException((String) esDataMap.get("EV_MESSAGE"));
        }

        //将数据封装成零部件标签打印vo实体中
        List<Map<String, Object>> returnMapList = (List<Map<String, Object>>) esDataMap.get("ET_DATA");

        List<PartsLabelPrintVo> voList = new ArrayList<>();

        for (Map<String, Object> map : returnMapList) {
            PartsLabelPrintVo vo = new PartsLabelPrintVo();
            vo.setItem((String) map.getOrDefault("MATNR", ""));
            vo.setItemDesc((String) map.getOrDefault("MAKTX_ZH", ""));
            vo.setEnglishName((String) map.getOrDefault("MAKTX_EN", ""));
            vo.setMode((String) map.getOrDefault("WRKST", ""));
            vo.setSpStorageLocationPosition((String) map.getOrDefault("ZSPCWH", ""));
            vo.setMapNumber((String) map.getOrDefault("ZEINR", ""));
            vo.setMinimumPackageNumber(new BigDecimal(map.getOrDefault("SCMNG", 0).toString()).intValue());
            vo.setSalePrice(new BigDecimal((String) map.getOrDefault("KBETR", 0)));
            vo.setQty(new BigDecimal(0));
            vo.setOrderNo("");
            voList.add(vo);
        }

        return voList;
    }
}
