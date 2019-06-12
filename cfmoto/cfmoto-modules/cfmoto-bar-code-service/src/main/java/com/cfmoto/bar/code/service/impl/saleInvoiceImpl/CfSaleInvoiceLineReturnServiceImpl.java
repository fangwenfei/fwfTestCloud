package com.cfmoto.bar.code.service.impl.saleInvoiceImpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.*;
import com.cfmoto.bar.code.model.entity.*;

import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceLineReturnService;
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
import java.text.SimpleDateFormat;
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
public class CfSaleInvoiceLineReturnServiceImpl extends ServiceImpl<CfSaleInvoiceLineMapper, CfSaleInvoiceLine> implements ICfSaleInvoiceLineReturnService {

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

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
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
            if(cfKtmReceivingOrder.getBarCodeNumber().compareTo(new BigDecimal(0))!=0){
                throw  new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_NUMBER);
            }
            //拿取销售发货单数据
            List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderList= cfSaleInvoiceHeaderMapper.selectList(new EntityWrapper<CfSaleInvoiceHeader>().
                    eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId)
                    .eq(CfSaleInvoiceHeader.INVOICE_MATERIAL_CODE_SQL,cfKtmReceivingOrder.getMaterialsNo())
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
            cfSaleInvoiceLine.setWarehouse(cfSaleInvoiceHeader.getWarehouse());
            cfSaleInvoiceLine.setMode(cfSaleInvoiceHeader.getMode());
            this.insert(cfSaleInvoiceLine);
        }else {
            //判断是否是整车CP码
            CfBarcodeBind cfBarcodeBind = new CfBarcodeBind();
            cfBarcodeBind.setCar(barCodeNo);
            cfBarcodeBind = cfBarcodeBindMapper.selectOne(cfBarcodeBind);
            String barcodeType = CfSaleInvoiceLine.BARCODE_TYPE_CP;

            CfBarcodeInventory cfBarcodeInventory = new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(barCodeNo);
            cfBarcodeInventory = cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
            if (cfBarcodeInventory == null) {
                throw new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_NO_NOT_HAVING);
            }
            //如果整车CP码没有数据，就取库存的数据类型
            if (cfBarcodeBind == null) {
                barcodeType = cfBarcodeInventory.getBarcodeType();
            }
            if(!StringUtils.isNotBlank(barcodeType)){
                throw new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_BARCODE_TYPE_NULL);
            }
            if(!(barcodeType.equals(CfSaleInvoiceLine.BARCODE_TYPE_CP)||barcodeType.equals(CfSaleInvoiceLine.BARCODE_TYPE_EG))){
                throw new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_BARCODE_TYPE_ERROR);
            }
            //判断条码数量是否还有
            if (cfBarcodeInventory.getBarCodeNumber().compareTo(new BigDecimal(0)) != 0) {
                throw new ValidateCodeException(CfSaleInvoiceLine.EX_BAR_CODE_IN_WAREHOUSE);
            }
            //条码代表库存数量
            BigDecimal barCodeNumberBefore=cfBarcodeInventory.getBarCodeNumber();
            //拿取销售发货单数据
            List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderList= cfSaleInvoiceHeaderMapper.selectList(new EntityWrapper<CfSaleInvoiceHeader>().
                    eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId)
                    .eq(CfSaleInvoiceHeader.INVOICE_MATERIAL_CODE_SQL,cfBarcodeInventory.getMaterialsNo())
                 //   .addFilter("need_number>scanning_number")
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

            //拿取销售发货单数据
            CfSaleInvoiceHeader  cfSaleInvoiceHeader =cfSaleInvoiceHeaderTempList.get(0);
            BigDecimal scanningNumberAddOne = cfSaleInvoiceHeader.getScanningNumber().add(new BigDecimal(1));
            //判断是否已经满仓
            if (scanningNumberAddOne.compareTo(cfSaleInvoiceHeader.getNeedNumber()) > 0) {
                throw new ValidateCodeException(CfSaleInvoiceLine.EX_BARCODE_NUMBER);
            }
            cfSaleInvoiceHeader.setScanningNumber(scanningNumberAddOne);
            cfSaleInvoiceHeader.setLastUpdatedBy(userId);
            cfSaleInvoiceHeader.setLastUpdateDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfSaleInvoiceHeaderMapper.updateById(cfSaleInvoiceHeader);
            //加入已扫描条码
            //加入已扫描条码
            CfSaleInvoiceLine cfSaleInvoiceLine=new CfSaleInvoiceLine();
            cfSaleInvoiceLine.setObjectSetBasicAttribute(userId,thisNewDate);
            cfSaleInvoiceLine.setBarCodeNo(barCodeNo);
            cfSaleInvoiceLine.setInvoiceRootId(invoiceRootId);
            cfSaleInvoiceLine.setInvoiceId(cfSaleInvoiceHeader.getInvoiceId());
            cfSaleInvoiceLine.setMaterialName(cfSaleInvoiceHeader.getMaterialName());
            cfSaleInvoiceLine.setMaterialCode(cfSaleInvoiceHeader.getMaterialCode());
            cfSaleInvoiceLine.setBarCodeNumber(new BigDecimal(1));
            cfSaleInvoiceLine.setBatchNo(cfBarcodeInventory.getBatchNo());
            cfSaleInvoiceLine.setBarcodeType(barcodeType);
            cfSaleInvoiceLine.setWarehouse(cfSaleInvoiceHeader.getWarehouse());
            cfSaleInvoiceLine.setOtherTableId(cfBarcodeInventory.getBarcodeInventoryId());
            cfSaleInvoiceLine.setWarehousePosition(cfBarcodeInventory.getWarehousePosition());
            cfSaleInvoiceLine.setInvoiceItem(cfSaleInvoiceHeader.getInvoiceItem());
            cfSaleInvoiceLine.setSalesItem(cfSaleInvoiceHeader.getSalesItem());
            cfSaleInvoiceLine.setSaleOrderNo(cfSaleInvoiceHeader.getSaleOrderNo());
            cfSaleInvoiceLine.setStorageArea(cfBarcodeInventory.getStorageArea());;
            cfSaleInvoiceLine.setMode(cfSaleInvoiceHeader.getMode());
            this.insert(cfSaleInvoiceLine);
        }
        //获取更新过的数据
        Map<String, Object> resultMap=new HashedMap();
        RowBounds rowBounds =new RowBounds( 0,  QueryPage.LIMIT_10000);
        List<CfSaleInvoiceHeader> cfSaleInvoiceHeaderList=cfSaleInvoiceHeaderMapper.selectPage(rowBounds, new EntityWrapper<CfSaleInvoiceHeader>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId).orderBy(CfSaleInvoiceHeader.INVOICE_ID_SQL,true));

        List<CfSaleInvoiceLine> cfSaleInvoiceLineList=cfSaleInvoiceLineMapper.selectPage(rowBounds, new EntityWrapper<CfSaleInvoiceLine>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId).orderBy(CfSaleInvoiceLine.INVOICE_LINE_ID_SQL,true));

        //获取已扫描数据
        Page<CfSaleInvoiceLine> cfSaleInvoiceLinePage=new Page<>();
        cfSaleInvoiceLinePage.setRecords(cfSaleInvoiceLineList);


        //获取已扫描数据
        Page<CfSaleInvoiceHeader> cfSaleInvoiceHeaderPage=new Page<>();
        cfSaleInvoiceHeaderPage.setRecords(cfSaleInvoiceHeaderList);

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
        String submitDate= params.getOrDefault("submitDate", "").toString();
        Date thisNewDate=new Date() ;
        if(!StringUtils.isNotBlank(invoiceRootIdSt)){
            throw  new ValidateCodeException(CfSaleInvoiceLine.EX_SALE_INVOICE_NO);
        }
        int invoiceRootId=Integer.parseInt(invoiceRootIdSt);
        //提交数据到sap
        List<CfSaleInvoiceLine> cfSaleInvoiceLineList=cfSaleInvoiceLineMapper.selectList(new EntityWrapper<CfSaleInvoiceLine>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,invoiceRootId));

        CfSaleInvoiceRoot cfSaleInvoiceRoot= cfSaleInvoiceRootMapper.selectById(invoiceRootId);

        //添加数量设置为1
        CfBarcodeInventory cfBarcodeInventory=null;
        BigDecimal number1=new BigDecimal(1);
        Map<String,Object> paramMapSap =new HashedMap();
        paramMapSap.put("functionName","ZMM_BC_006");
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IV_VBELN",cfSaleInvoiceRoot.getInvoiceNo());
        ArrayList< Map<String,String>> ItDataArray=new ArrayList<>();
        CfKtmReceivingOrder cfKtmReceivingOrder=null;
        for (CfSaleInvoiceLine cfSaleInvoiceLine:cfSaleInvoiceLineList ) {
            Map<String,String> tableMap=new HashedMap();
            //行项目
            tableMap.put("POSNR",StringUtils.trimToEmpty(cfSaleInvoiceLine.getInvoiceItem()));
            //物料名称
            tableMap.put("MAKTX",StringUtils.trimToEmpty(cfSaleInvoiceLine.getMaterialName()) );
            //规格型号
            tableMap.put("WRKST",StringUtils.trimToEmpty(cfSaleInvoiceLine.getMode()));
            //物料代码
            tableMap.put("MATNR",StringUtils.trimToEmpty(cfSaleInvoiceLine.getMaterialCode()) );
            //条码类型
            tableMap.put("TMLX",StringUtils.trimToEmpty(cfSaleInvoiceLine.getBarcodeType()));
            //序列号
            tableMap.put("GERNR",StringUtils.trimToEmpty(cfSaleInvoiceLine.getBarCodeNo()) );
            //批次
            tableMap.put("CHARG",StringUtils.trimToEmpty(cfSaleInvoiceLine.getBatchNo()));
            //数量
            tableMap.put("LFIMG",cfSaleInvoiceLine.getBarCodeNumber().toString());
            //仓库
            tableMap.put("LGORT",StringUtils.trimToEmpty(cfSaleInvoiceLine.getWarehouse()));
            //存储区域
            tableMap.put("VLTYP",StringUtils.trimToEmpty(cfSaleInvoiceLine.getStorageArea()));
            //存储区域
            tableMap.put("VLTYP",StringUtils.trimToEmpty(cfSaleInvoiceLine.getStorageArea()));
            //仓位
            tableMap.put("VLPLA",StringUtils.trimToEmpty(cfSaleInvoiceLine.getWarehousePosition()));
            //销售订单
            tableMap.put("VGBEL",StringUtils.trimToEmpty(cfSaleInvoiceLine.getSaleOrderNo()));
            //销售订单行项目
            tableMap.put("VGPOS",StringUtils.trimToEmpty(cfSaleInvoiceLine.getSalesItem()));
            //操作账号
            tableMap.put("ZUSER",String.valueOf(userId));
            //操作时间
            tableMap.put("ZDATE",this.simpleDateFormat.format(thisNewDate));
            //货物柜号
            tableMap.put("ZMM01",StringUtils.trimToEmpty(cfSaleInvoiceLine.getCabinetNo()) );
            //货柜铅封号
            tableMap.put("ZMM02",StringUtils.trimToEmpty(cfSaleInvoiceLine.getContainerSealNo()) );
            //运单号
            tableMap.put("ZMM03",StringUtils.trimToEmpty(cfSaleInvoiceLine.getWaybillNo()));
            ItDataArray.add(tableMap);
            if(CfSaleInvoiceLine.BARCODE_TYPE_KTM.equals(cfSaleInvoiceLine.getBarcodeType())){
                 cfKtmReceivingOrder=new CfKtmReceivingOrder();
                 cfKtmReceivingOrder.setBarCodeNumber(number1);
                 cfKtmReceivingOrder.setKtmReceivingId(cfSaleInvoiceLine.getOtherTableId());
                 cfKtmReceivingOrder.setRepository(cfSaleInvoiceLine.getWarehouse());
                 cfKtmReceivingOrderMapper.updateById(cfKtmReceivingOrder);
            }else{
                cfBarcodeInventory=new CfBarcodeInventory();
                cfBarcodeInventory.setBarcodeInventoryId(cfSaleInvoiceLine.getOtherTableId());
                cfBarcodeInventory.setBarCodeNumber(number1);
                cfBarcodeInventory.setWarehouse(cfSaleInvoiceLine.getWarehouse());
                cfBarcodeInventory.setStorageArea("");
                cfBarcodeInventory.setWarehousePosition("");
                cfBarcodeInventory.setSaleOrderNo("");
                cfBarcodeInventory.setSalesItem(CfBarcodeInventory.BARCODE_SALES_ITEM_DEFAULT);
                cfBarcodeInventoryMapper.updateById(cfBarcodeInventory);
            }
        }
        dataMap.put("IT_DATA",ItDataArray);
        paramMapSap.put("paramMap",dataMap);
        R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMapSap);
        if(result.getCode()!=0){
            throw  new ValidateCodeException(CfSaleInvoiceHeader.CF_SALE_INVOICE_HEADER_SAP);
        }
        if(!("1".equals(result.getData().get("EV_STATUS").toString()))){
            throw  new ValidateCodeException(result.getData().get("EV_MESSAGE").toString());
        }
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
}
