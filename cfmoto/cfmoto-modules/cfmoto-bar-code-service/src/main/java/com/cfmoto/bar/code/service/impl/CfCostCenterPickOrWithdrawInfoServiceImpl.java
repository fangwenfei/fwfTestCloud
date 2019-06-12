package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawInfoMapper;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInfo;
import com.cfmoto.bar.code.service.ICfCostCenterPickOrWithdrawInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-20
 */
@Service
public class CfCostCenterPickOrWithdrawInfoServiceImpl extends ServiceImpl<CfCostCenterPickOrWithdrawInfoMapper, CfCostCenterPickOrWithdrawInfo> implements ICfCostCenterPickOrWithdrawInfoService {

    @Autowired
    private CfCostCenterPickOrWithdrawInfoMapper infoMapper;

    /**
     * 根据订单号去成本中心领退料信息表中查询数据
     *
     * @param orderNo
     */
    @Override
    public CfCostCenterPickOrWithdrawInfo getByOrderNo(String orderNo) {

        //根据订单号去成本中心领退料信息表中查询数据
        CfCostCenterPickOrWithdrawInfo cfCostCenterPickOrWithdrawInfo = new CfCostCenterPickOrWithdrawInfo();
        cfCostCenterPickOrWithdrawInfo.setOrderNo(orderNo);

        return infoMapper.selectOne(cfCostCenterPickOrWithdrawInfo);

    }
}
