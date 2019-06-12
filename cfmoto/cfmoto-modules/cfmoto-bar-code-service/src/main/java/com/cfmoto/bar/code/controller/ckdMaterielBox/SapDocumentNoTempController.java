package com.cfmoto.bar.code.controller.ckdMaterielBox;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.dto.SapDocumentNoTemp;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.cfmoto.bar.code.service.ckdMaterielBox.ISapDocumentNoTempService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模拟接收sap接收的单据数据量 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-27
 */
@RestController
@RequestMapping("/sapDocumentNoTemp")
@Api(tags=" 模拟接收sap接收的单据数据量")
public class SapDocumentNoTempController extends BaseController {
    @Autowired private ISapDocumentNoTempService sapDocumentNoTempService;

    /**
     * 通过勾选的生产任务单，比较有验证是否正常
     * 并合成装箱清单
     */
    @PostMapping("/insertLoadPacking")
    @ApiOperation(value="并合成装箱清单")
    @CacheEvict(value = "R:SapJobOrderTemp:selectDocumentNoBySapJobOrder" , allEntries=true)
    public R<List<CfLoadPacking>> insertLoadPacking(@RequestBody  List<SapJobOrderTemp> sapJobOrderTempList,HttpServletRequest httpServletRequest) {
        try {
            int userId= UserUtils.getUserId(httpServletRequest);
            R<List<CfLoadPacking>> r=new R<>(sapDocumentNoTempService.insertLoadPacking(userId,sapJobOrderTempList));
             return r;
        }catch (Exception e){
            e.printStackTrace();
             return new R<>(R.FAIL, e.getMessage() );
        }

    }


    /**
    * 通过ID查询
    *
    * @param id ID
    * @return SapDocumentNoTemp
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<SapDocumentNoTemp> get(@RequestParam Integer id) {
        return new R<>(sapDocumentNoTempService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询模拟接收sap接收的单据数据量")
    public R<Page> page(@RequestParam Map<String, Object> params,SapDocumentNoTemp sapDocumentNoTemp) {
        return new R<>(sapDocumentNoTempService.selectPage(new QueryPage<>(params), new EntityWrapper<>(sapDocumentNoTemp)));
    }

    /**
     * 添加
     * @param  sapDocumentNoTemp  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加模拟接收sap接收的单据数据量")
    public R<Boolean> add(@RequestBody SapDocumentNoTemp sapDocumentNoTemp,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            return new R<>(sapDocumentNoTempService.insert(sapDocumentNoTemp));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除模拟接收sap接收的单据数据量通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        SapDocumentNoTemp sapDocumentNoTemp = new SapDocumentNoTemp();
        return new R<>(sapDocumentNoTempService.updateById(sapDocumentNoTemp));
    }

    /**
     * 编辑
     * @param  sapDocumentNoTemp  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除模拟接收sap接收的单据数据量")
    public R<Boolean> edit(@RequestBody SapDocumentNoTemp sapDocumentNoTemp) {
        return new R<>(sapDocumentNoTempService.updateById(sapDocumentNoTemp));
    }
}
