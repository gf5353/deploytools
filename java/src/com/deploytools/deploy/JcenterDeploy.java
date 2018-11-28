package com.deploytools.deploy;

import com.deploytools.config.ConfigCreator;
import com.deploytools.utils.ExecuteResult;
import com.deploytools.utils.Property;

/**
 * 国内需要翻墙,上传太慢
 */
public class JcenterDeploy extends BaseDeploy {
    @Override
    public ExecuteResult deploy(Property property) {
        String publish = ":%s:bintrayUpload";
        ExecuteResult executeResult = execute(String.format(publish, property.getModuleName()));
        return executeResult;
    }

    @Override
    public boolean configureGradle(Property property) {
        boolean isSuccess = createBuildGradle(property, ConfigCreator.getResourcePath()
                + "jcenter/build.gradle");

        if (isSuccess) {
            isSuccess = createModuleBuildGradle(property, ConfigCreator.getResourcePath()
                    + "jcenter/push.gradle");
        }
        return isSuccess;
    }

    @Override
    public boolean cleanConfigure(Property property) {
        boolean isSuccess = delBuildGradle(property);
        if (isSuccess) {
            isSuccess = delModuleBuildGradle(property);
        }
        return isSuccess;
    }
}
