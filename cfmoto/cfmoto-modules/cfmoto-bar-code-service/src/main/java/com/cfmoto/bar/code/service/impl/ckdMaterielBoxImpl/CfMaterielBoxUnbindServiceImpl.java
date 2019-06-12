package com.cfmoto.bar.code.service.impl.ckdMaterielBoxImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfLoadPackingMapper;
import com.cfmoto.bar.code.mapper.CfMaterielBoxMapper;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxService;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxUnbindService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/3/29.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
@Service
public class CfMaterielBoxUnbindServiceImpl  extends ServiceImpl<CfMaterielBoxMapper, CfMaterielBox> implements ICfMaterielBoxUnbindService {

    @Autowired
    private CfMaterielBoxMapper cfMaterielBoxMapper ;
    @Autowired
    SapFeignService sapFeignService ;

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    CfLoadPackingMapper cfLoadPackingMapper ;

    @Autowired
    ICfMaterielBoxService iCfMaterielBoxService;


    /**
     * 1.删除该条码号
     * 2.如果下一节点XH是箱子，清除该箱子的父类条码号
     *   如果下一节点WL物料，删除该节点数据，并修改装箱清单已装箱数据量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unbindMaterielBox(Map<String, Object> params, int userId) throws ValidateCodeException ,Exception {
        String barCodeNo= params.getOrDefault("barCodeNo", "").toString();
        if(!StringUtils.isNotBlank(barCodeNo)){
            throw  new ValidateCodeException("请填写要解箱/解托条码号");
        }
        String  barCodeNoHeader=  barCodeNo.substring(0,2);
        Date dateNow=new Date();
        if(!(barCodeNo.length()>2&&(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(barCodeNoHeader)
                ||CfMaterielBox.MATERIEL_BOX_NO_HEADER_TH.equals(barCodeNoHeader)))){
            throw  new ValidateCodeException("该条码非物料或装箱条码，验证失败");
        }
        if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_TH.equals(barCodeNoHeader)){
            Map<String,Object> paramMapSap =new HashedMap();
            paramMapSap.put("functionName","ZMM_BC_014");
            Map<String,Object> dataMap =new HashedMap();
            dataMap.put("IV_ZTNUM",barCodeNo);
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
            // ET_DATA
            JSONArray jsonArray=jsonObject.getJSONArray("ET_DATA");
            List<CfMaterielBox> cfMaterielBoxList=new ArrayList<>();
            for (int i=0;i<jsonArray.size();i++) {
                JSONObject etData=jsonArray.getJSONObject(i);
                CfMaterielBox cfMaterielBox=new CfMaterielBox();
                cfMaterielBox.setBarCodeNo(etData.getString("ZTMBM"));//条码
                cfMaterielBox.setParentNo(etData.getString("ZFLTMH"));//父类条码号
                cfMaterielBox.setObjectSetBasicAttribute(userId,new Date());
                cfMaterielBox.setSalesOrder(etData.getString("ZXSDD"));//销售订单
                cfMaterielBox.setDocumentNo(etData.getString("ZDJNUM"));//单据号
                cfMaterielBox.setBoxingTime(etData.getTimestamp("ZXTM"));//装箱时间
                cfMaterielBox.setBoxingUser(etData.getInteger("ZXRM"));//装箱人
                cfMaterielBox.setModel(etData.getString("ZGGXH"));//规格
                cfMaterielBox.setWeight(etData.getBigDecimal("ZMWG"));//毛重
                cfMaterielBox.setType(etData.getInteger("ZTYPE"));//类型1表示箱子  2表示物料  3表示托
                cfMaterielBox.setContractNo(etData.getString("ZHTBH"));//合同号
                cfMaterielBox.setMaterialNo(etData.getString("MATNR"));//物料代码
                cfMaterielBox.setMaterialName(etData.getString("MAKTX_EN"));//物料名称
                cfMaterielBox.setQty(etData.getBigDecimal("BDMNG"));//数量
                cfMaterielBox.setCarModel(etData.getString("FERTH"));//车型
                cfMaterielBox.setCountry(etData.getString("LANDX"));//国家
                cfMaterielBox.setObjectSetBasicAttribute(userId,dateNow);
                cfMaterielBoxList.add(cfMaterielBox);
               /* cfMaterielBoxMapper.insert(cfMaterielBox);*/
            }
            iCfMaterielBoxService.insertBatch(cfMaterielBoxList);
          }

          //1.删除该条码号
        CfMaterielBox cfMaterielBoxH=new CfMaterielBox();
        cfMaterielBoxH.setBarCodeNo(barCodeNo);//条码、
        cfMaterielBoxH=this.selectOne(new EntityWrapper<>(cfMaterielBoxH));
        if(cfMaterielBoxH==null){
            throw  new ValidateCodeException("该条码有问题，不可以解托或解箱");
        }
        if(StringUtils.isNotBlank(cfMaterielBoxH.getParentNo())){
            if(!cfMaterielBoxH.getType().equals(CfMaterielBox.CF_TYPE_3)){
                throw  new ValidateCodeException("未解托不可以解箱，未解主箱不可以解次箱");
            }
        }

        this.deleteById(cfMaterielBoxH.getBarCodeId());
        //获取该条码直属节点
        CfMaterielBox cfMaterielBoxP=new CfMaterielBox();
        cfMaterielBoxP.setParentNo(barCodeNo);//条码
        List<CfMaterielBox> cfMaterielBoxList=this.selectList(new EntityWrapper<>(cfMaterielBoxP));
        for(CfMaterielBox cfMaterielBoxAt:cfMaterielBoxList){
            if(cfMaterielBoxAt.getType()==CfMaterielBox.CF_TYPE_1){
                cfMaterielBoxAt.setParentNo("");
                this.updateById(cfMaterielBoxAt);
            }else{
                String[] arrStr = cfMaterielBoxAt.getBarCodeNo().split("&");
                String materielNo=arrStr[1];//物料号
                BigDecimal barCodeNoNumber= new BigDecimal(arrStr[2]);//该条码代表的数量
                //装箱清单
                CfLoadPacking cfLoadPacking=new  CfLoadPacking ();
                cfLoadPacking.setMaterialNo(materielNo);
                cfLoadPacking.setSalesOrderNo(cfMaterielBoxAt.getSalesOrder());
                cfLoadPacking.setDocumentNo(cfMaterielBoxAt.getDocumentNo());
                cfLoadPacking=cfLoadPackingMapper.selectOne(cfLoadPacking);
                BigDecimal materielNumberTotal=cfLoadPacking.getLoadNumber().subtract(barCodeNoNumber);
                cfLoadPacking.setLoadNumber(materielNumberTotal);
                //修改装箱数量
                cfLoadPackingMapper.updateById(cfLoadPacking);
                //删除该物料
               this.deleteById(cfMaterielBoxAt.getBarCodeId());
            }
        }
        //2.如果下一节点XH是箱子，清除该箱子的父类条码号
       // 如果下一节点WL物料，删除该节点数据，并修改装箱清单已装箱数据量


        return true;
    }

}
