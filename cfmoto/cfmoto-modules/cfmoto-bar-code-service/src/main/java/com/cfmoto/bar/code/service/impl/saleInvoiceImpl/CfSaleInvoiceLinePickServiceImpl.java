package com.cfmoto.bar.code.service.impl.saleInvoiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.*;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceHeaderService;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceLinePickService;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceRootService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
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
 * 销售发货单子表 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
@Service
public class CfSaleInvoiceLinePickServiceImpl extends ServiceImpl<CfSaleInvoiceLineMapper, CfSaleInvoiceLine> implements ICfSaleInvoiceLinePickService {

    @Autowired
    CfKtmReceivingOrderMapper cfKtmReceivingOrderMapper ;

    @Autowired
    CfSaleInvoiceLineMapper cfSaleInvoiceLineMapper;

    @Autowired
    CfSaleInvoiceHeaderMapper  cfSaleInvoiceHeaderMapper ;

    @Autowired
    CfBarcodeBindMapper cfBarcodeBindMapper ;

    @Autowired
    CfBarcodeInventoryMapper cfBarcodeInventoryMapper ;
    @Autowired
    CfSaleInvoiceRootMapper cfSaleInvoiceRootMapper ;

    @Autowired
    SapFeignService sapFeignService ;

    @Autowired
    ICfSaleInvoiceHeaderService iCfSaleInvoiceHeaderService;

    //扫描添加条码
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addCfSaleInvoiceData(int userId, Map<String, Object> params) throws Exception {

         String barCodeNo= params.getOrDefault("barCodeNo", "").toString();
         String invoiceRootIdSt= params.getOrDefault("invoiceRootId", "").toString();
         Date thisNewDate=new Date() ;
        if(!StringUtils.isNotBlank(barCodeNo)){
             throw  new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_NO);
         }
         if(!StringUtils.isNotBlank(invoiceRootIdSt)){
            throw  new ValidateCodeException(CfSaleInvoiceLine.EX_SALE_INVOICE_NO);
        }
        int invoiceRootId=Integer.parseInt(invoiceRootIdSt);
        //判断条码是否被扫描
        CfSaleInvoiceLine cfSaleInvoiceLineJudge=new CfSaleInvoiceLine();
        cfSaleInvoiceLineJudge.setInvoiceRootId(invoiceRootId);
        cfSaleInvoiceLineJudge.setBarCodeNo(barCodeNo);
        int selectCount=  this.selectCount(new EntityWrapper<CfSaleInvoiceLine>(cfSaleInvoiceLineJudge));
        if(selectCount>0){
            throw  new ValidateCodeException(CfSaleInvoiceLine.EX_BARCODE_DOUBLE);
        }
        //KTM整车
        CfKtmReceivingOrder cfKtmReceivingOrder=new CfKtmReceivingOrder();
        cfKtmReceivingOrder.setFrameNo(barCodeNo);
        cfKtmReceivingOrder= cfKtmReceivingOrderMapper.selectOne(cfKtmReceivingOrder);
        if(cfKtmReceivingOrder!=null){
            //判断条码数量是否还有
            if(cfKtmReceivingOrder.getBarCodeNumber().compareTo(new BigDecimal(1))<0){
                throw  new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_NUMBER);
            }
            //拿取销售发货单数据
            List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderList= cfSaleInvoiceHeaderMapper.selectList(new EntityWrapper<CfSaleInvoiceHeader>().
                    eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId)
                    .eq(CfSaleInvoiceHeader.INVOICE_MATERIAL_CODE_SQL,cfKtmReceivingOrder.getMaterialsNo())
                    .eq(CfSaleInvoiceHeader.INVOICE_WAREHOUSE_SQL,cfKtmReceivingOrder.getRepository())
                   // .addFilter("need_number>scanning_number")
            );
            if(cfSaleInvoiceHeaderList.size()==0){
                throw  new ValidateCodeException(CfSaleInvoiceHeader.EX_MATERIALS_WAREHOUSE_ERROR);
            }
            List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderTempList=new ArrayList<>();
            cfSaleInvoiceHeaderList.forEach(c-> {
                if(c.getNeedNumber().compareTo(c.getScanningNumber())>0){
                    cfSaleInvoiceHeaderTempList.add(c);
                }
            });
            if(cfSaleInvoiceHeaderTempList.size()==0){
                throw  new ValidateCodeException(CfSaleInvoiceHeader.EX_MATERIALS_INSERT_FULL);
            }
            //获取其中的一个
            CfSaleInvoiceHeader cfSaleInvoiceHeader=cfSaleInvoiceHeaderTempList.get(0);
            BigDecimal scanningNumber =cfSaleInvoiceHeader.getScanningNumber().add(new BigDecimal(1));
            cfSaleInvoiceHeader.setScanningNumber(scanningNumber);
            cfSaleInvoiceHeader.setLastUpdatedBy(userId);
            cfSaleInvoiceHeader.setLastUpdateDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfSaleInvoiceHeaderMapper.updateById(cfSaleInvoiceHeader);

            //加入已扫描条码
            CfSaleInvoiceLine cfSaleInvoiceLine=new CfSaleInvoiceLine();
            cfSaleInvoiceLine.setObjectSetBasicAttribute(userId,thisNewDate);
            cfSaleInvoiceLine.setBarCodeNo(barCodeNo);
            cfSaleInvoiceLine.setInvoiceRootId(invoiceRootId);
            cfSaleInvoiceLine.setInvoiceId(cfSaleInvoiceHeader.getInvoiceId());
            cfSaleInvoiceLine.setMaterialName(cfSaleInvoiceHeader.getMaterialName());
            cfSaleInvoiceLine.setMaterialCode(cfSaleInvoiceHeader.getMaterialCode());
            cfSaleInvoiceLine.setBarCodeNumber(new BigDecimal(1));
            cfSaleInvoiceLine.setBatchNo(cfKtmReceivingOrder.getBatchNo());
            cfSaleInvoiceLine.setBarcodeType(CfSaleInvoiceLine.BARCODE_TYPE_KTM);
            cfSaleInvoiceLine.setOtherTableId(cfKtmReceivingOrder.getKtmReceivingId());
            cfSaleInvoiceLine.setInvoiceItem(cfSaleInvoiceHeader.getInvoiceItem());
            cfSaleInvoiceLine.setSalesItem(cfSaleInvoiceHeader.getSalesItem());
            cfSaleInvoiceLine.setSaleOrderNo(cfSaleInvoiceHeader.getSaleOrderNo());
            cfSaleInvoiceLine.setMode(cfSaleInvoiceHeader.getMode());
            this.insert(cfSaleInvoiceLine);
        }else {
            //判断是否是整车CP码
            CfBarcodeBind cfBarcodeBind =new CfBarcodeBind();
            cfBarcodeBind.setCar(barCodeNo);
            cfBarcodeBind=cfBarcodeBindMapper.selectOne(cfBarcodeBind);
            String barcodeType=CfSaleInvoiceLine.BARCODE_TYPE_CP;

            CfBarcodeInventory cfBarcodeInventory=new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(barCodeNo);
            cfBarcodeInventory=cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
            if(cfBarcodeInventory==null){
                throw  new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_NO_NOT_HAVING);
            }
            //如果整车CP码没有数据，就取库存的数据类型
            if(cfBarcodeBind==null){
                barcodeType=cfBarcodeInventory.getBarcodeType();
            }
            //判断条码数量是否还有
            if(cfBarcodeInventory.getBarCodeNumber().compareTo(new BigDecimal(1))<0){
                throw  new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_NUMBER);
            }
            //判断条码状态是否可用
            if(CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER.equals(cfBarcodeInventory.getState())){
                throw  new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_CANT_USER);
            }
            //条码代表库存数量
            BigDecimal barCodeNumberBefore=cfBarcodeInventory.getBarCodeNumber();

            //拿取销售发货单数据
            List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderList= cfSaleInvoiceHeaderMapper.selectList(new EntityWrapper<CfSaleInvoiceHeader>().
                    eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId)
                    .eq(CfSaleInvoiceHeader.INVOICE_MATERIAL_CODE_SQL,cfBarcodeInventory.getMaterialsNo())
                  //  .addFilter("need_number>scanning_number")
            );
            if(cfSaleInvoiceHeaderList.size()==0){
                throw  new ValidateCodeException(CfSaleInvoiceHeader.EX_MATERIALS_ERROR);
            }
            List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderTempList=new ArrayList<>();
            cfSaleInvoiceHeaderList.forEach(c-> {
                if(c.getNeedNumber().compareTo(c.getScanningNumber())>0){
                    cfSaleInvoiceHeaderTempList.add(c);
                }
            });
            if(cfSaleInvoiceHeaderTempList.size()==0){
                throw  new ValidateCodeException(CfSaleInvoiceHeader.EX_MATERIALS_INSERT_FULL);
            }
            List<CfSaleInvoiceLine> cfSaleInvoiceLineList=new ArrayList<>();
            for( CfSaleInvoiceHeader    cfSaleInvoiceHeader :cfSaleInvoiceHeaderTempList){
                //如果数量已经被扣除完，结束循环
                if(barCodeNumberBefore.compareTo(new BigDecimal(0))<=0){
                    break;
                }
                //添加数据
                BigDecimal scanningNumber =cfSaleInvoiceHeader.getScanningNumber().add(barCodeNumberBefore);
                BigDecimal barCodeNumber=barCodeNumberBefore;
                //加入的数据是否超过满仓需求数量
                if(scanningNumber.compareTo(cfSaleInvoiceHeader.getNeedNumber())>=0){
                    //如果大于就等于需求数量
                    scanningNumber=cfSaleInvoiceHeader.getNeedNumber();
                    //额外添加数量
                    barCodeNumber=cfSaleInvoiceHeader.getNeedNumber().subtract(cfSaleInvoiceHeader.getScanningNumber());

                }
                //计算剩余数据量
                barCodeNumberBefore=barCodeNumberBefore.subtract(barCodeNumber);

                cfSaleInvoiceHeader.setScanningNumber(scanningNumber);
                cfSaleInvoiceHeader.setLastUpdatedBy(userId);
                cfSaleInvoiceHeader.setLastUpdateDate(thisNewDate);
                //修改汇总已扫描条码数量
                cfSaleInvoiceHeaderMapper.updateById(cfSaleInvoiceHeader);
                //加入已扫描条码
                CfSaleInvoiceLine cfSaleInvoiceLine=new CfSaleInvoiceLine();
                cfSaleInvoiceLine.setObjectSetBasicAttribute(userId,thisNewDate);
                cfSaleInvoiceLine.setBarCodeNo(barCodeNo);
                cfSaleInvoiceLine.setInvoiceRootId(invoiceRootId);
                cfSaleInvoiceLine.setInvoiceId(cfSaleInvoiceHeader.getInvoiceId());
                cfSaleInvoiceLine.setMaterialName(cfSaleInvoiceHeader.getMaterialName());
                cfSaleInvoiceLine.setMaterialCode(cfSaleInvoiceHeader.getMaterialCode());
                cfSaleInvoiceLine.setBarCodeNumber(barCodeNumber);
                cfSaleInvoiceLine.setBatchNo(cfBarcodeInventory.getBatchNo());
                cfSaleInvoiceLine.setBarcodeType(barcodeType);
                cfSaleInvoiceLine.setWarehouse(cfBarcodeInventory.getWarehouse());
                cfSaleInvoiceLine.setOtherTableId(cfBarcodeInventory.getBarcodeInventoryId());
                cfSaleInvoiceLine.setWarehousePosition(cfBarcodeInventory.getWarehousePosition());
                cfSaleInvoiceLine.setInvoiceItem(cfSaleInvoiceHeader.getInvoiceItem());
                cfSaleInvoiceLine.setSalesItem(cfSaleInvoiceHeader.getSalesItem());
                cfSaleInvoiceLine.setSaleOrderNo(cfSaleInvoiceHeader.getSaleOrderNo());
                cfSaleInvoiceLine.setStorageArea(cfBarcodeInventory.getStorageArea());
                cfSaleInvoiceLine.setMode(cfSaleInvoiceHeader.getMode());
                cfSaleInvoiceLineList.add(cfSaleInvoiceLine);
            }
            this.insertBatch(cfSaleInvoiceLineList);
        }
        //获取更新过的数据
        Map<String, Object> resultMap=new HashedMap();
        RowBounds rowBounds =new RowBounds( 0,  QueryPage.LIMIT_10000);
        List<CfSaleInvoiceHeader> cfSaleInvoiceHeader=cfSaleInvoiceHeaderMapper.selectPage(rowBounds, new EntityWrapper<CfSaleInvoiceHeader>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId).orderBy(CfSaleInvoiceHeader.INVOICE_ID_SQL,true));

        List<CfSaleInvoiceLine> cfSaleInvoiceLineList=cfSaleInvoiceLineMapper.selectPage(rowBounds, new EntityWrapper<CfSaleInvoiceLine>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId).orderBy(CfSaleInvoiceLine.INVOICE_LINE_ID_SQL,true));

        //获取已扫描数据
        Page<CfSaleInvoiceLine> cfSaleInvoiceLinePage=new Page<>();
        cfSaleInvoiceLinePage.setRecords(cfSaleInvoiceLineList);


        //获取已扫描数据
        Page<CfSaleInvoiceHeader> cfSaleInvoiceHeaderPage=new Page<>();
        cfSaleInvoiceHeaderPage.setRecords(cfSaleInvoiceHeader);

        resultMap.put("cfSaleInvoiceHeaderPage",cfSaleInvoiceHeaderPage);
        resultMap.put("cfSaleInvoiceLinePage",cfSaleInvoiceLinePage);
        //发动条码
        //WP车架条码（库存条码）配件条码

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitCfSaleInvoiceData(int userId, Map<String, Object> params) throws Exception {
        String invoiceRootIdSt= params.getOrDefault("invoiceRootId", "").toString();
        if(!StringUtils.isNotBlank(invoiceRootIdSt)){
            throw  new ValidateCodeException(CfSaleInvoiceLine.EX_SALE_INVOICE_NO);
        }
        int invoiceRootId=Integer.parseInt(invoiceRootIdSt);

        //删除临时表数据行数据
        cfSaleInvoiceLineMapper.delete(new EntityWrapper<CfSaleInvoiceLine>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId));
        //删除临时表数据头数据
        cfSaleInvoiceHeaderMapper.delete(new EntityWrapper<CfSaleInvoiceHeader>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId));
        //删除临时表数据根数据
        cfSaleInvoiceRootMapper.deleteById(invoiceRootId);

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getCfSaleInvoiceDataPick(int userId, Map<String, Object> params) throws Exception {
        //通过invoiceNo查询单据头
        String invoiceNo= params.getOrDefault("invoiceNo", "").toString();
        String invoiceState= params.getOrDefault(CfSaleInvoiceRoot.INVOICE_STATE, "").toString();
        CfSaleInvoiceRoot cfSaleInvoiceRoot=new CfSaleInvoiceRoot();
        cfSaleInvoiceRoot.setInvoiceNo(invoiceNo);
        cfSaleInvoiceRoot.setInvoiceState(invoiceState);
        cfSaleInvoiceRoot= cfSaleInvoiceRootMapper.selectOne(cfSaleInvoiceRoot);
        Map<String, Object> resultMap=new HashedMap();
        Date newDate=new Date();
        int current=1;
        int size=QueryPage.LIMIT_10000;
        Page<CfSaleInvoiceLine> cfSaleInvoiceLinePage=null;
        RowBounds rowBounds =new RowBounds( 0,  size);
        if(cfSaleInvoiceRoot!=null){
            //获取已扫描数据
            cfSaleInvoiceLinePage=new Page<>( current,  size,  CfSaleInvoiceHeader.INVOICE_ID_SQL);
            // cfSaleInvoiceLin
            List<CfSaleInvoiceLine> cfSaleInvoiceLineList=cfSaleInvoiceLineMapper.selectPage(rowBounds, new EntityWrapper<CfSaleInvoiceLine>().
                    eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,cfSaleInvoiceRoot.getInvoiceRootId()).orderBy(CfSaleInvoiceLine.INVOICE_LINE_ID_SQL,true));
            cfSaleInvoiceLinePage.setRecords(cfSaleInvoiceLineList);
        }else{
            Map<String,Object> paramMap =new HashedMap();
            paramMap.put("functionName","ZMM_BC_023");
            Map<String,Object> dataMap =new HashedMap();
            dataMap.put("IV_ZJPDBM",invoiceNo);
            paramMap.put("paramMap",dataMap);
            R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMap);
            if(result.getCode()!=0){
                throw  new ValidateCodeException(result.getMsg());
            }
            Map<String,Object> resultMapData=result.getData();
            JSONObject jsonObject =new JSONObject(resultMapData);
            if(!jsonObject.getString("EV_STATUS").equals("1")){
                throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
            }
            // ET_DATA
            JSONArray jsonArray=jsonObject.getJSONArray("ET_DATA");
            List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderList=new ArrayList<>();
            for (int i=0;i<jsonArray.size();i++) {
                JSONObject etData=jsonArray.getJSONObject(i);
                if(cfSaleInvoiceRoot==null&&i==0){
                    cfSaleInvoiceRoot=new CfSaleInvoiceRoot();
                    cfSaleInvoiceRoot.setInvoiceNo(invoiceNo);
                    cfSaleInvoiceRoot.setInvoiceState(invoiceState);
                    cfSaleInvoiceRoot.setDepartment(etData.getString("VKBUR"));
                    cfSaleInvoiceRoot.setObjectSetBasicAttribute(userId,newDate);
                    cfSaleInvoiceRoot.setObjectVersionNumber(1);
                    cfSaleInvoiceRootMapper.insert(cfSaleInvoiceRoot);
                }
                CfSaleInvoiceHeader  cfSaleInvoiceHeader= new CfSaleInvoiceHeader();
                cfSaleInvoiceHeader.setObjectSetBasicAttribute(userId,newDate);
                cfSaleInvoiceHeader.setInvoiceRootId(cfSaleInvoiceRoot.getInvoiceRootId());
                //VBELN  发货通知单
                cfSaleInvoiceHeader.setInvoiceNo(etData.getString("ZJPDBM"));
                //MAKTX  物料名称
                cfSaleInvoiceHeader.setMaterialName(etData.getString("NAME1"));
                //MATNR  物料代码
                cfSaleInvoiceHeader.setMaterialCode(etData.getString("MATNR"));
                //WRKST  规格型号
                cfSaleInvoiceHeader.setMode(etData.getString("ZCJHM"));
                //LFIMG  需求数量
                cfSaleInvoiceHeader.setNeedNumber(new BigDecimal(etData.getString("LFIMG")));

                cfSaleInvoiceHeader.setWarehouse(etData.getString("LGORT"));
                cfSaleInvoiceHeader.setScanningNumber(new BigDecimal(0));
                cfSaleInvoiceHeader.setObjectVersionNumber(1);
                cfSaleInvoiceHeaderList.add(cfSaleInvoiceHeader);
               // cfSaleInvoiceHeaderMapper.insert(cfSaleInvoiceHeader);
            }
            iCfSaleInvoiceHeaderService.insertBatch(cfSaleInvoiceHeaderList);
        }
        Page<CfSaleInvoiceHeader> cfSaleInvoiceHeaderPage=new Page<>(  current,  size,  CfSaleInvoiceHeader.INVOICE_ID_SQL);
        List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderList=cfSaleInvoiceHeaderMapper.selectPage(rowBounds, new EntityWrapper<CfSaleInvoiceHeader>().
                eq(CfSaleInvoiceHeader.INVOICE_ROOT_ID_SQL,cfSaleInvoiceRoot.getInvoiceRootId()));
        cfSaleInvoiceHeaderPage.setRecords(cfSaleInvoiceHeaderList);
        resultMap.put("cfSaleInvoiceRoot",cfSaleInvoiceRoot);
        resultMap.put("cfSaleInvoiceHeaderPage",cfSaleInvoiceHeaderPage);
        resultMap.put("cfSaleInvoiceLinePage",cfSaleInvoiceLinePage);
        return resultMap;
    }


}
