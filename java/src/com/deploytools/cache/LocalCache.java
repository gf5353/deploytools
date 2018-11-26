package com.deploytools.cache;

import com.deploytools.utils.FileUtils;

import java.io.*;
import java.util.Properties;

public class LocalCache {
    private static LocalCache localCache;
    private String PATH_CACHE = ".deploytools";
    private String FILE_CONFIG = "config.properties";
    private Properties properties;

    public static LocalCache getInstance() {
        synchronized (LocalCache.class) {
            if (localCache == null) {
                localCache = new LocalCache();
            }
            return localCache;
        }
    }

    private LocalCache() {
        String path = System.getProperties().getProperty("user.home") + "/" + PATH_CACHE + "/" + FILE_CONFIG;
        File file = new File(path);
        try {
            if (!file.isFile()) {
                //创建配置文件
                String config = "URL =\n" +
                        "USER =\n" +
                        "PASSWORD =\n" +
                        "GROUPID =\n" +
                        "PACKAGING =\n" +
                        "REPOKEY =\n" +
                        "TOOLS=Nexus\n";
                FileUtils.writFile(file, config);
            }
            properties = new Properties();
            try {
                // 通过输入缓冲流进行读取配置文件
                InputStream InputStream = new BufferedInputStream(new FileInputStream(file));
                // 加载输入流
                properties.load(InputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void put(String key, String value) {
        properties.put(key, value);
    }

    public Properties getProperties() {
        return properties;
    }

    public void save() {
        if (properties != null) {
            String path = System.getProperties().getProperty("user.home") + "/" + PATH_CACHE + "/" + FILE_CONFIG;
            File file = new File(path);
            try {
                FileOutputStream oFile = new FileOutputStream(file, false);
                properties.store(oFile, "Comment");
                oFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
