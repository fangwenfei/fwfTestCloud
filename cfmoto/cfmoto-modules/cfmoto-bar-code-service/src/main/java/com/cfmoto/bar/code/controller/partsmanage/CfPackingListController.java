package com.cfmoto.bar.code.controller.partsmanage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfPackingList;
import com.cfmoto.bar.code.service.partsmanage.ICfPackingListService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 装箱清单数据表 前端控制器
 * </p>
 *
 * @author space
 * @since 2019-04-09
 */
@RestController
@RequestMapping("/cfPackingList")
@Api(tags=" 装箱清单数据表")
public class CfPackingListController extends BaseController {

    @Autowired
    private ICfPackingListService cfPackingListService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfPackingList
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfPackingList> get(@RequestParam Integer id) {
        return new R<>(cfPackingListService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询装箱清单数据表")
    public R<Page> page(@RequestParam Map<String, Object> params,CfPackingList cfPackingList) {
        return new R<>(cfPackingListService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfPackingList)));
    }

    /**
     * 添加
     * @param  cfPackingList  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加装箱清单数据表")
    public R<Boolean> add(@RequestBody CfPackingList cfPackingList,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfPackingList.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfPackingListService.insert(cfPackingList));
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
    @ApiOperation(value="删除装箱清单数据表通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfPackingList cfPackingList = new CfPackingList();
        return new R<>(cfPackingListService.updateById(cfPackingList));
    }

    /**
     * 编辑
     * @param  cfPackingList  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除装箱清单数据表")
    public R<Boolean> edit(@RequestBody CfPackingList cfPackingList) {
        return new R<>(cfPackingListService.updateById(cfPackingList));
    }
}
