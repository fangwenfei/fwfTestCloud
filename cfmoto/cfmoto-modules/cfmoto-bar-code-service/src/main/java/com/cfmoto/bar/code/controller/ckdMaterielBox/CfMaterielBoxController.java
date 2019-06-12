package com.cfmoto.bar.code.controller.ckdMaterielBox;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfCabinet;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfLoadPackingService;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-18
 */
@RestController
@RequestMapping("/cfMaterielBox")
@Api(tags="装箱装托 ")
public class CfMaterielBoxController extends BaseController {
    @Autowired private ICfMaterielBoxService cfMaterielBoxService;


    @Autowired private ICfLoadPackingService cfLoadPackingService;
    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfMaterielBox
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfMaterielBox> get(@RequestParam Integer id) {
        return new R<>(cfMaterielBoxService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfMaterielBox cfMaterielBox) {
        return new R<>(cfMaterielBoxService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfMaterielBox)));
    }

    /**
     * 添加
     * @param  cfMaterielBox  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfMaterielBox cfMaterielBox,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfMaterielBox.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfMaterielBoxService.insert(cfMaterielBox));
       }catch (Exception e){
            return new R<>(R.FAIL, CfMaterielBox.CF_MATERIEL_BOX_SQL_ADD);
        }


    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfMaterielBox cfMaterielBox = new CfMaterielBox();
        return new R<>(cfMaterielBoxService.updateById(cfMaterielBox));
    }

    /**
     * 编辑
     * @param  cfMaterielBox  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfMaterielBox cfMaterielBox) {
        return new R<>(cfMaterielBoxService.updateById(cfMaterielBox));
    }

    /**
     * 扫描装箱
     * @param  params  实体
     * @return success/false
     */
    @PostMapping("/addMaterielBox")
    @ApiOperation(value="扫描装箱")
    @CacheEvict(value = "MaterielBox:CfMaterielBoxPrint:Select", key = "'MaterielBox:CfMaterielBoxPrint:selectSalesOrderList'")
    public R<Map<String, Object>> addMaterielBox(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            Map<String, Object> maps= cfMaterielBoxService.addMaterielBox(params,userId);
            return new R<>(maps);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfMaterielBox.CF_MATERIEL_BOX_SQL_ADD);
        }
    }


    /**
     * 通过销售订单获取单据号list
     *
     */
    @PostMapping("/selectDocumentNoListBySalesOrder")
    @ApiOperation(value="通过销售订单获取单据号list")
    public R< List<SelectList>> selectDocumentNoListBySalesOrder(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            String salesOrder= params.getOrDefault("salesOrder", "").toString();
            return new R<>(cfLoadPackingService.selectDocumentNoBySalesOrderNo(salesOrder));
        }catch (Exception e){
            return new R<>(R.FAIL,CfMaterielBox.CF_MATERIEL_BOX_GET_DATA);
        }
    }

    /**
     * 条形码验证
     *
     */
    @PostMapping("/barCodeVerification")
    @ApiOperation(value="条形码验证")
    public R<Map<String, Object>> barCodeVerification(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            Map<String, Object> map=  cfMaterielBoxService.barCodeVerification(params);
            map.put("verification","success");
            return new R<>(map);
        }catch (ValidateCodeException e){
            return new R<>(R.FAIL, e.getMessage());
        }catch (Exception e){
            return new R<>(R.FAIL, CfCabinet.CF_CABINET_GET_DATA);
        }
    }


}
