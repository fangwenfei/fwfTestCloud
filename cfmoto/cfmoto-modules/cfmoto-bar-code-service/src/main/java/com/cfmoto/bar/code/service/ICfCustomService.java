package com.cfmoto.bar.code.service;

import com.cfmoto.bar.code.model.vo.CostCenterPrintInVo;
import com.cfmoto.bar.code.model.vo.CostCenterPrintOutVo;
import com.cfmoto.bar.code.model.vo.EnginePutVo;
import com.cfmoto.bar.code.model.vo.ProductionTaskVo;
import com.github.pig.common.util.R;

public interface ICfCustomService {

    /**
     *
     * @param userId
     * @param enginePutVo
     * @return
     * @throws Exception
     */
    ProductionTaskVo productionPutStorage(int userId, EnginePutVo enginePutVo ) throws Exception;

    /**
     *
     * @param costCenterPrintInVo
     * @param userId
     * @return
     * @throws Exception
     */
    CostCenterPrintOutVo costCenterPrint(CostCenterPrintInVo costCenterPrintInVo, int userId) throws Exception;

    ProductionTaskVo getTaskNo(String taskNo) throws Exception;
}
