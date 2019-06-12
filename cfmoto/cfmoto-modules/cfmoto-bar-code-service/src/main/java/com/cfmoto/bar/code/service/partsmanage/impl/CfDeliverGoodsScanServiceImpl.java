package com.cfmoto.bar.code.service.partsmanage.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfDeliverGoodsScanMapper;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsScan;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsSum;
import com.cfmoto.bar.code.model.vo.DeliverGoodsFullVo;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsScanService;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsService;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsSumService;
import com.github.pig.common.constant.HandleRefConstants;
import com.github.pig.common.util.StringUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  部品发货扫描表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
@Service
public class CfDeliverGoodsScanServiceImpl extends ServiceImpl<CfDeliverGoodsScanMapper, CfDeliverGoodsScan> implements ICfDeliverGoodsScanService {

    @Autowired
    private CfDeliverGoodsScanMapper cfDeliverGoodsScanMapper;

    @Autowired
    private ICfDeliverGoodsService iCfDeliverGoodsService;

    @Autowired
    private ICfDeliverGoodsSumService iCfDeliverGoodsSumService;

    /**
     * 发货单箱号扫描
     * @param orderNo
     * @param caseNo
     * @param userId
     * @throws Exception
     */
    @Transactional( rollbackFor = {Exception.class,RuntimeException.class})
    @Override
    public void scanCaseNo( String orderNo, String caseNo, int userId ) throws Exception {

        String deliverGoodsId = StringUtils.genHandle( HandleRefConstants.DELIVER_ID, "1000", orderNo );
        DeliverGoodsFullVo deliverGoodsFullVo = iCfDeliverGoodsService.getDeliverGoodsOrder( orderNo, userId );
        if( "C".equalsIgnoreCase( deliverGoodsFullVo.getStatus() ) ){
            throw new Exception( "发货通知单"+orderNo+"处于已完成，请注意！" );
        }

        //验证箱号是否已扫描
        List<CfDeliverGoodsScan> cfDeliverGoodsScanList = deliverGoodsFullVo.getCfDeliverGoodsScanList();
        if( cfDeliverGoodsScanList.size()>0 ){
            for( int i=0,len=cfDeliverGoodsScanList.size(); i<len; i++ ){
                if( StrUtil.equals( cfDeliverGoodsScanList.get( i ).getCaseNo(), caseNo ) ){
                    throw new Exception( "已经扫描过该箱号" );
                }
            }
        }

        //验证该箱号是否已提交和是否存在发货清单列表
        List<CfDeliverGoodsSum> deliverGoodsSumList = deliverGoodsFullVo.getCfDeliverGoodsSumList();
        if( deliverGoodsSumList.size()==0 ){
            throw new Exception( "发货通知单"+orderNo+"无发货清单数据" );
        }
        boolean haveFlag = false;
        CfDeliverGoodsSum cfDeliverGoodsSum = null; //汇总数据
        for ( int i=0,len=deliverGoodsSumList.size(); i<len; i++ ){
            cfDeliverGoodsSum = deliverGoodsSumList.get( i );
            if( StrUtil.equals( cfDeliverGoodsSum.getCaseNo(),caseNo ) ){
                if( cfDeliverGoodsSum.getQty()!=null && cfDeliverGoodsSum.getQty().intValue()>0 ){
                    throw new Exception( "已经扫描并提交过该箱号" );
                }
                haveFlag = true;
                break;
            }
        }
        if( !haveFlag ){
            throw new Exception( "箱号"+caseNo+"不在发货清单，请注意！" );
        }

        //保存扫描表数据
        CfDeliverGoodsScan saveDeliverGoodsScan = new CfDeliverGoodsScan();
        saveDeliverGoodsScan.setDeliverGoodsScanId( UUID.randomUUID().toString().replaceAll( "-","" ).toUpperCase() );
        saveDeliverGoodsScan.setDeliverGoodsSumIdRef( cfDeliverGoodsSum.getDeliverGoodsSumId() );
        saveDeliverGoodsScan.setDeliverGoodsIdRef( cfDeliverGoodsSum.getDeliverGoodsIdRef() );
        saveDeliverGoodsScan.setRowItem( cfDeliverGoodsSum.getRowItem() );
        saveDeliverGoodsScan.setCaseNo( caseNo );
        saveDeliverGoodsScan.setObjectSetBasicAttribute( userId, new Date() );
        cfDeliverGoodsScanMapper.insert( saveDeliverGoodsScan );
        //更新汇总表数据
        CfDeliverGoodsSum deliverGoodsSum = new CfDeliverGoodsSum();
        deliverGoodsSum.setDeliverGoodsSumId( cfDeliverGoodsSum.getDeliverGoodsSumId() );
        deliverGoodsSum.setQty( new BigDecimal( 1 ) );
        deliverGoodsSum.setLastUpdatedBy( userId );
        deliverGoodsSum.setLastUpdateDate( new Date() );
        iCfDeliverGoodsSumService.updateById( deliverGoodsSum );

    }

    /**
     * 发货单删除行数据
     * @param deliverGoodsScanId
     * @param deliverGoodsSumIdRef
     * @param userId
     * @throws Exception
     */
    @Override
    public void deleteScanRow( String deliverGoodsScanId, String deliverGoodsSumIdRef, int userId ) throws Exception {

        //删除扫描表数据
        cfDeliverGoodsScanMapper.deleteById( deliverGoodsScanId );

        //更新汇总表数据
        CfDeliverGoodsSum deliverGoodsSum = new CfDeliverGoodsSum();
        deliverGoodsSum.setDeliverGoodsSumId( deliverGoodsSumIdRef );
        deliverGoodsSum.setQty( new BigDecimal( 0 ) );
        deliverGoodsSum.setLastUpdatedBy( userId );
        deliverGoodsSum.setLastUpdateDate( new Date() );
        iCfDeliverGoodsSumService.updateById( deliverGoodsSum );

    }

}
