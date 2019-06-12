package com.cfmoto.bar.code.controller.ckdMaterielBox;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfLocationBinding;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfLocationBindingService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
/**
 * <p>
 * 物料条码和货位条码绑定 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-01-24
 */
@RestController
@RequestMapping("/cfLocationBinding")
@Api(tags="物料条码和货位条码绑定")
public class CfLocationBindingController extends BaseController {
    @Autowired private ICfLocationBindingService cfLocationBindingService;




    /**
     * 货位绑定（增强货位转移）
     * 一、当物料条码对应的仓位信息为空时，校验物料仓库和目标仓库是否一致，不一致报错“条码仓库与目标仓库不匹配，请注意！”；
     *     一致，将调用SAP接口17（类型为上架0），接收反馈信息；
     *二、当物料条码对应的仓位信息不为空时，校验物料仓库和目标仓库是否一致，不一致报错“货位转换必须是同一仓库，请注意！”；
     *    一致，将调用SAP接口17（类型为仓位转移1），接收反馈信息；
     * @param params
     * @return
     */
    @PostMapping("/locationBindingToSap")
    @ApiOperation(value="货位绑定（增强货位转移）")
    public R<Boolean> locationBindingToSap(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            return new R<>(cfLocationBindingService.locationBindingToSap(params, userId));
        } catch (ValidateCodeException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage());
        } catch (Exception e) {
            return new R<>(R.FAIL, CfLocationBinding.CF_LOCATION_BINDING_SQL_ADD);
        }

    }

    /**
     * 通过物料条码获取详细信息
     * @param params
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/getDetailByBarcode")
    @ApiOperation(value="通过物料条码获取详细信息")
    public R<Map<String,Object>> getDetailByBarcode(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            return new  R<>(cfLocationBindingService.getDetailByBarcode(params,userId));
        }catch (ValidateCodeException e){
            return new R<>(R.FAIL, e.getMessage());
        }catch (Exception e){
            return new R<>(R.FAIL, CfLocationBinding.CF_LOCATION_BINDING_SQL_ADD);
        }

    }



    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfLocationBinding
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfLocationBinding> get(@RequestParam Integer id) {
        return new R<>(cfLocationBindingService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询物料条码和货位条码绑定")
    public R<Page> page(@RequestParam Map<String, Object> params,CfLocationBinding cfLocationBinding) {
        return new R<>(cfLocationBindingService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfLocationBinding)));
    }

    /**
     * 添加
     * @param  cfLocationBinding  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加物料条码和货位条码绑定")
    public R<Boolean> add(@RequestBody CfLocationBinding cfLocationBinding,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfLocationBinding.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfLocationBindingService.insert(cfLocationBinding));
       }catch (Exception e){
            return new R<>(R.FAIL, CfLocationBinding.CF_LOCATION_BINDING_SQL_ADD);
        }


    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除物料条码和货位条码绑定通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfLocationBinding cfLocationBinding = new CfLocationBinding();
        return new R<>(cfLocationBindingService.updateById(cfLocationBinding));
    }

    /**
     * 编辑
     * @param  cfLocationBinding  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除物料条码和货位条码绑定")
    public R<Boolean> edit(@RequestBody CfLocationBinding cfLocationBinding) {
        return new R<>(cfLocationBindingService.updateById(cfLocationBinding));
    }
}
