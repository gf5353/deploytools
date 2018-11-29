package com.deploytools.plugin.action;

import com.deploytools.gui.DeployFrame;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class DeployAction extends AnAction {

    public void actionPerformed(AnActionEvent event) {
        DeployFrame frame = new DeployFrame(event.getProject().getBasePath());
        frame.setVisible(true);
    }

}