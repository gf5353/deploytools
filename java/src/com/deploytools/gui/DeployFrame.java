package com.deploytools.gui;


import com.deploytools.DeployTools;
import com.deploytools.cache.LocalCache;
import com.deploytools.config.MavenConfig;
import com.deploytools.utils.Property;
import com.deploytools.utils.StringUtls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DeployFrame extends JFrame {

    private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private int windowsWidth = 600;
    private int windowsHeight = 400;
    private String projectPath;

    public DeployFrame() throws HeadlessException {
        this("");
    }

    public DeployFrame(String projectPath) throws HeadlessException {
        if (StringUtls.isEmpty(projectPath)) {
            File directory = new File("");// 参数为空
            try {
                projectPath = directory.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.projectPath = projectPath;

        MavenConfig mavenConfig = DeployTools.getInstance().getMavenConfig();
        mavenConfig.projectPath = this.projectPath;
        DeployTools.getInstance().setMavenConfig(mavenConfig);

        setTitle("MavenDeploy");
        setResizable(true);//禁止缩放
        setAlwaysOnTop(false);//始终处于顶部
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);
        createMenu();

        loadSetttingGradle();

        DeployTools.getInstance().setiLog(new DeployTools.ILog() {
            @Override
            public void log(String logTxt) {
                System.out.println(logTxt);
                if (logPanel != null) {
                    logPanel.append(logTxt);
                }
            }
        });
    }

    private void createMenu() {
        //创建并添加菜单栏
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
//创建并添加各菜单，注意：菜单的快捷键是同时按下Alt键和字母键，方法setMnemonic('F')是设置快捷键为Alt +Ｆ
        JMenu menuFile = new JMenu("Setting(F)");
        menuFile.setMnemonic('F');
        menuBar.add(menuFile);
        //添加“文件”菜单的各菜单项
        JMenu itemOpen = new JMenu("config");
        itemOpen.setMnemonic('C');
        itemOpen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ConfigureFrame configureFrame = new ConfigureFrame();
                configureFrame.setVisible(true);
            }
        });
        menuFile.add(itemOpen);
        JMenu fileDeployJMenu = new JMenu("fileDeploy");
        fileDeployJMenu.setMnemonic('F');
        fileDeployJMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                FileDeployFrame fileDeployFrame = new FileDeployFrame();
                fileDeployFrame.setOnSettingListener(onDeployClickListener);
                fileDeployFrame.setVisible(true);
            }
        });
        menuFile.add(fileDeployJMenu);
    }

    private LogPanel logPanel;

    /**
     * 加载当前模块
     */
    private void loadSetttingGradle() {
        try {
            List<String> names =
                    StringUtls.resolveSettingGradle(this.projectPath + "/settings.gradle");
            if (names.size() > 0) {
                //创建滚动面板
                //设置面板
                SettingPanel settingPanel = new SettingPanel(names);
                settingPanel.setOnSettingListener(onDeployClickListener);

                //日志面板
                logPanel = new LogPanel();

                JPanel rootPanel = new JPanel();
                rootPanel.setLayout(new GridLayout(2, 1));
                rootPanel.add(settingPanel);
                rootPanel.add(logPanel);
                setContentPane(rootPanel);
            } else {
                JPanel jPanel = new JPanel();
                jPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                String html = String.format("<html><body style=\"text-align:center;\">%s<body></html>", "Non-android project or can't read the settings.gradle file,\n" +
                        "Please check the path<br/>" +
                        "(非android项目或者读取不到settings.gradle文件,请检查路径)<br/>" + this.projectPath);
                jPanel.add(new JLabel(html));
                setContentPane(jPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    OnSettingListener onDeployClickListener = new OnSettingListener() {
        private void toast(String msg) {
            JOptionPane.showMessageDialog(DeployFrame.this, msg);

        }


        @Override
        public void onDeploy(String modelName, String version, File file) {
            if (StringUtls.isEmpty(version)) {
                toast("Upload module name cannot be empty");
            }
            if (StringUtls.isEmpty(version)) {
                toast("Version number cannot be empty");
                return;
            }

            LocalCache localCache = LocalCache.getInstance();
            if (localCache != null) {
                String URL = localCache.getProperty("URL");
                if (StringUtls.isEmpty(URL)) {
                    toast("URL cannot be empty");
                    return;
                }
                String USER = localCache.getProperty("USER");
                if (StringUtls.isEmpty(USER)) {
                    toast("USER can't be empty");
                    return;
                }
                String PASSWORD = localCache.getProperty("PASSWORD");
                if (StringUtls.isEmpty(PASSWORD)) {
                    toast("PASSWORD cannot be empty");
                    return;
                }
                String GROUPID = localCache.getProperty("GROUPID");
                if (StringUtls.isEmpty(GROUPID)) {
                    toast("GROUPID cannot be empty");
                    return;
                }
                String REPOKEY = localCache.getProperty("REPOKEY");
//                if (StringUtls.isEmpty(REPOKEY)) {
//                    toast("REPOKEY cannot be empty");
//                    return;
//                }
                String TOOLS = localCache.getProperty("TOOLS");
//                System.out.println("URL=" + URL + "\nUSER=" + USER + "\nPASSWORD=" + PASSWORD + "\nGROUPID=" + GROUPID + "\nARTIFACTID="
//                        + ARTIFACTID + "\nVERSION=" + VERSION + "\nREPOKEY=" + REPOKEY);

                logPanel.clean();

                String filePath = file != null ? file.getPath() : "";

                MavenConfig mavenConfig = DeployTools.getInstance().getMavenConfig();
                mavenConfig.groupId = GROUPID;


                Property upgradeProperty = new Property(modelName, version);
                upgradeProperty.setPath(filePath);

                int deployType = Property.NEXUS;
                switch (TOOLS) {
                    case "Artifactory":
                        deployType = Property.ARTIFACTORY;
                        break;
                    case "Jcenter":
                        deployType = Property.JCENTER;
                        break;
                }

                upgradeProperty.setDeployType(deployType);
                mavenConfig.userName = USER;
                mavenConfig.password = PASSWORD;
                mavenConfig.contextUrl = URL;
                mavenConfig.keyRelease = REPOKEY;

                DeployTools.getInstance().setMavenConfig(mavenConfig);

                upgradeProperty.setMavenConfig(mavenConfig);

                DeployTools.getInstance().deploy(upgradeProperty);

            }

        }
    };

    public static void main(String[] args) {
        File file = new File("../android");
        String projectPath = "";
        if (file.isDirectory()) {
            projectPath = file.getAbsolutePath();
        }
        DeployFrame frame = new DeployFrame(
                projectPath
        );
        frame.setVisible(true);
    }
}
