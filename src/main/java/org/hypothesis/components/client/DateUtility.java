package org.hypothesis.components.client;

import java.util.Date;

/**
 * @author kamil
 */
public class DateUtility {

    public static long getTimestamp() {
        Date date = new Date();
        return date.getTime();
    }

}
