package com.deploytools.gui;

import com.deploytools.DeployTools;
import com.deploytools.cache.LocalCache;
import com.deploytools.utils.FileUtils;
import com.deploytools.utils.StringUtls;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 发布设置页面
 */
public class FileDeployFrame extends JFrame {
    private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private int windowsWidth = 600;
    private int windowsHeight = 200;
    private JTextField etFileName, etFileVer;
    private JFileChooser jFileChooser;

    public FileDeployFrame() throws HeadlessException {
        setTitle("FileDeploy");
        setResizable(true);//禁止缩放
        setAlwaysOnTop(false);//始终处于顶部
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);


        initView();
    }

    private void initView() {
        JPanel filePanel = new JPanel();
        JButton fileButton = new JButton("Select the file");
        fileButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = DeployTools.getInstance().getMavenConfig().projectPath;
                jFileChooser = new JFileChooser(path);
                int option = jFileChooser.showOpenDialog(FileDeployFrame.this);
//                System.out.println("option="+option);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    String moduleName = FileUtils.getFileNameNoEx(file.getName());
                    etFileName.setText(moduleName);
                }
            }
        });
        filePanel.add(fileButton);
        filePanel.add(new JLabel("FileModuleName："));
        etFileName = new JTextField(15);
        filePanel.add(etFileName);
        filePanel.add(new JLabel("Version："));
        etFileVer = new JTextField(10);
        filePanel.add(etFileVer);


        JButton jButton2 = new JButton("deploy");
        jButton2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = etFileName.getText();
                String version = etFileVer.getText();
                File file = null;
                if (jFileChooser != null) {
                    file = jFileChooser.getSelectedFile();
                    if (file == null || StringUtls.isEmpty(file.getPath())) {
                        JOptionPane.showMessageDialog(FileDeployFrame.this, "you need select the file");
                        return;
                    }
                    DeployTools.getInstance().getiLog().log("Select the file Path:" + file.getPath());
                }
                if (onSettingListener != null) {
                    onSettingListener.onDeploy(name, version, file);
                }

            }
        });
        filePanel.add(jButton2);
        add(filePanel);
    }

    private OnSettingListener onSettingListener;

    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }
}
