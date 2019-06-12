package com.cfmoto.bar.code.service.appversioncontrol.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfAppVersionControlMapper;
import com.cfmoto.bar.code.model.entity.CfAppVersionControl;
import com.cfmoto.bar.code.service.appversioncontrol.ICfAppVersionControlService;
import com.github.pig.common.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * APP版本控制业务层接口实现类
 *
 * @author yezi
 * @date 2019/5/29
 */
@Service
public class CfAppVersionControlServiceImpl extends ServiceImpl<CfAppVersionControlMapper, CfAppVersionControl> implements ICfAppVersionControlService {

    @Autowired
    private CfAppVersionControlMapper appVersionControlMapper;

    @Override
    public CfAppVersionControl checkAppVersion(String appVersionNo) throws Exception {
        //获取对应版本号的app版本控制数据
        CfAppVersionControl selectVersion = this.selectOne(new EntityWrapper<CfAppVersionControl>().eq("app_version_no", appVersionNo));

        if (selectVersion == null) {
            throw new Exception("当前APP版本号:" + appVersionNo + "没有被维护,请联系系统管理员!!!");
        }

        //如果当前版本不为最新，则返回最新版本app下载链接
        if (selectVersion.getIsUpToDate() == 0) {
            CfAppVersionControl latestVersion = this.selectOne(new EntityWrapper<CfAppVersionControl>().eq("is_up_to_date", 1));
            if (latestVersion == null) {
                throw new Exception("APP更新失败!!!原因为:系统没有维护最新的APP版本,请联系管理员!!!");
            }
            selectVersion.setLatestDownloadLink(latestVersion.getAppDownloadLink());
        }
        return selectVersion;
    }

    @Override
    public void setAllRecordNotUpToDate() {
        appVersionControlMapper.setAllRecordNotUpToDate();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addNewAppVersion(CfAppVersionControl appVersionControl, Integer userId) {
        //设置所有记录不为最新版本
        this.setAllRecordNotUpToDate();

        //插入新的版本记录
        appVersionControl.setObjectBasicAttributes(userId, new Date());
        //默认为最新版本
        appVersionControl.setIsUpToDate((byte) 1);
        this.insert(appVersionControl);
    }

    @Override
    public void setUpToDate(CfAppVersionControl appVersionControl, Integer userId) {
        this.setAllRecordNotUpToDate();

        appVersionControl.setIsUpToDate((byte) 1);
        appVersionControl.setObjectBasicAttributes(userId, new Date());
        this.updateById(appVersionControl);
    }
}
