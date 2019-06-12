package com.cfmoto.bar.code.controller.stock;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfStockReturnLine;
import com.cfmoto.bar.code.service.ICfStockReturnLineService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 *  前端控制器备料退货扫描行表
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-19
 */
@RestController
@RequestMapping("/cfStockReturnLine")
@Api(tags="备料退货扫描 ")
public class CfStockReturnLineController extends BaseController {

    @Autowired
    private ICfStockReturnLineService cfStockReturnLineService;

    /**
     * 获取备料退货的数据
     *
     */
    @PostMapping("/getDataByStockByNo")
    @ApiOperation(value="获取备料退货的数据")
    public R<Map<String, Object>> getDataByStockByNo(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfStockReturnLineService.getDataByStockByNo(userId,params));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 添加扫描数据，并更新汇总数据
     *
     */
    @PostMapping("/addcfStockReturnLineData")
    @ApiOperation(value="添加扫描数据，并更新汇总数据")
    public R<Map<String, Object>> addCfStockScanLineData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfStockReturnLineService.addScanLineData(userId,params));
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockReturnLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }

    }
    /**
     * 删除备料退料扫描记录通过ID
     * @param params
     * @return success/false
     */
    @PostMapping("/deleteDataByBarCodeNo")
    @ApiOperation(value="删除备料退料扫描记录通过ID")
    public R<Map<String, Object>> deleteDataByBarCodeNo(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfStockReturnLineService.deleteDataByBarCodeNo(userId,params));
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }
    }

    /**
     * 分页查询备料退料扫描记录
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/cfStockTooReceiveLinePage")
    @ApiOperation(value="分页查询备料退料扫描记录")
    public R<Page> page(@RequestParam Map<String, Object> params) {
        try{
            Integer  page= Integer.parseInt(params.getOrDefault("page", 1).toString());
            Integer  limit= Integer.parseInt(params.getOrDefault("limit", QueryPage.LIMIT_10000).toString());
            Integer  stockRootId= Integer.parseInt(params.getOrDefault("stockRootId", "").toString());
            Page<CfStockReturnLine> pages=new Page<>(page,limit);
            return new R<>(cfStockReturnLineService.selectPage(pages,  new EntityWrapper<CfStockReturnLine>().
                    eq(CfStockReturnLine.STOCK_ROOT_ID_SQL,stockRootId)));
        }catch (Exception e){
            return new R<>(R.FAIL, e.getMessage() );
        }
    }

    /**
     * 分页查询条形码库存表
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @PostMapping("/cfStockReturnLinePage")
    @ApiOperation(value = "分页查询条形码库存表")
    public R<Page> cfStockReturnLinePage(@RequestParam Map<String, Object> params) {
        String   materialsNo= params.getOrDefault("materialsNo", "").toString();
        params.put("limit",QueryPage.LIMIT_10000);
        CfStockReturnLine cfStockReturnLine=new CfStockReturnLine();
        cfStockReturnLine.setMaterialsNo(materialsNo);
        return new R<>(cfStockReturnLineService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfStockReturnLine)));
    }
    /**
     * 获取汇总数据
     *
     */
    @PostMapping("/getCfStockReturnHeaderPage")
    @ApiOperation(value="获取汇总数据")
    public R<Page> getCfStockReturnHeaderPage(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            return new R<>(cfStockReturnLineService.getCfStockReturnHeaderPage(userId,params));
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL, CfStockReturnLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }

    }

    /**
     * 点击提交，更新生产退料扫描记录表中状态为未提交的行数据改为已提交；数据通过接口发送至SAP；更新库存表对应条码的状态为已入库
     *
     */
    @PostMapping("/submitICfStockReturnLineData")
    @ApiOperation(value="提交备料退料扫描记录")
    public R<Map<String, Object>> submitICfStockScanLineData(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            cfStockReturnLineService.submitICfStockReturnLineData(userId,params);
            return new R<>(R.SUCCESS,CfStockReturnLine.SUCCESS_SUBMIT);
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            return new R<>(R.FAIL,  CfStockReturnLine.EX_DOUBLE_DATA);
        }catch (Exception e){
            e.printStackTrace();
            return new R<>(R.FAIL, e.getMessage() );
        }
    }
}
