/*
 * Copyright (c) 2015 WIT Software. All rights reserved.
 *
 * WIT Software Confidential and Proprietary information. It is strictly forbidden for 3rd parties to modify, decompile,
 * disassemble, defeat, disable or circumvent any protection mechanism; to sell, license, lease, rent, redistribute or
 * make accessible to any third party, whether for profit or without charge.
 *
 * pandre 2015/02/20
 */
package com.ivoafsilva.floowchallenge.util;

import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Logger to use. Can be easily disabled by setting {@link #sEnabled} to false.
 *
 * @author pandre 2015/02/20
 */
public abstract class L {

    /**
     * Whether or not overall logging is enabled.
     */
    public static boolean sEnabled = true;

    /**
     * The maximum number of chars to log in a single line.
     */
    private static final int MAX_LOG_SIZE = 4000;

    /**
     * Private constructor.
     */
    private L() {
    }

    /**
     * Logs the received text in log level verbose to the console and to the log file.
     *
     * @param tag  The tag to the logger.
     * @param text Text to log.
     * @param args Optional arguments to replace in the received <code>text</code> using String.format
     *             (<code>text</code>, <code>args</code>).
     */
    public static void v(String tag, String text, Object... args) {
        if (!sEnabled) {
            return;
        }
        if (sizeOf(args) > 0) {
            text = String.format(text, args);
        }
        while (text.length() > MAX_LOG_SIZE) {
            String substring = text.substring(0, MAX_LOG_SIZE);
            Log.v(tag, substring);
            text = text.substring(MAX_LOG_SIZE);
        }
        Log.v(tag, text);
    }

    /**
     * Logs the received text in log level debug to the console and to the log file.
     *
     * @param tag  The tag to the logger.
     * @param text Text to log.
     * @param args Optional arguments to replace in the received <code>text</code> using String.format
     *             (<code>text</code>, <code>args</code>).
     */
    public static void d(String tag, String text, Object... args) {
        if (!sEnabled) {
            return;
        }
        if (sizeOf(args) > 0) {
            text = String.format(text, args);
        }
        while (text.length() > MAX_LOG_SIZE) {
            String substring = text.substring(0, MAX_LOG_SIZE);
            Log.d(tag, substring);
            text = text.substring(MAX_LOG_SIZE);
        }
        Log.d(tag, text);
    }

    /**
     * Logs the received text in log level info to the console and to the log file.
     *
     * @param tag  The tag to the logger.
     * @param text Text to log.
     * @param args Optional arguments to replace in the received <code>text</code> using String.format
     *             (<code>text</code>, <code>args</code>).
     */
    public static void i(String tag, String text, Object... args) {
        if (!sEnabled) {
            return;
        }
        if (sizeOf(args) > 0) {
            text = String.format(text, args);
        }
        while (text.length() > MAX_LOG_SIZE) {
            String substring = text.substring(0, MAX_LOG_SIZE);
            Log.i(tag, substring);
            text = text.substring(MAX_LOG_SIZE);
        }
        Log.i(tag, text);
    }

    /**
     * Logs the received text in log level warn to the console and to the log file.
     *
     * @param tag  The tag to the logger.
     * @param text Text to log.
     * @param args Optional arguments to replace in the received <code>text</code> using String.format
     *             (<code>text</code>, <code>args</code>).
     */
    public static void w(String tag, String text, Object... args) {
        if (!sEnabled) {
            return;
        }
        if (sizeOf(args) > 0) {
            text = String.format(text, args);
        }
        while (text.length() > MAX_LOG_SIZE) {
            String substring = text.substring(0, MAX_LOG_SIZE);
            Log.w(tag, substring);
            text = text.substring(MAX_LOG_SIZE);
        }
        Log.w(tag, text);
    }

    /**
     * Logs the received text in log level warn to the console and to the log file.
     *
     * @param tag  The tag to the logger.
     * @param text Text to log.
     * @param args Optional arguments to replace in the received <code>text</code> using String.format
     *             (<code>text</code>, <code>args</code>).
     */
    public static void w(String tag, String text, Throwable t, Object... args) {
        if (!sEnabled) {
            return;
        }
        if (sizeOf(args) > 0) {
            text = String.format(text, args);
        }
        text = text + " " + Log.getStackTraceString(t);
        Log.w(tag, text);
    }

    /**
     * Logs the received text in log level error to the console and to the log file.
     *
     * @param tag  The tag to the logger.
     * @param text Text to log.
     * @param args Optional arguments to replace in the received <code>text</code> using String.format
     *             (<code>text</code>, <code>args</code>).
     */
    public static void e(String tag, String text, Object... args) {
        if (!sEnabled) {
            return;
        }
        if (sizeOf(args) > 0) {
            text = String.format(text, args);
        }
        while (text.length() > MAX_LOG_SIZE) {
            String substring = text.substring(0, MAX_LOG_SIZE);
            Log.e(tag, substring);
            text = text.substring(MAX_LOG_SIZE);
        }
        Log.e(tag, text);
    }

    /**
     * Logs the received text and throwable's stack trace in log level error to the console and to the log file.
     *
     * @param tag       The tag to the logger.
     * @param text      The error text to log.
     * @param throwable The throwable
     * @param args      Optional arguments to replace in the received <code>text</code> using String.format
     *                  (<code>text</code>, <code>args</code>).
     */
    public static void e(String tag, String text, Throwable throwable, Object... args) {
        if (!sEnabled) {
            return;
        }
        if (sizeOf(args) > 0) {
            text = String.format(text, args);
        }
        String trace = null;
        try {
            trace = Log.getStackTraceString(throwable);
        } catch (Exception e) {
            trace = "[no trace found, throwable=" + throwable + "]";
        }
        text = text + " " + trace;
        Log.e(tag, text);
    }

    /**
     * Logs the received {@link InputStream} using the received tag.
     *
     * @param logTag      The tag to use.
     * @param prefix      The prefix to use
     * @param inputStream The {@link InputStream} to log.
     */
    public static void logInputStream(String logTag, String prefix, InputStream inputStream) {
        if (inputStream == null) {
            L.w(logTag, "%s null", prefix);
            return;
        }
        L.v(logTag, prefix);
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);

            char[] readChars = new char[MAX_LOG_SIZE];
            int count;
            while ((count = isr.read(readChars)) != -1) {
                L.v(logTag, new String(readChars, 0, count));
            }
            isr.close();
        } catch (Exception e) {
            L.e(logTag, "%s error", e, prefix);
        }
    }

    /**
     * Gets the length of the received array, or -1 if it is null.
     *
     * @param array The array whose length is to be retrieved.
     * @return The length of the received array, or -1 if it is null.
     */
    private static int sizeOf(Object[] array) {
        if (array == null) {
            return -1;
        }

        return array.length;
    }
}

