package android.text;

import android.support.annotation.NonNull;

/**
 * Created by Lakshmanan on 9/12/18.
 */


public class TextUtils {
    public static String join(@NonNull CharSequence delimiter, @NonNull Object[] tokens) {
        final int length = tokens.length;
        if (length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(tokens[0]);
        for (int i = 1; i < length; i++) {
            sb.append(delimiter);
            sb.append(tokens[i]);
        }
        return sb.toString();
    }
}
