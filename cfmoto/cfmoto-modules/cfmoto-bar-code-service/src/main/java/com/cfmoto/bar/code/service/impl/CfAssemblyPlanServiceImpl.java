package com.cfmoto.bar.code.service.impl;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfAssemblyPlan;
import com.cfmoto.bar.code.mapper.CfAssemblyPlanMapper;
import com.cfmoto.bar.code.service.ICfAssemblyPlanService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 总成方案数据表 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-15
 */
@Service
public class CfAssemblyPlanServiceImpl extends ServiceImpl<CfAssemblyPlanMapper, CfAssemblyPlan> implements ICfAssemblyPlanService {

    @Autowired
    CfAssemblyPlanMapper cfAssemblyPlanMapper ;

    @Override
    public List<SelectList> selectAllCountry() {
        return cfAssemblyPlanMapper.selectAllCountry();
    }

    @Override
    public List<SelectList> selectModelByCountry(String country) {
        return cfAssemblyPlanMapper.selectModelByCountry(country);
    }
}
