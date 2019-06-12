package com.cfmoto.bar.code.controller.cecdelivergoods;
import java.util.Map;
import java.util.Date;

import com.cfmoto.bar.code.service.cecdelivergoods.ICfCecDeliverGoodsScanRecordService;
import com.github.pig.common.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.model.entity.CfCecDeliverGoodsScanRecord;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-06-04
 */
@RestController
@RequestMapping("/cfCecDeliverGoodsScanRecord")
@Api(tags=" ")
public class CfCecDeliverGoodsScanRecordController extends BaseController {
    @Autowired private ICfCecDeliverGoodsScanRecordService cfCecDeliverGoodsScanRecordService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfCecDeliverGoodsScanRecord
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfCecDeliverGoodsScanRecord> get(@RequestParam Integer id) {
        return new R<>(cfCecDeliverGoodsScanRecordService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfCecDeliverGoodsScanRecord cfCecDeliverGoodsScanRecord) {
        return new R<>(cfCecDeliverGoodsScanRecordService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfCecDeliverGoodsScanRecord)));
    }

    /**
     * 添加
     * @param  cfCecDeliverGoodsScanRecord  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfCecDeliverGoodsScanRecord cfCecDeliverGoodsScanRecord,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfCecDeliverGoodsScanRecord.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfCecDeliverGoodsScanRecordService.insert(cfCecDeliverGoodsScanRecord));
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
        CfCecDeliverGoodsScanRecord cfCecDeliverGoodsScanRecord = new CfCecDeliverGoodsScanRecord();
        return new R<>(cfCecDeliverGoodsScanRecordService.updateById(cfCecDeliverGoodsScanRecord));
    }

    /**
     * 编辑
     * @param  cfCecDeliverGoodsScanRecord  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfCecDeliverGoodsScanRecord cfCecDeliverGoodsScanRecord) {
        return new R<>(cfCecDeliverGoodsScanRecordService.updateById(cfCecDeliverGoodsScanRecord));
    }
}
