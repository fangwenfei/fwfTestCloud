package com.cfmoto.bar.code.controller.ckdMaterielBox;

import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.service.ckdMaterielBox.ISapJobOrderTempService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模拟通过sap获取生产任务单信息 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-27
 */
@RestController
@RequestMapping("/sapJobOrderTemp")
@Api(tags=" 模拟通过sap获取生产任务单信息")
public class SapJobOrderTempController extends BaseController {
    @Autowired private ISapJobOrderTempService sapJobOrderTempService;
    private static final String PAGE = "page";
    private static final String LIMIT = "limit";
    /**
    * 通过ID查询
    *
    * @param id ID
    * @return SapJobOrderTemp
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<SapJobOrderTemp> get(@RequestParam Integer id) {
        return new R<>(sapJobOrderTempService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询模拟通过sap获取生产任务单信息")
    public R<Page> page(@RequestParam Map<String, Object> params,SapJobOrderTemp sapJobOrderTemp) {
        try {

            List<SapJobOrderTemp> sapJobOrderTempList= sapJobOrderTempService.getSapJobOrderData(params, sapJobOrderTemp);
            //通过单据头获取汇总数据
            Page<SapJobOrderTemp> SapJobOrderPage=new Page<>( Integer.parseInt(params.getOrDefault(PAGE, 1).toString())
                    , Integer.parseInt(params.getOrDefault(LIMIT, 10).toString()));
            SapJobOrderPage.setRecords(sapJobOrderTempList);
            return new R<>(SapJobOrderPage);
        } catch (Exception e) {
            e.printStackTrace();
            return new R<>(R.FAIL,e.getMessage());
        }

    }

    /**
     * 添加
     * @param  sapJobOrderTemp  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加模拟通过sap获取生产任务单信息")
    public R<Boolean> add(@RequestBody SapJobOrderTemp sapJobOrderTemp,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            return new R<>(sapJobOrderTempService.insert(sapJobOrderTemp));
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
    @ApiOperation(value="删除模拟通过sap获取生产任务单信息通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        SapJobOrderTemp sapJobOrderTemp = new SapJobOrderTemp();
        return new R<>(sapJobOrderTempService.updateById(sapJobOrderTemp));
    }

    /**
     * 编辑
     * @param  sapJobOrderTemp  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除模拟通过sap获取生产任务单信息")
    public R<Boolean> edit(@RequestBody SapJobOrderTemp sapJobOrderTemp) {
        return new R<>(sapJobOrderTempService.updateById(sapJobOrderTemp));
    }
}
