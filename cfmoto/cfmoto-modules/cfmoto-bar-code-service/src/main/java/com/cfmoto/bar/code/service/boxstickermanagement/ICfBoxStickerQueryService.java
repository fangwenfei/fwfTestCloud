package com.cfmoto.bar.code.service.boxstickermanagement;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;

import java.util.List;
import java.util.Map;

/**
 * 箱外贴查询业务层接口
 *
 * @author ye
 * @date 2019-04-25
 */
public interface ICfBoxStickerQueryService {
    /**
     * 根据过滤条件和分页条件进行分页模糊查询
     *
     * @param params 过滤条件
     * @return Page
     */
    Page selectPageByFilters(Map<String, Object> params);

    /**
     * 封装查询条件
     * @param params
     * @return
     */
    Wrapper wrapParams(Map<String,Object> params);

    /**
     * 导出报表
     * @param params  过滤参数
     * @return list
     */
    List<CfBarcodeBind> export(Map<String, Object> params);
}
