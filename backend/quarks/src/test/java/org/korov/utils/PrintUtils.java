package org.korov.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * @author zhu.lei
 * @date 2022-11-25 17:32
 */
public class PrintUtils {
    public static <T> Matcher<T> print() {
        return new BaseMatcher<T>() {
            @Override
            public boolean matches(Object actual) {
                System.out.println(actual);
                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
