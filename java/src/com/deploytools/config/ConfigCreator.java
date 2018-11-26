package com.deploytools.config;

import com.deploytools.utils.FileUtils;
import com.deploytools.utils.Property;


/**
 * 创建配置文件
 */
public class ConfigCreator {

    public static String getResourcePath() {
        return "resources/";
    }


    /**
     * 获取上传的gradle配置
     *
     * @param property
     * @param gradlePath
     * @return
     */
    public static String createPushGradle(Property property, String gradlePath) throws Exception {
        String path = ConfigCreator.getResourcePath() + "config.gradle";
        String gradlePushTxt = FileUtils.readTxtRes(gradlePath);

        String configTxt = FileUtils.readTxtRes(path);


        MavenConfig config = property.getMavenConfig();
        String url = config.contextUrl;
        String userName = config.userName;
        String password = config.password;
        String groupId = config.groupId;
        String artifactId = property.getModuleName();
        String version = property.getVersion();
        String repoKey = config.keyRelease;

        String gradleTxt = String.format(configTxt, url, userName, password, groupId, artifactId, version, "aar", repoKey);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(gradleTxt)
                .append(gradlePushTxt);

        return stringBuffer.toString();
    }


}
