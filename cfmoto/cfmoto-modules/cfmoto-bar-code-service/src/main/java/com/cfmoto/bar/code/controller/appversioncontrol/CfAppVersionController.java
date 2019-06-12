package com.cfmoto.bar.code.controller.appversioncontrol;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cfmoto.bar.code.model.entity.CfAppVersionControl;
import com.cfmoto.bar.code.service.appversioncontrol.ICfAppVersionControlService;
import com.github.pig.common.util.QueryPage;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * APP版本控制-前端控制器
 *
 * @author yezi
 * @date 2019/5/29
 */
@RestController
@RequestMapping("/appVersionControl")
@Api(tags = "APP版本控制")
@Log4j
public class CfAppVersionController {

    @Value("${fdfs.file-host}")
    private String fileHost;

    @Autowired
    private ICfAppVersionControlService appVersionControlService;

    /**
     * 引入FastDfs文件上传依赖
     */
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 根据传过来的版本号，与最新的版本号比对是否为最新app版本，来决定是否要更新APP
     *
     * @param appVersionNo APP版本号
     * @param request      请求对象
     * @return R
     * @since 1.0
     */
    @GetMapping("/checkAppVersion")
    public R<CfAppVersionControl> checkAppVersion(@RequestParam("appVersionNo") String appVersionNo, HttpServletRequest request) {
        try {
            return new R<>(appVersionControlService.checkAppVersion(appVersionNo));
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    /**
     * 分页查询APP版本记录信息
     *
     * @param params              分页参数
     * @param cfAppVersionControl 查询条件
     * @return R
     */
    @GetMapping("/getAppVersionsByPage")
    public R<Page> getAppVersionsByPage(@RequestParam Map<String, Object> params, CfAppVersionControl cfAppVersionControl) {
        try {
            EntityWrapper<CfAppVersionControl> wrapper = new EntityWrapper<>();
            return new R<>(appVersionControlService.selectPage(new QueryPage<>(params), wrapper));
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    @PostMapping("/uploadApk")
    public R<String> uploadApk(@RequestParam("file") MultipartFile multipartFile) {
        try {
            //调用FastDfs java客户端上传文件到FastDfs文件服务器上
            System.out.println(multipartFile.getOriginalFilename());
            String[] splitFileName = multipartFile.getOriginalFilename().split("\\.");
            String fileExtName = splitFileName[splitFileName.length - 1];
            StorePath storePath = fastFileStorageClient.uploadFile(multipartFile.getBytes(), fileExtName);
            //获取上传后的文件完整路径
            String fullPath = storePath.getFullPath();
            //返回给前端
            return new R<>(fileHost + fullPath);
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R<>(R.FAIL, e.getMessage());
        }
    }

    @PostMapping("/addNewAppVersion")
    public R addNewAppVersion(@RequestBody CfAppVersionControl appVersionControl, HttpServletRequest request) {
        if (StrUtil.isBlank(appVersionControl.getAppDownloadLink())) {
            return new R(R.FAIL, "请先上传apk!!!");
        }

        try {
            appVersionControlService.addNewAppVersion(appVersionControl, UserUtils.getUserId(request));
            return new R();
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }
    }

    @PostMapping("/setUpToDate")
    public R setUpToDate(@RequestBody CfAppVersionControl appVersionControl, HttpServletRequest request) {
        try {
            appVersionControlService.setUpToDate(appVersionControl,UserUtils.getUserId(request));
            return new R();
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            return new R(R.FAIL, e.getMessage());
        }
    }

}
