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
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInfo;
import com.cfmoto.bar.code.service.ICfCostCenterPickOrWithdrawInfoService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-20
 */
@RestController
@RequestMapping("/cfCostCenterPickOrWithdrawInfo")
@Api(tags=" 成本中心信息表接口")
public class CfCostCenterPickOrWithdrawInfoController extends BaseController {
    @Autowired private ICfCostCenterPickOrWithdrawInfoService cfCostCenterPickOrWithdrawInfoService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfCostCenterPickOrWithdrawInfo
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfCostCenterPickOrWithdrawInfo> get(@RequestParam Integer id) {
        return new R<>(cfCostCenterPickOrWithdrawInfoService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询")
    public R<Page> page(@RequestParam Map<String, Object> params,CfCostCenterPickOrWithdrawInfo cfCostCenterPickOrWithdrawInfo) {
        return new R<>(cfCostCenterPickOrWithdrawInfoService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfCostCenterPickOrWithdrawInfo)));
    }

    /**
     * 添加
     * @param  cfCostCenterPickOrWithdrawInfo  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加")
    public R<Boolean> add(@RequestBody CfCostCenterPickOrWithdrawInfo cfCostCenterPickOrWithdrawInfo,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfCostCenterPickOrWithdrawInfo.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfCostCenterPickOrWithdrawInfoService.insert(cfCostCenterPickOrWithdrawInfo));
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
        CfCostCenterPickOrWithdrawInfo cfCostCenterPickOrWithdrawInfo = new CfCostCenterPickOrWithdrawInfo();
        return new R<>(cfCostCenterPickOrWithdrawInfoService.updateById(cfCostCenterPickOrWithdrawInfo));
    }

    /**
     * 编辑
     * @param  cfCostCenterPickOrWithdrawInfo  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除")
    public R<Boolean> edit(@RequestBody CfCostCenterPickOrWithdrawInfo cfCostCenterPickOrWithdrawInfo) {
        return new R<>(cfCostCenterPickOrWithdrawInfoService.updateById(cfCostCenterPickOrWithdrawInfo));
    }
}
