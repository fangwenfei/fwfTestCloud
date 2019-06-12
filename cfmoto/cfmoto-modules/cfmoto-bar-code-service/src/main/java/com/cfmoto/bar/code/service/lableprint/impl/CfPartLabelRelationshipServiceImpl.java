package com.cfmoto.bar.code.service.lableprint.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfPartLabelRelationshipMapper;
import com.cfmoto.bar.code.model.entity.CfPartLabelRelationship;
import com.cfmoto.bar.code.service.lableprint.ICfPartLabelRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author space
 * @since 2019-04-20
 */
@Service
public class CfPartLabelRelationshipServiceImpl extends ServiceImpl<CfPartLabelRelationshipMapper, CfPartLabelRelationship> implements ICfPartLabelRelationshipService {

    @Autowired
    private CfPartLabelRelationshipMapper cfPartLabelRelationshipMapper;

    /**
     * 部品零部件标签模板对照信息维护导入
     * 存在更新，不存在导入
     * @param cfPartLabelRelationshipList
     */
    @Override
    public void customInsertOrSaveBatch( List<CfPartLabelRelationship> cfPartLabelRelationshipList ) {

        cfPartLabelRelationshipMapper.customInsertOrSaveBatch( cfPartLabelRelationshipList );

    }
}
