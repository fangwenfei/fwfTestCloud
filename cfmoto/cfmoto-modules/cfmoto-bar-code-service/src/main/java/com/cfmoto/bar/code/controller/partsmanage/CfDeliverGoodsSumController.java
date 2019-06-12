package com.cfmoto.bar.code.controller.partsmanage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsSum;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsSumService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author space
 * @since 2019-04-08
 */
@RestController
@RequestMapping("/cfDeliverGoodsSum")
@Api(tags=" 部品发货汇总")
@Slf4j
public class CfDeliverGoodsSumController extends BaseController {

    @Autowired
    private ICfDeliverGoodsSumService cfDeliverGoodsSumService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfDeliverGoodsSum
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfDeliverGoodsSum> get(@RequestParam Integer id) {
        return new R<>(cfDeliverGoodsSumService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfDeliverGoodsSum cfDeliverGoodsSum) {
        return new R<>(cfDeliverGoodsSumService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfDeliverGoodsSum)));
    }

    /**
     * 添加
     * @param  cfDeliverGoodsSum  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfDeliverGoodsSum cfDeliverGoodsSum,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfDeliverGoodsSum.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfDeliverGoodsSumService.insert(cfDeliverGoodsSum));
       }catch (Exception e){
            log.error( ExceptionUtils.getFullStackTrace( e ) );
            return new R<>(R.FAIL, e.getMessage() );
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
        CfDeliverGoodsSum cfDeliverGoodsSum = new CfDeliverGoodsSum();
        return new R<>(cfDeliverGoodsSumService.updateById(cfDeliverGoodsSum));
    }

    /**
     * 编辑
     * @param  cfDeliverGoodsSum  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfDeliverGoodsSum cfDeliverGoodsSum) {
        return new R<>(cfDeliverGoodsSumService.updateById(cfDeliverGoodsSum));
    }
}
