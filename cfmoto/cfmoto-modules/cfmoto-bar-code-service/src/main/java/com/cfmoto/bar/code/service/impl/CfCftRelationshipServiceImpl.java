package com.cfmoto.bar.code.service.impl;

import com.cfmoto.bar.code.model.entity.CfCftRelationship;
import com.cfmoto.bar.code.mapper.CfCftRelationshipMapper;
import com.cfmoto.bar.code.service.ICfCftRelationshipService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 车架与车型对应表 服务实现类
 * </p>
 *
 * @author space
 * @since 2019-02-26
 */
@Service
public class CfCftRelationshipServiceImpl extends ServiceImpl<CfCftRelationshipMapper, CfCftRelationship> implements ICfCftRelationshipService {

    @Autowired
    private CfCftRelationshipMapper cfCftRelationshipMapper;


    @Override
    @Transactional
    public void customInsertOrSaveBatch( List<CfCftRelationship> cfCftRelationshipList ) throws Exception {

        cfCftRelationshipMapper.customInsertOrSaveBatch( cfCftRelationshipList );

    }
}
