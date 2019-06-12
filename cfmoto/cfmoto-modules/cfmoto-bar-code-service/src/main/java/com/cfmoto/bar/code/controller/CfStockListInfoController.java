package com.cfmoto.bar.code.controller;
import java.util.Map;
import java.util.Date;
import com.github.pig.common.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pig.common.constant.CommonConstant;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.model.entity.CfStockListInfo;
import com.cfmoto.bar.code.service.ICfStockListInfoService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-12
 */
@RestController
@RequestMapping("/cfStockListInfo")
@Api(tags="备料单信息表 ")
public class CfStockListInfoController extends BaseController {
    @Autowired private ICfStockListInfoService cfStockListInfoService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfStockListInfo
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfStockListInfo> get(@RequestParam Integer id) {
        return new R<>(cfStockListInfoService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfStockListInfo cfStockListInfo) {
        return new R<>(cfStockListInfoService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfStockListInfo)));
    }

    /**
     * 添加
     * @param  cfStockListInfo  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfStockListInfo cfStockListInfo,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfStockListInfo.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfStockListInfoService.insert(cfStockListInfo));
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
    @ApiOperation(value="删除通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfStockListInfo cfStockListInfo = new CfStockListInfo();
        return new R<>(cfStockListInfoService.updateById(cfStockListInfo));
    }

    /**
     * 编辑
     * @param  cfStockListInfo  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfStockListInfo cfStockListInfo) {
        return new R<>(cfStockListInfoService.updateById(cfStockListInfo));
    }
}
