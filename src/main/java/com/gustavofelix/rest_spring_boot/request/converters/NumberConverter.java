package com.gustavofelix.rest_spring_boot.request.converters;

import java.io.UnsupportedEncodingException;

public class NumberConverter {

    public static Double convertToDouble(String strNumber) throws Exception{
        if (strNumber == null || strNumber.isEmpty()) throw new UnsupportedEncodingException();

        String number = strNumber.replace(",", ".");
        return Double.parseDouble(number);
    }

    public static boolean isNumeric(String strNumber) {
        if (strNumber == null || strNumber.isEmpty()) return false;

        String number = strNumber.replace(",", ".");
        return number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }
}
