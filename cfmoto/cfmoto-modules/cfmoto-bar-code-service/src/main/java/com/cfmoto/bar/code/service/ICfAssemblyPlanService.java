package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfAssemblyPlan;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 总成方案数据表 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-15
 */
public interface ICfAssemblyPlanService extends IService<CfAssemblyPlan> {

   List<SelectList> selectAllCountry();

   /**
    * 通过国家获取类型
    * @return
    */
   List<SelectList> selectModelByCountry(String country);
}
