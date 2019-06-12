package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfAssemblyPlan;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 总成方案数据表 Mapper 接口
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-15
 */
public interface CfAssemblyPlanMapper extends BaseMapper<CfAssemblyPlan> {

    /**
     * 获取国家
     * @return
     */
    List<SelectList> selectAllCountry();

    /**
     * 通过国家获取类型
     * @return
     */
    List<SelectList> selectModelByCountry(String country );
}
