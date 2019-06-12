package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfPrintFunction;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 模板功能表 服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-08
 */
public interface ICfPrintFunctionService extends IService<CfPrintFunction> {

   String  deleteCfPrintFunctionById(Integer id);


}
