package com.cfmoto.bar.code.utiles;

import cn.hutool.core.util.StrUtil;
import com.github.pig.common.util.R;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 条码工具类
 *
 * @author ye
 */
public class BarcodeUtils {

    public final static String BARCODE_ERROR_MSG = "请输入有效的条码进行解析！！！";

    public final static String STAGE_CODE_ERROR_MSG = "条码不正确或打开的应用界面不正确";

    public final static String DIFFERENT_STOCK_LIST_NO_ERROR_MSG = "条码对应的备料单不符，请重置！";

    public final static String INVALID_STOCK_LIST_NO_ERROR_MSG = "条码无效，请注意";

    public final static String ALREDY_HANDOVER_ERROR_MSG = "该条码已经完成交接，请注意";

    public final static String NUMBER_INCORRECT = "条码异常，数量大于未清数量，请注意。";

    public final static String INVENTORY_BARCODE_CODE = "1";

    public final static String MATERIALS_BARCODE_CODE = "2";
    //不可用状态码
    public final static String UNAVAILABLE_STATE_CODE = "N";

    public final static String PREFIX = "ZT";

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");


    /**
     * 条码拆分
     *
     * @param barcode
     * @return Map<String, Object>
     * @author ye
     */
    public static Map<String, Object> splitBarcode(String barcode) {

        Map<String, Object> paramMap = new HashMap<>();

        //解析条码，并将其中的数据分配给不同的变量
        String[] split = barcode.split("%");

        paramMap.put("prefix", split[0]);
        paramMap.put("stockListNo", split[1]);
        paramMap.put("stageCode", split[2]);
        paramMap.put("materialsNo", split[3]);
        paramMap.put("batchNo", split[4]);
        paramMap.put("number", split[5]);
        paramMap.put("waterCode", split[6]);

        return paramMap;
    }


    /**
     * 条码拆分后进行格式校验
     * 校验规则：
     * 1.以BL开头
     * 2.数量必须为数值
     *
     * @param paramMap
     * @return R
     * @author ye
     */
    public static Map<String, Object> verifyBarcodeFormatAfterSplit(Map<String, Object> paramMap) {

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);

        Integer number = Integer.parseInt(paramMap.get("number").toString());
        Integer stageCode = Integer.parseInt(paramMap.get("stageCode").toString());


        return result;
    }

    /**
     * 条码拆分前进行格式校验
     * 校验规则：
     * 1.必须有6个'%'作为分隔符
     *
     * @param barcode
     * @return R
     * @author ye
     */
    public static Map<String, Object> verifyBarcodeFormatBeforeSplit(String barcode) {
        Map<String, Object> result = new HashMap<>();
        //初始化result结果对象的code状态码为0
        result.put("code", 0);


        String[] split = barcode.split("%");

        if (split.length != 7) {
            //code为1代表校验失败
            result.put("code", 1);
            result.put("msg", BARCODE_ERROR_MSG);
        }

        return result;
    }


    /**
     * 条码业务校验
     * 校验规则
     * 1.阶段码必须为1
     *
     * @param stageCode
     * @return r
     * @author ye
     */
    public static Map<String, Object> verifyStageCode(Integer stageCode, Integer correctCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        if (!stageCode.equals(correctCode)) {
            result.put("code", 1);
            result.put("msg", STAGE_CODE_ERROR_MSG);
        }
        return result;
    }

    /**
     * 条码解析的备料单与输入框锁定的备料单号是否相同校验（需要相同）
     *
     * @param stockListNo
     * @param inputStockListNo
     * @return r
     * @author ye
     */
    public static Map<String, Object> verifyStockListNosEqulas(String stockListNo, String inputStockListNo) {

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        if (!stockListNo.equals(inputStockListNo)) {
            result.put("code", 1);
            result.put("msg", DIFFERENT_STOCK_LIST_NO_ERROR_MSG);
        }

        return result;
    }

    /**
     * 判断条码中的物料代码是否存在汇总界面中
     *
     * @param materialsNo
     * @param materialsList
     * @return
     */
    public static Map<String, Object> verifyMaterialsIsExist(String materialsNo, List<Map<String, Object>> materialsList) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        boolean flag = false;

        for (Map<String, Object> stringStringMap : materialsList) {
            if (materialsNo.equals(stringStringMap.get("materialsNo").toString())) {
                flag = true;
            }
        }

        if (!flag) {
            result.put("code", 1);
            result.put("msg", INVALID_STOCK_LIST_NO_ERROR_MSG);
        }
        return result;
    }

    /**
     * 校验条码中是否含有*号，没有则报错：条码非部品条码，请注意!
     * 有则解析出物料代码和数量（*号之前为物料条码，*号之后为数量）
     *
     * @param barcode
     * @return r
     */
    public static R<Map<String, Object>> anaylysisAndSplitBarcode(String barcode, R<Map<String, Object>> r) {
        //首先判断条码中是否含有*号且只有一个
        if (!barcode.contains("*") || getSameCharacterCount(barcode) > 1 || barcode.length() < 3) {

            r.setErrorAndErrorMsg("条码非部品条码，请注意!");
            return r;
        } else {//解析出物料代码和数量并返回
            Map<String, Object> returnMap = new HashMap<>();
            String[] split = barcode.split("\\*");
            returnMap.put("materialsNo", split[0]);
            returnMap.put("number", Integer.parseInt(split[1]));
            r.setData(returnMap);
            return r;
        }
    }

    /**
     * 校验条码中是否含有*号，没有则抛出异常：条码非部品条码，请注意!
     * 有则解析出物料代码和数量（*号之前为物料条码，*号之后为数量）
     *
     * @param barcode 条码
     * @return map
     */
    public static Map<String, Object> anaylysisAndSplitBarcodeThrowException(String barcode) throws Exception {
        //首先判断条码中是否含有*号且只有一个
        if (!barcode.contains("*") || getSameCharacterCount(barcode) > 1 || barcode.length() < 3) {
            throw new Exception("条码非部品条码，请注意!");
        } else {//解析出物料代码和数量并返回
            Map<String, Object> returnMap = new HashMap<>(4);
            String[] split = barcode.split("\\*");
            returnMap.put("materialsNo", split[0]);
            returnMap.put("number", Integer.parseInt(split[1]));
            return returnMap;
        }
    }

    /**
     * 获取条码中*的数量
     *
     * @param barcode
     * @return
     */
    public static int getSameCharacterCount(String barcode) {
        int count = 0;
        char[] c = barcode.toCharArray();
        for (int i = 0; i < barcode.length(); i++) {
            if (c[i] == "*".charAt(0)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 生成条码规则：前缀ZT+时间戳17位
     *
     * @return barcode
     */
    public synchronized static String generateOnWayLabelBarcode() throws InterruptedException {
        Date date = new Date();
        Thread.sleep(1);
        return PREFIX + sdf.format(date);
    }


    /**
     * 校验是否为CP开头的条码
     *
     * @param barcode 条码
     * @return true/false
     */
    public static boolean isCpCode(String barcode) {
        char firstChar = barcode.charAt(0);
        char secondChar = barcode.charAt(1);
        return firstChar == 'C' && secondChar == 'P';
    }
}
