package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.mapper.CfPrintFunctionMapper;
import com.cfmoto.bar.code.model.entity.CfPrintFunction;
import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.mapper.CfPrintLodopTemplateMapper;
import com.cfmoto.bar.code.service.ICfPrintLodopTemplateService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-08
 */
@Service
public class CfPrintLodopTemplateServiceImpl extends ServiceImpl<CfPrintLodopTemplateMapper, CfPrintLodopTemplate> implements ICfPrintLodopTemplateService {
    @Autowired
    CfPrintFunctionMapper cfPrintFunctionMapper ;

    @Override
    public CfPrintLodopTemplate getPrintLodopTemplate(Map<String, Object> params) {
        //功能模块名
        String functionName= params.getOrDefault("functionName", "").toString();
        CfPrintFunction cfPrintFunction =new CfPrintFunction();
        cfPrintFunction.setFunctionName(functionName);
        cfPrintFunction=cfPrintFunctionMapper.selectOne(cfPrintFunction);
        //模板名称
        String printLodopTemplateName= params.getOrDefault("printLodopTemplateName", "").toString();
        CfPrintLodopTemplate cfPrintLodopTemplate= new CfPrintLodopTemplate();
        if(StringUtils.isNotBlank(printLodopTemplateName)){
            cfPrintLodopTemplate.setPrintLodopTemplateName(printLodopTemplateName);
        }
        cfPrintLodopTemplate.setFunctionId(cfPrintFunction.getFunctionId());
        cfPrintLodopTemplate.setCfCheck(CfPrintLodopTemplate.TRUE_SQL);
        List<CfPrintLodopTemplate> CfPrintLodopTemplateList=this.selectList(new EntityWrapper<CfPrintLodopTemplate>(cfPrintLodopTemplate));
        return CfPrintLodopTemplateList.get(0);
    }

    @Override
    public Boolean setCfCheck(CfPrintLodopTemplate cfPrintLodopTemplate,int userId) {
        CfPrintLodopTemplate cfPrintLodopTemplateAll=new CfPrintLodopTemplate();
        cfPrintLodopTemplateAll.setCfCheck(CfPrintLodopTemplate.FALSE_SQL);
        cfPrintLodopTemplateAll.setFunctionId(cfPrintLodopTemplate.getFunctionId());
        cfPrintLodopTemplateAll.setLastUpdatedBy(userId);
        cfPrintLodopTemplateAll.setLastUpdateDate(new Date());
        this.update(cfPrintLodopTemplateAll,new EntityWrapper<CfPrintLodopTemplate>().eq(CfPrintLodopTemplate.FUNCTION_ID_SQL,cfPrintLodopTemplate.getFunctionId()));
        if(CfPrintLodopTemplate.TRUE_SQL.equals(cfPrintLodopTemplate.getCfCheck())){
            cfPrintLodopTemplate.setCfCheck(CfPrintLodopTemplate.FALSE_SQL);
        }else{
            cfPrintLodopTemplate.setCfCheck(CfPrintLodopTemplate.TRUE_SQL);
        }
        cfPrintLodopTemplate.setLastUpdatedBy(userId);
        cfPrintLodopTemplate.setLastUpdateDate(new Date());
        this.updateById(cfPrintLodopTemplate);
        return true;
    }
}
