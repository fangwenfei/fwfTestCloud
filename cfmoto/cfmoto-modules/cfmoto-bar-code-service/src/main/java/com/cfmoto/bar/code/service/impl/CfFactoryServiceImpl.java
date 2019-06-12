package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.model.entity.CfFactory;
import com.cfmoto.bar.code.mapper.CfFactoryMapper;
import com.cfmoto.bar.code.service.ICfFactoryService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-05-09
 */
@Service
public class CfFactoryServiceImpl extends ServiceImpl<CfFactoryMapper, CfFactory> implements ICfFactoryService {

    @Autowired
    private CfFactoryMapper cfFactoryMapper;

    @Override
    public List<CfFactory> getAllFactoryByCondition(String factoryCode, String factoryName, String remark) {
        EntityWrapper<CfFactory> wrapper = new EntityWrapper<>();
        wrapper.like("factory_code", factoryCode).and()
                .like("factory_name", factoryName).and()
                .like("remark", remark);
        List<CfFactory> cfFactoryList = cfFactoryMapper.selectList(wrapper);
        return cfFactoryList;
    }
}
