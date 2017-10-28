package com.yyd.socketdemo;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        File f1 = new File("");
        File f2 = f1;

        System.out.println("f1 null: " + (f1 == null));
        System.out.println("f2 null: " + (f2 == null));
        f1 = null;
        System.out.println("f1 null: " + (f1 == null));
        System.out.println("f2 null: " + (f2 == null));

    }
}