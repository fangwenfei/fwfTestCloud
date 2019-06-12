package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.baomidou.mybatisplus.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-08
 */
public interface ICfPrintLodopTemplateService extends IService<CfPrintLodopTemplate> {

    CfPrintLodopTemplate getPrintLodopTemplate(Map<String, Object> params);


    Boolean setCfCheck(CfPrintLodopTemplate cfPrintLodopTemplate,int userId);

//JUST_IN_TIME_INVENTORY_PRINT
}
