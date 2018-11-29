package com.deploytools.utils;

import com.deploytools.config.MavenConfig;

/**
 * maven上传的实例
 */
public class Property {
    private String moduleName;
    private String version;
    private String log = "";
    private String path;//发布路径
    private int deployType = NEXUS;//1:nexus 2:artifactory
    private MavenConfig mavenConfig;

    public final static int NEXUS = 1;
    public final static int ARTIFACTORY = 2;
    public final static int JCENTER = 3;

    public Property(String moduleName, String version) {
        this.moduleName = moduleName;
        this.version = version;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    public String getPath() {
        return path;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public int getDeployType() {
        return deployType;
    }

    public void setDeployType(int deployType) {
        this.deployType = deployType;
    }

    public MavenConfig getMavenConfig() {
        return mavenConfig;
    }

    public void setMavenConfig(MavenConfig mavenConfig) {
        this.mavenConfig = mavenConfig;
    }

    public void setPath(String path) {
        this.path = path;
    }




    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFullVersion() {

        return version;
    }




    public String toString(boolean suffix) {
        return moduleName + "=" + (suffix ? getFullVersion() : version);
    }

    private boolean isEmpty(String value) {
        if (value == null || "".equals(value)) return true;
        return false;
    }
}
