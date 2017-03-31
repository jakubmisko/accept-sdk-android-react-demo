package com.wirecard.accept.help;

/**
 * Created by jakub.misko on 31. 3. 2017.
 */

public class StringUtils {
    public static String extractNumbers(String str){
        return str.replaceAll("[^0-9.,]+","");
    }
}
