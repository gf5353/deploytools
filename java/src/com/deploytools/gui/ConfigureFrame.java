package com.deploytools.gui;

import com.deploytools.DeployTools;
import com.deploytools.cache.LocalCache;
import com.deploytools.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;

/**
 * 发布设置页面
 */
public class ConfigureFrame extends JFrame {
    private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private int windowsWidth = 600;
    private int windowsHeight = 300;
    private Map<String, JTextField> jTextFieldMap = new HashMap<>();
    private JComboBox comboBox;
    private JFileChooser jFileChooser;
    private JPanel configJPanel;

    public ConfigureFrame() throws HeadlessException {
        setTitle("Setting");
        setResizable(true);//禁止缩放
        setAlwaysOnTop(false);//始终处于顶部
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);


        initView();
        initData();
    }

    private JPanel createSettingJPanel(Properties properties, Object key) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, 2));
        JLabel label = new JLabel(key.toString());
        jPanel.add(label);
        JTextField jTextField = new JTextField();
        jTextField.setText(properties.getProperty((String) key));
        jTextFieldMap.put(key.toString(), jTextField);
        jPanel.add(jTextField);
        return jPanel;
    }

    private void initData() {
        LocalCache localCache = LocalCache.getInstance();
        Properties properties = localCache.getProperties();
        if (properties == null) {
            return;
        }

        jTextFieldMap.clear();
        configJPanel.removeAll();

//        Enumeration<?> enu = properties.propertyNames();
        SortedMap sortedMap = new TreeMap(properties);
        Set set = sortedMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            if ("TOOLS".equals(key)) continue;
            JPanel jPanel = createSettingJPanel(properties, key);
            configJPanel.add(jPanel);
        }
        configJPanel.updateUI();
        String tools = properties.getProperty("TOOLS");
        comboBox.setSelectedItem(tools);
    }


    private void initView() {
        JPanel rootJPanel = new JPanel();
        rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));
        setContentPane(rootJPanel);


        //配置信息
        configJPanel = new JPanel();
        configJPanel.setLayout(new BoxLayout(configJPanel, BoxLayout.Y_AXIS));
        rootJPanel.add(configJPanel);


        //发布工具
        JPanel deployToolsPanel = new JPanel();
        deployToolsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        deployToolsPanel.add(new JLabel("tools:"));

        comboBox = new JComboBox();
        comboBox.addItem("Nexus");
        comboBox.addItem("Artifactory");
        comboBox.addItem("Jcenter");

        deployToolsPanel.add(comboBox);
        rootJPanel.add(deployToolsPanel);

        //保存按钮
        JPanel btnJPanel = new JPanel();
        JButton btnSave = new JButton("Save");
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
                    JOptionPane.showMessageDialog(rootJPanel, "Successfully saved");
                    dispose();
                    return;
                }
                JOptionPane.showMessageDialog(rootJPanel, "Save failed");
            }
        });
        btnJPanel.add(btnSave);
        JButton btnImport = new JButton("Import");
        btnImport.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalCache localCache = LocalCache.getInstance();
                if (localCache == null) return;
                String path = localCache.getConfigPath();

                jFileChooser = new JFileChooser(path);
                FileFilter fileFilter = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) return true;
                        if (f.getPath().equals(path)) {
                            return false;
                        }
                        return f.getName().endsWith(".properties");  //设置为选择以.class为后缀的文件
                    }

                    @Override
                    public String getDescription() {
                        return ".properties";
                    }
                };
                jFileChooser.setFileFilter(fileFilter);

                int option = jFileChooser.showOpenDialog(ConfigureFrame.this);
//                System.out.println("option="+option);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    String filePath = file.getPath();
                    System.out.println("fileName=" + filePath);
                    localCache.importConfig(filePath);
                    initData();
                }
            }
        });
        btnJPanel.add(btnImport);
        rootJPanel.add(btnJPanel);
    }
}
