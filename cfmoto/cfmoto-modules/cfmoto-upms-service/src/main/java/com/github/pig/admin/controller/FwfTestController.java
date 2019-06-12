package com.github.pig.admin.controller;
import java.util.Map;
import java.util.Date;

import com.github.pig.admin.model.dto.UserInfo;
import com.github.pig.admin.model.entity.FwfTest;
import com.github.pig.admin.service.IFwfTestService;
import com.github.pig.common.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pig.common.constant.CommonConstant;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;

import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2018-12-18
 */
@RestController
@RequestMapping("/fwfTest")
public class FwfTestController extends BaseController {
    @Autowired private IFwfTestService fwfTestService;


    @GetMapping("/info")
    public String  user(UserVO userVo) {

        return "你说你不牛";
    }
    /**
    * 通过ID查询
    *
    * @param id ID
    * @return FwfTest
    */
    @GetMapping("/{id}")
    public R<FwfTest> get(@PathVariable Integer id) {
        return new R<>(fwfTestService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @RequestMapping("/page")
    public Page page(@RequestParam Map<String, Object> params) {
        params.put(CommonConstant.DEL_FLAG, CommonConstant.STATUS_NORMAL);
        return fwfTestService.selectPage(new Query<>(params), new EntityWrapper<>());
    }

    /**
     * 添加
     * @param  fwfTest  实体
     * @return success/false
     */
    @PostMapping
    public R<Boolean> add(@RequestBody FwfTest fwfTest) {
        return new R<>(fwfTestService.insert(fwfTest));
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Integer id) {
        FwfTest fwfTest = new FwfTest();
        return new R<>(fwfTestService.updateById(fwfTest));
    }

    /**
     * 编辑
     * @param  fwfTest  实体
     * @return success/false
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody FwfTest fwfTest) {
        return new R<>(fwfTestService.updateById(fwfTest));
    }
}
