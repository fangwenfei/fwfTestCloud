package com.cfmoto.bar.code.service.impl;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfMaterielBoxMapper;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfCabinet;
import com.cfmoto.bar.code.mapper.CfCabinetMapper;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.cfmoto.bar.code.service.ICfCabinetService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.utiles.NoCodeUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 柜子信息 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-20
 */
@Service
@Transactional
public class CfCabinetServiceImpl extends ServiceImpl<CfCabinetMapper, CfCabinet> implements ICfCabinetService {



    @Autowired
    SapFeignService sapFeignService ;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Map<String, Object>> addCabinet(Map<String, Object> params, HttpServletRequest httpServletRequest) throws Exception {
        Map<String, Object> resultMap =new HashedMap();
        //统一时间
        Date newDate=new Date();
        LocalDateTime today = LocalDateTime.now();
        //获取登录id
        int userId=UserUtils.getUserId(httpServletRequest);
        //获取销售订单
        String salesOrder= params.getOrDefault("salesOrder", "").toString();
        //发货通知单
        String sendGoodsNo= params.getOrDefault("sendGoodsNo", "").toString();
        //实物柜号
        String realCabinetNo= params.getOrDefault("realCabinetNo", "").toString();
        //货柜铅封号
        String containerSealNo= params.getOrDefault("containerSealNo", "").toString();
        //箱子的条码
        String barCodeNoList= params.getOrDefault("barCodeNoList", "").toString();


        CfCabinet cfCabinet=new CfCabinet();
        cfCabinet.setSalesOrder(salesOrder);
        cfCabinet.setSendGoodsNo(sendGoodsNo);
        cfCabinet.setRealCabinetNo(realCabinetNo);
        cfCabinet.setContainerSealNo(containerSealNo);
        cfCabinet.setObjectSetBasicAttribute(userId,newDate);
        this.insert(cfCabinet);
        //（GH+2位年+2位月+2位日+2位时+“-”+发货通知单+“-”+6位流水码；不论多台设备同时作业，同一发货通知单不允许有相同的柜号。）
        String cabinetNo=CfCabinet.CABINET_NO_HEADER_GH+ NoCodeUtils.getDateNo(today)+"-"+sendGoodsNo+
                 "-"+NoCodeUtils.getCodeNoByIdAndLength(6,cfCabinet.getCabinetId());
        cfCabinet.setCabinetNo(cabinetNo);
        //更新柜子的编码
        this.updateById(cfCabinet);
        JSONArray MaterielBoxArray=new JSONArray(barCodeNoList);
        Map<String,Object> paramMapSap =new HashedMap();
        paramMapSap.put("functionName","ZMM_BC_013");

        ArrayList< Map<String,String>> ItDataArray =new ArrayList<>();
        for(int i=0;i<MaterielBoxArray.size();i++){
            Map<String,String> tableMap=new HashedMap();
            tableMap.put("ZXSDD",cfCabinet.getSalesOrder());//销售订单
            tableMap.put("ZTNUM",MaterielBoxArray.getJSONObject(i).getStr("barCodeNo"));//托号
            tableMap.put("ZFHTZD",cfCabinet.getSendGoodsNo());//发货通过单
            tableMap.put("ZSGNUM",realCabinetNo);//实物柜号
            tableMap.put("ZQFNUM",containerSealNo);//铅封号
            ItDataArray.add(tableMap);
        }
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IV_ZGNUM",cfCabinet.getCabinetNo());//柜号
        dataMap.put("IT_DATA",ItDataArray);
        paramMapSap.put("paramMap",dataMap);
        //TODO SAP获取数据
        R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMapSap);
        if(result.getCode()!=0){
            throw  new ValidateCodeException(result.getMsg());
        }

        Map<String,Object> resultMapData=result.getData();
        JSONObject jsonObject =new JSONObject(resultMapData);
        if(!jsonObject.getString("EV_STATUS").equals("1")){
            throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }
        resultMap.put("cfCabinet",cfCabinet);
        return new R<>(resultMap);
    }

    @Override
    public SelectList selectSalesOrderListBySendGoodsNo(int userId, String sendGoodsNo) throws Exception {
        if(!StringUtils.isNotBlank(sendGoodsNo)){
            throw  new ValidateCodeException(CfCabinet.SEND_GOODS_NO_NOT_NULL);
        }
        Map<String,Object> paramMap =new HashedMap();
        paramMap.put("functionName","ZMM_BC_015");
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IV_VBELN",sendGoodsNo);
        paramMap.put("paramMap",dataMap);
        try {
            R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMap);
            if(result.getCode()!=0){
                throw  new ValidateCodeException(result.getMsg());
            }

            Map<String,Object> resultMapData=result.getData();
            JSONObject jsonObject =new JSONObject(resultMapData);
            if(!jsonObject.getString("EV_STATUS").equals("1")){
                throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
            }
            SelectList selectListA=new SelectList();
            selectListA.setSelectKey(jsonObject.getString("EV_VGBEL"));
            selectListA.setSelectValue(jsonObject.getString("EV_VGBEL"));
            selectListA.setSelectDescription(jsonObject.getString("EV_VGBEL"));

            return selectListA;
        }catch (Exception e){
            throw  new ValidateCodeException(e.getMessage());
        }
    }
}
