package com.wirecard.accept.uicomponents;

import java.util.Objects;

/**
 * Created by jakub.misko on 27. 2. 2017.
 */

public class Preconditions {
    public static void nullCheck(Object o){
        if(o == null){
            throw new NullPointerException("Field can't be null!");
        }
    }

    public static boolean softNullCheck(Object o){
        return o == null;
    }
}
