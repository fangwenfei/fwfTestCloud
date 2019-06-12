package com.cfmoto.bar.code.service.allotmanagement.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfAllotInfoMapper;
import com.cfmoto.bar.code.model.entity.CfAllotInfo;
import com.cfmoto.bar.code.service.allotmanagement.ICfAllotInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 调拨信息表 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-02
 */
@Service
public class CfAllotInfoServiceImpl extends ServiceImpl<CfAllotInfoMapper, CfAllotInfo> implements ICfAllotInfoService {

    @Autowired
    private CfAllotInfoMapper allotInfoMapper;

    /**
     * 根据调拨单号获取调拨信息表数据（一对一）
     *
     * @param orderNo 调拨单号
     * @return cfAllotInfo
     */
    @Override
    public CfAllotInfo getAllotInfoByOrderNo(String orderNo) {

        CfAllotInfo allotInfo = new CfAllotInfo();
        allotInfo.setOrderNo(orderNo);
        //返回查询的调拨信息表数据
        return allotInfoMapper.selectOne(allotInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertAllotInfo(CfAllotInfo cfAllotInfo) {
        allotInfoMapper.insert(cfAllotInfo);
    }
}
