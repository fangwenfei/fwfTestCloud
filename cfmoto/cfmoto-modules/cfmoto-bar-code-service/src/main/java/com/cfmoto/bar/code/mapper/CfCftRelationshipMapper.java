package com.cfmoto.bar.code.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.entity.CfCftRelationship;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 车架与车型对应表 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-02-26
 */
@Repository
public interface CfCftRelationshipMapper extends BaseMapper<CfCftRelationship> {

    void customInsertOrSaveBatch( List<CfCftRelationship> cfCftRelationshipList );

}
