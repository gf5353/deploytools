package com.deploytools.deploy;

import com.deploytools.DeployTools;
import com.deploytools.config.ConfigCreator;
import com.deploytools.config.MavenConfig;
import com.deploytools.utils.DeviceTools;
import com.deploytools.utils.ExecuteResult;
import com.deploytools.utils.FileUtils;
import com.deploytools.utils.Property;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public abstract class BaseDeploy {
    /**
     * clean model
     *
     * @param modelName
     * @return
     */
    public ExecuteResult clean(String modelName) {
        String clean = ":%s:clean";
        ExecuteResult executeResult = execute(String.format(clean, modelName));
        return executeResult;
    }

    public ExecuteResult assembleReleaseAar(String modelName) {
        String assembleReleaseAar = ":%s:assembleRelease";
        ExecuteResult executeResult = execute(String.format(assembleReleaseAar, modelName));
        return executeResult;
    }

    public ExecuteResult assembleReleaseJar(String modelName) {
        String assembleReleaseJar = ":%s:jar";
        ExecuteResult executeResult = execute(String.format(assembleReleaseJar, modelName));
        return executeResult;
    }

    /**
     * 备份文件(当前文件夹.文件名备份)
     *
     * @param file
     */
    private void backupFile(File file) {
        File newFile = new File(file.getParent() + "/." + file.getName());
        FileUtils.copy(file, newFile);
    }

    /**
     * 删除备份
     *
     * @param file 原文件
     * @return
     */
    private boolean delBackupFile(File file) {
        if (!file.exists()) return false;
        File tempFile = new File(file.getParent() + "/." + file.getName());
        if (!tempFile.exists()) {
            log("备份文件不存在");
            return false;
        }
        FileUtils.copy(tempFile, file);
        tempFile.delete();
        return true;
    }

    /**
     * 创建根目录build.gradle并备份
     *
     * @param property
     * @param gradlePath
     * @return
     */
    protected boolean createBuildGradle(Property property, String gradlePath) {
        MavenConfig config = property.getMavenConfig();
        if (config == null) return false;
        String projectPath = config.projectPath;
        File gradleFile = new File(projectPath + "/build.gradle");
        if (!gradleFile.isFile()) {
            //非文件
            return false;
        }
        //读取配置
        String gradleText = null;
        try {
            gradleText = FileUtils.readTxtRes(gradlePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gradleText == null) return false;
        log("读取配置文件：" + gradlePath);
        //备份build.gradle
        backupFile(gradleFile);
        //追加配置内容
        FileUtils.appendFileTxt(gradleFile, gradleText);
        return true;
    }

    /**
     * 还原build.gradle并删除备份
     *
     * @param property
     * @return
     */
    protected boolean delBuildGradle(Property property) {
        MavenConfig config = property.getMavenConfig();
        if (config == null) return false;
        String projectPath = config.projectPath;
        File gradleFile = new File(projectPath + "/build.gradle");
        delBackupFile(gradleFile);
        return true;
    }

    /**
     * 创建发布模块build.gradle并备份
     *
     * @param property
     * @param gradlePath
     * @return
     */
    protected boolean createModuleBuildGradle(Property property, String gradlePath) {
        MavenConfig config = property.getMavenConfig();
        if (config == null) return false;

        String path = property.getPath();
        if (path == null || "".equals(path)) {//无设置文件地址则自己拼接
            path = config.projectPath + "/" + property.getModuleName() + "/build.gradle";
        }
        File file = new File(path);
        if (!file.exists()) return false;

        //读取配置
        String gradleText = null;
        try {
            gradleText = ConfigCreator.createPushGradle(property, gradlePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gradleText == null) return false;
        log("读取配置文件：" + gradlePath);
        //备份build.gradle
        backupFile(file);
        //追加配置内容
        FileUtils.appendFileTxt(file, gradleText);
        return true;
    }

    /**
     * 还原发布模块build.gradle并删除备份
     *
     * @param property
     * @return
     */
    protected boolean delModuleBuildGradle(Property property) {
        MavenConfig config = property.getMavenConfig();
        if (config == null) return false;

        String path = property.getPath();
        if (path == null || "".equals(path)) {//无设置文件地址则自己拼接
            path = config.projectPath + "/" + property.getModuleName() + "/build.gradle";
        }

        File file = new File(path);
        delBackupFile(file);
        return true;
    }


    public ExecuteResult execute(String command) {
        try {
            log(command);
            Process process = null;
            String cd = getCD();
            if (DeviceTools.isMac()) {
                String cmds[] = new String[]{DeviceTools.getCommand(), DeviceTools.getC(), cd + DeviceTools.getPlatformWithGradle() + command};
                process = Runtime.getRuntime().exec(cmds);

            } else {
                String cmd = DeviceTools.getCommand() + " " + DeviceTools.getC() + cd + DeviceTools.getPlatformWithGradle() + command;
                process = Runtime.getRuntime().exec(cmd);
            }

            String msg = readStream(process.getInputStream());
            String error = readStream(process.getErrorStream());
            int result = process.waitFor();//0 成功
            return new ExecuteResult(result, msg, error);
        } catch (Exception e) {
            e.printStackTrace();
            return new ExecuteResult(1, "", e.getMessage());
        }
    }

    private String getCD() {
        String os = System.getProperty("os.name");
        String projectPath = DeployTools.getInstance().getMavenConfig().projectPath;
        if (os.toLowerCase().startsWith("win")) {
            return String.format("cd /d %s && ", projectPath);
        }
        return String.format("cd %s && ", projectPath);
    }

    private String readStream(InputStream inputStream) {
        try {
            StringBuffer sb = new StringBuffer();
            InputStreamReader ir = new InputStreamReader(inputStream);
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
//                if (executeListener != null) {
//                    executeListener.onExecute(line);
//                }
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    public void log(String log) {
        DeployTools.getInstance().getiLog().log(log);
    }

    /**
     * 执行上传aar
     *
     * @param property
     * @return
     */
    public abstract ExecuteResult deploy(Property property);

    /**
     * 配置gradle
     *
     * @param property
     * @return
     */
    public abstract boolean configureGradle(Property property);

    /**
     * 清除配置
     *
     * @param property
     * @return
     */
    public abstract boolean cleanConfigure(Property property);
}
