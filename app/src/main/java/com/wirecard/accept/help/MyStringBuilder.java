package com.wirecard.accept.help;

import android.text.TextUtils;

/**
 * customized string builder
 */
public class MyStringBuilder {

    private StringBuilder sb;

    public MyStringBuilder(StringBuilder sb) {
        this.sb = sb;
    }

    public MyStringBuilder appendWithNextLine(String string) {
        if(!TextUtils.isEmpty(string)) {
            sb.append(string);
            sb.append('\n');
        }
        return this;
    }

    public MyStringBuilder append(String string) {
        sb.append(string);
        return this;
    }

    public MyStringBuilder append(char character) {
        sb.append(character);
        return this;
    }

    public MyStringBuilder appendTwoStringsWithNextLine(String string1, String string2) {
        if(!TextUtils.isEmpty(string1))
            sb.append(string1);
        if(!TextUtils.isEmpty(string2))
            sb.append(string2);
        if(!TextUtils.isEmpty(string1) || !TextUtils.isEmpty(string2))
            sb.append('\n');
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
