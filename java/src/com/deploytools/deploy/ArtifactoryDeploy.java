package com.deploytools.deploy;

import com.deploytools.config.ConfigCreator;
import com.deploytools.utils.ExecuteResult;
import com.deploytools.utils.Property;

/**
 * artifactory上传
 */
public class ArtifactoryDeploy extends BaseDeploy {


    private ExecuteResult publishWithPom(String modelName) {
        String publishWithPom = ":%s:generatePomFileForAarPublication";
        ExecuteResult executeResult = execute(String.format(publishWithPom, modelName));
        return executeResult;
    }

    private ExecuteResult publish(String modelName) {
        String publish = ":%s:artifactoryPublish";
        ExecuteResult executeResult = execute(String.format(publish, modelName));
        return executeResult;
    }

    @Override
    public ExecuteResult deploy(Property property) {
        String modelName = property.getModuleName();
        ExecuteResult result = publishWithPom(modelName);//生成pom文件
        if (result.isSuccess()) {//上传
            result = publish(modelName);
        }
        return result;
    }

    @Override
    public boolean configureGradle(Property property) {
        //更新根目录配置
        boolean isSuccess = createBuildGradle(property, ConfigCreator.getResourcePath()
                + "artifactory/build.gradle");

        if (isSuccess) {
            isSuccess = createModuleBuildGradle(property, ConfigCreator.getResourcePath()
                    + "artifactory/push.gradle");
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
//        return true;
    }
}
