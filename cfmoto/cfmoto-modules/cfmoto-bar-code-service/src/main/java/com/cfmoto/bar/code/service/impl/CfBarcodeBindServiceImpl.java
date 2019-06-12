package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfBarcodeBindMapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfCftRelationship;
import com.cfmoto.bar.code.model.entity.CfReportWorkRecord;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.model.vo.ThreeCodeBindVo;
import com.cfmoto.bar.code.service.ICfBarcodeBindService;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfCftRelationshipService;
import com.cfmoto.bar.code.service.ICfCustomService;
import com.cfmoto.bar.code.service.reportworkrecord.ICfReportWorkRecordService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.xiaoleilu.hutool.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-02-28
 */
@Service
public class CfBarcodeBindServiceImpl extends ServiceImpl<CfBarcodeBindMapper, CfBarcodeBind> implements ICfBarcodeBindService {

    @Autowired
    private CfBarcodeBindMapper cfBarcodeBindMapper;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private ICfCftRelationshipService iCfCftRelationshipService;

    @Autowired
    private ICfBarcodeInventoryService iCfBarcodeInventoryService;

    @Autowired
    private ICfCustomService iCfCustomService;

    @Autowired
    private ICfReportWorkRecordService cfReportWorkRecordService;

    /**
     * codeType:C:整车条码 E:发动机条码 F：车架条码
     *
     * @param code
     * @param codeType
     * @throws Exception
     */
    @Override
    public ThreeCodeBindVo codeValidate(String code, String codeType) throws Exception {

        if (StrUtil.equalsIgnoreCase("C", codeType)) {//整车
            return validateCP(code);
        } else if (StrUtil.equalsIgnoreCase("E", codeType)) {//发动机
            return validateEG(code);
        } else if (StrUtil.equalsIgnoreCase("F", codeType)) { //车架
            return validateFR(code);
        } else {
            throw new Exception("未知条码类型");
        }

    }

    /**
     * codeType:C:整车条码 E:发动机条码 F：车架条码
     * bindType: B 绑定，U 解绑
     *
     * @param code
     * @param codeType
     * @throws Exception
     */
    @Override
    public ThreeCodeBindVo codeValidate(String code, String codeType, String bindType) throws Exception {

        if( StrUtil.equalsIgnoreCase("B", bindType) ){ //绑定操作验证
            return codeValidate( code,codeType );
        }else{ //非绑定操作验证
            if( StrUtil.equalsIgnoreCase("C", codeType) ){
                return validateUnbindCP( code );
            }else{
                return codeValidate( code,codeType );
            }
        }
    }

    /**
     * 验证CP条码，同时也验证生产订单数量是否已达标
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Override
    public ThreeCodeBindVo validateCP( String code ) throws Exception {
        List<CfBarcodeInventory> cfBarcodeInventoryList = getBarcodeInventoryList(code);
        if (cfBarcodeInventoryList.size() == 0) {
            throw new Exception("CP码" + code + "不存在");
        }
        CfBarcodeInventory cfBarcodeInventory = cfBarcodeInventoryList.get(0);
        String productionTaskOrder = cfBarcodeInventory.getProductionTaskOrder();
        if (StrUtil.isBlank(productionTaskOrder)) {
            throw new Exception("条码" + productionTaskOrder + "生产订单未维护");
        }
        //获取生产订单数据
        ProductionTaskVo productionTaskVo = iCfCustomService.getTaskNo(productionTaskOrder);
        //查询三码绑定表生产订单已绑定数量
        EntityWrapper<CfBarcodeBind> wrapper = new EntityWrapper<CfBarcodeBind>();
        wrapper.eq("production_order", productionTaskOrder);
        Integer count = cfBarcodeBindMapper.selectCount(wrapper);
        if (count >= productionTaskVo.getQuantity().intValue()) { //检查生产订单数量是否已满足
            throw new Exception("生产订单需求数量" + productionTaskVo.getQuantity().intValue() + "已达标");
        }
        ThreeCodeBindVo threeCodeBindVo = new ThreeCodeBindVo();
        threeCodeBindVo.setCarType(cfBarcodeInventory.getCarModel());
        threeCodeBindVo.setProductionTaskOrder(productionTaskOrder);
        threeCodeBindVo.setMessage("第[" + (count + 1) + "]台绑定");
        return threeCodeBindVo;

    }

    /**
     * 解绑CP验证
     * @param code
     * @return
     * @throws Exception
     */
    public ThreeCodeBindVo validateUnbindCP( String code ) throws Exception {
        List<CfBarcodeInventory> cfBarcodeInventoryList = getBarcodeInventoryList(code);
        if (cfBarcodeInventoryList.size() == 0) {
            throw new Exception("CP码" + code + "不存在");
        }
        CfBarcodeInventory cfBarcodeInventory = cfBarcodeInventoryList.get(0);
        ThreeCodeBindVo threeCodeBindVo = new ThreeCodeBindVo();
        threeCodeBindVo.setCarType(cfBarcodeInventory.getCarModel());
        return threeCodeBindVo;

    }

    public List<CfBarcodeInventory> getBarcodeInventoryList(String code) {
        EntityWrapper<CfBarcodeInventory> barcodeInventoryEntity = new EntityWrapper<CfBarcodeInventory>();
        CfBarcodeInventory barcodeInventory = new CfBarcodeInventory();
        barcodeInventory.setBarcode(code);
        barcodeInventoryEntity.setEntity(barcodeInventory);
        return iCfBarcodeInventoryService.selectList(barcodeInventoryEntity);
    }

    /**
     * 验证发送机条码
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Override
    public ThreeCodeBindVo validateEG(String code) throws Exception {
        List<CfBarcodeInventory> cfBarcodeInventoryList = getBarcodeInventoryList(code);
        if (cfBarcodeInventoryList.size() == 0) {
            throw new Exception("发动机条码" + code + "不存在");
        }
        ThreeCodeBindVo threeCodeBindVo = new ThreeCodeBindVo();
        threeCodeBindVo.setCarType(cfBarcodeInventoryList.get(0).getBarcode().split(" ")[0]);
        threeCodeBindVo.setProductionTaskOrder(cfBarcodeInventoryList.get(0).getProductionTaskOrder());
        threeCodeBindVo.setMessage("");
        return threeCodeBindVo;

    }

    /**
     * 验证车架条码
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Override
    public ThreeCodeBindVo validateFR(String code) throws Exception {

        if (code.length() < 8) {
            throw new Exception("条码长度不足8位");
        }
        String carFrame = code.substring(0, 8);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("car_frame", carFrame);
        List<CfCftRelationship> cfCftRelationshipList = iCfCftRelationshipService.selectByMap(paramMap);
        if (cfCftRelationshipList.size() == 0) {
            throw new Exception("车架代码" + carFrame + "不存在");
        }
        ThreeCodeBindVo threeCodeBindVo = new ThreeCodeBindVo();
        threeCodeBindVo.setCarType(cfCftRelationshipList.get(0).getCarType());
        threeCodeBindVo.setMessage("");
        return threeCodeBindVo;

    }

    @Override
    public void validateTBoxCP(String code) throws Exception {
        //条码规则：CP+2位年+2位月+2位日+6位流水码
        //有效性校验：1、前两位为CP，以及条码长度为14位；2、通过CP码在三码绑定表中能查找对应的行数据，否则报错“该CP码没有三码绑定记录”；检验行数据中T-BOX为空，否则报错“该CP已有T-BOX绑定记录”
        if (!(code.startsWith("CP") && code.length() == 14)) {
            throw new Exception("CP条码必须以CP开头，且长度为14位!!!");
        }
        CfBarcodeBind barcodeBind = new CfBarcodeBind();
        barcodeBind.setCar(code);
        CfBarcodeBind load = cfBarcodeBindMapper.selectOne(barcodeBind);
        //验证是否存在三码绑定记录
        if (load == null) {
            throw new Exception("该CP码没有三码绑定记录,请注意!!!");
        } else {
            if (load.gettBoxCode() != null) {
                throw new Exception("该CP条码已有T-BOX绑定记录,请注意!!!");
            }
        }
    }

    @Override
    public void validateTBox(String code) throws Exception {
        if (code.length() != 15) {
            throw new Exception("T-Box码长度必须为15位，请注意!!!");
        }
    }

    /**
     * 三码绑定接口
     *
     * @param userId
     * @param carCode
     * @param engineCode
     * @param frameCode
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void threeCodeBind(Integer userId, String carCode, String engineCode, String frameCode) throws Exception {

        ThreeCodeBindVo threeCodeBindVo = validateCP(carCode);
        String carType1 = threeCodeBindVo.getCarType();
        String carType2 = validateEG(engineCode).getCarType();
        String carType3 = validateFR(frameCode).getCarType();
    /*    if (!carType1.equals(carType2)) {
            throw new Exception("整车CP码和发动机条码车型不一致");
        }*/
        if (!carType1.equals(carType3)) {
            throw new Exception("整车CP码和车架条码车型不一致");
        }
        //查询是否有绑定记录，carCode、engineCode、frameCode检查任意一码即可
        CfBarcodeBind entity = new CfBarcodeBind();
        entity.setCar(carCode);
        entity = cfBarcodeBindMapper.selectOne(entity);
        if (entity != null) {
            throw new Exception("CP条码[" + carCode + "]" + "已有绑定记录");
        }

        CfBarcodeBind entity1 = new CfBarcodeBind();
        entity1.setFrame(frameCode);
        entity1 = cfBarcodeBindMapper.selectOne(entity1);
        if (entity1 != null) {
            throw new Exception("车架条码[" + frameCode + "]" + "已有绑定记录");
        }

        CfBarcodeBind entity2 = new CfBarcodeBind();
        entity2.setEngine(engineCode);
        entity2 = cfBarcodeBindMapper.selectOne(entity2);
        if (entity2 != null) {
            throw new Exception("发动机条码[" + engineCode + "]" + "已有绑定记录");
        }
        //保存三码绑定数据
        CfBarcodeBind cfBarcodeBind = new CfBarcodeBind();
        cfBarcodeBind.setCar(carCode);
        cfBarcodeBind.setEngine(engineCode);
        cfBarcodeBind.setFrame(frameCode);
        cfBarcodeBind.setProductionOrder(threeCodeBindVo.getProductionTaskOrder());
        cfBarcodeBind.setObjectSetBasicAttribute(userId, new Date());
        cfBarcodeBindMapper.insert(cfBarcodeBind);

        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("IV_ZCCPM", carCode);
        paramMap.put("IV_ZFDJM", engineCode);
        paramMap.put("IV_ZCJBM", frameCode);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_018");
        callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);
        R returnR = sapFeignService.executeJcoFunction(callParamMap);
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }

    }


    /**
     * 三码解绑
     *
     * @param userId
     * @param carCode
     * @param engineCode
     * @param frameCode
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public CfBarcodeBind threeCodeUnbind(Integer userId, String carCode, String engineCode, String frameCode) throws Exception {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (!StrUtil.isBlank(carCode)) {
            paramMap.put("IV_ZCCPM", carCode);

        }
        if (!StrUtil.isBlank(engineCode)) {
            paramMap.put("IV_ZFDJM", engineCode);

        }
        if (!StrUtil.isBlank(frameCode)) {
            paramMap.put("IV_ZCJBM", frameCode);

        }
        //查询绑定记录
        EntityWrapper<CfBarcodeBind> barcodeBindEntityWrapper = new EntityWrapper<CfBarcodeBind>();
        barcodeBindEntityWrapper.addFilter("(car='" + carCode + "' or frame='" + frameCode + "' or engine='" + engineCode + "' )");
        List<CfBarcodeBind> barcodeBindList = cfBarcodeBindMapper.selectList(barcodeBindEntityWrapper);
        if (barcodeBindList.size() == 0) {
            throw new Exception("条码无绑定记录");
        }
        //删除绑定记录
        cfBarcodeBindMapper.deleteById(barcodeBindList.get(0).getBarcodeBindId());
        barcodeBindList.forEach( c->{
            cfReportWorkRecordService.delete( new EntityWrapper<CfReportWorkRecord>().eq(CfReportWorkRecord.BARCODE_SQL,c.getCar())) ;
        });



        Map<String, Object> callParamMap = new HashMap<String, Object>();
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_019");
        callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);
        R returnR = sapFeignService.executeJcoFunction(callParamMap);
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }
        return barcodeBindList.get(0);
    }

    @Override
    public void tBoxValidate(String code, String codeType) throws Exception {
        //C：整车   T:T-Box
        if (StrUtil.equalsIgnoreCase("C", codeType)) {
            validateTBoxCP(code);
        } else if (StrUtil.equalsIgnoreCase("T", codeType)) {
            validateTBox(code);
        } else {
            throw new Exception("未知条码类型");
        }
    }

    @Override
    public void tBoxBind(String carCode, String tBoxCode, int userId) throws Exception {
        Map<String, Object> callParamMap = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("IV_ZCCPM", carCode);
        paramMap.put("IV_ZFDJM", "");
        paramMap.put("IV_ZCJBM", "");
        paramMap.put("IV_ZTBOX", tBoxCode);
        callParamMap.put(HandleRefConstants.FUNCTION_NAME, "ZMM_BC_018");
        callParamMap.put(HandleRefConstants.PARAM_MAP, paramMap);
        R returnR = sapFeignService.executeJcoFunction(callParamMap);
        if (returnR.getCode() != 0) {
            throw new Exception(returnR.getMsg());
        }
        Map<String, Object> returnMap = (Map<String, Object>) returnR.getData();
        Integer evStatus = (Integer) returnMap.get("EV_STATUS");
        if (evStatus == 0) {
            throw new Exception((String) returnMap.get("EV_MESSAGE"));
        }

        //程序到这里则说明sap接口调用成功且三码绑定成功

        //更新三码绑定记录表
        //获取三码绑定记录
        CfBarcodeBind barcodeBind = new CfBarcodeBind();
        barcodeBind.setCar(carCode);
        CfBarcodeBind load = cfBarcodeBindMapper.selectOne(barcodeBind);
        //更改三码绑定记录中的数据
        load.settBoxCode(tBoxCode);
        load.settBoxBindBy(userId);
        Date now = new Date();
        load.settBoxBindDate(now);
        load.setObjectSetBasicAttributeForUpdate(userId, now);
        //更新三码绑定记录表
        cfBarcodeBindMapper.updateById(load);

    }
}
