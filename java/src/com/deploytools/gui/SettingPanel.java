package com.deploytools.gui;


import com.deploytools.DeployTools;
import com.deploytools.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

/**
 * 设置面板
 */
public class SettingPanel extends JPanel {
    private JComboBox comboBox;
    private JTextField textField, etFileName, etFileVer;
    private JFileChooser jFileChooser;
    /*模块名：
     *版本号：
     *选择文件
     *发布
     *
     * */

    public SettingPanel(List<String> names) {
        setLayout(new GridLayout(2, 1));

        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("模块名："));

        comboBox = new JComboBox();
        comboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                int i = comboBox.getSelectedIndex() + 1;
                if (textField != null) {
                    textField.setText("");
                }
                String name = (String) comboBox.getSelectedItem();
                if (onSettingListener != null) {
                    onSettingListener.onSelectedName(name);
                }
//                System.out.println("你选中的是第" + i + "项" + ",内容是:" + s);
            }
        });

        for (String name : names) {
            comboBox.addItem(name);
//            System.out.println(name);
        }
        jPanel.add(comboBox);

        jPanel.add(new JLabel("版本号："));
        textField = new JTextField(10);
        jPanel.add(textField);
        JButton jButton = new JButton("发布");
        jButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = (String) comboBox.getSelectedItem();
                String version = textField.getText();
                if (onSettingListener != null) {
                    onSettingListener.onDeploy(name, version, null);
                }
            }
        });
        jPanel.add(jButton);
        add(jPanel);


        JPanel filePanel = new JPanel();
        JButton fileButton = new JButton("选择文件");
        fileButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = DeployTools.getInstance().getMavenConfig().projectPath;
                jFileChooser = new JFileChooser(path);
                int option = jFileChooser.showOpenDialog(SettingPanel.this);
//                System.out.println("option="+option);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    String moduleName = FileUtils.getFileNameNoEx(file.getName());
                    etFileName.setText(moduleName);
                }
            }
        });
        filePanel.add(fileButton);
        filePanel.add(new JLabel("文件模块名："));
        etFileName = new JTextField(15);
        filePanel.add(etFileName);
        filePanel.add(new JLabel("版本号："));
        etFileVer = new JTextField(10);
        filePanel.add(etFileVer);


        JButton jButton2 = new JButton("发布");
        jButton2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = etFileName.getText();
                String version = etFileVer.getText();
                File file = null;
                if (jFileChooser != null) {
                    file = jFileChooser.getSelectedFile();
                    DeployTools.getInstance().getiLog().log("选择文件路径:" + file.getPath());
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

    public interface OnSettingListener {
        void onSelectedName(String name);

        void onDeploy(String name, String version, File file);
    }
}
