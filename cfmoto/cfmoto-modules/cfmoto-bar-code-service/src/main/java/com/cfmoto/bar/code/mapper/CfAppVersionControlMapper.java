package com.cfmoto.bar.code.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cfmoto.bar.code.model.entity.CfAppVersionControl;

/**
 * @author yezi
 * @date 2019/5/29
 */
public interface CfAppVersionControlMapper extends BaseMapper<CfAppVersionControl> {
    /**
     * 设置所有记录不为最新版本
     */
    void setAllRecordNotUpToDate();
}
