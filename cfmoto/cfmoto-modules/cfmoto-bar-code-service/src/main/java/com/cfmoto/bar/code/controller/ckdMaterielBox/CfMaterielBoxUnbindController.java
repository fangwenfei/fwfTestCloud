package com.cfmoto.bar.code.controller.ckdMaterielBox;

import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxUnbindService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/29.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@RestController
@RequestMapping("/cfMaterielBoxUnbind")
@Api(tags="解箱/解托（PDA端)")
public class CfMaterielBoxUnbindController  extends BaseController {
    @Autowired
    private ICfMaterielBoxUnbindService cfMaterielBoxUnbindService;

    /**
     * 1.删除该条码号
     * 2.如果下一节点XH是箱子，清除该箱子的父类条码号
     *   如果下一节点WL物料，删除该节点数据，并修改装箱清单已装箱数据量
     */
    @PostMapping("/unbindMaterielBox")
    @ApiOperation(value="解箱/解托（PDA端)，并从sap拉取数据")
    public R<Boolean> unbindMaterielBox(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            return new R<>(cfMaterielBoxUnbindService.unbindMaterielBox(params,userId));
        }catch (ValidateCodeException e){
            logger.error("/cfMaterielBoxUnbind/unbindMaterielBox",e.getMessage());
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            logger.error("/cfMaterielBoxUnbind/unbindMaterielBox",e.getMessage());
            return new R<>(R.FAIL, CfMaterielBox.CF_MATERIEL_BOX_UNBIND_DATA);
        }
    }

}
