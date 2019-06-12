package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfFactory;
import com.cfmoto.bar.code.model.entity.CfStorageLocation;
import com.cfmoto.bar.code.service.ICfFactoryService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/cfFactory")
@Api(tags = " 工厂维护")
public class CfFactoryController extends BaseController {
    @Autowired
    private ICfFactoryService cfFactoryService;

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfFactory
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfFactory> get(@RequestParam Integer id) {
        return new R<>(cfFactoryService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询")
    public R<Page> page(@RequestBody Map<String, Object> params) {

        EntityWrapper<CfFactory> entityWrapper = new EntityWrapper<>();
        entityWrapper.like("factory_code", params.getOrDefault("field", "").toString(), SqlLike.DEFAULT);

        return new R<>(cfFactoryService.selectPage(new QueryPage<>(params), entityWrapper));
    }

    /**
     * 添加
     *
     * @param cfFactory 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加")
    public R<Boolean> add(@RequestBody CfFactory cfFactory, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfFactory.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfFactoryService.insert(cfFactory));
        } catch (Exception e) {
            if (e.getMessage().contains("MySQLIntegrityConstraintViolationException")) {
                return new R<>(R.FAIL, "工厂数据已维护");
            }
            return new R<>(R.FAIL, e.getMessage());
        }

    }

    /**
     * 删除
     *
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value = "删除通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        try {

            if (id == null) {
                throw new Exception("id不能为空");
            }
            CfFactory cfFactory = cfFactoryService.selectById(id);
            if (cfFactory == null) {
                throw new Exception("删除数据不存在");
            }
            cfFactoryService.deleteById(id);
        } catch (Exception e) {
            return new R<>(R.FAIL, e.getMessage());
        }
        return new R<>(R.SUCCESS, "删除成功");
    }

    /**
     * 编辑
     *
     * @param cfFactory 实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑除")
    public R<Boolean> edit(@RequestBody CfFactory cfFactory) {
        return new R<>(cfFactoryService.updateById(cfFactory));
    }


    @GetMapping("getAllFactoryByCondition")
    @ApiOperation(value = "获取工厂列表")
    public R<List<CfFactory>> getAllFactoryByCondition(@RequestParam(required = false, defaultValue = "") String factoryCode,
                                            @RequestParam(required = false, defaultValue = "") String factoryName,
                                            @RequestParam(required = false, defaultValue = "") String remark) {
        try {

            List<CfFactory> cfFactoryList = cfFactoryService.getAllFactoryByCondition(factoryCode, factoryName, remark);

            return new R<>(cfFactoryList, "获取工厂列表成功!!!");
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }
}
