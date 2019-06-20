package com.sofn.sys.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UUIDTool {
    private static Random random = new Random();
    private static final int UUID_LENGTH = 32;
    public static final String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9"};

    public static String getUUID() {
        return getUUID(new Date());
    }

    public static String getTimeStamp() {
        return System.currentTimeMillis()+"";
    }


    public static String getUUID(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String prefix_UUID = sdf.format(date)+"_";
        StringBuffer buffer = new StringBuffer(prefix_UUID);

        int rand_length = UUID_LENGTH - prefix_UUID.length();
        int arr_length = chars.length;
        for (int i = 0; i < rand_length; i++) {
            buffer.append(chars[random.nextInt(arr_length)]);
        }
        return buffer.toString();
    }
}
