package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfProcess;
import com.cfmoto.bar.code.service.ICfProcessService;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
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
 * @author FangWenFei
 * @since 2019-01-18
 */
@RestController
@RequestMapping("/cfProcess")
public class CfProcessController extends BaseController {
    @Autowired private ICfProcessService cfProcessService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfProcess
    */
    @GetMapping("/{id}")
    public R<CfProcess> get(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        int m= UserUtils.getUserId(httpServletRequest);
        logger.info("UserUtils.getUser {}",m);
        return new R<>(cfProcessService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @RequestMapping("/page")
    public Page page(@RequestParam Map<String, Object> params) {
        return cfProcessService.selectPage(new Query<>(params), new EntityWrapper<>());
    }

    /**
     * 添加
     * @param  cfProcess  实体
     * @return success/false
     */
    @PostMapping
    public R<Boolean> add(@RequestBody CfProcess cfProcess, HttpServletRequest httpServletRequest) {
        int userId= UserUtils.getUserId(httpServletRequest);
        cfProcess.setLastUpdatedBy(userId);
        cfProcess.setCreatedBy(userId);
        cfProcess.setLastUpdateDate(new Date());
        cfProcess.setCreationDate(new Date());
        return new R<>(cfProcessService.insert(cfProcess));
    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Integer id) {
        CfProcess cfProcess = new CfProcess();
        cfProcess.setProcessId(id);
        cfProcess.setLastUpdateDate(new Date());
        String m= UserUtils.getUser();
        return new R<>(cfProcessService.updateById(cfProcess));
    }

    /**
     * 编辑
     * @param  cfProcess  实体
     * @return success/false
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody CfProcess cfProcess, HttpServletRequest httpServletRequest) {
        int userId= UserUtils.getUserId(httpServletRequest);
        cfProcess.setLastUpdatedBy(userId);
        cfProcess.setLastUpdateDate(new Date());
        return new R<>(cfProcessService.updateById(cfProcess));
    }
}
