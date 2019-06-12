package com.cfmoto.bar.code.service.boxstickermanagement.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfBarcodeBind;
import com.cfmoto.bar.code.service.ICfBarcodeBindService;
import com.cfmoto.bar.code.service.boxstickermanagement.ICfBoxStickerQueryService;
import com.github.pig.common.util.QueryPage;
import com.xiaoleilu.hutool.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 箱外贴查询业务层接口实现类
 *
 * @author ye
 * @date 2019-04-25
 */
@Service
public class CfBoxStickerQueryServiceImpl implements ICfBoxStickerQueryService {

    @Autowired
    private ICfBarcodeBindService barcodeBindService;

    @Override
    public Page selectPageByFilters(Map<String, Object> params) {
        //根据搜索框输入的内容对 车架、CP码、发动机号、物料代码、车辆类型等字段进行模糊查询

        Wrapper wrapper = wrapParams(params);

        return barcodeBindService.selectPage(new QueryPage<>(params), wrapper);

    }


    @Override
    public Wrapper wrapParams(Map<String, Object> params) {
        String inputKey = (String) params.get("inputKey");
        Wrapper<CfBarcodeBind> wrapper = new EntityWrapper<CfBarcodeBind>().like("frame", inputKey).or()
                .like("car", inputKey).or()
                .like("engine", inputKey).or()
                .like("material_code", inputKey).or()
                .like("car_type", inputKey);
        //根据传递过来的过滤条件进行动态模糊查询
        //时间区间
        List<String> timeRange = (List<String>) params.get("timeRange");
        //打印类型
        String printType = (String) params.get("printType");
        //车辆类型
        String carType = (String) params.get("carType");
        //车架号
        String frame = (String) params.get("frame");
        //发动机号
        String engine = (String) params.get("engine");
        //物料代码
        String materialsNo = (String) params.get("materialsNo");
        //车型
        String carModel = (String) params.get("carModel");
        //生产订单号
        String productionOrder = (String) params.get("productionOrder");
        //整车条码
        String car = (String) params.get("car");

        if (timeRange != null && timeRange.size() == 2) {
            wrapper.and().between("box_label_print_date", timeRange.get(0), timeRange.get(1));
        }

        if (StrUtil.isNotBlank(printType)) {
            wrapper.and().like("print_type", printType);
        }

        if (StrUtil.isNotBlank(carType)) {
            wrapper.and().like("car_type", carType);
        }

        if (StrUtil.isNotBlank(frame)) {
            wrapper.and().like("frame", frame);
        }

        if (StrUtil.isNotBlank(engine)) {
            wrapper.and().like("engine", engine);
        }

        if (StrUtil.isNotBlank(materialsNo)) {
            wrapper.and().like("material_code", materialsNo);
        }

        if (StrUtil.isNotBlank(carModel)) {
            wrapper.and().like("car_model", carModel);
        }

        if (StrUtil.isNotBlank(productionOrder)) {
            wrapper.and().like("production_order", productionOrder);
        }

        if (StrUtil.isNotBlank(car)) {
            wrapper.and().like("car", car);
        }
        return wrapper;
    }

    @Override
    public List<CfBarcodeBind> export(Map<String, Object> params) {
        Wrapper wrapper = wrapParams(params);
        return barcodeBindService.selectList(wrapper);
    }
}
