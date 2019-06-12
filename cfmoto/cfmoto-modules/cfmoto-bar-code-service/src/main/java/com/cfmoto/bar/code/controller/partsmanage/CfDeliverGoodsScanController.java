package com.cfmoto.bar.code.controller.partsmanage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfDeliverGoodsScan;
import com.cfmoto.bar.code.model.vo.DeliverGoodsFullVo;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsScanService;
import com.cfmoto.bar.code.service.partsmanage.ICfDeliverGoodsService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@RequestMapping("/cfDeliverGoodsScan")
@Api(tags=" 部品发货扫描")
@Slf4j
public class CfDeliverGoodsScanController extends BaseController {

    @Autowired
    private ICfDeliverGoodsScanService cfDeliverGoodsScanService;


    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfDeliverGoodsScan
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfDeliverGoodsScan> get(@RequestParam Integer id) {
        return new R<>(cfDeliverGoodsScanService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfDeliverGoodsScan cfDeliverGoodsScan) {
        return new R<>(cfDeliverGoodsScanService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfDeliverGoodsScan)));
    }

    /**
     * 添加
     * @param  cfDeliverGoodsScan  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfDeliverGoodsScan cfDeliverGoodsScan,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfDeliverGoodsScan.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfDeliverGoodsScanService.insert(cfDeliverGoodsScan));
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
        CfDeliverGoodsScan cfDeliverGoodsScan = new CfDeliverGoodsScan();
        return new R<>(cfDeliverGoodsScanService.updateById(cfDeliverGoodsScan));
    }

    /**
     * 编辑
     * @param  cfDeliverGoodsScan  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfDeliverGoodsScan cfDeliverGoodsScan) {
        return new R<>(cfDeliverGoodsScanService.updateById(cfDeliverGoodsScan));
    }
}
