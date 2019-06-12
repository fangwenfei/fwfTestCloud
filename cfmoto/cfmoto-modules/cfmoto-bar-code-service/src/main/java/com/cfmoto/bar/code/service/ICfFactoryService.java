package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfFactory;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-05-09
 */
public interface ICfFactoryService extends IService<CfFactory> {

    /**
     * 根据传入的条件做模糊查询
     *
     * @param factoryCode 工厂代码
     * @param factoryName 工厂名称
     * @param remark      备注
     * @return 工厂实体类集合
     */
    List<CfFactory> getAllFactoryByCondition(String factoryCode, String factoryName, String remark);
}
