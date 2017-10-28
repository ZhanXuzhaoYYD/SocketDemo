package com.yyd.socketdemo;

import java.io.Closeable;
import java.io.IOException;

/**
 * <pre>
 *     author : ZhanXuzhao
 *     e-mail : zhanxuzhao@yydrobot.com
 *     time   : 2017/10/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CloseUtils {
    public static void closeIO(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
