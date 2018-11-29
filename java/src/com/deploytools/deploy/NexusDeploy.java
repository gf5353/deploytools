package com.deploytools.deploy;

import com.deploytools.config.ConfigCreator;
import com.deploytools.utils.ExecuteResult;
import com.deploytools.utils.Property;

public class NexusDeploy extends BaseDeploy {

    public ExecuteResult uploadArchives(String modelName) {
        String clean = ":%s:uploadArchives";
        ExecuteResult executeResult = execute(String.format(clean, modelName));
        return executeResult;
    }

    @Override
    public ExecuteResult deploy(Property property) {
        ExecuteResult uploadArchives = uploadArchives(property.getModuleName());
        return uploadArchives;
    }

    @Override
    public boolean configureGradle(Property property) {
        boolean isSuccess = createModuleBuildGradle(property,
                ConfigCreator.getResourcePath()
                         + "nexus/push.gradle");
        return isSuccess;

    }

    @Override
    public boolean cleanConfigure(Property property) {
        delModuleBuildGradle(property);
        return true;
    }

}
