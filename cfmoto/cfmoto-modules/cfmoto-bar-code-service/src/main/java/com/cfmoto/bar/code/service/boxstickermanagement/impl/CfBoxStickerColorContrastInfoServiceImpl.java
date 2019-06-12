package com.cfmoto.bar.code.service.boxstickermanagement.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.mapper.CfBoxStickerColorContrastInfoMapper;
import com.cfmoto.bar.code.model.entity.CfBoxStickerColorContrastInfo;
import com.cfmoto.bar.code.service.boxstickermanagement.ICfBoxStickerColorContrastInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 箱外贴颜色对照信息表 服务实现类
 * </p>
 *
 * @author ye
 * @since 2019-04-24
 */
@Service
public class CfBoxStickerColorContrastInfoServiceImpl extends ServiceImpl<CfBoxStickerColorContrastInfoMapper, CfBoxStickerColorContrastInfo> implements ICfBoxStickerColorContrastInfoService {

    @Autowired
    private CfBoxStickerColorContrastInfoMapper infoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addOrEdit(CfBoxStickerColorContrastInfo cfBoxStickerColorContrastInfo, int userId) {
        CfBoxStickerColorContrastInfo info = infoMapper.selectById(cfBoxStickerColorContrastInfo.getCfBoxStickerColorContrastInfoId());
        //判断根据传来的信息是否能查到数据
        //查不到，就新增
        int row = 0;
        if (info == null) {
            cfBoxStickerColorContrastInfo.setObjectSetBasicAttribute(userId, new Date());
            row += infoMapper.insert(cfBoxStickerColorContrastInfo);
        } else {
            //查得到，就修改
            cfBoxStickerColorContrastInfo.setObjectSetBasicAttributeForUpdate(userId, new Date());
            row += infoMapper.updateAllColumnById(cfBoxStickerColorContrastInfo);
        }
        return row > 0;
    }
}
