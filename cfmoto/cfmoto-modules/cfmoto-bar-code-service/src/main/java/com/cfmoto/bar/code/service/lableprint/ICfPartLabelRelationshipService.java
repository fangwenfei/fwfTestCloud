package com.cfmoto.bar.code.service.lableprint;

import com.cfmoto.bar.code.model.entity.CfPartLabelRelationship;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author space
 * @since 2019-04-20
 */
public interface ICfPartLabelRelationshipService extends IService<CfPartLabelRelationship> {

    void customInsertOrSaveBatch( List<CfPartLabelRelationship> cfPartLabelRelationshipList );
}
