package com.cfmoto.bar.code.service.impl.stock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.mapper.*;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.model.vo.CfStockSplitVo;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.ICfStockInventoryService;
import com.cfmoto.bar.code.service.ICfStockScanLineService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.service.ICfStockSplitService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.github.pig.common.vo.UserVO;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 备料扫描记录 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-12
 */
@Service
public class CfStockScanLineServiceImpl extends ServiceImpl<CfStockScanLineMapper, CfStockScanLine> implements ICfStockScanLineService {

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    CfStockListInfoMapper cfStockListInfoMapper;

    @Autowired
    CfStockInventoryMapper cfStockInventoryMapper;

    @Autowired
    CfKtmReceivingOrderMapper cfKtmReceivingOrderMapper;

    @Autowired
    CfBarcodeInventoryMapper cfBarcodeInventoryMapper;

    @Autowired
    SapFeignService sapFeignService;

    @Autowired
    ICfStockInventoryService ICfStockInventoryService;

    @Autowired
    private ICfNextNumberService cfNextNumberService;

    @Autowired
    ICfStockSplitService iCfStockSplitService;

    @Override
    public UserVO user(Integer id) {
        return userFeignService.user(id);
    }


    /***
     * 先检查后台备料信息表里是否存在数据
     * @param userId
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getDataByStockListNo(int userId, Map<String, Object> params) throws Exception {
        //通过stockListNo查询单据头
        String stockListNo = params.getOrDefault("stockListNo", "").toString();
        String stockFunctionTypeStr = params.getOrDefault("stockFunctionType", "").toString();
        CfStockListInfo cfStockListInfoRoot = new CfStockListInfo();
        cfStockListInfoRoot.setStockListNo(stockListNo);
        cfStockListInfoRoot = cfStockListInfoMapper.selectOne(cfStockListInfoRoot);
        Map<String, Object> resultMap = new HashedMap();
        Date newDate = new Date();

        // TODO SAP 接口获取数据 备料单传输接口
        Map<String, Object> paramMap = new HashedMap();
        paramMap.put("functionName", "ZMM_BC_027");
        Map<String, Object> dataMap = new HashedMap();
        dataMap.put("IV_ZLIST", stockListNo);
        paramMap.put("paramMap", dataMap);
        R<Map<String, Object>> result = sapFeignService.executeJcoFunction(paramMap);
        if (result == null) {
            throw new ValidateCodeException("SAP调用服务异常");
        }
        if (result.getCode() != 0) {
            throw new ValidateCodeException(result.getMsg());
        }
        Map<String, Object> resultMapData = result.getData();
        JSONObject jsonObject = new JSONObject(resultMapData);
        if (!jsonObject.getString("EV_STATUS").equals("1")) {
            throw new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }
        //验证是否是当前界面
        String stockFunctionTypeName = jsonObject.getString("EV_ZBLLX");
        String stockFunctionType = jsonObject.getString("EV_BLLXBM");
        if (!stockFunctionTypeStr.equals(stockFunctionType)) {
            throw new ValidateCodeException(CfStockScanLine.EX_FUNCTION_JUDGE + stockFunctionTypeName);
        }
        //判断是否是可进行
        if (!CfStockScanLine.STATUS_UNCOMPLETE.equals(jsonObject.getString("EV_ZSTATUS"))) {
            throw new ValidateCodeException(CfStockScanLine.EX_STATUS_JUDGE);
        }
        //如果头数据为空
        if (cfStockListInfoRoot == null) {
            //添加根信息
            cfStockListInfoRoot = new CfStockListInfo();
            cfStockListInfoRoot.setStockListNo(stockListNo);
            cfStockListInfoRoot.setObjectSetBasicAttribute(userId, newDate);
            cfStockListInfoRoot.setStockFunctionType(stockFunctionType);
            cfStockListInfoRoot.setStockFunctionTypeName(stockFunctionTypeName);

            // ET_DATA
            JSONArray jsonArray = jsonObject.getJSONArray("ET_DATA");
            List<CfStockInventory> cfStockInventoryList = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject etData = jsonArray.getJSONObject(i);
                if (i == 0) {
                    String stockRepository = etData.getString("LGORT");
                    cfStockListInfoRoot.setStockRepository(stockRepository);
                    cfStockListInfoMapper.insert(cfStockListInfoRoot);
                }
                CfStockInventory cfStockInventory = new CfStockInventory();
                cfStockInventory.setObjectSetBasicAttribute(userId, newDate);
                cfStockInventory.setStockListId(cfStockListInfoRoot.getStockListId());
                cfStockInventory.setStockListNo(stockListNo);
                cfStockInventory.setMaterialsNo(etData.getString("MATNR"));//物料编号
                cfStockInventory.setMaterialsName(etData.getString("MAKTX"));//物料名称
                cfStockInventory.setSpec(etData.getString("WRKST"));//规格型号
                cfStockInventory.setRepository(etData.getString("LGPRO"));//发货仓库
                cfStockInventory.setStorageArea(etData.getString("LGTYP"));//存储区域
                cfStockInventory.setShouldSendNumber(etData.getBigDecimal("BDMNG"));//数量
                cfStockInventory.setActualSendNumber(etData.getBigDecimal("BDMNG").subtract(etData.getBigDecimal("ZBLWQSL")));//未清数量
                cfStockInventoryList.add(cfStockInventory);
                /* cfStockInventoryMapper.insert(cfStockInventory);*/
            }
            ICfStockInventoryService.insertBatch(cfStockInventoryList);
        }

        if (!stockFunctionTypeStr.equals(cfStockListInfoRoot.getStockFunctionType())) {
            throw new ValidateCodeException(CfStockScanLine.EX_FUNCTION_JUDGE + cfStockListInfoRoot.getStockFunctionTypeName());
        }
        int current = 1;
        int size = QueryPage.LIMIT_10000;
        //获取已扫描数据
        Page<CfStockScanLine> linePage = new Page<>(current, size, CfStockScanLine.STOCK_LINE_ID_SQL, false);
        Page cfStockScanLinePage = this.selectPage(linePage, new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL, cfStockListInfoRoot.getStockListId()).eq(CfStockScanLine.CREATED_BY_SQL, userId));

        //通过单据头获取汇总数据
        Page<CfStockInventory> cfStockInventoryHeaderPage = new Page<>(current, size);
        // CfStockInventory
        RowBounds rowBounds = new RowBounds(0, size);
        Wrapper entityWrapper = new EntityWrapper<CfStockInventory>()
                .eq(CfStockScanLine.STOCK_LIST_ID_SQL, cfStockListInfoRoot.getStockListId())
                // TODO .eq(CfStockScanLine.PARAMS_REPOSITORY,userVO.getWarehouse())
                .orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false);
                 /*   TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
                        entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
                    }*/
        List<CfStockInventory> cfStockInventoryList = cfStockInventoryMapper.selectPage(rowBounds, entityWrapper);
        cfStockInventoryHeaderPage.setRecords(cfStockInventoryList);
        //将数据封装到界面
        resultMap.put("cfStockListInfoRoot", cfStockListInfoRoot);
        resultMap.put("cfStockInventoryHeaderPage", cfStockInventoryHeaderPage);
        resultMap.put("cfStockScanLinePage", cfStockScanLinePage);

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addScanLineData(int userId, Map<String, Object> params) throws Exception {

        String barCodeNo = params.getOrDefault("barCodeNo", "").toString();
        String stockListIdSt = params.getOrDefault("stockListId", "").toString();
        String checkRecord = params.getOrDefault("checkRecord", "N").toString();
        Date thisNewDate = new Date();
        if (!StringUtils.isNotBlank(barCodeNo)) {
            throw new ValidateCodeException(CfStockScanLine.EX_BAR_CODE_NO);
        }
        if (!StringUtils.isNotBlank(stockListIdSt)) {
            throw new ValidateCodeException(CfStockScanLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        //判断条码是否被重复扫描
        int count = this.selectCount(new EntityWrapper<CfStockScanLine>().eq(CfStockScanLine.PARAMS_BARCODE, barCodeNo));
        if (count > 0) {
            throw new ValidateCodeException(CfStockScanLine.EX_BAR_CODE_DOUBLE);
        }

        //获取用户的绑定的厂库
        UserVO userVO = userFeignService.user(userId);
        int stockListId = Integer.parseInt(stockListIdSt);
        CfKtmReceivingOrder cfKtmReceivingOrder = new CfKtmReceivingOrder();
        cfKtmReceivingOrder.setFrameNo(barCodeNo);
        cfKtmReceivingOrder = cfKtmReceivingOrderMapper.selectOne(cfKtmReceivingOrder);


        if (cfKtmReceivingOrder != null) {
            //判断条码数量是否还有
            if (cfKtmReceivingOrder.getBarCodeNumber().compareTo(new BigDecimal(1)) < 0) {
                throw new ValidateCodeException(CfStockScanLine.EX_BAR_CODE_NUMBER);
            }
            //拿取销售发货单数据
            CfStockInventory cfStockInventoryHeader = new CfStockInventory();
            cfStockInventoryHeader.setMaterialsNo(cfKtmReceivingOrder.getMaterialsNo());
            cfStockInventoryHeader.setMaterialsName(cfKtmReceivingOrder.getMaterialsName());
            cfStockInventoryHeader.setStockListId(stockListId);
            //加入人员的过滤
           /* TODO cfStockInventoryHeader.setRepository(userVO.getWarehouse());
            if(StringUtils.isNotBlank(userVO.getStorageArea())){
                cfStockInventoryHeader.setStorageArea(userVO.getStorageArea());
            }*/
            cfStockInventoryHeader = cfStockInventoryMapper.selectOne(cfStockInventoryHeader);
            if (cfStockInventoryHeader == null) {
                throw new ValidateCodeException(CfStockScanLine.EX_MATERIALS_NOT_HAVING);
            }
           /* TODO if(!cfStockInventoryHeader.getRepository().equals(userVO.getWarehouse())){
                throw  new ValidateCodeException(CfStockScanLine.EX_MATERIALS_NOT_HAVING);
            }*/
            BigDecimal scanningNumber = cfStockInventoryHeader.getActualSendNumber().add(new BigDecimal(1));
            if (scanningNumber.compareTo(cfStockInventoryHeader.getShouldSendNumber()) > 0) {
                throw new ValidateCodeException(CfStockScanLine.EX_BARCODE_NUMBER);
            }
            cfStockInventoryHeader.setActualSendNumber(scanningNumber);
            cfStockInventoryHeader.setLastUpdatedBy(userId);
            cfStockInventoryHeader.setLastUpdatedDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfStockInventoryMapper.updateById(cfStockInventoryHeader);
            //加入已扫描条码
            CfStockScanLine cfStockScanLine = new CfStockScanLine();
            cfStockScanLine.setObjectSetBasicAttribute(userId, thisNewDate);
            cfStockScanLine.setStockListNo(cfStockInventoryHeader.getStockListNo());
            cfStockScanLine.setBarcode(barCodeNo);
            cfStockScanLine.setStockListId(stockListId);
            cfStockScanLine.setStockInventoryId(cfStockInventoryHeader.getStockInventoryId());
            cfStockScanLine.setMaterialsName(cfStockInventoryHeader.getMaterialsName());
            cfStockScanLine.setMaterialsNo(cfStockInventoryHeader.getMaterialsNo());
            cfStockScanLine.setNumber(new BigDecimal(1));
            cfStockScanLine.setBatchNo(cfKtmReceivingOrder.getBatchNo());
            cfStockScanLine.setBarcodeType(CfStockScanLine.BARCODE_TYPE_KTM);
            cfStockScanLine.setWarehousePosition(userVO.getWarehousePosition());
            cfStockScanLine.setOtherTableId(cfKtmReceivingOrder.getKtmReceivingId());
            this.insert(cfStockScanLine);
        } else {

            CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(barCodeNo);
            cfBarcodeInventory = cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
            if (cfBarcodeInventory == null) {
                throw new ValidateCodeException(CfStockScanLine.EX_BAR_CODE_NO_NOT_HAVING);
            }
            //取库存的数据类型
            String barcodeType = cfBarcodeInventory.getBarcodeType();

            //判断条码数量是否还有
            if (cfBarcodeInventory.getBarCodeNumber().compareTo(new BigDecimal(1)) < 0) {
                throw new ValidateCodeException(CfStockScanLine.EX_BAR_CODE_NUMBER);
            }
            //判断条码状态是否可用
            if (CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER.equals(cfBarcodeInventory.getState())) {
                throw new ValidateCodeException(CfStockScanLine.EX_BAR_CODE_CANT_USER);
            }
            //拿取销售发货单数据
            CfStockInventory cfStockInventoryHeader = new CfStockInventory();
            cfStockInventoryHeader.setMaterialsNo(cfBarcodeInventory.getMaterialsNo());
            cfStockInventoryHeader.setStockListId(stockListId);
            //加入人员的过滤
           /* TODO  cfStockInventoryHeader.setRepository(userVO.getWarehouse());
            if(StringUtils.isNotBlank(userVO.getStorageArea())){
                cfStockInventoryHeader.setStorageArea(userVO.getStorageArea());
            }*/
            cfStockInventoryHeader = cfStockInventoryMapper.selectOne(cfStockInventoryHeader);
            if ((cfStockInventoryHeader == null)) {
                throw new ValidateCodeException(CfStockScanLine.EX_MATERIALS_NOT_HAVING);
            }
          /* TODO  if(!cfStockInventoryHeader.getRepository().equals(userVO.getWarehouse())){
                throw  new ValidateCodeException(CfStockScanLine.EX_MATERIALS_NOT_HAVING);
            }*/

            BigDecimal scanningNumberAddOne = cfStockInventoryHeader.getActualSendNumber().add(new BigDecimal(1));
            //判断是否已经满仓
            if (scanningNumberAddOne.compareTo(cfStockInventoryHeader.getShouldSendNumber()) > 0) {
                throw new ValidateCodeException(CfStockScanLine.EX_BARCODE_NUMBER);
            }
            //添加数据
            BigDecimal scanningNumber = cfStockInventoryHeader.getActualSendNumber().add(cfBarcodeInventory.getBarCodeNumber());
            BigDecimal barCodeNumber = cfBarcodeInventory.getBarCodeNumber();
            //加入的数据是否超过满仓需求数量
            if (scanningNumber.compareTo(cfStockInventoryHeader.getShouldSendNumber()) > 0) {
                //如果大于就等于需求数量
                scanningNumber = cfStockInventoryHeader.getShouldSendNumber();
                //额外添加数量
                barCodeNumber = cfStockInventoryHeader.getShouldSendNumber().subtract(cfStockInventoryHeader.getActualSendNumber());
            }

            cfStockInventoryHeader.setActualSendNumber(scanningNumber);
            cfStockInventoryHeader.setLastUpdatedBy(userId);
            cfStockInventoryHeader.setLastUpdatedDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfStockInventoryMapper.updateById(cfStockInventoryHeader);
            //加入已扫描条码
            CfStockScanLine cfStockScanLine = new CfStockScanLine();
            cfStockScanLine.setObjectSetBasicAttribute(userId, thisNewDate);
            cfStockScanLine.setStockListNo(cfStockInventoryHeader.getStockListNo());
            cfStockScanLine.setBarcode(barCodeNo);
            cfStockScanLine.setStockListId(stockListId);
            cfStockScanLine.setStockInventoryId(cfStockInventoryHeader.getStockInventoryId());
            cfStockScanLine.setMaterialsName(cfStockInventoryHeader.getMaterialsName());
            cfStockScanLine.setMaterialsNo(cfStockInventoryHeader.getMaterialsNo());
            cfStockScanLine.setNumber(barCodeNumber);
            cfStockScanLine.setBatchNo(cfBarcodeInventory.getBatchNo());
            cfStockScanLine.setBarcodeType(barcodeType);
            cfStockScanLine.setRepository(cfBarcodeInventory.getWarehouse());
            cfStockScanLine.setWarehousePosition(cfBarcodeInventory.getWarehousePosition());
            cfStockScanLine.setStorageArea(cfBarcodeInventory.getStorageArea());
            cfStockScanLine.setOtherTableId(cfBarcodeInventory.getBarcodeInventoryId());
            this.insert(cfStockScanLine);
        }
        //获取更新过的数据
        Map<String, Object> resultMap = new HashedMap();
        int current = 1;
        int size = QueryPage.LIMIT_10000;
        //通过单据头获取汇总数据
        Page<CfStockScanLine> linePage = new Page<>(current, size);
        Page cfStockScanLinePage = this.selectPage(linePage, new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId).eq(CfStockScanLine.CREATED_BY_SQL, userId).orderBy(CfStockScanLine.STOCK_LINE_ID_SQL, false));
        //获取已扫描数据
        Page<CfStockInventory> cfStockInventoryHeaderPage = new Page<>(current, size);
        //设置查询条件
        Wrapper entityWrapper = new EntityWrapper<CfStockInventory>()
                .eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId);
        /*  TODO   .eq(CfStockScanLine.PARAMS_REPOSITORY,userVO.getWarehouse())*/
        if (CfStockScanLine.PARAMS_N.equals(checkRecord)) {
          /*  TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false);

        } else {
           /*TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false).addFilter("should_send_number>actual_send_number");
        }
        // CfStockInventory
        RowBounds rowBounds = new RowBounds(0, size);
        List<CfStockInventory> cfStockInventoryList = cfStockInventoryMapper.selectPage(rowBounds, entityWrapper);

        cfStockInventoryHeaderPage.setRecords(cfStockInventoryList);
        //将数据封装到界面
        resultMap.put("cfStockInventoryHeaderPage", cfStockInventoryHeaderPage);
        resultMap.put("cfStockScanLinePage", cfStockScanLinePage);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteDataByBarCodeNo(int userId, Map<String, Object> params) throws Exception {
        Integer stockLineId = Integer.parseInt(params.getOrDefault("stockLineId", "").toString());
        Integer stockListId = Integer.parseInt(params.getOrDefault("stockListId", "").toString());
        Integer stockInventoryId = Integer.parseInt(params.getOrDefault("stockInventoryId", "").toString());
        BigDecimal number = new BigDecimal(params.getOrDefault("number", "").toString());
        String checkRecord = params.getOrDefault("checkRecord", "N").toString();
        CfStockInventory cfStockInventory = cfStockInventoryMapper.selectById(stockInventoryId);
        cfStockInventory.setActualSendNumber(cfStockInventory.getActualSendNumber().subtract(number));
        cfStockInventoryMapper.updateById(cfStockInventory);
        this.deleteById(stockLineId);
        //获取更新过的数据
        Map<String, Object> resultMap = new HashedMap();
        int current = 1;
        int size = QueryPage.LIMIT_10000;
        //通过单据头获取汇总数据
        Page<CfStockScanLine> linePage = new Page<>(current, size, CfStockScanLine.STOCK_LINE_ID_SQL, false);
        Page cfStockScanLinePage = this.selectPage(linePage, new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId).eq(CfStockScanLine.CREATED_BY_SQL, userId).orderBy(CfStockScanLine.STOCK_LINE_ID_SQL, false));
        //获取已扫描数据
        Page<CfStockInventory> cfStockInventoryHeaderPage = new Page<>(current, size, CfStockScanLine.STOCK_LIST_ID_SQL, false);

        // CfStockInventory
        RowBounds rowBounds = new RowBounds(0, size);
        Wrapper entityWrapper = new EntityWrapper<CfStockInventory>()
                .eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId)
                /*. TODO eq(CfStockScanLine.PARAMS_REPOSITORY,userVO.getWarehouse());*/;
        if (CfStockScanLine.PARAMS_N.equals(checkRecord)) {
          /*  TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false);

        } else {
          /*  TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false).addFilter("should_send_number>actual_send_number");
        }

        List<CfStockInventory> cfStockInventoryList = cfStockInventoryMapper.selectPage(rowBounds, entityWrapper);

        cfStockInventoryHeaderPage.setRecords(cfStockInventoryList);
        //将数据封装到界面
        resultMap.put("cfStockInventoryHeaderPage", cfStockInventoryHeaderPage);
        resultMap.put("cfStockScanLinePage", cfStockScanLinePage);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitICfStockScanLineData(int userId, Map<String, Object> params) throws Exception {
        String stockListIdSt = params.getOrDefault("stockListId", "").toString();
        String printCheck = params.getOrDefault("printCheck", "N").toString();
        if (!StringUtils.isNotBlank(stockListIdSt)) {
            throw new ValidateCodeException(CfStockScanLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        int stockListId = Integer.parseInt(stockListIdSt);
        Date newDate = new Date();
        CfStockListInfo cfStockListInfoRoot = cfStockListInfoMapper.selectById(stockListId);
        //删除自己的数据
        List<CfStockScanLine> cfStockScanLineList = this.selectList(new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId).eq(CfStockScanLine.CREATED_BY_SQL, userId));
        //扣除数量以及sap数据封装
        CfKtmReceivingOrder cfKtmReceivingOrder = null;
        ArrayList<Map<String, String>> sapArrayData = new ArrayList<>();
        BigDecimal number0 = new BigDecimal(0);

        for (CfStockScanLine cfStockScanLine : cfStockScanLineList) {
            if (CfStockScanLine.BARCODE_TYPE_KTM.equals(cfStockScanLine.getBarcodeType())) {
                //修改ktm数据
                cfKtmReceivingOrder = new CfKtmReceivingOrder();
                cfKtmReceivingOrder.setBarCodeNumber(number0);
                cfKtmReceivingOrder.setKtmReceivingId(cfStockScanLine.getOtherTableId());
                cfKtmReceivingOrderMapper.updateById(cfKtmReceivingOrder);
            } else {
                //修改数据量
                CfBarcodeInventory cfBarcodeInventory = cfBarcodeInventoryMapper.selectById(cfStockScanLine.getOtherTableId());
                cfBarcodeInventory.setBarCodeNumber(cfBarcodeInventory.getBarCodeNumber().subtract(cfStockScanLine.getNumber()));
                cfBarcodeInventoryMapper.updateById(cfBarcodeInventory);
            }
            //TODO 需要sap的处理
            Map<String, String> tableMap = new HashedMap();
            tableMap.put("ZLIST", StringUtils.trimToEmpty(cfStockScanLine.getStockListNo()));//备料单号
            tableMap.put("MATNR", StringUtils.trimToEmpty(cfStockScanLine.getMaterialsNo()));//物料编号
            tableMap.put("MAKTX", StringUtils.trimToEmpty(cfStockScanLine.getMaterialsName()));//物料名称
            tableMap.put("WRKST", StringUtils.trimToEmpty(cfStockScanLine.getMode()));//规格型号
            tableMap.put("TMLX", StringUtils.trimToEmpty(cfStockScanLine.getBarcodeType()));//条码类型
            tableMap.put("GERNR", StringUtils.trimToEmpty(cfStockScanLine.getBarcode()));//条码
            tableMap.put("CHARG", StringUtils.trimToEmpty(cfStockScanLine.getBatchNo()));//批次
            tableMap.put("BDMNG", StringUtils.trimToEmpty(cfStockScanLine.getNumber().toPlainString()));//数量
            tableMap.put("LGPRO", StringUtils.trimToEmpty(cfStockScanLine.getRepository()));//发货仓库
            tableMap.put("LGTYP", StringUtils.trimToEmpty(cfStockScanLine.getStorageArea()));//存储区域
            tableMap.put("LGPLA", StringUtils.trimToEmpty(cfStockScanLine.getWarehousePosition()));//仓位
            tableMap.put("LGORT", StringUtils.trimToEmpty(cfStockListInfoRoot.getStockRepository()));//备料仓库
            sapArrayData.add(tableMap);
        }
        Map<String, Object> paramMapSap = new HashedMap();
        paramMapSap.put("functionName", "ZMM_BC_031");
        Map<String, Object> dataMap = new HashedMap();
        dataMap.put("IT_DATA", sapArrayData);
        dataMap.put("IV_ZLIST", cfStockListInfoRoot.getStockListNo());
        paramMapSap.put("paramMap", dataMap);
        R<Map<String, Object>> result = sapFeignService.executeJcoFunction(paramMapSap);
        if (result == null) {
            throw new ValidateCodeException("SAP调用服务异常");
        }
        if (result.getCode() != 0) {
            throw new ValidateCodeException(result.getMsg());
        }
        Map<String, Object> resultMapData = result.getData();
        JSONObject jsonObject = new JSONObject(resultMapData);
        if (!jsonObject.getString("EV_STATUS").equals("1")) {
            throw new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }


        Page cfStockInventoryHeaderPage = this.getCfStockInventoryPage(userId, params);
        List<CfStockSplit> cfStockSplitList = new ArrayList<>();
        if (printCheck.equals(CfStockScanLine.PARAMS_Y)) {


            List<CfStockScanLine> splitList = this.selectList(new EntityWrapper<CfStockScanLine>().
                    eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId)
                    .eq(CfStockScanLine.CREATED_BY_SQL, userId)
                    .setSqlSelect(" materials_no , materials_name ,batch_no,sum(number) number ,mode")
                    .groupBy("materials_no ,materials_name ,batch_no,mode")
                    .orderBy("materials_no")
            );
            List<CfStockSplitVo> cfStockSplitVoList = new ArrayList<>();
            String materialsNo = "MATERIALS_NO";
            BigDecimal bigDecimalNumber = new BigDecimal(0);
            for (int i = 0; i < splitList.size(); i++) {
                CfStockScanLine cfStockScanLineSplit = splitList.get(i);
                CfStockSplitVo cfStockSplitVo = new CfStockSplitVo();
                cfStockSplitVo.setMaterialsNo(cfStockScanLineSplit.getMaterialsNo());
                cfStockSplitVo.setMaterialsName(cfStockScanLineSplit.getMaterialsName());
                cfStockSplitVo.setBatchNo(cfStockScanLineSplit.getBatchNo());
                cfStockSplitVo.setNumber(cfStockScanLineSplit.getNumber());
                if (cfStockScanLineSplit.getMaterialsNo().equals(materialsNo) || i == 0) {
                    bigDecimalNumber = bigDecimalNumber.add(cfStockScanLineSplit.getNumber());
                    materialsNo = cfStockScanLineSplit.getMaterialsNo();
                    cfStockSplitVoList.add(cfStockSplitVo);
                } else {
                    //生成物料拆分
                    CfStockScanLine cfStockScanLineSplitBefore = splitList.get(i - 1);
                    CfStockSplit cfStockSplit = new CfStockSplit();
                    String NextNumber = cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                    cfStockSplit.setSplitNo(NextNumber);
                    cfStockSplit.setStockListNo(cfStockListInfoRoot.getStockListNo());
                    cfStockSplit.setMaterialsName(cfStockScanLineSplitBefore.getMaterialsName());
                    cfStockSplit.setObjectSetBasicAttribute(userId, newDate);
                    cfStockSplit.setFlag(0);
                    cfStockSplit.setMaterialsNo(cfStockScanLineSplitBefore.getMaterialsNo());
                    cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoList));
                    cfStockSplit.setNumber(bigDecimalNumber);
                    cfStockSplit.setMode(cfStockScanLineSplitBefore.getMode());
                    cfStockSplitList.add(cfStockSplit);
                    cfStockSplitVoList = new ArrayList<>();
                    bigDecimalNumber = new BigDecimal(0);
                    bigDecimalNumber = bigDecimalNumber.add(cfStockScanLineSplit.getNumber());
                    materialsNo = cfStockScanLineSplit.getMaterialsNo();
                    cfStockSplitVoList.add(cfStockSplitVo);
                }
                if (i == splitList.size() - 1) {
                    //生成物料拆分
                    CfStockSplit cfStockSplit = new CfStockSplit();
                    String NextNumber = cfNextNumberService.generateNextNumber(CfStockScanLine.STOCK_SPLIT);
                    cfStockSplit.setSplitNo(NextNumber);
                    cfStockSplit.setStockListNo(cfStockListInfoRoot.getStockListNo());
                    cfStockSplit.setObjectSetBasicAttribute(userId, newDate);
                    cfStockSplit.setFlag(0);
                    cfStockSplit.setMaterialsNo(cfStockScanLineSplit.getMaterialsNo());
                    cfStockSplit.setMaterialsName(cfStockScanLineSplit.getMaterialsName());
                    cfStockSplit.setMode(cfStockScanLineSplit.getMode());
                    cfStockSplit.setBatchNoText(JSONObject.toJSONString(cfStockSplitVoList));
                    cfStockSplit.setNumber(bigDecimalNumber);
                    cfStockSplitList.add(cfStockSplit);
                }
            }
            iCfStockSplitService.insertBatch(cfStockSplitList);
        }
        //删除临时表数据行数据
        this.delete(new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId).eq(CfStockScanLine.CREATED_BY_SQL, userId));
        Map<String, Object> resultMap = new HashedMap();
        resultMap.put("cfStockListInfoRoot", cfStockListInfoRoot);
        resultMap.put("cfStockInventoryHeaderPage", cfStockInventoryHeaderPage);
        resultMap.put("cfStockSplitList", cfStockSplitList);
        resultMap.put("cfStockScanLinePage", null);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page getCfStockInventoryPage(int userId, Map<String, Object> params) throws Exception {
        Integer page = Integer.parseInt(params.getOrDefault("page", 1).toString());
        Integer limit = Integer.parseInt(params.getOrDefault("limit", QueryPage.LIMIT_10000).toString());
        String stockListIdSt = params.getOrDefault("stockListId", "").toString();
        String checkRecord = params.getOrDefault("checkRecord", "N").toString();
        if (!StringUtils.isNotBlank(stockListIdSt)) {
            throw new ValidateCodeException(CfStockScanLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        int stockListId = Integer.parseInt(stockListIdSt);
        //设置查询条件
        //获取已扫描数据
        Page<CfStockInventory> cfStockInventoryHeaderPage = new Page<>(page, limit, CfStockScanLine.STOCK_LIST_ID_SQL, false);
        Wrapper entityWrapper = new EntityWrapper<CfStockInventory>()
                .eq(CfStockScanLine.STOCK_LIST_ID_SQL, stockListId)
                /* TODO .eq(CfStockScanLine.PARAMS_REPOSITORY,userVO.getWarehouse())*/;
        ;
        if (CfStockScanLine.PARAMS_N.equals(checkRecord)) {
         /*  TODO  if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false);

        } else {
          /* TODO  if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false).addFilter("should_send_number>actual_send_number");
        }
        // CfStockInventory
        RowBounds rowBounds = new RowBounds(page - 1, limit);
        List<CfStockInventory> cfStockInventoryList = cfStockInventoryMapper.selectPage(rowBounds, entityWrapper);
        cfStockInventoryHeaderPage.setRecords(cfStockInventoryList);
        return cfStockInventoryHeaderPage;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateByStockScanLineData(int userId, Map<String, Object> params) throws Exception {
        String checkRecord = params.getOrDefault("checkRecord", "N").toString();
        String stockLineIdSt = params.getOrDefault("stockLineId", "").toString();
        String changeNumberSt = params.getOrDefault("changeNumber", "").toString();
        String changeRemark = params.getOrDefault("changeRemark", "").toString();
        BigDecimal changeNumber;
        if (!StringUtils.isNotBlank(stockLineIdSt)) {
            throw new ValidateCodeException(CfStockScanLine.EX_STOCK_LINE_ID);
        }
        if (!StringUtils.isNotBlank(changeNumberSt)) {
            throw new ValidateCodeException(CfStockScanLine.EX_CHANGE_NUMBER);
        }
        try {
            changeNumber = new BigDecimal(changeNumberSt);
        } catch (Exception e) {
            throw new ValidateCodeException(CfStockScanLine.EX_CHANGE_NUMBER);
        }

        int stockLineId = Integer.parseInt(stockLineIdSt);
        CfStockScanLine cfStockScanLine = this.selectById(stockLineId);
        if (changeNumber.compareTo(cfStockScanLine.getNumber()) > 0) {
            throw new ValidateCodeException(CfStockScanLine.EX_CHANGE_NUMBER_TOO_BIG);
        }
        BigDecimal number = cfStockScanLine.getNumber().subtract(changeNumber);
        cfStockScanLine.setNumber(changeNumber);
        this.updateById(cfStockScanLine);
        //
        CfStockInventory cfStockInventory = cfStockInventoryMapper.selectById(cfStockScanLine.getStockInventoryId());
        cfStockInventory.setActualSendNumber(cfStockInventory.getActualSendNumber().subtract(number));
        cfStockInventoryMapper.updateById(cfStockInventory);

        //获取更新过的数据
        Map<String, Object> resultMap = new HashedMap();
        int current = 1;
        int size = QueryPage.LIMIT_10000;
        //通过单据头获取汇总数据
        Page<CfStockScanLine> headerPage = new Page<>(current, size, CfStockScanLine.STOCK_LINE_ID_SQL, false);
        Page cfStockScanLinePage = this.selectPage(headerPage, new EntityWrapper<CfStockScanLine>().
                eq(CfStockScanLine.STOCK_LIST_ID_SQL, cfStockScanLine.getStockListId()).eq(CfStockScanLine.CREATED_BY_SQL, userId));
        //获取已扫描数据
        Page<CfStockInventory> cfStockInventoryHeaderPage = new Page<>(current, size, CfStockScanLine.STOCK_LIST_ID_SQL, false);

        // CfStockInventory
        RowBounds rowBounds = new RowBounds(0, size);
        Wrapper entityWrapper = new EntityWrapper<CfStockInventory>()
                .eq(CfStockScanLine.STOCK_LIST_ID_SQL, cfStockScanLine.getStockListId())
                /* TODO .eq(CfStockScanLine.PARAMS_REPOSITORY,userVO.getWarehouse())*/;
        ;
        if (CfStockScanLine.PARAMS_N.equals(checkRecord)) {
          /* TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false);

        } else {
           /* TODO if(StringUtils.isNotBlank(userVO.getStorageArea())){
                entityWrapper=entityWrapper.eq(CfStockScanLine.PARAMS_STORAGE_AREA,userVO.getStorageArea());
            }*/
            entityWrapper = entityWrapper.orderBy(CfStockScanLine.LAST_UPDATED_DATE_SQL, false).addFilter("should_send_number>actual_send_number");
        }

        List<CfStockInventory> cfStockInventoryList = cfStockInventoryMapper.selectPage(rowBounds, entityWrapper);

        cfStockInventoryHeaderPage.setRecords(cfStockInventoryList);
        //将数据封装到界面
        resultMap.put("cfStockInventoryHeaderPage", cfStockInventoryHeaderPage);
        resultMap.put("cfStockScanLinePage", cfStockScanLinePage);
        return resultMap;
    }


}

