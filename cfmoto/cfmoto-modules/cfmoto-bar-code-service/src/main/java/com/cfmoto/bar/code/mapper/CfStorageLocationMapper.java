package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 仓库 Mapper 接口
 * </p>
 *
 * @author space
 * @since 2019-03-05
 */
@Repository
public interface CfStorageLocationMapper extends BaseMapper<CfStorageLocation> {


    /**
     * 获取所有的仓库号码
     *
     * @param params 用户输入的数据
     * @return list
     */
    List<CfStorageLocation> getWareHouse(Map<String,Object> params);

    /**
     * 根据工厂查询仓库并分组
     * @param site
     * @return
     */
    List<String> getWareHouseBySite(@Param("site") String site);
}
