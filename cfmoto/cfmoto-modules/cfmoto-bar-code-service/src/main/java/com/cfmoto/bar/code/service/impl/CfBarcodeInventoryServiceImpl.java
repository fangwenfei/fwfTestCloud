package com.cfmoto.bar.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.mapper.CfBarcodeInventoryMapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfKtmReceivingOrder;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.model.vo.EnginePutVo;
import com.cfmoto.bar.code.model.vo.JustInTimeInventoryPrintVo;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.service.*;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.github.pig.common.vo.UserVO;
import com.google.gson.JsonObject;
import com.xiaoleilu.hutool.util.StrUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 条形码库存表 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-05
 */
@Service
public class CfBarcodeInventoryServiceImpl extends ServiceImpl<CfBarcodeInventoryMapper, CfBarcodeInventory> implements ICfBarcodeInventoryService {

    @Autowired
    private ICfStorageLocationService iCfStorageLocationService;

    @Autowired
    private ICfCftRelationshipService iCfCftRelationshipService;

    @Autowired
    private ICfKtmReceivingOrderService ktmReceivingOrderService;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private CfBarcodeInventoryMapper cfBarcodeInventoryMapper;

    @Autowired
    private ICfNextNumberService iCfNextNumberService;

    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;

    private Logger logger = LoggerFactory.getLogger(CfBarcodeInventoryServiceImpl.class);

    /**
     * 发送机关联入库
     *
     * @param userId
     * @param enginePutVo
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public ProductionTaskVo engineWarehousing(int userId, EnginePutVo enginePutVo) throws Exception {
        //engineBarCode发送机条码 条码规则：车型+“ ”+1位年+1位月+6位流水码
     /*   Pattern pattern = Pattern.compile( "^.+ \\d.\\w{6}" );
        Matcher matcher = pattern.matcher( enginePutVo.getBarCode() );
        if (!matcher.matches()) {
            throw new Exception("发动机条码不符合条码规则，条码规则：车型+1位空格+1位年+1位月+6位流水码");
        }*/
        String[] engineBarCodes = enginePutVo.getBarCode().split(" ");
        //车型
        String carType = engineBarCodes[0].trim();
        String codes = engineBarCodes[1].trim();
        if (!StrUtil.isBlank(enginePutVo.getSaleOrder())) {
            if (!codes.substring(0, 1).equalsIgnoreCase(enginePutVo.getOrderYear())) {
                throw new Exception("发动机年份信息与订单不符，无法入库");
            }
        }
        //验证发动机车型是否更生产任务单车型一致
        if (!StrUtil.equals(carType, enginePutVo.getCarType())) {
            throw new Exception("车型信息与任务单对应车型不一致");
        }

        //验证仓库是否存在
        EntityWrapper<CfStorageLocation> entityWrapper = new EntityWrapper<CfStorageLocation>();
        entityWrapper.eq("warehouse", enginePutVo.getStorageLocation());
        List<CfStorageLocation> cfStorageLocationList = iCfStorageLocationService.selectList(entityWrapper);
        if (cfStorageLocationList.size() <= 0) {
            throw new Exception("仓库" + enginePutVo.getStorageLocation() + "不存在");
        }

        //验证车型是否存在
      /*  EntityWrapper<CfCftRelationship> cfCftRelationshipEntityWrapper = new EntityWrapper<CfCftRelationship>();
        cfCftRelationshipEntityWrapper.eq("car_type", carType);
        List<CfCftRelationship> cfCftRelationshipList = iCfCftRelationshipService.selectList(cfCftRelationshipEntityWrapper);
        if (cfCftRelationshipList.size() <= 0) {
            throw new Exception( "车型" + carType + "不存在" );
        }*/

        //验证库存是否已入库
        EntityWrapper<CfBarcodeInventory> barcodeWrapperEntity = new EntityWrapper<CfBarcodeInventory>();
        CfBarcodeInventory barEntity = new CfBarcodeInventory();
        barEntity.setBarcode(enginePutVo.getBarCode());
        barcodeWrapperEntity.setEntity(barEntity);
        List<CfBarcodeInventory> cfBarcodeInventoryList = cfBarcodeInventoryMapper.selectList(barcodeWrapperEntity);
        if (cfBarcodeInventoryList.size() > 0) {
            throw new Exception("发动机" + enginePutVo.getBarCode() + "已入库");
        }

        CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
        cfBarcodeInventory.setBarcode(enginePutVo.getBarCode());
        cfBarcodeInventory.setBarcodeType("EG");
        cfBarcodeInventory.setMaterialsNo(enginePutVo.getItem());
        cfBarcodeInventory.setMaterialsName(enginePutVo.getItemDesc());
        cfBarcodeInventory.setWarehouse(enginePutVo.getStorageLocation());
        cfBarcodeInventory.setBarCodeNumber(new BigDecimal(1));
        cfBarcodeInventory.setProductionTaskOrder(enginePutVo.getTaskNo());
        cfBarcodeInventory.setMode(enginePutVo.getMode());
        cfBarcodeInventory.setCarModel(enginePutVo.getCarType());
        cfBarcodeInventory.setBatchNo(iCfNextNumberService.generateNextNumber("BATCH_NO"));
        cfBarcodeInventory.setObjectSetBasicAttribute(userId, new Date());
        cfBarcodeInventory.setSalesItem(enginePutVo.getSaleOrderRowItem());
        cfBarcodeInventory.setContractNo(enginePutVo.getContract());
        cfBarcodeInventoryMapper.insert(cfBarcodeInventory);

        //发送ERP生产订单数据
        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("AUFNR", enginePutVo.getTaskNo());
        paramMap.put("MATNR", enginePutVo.getItem());
        paramMap.put("TMLX", "EG");
        paramMap.put("GERNR", enginePutVo.getBarCode());
        paramMap.put("CHARG", cfBarcodeInventory.getBatchNo());
        paramMap.put("ERFMG", cfBarcodeInventory.getBarCodeNumber().toString());
        paramMap.put("LGORT", enginePutVo.getStorageLocation());
        Map<String, Object> paramNameMap = new HashMap<String, Object>();
        paramNameMap.put("IS_DATA", paramMap);
        callParamMap.put(HandleRefConstants.PARAM_MAP, paramNameMap);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_005");
        R returnR = sapFeignService.executeJcoFunction(callParamMap);
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> esDataMap = (Map<String, Object>) returnR.getData();
        if ((Integer) esDataMap.get("EV_STATUS") == 0) {
            throw new Exception((String) esDataMap.get("EV_MESSAGE"));
        }
        Map<String, Object> reDataMap = (Map<String, Object>) esDataMap.get("ES_DATA");
        //AUFNR==生产订单 DAUAT==订单类型 MATNR==物料代码 MAKTX==物料名称 PSMNG==任务单数量 WEMNG==已入库数量 FERTH==车型 WRKST==产品规格 KDAUF==销售订单
        //KDPOS==销售订单行项目 KUNNR==客户 ZTEXT==销售订单年份 ZHTH==合同号 LGPRO==生产仓储地点
        ProductionTaskVo productionTaskVo = new ProductionTaskVo();
        productionTaskVo.setTaskNo((String) reDataMap.get("AUFNR"));
        productionTaskVo.setOrderType((String) reDataMap.get("DAUAT"));
        productionTaskVo.setItem((String) reDataMap.get("MATNR"));
        productionTaskVo.setItemDesc((String) reDataMap.get("MAKTX"));
        productionTaskVo.setQuantity(new BigDecimal(reDataMap.get("PSMNG").toString()));
        productionTaskVo.setReceivedQty(new BigDecimal(reDataMap.get("WEMNG").toString()));
        productionTaskVo.setCarType((String) reDataMap.get("FERTH"));
        productionTaskVo.setMode((String) reDataMap.get("WRKST"));
        productionTaskVo.setSaleOrder((String) reDataMap.get("KDAUF"));
        productionTaskVo.setSaleOrderRowItem((String) reDataMap.get("KDPOS"));
        productionTaskVo.setCustomer((String) reDataMap.get("KUNNR"));
        productionTaskVo.setSaleOrderYear((String) reDataMap.get("ZTEXT"));
        productionTaskVo.setContract((String) reDataMap.get("ZHTH"));
        productionTaskVo.setStorageLocation((String) reDataMap.get("LGPRO"));
        return productionTaskVo;

    }

    /**
     * 根据barcode查询条形码库存表
     * 有则返回数据
     * 未查到则返回提示信息"条码不正确，请注意！"
     *
     * @param barCode 条码
     * @return R
     */
    @Override
    public R<CfBarcodeInventory> getByBarcode(String barCode) throws Exception {
        //首先根据条码去ktm表中查询数据
        CfKtmReceivingOrder ktm = ktmReceivingOrderService.selectOne(new EntityWrapper<CfKtmReceivingOrder>().eq("frame_no", barCode));

        //根据条码去条码库存表中查询数据
        CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>().eq("barcode", barCode));

        //校验ktm和条码库存表：不能都为空或都不为空
        if ((ktm == null && barcodeInventory == null) || (ktm != null && barcodeInventory != null)) {
            throw new Exception("条码有误，请注意!!!");
        }

        //返回ktm或条码表的数据
        if (ktm != null) {
            CfBarcodeInventory tempBarcodeInventory = new CfBarcodeInventory();
            tempBarcodeInventory.setBarcode(StringUtils.trimToEmpty(ktm.getFrameNo()));
            tempBarcodeInventory.setBarCodeNumber(ktm.getBarCodeNumber() == null ? new BigDecimal(0) : ktm.getBarCodeNumber());
            tempBarcodeInventory.setMaterialsNo(StringUtils.trimToEmpty(ktm.getMaterialsNo()));
            tempBarcodeInventory.setMaterialsName(StringUtils.trimToEmpty(ktm.getMaterialsName()));
            tempBarcodeInventory.setWarehouse(StringUtils.trimToEmpty(ktm.getRepository()));
            tempBarcodeInventory.setStorageArea("");
            tempBarcodeInventory.setWarehousePosition("");
            tempBarcodeInventory.setBarcodeType("KTM");
            return new R<>(tempBarcodeInventory);
        } else {
            return new R<>(barcodeInventory);
        }

    }


    /**
     * 根据条形码更新条形码数量
     * 修改只能比之前的数量少，不能比以前的数量多
     *
     * @param barcode       条码
     * @param barcodeNumber 修改的条码数量
     * @return success/fail
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<CfBarcodeInventory> updateNumberByBarcode(String barcode, Integer barcodeNumber, Integer userId) throws Exception {

        //首先校验条码是否存在
        R<CfBarcodeInventory> result = this.getByBarcode(barcode);

        CfBarcodeInventory barcodeInventory = result.getData();

        //判断条码类型
        String barcodeType = barcodeInventory.getBarcodeType();
        switch (barcodeType) {
            case "KTM": {
                //修改数量只能为0或1
                if (barcodeNumber > 1) {
                    throw new Exception(barcodeType + "条码的修改数量不能大于1,请注意!!!");
                }
                //修改ktm表中条码数量
                CfKtmReceivingOrder ktm = ktmReceivingOrderService.selectOne(new EntityWrapper<CfKtmReceivingOrder>().eq("frame_no", barcode));
                ktm.setBarCodeNumber(new BigDecimal(barcodeNumber));
                ktm.setBasicAttributeForUpdate(userId, new Date());
                ktmReceivingOrderService.updateById(ktm);
                return this.getByBarcode(barcode);
            }
            case "EG":
            case "CP": {
                //修改数量只能为0或1
                if (barcodeNumber > 1) {
                    throw new Exception(barcodeType + "条码的修改数量不能大于1,请注意!!!");
                }

                barcodeInventory.setBarCodeNumber(new BigDecimal(barcodeNumber));
                barcodeInventory.setBasicAttributeForUpdate(userId, new Date());
                barcodeInventoryService.updateById(barcodeInventory);
                return this.getByBarcode(barcode);
            }
            case "OT": {
                //条码数量只能改小
                BigDecimal oldBarcodeNumber = barcodeInventory.getBarCodeNumber();
                BigDecimal newBarcodeNumber = new BigDecimal(barcodeNumber);
                if (newBarcodeNumber.compareTo(oldBarcodeNumber) == 1) {
                    throw new Exception(barcodeType + "条码的修改数量不能大于当前数量,请注意!!!");
                }
                barcodeInventory.setBarCodeNumber(new BigDecimal(barcodeNumber));
                barcodeInventory.setBasicAttributeForUpdate(userId, new Date());
                barcodeInventoryService.updateById(barcodeInventory);
                return this.getByBarcode(barcode);
            }
            default: {
                throw new Exception("条码类型有误,请注意!!!");
            }

        }

    }


    /**
     * 修改减少条码数量
     *
     * @param userId
     * @param barcode
     * @param qty
     */
    @Override
    public void reduceInventoryQtyByBarcode(int userId, String barcode, BigDecimal qty) {

        cfBarcodeInventoryMapper.reduceInventoryQtyByBarcode(userId, barcode, qty, new Date());
    }

    /**
     * 批量减少库存数量
     *
     * @param mapList
     */
    @Override
    public void reduceInventoryQtyByBarcodeList(List mapList) {

        cfBarcodeInventoryMapper.reduceInventoryQtyByBarcodeList(mapList);
    }

    @Override
    public Page pageByLikeParam(Map<String, Object> params, CfBarcodeInventory cfBarcodeInventory) {
        return this.selectPage(new QueryPage<>(params), new EntityWrapper<CfBarcodeInventory>()
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getBarcode()), CfBarcodeInventory.CF_BARCODE_INVENTORY_BARCODE_SQL, cfBarcodeInventory.getBarcode())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getMaterialsNo()), CfBarcodeInventory.CF_BARCODE_INVENTORY_MATERIALS_NO_SQL, cfBarcodeInventory.getMaterialsNo())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getMaterialsName()), CfBarcodeInventory.CF_BARCODE_INVENTORY_MATERIALS_NAME_SQL, cfBarcodeInventory.getMaterialsName())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getBatchNo()), CfBarcodeInventory.CF_BARCODE_INVENTORY_BATCH_NO_SQL, cfBarcodeInventory.getBatchNo())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getWarehouse()), CfBarcodeInventory.CF_BARCODE_INVENTORY_WAREHOUSE_SQL, cfBarcodeInventory.getWarehouse())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getStorageArea()), CfBarcodeInventory.CF_BARCODE_INVENTORY_STORAGE_AREA_SQL, cfBarcodeInventory.getStorageArea())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getWarehousePosition()), CfBarcodeInventory.CF_BARCODE_INVENTORY_WAREHOUSE_POSITION_SQL, cfBarcodeInventory.getWarehousePosition())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getSuppler()), CfBarcodeInventory.CF_BARCODE_INVENTORY_SUPPLER_SQL, cfBarcodeInventory.getSuppler())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getFactory()), "factory", cfBarcodeInventory.getFactory())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getWarehouse()), "warehouse", cfBarcodeInventory.getWarehouse())
                .like(StringUtils.isNotBlank(cfBarcodeInventory.getStorageArea()), "storage_area", cfBarcodeInventory.getStorageArea())
        );
    }

    @Override
    public R getSapDataByParam(Map<String, Object> params) throws Exception {
        String IV_WERKS = params.getOrDefault("IV_WERKS", "").toString();
        String IV_LGORT = params.getOrDefault("IV_LGORT", "").toString();
        String IV_MATNR = params.getOrDefault("IV_MATNR", "").toString();
        //批号
        String IV_CHARG = params.getOrDefault("IV_CHARG", "").toString();
        //存储区域
        String IV_LGTYP = params.getOrDefault("IV_LGTYP", "").toString();

        Map<String, Object> paramMap = new HashedMap();
        paramMap.put("functionName", "ZMM_BC_020");
        Map<String, Object> dataMap = new HashedMap();

        dataMap.put("IV_WERKS", IV_WERKS);
        dataMap.put("IV_LGORT", IV_LGORT);

        //如果物料代码或批号或存储区域不为空，则传给sap，否则不传
        if (StringUtils.isNotBlank(IV_MATNR)) {
            dataMap.put("IV_MATNR", IV_MATNR);
        }

        if (StringUtils.isNotBlank(IV_CHARG)) {
            dataMap.put("IV_CHARG", IV_CHARG);
        }

        if (StringUtils.isNotBlank(IV_LGTYP)) {
            dataMap.put("IV_LGTYP", IV_LGTYP);
        }

        paramMap.put("paramMap", dataMap);
        R result = sapFeignService.executeJcoFunction(paramMap);
        Map<String, Object> resultMap = (Map<String, Object>) result.getData();
        List<Map<String, Object>> dataMapList = (List<Map<String, Object>>) resultMap.get("ET_DATA");


        List<Map<String, Object>> tempMapList = new ArrayList<>();

        for (Map<String, Object> map : dataMapList) {
            //获取sap返回的非限制库存
            int unLimitedInventoryNumber = new BigDecimal(map.getOrDefault("ZFXKC", 0).toString()).intValue();
            //获取sap返回的质检库存
            int qtInventoryNumber = new BigDecimal(map.getOrDefault("ZZJKC", 0).toString()).intValue();
            //获取sap返回的冻结库存
            int freezeInventoryNumber = new BigDecimal(map.getOrDefault("ZDJKC", 0).toString()).intValue();
            //获取sap返沪的调拨在途库存
            int allotOnWayInventoryNumber = new BigDecimal(map.getOrDefault("CUMLM", 0).toString()).intValue();
            //如果上面四个数量都为0，则不显示
            if ((unLimitedInventoryNumber + qtInventoryNumber + freezeInventoryNumber + allotOnWayInventoryNumber) != 0) {
                tempMapList.add(map);
            }
        }

        //将处理后的sap返回数据放入返回对象中
        resultMap.put("ET_DATA", tempMapList);

        return result;
    }


    /**
     * 拆分打印生产入库标签打印（非整车、发动机）（PC）
     *
     * @param params 参数
     * @param userId 用户id
     * @return list
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CfBarcodeInventory> splitDataProducePrintByParam(Map<String, Object> params, int userId) throws Exception {
        int splitNumber = Integer.parseInt(params.getOrDefault("splitNumber", "0").toString());
        int allCount = Integer.parseInt(params.getOrDefault("allCount", "0").toString());
        JSON paramss = (JSON) JSONObject.toJSON(params.getOrDefault("objectData", ""));
        ProductionTaskVo productionTaskVo = JSON.toJavaObject(paramss, ProductionTaskVo.class);
        Date thisDate = new Date();
        if (splitNumber <= 0 || allCount <= 0) {
            throw new Exception(CfBarcodeInventory.CF_BARCODE_SPLIT_NUMBER_PARAM_ERROR);
        }
        //取整
        int intNumber = allCount / splitNumber;
        //取余
        int lastNumber = allCount % splitNumber;
        List<CfBarcodeInventory> cfBarcodeInventoryList = new ArrayList<>();
        String hoseWare = productionTaskVo.getStorageLocation();
        String batchNoNextNumber = iCfNextNumberService.generateNextNumber(CfBarcodeInventory.BARCODE_BATCH_NO);
        for (int i = 0; i < intNumber; i++) {
            String NextNumber = iCfNextNumberService.generateNextNumber(CfBarcodeInventory.BARCODE_IS_TYPE_NO_CP);
            CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(NextNumber); //条码
            cfBarcodeInventory.setBarcodeType(CfBarcodeInventory.BARCODE_TYPE_OT);//条码类型
            cfBarcodeInventory.setState(CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER);//状态
            cfBarcodeInventory.setBarCodeNumber(new BigDecimal(splitNumber));//条码数量
            cfBarcodeInventory.setMaterialsNo(productionTaskVo.getItem());//获取物料代码
            cfBarcodeInventory.setMaterialsName(productionTaskVo.getItemDesc());//获取物料描述
            cfBarcodeInventory.setBatchNo(batchNoNextNumber);//批次号
            cfBarcodeInventory.setMode(productionTaskVo.getMode());//规格型号
            cfBarcodeInventory.setFactory(productionTaskVo.getFactory());//工厂
            cfBarcodeInventory.setWarehouse(hoseWare);
            cfBarcodeInventory.setProductionTaskOrder(productionTaskVo.getTaskNo());
            cfBarcodeInventory.setCarModel(productionTaskVo.getCarType());
            cfBarcodeInventory.setSaleOrderNo(productionTaskVo.getSaleOrder());
            cfBarcodeInventory.setSalesItem(productionTaskVo.getSaleOrderRowItem());
            cfBarcodeInventory.setContractNo(productionTaskVo.getContract());
            cfBarcodeInventory.setPrintingDate(thisDate);//打印时间
            cfBarcodeInventory.setPrintingBy(userId);//打印人员
            cfBarcodeInventory.setBasicAttributeForUpdate(userId, thisDate);
            cfBarcodeInventoryList.add(cfBarcodeInventory);
        }
        if (lastNumber > 0) {
            String NextNumber = iCfNextNumberService.generateNextNumber(CfBarcodeInventory.BARCODE_IS_TYPE_NO_CP);
            CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(NextNumber); //条码
            cfBarcodeInventory.setBarcodeType(CfBarcodeInventory.BARCODE_TYPE_OT);//条码类型
            cfBarcodeInventory.setState(CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER);//状态
            cfBarcodeInventory.setBarCodeNumber(new BigDecimal(lastNumber));//条码数量
            cfBarcodeInventory.setMaterialsNo(productionTaskVo.getItem());//获取物料代码
            cfBarcodeInventory.setMaterialsName(productionTaskVo.getItemDesc());//获取物料描述
            cfBarcodeInventory.setBatchNo(batchNoNextNumber);//批次号
            cfBarcodeInventory.setMode(productionTaskVo.getMode());//规格型号
            cfBarcodeInventory.setFactory(productionTaskVo.getFactory());//工厂
            cfBarcodeInventory.setWarehouse(hoseWare);
            cfBarcodeInventory.setProductionTaskOrder(productionTaskVo.getTaskNo());
            cfBarcodeInventory.setSaleOrderNo(productionTaskVo.getSaleOrder());
            cfBarcodeInventory.setSalesItem(productionTaskVo.getSaleOrderRowItem());
            cfBarcodeInventory.setContractNo(productionTaskVo.getContract());
            cfBarcodeInventory.setCarModel(productionTaskVo.getCarType());
            cfBarcodeInventory.setPrintingDate(thisDate);//打印时间
            cfBarcodeInventory.setPrintingBy(userId);//打印人员
            cfBarcodeInventory.setBasicAttributeForUpdate(userId, thisDate);
            cfBarcodeInventoryList.add(cfBarcodeInventory);
        }
        this.insertBatch(cfBarcodeInventoryList);
        return cfBarcodeInventoryList;
    }

    @Override
    public List<CfBarcodeInventory> splitDataProducePrintByParamCP(Map<String, Object> params, int userId) throws Exception {
        int intNumber = Integer.parseInt(params.getOrDefault("allCount", "0").toString());
        JSON paramss = (JSON) JSONObject.toJSON(params.getOrDefault("objectData", ""));
        ProductionTaskVo productionTaskVo = JSON.toJavaObject(paramss, ProductionTaskVo.class);
        Date thisDate = new Date();
        List<CfBarcodeInventory> cfBarcodeInventoryList = new ArrayList<>();
        String hoseWare = productionTaskVo.getStorageLocation();
        for (int i = 0; i < intNumber; i++) {
            String NextNumber = iCfNextNumberService.generateNextNumber(CfBarcodeInventory.BARCODE_IS_TYPE_CP);
            CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(NextNumber); //条码
            cfBarcodeInventory.setBarcodeType(CfBarcodeInventory.BARCODE_TYPE_CP);//条码类型
            cfBarcodeInventory.setState(CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER);//状态
            cfBarcodeInventory.setBarCodeNumber(new BigDecimal(1));//条码数量
            cfBarcodeInventory.setMaterialsNo(productionTaskVo.getItem());//获取物料代码
            cfBarcodeInventory.setMaterialsName(productionTaskVo.getItemDesc());//获取物料描述
            cfBarcodeInventory.setMode(productionTaskVo.getMode());//规格型号
            cfBarcodeInventory.setFactory(productionTaskVo.getFactory());//工厂
            cfBarcodeInventory.setWarehouse(hoseWare);
            cfBarcodeInventory.setCarModel(productionTaskVo.getCarType());
            cfBarcodeInventory.setProductionTaskOrder(productionTaskVo.getTaskNo());
            cfBarcodeInventory.setSaleOrderNo(productionTaskVo.getSaleOrder());
            cfBarcodeInventory.setSalesItem(productionTaskVo.getSaleOrderRowItem());
            cfBarcodeInventory.setContractNo(productionTaskVo.getContract());
            cfBarcodeInventory.setPrintingDate(thisDate);//打印时间
            cfBarcodeInventory.setPrintingBy(userId);//打印人员
            cfBarcodeInventory.setBasicAttributeForUpdate(userId, thisDate);
            cfBarcodeInventoryList.add(cfBarcodeInventory);
        }
        this.insertBatch(cfBarcodeInventoryList);
        return cfBarcodeInventoryList;
    }

    @Override
    public List<CfBarcodeInventory> getInventoryFromSap(String materialsNo, String warehouse, Integer userId, String currentUrl) throws Exception {
        //根据用户id获取用户对应的工厂
        UserVO user = userFeignService.user(userId);

        //从sap获取即时库存信息
        //传入的仓库为空则获取用户仓库，不为空则获取传入的仓库
        List<Map<String, Object>> mapList = sapApiService.newGetDataFromSapApi10(StrUtil.isBlank(warehouse) ? user.getWarehouse() : warehouse, materialsNo, user.getSite(), currentUrl);
        List<CfBarcodeInventory> barcodeInventoryList = new ArrayList<>();
        //将sap获取的库存信息放入与表结构对应的实体类CfBarcodeInventory的集合中
        for (Map<String, Object> map : mapList) {
            CfBarcodeInventory barcodeInventory = new CfBarcodeInventory();
            barcodeInventory.setMaterialsName((String) map.getOrDefault("materialsName", ""));
            barcodeInventory.setMaterialsNo((String) map.getOrDefault("materialsNo", ""));
            barcodeInventory.setBatchNo((String) map.getOrDefault("batchNo", ""));
            barcodeInventory.setWarehouse((String) map.getOrDefault("wareHouse", ""));
            barcodeInventory.setStorageArea((String) map.getOrDefault("storageArea", ""));
            barcodeInventory.setWarehousePosition((String) map.getOrDefault("warehousePosition", ""));
            barcodeInventory.setMode((String) map.getOrDefault("spec", ""));
            barcodeInventory.setBarCodeNumber(new BigDecimal(map.getOrDefault("batchNumber", 0).toString()));
            barcodeInventory.setFreezeInventoryNumber(new BigDecimal(map.getOrDefault("freezeInventoryNumber", 0).toString()));
            barcodeInventoryList.add(barcodeInventory);
        }

        return barcodeInventoryList;
    }
}
