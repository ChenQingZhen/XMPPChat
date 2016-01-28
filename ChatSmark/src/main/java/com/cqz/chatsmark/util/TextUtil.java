package com.cqz.chatsmark.util;

/**
 * Created by chenqingzhen on 2016/1/27.
 */
public class TextUtil {
    public static String getName(String user){
        return user.substring(0, user.indexOf("@"));
    }
}
