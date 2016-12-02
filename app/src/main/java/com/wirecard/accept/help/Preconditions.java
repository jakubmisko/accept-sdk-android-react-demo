package com.wirecard.accept.help;

/**
 * Created by super on 28.11.2016.
 */

public class Preconditions {
    public static void nullCheck(Object o, String msg){
        if(o == null){
            throw new NullPointerException(msg);
        }
    }
}
