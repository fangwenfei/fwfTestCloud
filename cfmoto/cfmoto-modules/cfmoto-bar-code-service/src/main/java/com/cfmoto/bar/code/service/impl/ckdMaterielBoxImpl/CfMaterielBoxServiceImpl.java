package com.cfmoto.bar.code.service.impl.ckdMaterielBoxImpl;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfLoadPackingMapper;
import com.cfmoto.bar.code.mapper.CfMaterielBoxMapper;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfMaterielBoxService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.service.ckdMaterielBox.ISapJobOrderTempService;
import com.cfmoto.bar.code.utiles.NoCodeUtils;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-18
 */
@Service
public class CfMaterielBoxServiceImpl extends ServiceImpl<CfMaterielBoxMapper, CfMaterielBox> implements ICfMaterielBoxService {

    @Autowired
    private CfMaterielBoxMapper cfMaterielBoxMapper ;
    @Autowired
    SapFeignService sapFeignService ;

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    CfLoadPackingMapper cfLoadPackingMapper ;
    @Autowired
    ISapJobOrderTempService iSapJobOrderTempService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addMaterielBox(Map<String, Object> params, int userId) throws Exception {
        Map<String, Object> resultMap =new HashedMap();
        //统一时间
        Date newDate=new Date();
        LocalDateTime today = LocalDateTime.now();
       //前缀 WL XH TH GH
        String materielBoxNoHeader= params.getOrDefault("materielBoxNoHeader", "").toString();
        //获取销售订单
        String salesOrder= params.getOrDefault("salesOrder", "").toString();
        //单据号
        String documentNo= params.getOrDefault("documentNo", "").toString();

        String[] documentNoList = documentNo.split("&");

        String  documentNoAt=documentNoList[0];
        SapJobOrderTemp sapJobOrderTemp=new SapJobOrderTemp();
        sapJobOrderTemp.setSalesOrder(salesOrder);
        sapJobOrderTemp.setJobOrderNo(documentNoAt);
        List<SapJobOrderTemp> sapJobOrderTempList= iSapJobOrderTempService.selectList(new EntityWrapper<>(sapJobOrderTemp));
        if(sapJobOrderTempList.size()<=0){
            throw  new ValidateCodeException("该销售订单和单据号找不到数据");
        }
        sapJobOrderTemp=sapJobOrderTempList.get(0);
        //设置主箱
        CfMaterielBox  cfMaterielBoxHeader=new CfMaterielBox();
        cfMaterielBoxHeader.setBoxingTime(newDate);
        cfMaterielBoxHeader.setBoxingUser(userId);
        cfMaterielBoxHeader.setDocumentNo(documentNo);
        cfMaterielBoxHeader.setParentNo("");
        cfMaterielBoxHeader.setObjectSetBasicAttribute(userId,newDate);
        cfMaterielBoxHeader.setSalesOrder(salesOrder);
        int  materielMapWLNumber=0;
        if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(materielBoxNoHeader)){
            cfMaterielBoxHeader.setType(CfMaterielBox.CF_TYPE_1);//1是箱子
            //毛重
            BigDecimal weight= new BigDecimal(params.getOrDefault("weight", "0").toString());
            //规格
            String model= params.getOrDefault("model", "").toString();
            cfMaterielBoxHeader.setWeight(weight);
            cfMaterielBoxHeader.setModel(model);
        }else if (CfMaterielBox.MATERIEL_BOX_NO_HEADER_TH.equals(materielBoxNoHeader)) {
            cfMaterielBoxHeader.setType(CfMaterielBox.CF_TYPE_3);//3是托
        }
        cfMaterielBoxHeader.setCarModel(sapJobOrderTemp.getModel());
        cfMaterielBoxHeader.setContractNo(sapJobOrderTemp.getContractNo());
        cfMaterielBoxHeader.setCountry(sapJobOrderTemp.getCountry());
        this.insert(cfMaterielBoxHeader);
        //materielBoxNoHeader+2位年+2位月+2位日+2位时+“-”+销售订单+“-”+6位流水码
        String barCodeNo= materielBoxNoHeader+NoCodeUtils.getDateNo(today)+"-"+salesOrder+
                          "-"+NoCodeUtils.getCodeNoByIdAndLength(6,cfMaterielBoxHeader.getBarCodeId());
        cfMaterielBoxHeader.setBarCodeNo(barCodeNo);
        this.updateById(cfMaterielBoxHeader);
        Map<String, CfMaterielBox> materielMapWL =new HashedMap();
        //获取扫描清单
        String MaterielBoxList= params.getOrDefault("MaterielBoxList", "").toString();
        JSONArray materielBoxArray=new JSONArray(MaterielBoxList);
        ArrayList<CfMaterielBox> cfMaterielBoxLine=new ArrayList<>();
        CfMaterielBox  cfMaterielBox;
        for(int i=0;i<materielBoxArray.size();i++){
            cfMaterielBox=new CfMaterielBox();
            String  materielBoxNo=materielBoxArray.getJSONObject(i).getStr("MaterielBoxNo");
            String  materielBoxNoHeaderSub=  materielBoxNo.substring(0,2);
            //含有XH的是盒子
            if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(materielBoxNoHeaderSub)){
                cfMaterielBox.setBarCodeNo(materielBoxNo);
                cfMaterielBox=cfMaterielBoxMapper.selectOne(cfMaterielBox);
                //修改箱子update
                if(cfMaterielBox!=null){
                    cfMaterielBox.setParentNo(barCodeNo);
                    cfMaterielBox.setObjectSetBasicAttribute(userId,new Date());
                    cfMaterielBox.setSalesOrder(salesOrder);
                    cfMaterielBox.setDocumentNo(documentNo);
                    cfMaterielBox.setBoxingTime(newDate);
                    cfMaterielBox.setBoxingUser(userId);
                    cfMaterielBoxMapper.updateById(cfMaterielBox);
                }
            }else if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_WL.equals(materielBoxNoHeaderSub)){
                //如果是装箱的物料：防止中途期间交叉作业导致数据量问题，并修改装箱清单已装箱数量
                CfLoadPacking cfLoadPacking=new  CfLoadPacking ();
                String[] arrStr = materielBoxNo.split("&");
                String materielNo=arrStr[1];//物料号
                BigDecimal barCodeNoNumber= new BigDecimal(arrStr[2]);//该条码代表的数量
                //装箱清单
                cfLoadPacking.setMaterialNo(materielNo);
                cfLoadPacking.setSalesOrderNo(salesOrder);
                cfLoadPacking.setDocumentNo(documentNo);
                cfLoadPacking=cfLoadPackingMapper.selectOne(cfLoadPacking);
                if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(materielBoxNoHeader)){
                    //物料的数量小于等于待装箱数量（如，大于需求数量，报错"条码数量大于待装箱数量，请注意！"）
                    //已经装箱数量+本次扫描数量
                    BigDecimal materielNumberTotal=cfLoadPacking.getLoadNumber().add(barCodeNoNumber);
                    if(materielNumberTotal.compareTo(cfLoadPacking.getMaterialNumber())>0){
                        throw  new ValidateCodeException("物料的数量应该小于等于待装箱数量，验证失败");
                    }
                    cfLoadPacking.setLoadNumber(materielNumberTotal);
                    //修改装箱数量
                    cfLoadPackingMapper.updateById(cfLoadPacking);

                }
                //添加物料
                cfMaterielBox=new CfMaterielBox();
                cfMaterielBox.setBarCodeNo(materielBoxNo);
                cfMaterielBox.setParentNo(barCodeNo);
                cfMaterielBox.setObjectSetBasicAttribute(userId,new Date());
                cfMaterielBox.setSalesOrder(salesOrder);
                cfMaterielBox.setDocumentNo(documentNo);
                cfMaterielBox.setBoxingTime(newDate);
                cfMaterielBox.setBoxingUser(userId);
                cfMaterielBox.setMaterialNo(materielNo);//物料代码
                cfMaterielBox.setQty(barCodeNoNumber);//数量
                cfMaterielBox.setMaterialName(cfLoadPacking.getMaterialName());//物料名称
                cfMaterielBox.setEnglishName(cfLoadPacking.getEnglishName());//英文名称
                cfMaterielBox.setType(CfMaterielBox.CF_TYPE_2);
                cfMaterielBox.setCountry(sapJobOrderTemp.getCountry());  //国家
                cfMaterielBox.setCarModel(sapJobOrderTemp.getModel());    //车型
                cfMaterielBox.setContractNo(sapJobOrderTemp.getContractNo());//合同号
                cfMaterielBoxMapper.insert(cfMaterielBox);
                if(!materielMapWL.containsKey(materielNo)){
                    materielMapWL.put(materielNo,cfMaterielBox);
                }else{
                    CfMaterielBox cfMaterielBoxWL=  materielMapWL.get(materielNo);
                    cfMaterielBoxWL.setQty(cfMaterielBoxWL.getQty().add(cfMaterielBox.getQty()));
                    materielMapWL.put(materielNo,cfMaterielBoxWL);
                }



            }
           // cfMaterielBoxLine.add(cfMaterielBox);
        }
        //TODO 如果是装托提交到sap
       if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_TH.equals(materielBoxNoHeader)){
           Map<String,Object> paramMapSap =new HashedMap();
           paramMapSap.put("functionName","ZMM_BC_012");
           ArrayList< Map<String,String>> ItDataArray =new ArrayList<>();
           Map<String,String> tableMap=new HashedMap();
           tableMap.put("ZTNUM",cfMaterielBoxHeader.getBarCodeNo());//托号
           tableMap.put("ZTMBM",cfMaterielBoxHeader.getBarCodeNo());//条码
           tableMap.put("ZXSDD",cfMaterielBoxHeader.getSalesOrder());//销售订单
           tableMap.put("ZDJNUM",cfMaterielBoxHeader.getDocumentNo());//单据号
           tableMap.put("ZGGXH", StringUtils.isNotBlank(cfMaterielBoxHeader.getModel())?cfMaterielBoxHeader.getModel():"");//规格
           tableMap.put("ZMWG",  cfMaterielBoxHeader.getWeight()==null?"":cfMaterielBoxHeader.getWeight()+"");//毛重
           tableMap.put("ZXTM",  this.simpleDateFormat.format(cfMaterielBoxHeader.getBoxingTime()));//装箱时间
           tableMap.put("ZXRM", cfMaterielBoxHeader.getBoxingUser()+"");//装箱人
           tableMap.put("ZTYPE",cfMaterielBoxHeader.getType()==null?"":cfMaterielBoxHeader.getType()+"");//类型1表示箱子  2表示物料  3表示托
           tableMap.put("ZFLTMH", StringUtils.isNotBlank(cfMaterielBoxHeader.getParentNo())?cfMaterielBoxHeader.getParentNo():"");//父类条码号
           tableMap.put("ZCJR",cfMaterielBoxHeader.getCreatedBy()+"");//数据创建人
           tableMap.put("ZCJSJ",this.simpleDateFormat.format(cfMaterielBoxHeader.getCreationDate()));//数据创建时间
           ItDataArray.add(tableMap);
           //删除该托信息
           this.deleteById(cfMaterielBoxHeader.getBarCodeId());
           ItDataArray= this.getDataToSap(ItDataArray,cfMaterielBoxHeader.getBarCodeNo(),cfMaterielBoxHeader.getBarCodeNo(),cfMaterielBoxHeader.getSalesOrder());
           Map<String,Object> dataMap =new HashedMap();
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

       }
        for(Map.Entry<String,CfMaterielBox> m:materielMapWL.entrySet()){
            cfMaterielBoxLine.add(m.getValue());
        }
        resultMap.put("cfMaterielBoxHeader",cfMaterielBoxHeader);
        resultMap.put("cfMaterielBoxLine",cfMaterielBoxLine);
        return resultMap;
    }

    /**
     * 通过托号递归
     * @param resultData
     * @param barCodeNo
     * @param lumpNo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public  ArrayList< Map<String,String>> getDataToSap(ArrayList< Map<String,String>> resultData,String  barCodeNo,String lumpNo,String salesOrder){

        //像sap接口插入数据
        CfMaterielBox  cfMaterielBoxHeader=new CfMaterielBox();
        cfMaterielBoxHeader.setParentNo(barCodeNo);
        cfMaterielBoxHeader.setSalesOrder(salesOrder);
        List<CfMaterielBox> cfMaterielBoxLine= this.selectList(new EntityWrapper<>(cfMaterielBoxHeader) );
        List<Integer> cfMaterielBoxLineIdList=new ArrayList<>();
        cfMaterielBoxLine.stream().forEach(s->cfMaterielBoxLineIdList.add(s.getBarCodeId()));
        //删除该箱信息
        if(cfMaterielBoxLine.size()>0){
            this.deleteBatchIds(cfMaterielBoxLineIdList);
        }
        for (CfMaterielBox cfMaterielBox:cfMaterielBoxLine) {
            Map<String,String> tableMap=new HashedMap();
            tableMap.put("ZTNUM",lumpNo);//托号
            tableMap.put("ZTMBM",cfMaterielBox.getBarCodeNo());//条码
            tableMap.put("ZXSDD",cfMaterielBox.getSalesOrder());//销售订单
            tableMap.put("ZDJNUM",cfMaterielBox.getDocumentNo());//单据号
            tableMap.put("ZGGXH", StringUtils.isNotBlank(cfMaterielBox.getModel())?cfMaterielBox.getModel():"");//规格
            tableMap.put("ZMWG", cfMaterielBox.getWeight()==null?"":cfMaterielBox.getWeight()+"");//毛重
            tableMap.put("ZXTM",this.simpleDateFormat.format(cfMaterielBox.getBoxingTime()));//装箱时间
            tableMap.put("ZXRM",cfMaterielBox.getBoxingUser()+"");//装箱人
            tableMap.put("ZTYPE",cfMaterielBox.getType()+"");//类型1表示箱子  2表示物料  3表示托
            tableMap.put("ZFLTMH",StringUtils.isNotBlank(cfMaterielBox.getParentNo())?cfMaterielBox.getParentNo():"");//父类条码号
            tableMap.put("ZCJR",cfMaterielBox.getCreatedBy()+"");//数据创建人
            tableMap.put("ZCJSJ",this.simpleDateFormat.format(cfMaterielBox.getCreationDate()));//数据创建时间
            resultData.add(tableMap);
            this.getDataToSap(resultData,cfMaterielBox.getBarCodeNo(),lumpNo,salesOrder);
        }
        return  resultData;
    }


    @Override
    public Map<String, Object> barCodeVerification(Map<String, Object> param) throws ValidateCodeException {
        String barCodeNo= param.getOrDefault("barCodeNo", "").toString();
        String salesOrder= param.getOrDefault("salesOrder", "").toString();
        String resultMapStr= param.getOrDefault("materielMap", "").toString();
        String verificationType= param.getOrDefault("verificationType", "").toString();
        String documentNo= param.getOrDefault("documentNo", "").toString();

        Map<String, Object> resultMap = new HashMap<String, Object>();

        Gson gson = new Gson();
        Map<String, Object> materielMap = new HashMap<String, Object>();
        Map<String, Object> barCodeMap = new HashMap<String, Object>();
        if(StringUtils.isNotBlank(resultMapStr)){
            resultMap = gson.fromJson(resultMapStr, materielMap.getClass());
            if(resultMap!=null){
                if(resultMap.containsKey("materielMap")){
                    materielMap= gson.fromJson(resultMap.get("materielMap").toString(), materielMap.getClass());
                }if(resultMap.containsKey("barCodeMap")){
                    barCodeMap= gson.fromJson(resultMap.get("barCodeMap").toString(), materielMap.getClass());
                }
            }
        }


        Map<String, Object> resultR =new HashedMap();
        resultR.put("barCodeNo",barCodeNo);
        //判断是否重复提交条码
        if(barCodeMap.containsKey(barCodeNo)){
            throw  new ValidateCodeException("该条码刚刚已扫描，验证失败");
        }
        barCodeMap.put(barCodeNo,"HAVE");
        resultMap.put("barCodeMap",barCodeMap);
        String  barCodeNoHeader=  barCodeNo.substring(0,2);
        if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(verificationType)){

            //装箱
            //前两位必须为WL和XH
            if(!(barCodeNo.length()>2&&(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(barCodeNoHeader)
                    ||CfMaterielBox.MATERIEL_BOX_NO_HEADER_WL.equals(barCodeNoHeader)))){
                throw  new ValidateCodeException("该条码非物料或装箱条码，验证失败");
            }

            //前两位为WL时，检验物料是否存在于装箱清单里
            CfMaterielBox  cfMaterielBoxHeader=new CfMaterielBox();
            cfMaterielBoxHeader.setBarCodeNo(barCodeNo);
            if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_WL.equals(barCodeNoHeader)){
                String[] arrStr = barCodeNo.split("&");
                String materielNo=arrStr[1];
                BigDecimal barCodeNoNumber= new BigDecimal(arrStr[2]);
                //是否已扫描
                cfMaterielBoxHeader=cfMaterielBoxMapper.selectOne(cfMaterielBoxHeader);
                if(cfMaterielBoxHeader!=null){
                    throw  new ValidateCodeException("该条码物料已扫描，验证失败");
                }
                //装箱清单
                CfLoadPacking cfLoadPacking=new  CfLoadPacking ();
                cfLoadPacking.setMaterialNo(materielNo);
                cfLoadPacking.setSalesOrderNo(salesOrder);
                cfLoadPacking.setDocumentNo(documentNo);
                cfLoadPacking=cfLoadPackingMapper.selectOne(cfLoadPacking);
                //检验物料是否存在于装箱清单里
                if(cfLoadPacking==null){
                    throw  new ValidateCodeException("该条码物料不存在于装箱清单里，验证失败");
                }
                BigDecimal materielNumber=new BigDecimal(0);
                if(materielMap.containsKey(materielNo)){
                    materielNumber= new BigDecimal(materielMap.get(materielNo).toString());
                }
                //物料的数量小于等于待装箱数量（如，大于需求数量，报错"条码数量大于待装箱数量，请注意！"）
                //已经装箱数量+已扫描数量+本次扫描数量
                BigDecimal materielNumberTotal=cfLoadPacking.getLoadNumber().add(barCodeNoNumber).add(materielNumber);
                if(materielNumberTotal.compareTo(cfLoadPacking.getMaterialNumber())>0){
                    throw  new ValidateCodeException("物料的数量应该小于等于待装箱数量，验证失败");
                }
                BigDecimal materielNumberResultTotal=barCodeNoNumber.add(materielNumber);
                materielMap.put(materielNo,materielNumberResultTotal);
                resultMap.put("materielMap",materielMap);
                resultR.put("materielMap",resultMap);
            }
            //当前两位为XH时
            //检验销售订单是否匹配
            if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(barCodeNoHeader)){
                String[] arrStr = barCodeNo.split("-");
                String salesOrderS=arrStr[1];
                if(!salesOrder.equals(salesOrderS)){
                    throw  new ValidateCodeException("检验销售订单不匹配，验证失败");
                }
                cfMaterielBoxHeader=cfMaterielBoxMapper.selectOne(cfMaterielBoxHeader);
                if(cfMaterielBoxHeader==null){
                    throw  new ValidateCodeException("该箱条码不存在，验证失败");
                }
                if(StringUtils.isNotBlank(cfMaterielBoxHeader.getParentNo())){
                    throw  new ValidateCodeException("该箱条码也被扫描装箱，验证失败");
                }
                resultMap.put("materielMap",materielMap);
                resultR.put("materielMap",resultMap);
            }
        }else if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_TH.equals(verificationType)){
            if(!(barCodeNo.length()>2&&(CfMaterielBox.MATERIEL_BOX_NO_HEADER_XH.equals(barCodeNoHeader)))){
                throw  new ValidateCodeException("该装箱条码错误，验证失败");
            }
            String[] arrStr = barCodeNo.split("-");
            String salesOrderS=arrStr[1];
            if(!salesOrder.equals(salesOrderS)){
                throw  new ValidateCodeException("检验销售订单不匹配，验证失败");
            }
            CfMaterielBox  cfMaterielBoxHeader=new CfMaterielBox();
            cfMaterielBoxHeader.setBarCodeNo(barCodeNo);
            cfMaterielBoxHeader=cfMaterielBoxMapper.selectOne(cfMaterielBoxHeader);
            if(cfMaterielBoxHeader==null){
                throw  new ValidateCodeException("该箱条码不存在，验证失败");
            }
            if(StringUtils.isNotBlank(cfMaterielBoxHeader.getParentNo())){
                throw  new ValidateCodeException("该箱条码也被扫描装箱，验证失败");
            }
            resultMap.put("materielMap",materielMap);
            resultR.put("materielMap",resultMap);
        }else if(CfMaterielBox.MATERIEL_BOX_NO_HEADER_GH.equals(verificationType)){
            //装柜
            if(!(barCodeNo.length()>2&&(CfMaterielBox.MATERIEL_BOX_NO_HEADER_TH.equals(barCodeNoHeader)))){
                throw  new ValidateCodeException("该托条码错误，验证失败");
            }
            String[] arrStr = barCodeNo.split("-");
            String salesOrderS=arrStr[1];
            if(!salesOrder.equals(salesOrderS)){
                throw  new ValidateCodeException("检验销售订单不匹配，验证失败");
            }
            resultMap.put("materielMap",materielMap);
            resultR.put("materielMap",resultMap);
        }else {
            throw  new ValidateCodeException("该条码为非法条码，验证失败");
        }


        return  resultR;
    }

    @Override
    @Cacheable(value = "MaterielBox:CfMaterielBoxPrint:Select", key = "'MaterielBox:CfMaterielBoxPrint:selectSalesOrderList'")
    public List<SelectList> selectAllSalesOrderNo() {
        return cfMaterielBoxMapper.selectAllSalesOrderNo();
    }

    @Override
    public List<SelectList> selectDocumentNoBySalesOrderNo(String salesOrderNo) {
        return cfMaterielBoxMapper.selectDocumentNoBySalesOrderNo(salesOrderNo);
    }
}
