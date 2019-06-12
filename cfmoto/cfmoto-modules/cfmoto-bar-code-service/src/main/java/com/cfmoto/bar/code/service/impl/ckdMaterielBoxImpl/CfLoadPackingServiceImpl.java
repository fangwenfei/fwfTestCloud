package com.cfmoto.bar.code.service.impl.ckdMaterielBoxImpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cfmoto.bar.code.mapper.CfLoadPackingMapper;
import com.cfmoto.bar.code.model.dto.SapJobOrderTemp;
import com.cfmoto.bar.code.model.dto.SelectList;
import com.cfmoto.bar.code.model.entity.CfLoadPacking;
import com.cfmoto.bar.code.service.ckdMaterielBox.ICfLoadPackingService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cfmoto.bar.code.service.ckdMaterielBox.ISapJobOrderTempService;
import com.github.pig.common.util.exception.ValidateCodeException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author FangWenFei
 * @since 2019-02-26
 */
@Service
public class CfLoadPackingServiceImpl extends ServiceImpl<CfLoadPackingMapper, CfLoadPacking> implements ICfLoadPackingService {

    @Autowired
    CfLoadPackingMapper cfLoadPackingMapper ;

    @Autowired private ISapJobOrderTempService sapJobOrderTempService;

    @Override
    public List<SelectList> selectAllSalesOrderNo() {
        return cfLoadPackingMapper.selectAllSalesOrderNo();
    }

    @Override
    public List<SelectList> selectDocumentNoBySalesOrderNo(String salesOrderNo) {
        return cfLoadPackingMapper.selectDocumentNoBySalesOrderNo(salesOrderNo);
    }

    @Override
   @Transactional(rollbackFor = Exception.class)
    public boolean deleteByCfLoadPacking(CfLoadPacking cfLoadPacking) throws ValidateCodeException {



        EntityWrapper entityWrapper=  new EntityWrapper<CfLoadPacking>();
        entityWrapper.eq(CfLoadPacking.CF_SALES_ORDER_NO_SQL,cfLoadPacking.getSalesOrderNo());
        entityWrapper.eq(CfLoadPacking.CF_DOCUMENT_NO_SQL,cfLoadPacking.getDocumentNo());
        int loadNumber=this.selectCount(entityWrapper.addFilter("load_number>0"));
        if(loadNumber>0){
            throw  new ValidateCodeException(CfLoadPacking.CF_LOAD_NUMBER_EX);
        }


        EntityWrapper entityWrapperDelete=  new EntityWrapper<CfLoadPacking>();
        entityWrapperDelete.eq(CfLoadPacking.CF_SALES_ORDER_NO_SQL,cfLoadPacking.getSalesOrderNo());
        entityWrapperDelete.eq(CfLoadPacking.CF_DOCUMENT_NO_SQL,cfLoadPacking.getDocumentNo());
        this.delete(entityWrapperDelete);

        String[] documentNoList =  cfLoadPacking.getDocumentNo().split("&");

        EntityWrapper sapJobOrderTempEntityWrapper=  new EntityWrapper<SapJobOrderTemp>();
        sapJobOrderTempEntityWrapper.eq(SapJobOrderTemp.SQL_SALES_ORDER_STR,cfLoadPacking.getSalesOrderNo())
        .in(SapJobOrderTemp.SQL_JOB_ORDER_NO_STR,documentNoList);

        sapJobOrderTempService.delete(sapJobOrderTempEntityWrapper);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCfLoadPacking(CfLoadPacking cfLoadPacking) throws Exception {
        String[] documentNoList =  cfLoadPacking.getDocumentNo().split("&");
        EntityWrapper sapJobOrderTempEntityWrapper=  new EntityWrapper<SapJobOrderTemp>();
        sapJobOrderTempEntityWrapper.eq(SapJobOrderTemp.SQL_SALES_ORDER_STR,cfLoadPacking.getSalesOrderNo())
                .eq(SapJobOrderTemp.SQL_JOB_ORDER_NO_STR,documentNoList[0]);

        List<SapJobOrderTemp> sapJobOrderTemplist=sapJobOrderTempService.selectList(sapJobOrderTempEntityWrapper);
        if(sapJobOrderTemplist.size()==0){
            throw  new ValidateCodeException("无法找到对应的车型和国家");
        }
        SapJobOrderTemp sapJobOrderTemp=sapJobOrderTemplist.get(0);
        cfLoadPacking.setCountry(sapJobOrderTemp.getCountry());
        cfLoadPacking.setModel(sapJobOrderTemp.getModel());
        try {
            this.insert(cfLoadPacking);
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
            if(e.getMessage().contains("MySQLIntegrityConstraintViolationException")){
                throw new ValidateCodeException("已经存在相同的物料，请仔细检查");
            }
            throw new ValidateCodeException("新增数据失败，请联系管理员");
        }
        return true;
    }
}
