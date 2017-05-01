package com.wirecard.accept.help;

import android.text.TextUtils;

/**
 * customized string builder
 */
public class EnhancedStringBuilder {

    private StringBuilder sb;

    public EnhancedStringBuilder(StringBuilder sb) {
        this.sb = sb;
    }

    public EnhancedStringBuilder appendWithNewLine(String string) {
        if(!TextUtils.isEmpty(string)) {
            sb.append(string);
            sb.append('\n');
        }
        return this;
    }

    public EnhancedStringBuilder append(String string) {
        sb.append(string);
        return this;
    }

    public EnhancedStringBuilder append(char character) {
        sb.append(character);
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
