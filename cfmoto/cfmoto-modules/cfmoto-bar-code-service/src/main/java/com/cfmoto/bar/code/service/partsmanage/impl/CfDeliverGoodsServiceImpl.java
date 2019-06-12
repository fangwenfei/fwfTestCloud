package com.cfmoto.bar.code.service.partsmanage.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfDeliverGoodsMapper;
import com.cfmoto.bar.code.model.entity.CfDeliverGoods;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsScan;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsSum;
import com.cfmoto.bar.code.model.vo.DeliverGoodsFullVo;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsScanService;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsService;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsSumService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.R;
import com.github.pig.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 部品发货表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
@Service
public class CfDeliverGoodsServiceImpl extends ServiceImpl<CfDeliverGoodsMapper, CfDeliverGoods> implements ICfDeliverGoodsService {

    @Autowired
    private CfDeliverGoodsMapper cfDeliverGoodsMapper;

    @Autowired
    private SapFeignService sapFeignService;

    @Autowired
    private ICfDeliverGoodsScanService iCfDeliverGoodsScanService;

    @Autowired
    private ICfDeliverGoodsSumService iCfDeliverGoodsSumService;

    /**
     * 通过部品表id和用户查询部品表相关联数据
     * @param deliverGoodsId
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public DeliverGoodsFullVo getDeliverGoodsFullVoByIdAndUserId( String deliverGoodsId, Integer userId ){
        return cfDeliverGoodsMapper.getDeliverGoodsFullVoByIdAndUserId( deliverGoodsId,userId );
    }


    /**
     * 根据单号获取ERP获取数据
     * @param orderNo
     * @return
     * @throws Exception
     */
    public List< Map<String, Object> > fetchSapPartDeliverGoodsData( String orderNo ) throws Exception{

        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put( "IV_ZCELN", orderNo );
        callParamMap.put( HandleRefConstants.FUNCTION_NAME, "ZMM_BC_035" );
        callParamMap.put( HandleRefConstants.PARAM_MAP, paramMap );
        R r = sapFeignService.executeJcoFunction( callParamMap );
        if( r.getCode()!=0 ){
            throw new Exception( r.getMsg() );
        }
        Map<String, Object> returnMap = ( Map<String, Object> ) r.getData();
        Integer evStatus = (Integer) returnMap.get( "EV_STATUS" );
        if( evStatus==0 ){
            throw new Exception( (String) returnMap.get( "EV_MESSAGE" ) );
        }
        return (List<Map<String, Object>>) returnMap.get( "ET_DATA" );

    }


    /**
     * 发送ERP数据
     * @param orderNo
     * @param cfDeliverGoodsScanList
     * @param userId
     * @return
     */
    public  R pushSapPartDeliverGoodsData( String orderNo, List<CfDeliverGoodsScan> cfDeliverGoodsScanList,Integer userId ) throws Exception{

        Map<String, Object> callParamMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        List< Map<String, Object> > paramMapList = new ArrayList< Map<String, Object> >();
        Map<String, Object> pMap = null;
        for( int i=0,len=cfDeliverGoodsScanList.size(); i<len; i++ ){

            pMap = new HashMap<String, Object>();
            pMap.put( "ZCELN", orderNo );
            pMap.put( "ZZXH", cfDeliverGoodsScanList.get( i ).getCaseNo() );
            paramMapList.add( pMap );
        }
        paramMap.put( "IT_DATA", paramMapList );
        callParamMap.put( HandleRefConstants.FUNCTION_NAME, "ZMM_BC_036" );
        callParamMap.put( HandleRefConstants.PARAM_MAP, paramMap );
        R r = null;
        try{
            r = sapFeignService.executeJcoFunction( callParamMap );
        }catch ( Exception e ){
            throw e;
        }
        if( r.getCode()!=0 ){
            throw new Exception( r.getMsg() );
        }
        Map<String, Object> returnMap = ( Map<String, Object> ) r.getData();
        Integer evStatus = (Integer) returnMap.get( "EV_STATUS" );
        if( evStatus==0 ){
            throw new Exception( (String) returnMap.get( "EV_MESSAGE" ) );
        }
        return r;
    }

    /**
     * 组装数据
     * @param orderNo
     * @param userId
     * @param etDataList
     * @return
     */
    DeliverGoodsFullVo assemblyDeliverData( String orderNo, Integer userId, List<Map<String, Object>> etDataList ){

        if( etDataList==null || etDataList.size()==0 ){
            return null;
        }
        //ZCELN==出库通知单  ZZXH==箱号  ZFHXS==箱数  VBELN==销售订单  KUNNR==客户  ZSTATU==状态
        DeliverGoodsFullVo deliverGoodsFullVo = new DeliverGoodsFullVo();

        //组装主表数据
        Map<String, Object> map = etDataList.get( 0 );
        deliverGoodsFullVo.setDeliverGoodsId( StringUtils.genHandle( HandleRefConstants.DELIVER_ID,"1000", orderNo ) );
        deliverGoodsFullVo.setOrderNo( orderNo );
        deliverGoodsFullVo.setStatus( (String) map.get( "ZSTATU" ) );
        deliverGoodsFullVo.setSaleOrder( (String) map.get( "VBELN" ) );
        deliverGoodsFullVo.setCustomer( (String) map.get( "KUNNR" ) );
        deliverGoodsFullVo.setObjectSetBasicAttribute( userId, new Date() );
        //组装汇总表数据
        List<CfDeliverGoodsSum> cfDeliverGoodsSumList = new ArrayList<CfDeliverGoodsSum>();
        CfDeliverGoodsSum cfDeliverGoodsSum = null;
        for( int i=0,len=etDataList.size(); i<len; i++ ){

            cfDeliverGoodsSum = new CfDeliverGoodsSum();
            map = etDataList.get( i );
            cfDeliverGoodsSum.setCaseNo( (String) map.get( "ZZXH" ) );
            cfDeliverGoodsSum.setDeliverGoodsSumId( StringUtils.genHandle( HandleRefConstants.DELIVER_SUM_ID,deliverGoodsFullVo.getDeliverGoodsId(),
                    cfDeliverGoodsSum.getCaseNo() ) );
            cfDeliverGoodsSum.setDeliverGoodsIdRef( deliverGoodsFullVo.getDeliverGoodsId() );
            cfDeliverGoodsSum.setQty( new BigDecimal( (String)map.get( "ZFHXS" ) ) );
            cfDeliverGoodsSum.setSaleOrder( (String) map.get( "VBELN" ) );
            cfDeliverGoodsSum.setCustomer( (String) map.get( "KUNNR" ) );
            cfDeliverGoodsSum.setObjectSetBasicAttribute( deliverGoodsFullVo.getCreatedBy(),deliverGoodsFullVo.getCreationDate() );
            cfDeliverGoodsSumList.add( cfDeliverGoodsSum );

        }
        deliverGoodsFullVo.setCfDeliverGoodsSumList( cfDeliverGoodsSumList );
        return deliverGoodsFullVo;
    }

    /**
     * 获取发货单数据
     * @param orderNo
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional( rollbackFor = { Exception.class,RuntimeException.class} )
    public DeliverGoodsFullVo getDeliverGoodsOrder( String orderNo, Integer userId ) throws Exception{

        List<Map<String, Object>> etDataList = fetchSapPartDeliverGoodsData( orderNo ); //获取ERP单号数据
        DeliverGoodsFullVo oDeliverGoodsFullVo = assemblyDeliverData( orderNo,userId,etDataList ); //转换组装数据
        if( "C".equalsIgnoreCase( oDeliverGoodsFullVo.getStatus() ) ){
            return oDeliverGoodsFullVo;
        }
        String deliverGoodsId = StringUtils.genHandle( HandleRefConstants.DELIVER_ID,"1000",orderNo );
        DeliverGoodsFullVo deliverGoodsFullVo = getDeliverGoodsFullVoByIdAndUserId( deliverGoodsId,userId );//通过部品表id和用户查询部品表相关联数据
        deliverGoodsFullVo = compareDataAndSave( deliverGoodsFullVo, oDeliverGoodsFullVo ); //比较数据、保存数据
        return deliverGoodsFullVo;
    }


    /**
     * 比较数据、保存数据
     * @param deliverGoodsFullVo
     * @param oDeliverGoodsFullVo
     * @throws Exception
     */
    public DeliverGoodsFullVo compareDataAndSave( DeliverGoodsFullVo deliverGoodsFullVo ,DeliverGoodsFullVo oDeliverGoodsFullVo ) throws Exception{

        if( "C".equalsIgnoreCase( oDeliverGoodsFullVo.getStatus() ) ){ //判断单号数据是否完成
            deliverGoodsFullVo = oDeliverGoodsFullVo;
        }else{
            if( deliverGoodsFullVo==null ){ //代表数据未下载或单号已完成数据不进行保存

                deliverGoodsFullVo = oDeliverGoodsFullVo;
                if( deliverGoodsFullVo==null ){
                    throw new Exception( "单号"+oDeliverGoodsFullVo.getOrderNo()+"无数据" );
                }
                //保存主表数据
                cfDeliverGoodsMapper.customInsert( deliverGoodsFullVo );
                //保存汇总表数据
                iCfDeliverGoodsSumService.saveSumBatch( deliverGoodsFullVo.getCfDeliverGoodsSumList() );

            }else{

                //是否需要保存数据标识
                boolean saveFlag = false;
                //代表数据已下载，比较汇总数据是否和拉取数据一致，不一致则重新保存
                if( deliverGoodsFullVo.getCfDeliverGoodsSumList().size() != oDeliverGoodsFullVo.getCfDeliverGoodsSumList().size() ){
                    //代表已存在数据跟从erp获取的数据不一致，重新保存数据
                    saveFlag = true;
                }

                //比较数组是否相同，根据箱号比较，先根据箱号对两个数组进行相同规则（升序或降序）排序
                //已存在内容进行升序排序
                Collections.sort( deliverGoodsFullVo.getCfDeliverGoodsSumList(), new Comparator<CfDeliverGoodsSum>() {
                    @Override
                    public int compare( CfDeliverGoodsSum o1, CfDeliverGoodsSum o2 ) {
                        return o1.getCaseNo().compareTo( o2.getCaseNo() );
                    }
                } );
                //拉取内容进行升序排序
                Collections.sort(oDeliverGoodsFullVo.getCfDeliverGoodsSumList(), new Comparator<CfDeliverGoodsSum>() {
                    @Override
                    public int compare(CfDeliverGoodsSum o1, CfDeliverGoodsSum o2) {
                        return o1.getCaseNo().compareTo( o2.getCaseNo() );
                    }
                });
                //判断排序后的箱号是否一一对应
                for( int i=0, len=deliverGoodsFullVo.getCfDeliverGoodsSumList().size(); i<len; i++ ){
                    if( !(deliverGoodsFullVo.getCfDeliverGoodsSumList().get( i ).getCaseNo()
                            .equals( oDeliverGoodsFullVo.getCfDeliverGoodsSumList( ).get( i ).getCaseNo() )
                            &&deliverGoodsFullVo.getCfDeliverGoodsSumList().get( i ).getQty()
                            .compareTo( oDeliverGoodsFullVo.getCfDeliverGoodsSumList( ).get( i ).getQty())==0 )){
                        saveFlag = true;
                        break;
                    }
                }
                if( saveFlag ){ //需要重新保存,更新汇总表数据，以拉取的数据为准

                    //先删除已存在的汇总数据
                    Map<String, Object> deleteMap = new HashMap<String, Object>();
                    deleteMap.put( "deliver_goods_id_ref", deliverGoodsFullVo.getDeliverGoodsId() );
                    iCfDeliverGoodsSumService.deleteByMap( deleteMap );

                    //查询单号已扫描的数据，然后计算汇总
                    Map<String, Object> scanSelectMap = new HashMap<String, Object>();
                    scanSelectMap.put( "deliver_goods_id_ref",deliverGoodsFullVo.getDeliverGoodsId() );
                    List<CfDeliverGoodsScan> cfDeliverGoodsScanList = iCfDeliverGoodsScanService.selectByMap( scanSelectMap );

                    //计算汇总数据
                    CfDeliverGoodsSum oCfDeliverGoodsSum = null;
                    if( cfDeliverGoodsScanList!=null && cfDeliverGoodsScanList.size() >0 ){
                        for( int i=0,len=cfDeliverGoodsScanList.size(); i<len; i++ ){
                            for( int j=0,jlen=oDeliverGoodsFullVo.getCfDeliverGoodsSumList( ).size(); j<jlen; j++ ){
                                if( cfDeliverGoodsScanList.get( i ).getCaseNo()
                                        .equals( oDeliverGoodsFullVo.getCfDeliverGoodsSumList( ).get( j ).getCaseNo() ) ){
                                    oCfDeliverGoodsSum = oDeliverGoodsFullVo.getCfDeliverGoodsSumList( ).get( j );
                                    oCfDeliverGoodsSum.setQty( oCfDeliverGoodsSum.getQty().add( BigDecimal.ONE ) );
                                    break;
                                }
                            }
                        }
                    }
                    //保存汇总数据
                    iCfDeliverGoodsSumService.saveSumBatch( oDeliverGoodsFullVo.getCfDeliverGoodsSumList( ) );
                    //替换存在数据的汇总数据
                    deliverGoodsFullVo.setCfDeliverGoodsSumList( oDeliverGoodsFullVo.getCfDeliverGoodsSumList() );
                }
            }
        }
        return deliverGoodsFullVo;
    }

    /**
     * 发货单提交
     * @param orderNo
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional( rollbackFor = {Exception.class,RuntimeException.class} )
    public DeliverGoodsFullVo doDeliverGoodsOrderSubmit( String orderNo, int userId ) throws Exception {

        String deliverGoodsId = StringUtils.genHandle( HandleRefConstants.DELIVER_ID, "1000", orderNo );
        DeliverGoodsFullVo deliverGoodsFullVo = getDeliverGoodsFullVoByIdAndUserId( deliverGoodsId,userId );
        if( deliverGoodsFullVo==null ){
            throw new Exception( "数据已修改，请重新获取单号数据,请重新扫描箱号！" );
        }

        //获取扫描数据
        EntityWrapper<CfDeliverGoodsScan> scanWrapper = new EntityWrapper<CfDeliverGoodsScan>();
        CfDeliverGoodsScan scanEntity = new CfDeliverGoodsScan();
        scanEntity.setDeliverGoodsIdRef( deliverGoodsId );
        scanEntity.setCreatedBy( userId );
        scanWrapper.setEntity( scanEntity );
        List<CfDeliverGoodsScan> cfDeliverGoodsScanList = iCfDeliverGoodsScanService.selectList( scanWrapper );
        if( cfDeliverGoodsScanList.size() > 0 ){ //扫描表有数据
            //删除扫描表数据
            iCfDeliverGoodsScanService.delete( scanWrapper );

            //发送ERP数据
            R r = pushSapPartDeliverGoodsData( orderNo, cfDeliverGoodsScanList, userId );
            Map<String, Object> returnMap = (Map<String, Object>) r.getData();
            List< Map<String, Object> > etDataList = (List<Map<String, Object>>) returnMap.get( "ET_DATA" );
            DeliverGoodsFullVo oDeliverGoodsFullVo = assemblyDeliverData( orderNo,userId,etDataList ); //转换组装数据
            deliverGoodsFullVo = compareDataAndSave( deliverGoodsFullVo,oDeliverGoodsFullVo );

            List< Map<String, Object> > errorList = (List<Map<String, Object>>) returnMap.get( "ET_ERROR" ); //失败箱号列表
            if( errorList.size() > 0 ){

                List<String> failCaseNoList = new ArrayList<String>();
                for( int i=0; i<errorList.size(); i++ ){
                    failCaseNoList.add( (String) errorList.get( i ).get( "ZZXH" ) );

                    //将提交失败，更新汇总表数据箱号数量为0
                    CfDeliverGoodsSum deliverGoodsSum = new CfDeliverGoodsSum();
                    deliverGoodsSum.setCaseNo( (String) errorList.get( i ).get( "ZZXH" ) );
                    deliverGoodsSum.setQty( new BigDecimal( 0 ) );
                    deliverGoodsSum.setLastUpdatedBy( userId );
                    deliverGoodsSum.setLastUpdateDate( new Date() );
                    Wrapper<CfDeliverGoodsSum> sumWrapper = new EntityWrapper<CfDeliverGoodsSum>();
                    sumWrapper.eq( "case_no", deliverGoodsSum.getCaseNo() ).andNew()
                            .eq( "deliver_goods_id_ref", cfDeliverGoodsScanList.get( 0 ).getDeliverGoodsIdRef() );
                    iCfDeliverGoodsSumService.update( deliverGoodsSum, sumWrapper );
                }
                deliverGoodsFullVo.setFailCaseNoList( failCaseNoList );
                deliverGoodsFullVo.setMessage( (String) returnMap.get( "EV_MESSAGE" ) );


            }
            deliverGoodsFullVo.setCfDeliverGoodsScanList( new ArrayList<>() );
            return deliverGoodsFullVo;

        }else{ //扫描表无数据
            throw new Exception( "无数据可提交" );
        }
    }


}
