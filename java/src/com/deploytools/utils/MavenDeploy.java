package com.deploytools.utils;

import com.deploytools.deploy.ArtifactoryDeploy;
import com.deploytools.deploy.BaseDeploy;
import com.deploytools.deploy.FileDeploy;
import com.deploytools.deploy.NexusDeploy;

import java.io.File;


public class MavenDeploy {


    public void deploy(Property property) {
        long timeStart = System.currentTimeMillis();

        BaseDeploy deploy = null;

        String path = property.getPath();
        if (path != null && !"".equals(path)) {
            File file = new File(path);
            if (file.isFile()) {
                deploy = new FileDeploy();
            }

        }

        String deployName = "Nexus";
        if (deploy == null) {
            switch (property.getDeployType()) {
                case Property.ARTIFACTORY:
                    deploy = new ArtifactoryDeploy();
                    deployName = "Artifactory";
                    break;
                default: {
                    deploy = new NexusDeploy();
                }
            }
        }
        deploy.log(String.format("正在使用%s上传", deployName));

        deploy.configureGradle(property);

        String modelName = property.getModuleName();

        ExecuteResult result;
        if (deploy instanceof FileDeploy) {//文件类型直接上传
            result = deploy.deploy(property);
        } else {
            result = deploy.clean(modelName);
            if (result.isSuccess()) {
                //打包aar
                result = deploy.assembleReleaseAar(modelName);
                if (!result.isSuccess()) {
                    result = deploy.assembleReleaseJar(modelName);
                }
                if (result.isSuccess()) {//上传
                    result = deploy.deploy(property);
                }
            }
        }
        deploy.cleanConfigure(property);


        long time = System.currentTimeMillis() - timeStart;
        if (result != null && result.isSuccess()) {
            deploy.log(String.format("BUILD SUCCESSFUL in %d ms\n", time));
        } else {
            deploy.log(String.format("BUILD FAILED in %d ms\n ", time));
            if (result != null) {
                deploy.log(result.getMsg());
                deploy.log(result.getErrorMsg());
            }


        }
    }


}
