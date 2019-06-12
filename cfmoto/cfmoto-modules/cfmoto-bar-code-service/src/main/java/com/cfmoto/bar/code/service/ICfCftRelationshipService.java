package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.entity.CfCftRelationship;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 车架与车型对应表 服务类
 * </p>
 *
 * @author space
 * @since 2019-02-26
 */
public interface ICfCftRelationshipService extends IService<CfCftRelationship> {

    /**
     * 存在则更新否则保存
     * @param cfCftRelationshipList
     * @throws Exception
     */
    void customInsertOrSaveBatch( List<CfCftRelationship> cfCftRelationshipList ) throws Exception;

}
