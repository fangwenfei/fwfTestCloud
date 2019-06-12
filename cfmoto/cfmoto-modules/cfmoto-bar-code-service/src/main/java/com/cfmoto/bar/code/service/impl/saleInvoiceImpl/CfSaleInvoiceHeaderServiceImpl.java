package com.cfmoto.bar.code.service.impl.saleInvoiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.feign.SapFeignService;
import com.cfmoto.bar.code.mapper.CfSaleInvoiceHeaderMapper;
import com.cfmoto.bar.code.mapper.CfSaleInvoiceLineMapper;
import com.cfmoto.bar.code.mapper.CfSaleInvoiceRootMapper;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceHeader;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceLine;
import com.cfmoto.bar.code.model.entity.CfSaleInvoiceRoot;
import com.cfmoto.bar.code.service.saleInvoice.ICfSaleInvoiceHeaderService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.ValidateCodeException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 销售发货单 服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-03-04
 */
@Service
public class CfSaleInvoiceHeaderServiceImpl extends ServiceImpl<CfSaleInvoiceHeaderMapper, CfSaleInvoiceHeader> implements ICfSaleInvoiceHeaderService {

    @Autowired
    CfSaleInvoiceRootMapper cfSaleInvoiceRootMapper;
    @Autowired
    CfSaleInvoiceLineMapper cfSaleInvoiceLineMapper;
    @Autowired
    CfSaleInvoiceHeaderMapper cfSaleInvoiceHeaderMapper;

    @Autowired
    SapFeignService sapFeignService ;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getCfSaleInvoiceData(int userId, Map<String, Object> params) throws Exception {
       //通过invoiceNo查询单据头
        String invoiceNo= params.getOrDefault("invoiceNo", "").toString();
        String invoiceState= params.getOrDefault(CfSaleInvoiceRoot.INVOICE_STATE, "").toString();
        CfSaleInvoiceRoot cfSaleInvoiceRoot=new CfSaleInvoiceRoot();
        cfSaleInvoiceRoot.setInvoiceNo(invoiceNo);
        cfSaleInvoiceRoot.setInvoiceState(invoiceState);
        int objectVersionNumberOld=1;
        int objectVersionNumberNew=1;
        int cleanNumber=0;
        int rootSize=0;
        cfSaleInvoiceRoot= cfSaleInvoiceRootMapper.selectOne(cfSaleInvoiceRoot);
        Map<String, Object> resultMap=new HashedMap();
        Date newDate=new Date();
        int current=1;
        int size= QueryPage.LIMIT_10000;
        Page<CfSaleInvoiceLine> cfSaleInvoiceLinePage=null;
        if(cfSaleInvoiceRoot!=null){
            objectVersionNumberOld=cfSaleInvoiceRoot.getObjectVersionNumber();
            objectVersionNumberNew=objectVersionNumberOld+1;
            cfSaleInvoiceRoot.setObjectVersionNumber(objectVersionNumberNew);
            cfSaleInvoiceRootMapper.updateById(cfSaleInvoiceRoot);
            rootSize=1;
        }
        Map<String,Object> paramMap =new HashedMap();
        paramMap.put("functionName","ZMM_BC_007");
        Map<String,Object> dataMap =new HashedMap();
        dataMap.put("IV_VBELN",invoiceNo);
        paramMap.put("paramMap",dataMap);
        R< Map<String,Object>> result= sapFeignService.executeJcoFunction(paramMap);
        if(result.getCode()!=0){
            throw  new ValidateCodeException(result.getMsg());
        }
        Map<String,Object> resultMapData=result.getData();
        JSONObject jsonObject =new JSONObject(resultMapData);
        if(!jsonObject.getString("EV_STATUS").equals("1")){
            throw  new ValidateCodeException(jsonObject.getString("EV_MESSAGE"));
        }
        String  invoiceTypeSap= jsonObject.getString("EV_LFART");
        String  invoiceStateSap= jsonObject.getString("EV_GBSTK");
        //订单状态为已完成时，报错“发货通知单处于已完成，请注意！”
        if(CfSaleInvoiceHeader.PARAM_INVOICE_STATE_SAP_C.equals(invoiceStateSap)){
            throw  new ValidateCodeException(CfSaleInvoiceHeader.CF_SALE_INVOICE_STATE_SAP_ERROR);
        }
        //订单类型不为销售发货时，报错“打开功能界面错误，请注意！”
        if(CfSaleInvoiceRoot.INVOICE_STATE_SUBMIT.equals(invoiceState)){
            if(invoiceTypeSap.indexOf(CfSaleInvoiceHeader.PARAM_INVOICE_TYPE_SAP_ZD)<0){
                throw  new ValidateCodeException(CfSaleInvoiceHeader.PARAM_INVOICE_TYPE_SAP_ERROR);
            }
        }else if(CfSaleInvoiceRoot.INVOICE_STATE_RETURN.equals(invoiceState)){
            if(invoiceTypeSap.indexOf(CfSaleInvoiceHeader.PARAM_INVOICE_TYPE_SAP_ZR)<0){
                throw  new ValidateCodeException(CfSaleInvoiceHeader.PARAM_INVOICE_TYPE_SAP_ERROR);
            }
        }

        // ET_DATA
        JSONArray jsonArray=jsonObject.getJSONArray("ET_DATA");
        //插入数据
        for (int i=0;i<jsonArray.size();i++) {
                    JSONObject etData=jsonArray.getJSONObject(i);
                    if(cfSaleInvoiceRoot==null&&i==0){
                        cfSaleInvoiceRoot=new CfSaleInvoiceRoot();
                        cfSaleInvoiceRoot.setInvoiceNo(invoiceNo);
                        cfSaleInvoiceRoot.setInvoiceState(invoiceState);
                        cfSaleInvoiceRoot.setDepartment(etData.getString("BEZEI"));
                        cfSaleInvoiceRoot.setObjectSetBasicAttribute(userId,newDate);
                        cfSaleInvoiceRoot.setObjectVersionNumber(objectVersionNumberNew);
                        //KUNNR  购货单位
                        cfSaleInvoiceRoot.setPurchaseUnit(etData.getString("NAME1"));
                        //VSTEL  分销渠道
                        cfSaleInvoiceRoot.setChannel(etData.getString("VSTEL"));
                        cfSaleInvoiceRootMapper.insert(cfSaleInvoiceRoot);
                        cleanNumber=1;
                    }
                    CfSaleInvoiceHeader  cfSaleInvoiceHeader= new CfSaleInvoiceHeader();

                    cfSaleInvoiceHeader.setInvoiceRootId(cfSaleInvoiceRoot.getInvoiceRootId());
                    //VBELN  发货通知单 1
                    cfSaleInvoiceHeader.setInvoiceNo(etData.getString("VBELN"));
                    //POSNR  通知单行项目
                    cfSaleInvoiceHeader.setInvoiceItem(etData.getString("POSNR"));

                    //MATNR  物料代码
                    cfSaleInvoiceHeader.setMaterialCode(etData.getString("MATNR"));

                    //厂库
                    cfSaleInvoiceHeader.setWarehouse(etData.getString("LGORT"));

                    CfSaleInvoiceHeader  cfSaleInvoiceHeaderTemp=cfSaleInvoiceHeaderMapper.selectOne(cfSaleInvoiceHeader);

                    //LFIMG  需求数量
                    cfSaleInvoiceHeader.setNeedNumber(new BigDecimal(etData.getString("LFIMG")));

                    //MAKTX  物料名称
                    cfSaleInvoiceHeader.setMaterialName(etData.getString("MAKTX"));
                    //WRKST  规格型号
                    cfSaleInvoiceHeader.setMode(etData.getString("WRKST"));
                    //KUNNR  购货单位
                    cfSaleInvoiceHeader.setPurchaseUnit(etData.getString("NAME1"));
                    //VKBUR  部门
                    cfSaleInvoiceHeader.setDepartment(etData.getString("BEZEI"));
                    //VGBEL  销售订单
                    cfSaleInvoiceHeader.setSaleOrderNo(etData.getString("VGBEL"));
                    //VGPOS  销售行项目
                    cfSaleInvoiceHeader.setSalesItem(etData.getString("VGPOS"));
                    //VGPOS  合同号
                    cfSaleInvoiceHeader.setContractNo(etData.getString("ZHTH"));
                    //VSTEL  分销渠道
                    cfSaleInvoiceHeader.setChannel(etData.getString("VSTEL"));
                    cfSaleInvoiceHeader.setObjectSetBasicAttribute(userId,newDate);
                    //如果已经拉取过数据且表头没有数据
                    if(cfSaleInvoiceHeaderTemp==null){
                        cleanNumber=1;
                        cfSaleInvoiceHeader.setScanningNumber(new BigDecimal(0));
                        cfSaleInvoiceHeader.setObjectVersionNumber(objectVersionNumberNew);
                        this.insert(cfSaleInvoiceHeader);
                    }else{
                        cfSaleInvoiceHeader.setInvoiceId(cfSaleInvoiceHeaderTemp.getInvoiceId());
                        cfSaleInvoiceHeader.setInvoiceRootId(cfSaleInvoiceHeaderTemp.getInvoiceRootId());
                        cfSaleInvoiceHeader.setObjectVersionNumber(objectVersionNumberNew);
                        this.updateById(cfSaleInvoiceHeader);
                    }
            }
        //删除老版本头数据
        this.delete(new EntityWrapper<CfSaleInvoiceHeader>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,cfSaleInvoiceRoot.getInvoiceRootId())
                .addFilter(CfSaleInvoiceHeader.OBJECT_VERSION_NUMBER+" !="+cfSaleInvoiceRoot.getObjectVersionNumber()));

            //通过单据头获取汇总数据
            Page<CfSaleInvoiceHeader> headerPage=new Page<>( current,  size,  CfSaleInvoiceHeader.INVOICE_ID_SQL);
            Page  cfSaleInvoiceHeaderPage=this.selectPage(headerPage, new EntityWrapper<CfSaleInvoiceHeader>().
                    eq(CfSaleInvoiceHeader.INVOICE_ROOT_ID_SQL,cfSaleInvoiceRoot.getInvoiceRootId()));

        //获取已扫描数据
        cfSaleInvoiceLinePage=new Page<>( current,  size,  CfSaleInvoiceHeader.INVOICE_ID_SQL);
        // cfSaleInvoiceLin
        RowBounds rowBounds =new RowBounds( 0,  size);
        List<CfSaleInvoiceLine> cfSaleInvoiceLineList=cfSaleInvoiceLineMapper.selectPage(rowBounds, new EntityWrapper<CfSaleInvoiceLine>().
                eq(CfSaleInvoiceLine.INVOICE_ROOT_ID_SQL,cfSaleInvoiceRoot.getInvoiceRootId()).orderBy(CfSaleInvoiceLine.INVOICE_LINE_ID_SQL,true));

        cfSaleInvoiceLinePage.setRecords(cfSaleInvoiceLineList);

            resultMap.put("cfSaleInvoiceRoot",cfSaleInvoiceRoot);
            resultMap.put("cfSaleInvoiceHeaderPage",cfSaleInvoiceHeaderPage);
            resultMap.put("cfSaleInvoiceLinePage",cfSaleInvoiceLinePage);
        return resultMap;
    }
}
