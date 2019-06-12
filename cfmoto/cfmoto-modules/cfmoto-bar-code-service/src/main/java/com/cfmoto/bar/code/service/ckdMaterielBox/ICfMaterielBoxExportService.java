package com.cfmoto.bar.code.service.ckdMaterielBox;

import com.baomidou.mybatisplus.service.IService;
import com.cfmoto.bar.code.model.entity.CfMaterielBox;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/4/10.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
public interface ICfMaterielBoxExportService  extends IService<CfMaterielBox> {

    SXSSFWorkbook  export(CfMaterielBox cfMaterielBox);

    Map<String ,Object>  getExportData(CfMaterielBox cfMaterielBox);
}
