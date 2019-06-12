package com.cfmoto.bar.code.controller.commonfunction;

import com.cfmoto.bar.code.service.commonfuncton.ICommonExcelImportService;
import com.github.pig.common.util.UserUtils;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yezi
 * @date 2019/5/27
 */
@RestController
@RequestMapping("/commonFunction")
@Log4j
public class CommonExcelImportController {

    @Autowired
    private ICommonExcelImportService commonExcelImportService;

    @RequestMapping("/importExcel")
    public void importExcel(@RequestParam("file") MultipartFile file, @RequestParam("data") String tableName, HttpServletRequest request) {
        try {
            commonExcelImportService.excelImport(file, tableName, UserUtils.getUserId(request));
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
        }
    }

    @RequestMapping("/exportExcel")
    public void exportExcel(@RequestParam("data") String tableName, HttpServletRequest request, HttpServletResponse response) {
        try {
            commonExcelImportService.excelExport(tableName,request,response);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
        }
    }

}