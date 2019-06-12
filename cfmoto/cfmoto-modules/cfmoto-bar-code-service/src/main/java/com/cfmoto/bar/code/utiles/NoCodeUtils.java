package com.cfmoto.bar.code.utiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* **********************************************************************
 *              Created by FangWenFei on 2019/2/19.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
public class NoCodeUtils {

    public static String getDateNo(LocalDateTime today) {
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyyMMddHH");
        return today.format(formatter).toString();
    }

    public static String getCodeNoByIdAndLength(int length,int id) {
      String HeaderStr="000000"+id;
      return   HeaderStr.substring(HeaderStr.length()-length);
    }

/*    public static void main(String[] args) {
        System.out.println(getDateNo(12));
    }*/
}
