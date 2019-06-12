package com.cfmoto.bar.code.service.impl.stock;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.mapper.*;
import com.cfmoto.bar.code.model.entity.*;
import com.cfmoto.bar.code.service.ICfStockReturnLineService;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-19
 */
@Service
public class CfStockReturnLineServiceImpl extends ServiceImpl<CfStockReturnLineMapper, CfStockReturnLine> implements ICfStockReturnLineService {
    @Autowired
    UserFeignService userFeignService;

    @Autowired
    CfStockReturnRootMapper cfStockReturnRootMapper;

    @Autowired
    CfStockReturnHeaderMapper cfStockReturnHeaderMapper ;

    @Autowired
    CfKtmReceivingOrderMapper cfKtmReceivingOrderMapper ;


    @Autowired
    CfBarcodeInventoryMapper cfBarcodeInventoryMapper ;

    @Autowired
    SapFeignService sapFeignService ;

    /**
     * 获取退货的数据信息
     * @param userId
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getDataByStockByNo(int userId, Map<String, Object> params) throws Exception {
        //通过stockListNo查询单据头
        String stockListNo= params.getOrDefault("stockListNo", "").toString();
        CfStockReturnRoot cfStockReturnRoot =new CfStockReturnRoot();
        cfStockReturnRoot.setStockListNo(stockListNo);
        cfStockReturnRoot=cfStockReturnRootMapper.selectOne(cfStockReturnRoot);
        Map<String, Object> resultMap=new HashedMap();
        Date newDate=new Date();
        if(cfStockReturnRoot!=null){
            int current=1;
            int size=10;
            //获取已扫描数据
            Page<CfStockReturnLine> headerPage=new Page<>( current,  size,  CfStockReturnLine.STOCK_LINE_ID_SQL,false);
            Page  cfStockReturnLinePage=this.selectPage(headerPage, new EntityWrapper<CfStockReturnLine>().
                    eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,cfStockReturnRoot.getStockRootId()));

            //通过单据头获取汇总数据
            Page<CfStockReturnHeader> cfStockReturnHeaderPage=new Page<>( current,  size,  CfStockReturnLine.STOCK_ROOT_ID_SQL,false);

            // CfStockReturnHeader
            RowBounds rowBounds =new RowBounds( 0,  size);
            Wrapper entityWrapper= new EntityWrapper<CfStockReturnHeader>()
                    .eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,cfStockReturnRoot.getStockRootId())
                    .orderBy(CfStockReturnLine.LAST_UPDATED_DATE_SQL,false);
            List<CfStockReturnHeader> cfStockReturnHeaderList=cfStockReturnHeaderMapper.selectPage(rowBounds,entityWrapper);
            cfStockReturnHeaderPage.setRecords(cfStockReturnHeaderList);
            //将数据封装到界面
            resultMap.put("cfStockReturnRoot",cfStockReturnRoot);
            resultMap.put("cfStockReturnHeaderPage",cfStockReturnHeaderPage);
            resultMap.put("cfStockReturnLinePage",cfStockReturnLinePage);
        }else{
            // TODO SAP 接口获取数据 仓库备料
        }

        return resultMap;
    }


    /**
     * 添加扫描的数据扫描
     * @param userId
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> addScanLineData(int userId, Map<String, Object> params) throws Exception {
        String barCodeNo= params.getOrDefault("barCodeNo", "").toString();
        String stockRootIdSt= params.getOrDefault("stockRootId", "").toString();
        String checkRecord= params.getOrDefault("checkRecord", "N").toString();
        String repository= params.getOrDefault("repository", "").toString();
        Date thisNewDate=new Date() ;
        if(!StringUtils.isNotBlank(barCodeNo)){
            throw  new ValidateCodeException(CfStockReturnLine.EX_BAR_CODE_NO);
        }
        if(!StringUtils.isNotBlank(stockRootIdSt)){
            throw  new ValidateCodeException(CfStockReturnLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        if(!StringUtils.isNotBlank(repository)){
            throw  new ValidateCodeException(CfStockReturnLine.EX_REPOSITORY_NOT_HAVING);
        }

        int stockRootId=Integer.parseInt(stockRootIdSt);
        CfKtmReceivingOrder cfKtmReceivingOrder=new CfKtmReceivingOrder();
        cfKtmReceivingOrder.setFrameNo(barCodeNo);
        cfKtmReceivingOrder= cfKtmReceivingOrderMapper.selectOne(cfKtmReceivingOrder);


        if(cfKtmReceivingOrder!=null){
            //判断条码数量是否还有
            if(cfKtmReceivingOrder.getBarCodeNumber().compareTo(new BigDecimal(0))!=0){
                throw  new ValidateCodeException(CfStockReturnLine.EX_BAR_CODE_NUMBER_USED);
            }
            //拿取销售发货单数据
            CfStockReturnHeader cfStockReturnHeader=new CfStockReturnHeader();
            cfStockReturnHeader.setMaterialsNo(cfKtmReceivingOrder.getMaterialsNo());
            cfStockReturnHeader.setMaterialsName(cfKtmReceivingOrder.getMaterialsName());
            cfStockReturnHeader.setStockRootId(stockRootId);
            //加入人员的过滤

            cfStockReturnHeader=cfStockReturnHeaderMapper.selectOne(cfStockReturnHeader);
            if(cfStockReturnHeader==null){
                throw  new ValidateCodeException(CfStockReturnLine.EX_MATERIALS_NOT_HAVING);
            }
            BigDecimal scanningNumber =cfStockReturnHeader.getActualSendNumber().add(new BigDecimal(1));
            if(scanningNumber.compareTo(cfStockReturnHeader.getShouldSendNumber())>0){
                throw  new ValidateCodeException(CfStockReturnLine.EX_BARCODE_NUMBER);
            }
            cfStockReturnHeader.setActualSendNumber(scanningNumber);
            cfStockReturnHeader.setLastUpdatedBy(userId);
            cfStockReturnHeader.setLastUpdateDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfStockReturnHeaderMapper.updateById(cfStockReturnHeader);
            //加入已扫描条码
            CfStockReturnLine cfStockReturnLine =new CfStockReturnLine();
            cfStockReturnLine.setObjectSetBasicAttribute(userId,thisNewDate);
            cfStockReturnLine.setStockListNo(cfStockReturnHeader.getStockListNo());
            cfStockReturnLine.setBarcode(barCodeNo);
            cfStockReturnLine.setStockRootId(stockRootId);
            cfStockReturnLine.setStockHeaderId(cfStockReturnHeader.getStockHeaderId());
            cfStockReturnLine.setMaterialsName(cfStockReturnHeader.getMaterialsName());
            cfStockReturnLine.setMaterialsNo(cfStockReturnHeader.getMaterialsNo());
            cfStockReturnLine.setNumber(new BigDecimal(1));
            cfStockReturnLine.setBatchNo(cfKtmReceivingOrder.getBatchNo());
            cfStockReturnLine.setBarcodeType(CfStockReturnLine.BARCODE_TYPE_KTM);
            cfStockReturnLine.setOtherTableId(cfKtmReceivingOrder.getKtmReceivingId());
            cfStockReturnLine.setRepository(repository);
            this.insert(cfStockReturnLine);
        }else{
            //判断是否是整车CP码
            CfBarcodeInventory cfBarcodeInventory=new CfBarcodeInventory();
            cfBarcodeInventory.setBarcode(barCodeNo);
            cfBarcodeInventory=cfBarcodeInventoryMapper.selectOne(cfBarcodeInventory);
            if(cfBarcodeInventory==null){
                throw  new ValidateCodeException(CfStockReturnLine.EX_BAR_CODE_NO_NOT_HAVING);
            }
            String barcodeType=cfBarcodeInventory.getBarcodeType();
             if(CfStockReturnLine.BARCODE_TYPE_CP.equals(barcodeType)||CfStockReturnLine.BARCODE_TYPE_EG.equals(barcodeType)){
                  //判断条码数量是否被征用
                 if(cfBarcodeInventory.getBarCodeNumber().compareTo(new BigDecimal(0))!=0){
                     throw  new ValidateCodeException(CfStockReturnLine.EX_BAR_CODE_NUMBER_USED);
                 }
                 //默认cp eg 的退料一次退数量为1.
                 cfBarcodeInventory.setBarCodeNumber(new BigDecimal(1));
             }else{
                 //判断条码状态是否可用
                 if(!CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER.equals(cfBarcodeInventory.getState())){
                     throw  new ValidateCodeException(CfStockReturnLine.EX_BAR_CODE_CANT_USER);
                 }
             }
            //拿取销售发货单数据
            CfStockReturnHeader cfStockReturnHeader=new CfStockReturnHeader();
            cfStockReturnHeader.setMaterialsNo(cfBarcodeInventory.getMaterialsNo());
            cfStockReturnHeader.setMaterialsName(cfBarcodeInventory.getMaterialsName());
            cfStockReturnHeader.setStockRootId(stockRootId);
            cfStockReturnHeader=cfStockReturnHeaderMapper.selectOne(cfStockReturnHeader);
            if(cfStockReturnHeader==null){
                throw  new ValidateCodeException(CfStockReturnLine.EX_MATERIALS_NOT_HAVING);
            }
            BigDecimal scanningNumberAddOne =cfStockReturnHeader.getActualSendNumber().add(new BigDecimal(1));
            //判断是否已经满仓
            if(scanningNumberAddOne.compareTo(cfStockReturnHeader.getShouldSendNumber())>0){
                throw  new ValidateCodeException(CfStockReturnLine.EX_BARCODE_NUMBER);
            }
            //添加数据
            BigDecimal scanningNumber =cfStockReturnHeader.getActualSendNumber().add(cfBarcodeInventory.getBarCodeNumber());
            BigDecimal barCodeNumber=cfBarcodeInventory.getBarCodeNumber();
            //加入的数据是否超过满仓需求数量
            if(scanningNumber.compareTo(cfStockReturnHeader.getShouldSendNumber())>0){
                //如果大于就等于需求数量
                scanningNumber=cfStockReturnHeader.getShouldSendNumber();
                //额外添加数量
                barCodeNumber=cfStockReturnHeader.getShouldSendNumber().subtract(cfStockReturnHeader.getActualSendNumber());
            }

            cfStockReturnHeader.setActualSendNumber(scanningNumber);
            cfStockReturnHeader.setLastUpdatedBy(userId);
            cfStockReturnHeader.setLastUpdateDate(thisNewDate);
            //修改汇总已扫描条码数量
            cfStockReturnHeaderMapper.updateById(cfStockReturnHeader);
            //加入已扫描条码
            CfStockReturnLine cfStockReturnLine=new CfStockReturnLine();
            cfStockReturnLine.setObjectSetBasicAttribute(userId,thisNewDate);
            cfStockReturnLine.setStockListNo(cfStockReturnHeader.getStockListNo());
            cfStockReturnLine.setBarcode(barCodeNo);
            cfStockReturnLine.setStockRootId(stockRootId);
            cfStockReturnLine.setStockHeaderId(cfStockReturnHeader.getStockHeaderId());
            cfStockReturnLine.setMaterialsName(cfStockReturnHeader.getMaterialsName());
            cfStockReturnLine.setMaterialsNo(cfStockReturnHeader.getMaterialsNo());
            cfStockReturnLine.setNumber(barCodeNumber);
            cfStockReturnLine.setBatchNo(cfBarcodeInventory.getBatchNo());
            cfStockReturnLine.setRepository(repository);
            cfStockReturnLine.setBarcodeType(barcodeType);
            cfStockReturnLine.setOtherTableId(cfBarcodeInventory.getBarcodeInventoryId());
            this.insert(cfStockReturnLine);
        }
        //获取更新过的数据
        Map<String, Object> resultMap=new HashedMap();
        int current=1;
        int size=10;
        //通过单据头获取汇总数据
        Page<CfStockReturnLine> headerPage=new Page<>( current,  size);
        Page  cfStockReturnLinePage=this.selectPage(headerPage, new EntityWrapper<CfStockReturnLine>().
                eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId).orderBy(CfStockReturnLine.STOCK_LINE_ID_SQL,false));
        //获取已扫描数据
        Page<CfStockReturnHeader> cfStockReturnHeaderPage=new Page<>( current,  size);
        //设置查询条件
        Wrapper  entityWrapper= new EntityWrapper<CfStockReturnHeader>()
                .eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId).orderBy(CfStockReturnLine.LAST_UPDATED_DATE_SQL,false);
        ;
        if(!CfStockReturnLine.PARAMS_N.equals(checkRecord)){
            entityWrapper=entityWrapper.addFilter("should_send_number>actual_send_number");
        }
        // CfStockReturnHeader
        RowBounds rowBounds =new RowBounds( 0,  size);
        List<CfStockReturnHeader> cfStockReturnHeaderList=cfStockReturnHeaderMapper.selectPage(rowBounds,entityWrapper);

        cfStockReturnHeaderPage.setRecords(cfStockReturnHeaderList);
        //将数据封装到界面
        resultMap.put("cfStockReturnHeaderPage",cfStockReturnHeaderPage);
        resultMap.put("cfStockReturnLinePage",cfStockReturnLinePage);
        return resultMap;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteDataByBarCodeNo(int userId, Map<String, Object> params) throws Exception {

        String checkRecord= params.getOrDefault("checkRecord", "N").toString();
        Integer  stockLineId= Integer.parseInt(params.getOrDefault("stockLineId", "").toString());
        Integer  stockRootId= Integer.parseInt(params.getOrDefault("stockRootId", "").toString());
        Integer  stockHeaderId= Integer.parseInt(params.getOrDefault("stockHeaderId", "").toString());
        BigDecimal  number= new BigDecimal(params.getOrDefault("number", "").toString());
        CfStockReturnHeader cfStockReturnHeader= cfStockReturnHeaderMapper.selectById(stockHeaderId);
        cfStockReturnHeader.setActualSendNumber(cfStockReturnHeader.getActualSendNumber().subtract(number));
        cfStockReturnHeaderMapper.updateById(cfStockReturnHeader);
        this.deleteById(stockLineId);
        //获取更新过的数据
        Map<String, Object> resultMap=new HashedMap();
        int current=1;
        int size=10;
        //通过单据头获取汇总数据
        Page<CfStockReturnLine> linePage=new Page<>( current,  size,  CfStockReturnLine.STOCK_LINE_ID_SQL,false);
        Page  cfStockReturnLinePage=this.selectPage(linePage, new EntityWrapper<CfStockReturnLine>().
                eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId).orderBy(CfStockReturnLine.STOCK_LINE_ID_SQL,false));
        //获取已扫描数据
        Page<CfStockReturnHeader> cfStockReturnHeaderPage=new Page<>( current,  size);

        // CfStockReturnHeader
        RowBounds rowBounds =new RowBounds( 0,  size);
        Wrapper  entityWrapper= new EntityWrapper<CfStockReturnHeader>()
                .eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId);
        if(CfStockReturnLine.PARAMS_N.equals(checkRecord)){
            entityWrapper=entityWrapper.orderBy(CfStockReturnLine.LAST_UPDATED_DATE_SQL,false);

        }else{
            entityWrapper=entityWrapper.orderBy(CfStockReturnLine.LAST_UPDATED_DATE_SQL,false).addFilter("should_send_number>actual_send_number");
        }

        List<CfStockReturnHeader> cfStockReturnHeaderList=cfStockReturnHeaderMapper.selectPage(rowBounds,entityWrapper);

        cfStockReturnHeaderPage.setRecords(cfStockReturnHeaderList);
        //将数据封装到界面
        resultMap.put("cfStockReturnHeaderPage",cfStockReturnHeaderPage);
        resultMap.put("cfStockReturnLinePage",cfStockReturnLinePage);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitICfStockReturnLineData(int userId, Map<String, Object> params) throws Exception {
        String stockRootIdSt= params.getOrDefault("stockRootId", "").toString();
        if(!StringUtils.isNotBlank(stockRootIdSt)){
            throw  new ValidateCodeException(CfStockReturnLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }
        int stockRootId=Integer.parseInt(stockRootIdSt);

        List<CfStockReturnLine> cfStockReturnLineList=this.selectList(new EntityWrapper<CfStockReturnLine>().
                eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId).eq(CfStockReturnLine.CREATED_BY_SQL,userId));
        //扣除数量以及sap数据封装
        CfKtmReceivingOrder cfKtmReceivingOrder=null;
        BigDecimal number0=new BigDecimal(0);
        for (CfStockReturnLine cfStockReturnLine:cfStockReturnLineList){
            //TODO 需要sap的处理

            if(CfStockReturnLine.BARCODE_TYPE_KTM.equals(cfStockReturnLine.getBarcodeType())){
                cfKtmReceivingOrder=new CfKtmReceivingOrder();
                cfKtmReceivingOrder.setBarCodeNumber(new BigDecimal(1));
                cfKtmReceivingOrder.setKtmReceivingId(cfStockReturnLine.getOtherTableId());
                cfKtmReceivingOrder.setRepository(cfStockReturnLine.getRepository());
                cfKtmReceivingOrderMapper.updateById(cfKtmReceivingOrder);
            }else{
                CfBarcodeInventory    cfBarcodeInventory =cfBarcodeInventoryMapper.selectById(cfStockReturnLine.getOtherTableId());

                cfBarcodeInventory.setWarehouse(cfStockReturnLine.getRepository());
                cfBarcodeInventory.setStorageArea("");
                cfBarcodeInventory.setWarehousePosition("");
                if(CfStockReturnLine.BARCODE_TYPE_CP.equals(cfBarcodeInventory.getBarcodeType())
                        ||CfStockReturnLine.BARCODE_TYPE_EG.equals(cfBarcodeInventory.getBarcodeType())){
                    cfBarcodeInventory.setBarCodeNumber(new BigDecimal(1));
                }else{
                    cfBarcodeInventory.setBarCodeNumber(cfBarcodeInventory.getBarCodeNumber().add(cfStockReturnLine.getNumber()));
                    cfBarcodeInventory.setState(CfStockReturnLine.PARAMS_Y);
                }
                cfBarcodeInventoryMapper.updateById(cfBarcodeInventory);
            }
        }
        //删除临时表数据行数据
        this.delete(new EntityWrapper<CfStockReturnLine>().
                eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId));
        //删除汇总数据
        Wrapper  entityWrapper= new EntityWrapper<CfStockReturnHeader>()
                .eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId);
        cfStockReturnHeaderMapper.delete(entityWrapper);
        //删除头数据
        cfStockReturnRootMapper.deleteById(stockRootId);

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page getCfStockReturnHeaderPage(int userId, Map<String, Object> params) throws Exception {
        Integer  page= Integer.parseInt(params.getOrDefault("page", 1).toString());
        Integer  limit= Integer.parseInt(params.getOrDefault("limit", 10).toString());
        String stockRootIdSt= params.getOrDefault("stockRootId", "").toString();
        String checkRecord= params.getOrDefault("checkRecord", "N").toString();
        if(!StringUtils.isNotBlank(stockRootIdSt)){
            throw  new ValidateCodeException(CfStockReturnLine.EX_STOCK_LIST_NO_NOT_HAVING);
        }

        int stockRootId=Integer.parseInt(stockRootIdSt);
        //设置查询条件
        //获取已扫描数据
        Page<CfStockReturnHeader> cfStockReturnHeaderPage=new Page<>( page,  limit,  CfStockReturnLine.STOCK_LINE_ID_SQL,false);
        Wrapper  entityWrapper= new EntityWrapper<CfStockReturnHeader>()
                .eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId);
        if(CfStockReturnLine.PARAMS_N.equals(checkRecord)){
            entityWrapper=entityWrapper.orderBy(CfStockReturnLine.LAST_UPDATED_DATE_SQL,false);
        }else{
            entityWrapper=entityWrapper.orderBy(CfStockReturnLine.LAST_UPDATED_DATE_SQL,false).addFilter("should_send_number>actual_send_number");
        }
        // CfStockReturnHeader
        RowBounds rowBounds =new RowBounds( page-1,  limit);
        List<CfStockReturnHeader> cfStockReturnHeaderList=cfStockReturnHeaderMapper.selectPage(rowBounds,entityWrapper);
        cfStockReturnHeaderPage.setRecords(cfStockReturnHeaderList);
        return cfStockReturnHeaderPage;

    }

}
