package com.deploytools.utils;

public class DeviceTools {

    public static String getPlatformWithGradle() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "/gradlew ";
        }
        return "./gradlew ";
    }

    public static String getC() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "/C ";
        }
        return "-c";
    }

    public static String getCommand() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "cmd";
        }
        return "/bin/sh";
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name");
        return !os.toLowerCase().startsWith("win");
    }
}
