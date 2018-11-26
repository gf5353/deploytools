package com.deploytools.deploy;

import com.deploytools.config.ConfigCreator;
import com.deploytools.config.MavenConfig;
import com.deploytools.utils.ExecuteResult;
import com.deploytools.utils.FileUtils;
import com.deploytools.utils.Property;

import java.io.File;
import java.io.IOException;

public class FileDeploy extends NexusDeploy {


    @Override
    public ExecuteResult deploy(Property property) {
        String path = property.getPath();
        File file = new File(path);
        if (!file.exists()) {
            log("非文件无法上传");
            return new ExecuteResult(1, "", "");
        }
        String moduleName = property.getModuleName();
        if (moduleName == null || "".equals(moduleName)) {
            moduleName = FileUtils.getFileNameNoEx(file.getName());
        }
        String clean = ":%s:uploadArchives";
        ExecuteResult executeResult = execute(String.format(clean, moduleName));
        return executeResult;
    }

    @Override
    public boolean configureGradle(Property property) {
//文件拷贝到根目录新建文件夹创建build.gradle再进行上传arr
        String path = property.getPath();
        File file = new File(path);
        if (!file.exists()) {
            log("非文件无法上传");
            return false;
        }
        MavenConfig config = property.getMavenConfig();
        if (config == null) return false;
        String projectPath = config.projectPath;
        String fileName = file.getName();
        String moduleName = property.getModuleName();
        if (moduleName == null || "".equals(moduleName)) {
            moduleName = FileUtils.getFileNameNoEx(fileName);
        }
        File newFile = new File(projectPath + "/" + moduleName + "/" + fileName);

        newFile.getParentFile().mkdirs();
//        log("拷贝文件" + file.getPath() + "|" + newFile.getPath());
        File gradleFile = new File(newFile.getParent() + "/build.gradle");

        try {
            newFile.createNewFile();
            gradleFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.copy(file, newFile);//上传的文件拷贝到上传的目录
        //备份setting文件
        File settingFile = new File(projectPath + "/settings.gradle");
        if (settingFile.isFile()) {
            File copySettingFile = new File(settingFile.getParent() + "/." + settingFile.getName());
            FileUtils.copy(settingFile, copySettingFile);
            String settingText = String.format("include ':%s'", moduleName);
            FileUtils.appendFileTxt(settingFile, settingText);
        }
        String gradleText = null;
        try {
            gradleText = ConfigCreator.createPushGradle(property, ConfigCreator.getResourcePath()
                    + "nexus/push.gradle");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gradleText == null) return false;

        StringBuffer stringBuffer = new StringBuffer();
        String text = String.format("artifacts {\n" +
                "    archives file('%s')\n" +
                "}\n", file.getName());
        stringBuffer.append(gradleText)
                .append("\n")
                .append(text);
        gradleText = stringBuffer.toString();
//        log(gradleText);
        try {
            FileUtils.writFile(gradleFile, gradleText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean cleanConfigure(Property property) {    //上传完成后把临时文件夹直接删除
        String path = property.getPath();
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        String fileName = file.getName();

        MavenConfig config = property.getMavenConfig();
        if (config == null) return false;
        String projectPath = config.projectPath;


        String moduleName = property.getModuleName();
        if (moduleName == null || "".equals(moduleName)) {
            moduleName = FileUtils.getFileNameNoEx(fileName);
        }

        File newFile = new File(projectPath + "/" + moduleName);

        if (newFile.isFile()) {
            newFile.delete();
        } else {
            FileUtils.delFolder(newFile.getPath());
        }
        File copySettingFile = new File(projectPath + "/.settings.gradle");
        if (copySettingFile.isFile()) {
            File settingFile = new File(projectPath + "/settings.gradle");
            FileUtils.copy(copySettingFile, settingFile);
            copySettingFile.delete();
        }

        return false;
    }
}
