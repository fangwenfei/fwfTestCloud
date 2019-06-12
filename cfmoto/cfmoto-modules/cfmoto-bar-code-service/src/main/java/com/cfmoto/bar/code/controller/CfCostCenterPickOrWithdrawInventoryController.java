package com.cfmoto.bar.code.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.mapper.CfCostCenterPickOrWithdrawInventoryMapper;
import com.cfmoto.bar.code.model.entity.CfCostCenterPickOrWithdrawInventory;
import com.cfmoto.bar.code.service.ICfCostCenterPickOrWithdrawInventoryService;
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
import java.util.List;
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
@RequestMapping("/cfCostCenterPickOrWithdrawInventory")
@Api(tags = " 成本中心清单表接口")
public class CfCostCenterPickOrWithdrawInventoryController extends BaseController {

    @Autowired
    private ICfCostCenterPickOrWithdrawInventoryService cfCostCenterPickOrWithdrawInventoryService;

    /**
     * 根据前端"未录完"勾选框的状态以及领料单号去 成本中心领退料清单表中查找数据
     * 1.未录完为tue：根据物料代码查找应发数量！=实发数量的行数据
     * 2.录完为false：根据物料代码查找所有对应数据
     */
    @GetMapping("getListByMaterialsNoAndState")
    @ApiOperation(value = "根据领料单号和是否已经录完状态查找对应的成本中心领退料清单表数据")
    public R<List<CfCostCenterPickOrWithdrawInventory>> getListByMaterialsAndState(String orderNo, Boolean state) {

        CfCostCenterPickOrWithdrawInventory inventory = new CfCostCenterPickOrWithdrawInventory();

        inventory.setOrderNo(orderNo);

        //创建包装对象
        EntityWrapper<CfCostCenterPickOrWithdrawInventory> wrapper = new EntityWrapper<>(inventory);


        //判断录入状态
        //查找未录完的数据
        if (state) {

            //拼接查询条件
            wrapper.where(" should_pick_or_withdraw_number != scanned_number ");

        }

        List<CfCostCenterPickOrWithdrawInventory> list = cfCostCenterPickOrWithdrawInventoryService.selectList(wrapper);
        return new R<>(list);
    }
}
