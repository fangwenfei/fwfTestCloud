package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfOrderSumTempMapper;
import com.cfmoto.bar.code.model.entity.CfOrderSumTemp;
import com.cfmoto.bar.code.service.ICfOrderSumTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 订单汇总临时表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-03-12
 */
@Service
public class CfOrderSumTempServiceImpl extends ServiceImpl<CfOrderSumTempMapper, CfOrderSumTemp> implements ICfOrderSumTempService {

    @Autowired
    private CfOrderSumTempMapper cfOrderSumTempMapper;
    @Override
    public void insertDataByBatch( List<CfOrderSumTemp> cfOrderSumTempList ) {
        cfOrderSumTempMapper.insertDataByBatch( cfOrderSumTempList );
    }

}
