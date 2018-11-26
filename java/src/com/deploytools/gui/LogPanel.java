package com.deploytools.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 日志输出面板
 */
public class LogPanel extends JPanel {

    private JTextArea jTextArea;

    public LogPanel() {
        setLayout(new GridLayout(1, 1));
        JScrollPane logScrollPane = new JScrollPane();
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setBackground(null);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);

        logScrollPane.setViewportView(jTextArea);
        add(logScrollPane);
    }

    public void clean() {
        jTextArea.setText("");
    }

    public void append(String log) {
        jTextArea.append("\n");
        jTextArea.append(log);
        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
    }
}
