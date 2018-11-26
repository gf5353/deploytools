package com.deploytools.deploy;

import com.deploytools.utils.ExecuteResult;
import com.deploytools.utils.Property;

/**
 * 国内需要翻墙,上传太慢
 */
public class JcenterDeploy extends BaseDeploy {
    @Override
    public ExecuteResult deploy(Property property) {
        return null;
    }

    @Override
    public boolean configureGradle(Property property) {
        return false;
    }

    @Override
    public boolean cleanConfigure(Property property) {
        return false;
    }
}
