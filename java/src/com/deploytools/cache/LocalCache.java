package com.deploytools.cache;

import com.deploytools.DeployTools;
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

    }

    public void loadConfig() {
        String path = System.getProperties().getProperty("user.home") + "/" + PATH_CACHE + "/" + FILE_CONFIG;
        File file = new File(path);
        try {
            if (!file.isFile()) {
                file.getParentFile().mkdirs();
                //创建配置文件
                String config = "URL =\n" +
                        "USER =\n" +
                        "PASSWORD =\n" +
                        "GROUPID =\n" +
                        "PACKAGING =\n" +
                        "REPOKEY =\n" +
                        "TOOLS=Nexus\n";
                FileUtils.writFile(file, config);
                DeployTools.getInstance().getiLog().log("Create a configuration file in the user directory");
            }
            properties = new Properties();
            // 通过输入缓冲流进行读取配置文件
            InputStream InputStream = new BufferedInputStream(new FileInputStream(file));
//                // 加载输入流
            properties.load(InputStream);
        } catch (Exception e) {
            e.printStackTrace();
            DeployTools.getInstance().getiLog().log(e.getMessage());

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
