package com.cfmoto.bar.code.service.commonfuncton;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yezi
 * @date 2019/5/27
 */
public interface ICommonExcelImportService {
    /**
     * 根据传入的表名动态导入表中数据到数据库中
     *
     * @param file      excel表文件
     * @param tableName 表名
     * @param userId    用户ID
     * @return Map
     * @throws Exception
     */
    void excelImport(MultipartFile file, String tableName, Integer userId) throws Exception;

    /**
     * 根据传入的表名导出Excel表模板
     *
     * @param tableName 表名
     * @param request   请求对象
     * @param response  响应对象
     * @throws Exception
     */
    void excelExport(String tableName, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
