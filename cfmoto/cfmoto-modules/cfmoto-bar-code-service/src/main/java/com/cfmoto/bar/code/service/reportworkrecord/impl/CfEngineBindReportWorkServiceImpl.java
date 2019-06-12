package com.cfmoto.bar.code.service.reportworkrecord.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cfmoto.bar.code.mapper.CfReportWorkRecordMapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfReportWorkRecord;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfNextNumberService;
import com.cfmoto.bar.code.service.ISapApiService;
import com.cfmoto.bar.code.service.reportworkrecord.ICfEngineBindReportWorkService;
import com.cfmoto.bar.code.service.reportworkrecord.ICfReportWorkRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发动机绑定报工 业务层接口实现类
 *
 * @author yezi
 * @date 2019/6/11
 */
@Service
public class CfEngineBindReportWorkServiceImpl implements ICfEngineBindReportWorkService {

    @Autowired
    private ISapApiService sapApiService;

    @Autowired
    private ICfNextNumberService nextNumberService;

    @Autowired
    private CfReportWorkRecordMapper cfReportWorkRecordMapper;

    @Autowired
    private ICfReportWorkRecordService reportWorkRecordService;

    @Autowired
    private ICfBarcodeInventoryService barcodeInventoryService;


    @Override
    public ProductionTaskVo productOrder(String productOrderNo) throws Exception {
        //调用SAP004接口获取订单数量
        ProductionTaskVo productionTaskVo = sapApiService.getDataFromSapApi004(productOrderNo);

        //查询数据库中对应单号的报工记录数据条数
        productionTaskVo.setBoundNumber(cfReportWorkRecordMapper.getTotalByProductTaskOrder(productOrderNo));

        return productionTaskVo;
    }

    /**
     * 1.查找库存表中是否有此条码
     * a.存在弹出报错确认框“该条码存在绑定记录，请确定！”
     * b.不存在，输入插入到库存表，状态为N。
     *
     * @param productionTaskVo 生产任务单相关数据
     * @param userId           用户ID
     */
    @Override
    public void engineBarcodeScan(ProductionTaskVo productionTaskVo, Integer userId) throws Exception {
        String engineBarcode = productionTaskVo.getEngineBarcode();

        //查找库存表是否有此发动机条码
        CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(new EntityWrapper<CfBarcodeInventory>()
                .eq("barcode", engineBarcode));

        //存在则报错
        if (barcodeInventory != null) {
            throw new Exception("该条码存在绑定记录，请确定！");
        }

        //不存在则插入一条数据到数据库
        CfBarcodeInventory newBarcodeInventory = new CfBarcodeInventory();
        newBarcodeInventory.setBarcode(engineBarcode);
        //条码类型为EG，代表发动机
        newBarcodeInventory.setBarcodeType("EG");
        newBarcodeInventory.setState("N");
        newBarcodeInventory.setBarCodeNumber(new BigDecimal(1));
        newBarcodeInventory.setMaterialsNo(productionTaskVo.getItem());
        newBarcodeInventory.setMaterialsName(productionTaskVo.getItemDesc());
        //生成批次
        newBarcodeInventory.setBatchNo(nextNumberService.generateNextNumber("BATCH_NO"));
        newBarcodeInventory.setMode(productionTaskVo.getMode());
        newBarcodeInventory.setCarModel(productionTaskVo.getCarType());
        newBarcodeInventory.setFactory(productionTaskVo.getFactory());
        newBarcodeInventory.setWarehouse(productionTaskVo.getStorageLocation());
        newBarcodeInventory.setProductionTaskOrder(productionTaskVo.getTaskNo());
        newBarcodeInventory.setSaleOrderNo(productionTaskVo.getSaleOrder());
        newBarcodeInventory.setSalesItem(productionTaskVo.getSaleOrderRowItem());
        newBarcodeInventory.setObjectSetBasicAttribute(userId, new Date());
        newBarcodeInventory.setContractNo(productionTaskVo.getContract());

        barcodeInventoryService.insert(newBarcodeInventory);
    }

    /**
     * 1、校验条码是否存在库存；
     * 不存在报错“条码不存在”；
     * 存在，校验状态是否为N，不为N报错“条码有入库记录，不能解绑”，为N，删除库存表数据，删除报工记录表数据
     *
     * @param engineBarcode 发动机条码
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unbindEngineBarcode(String engineBarcode) throws Exception {
        Wrapper<CfBarcodeInventory> barcodeInventoryWrapper = new EntityWrapper<CfBarcodeInventory>()
                .eq("barcode", engineBarcode);

        CfBarcodeInventory barcodeInventory = barcodeInventoryService.selectOne(barcodeInventoryWrapper);
        if (barcodeInventory == null) {
            throw new Exception("该发动机条码不存在!!!");
        }

        String state = barcodeInventory.getState();
        if (!"N".equals(state)) {
            throw new Exception("条码有入库记录，不能解绑!!!");
        }

        //删除库存表
        barcodeInventoryService.delete(barcodeInventoryWrapper);

        //删除报工记录表
        reportWorkRecordService.delete(new EntityWrapper<CfReportWorkRecord>().eq("barcode", engineBarcode));

    }
}
