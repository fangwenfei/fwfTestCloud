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
import com.cfmoto.bar.code.model.entity.CfStockHandoverScanRecord;
import com.cfmoto.bar.code.service.ICfStockHandoverScanRecordService;
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
@RequestMapping("/cfStockHandoverScanRecord")
@Api(tags="备料交接扫描记录表 ")
public class CfStockHandoverScanRecordController extends BaseController {
    @Autowired private ICfStockHandoverScanRecordService cfStockHandoverScanRecordService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfStockHandoverScanRecord
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfStockHandoverScanRecord> get(@RequestParam Integer id) {
        return new R<>(cfStockHandoverScanRecordService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfStockHandoverScanRecord cfStockHandoverScanRecord) {
        return new R<>(cfStockHandoverScanRecordService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfStockHandoverScanRecord)));
    }

    /**
     * 添加
     * @param  cfStockHandoverScanRecord  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfStockHandoverScanRecord cfStockHandoverScanRecord,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfStockHandoverScanRecord.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfStockHandoverScanRecordService.insert(cfStockHandoverScanRecord));
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
        CfStockHandoverScanRecord cfStockHandoverScanRecord = new CfStockHandoverScanRecord();
        return new R<>(cfStockHandoverScanRecordService.updateById(cfStockHandoverScanRecord));
    }

    /**
     * 编辑
     * @param  cfStockHandoverScanRecord  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfStockHandoverScanRecord cfStockHandoverScanRecord) {
        return new R<>(cfStockHandoverScanRecordService.updateById(cfStockHandoverScanRecord));
    }
}
