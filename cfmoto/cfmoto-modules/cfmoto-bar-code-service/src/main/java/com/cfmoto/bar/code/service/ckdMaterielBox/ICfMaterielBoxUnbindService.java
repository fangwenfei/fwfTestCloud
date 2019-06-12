package com.cfmoto.bar.code.service.ckdMaterielBox;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.github.pig.common.util.exception.ValidateCodeException;

import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/29.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * 解箱/解托（PDA端)，并从sap拉取数据
 * **********************************************************************
 */
public interface ICfMaterielBoxUnbindService  extends IService<CfMaterielBox> {

    Boolean unbindMaterielBox(Map<String, Object> params, int userId) throws ValidateCodeException, Exception;
}
