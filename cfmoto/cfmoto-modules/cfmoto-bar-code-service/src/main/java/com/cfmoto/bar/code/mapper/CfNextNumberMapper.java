package com.cfmoto.bar.code.mapper;

import com.cfmoto.bar.code.model.entity.CfNextNumber;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 获取下一编号 Mapper 接口
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-21
 */
@Repository
public interface CfNextNumberMapper extends BaseMapper<CfNextNumber> {

    CfNextNumber selectByIdForUpdate(@Param("handle") String handle);

    List<String> selectIdentical(@Param("handle")String handle, @Param("prefix")String prefix, @Param("suffix")String suffix, @Param("forUpdate") boolean forUpdate);

}
