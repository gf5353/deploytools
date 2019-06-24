package com.deploytools.gui;


import com.deploytools.DeployTools;
import com.deploytools.utils.FileUtils;
import com.deploytools.utils.StringUtls;

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
    private JTextField textField, nameField;

    public SettingPanel(List<String> names) {
        setLayout(new GridLayout(2, 1));

        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("ModuleName："));

        comboBox = new JComboBox();
        comboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField != null) {
                    textField.setText("");
                }
                String name = (String) comboBox.getSelectedItem();
                if (nameField != null) {
                    nameField.setText(name);
                }
            }
        });

        for (String name : names) {
            comboBox.addItem(name);
        }
        jPanel.add(comboBox);
        nameField = new JTextField(10);
        if (names.size() > 0) {
            nameField.setText(names.get(0));
        }
        jPanel.add(nameField);
        add(jPanel);


        JPanel jPanel2 = new JPanel();
        jPanel2.add(new JLabel("Version："));
        textField = new JTextField(20);
        jPanel2.add(textField);
        JButton jButton = new JButton("deploy");
        jButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String version = textField.getText();
                if (onSettingListener != null) {
                    onSettingListener.onDeploy(name, version, null);
                }
            }
        });
        jPanel2.add(jButton);
        add(jPanel2);


    }

    private OnSettingListener onSettingListener;

    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

}
