package com.github.pig.common.util;

public class StringUtils {

    public static String genHandle( String ref, String site, String... keys ) {
        return String.format( "%s:%s,%s", ref, site, String.join(",", keys ) );
    }
}
