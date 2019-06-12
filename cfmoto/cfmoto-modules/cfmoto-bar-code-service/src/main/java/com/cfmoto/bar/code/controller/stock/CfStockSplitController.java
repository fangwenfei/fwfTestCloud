package com.cfmoto.bar.code.controller.stock;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfStockSplit;
import com.cfmoto.bar.code.model.vo.CfStockSplitVo;
import com.cfmoto.bar.code.service.ICfStockSplitService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 备料拆分 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-04-22
 */
@RestController
@RequestMapping("/cfStockSplit")
@Api(tags=" 备料拆分")
public class CfStockSplitController extends BaseController {
    @Autowired private ICfStockSplitService cfStockSplitService;



    /**
    * 通过ID查询
    *
    * @param  cfStockSplit
    * @return CfStockSplit
    */
    @PostMapping("/getCfStockSplit")
    @ApiOperation(value="通过条码获取拆分数据")
    public R<Map<String, Object>> get(@RequestBody CfStockSplit cfStockSplit) {
        try{
            CfStockSplit  cfStockSplitOne=  cfStockSplitService.selectOne(new EntityWrapper<>(cfStockSplit));
            List<CfStockSplitVo> cfStockSplitVo= JSONArray.parseArray(cfStockSplitOne.getBatchNoText(),CfStockSplitVo.class);
            Map<String, Object> resultR=new HashedMap();
            resultR.put("cfStockSplit",cfStockSplitOne);
            resultR.put("cfStockSplitVo",cfStockSplitVo);
            return new R<>(resultR);
        }catch (Exception e){
            return new R<>(R.FAIL,"获取数据失败");
        }


    }


    /**
     * 通过ID查询
     *
     * @param  params
     * @return CfStockSplit
     */
    @PostMapping("/splitCfStock")
    @ApiOperation(value="拆分备料数据")
    public R<Map<String, Object>> splitCfStock(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>( cfStockSplitService.splitCfStock(params,userId));
        }catch (Exception e){
            return new R<>(R.FAIL,e.getMessage());
        }

    }

    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询备料拆分")
    public R<Page> page(@RequestParam Map<String, Object> params,CfStockSplit cfStockSplit) {
        return new R<>(cfStockSplitService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfStockSplit)));
    }

    /**
     * 备料标识标签补打印
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/getCfStockSplitData")
    @ApiOperation(value="备料标识标签补打印")
    public R<List<CfStockSplit>> getCfStockSplit(@RequestBody CfStockSplit cfStockSplit) {
        try {
            List<CfStockSplit> list= cfStockSplitService.getCfStockSplit(cfStockSplit);
            return new R<>(list);
        }catch (Exception e){
            return new R<>(R.FAIL,e.getMessage());
        }

    }




    /**
     * 添加
     * @param  cfStockSplit  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加备料拆分")
    public R<Boolean> add(@RequestBody CfStockSplit cfStockSplit,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfStockSplit.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfStockSplitService.insert(cfStockSplit));
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
    @ApiOperation(value="删除备料拆分通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfStockSplit cfStockSplit = new CfStockSplit();
        return new R<>(cfStockSplitService.updateById(cfStockSplit));
    }

    /**
     * 编辑
     * @param  cfStockSplit  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除备料拆分")
    public R<Boolean> edit(@RequestBody CfStockSplit cfStockSplit) {
        return new R<>(cfStockSplitService.updateById(cfStockSplit));
    }
}
