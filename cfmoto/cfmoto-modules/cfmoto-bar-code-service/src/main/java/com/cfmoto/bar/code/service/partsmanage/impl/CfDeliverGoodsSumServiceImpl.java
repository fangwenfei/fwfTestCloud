package com.cfmoto.bar.code.service.partsmanage.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfDeliverGoodsSumMapper;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsSum;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsSumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 部品发货汇总表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
@Service
public class CfDeliverGoodsSumServiceImpl extends ServiceImpl<CfDeliverGoodsSumMapper, CfDeliverGoodsSum> implements ICfDeliverGoodsSumService {

    @Autowired
    private CfDeliverGoodsSumMapper cfDeliverGoodsSumMapper;
    @Override
    public void saveSumBatch( List<CfDeliverGoodsSum> cfDeliverGoodsSumList ) {
        cfDeliverGoodsSumMapper.saveSumBatch( cfDeliverGoodsSumList );
    }
}
