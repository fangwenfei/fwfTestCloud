package com.cfmoto.bar.code.service.appversioncontrol;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfAppVersionControl;

/**
 * APP版本控制业务层接口
 *
 * @author yezi
 * @date 2019/5/29
 */
public interface ICfAppVersionControlService extends IService<CfAppVersionControl> {
    /**
     * 根据传入的app版本号获取数据库中对应数据,如果不为最新，则附加最新的app下载链接，反则不附加
     *
     * @param appVersionNo APP版本号
     * @return CfAppVersionControl
     * @throws Exception
     */
    CfAppVersionControl checkAppVersion(String appVersionNo) throws Exception;

    /**
     * 将所有版本记录设置为非最新版本
     */
    void setAllRecordNotUpToDate();

    /**
     * 发布新版本
     * @param userId
     * @param cfAppVersionControl
     */
    void addNewAppVersion(CfAppVersionControl cfAppVersionControl,Integer userId);

    /**
     * 设置版本为默认最新版本
     * @param appVersionControl
     * @param userId
     */
    void setUpToDate(CfAppVersionControl appVersionControl, Integer userId);
}
