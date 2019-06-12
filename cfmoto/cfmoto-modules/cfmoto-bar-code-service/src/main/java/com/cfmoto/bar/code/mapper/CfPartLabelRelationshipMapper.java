package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfPartLabelRelationship;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-04-20
 */
@Repository
public interface CfPartLabelRelationshipMapper extends BaseMapper<CfPartLabelRelationship> {

    void customInsertOrSaveBatch( List<CfPartLabelRelationship> cfPartLabelRelationshipList );
}
