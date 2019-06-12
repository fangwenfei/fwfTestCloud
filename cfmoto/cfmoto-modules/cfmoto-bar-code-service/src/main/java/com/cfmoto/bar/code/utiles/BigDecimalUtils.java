package com.cfmoto.bar.code.utiles;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public static BigDecimal numberObjectToBigDecimal(Object number) {
        return new BigDecimal(number.toString());
    }

    public static Integer numberObjectToInteger(Object number) {
        return new BigDecimal(number.toString()).intValue();
    }

}
