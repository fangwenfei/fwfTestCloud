package com.cfmoto.bar.code.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.cfmoto.bar.code.feign.UserFeignService;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.vo.UserVO;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pig.common.constant.CommonConstant;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.cfmoto.bar.code.model.entity.CfCabinet;
import com.cfmoto.bar.code.service.ICfCabinetService;
import javax.servlet.http.HttpServletRequest;
import com.github.pig.common.web.BaseController;

/**
 * <p>
 * 柜子信息 前端控制器
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-20
 */
@RestController
@RequestMapping("/cfCabinet")
@Api(tags=" 柜子信息")
public class CfCabinetController extends BaseController {
    @Autowired private ICfCabinetService cfCabinetService;

    @Autowired private UserFeignService userFeignService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return CfCabinet
    */
    @PostMapping("/getById")
    @ApiOperation(value="通过ID查询")
    public R<CfCabinet> get(@RequestParam Integer id) {
        return new R<>(cfCabinetService.selectById(id));
    }

    @PostMapping("/getUserById")
    @ApiOperation(value="通过ID查询")
    public R<UserVO> getUserById() {
        return new R<>( userFeignService.user(1));
    }




    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @PostMapping("/page")
    @ApiOperation(value="分页查询柜子信息")
    public R<Page> page(@RequestParam Map<String, Object> params,CfCabinet cfCabinet) {
        return new R<>(cfCabinetService.selectPage(new QueryPage<>(params), new EntityWrapper<>(cfCabinet)));
    }

    /**
     * 添加
     * @param  cfCabinet  实体
     * @return success/false
     */
    @PostMapping("/add")
    @ApiOperation(value="添加柜子信息")
    public R<Boolean> add(@RequestBody CfCabinet cfCabinet,HttpServletRequest httpServletRequest) {
        try{
            int userId= UserUtils.getUserId(httpServletRequest);
            cfCabinet.setObjectSetBasicAttribute(userId,new Date());
            return new R<>(cfCabinetService.insert(cfCabinet));
       }catch (Exception e){
            return new R<>(R.FAIL, CfCabinet.CF_CABINET_SQL_ADD);
        }


    }

    /**
     * 删除
     * @param id ID
     * @return success/false
     */
    @PostMapping("/deleteById")
    @ApiOperation(value="删除柜子信息通过ID")
    public R<Boolean> delete(@RequestParam Integer id) {
        CfCabinet cfCabinet = new CfCabinet();
        return new R<>(cfCabinetService.updateById(cfCabinet));
    }

    /**
     * 编辑
     * @param  cfCabinet  实体
     * @return success/false
     */
    @PostMapping("/edit")
    @ApiOperation(value="编辑除柜子信息")
    public R<Boolean> edit(@RequestBody CfCabinet cfCabinet) {
        return new R<>(cfCabinetService.updateById(cfCabinet));
    }

    /**
     * 扫描装箱进行装柜
     * @param  params  实体
     * @return success/false
     */
    @PostMapping("/addCabinet")
    @ApiOperation(value="扫描装箱进行装柜")
    public R<Map<String, Object>> addCabinet(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            return cfCabinetService.addCabinet(params,httpServletRequest);
        }catch (Exception e){
            return new R<>(R.FAIL, CfCabinet.CF_CABINET_SQL_ADD);
        }
    }

    /**
     * 获取销售订单list
     *
     */
    @PostMapping("/selectSendGoodsNoList")
    @ApiOperation(value="获取发货通知单list")
    public R< List<SelectList>> selectSendGoodsNoList(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            List<SelectList> selectSendGoodsNoList=new ArrayList<>();
            SelectList selectListA=new SelectList();
            selectListA.setSelectKey("SG00001");
            selectListA.setSelectValue("SG00001");
            selectListA.setSelectDescription("发货通知单001");
            selectSendGoodsNoList.add(selectListA);
            SelectList selectListB=new SelectList();
            selectListB.setSelectKey("SG00002");
            selectListB.setSelectValue("SG00002");
            selectListB.setSelectDescription("发货通知单002");
            selectSendGoodsNoList.add(selectListB);
            SelectList selectListC=new SelectList();
            selectListC.setSelectKey("SG00003");
            selectListC.setSelectValue("SG00003");
            selectListC.setSelectDescription("发货通知单003");
            selectSendGoodsNoList.add(selectListC);
            return new R<>(selectSendGoodsNoList);

        }catch (Exception e){
            return new R<>(R.FAIL, CfCabinet.CF_CABINET_GET_DATA);
        }


    }

    /**
     * 通过销售订单获取单据号list
     *
     */
    @PostMapping("/selectSalesOrderListBySendGoodsNo")
    @ApiOperation(value="通过发货通知单获取销售订单")
    public R< SelectList> selectSalesOrderListBySendGoodsNo(@RequestBody Map<String, Object> params,HttpServletRequest httpServletRequest) {
        try{
            int userId=UserUtils.getUserId(httpServletRequest);
            String salesOrder= params.getOrDefault("sendGoodsNo", "").toString();
            return new R<>(cfCabinetService.selectSalesOrderListBySendGoodsNo(userId,salesOrder));
        }catch (Exception e){
            return new R<>(R.FAIL, CfCabinet.CF_CABINET_GET_DATA);
        }
    }




}
