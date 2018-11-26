package com.deploytools.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtls {


    /**
     * 解析setting.gradle中模块名
     *
     * @param path
     * @return
     */
    public static List<String> resolveSettingGradle(String path) throws Exception {
        File file = new File(path);
        if (!file.isFile()) return new ArrayList<>();
        String txt = FileUtils.readTxtFile(path);
        Pattern p = Pattern.compile("\':(.*?)\'");
        Matcher m = p.matcher(txt);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group(1));
        }
        return list;
    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
