package com.deploytools;

import com.deploytools.config.MavenConfig;
import com.deploytools.gui.DeployFrame;
import com.deploytools.utils.ExecutorTools;
import com.deploytools.utils.MavenDeploy;
import com.deploytools.utils.Property;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 上传多线程控制
 */
public class DeployTools {
    private static DeployTools instance;
    private MavenConfig mavenConfig;
    private MavenDeploy mavenDeploy = new MavenDeploy();
    private ILog iLog = new SamleLog();

    public MavenConfig getMavenConfig() {
        if (mavenConfig == null) {
            mavenConfig = new MavenConfig();
        }
        return mavenConfig;
    }

    public void setMavenConfig(MavenConfig mavenConfig) {
        this.mavenConfig = mavenConfig;
    }

    public static DeployTools getInstance() {
        synchronized (DeployTools.class) {
            if (instance == null) {
                instance = new DeployTools();
            }
            return instance;
        }
    }

    public void deploy(Property upgradeProperty) {
        ExecutorTools.getInstance().execute(new DeployRunnable(upgradeProperty));
    }


    class DeployRunnable implements Runnable {
        private Property upgradeProperty;

        public DeployRunnable(Property upgradeProperty) {
            this.upgradeProperty = upgradeProperty;
        }

        @Override
        public void run() {
            mavenDeploy.deploy(upgradeProperty);
        }
    }

    public ILog getiLog() {
        return iLog;
    }

    public void setiLog(ILog iLog) {
        this.iLog = iLog;
    }

    public static interface ILog {
        void log(String logTxt);
    }

    class SamleLog implements ILog {

        @Override
        public void log(String logTxt) {
            System.out.println(logTxt);
        }
    }



}
