package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfNextNumber;
import com.cfmoto.bar.code.service.ICfNextNumberService;
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
 * 获取下一编号 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-21
 */
@RestController
@RequestMapping("/cfNextNumber")
@Api(tags=" 获取下一编号")
public class CfNextNumberController extends BaseController {
    @Autowired
    private ICfNextNumberService cfNextNumberService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfNextNumber
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfNextNumber> get(@RequestParam Integer id) {
        return new R<>(cfNextNumberService.selectById(id));
    }

    @GetMapping("/getNextNumberByType")
    @ApiOperation(value="通过类型获取下一编号")
    public R<String> getNextNumberByType( @RequestParam String nextType ) {

        try {
            return new R<>(cfNextNumberService.generateNextNumber( nextType ) );
        } catch (Exception e) {
            return new R<>( R.FAIL,e.getMessage() );
        }
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询获取下一编号")
    public R<Page> page(@RequestParam Map<String, Object> params,CfNextNumber cfNextNumber) {
        return new R<>(cfNextNumberService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfNextNumber)));
    }

    /**
     * 添加
     * @param  cfNextNumber  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加获取下一编号")
    public R<Boolean> add(@RequestBody CfNextNumber cfNextNumber,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfNextNumber.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfNextNumberService.insert(cfNextNumber));
       }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }


    }

}
