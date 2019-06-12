package com.cfmoto.bar.code.service.impl.ckdMaterielBoxImpl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfBarcodeInventoryMapper;
import com.cfmoto.bar.code.mapper.CfStorageLocationMapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfLocationBinding;
import com.cfmoto.bar.code.mapper.CfLocationBindingMapper;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfLocationBindingService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物料条码和货位条码绑定 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-24
 */
@Service
public class CfLocationBindingServiceImpl extends ServiceImpl<CfLocationBindingMapper, CfLocationBinding> implements ICfLocationBindingService {

    protected org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CfBarcodeInventoryMapper cfBarcodeInventoryMapper;

    @Autowired
    SapFeignService sapFeignService;

    @Autowired
    private CfStorageLocationMapper storageLocationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean locationBindingToSap(Map<String, Object> params, int userId) throws ValidateCodeException, Exception {
        String type = params.getOrDefault("type", "").toString();
        String barCodeNo = params.getOrDefault("barCodeNo", "").toString();
        String materialsName = params.getOrDefault("materialsName", "").toString();
        String materialsNo = params.getOrDefault("materialsNo", "").toString();
        String barCodeNumber = params.getOrDefault("barCodeNumber", "").toString();
        String warehouse = params.getOrDefault("warehouse", "").toString();
        String warehousePosition = params.getOrDefault("warehousePosition", "").toString();
        String storageArea = params.getOrDefault("storageArea", "").toString();
        String batchNo = params.getOrDefault("batchNo", "").toString();
        String warehouseNew = params.getOrDefault("warehouseNew", "").toString();
        String warehousePositionNew = params.getOrDefault("warehousePositionNew", "").toString();
        String factoryNew = params.getOrDefault("factoryNew", "").toString();
        String storageAreaNew = params.getOrDefault("storageAreaNew", "").toString();
        //果如工厂为空报错
        if (!StringUtils.isNotBlank(factoryNew)) {
            throw new ValidateCodeException(CfLocationBinding.CF_FACTORY_ERROR);

        }
        //判断目标仓位和原有仓位不可以相同
        if (type.equals(CfLocationBinding.CF_BARCODE_TYPE_CHANGE_LOCATION)) {
            if (warehousePositionNew.equals(warehousePosition) && storageArea.equals(storageAreaNew)) {
                throw new ValidateCodeException(CfLocationBinding.CF_WAREHOUSE_POSITION_ERROR);
            }
        }

        //判断该工厂是否和厂库存在报错
        CfStorageLocation cfStorageLocation = new CfStorageLocation();
        cfStorageLocation.setWareHouse(warehouseNew);
        cfStorageLocation.setSite(factoryNew);
        EntityWrapper<CfStorageLocation> wrapper = new EntityWrapper<>(cfStorageLocation);
        List<CfStorageLocation> storageLocationList = storageLocationMapper.selectList(wrapper);
        if (storageLocationList.size() == 0) {
            throw new ValidateCodeException(CfLocationBinding.CF_FACTORY_ERROR);
        }
        if (!StringUtils.isNotBlank(type)) {
            throw new ValidateCodeException(CfLocationBinding.CF_BARCODE_PARAMS_ERROR);
        }

        if (!warehouse.equals(warehouseNew)) {
            if (type.equals(CfLocationBinding.CF_BARCODE_TYPE_ADD_SHELVES)) {
                throw new ValidateCodeException(CfLocationBinding.CF_WAREHOUSE_ERROR);
            } else {
                throw new ValidateCodeException(CfLocationBinding.CF_WAREHOUSE_CHANGE_ERROR);
            }
        }
        CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
        cfBarcodeInventory.setBarcode(barCodeNo);
        cfBarcodeInventory = cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
        if (cfBarcodeInventory == null) {
            throw new ValidateCodeException(CfLocationBinding.CF_BARCODE_NOT_HAVING);
        }
        cfBarcodeInventory.setWarehouse(warehouseNew);
        cfBarcodeInventory.setWarehousePosition(warehousePositionNew);
        cfBarcodeInventory.setStorageArea(storageAreaNew);
        cfBarcodeInventoryMapper.updateById(cfBarcodeInventory);
        Map<String, Object> paramMapSap = new HashedMap();
        paramMapSap.put("functionName", "ZMM_BC_016");
        Map<String, String> dataMap = new HashedMap();
        dataMap.put("ZSJTP", type);//类型
        dataMap.put("MATNR", materialsNo);//物料代码
        dataMap.put("CHARG", batchNo);//批次
        dataMap.put("NISTA", barCodeNumber);//数量
        dataMap.put("VLTYP", storageArea);//原存储区域
        dataMap.put("VLPLA", warehousePosition);//原仓位
        dataMap.put("LGORT", warehouse);//原仓库
        dataMap.put("NLTYP", storageAreaNew);//目标存储区域
        dataMap.put("NLPLA", warehousePositionNew);//目标仓位
        dataMap.put("WERKS", factoryNew);//工厂
        Map<String, Object> paramData = new HashedMap();
        paramData.put("IS_DATA", dataMap);
        paramMapSap.put("paramMap", paramData);
        //TODO SAP获取数据
        R<Map<String, Object>> result = sapFeignService.executeJcoFunction(paramMapSap);
        if (result.getCode() != 0) {
            logger.error("locationBindingToSap SAP获取数据异常 :" + result.getMsg());
            throw new ValidateCodeException(result.getMsg());
        }

        Map<String, Object> resultMapData = result.getData();
        JSONObject jsonObject = new JSONObject(resultMapData);
        if (!jsonObject.getString("EV_STATUS").equals("1")) {
            throw new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }
        return true;
    }

    @Override
    public Map<String, Object> getDetailByBarcode(Map<String, Object> params, int userId) throws ValidateCodeException {
        Map<String, Object> resultMap = new HashedMap();
        String barCodeNo = params.getOrDefault("barCodeNo", "").toString();
        if (!StringUtils.isNotBlank(barCodeNo)) {
            throw new ValidateCodeException(CfLocationBinding.CF_BARCODE_NOT_NULL);
        }
        CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
        cfBarcodeInventory.setBarcode(barCodeNo);
        cfBarcodeInventory = cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
        if (cfBarcodeInventory == null) {
            throw new ValidateCodeException(CfLocationBinding.CF_BARCODE_NOT_HAVING);
        }
        //0，上架
        if (!StringUtils.isNotBlank(cfBarcodeInventory.getWarehousePosition())) {
            resultMap.put("type", CfLocationBinding.CF_BARCODE_TYPE_ADD_SHELVES);
        } else {
            // 1，仓位转换
            resultMap.put("type", CfLocationBinding.CF_BARCODE_TYPE_CHANGE_LOCATION);
        }
        resultMap.put("materialsName", StringUtils.isNotBlank(cfBarcodeInventory.getMaterialsName()) ? cfBarcodeInventory.getMaterialsName() : "");//物料名称
        resultMap.put("materialsNo", StringUtils.isNotBlank(cfBarcodeInventory.getMaterialsNo()) ? cfBarcodeInventory.getMaterialsNo() : "");//物料代码
        resultMap.put("barCodeNumber", cfBarcodeInventory.getBarCodeNumber());//数量
        resultMap.put("warehouse", StringUtils.isNotBlank(cfBarcodeInventory.getWarehouse()) ? cfBarcodeInventory.getWarehouse() : "");//仓库
        resultMap.put("warehousePosition", StringUtils.isNotBlank(cfBarcodeInventory.getWarehousePosition()) ? cfBarcodeInventory.getWarehousePosition() : "");//仓位
        resultMap.put("storageArea", StringUtils.isNotBlank(cfBarcodeInventory.getStorageArea()) ? cfBarcodeInventory.getStorageArea() : "");//存储区域
        resultMap.put("batchNo", StringUtils.isNotBlank(cfBarcodeInventory.getBatchNo()) ? cfBarcodeInventory.getBatchNo() : "");//存储区域
        return resultMap;
    }
}
