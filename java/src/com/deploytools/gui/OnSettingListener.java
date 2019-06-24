package com.deploytools.gui;

import java.io.File;

public interface OnSettingListener {

    void onDeploy(String name, String version, File file);
}
