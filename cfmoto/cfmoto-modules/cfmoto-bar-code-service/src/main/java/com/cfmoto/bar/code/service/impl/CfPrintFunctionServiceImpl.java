package com.cfmoto.bar.code.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.mapper.CfPrintLabelMapper;
import com.cfmoto.bar.code.mapper.CfPrintLodopTemplateMapper;
import com.cfmoto.bar.code.model.entity.CfPrintFunction;
import com.cfmoto.bar.code.mapper.CfPrintFunctionMapper;
import com.cfmoto.bar.code.model.entity.CfPrintLabel;
import com.cfmoto.bar.code.model.entity.CfPrintLodopTemplate;
import com.cfmoto.bar.code.service.ICfPrintFunctionService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 模板功能表 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-08
 */
@Service
public class CfPrintFunctionServiceImpl extends ServiceImpl<CfPrintFunctionMapper, CfPrintFunction> implements ICfPrintFunctionService {

    @Autowired
    CfPrintLabelMapper cfPrintLabelMapper;

    @Autowired
    CfPrintLodopTemplateMapper cfPrintLodopTemplateMapper;

    @Autowired
    CfPrintFunctionMapper cfPrintFunctionMapper ;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteCfPrintFunctionById(Integer id) {
        //删除功能模块数据
        int resultCount=cfPrintFunctionMapper.deleteById(id);
        //删除标签数据
        cfPrintLodopTemplateMapper.delete(new EntityWrapper<CfPrintLodopTemplate>().eq("function_id",id));
        //删除模板数据
        cfPrintLabelMapper.delete(new EntityWrapper<CfPrintLabel>().eq("function_id",id));
        return CfPrintFunction.CF_PRINT_FUNCTION_SUCCESS;
    }
}
