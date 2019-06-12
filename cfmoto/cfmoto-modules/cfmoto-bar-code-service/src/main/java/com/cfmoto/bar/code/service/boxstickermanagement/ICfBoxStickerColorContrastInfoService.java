package com.cfmoto.bar.code.service.boxstickermanagement;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfBoxStickerColorContrastInfo;

/**
 * <p>
 * 箱外贴颜色对照信息表 服务类
 * </p>
 *
 * @author ye
 * @since 2019-04-24
 */
public interface ICfBoxStickerColorContrastInfoService extends IService<CfBoxStickerColorContrastInfo> {
    /**
     * 新增或修改信息
     *
     * @param cfBoxStickerColorContrastInfo 信息
     * @param userId                        用户id
     * @return
     */
    Boolean addOrEdit(CfBoxStickerColorContrastInfo cfBoxStickerColorContrastInfo, int userId);
}
