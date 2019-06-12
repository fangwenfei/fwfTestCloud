package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfOrderScanTempMapper;
import com.cfmoto.bar.code.model.entity.CfBarcodeInventory;
import com.cfmoto.bar.code.model.entity.CfOrderScanTemp;
import com.cfmoto.bar.code.model.entity.CfOrderSumTemp;
import com.cfmoto.bar.code.model.vo.OrderFullVo;
import com.cfmoto.bar.code.service.ICfBarcodeInventoryService;
import com.cfmoto.bar.code.service.ICfOrderScanTempService;
import com.cfmoto.bar.code.service.ICfOrderSumTempService;
import com.cfmoto.bar.code.service.ICfOrderTempService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 订单扫描临时表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
@Service
public class CfOrderScanTempServiceImpl extends ServiceImpl<CfOrderScanTempMapper, CfOrderScanTemp> implements ICfOrderScanTempService {

    @Autowired
    private CfOrderScanTempMapper cfOrderScanTempMapper;

    @Autowired
    private ICfBarcodeInventoryService iCfBarcodeInventoryService;

    @Autowired
    private ICfOrderTempService iCfOrderTempService;

    @Autowired
    private ICfOrderSumTempService iCfOrderSumTempService;


    /**
     * 查询扫描表该库存条码总共已扫描数据
     * @param barcode
     * @return
     */
    public List<CfOrderScanTemp> getBarcodeScanDataList( String barcode ){
        EntityWrapper<CfOrderScanTemp> orderScanTempEntity = new EntityWrapper<CfOrderScanTemp>();
        CfOrderScanTemp cfOrderScanModel = new CfOrderScanTemp();
        cfOrderScanModel.setBarcode( barcode );
        orderScanTempEntity.setEntity( cfOrderScanModel );
        return cfOrderScanTempMapper.selectList( orderScanTempEntity );
    }


    /**
     * 条码扫描
     * @param userId
     * @param outSourceOrder
     * @param barcode
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanBarcode( int userId, String outSourceOrder, String barcode ) throws Exception {

        String outSourceOrderId = StringUtils.genHandle( HandleRefConstants.ORDER_ID,"1000",outSourceOrder ); //查询订单数据

        //查询条码库存
        EntityWrapper<CfBarcodeInventory> entityWrapper = new EntityWrapper<CfBarcodeInventory>();
        CfBarcodeInventory cEntity = new CfBarcodeInventory();
        cEntity.setBarcode( barcode );
        entityWrapper.setEntity( cEntity );
        List<CfBarcodeInventory> barcodeList = iCfBarcodeInventoryService.selectList( entityWrapper );
        if( barcodeList.size() == 0 ){
            throw new Exception( "条码"+barcode+"不存在" );
        }

        OrderFullVo orderFullVo = iCfOrderTempService.outSourceOrderData( userId,outSourceOrder );//获取订单数据

        List<CfOrderSumTemp> cfOrderSumTempList = orderFullVo.getCfOrderSumTempList();
        CfBarcodeInventory cfBarcodeInventory = barcodeList.get( 0 ); //条码数据
        if( CfBarcodeInventory.CF_BARCODE_INVENTORY_STATE_NOT_USER.equals( cfBarcodeInventory.getState() ) ){ //判断条码状态是否可用
            throw new Exception( "条码状态不可用" );
        }

        CfOrderSumTemp orderSumTemp = null;
        for ( int i=0,len=cfOrderSumTempList.size(); i< len; i++ ){
            if( cfOrderSumTempList.get( i ).getItem().equals( cfBarcodeInventory.getMaterialsNo() ) ){
                orderSumTemp = cfOrderSumTempList.get( i );
                if( orderSumTemp.getOutputQty().compareTo( orderSumTemp.getPayableQty() )<0 ){
                    break;
                }
            }
        }
        if( orderSumTemp==null ){
            throw new Exception( "条码"+barcode+"对应的料号不对，不能出库" );
        }
        if( orderSumTemp.getOutputQty().compareTo( orderSumTemp.getPayableQty() )>=0 ){
            throw new Exception( "该物料出货数量已达标完成，请注意" );
        }

        List<CfOrderScanTemp> cfOrderScanTempList = getBarcodeScanDataList( barcode );
        BigDecimal scanTotalQty = BigDecimal.ZERO; //条码已扫描总数
        if( cfOrderScanTempList.size()> 0 ){
            for( int i=0,len=cfOrderScanTempList.size(); i<len; i++ ){
                scanTotalQty = scanTotalQty.add( cfOrderScanTempList.get( i ).getQuantity() );
            }
        }

        //条码剩余数量
        BigDecimal barcodeRemainQty = cfBarcodeInventory.getBarCodeNumber().subtract( scanTotalQty );
        if( barcodeRemainQty.doubleValue() <=0 ){
            throw new Exception( "条码"+barcode+"已扫描完成" );
        }

        //需要出库数量
        BigDecimal requireQty = orderSumTemp.getPayableQty().subtract( orderSumTemp.getOutputQty() );

        //组装扫描表数据
        CfOrderScanTemp cfOrderScanTemp = new CfOrderScanTemp();
        cfOrderScanTemp.setOrderScanTempId( UUID.randomUUID().toString().replaceAll( "-","" ).toUpperCase() );
        cfOrderScanTemp.setOrderTempIdRef( outSourceOrderId );
        cfOrderScanTemp.setItem( cfBarcodeInventory.getMaterialsNo() );
        cfOrderScanTemp.setItemDesc( cfBarcodeInventory.getMaterialsName() );
        cfOrderScanTemp.setItemPurpose( cfBarcodeInventory.getMaterialsNo() );
        cfOrderScanTemp.setMode( cfBarcodeInventory.getMode() );
        cfOrderScanTemp.setBarcode( barcode );
        cfOrderScanTemp.setBatchNo( cfBarcodeInventory.getBatchNo() );
        cfOrderScanTemp.setQuantity( requireQty.compareTo( barcodeRemainQty )<=0?requireQty:barcodeRemainQty );
        cfOrderScanTemp.setStorageLocation( cfBarcodeInventory.getWarehouse() );
        cfOrderScanTemp.setStorageArea( cfBarcodeInventory.getStorageArea() );
        cfOrderScanTemp.setStoragePosition( cfBarcodeInventory.getWarehousePosition() );
        cfOrderScanTemp.setVendor( orderFullVo.getVendor() );
        cfOrderScanTemp.setObjectSetBasicAttribute( userId, new Date() );
        cfOrderScanTemp.setOrderSumTempIdRef( orderSumTemp.getOrderSumTempId() );
        cfOrderScanTemp.setRowItem( orderSumTemp.getRowItem() );

        cfOrderScanTempMapper.insert( cfOrderScanTemp ); //保存扫描数据

        //更新汇总数据
        EntityWrapper<CfOrderSumTemp> cfOrderSumTempEntity = new EntityWrapper<CfOrderSumTemp>();
        CfOrderSumTemp cfOrderSumTemp = new CfOrderSumTemp();
        cfOrderSumTemp.setOutputQty( orderSumTemp.getOutputQty().add( cfOrderScanTemp.getQuantity() ) );
        cfOrderSumTempEntity.eq( "order_sum_temp_id",orderSumTemp.getOrderSumTempId() );
        iCfOrderSumTempService.update( cfOrderSumTemp, cfOrderSumTempEntity );

    }

    /**
     * 修改指定行条码数量
     * userId用户
     * orderNo委外订单
     * orderSumTempIdRef 订单汇总零时表主键
     * orderScanTempId 订单扫描零时表主键
     * remarks备注
     * qty数量
     * @param userId
     * @param orderScanTempId
     * @param orderSumTempIdRef
     * @param remarks
     * @param qty
     * @throws Exception
     */
    @Override
    public void ourSourceModifyRow( int userId, String orderNo, String orderSumTempIdRef, String orderScanTempId,
                                    String remarks, String qty ) throws Exception {
        //查询订单扫描零时表数据
        CfOrderScanTemp cfOrderScanTemp = cfOrderScanTempMapper.selectById( orderScanTempId );
        if( cfOrderScanTemp==null ){
            throw new Exception( "数据已修改，请重新获取订单数据" );
        }
        BigDecimal modifyDecimal = null; //修改数量
        try{
            modifyDecimal = new BigDecimal( qty );
            if( modifyDecimal.doubleValue()< 0 ){
                throw new Exception( "修改数量不合法" );
            }
        }catch ( Exception e ){
            throw new Exception( "修改数量不合法" );
        }
        if( cfOrderScanTemp.getQuantity().compareTo( modifyDecimal )==0 ){
            throw new Exception( "修改数量不能跟原始数量相等" );
        }

        //查询订单汇总零时表数据
        CfOrderSumTemp cfOrderSumTemp = iCfOrderSumTempService.selectById( orderSumTempIdRef );
        if( cfOrderSumTemp==null ){
            throw new Exception( "数据已修改，请重新获取订单数据" );
        }
        //判断修改数量是增加还是减少
        BigDecimal subtractQty = cfOrderScanTemp.getQuantity().subtract( modifyDecimal );
        if( subtractQty.doubleValue() < 0 ){ //代表增加

            //查询扫描表该库存条码总共已扫描数量
            List<CfOrderScanTemp> cfOrderScanTempList = getBarcodeScanDataList( cfOrderScanTemp.getBarcode() );

            BigDecimal totalScanQty = new BigDecimal( 0 ); //计算该条码已扫描数量总和
            if( cfOrderScanTempList.size() == 0 ){
                throw new Exception( "数据已修改，请重新获取订单数据" );
            }
            for( int i=0,len=cfOrderScanTempList.size(); i<len; i++ ){
                totalScanQty = totalScanQty.add( cfOrderScanTempList.get( i ).getQuantity() );
            }

            //查询该库存数量总和
            EntityWrapper<CfBarcodeInventory> barcodeEntityWrapper = new EntityWrapper<CfBarcodeInventory>();
            CfBarcodeInventory barcodeInventoryModel = new CfBarcodeInventory();
            barcodeInventoryModel.setBarcode( cfOrderScanTemp.getBarcode() );
            barcodeEntityWrapper.setEntity( barcodeInventoryModel );
            List<CfBarcodeInventory> cfBarcodeInventoryList = iCfBarcodeInventoryService.selectList( barcodeEntityWrapper );
            if( cfBarcodeInventoryList.size()==0 ){
                throw new Exception( "库存"+cfOrderScanTemp.getBarcode()+"不存在" );
            }
            BigDecimal inventoryQty = cfBarcodeInventoryList.get( 0 ).getBarCodeNumber(); //库存总数

            if( totalScanQty.subtract( inventoryQty ).doubleValue() >=0 ){ //比较已扫描数量和库存总数大小
                throw new Exception( "修改数量超出条码已扫描库存总数" );
            }
            if( totalScanQty.add( subtractQty.abs() ).compareTo( inventoryQty )> 0 ){
                throw new Exception( "修改数量超出条码已扫描库存总数" );
            }

        }

        Date modifyDate = new Date();
        CfOrderScanTemp cfOrderScanEntity = new CfOrderScanTemp();
        cfOrderScanEntity.setOrderScanTempId( orderScanTempId );
        cfOrderScanEntity.setQuantity( modifyDecimal );
        cfOrderScanEntity.setLastUpdatedBy( userId );
        cfOrderScanEntity.setLastUpdateDate( modifyDate );
        //更新扫描表数量
        cfOrderScanTempMapper.updateById( cfOrderScanEntity );
        //更新汇总表数量
        CfOrderSumTemp orderSumEntity = new CfOrderSumTemp();
        orderSumEntity.setOrderSumTempId( orderSumTempIdRef );
        orderSumEntity.setOutputQty( cfOrderSumTemp.getOutputQty().subtract( subtractQty ) );
        orderSumEntity.setLastUpdatedBy( userId );
        orderSumEntity.setLastUpdateDate( modifyDate );
        iCfOrderSumTempService.updateById( orderSumEntity );

    }


    /**
     * 删除扫描数据，更新汇总数据
     * @param userId
     * @param orderNo
     * @param orderSumTempIdRef
     * @param orderScanTempId
     * @throws Exception
     */
    @Override
    public void ourSourceDeleteRow( int userId, String orderNo, String orderSumTempIdRef, String orderScanTempId ) throws Exception {

        CfOrderScanTemp cfOrderScanTemp = cfOrderScanTempMapper.selectById( orderScanTempId ); //查询扫描数据
        if( cfOrderScanTemp == null ){ //代表扫描数据被删除
            throw new Exception( "数据已修改，请重新获取" );
        }
        //删除扫描数据
        EntityWrapper<CfOrderScanTemp> wrapperEntity = new EntityWrapper<CfOrderScanTemp>();
        CfOrderScanTemp delCfOrderScanTemp = new CfOrderScanTemp();
        delCfOrderScanTemp.setLastUpdateDate( cfOrderScanTemp.getLastUpdateDate() );
        delCfOrderScanTemp.setOrderScanTempId( orderScanTempId );
        wrapperEntity.setEntity( delCfOrderScanTemp );
        Integer delInt = cfOrderScanTempMapper.delete( wrapperEntity );
        if( delInt <= 0 ){ //代表数据已被修改
            return;
        }

        CfOrderSumTemp cfOrderSumTemp = iCfOrderSumTempService.selectById( orderSumTempIdRef );//查询汇总数据
        if( cfOrderSumTemp==null ){ //代表汇总数据被删除,不需要继续处理
            return;
        }
        //更新汇总数据
        CfOrderSumTemp orderSumTemp = new CfOrderSumTemp();
        orderSumTemp.setOrderSumTempId( cfOrderSumTemp.getOrderSumTempId() );
        orderSumTemp.setOutputQty( cfOrderSumTemp.getOutputQty().subtract( cfOrderScanTemp.getQuantity() ) );
        orderSumTemp.setLastUpdatedBy( userId );
        orderSumTemp.setLastUpdateDate( new Date() );
        iCfOrderSumTempService.updateById( orderSumTemp );

    }

}
