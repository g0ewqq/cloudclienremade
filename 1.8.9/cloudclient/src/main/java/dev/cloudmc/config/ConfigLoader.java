/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.config;

import com.google.gson.Gson;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.option.Option;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.gui.Style;
import dev.cloudmc.helpers.OSHelper;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;

public class ConfigLoader {

    /**
     * Loads the config from .minecraft/cloud/config.json
     */

    public static void loadConfig() throws IOException {
        Config config;
        try (FileReader reader = new FileReader(OSHelper.getCloudDirectory() + "config.json")) {
            config = new Gson().fromJson(reader, Config.class);
        }

        if (config == null || config.getConfig() == null || config.getOptionsConfigList() == null) {
            return;
        }

        int modCount = Math.min(config.getConfig().size(), Cloud.INSTANCE.modManager.getMods().size());
        for (int i = 0; i < modCount; i++) {
            ModConfig modConfig = config.getConfig().get(i);
            if (modConfig == null) {
                continue;
            }
            Mod mod = Cloud.INSTANCE.modManager.getMods().get(i);
            mod.setToggled(modConfig.isToggled());
            if (modConfig.getSettings() == null) {
                continue;
            }
            int settingCount = Math.min(modConfig.getSettings().size(), Cloud.INSTANCE.settingManager.getSettingsByMod(mod).size());
            for (int j = 0; j < settingCount; j++) {
                Setting configSetting = modConfig.getSettings().get(j);
                if (configSetting == null || configSetting.getMode() == null) {
                    continue;
                }
                Setting clientSetting = Cloud.INSTANCE.settingManager.getSettingsByMod(mod).get(j);
                switch (configSetting.getMode()) {
                    case "CheckBox":
                        boolean toggled = configSetting.isCheckToggled();
                        clientSetting.setCheckToggled(toggled);
                        break;
                    case "Slider":
                        float amount = configSetting.getCurrentNumber();
                        clientSetting.setCurrentNumber(amount);
                        break;
                    case "ModePicker":
                        String mode = configSetting.getCurrentMode();
                        int index = configSetting.getModeIndex();
                        clientSetting.setCurrentMode(mode);
                        clientSetting.setModeIndex(index);
                        break;
                    case "ColorPicker":
                        Color color = configSetting.getColor();
                        Color sideColor = configSetting.getSideColor();
                        float sideSlider = configSetting.getSideSlider();
                        float[] mainSlider = configSetting.getMainSlider();
                        clientSetting.setColor(color);
                        clientSetting.setSideColor(sideColor);
                        clientSetting.setSideSlider(sideSlider);
                        clientSetting.setMainSlider(mainSlider);
                        break;
                    case "CellGrid":
                        boolean[][] cells = configSetting.getCells();
                        clientSetting.setCells(cells);
                        break;
                    case "Keybinding":
                        int key = configSetting.getKey();
                        clientSetting.setKey(key);
                        break;
                }
            }

            if (Cloud.INSTANCE.hudEditor.getHudMod(mod.getName()) != null
                    && modConfig.getPositions() != null && modConfig.getPositions().length >= 2) {
                Cloud.INSTANCE.hudEditor.getHudMod(mod.getName()).setX(modConfig.getPositions()[0]);
                Cloud.INSTANCE.hudEditor.getHudMod(mod.getName()).setY(modConfig.getPositions()[1]);
                Cloud.INSTANCE.hudEditor.getHudMod(mod.getName()).setSize(modConfig.getSize());
            }
        }

        int optionCount = Math.min(config.getOptionsConfigList().size(), Cloud.INSTANCE.optionManager.getOptions().size());
        for(int i = 0; i < optionCount; i++){
            Option configOption = config.getOptionsConfigList().get(i);
            if (configOption == null || configOption.getMode() == null) {
                continue;
            }
            Option clientOption = Cloud.INSTANCE.optionManager.getOptions().get(i);
            switch (configOption.getMode()) {
                case "CheckBox":
                    boolean toggled = configOption.isCheckToggled();
                    clientOption.setCheckToggled(toggled);
                    break;
                case "Slider":
                    float amount = configOption.getCurrentNumber();
                    clientOption.setCurrentNumber(amount);
                    break;
                case "ModePicker":
                    String mode = configOption.getCurrentMode();
                    int index = configOption.getModeIndex();
                    clientOption.setCurrentMode(mode);
                    clientOption.setModeIndex(index);
                    break;
                case "ColorPicker":
                    Color color = configOption.getColor();
                    Color sideColor = configOption.getSideColor();
                    float sideSlider = configOption.getSideSlider();
                    float[] mainSlider = configOption.getMainSlider();
                    clientOption.setColor(color);
                    clientOption.setSideColor(sideColor);
                    clientOption.setSideSlider(sideSlider);
                    clientOption.setMainSlider(mainSlider);
                    break;
                case "CellGrid":
                    boolean[][] cells = configOption.getCells();
                    clientOption.setCells(cells);
                    break;
                case "Keybinding":
                    int key = configOption.getKey();
                    clientOption.setKey(key);
                    break;
            }
        }

        Style.setDarkMode(config.isDarkMode());
        Style.setSnapping(config.isSnapping());
    }
}
