package com.cfmoto.bar.code.controller.reportworkrecord;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfReportWorkRecord;
import com.cfmoto.bar.code.service.reportworkrecord.ICfReportWorkRecordService;
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
 * 扫描报工记录表 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-11
 */
@RestController
@RequestMapping("/cfReportWorkRecord")
@Api(tags=" 扫描报工记录表")
public class CfReportWorkRecordController extends BaseController {
    @Autowired private ICfReportWorkRecordService cfReportWorkRecordService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfReportWorkRecord
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfReportWorkRecord> get(@RequestParam Integer id) {
        return new R<>(cfReportWorkRecordService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询扫描报工记录表")
    public R<Page> page(@RequestParam Map<String, Object> params,CfReportWorkRecord cfReportWorkRecord) {
        return new R<>(cfReportWorkRecordService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfReportWorkRecord)));
    }

    /**
     * 添加
     * @param  cfReportWorkRecord  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加扫描报工记录表")
    public R<Boolean> add(@RequestBody CfReportWorkRecord cfReportWorkRecord,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfReportWorkRecord.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfReportWorkRecordService.insert(cfReportWorkRecord));
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
    @ApiOperation(value="删除扫描报工记录表通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfReportWorkRecord cfReportWorkRecord = new CfReportWorkRecord();
        return new R<>(cfReportWorkRecordService.updateById(cfReportWorkRecord));
    }

    /**
     * 编辑
     * @param  cfReportWorkRecord  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除扫描报工记录表")
    public R<Boolean> edit(@RequestBody CfReportWorkRecord cfReportWorkRecord) {
        return new R<>(cfReportWorkRecordService.updateById(cfReportWorkRecord));
    }
}
