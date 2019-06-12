package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawScanRecord;
import com.cfmoto.bar.code.service.ICfCostCenterPickOrWithdrawScanRecordService;
import com.cfmoto.bar.code.service.ICfCostCenterPickService;
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
 * 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-20
 */
@RestController
@RequestMapping("/cfCostCenterPickOrWithdrawScanRecord")
@Api(tags = " 成本中心扫描记录表接口")
public class CfCostCenterPickOrWithdrawScanRecordController extends BaseController {
    @Autowired
    private ICfCostCenterPickOrWithdrawScanRecordService cfCostCenterPickOrWithdrawScanRecordService;

    @Autowired
    private ICfCostCenterPickService pickService;

    /**
     * 通过ID查询
     *
     * @param id ID
     * @return CfCostCenterPickOrWithdrawScanRecord
     */
    @PostMapping("/getById")
    @ApiOperation(value = "通过ID查询")
    public R<CfCostCenterPickOrWithdrawScanRecord> get(@RequestParam Integer id) {
        return new R<>(cfCostCenterPickOrWithdrawScanRecordService.selectById(id));
    }


    /**
     * 分页查询信息
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params, CfCostCenterPickOrWithdrawScanRecord cfCostCenterPickOrWithdrawScanRecord) {
        return new R<>(cfCostCenterPickOrWithdrawScanRecordService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfCostCenterPickOrWithdrawScanRecord)));
    }

    /**
     * 添加
     *
     * @param cfCostCenterPickOrWithdrawScanRecord 实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加")
    public R<Boolean> add(@RequestBody CfCostCenterPickOrWithdrawScanRecord cfCostCenterPickOrWithdrawScanRecord, HttpServletRequest httpServletRequest) {
        try {
            int userId = UserUtils.getUserId(httpServletRequest);
            cfCostCenterPickOrWithdrawScanRecord.setObjectSetBasicAttribute(userId, new Date());
            return new R<>(cfCostCenterPickOrWithdrawScanRecordService.insert(cfCostCenterPickOrWithdrawScanRecord));
        } catch (Exception e) {
            return new R<>(R.FAIL, e.getMessage());
        }


    }

    /**
     * 根据id删除扫描记录表中数据，更新汇总界面的实发数量，更新清单表中的实发数量
     *
     * @param id
     * @return actualSendNumber
     * @author ye
     */
    @GetMapping("/deleteAndGetUpdateNumberById")
    @ApiOperation(value = "根据id删除扫描记录表中的行数据以及返回更新后的清单表的实发数量")
    public R<Map<String, Object>> deleteAndGetUpdateById(String orderNo, Integer id, HttpServletRequest request) {
        //获取当前用户的id
        Integer userId = UserUtils.getUserId(request);

        R<Map<String, Object>> r = new R<>();

        if (id == null || id.equals(0)) {
            r.setCode(R.FAIL);
            r.setMsg("未选中删除行数据");
            return r;
        }

        try {//使用try catch将修改数据库的操作包起来，如果操作数据库时出现错误则捕获并返回错误信息到前端进行提示

            cfCostCenterPickOrWithdrawScanRecordService.deleteAndGetUpdateNumberById(id, userId, orderNo);
            r.setData(pickService.loadDataFromLocalDataBase(orderNo, userId));
        } catch (Exception e) {
            r.setCode(R.FAIL);
            r.setMsg(e.getMessage());
            logger.info("操作数据库时出现异常，异常原因为" + e.getMessage());
        }
        return r;

    }


    /**
     * 根据id更新成本中心领退扫描记录表中的数据，更新汇总界面信息，更新成本中心领退料清单表中的数据（实发数量）
     * 其中需要校验待修改数量是否大于行数据数量（只能改小，不能改大）,否则则报错
     *
     * @param id number
     */
    @GetMapping("updateById")
    @ApiOperation(value = "根据id修改扫描表的数量和备注，清单表的已扫描数量")
    public R<Map<String, Object>> updateById(String orderNo, Integer id, Integer number, @RequestParam(required = false, defaultValue = "") String remark, HttpServletRequest request) {
        //获取当前登陆用户的id
        Integer userId = UserUtils.getUserId(request);

        R<Map<String, Object>> r = new R<>();

        if (id == null || id.equals(0)) {
            r.setCode(R.FAIL);
            r.setMsg("未选中修改行数据");
            return r;
        }
        CfCostCenterPickOrWithdrawScanRecord scanRecord = cfCostCenterPickOrWithdrawScanRecordService.selectById(id);
        //待修改数量大于行数据数量，报错
        if (number >= scanRecord.getNumber()) {
            r.setCode(R.FAIL);
            r.setMsg("输入的数量不能大于等于行数据的数量");
            return r;
        }

        try {//捕获对数据库进行修改时发生的异常，并封装错误信息返回给前端界面进行显示
            cfCostCenterPickOrWithdrawScanRecordService.updatedById(orderNo, id, number, remark, userId);
            r.setData(pickService.loadDataFromLocalDataBase(orderNo, userId));
        } catch (Exception e) {
            r.setCode(R.FAIL);
            r.setMsg(e.getMessage());
            logger.info("操作数据库时出现异常，异常原因为:" + e.getMessage());
        }
        return r;
    }

}
