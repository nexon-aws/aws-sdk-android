/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Portions copyright 2006-2009 James Murty. Please see LICENSE.txt
 * for applicable license terms and NOTICE.txt for applicable notices.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Utilities for parsing and formatting dates.
 */
public class DateUtils {
    /**
     * ISO 8601 format
     */
    public static final String ISO8601_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    /**
     * Alternate ISO 8601 format without fractional seconds
     */
    public static final String ALTERNATE_ISO8601_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    /**
     * RFC 822 format
     */
    public static final String RFC822_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    /**
     * This is another ISO 8601 format that's used in clock skew error response
     */
    public static final String COMPRESSED_DATE_PATTERN = "yyyyMMdd'T'HHmmss'Z'";

    private static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

    /**
     * A map to cache date pattern string to SimpleDateFormat object
     */
    private static final Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * A helper function to retrieve a SimpleDateFormat object for the given
     * date pattern
     *
     * @param pattern date pattern
     * @return SimpleDateFormat object
     */
    private static ThreadLocal<SimpleDateFormat> getSimpleDateFormat(final String pattern) {
        ThreadLocal<SimpleDateFormat> sdf = sdfMap.get(pattern);
        if (sdf == null) {
            synchronized (sdfMap) {
                sdf = sdfMap.get(pattern);
                if (sdf == null) {
                    sdf = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
                            sdf.setTimeZone(GMT_TIMEZONE);
                            sdf.setLenient(false);
                            return sdf;
                        }
                    };
                    sdfMap.put(pattern, sdf);
                }
            }
        }
        return sdf;
    }

    /**
     * Parses the specified date string with the given date pattern and returns
     * the Date object.
     *
     * @param pattern date pattern
     * @param dateString The date string to parse.
     * @return The parsed Date object.
     */
    public static Date parse(String pattern, String dateString) {
        try {
            return getSimpleDateFormat(pattern).get().parse(dateString);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    /**
     * Formats the specific date into the given pattern
     *
     * @param pattern date pattern
     * @param date date to be formatted
     * @return formated string representing the give date
     */
    public static String format(String pattern, Date date) {
        return getSimpleDateFormat(pattern).get().format(date);
    }

    /**
     * Parses the specified date string as an ISO 8601 date and returns the Date
     * object.
     *
     * @param dateString The date string to parse.
     * @return The parsed Date object.
     */
    public static Date parseISO8601Date(String dateString) {
        try {
            return parse(ISO8601_DATE_PATTERN, dateString);
        } catch (IllegalArgumentException e) {
            // If the first ISO 8601 parser didn't work, try the alternate
            // version which doesn't include fractional seconds
            return parse(ALTERNATE_ISO8601_DATE_PATTERN, dateString);
        }
    }

    /**
     * Formats the specified date as an ISO 8601 string.
     *
     * @param date The date to format.
     * @return The ISO 8601 string representing the specified date.
     */
    public static String formatISO8601Date(Date date) {
        return format(ISO8601_DATE_PATTERN, date);
    }

    /**
     * Parses the specified date string as an RFC 822 date and returns the Date
     * object.
     *
     * @param dateString The date string to parse.
     * @return The parsed Date object.
     */
    public static Date parseRFC822Date(String dateString) {
        return parse(RFC822_DATE_PATTERN, dateString);
    }

    /**
     * Formats the specified date as an RFC 822 string.
     *
     * @param date The date to format.
     * @return The RFC 822 string representing the specified date.
     */
    public static String formatRFC822Date(Date date) {
        return format(RFC822_DATE_PATTERN, date);
    }

    /**
     * Parses the specified date string as a compressedIso8601DateFormat
     * ("yyyyMMdd'T'HHmmss'Z'") and returns the Date object.
     *
     * @param dateString The date string to parse.
     * @return The parsed Date object.
     */
    public static Date parseCompressedISO8601Date(String dateString) {
        return parse(COMPRESSED_DATE_PATTERN, dateString);
    }
}
