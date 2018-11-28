package com.deploytools.gui;

import com.deploytools.cache.LocalCache;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigureFrame extends JFrame {
    private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private int windowsWidth = 600;
    private int windowsHeight = 400;
    private Map<String, JTextField> jTextFieldMap = new HashMap<>();
    private JComboBox comboBox;

    public ConfigureFrame() throws HeadlessException {
        setTitle("发布配置");
        setResizable(true);//禁止缩放
        setAlwaysOnTop(false);//始终处于顶部
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);


        initView();
    }

    private void initView() {
        LocalCache localCache = LocalCache.getInstance();
        if (localCache != null) {

            JPanel rootJPanel = new JPanel();
            rootJPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            rootJPanel.setLayout(new GridLayout(3, 1));
            setContentPane(rootJPanel);


            //配置信息
            JPanel configJPanel = new JPanel();
            Properties properties = localCache.getProperties();
            configJPanel.setLayout(new GridLayout(properties.size(), 2));
            configJPanel.setSize(100, 100);

            Enumeration<?> enu = properties.propertyNames();
            jTextFieldMap.clear();
            while (enu.hasMoreElements()) {
                Object key = enu.nextElement();
                if ("TOOLS".equals(key)) continue;
                JLabel label = new JLabel(key.toString());
                label.setBorder(new EmptyBorder(10, 5, 10, 5));
                configJPanel.add(label);
                JTextField jTextField = new JTextField();
//                jTextField.setBorder(new EmptyBorder(5,5,5,5));
                jTextField.setText(properties.getProperty((String) key));
                configJPanel.add(jTextField);
                jTextFieldMap.put(key.toString(), jTextField);
            }
            rootJPanel.add(configJPanel);


            //发布工具
            JPanel deployToolsPanel = new JPanel();
            deployToolsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            deployToolsPanel.add(new JLabel("发布工具:"));
            String tools = properties.getProperty("TOOLS");
            comboBox = new JComboBox();
            comboBox.addItem("Nexus");
            comboBox.addItem("Artifactory");
            comboBox.addItem("Jcenter");
            comboBox.setSelectedItem(tools);

            deployToolsPanel.add(comboBox);
            rootJPanel.add(deployToolsPanel);

            //保存按钮
            JPanel btnJPanel = new JPanel();
            btnJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JButton btnSave = new JButton("保存");
            btnSave.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LocalCache localCache = LocalCache.getInstance();
                    if (localCache != null) {
                        for (String name : jTextFieldMap.keySet()) {
                            JTextField jTextField = jTextFieldMap.get(name);
                            localCache.put(name, jTextField.getText());
                        }
                        String tools = (String) comboBox.getSelectedItem();
                        localCache.put("TOOLS", tools);
                        localCache.save();
                        JOptionPane.showMessageDialog(rootJPanel, "保存成功");
                        dispose();
                        return;
                    }
                    JOptionPane.showMessageDialog(rootJPanel, "保存失败");

                }
            });
            btnJPanel.add(btnSave);
            rootJPanel.add(btnJPanel);


        }
    }
}
