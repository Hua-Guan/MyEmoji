package pri.guanhua.myemoji.utils;

import android.content.Context;
import android.util.TypedValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyUtils {

    public static int px2dp(Context context, float pxVal) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxVal / scale + 0.5f);
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    public static String getMD5(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(bytes);
        byte[] encryption = md5.digest();
        StringBuilder strBuf = new StringBuilder();
        for (int i = 0; i < encryption.length; i++){
            if (Integer.toHexString(0xff & encryption[i]).length() == 1){
                strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
            }else {
                strBuf.append(Integer.toHexString(0xff & encryption[i]));
            }
        }
        return strBuf.toString();
    }

}
