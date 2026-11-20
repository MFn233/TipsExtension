package com.mfn233.tipsext;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfig {
    public static Configuration config;

    // 定义分类名称常量
    public static String CAT_GENERAL = "general";
    public static String CAT_PAUSE = "pause";
    public static String CAT_BREATHING = "breathing";

    public static boolean showPauseTips;
    public static boolean enableAutoRefresh;
    public static int refreshInterval;

    // 暂停界面提示的独立位置（不再借 Tips 的 xOffset / yOffset）
    public static int pauseX;
    public static int pauseY;
    public static int pauseWidthPercent;

    public static boolean enableFade;
    public static float fadeSpeed;
    public static float minAlpha;
    public static float maxAlpha;

    public static void init(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    public static void syncConfig() {
        // --- 定义分类并设置语言 Key ---
        ConfigCategory catGeneral = config.getCategory("general");
        catGeneral.setLanguageKey("tipsext.config.category.general");

        ConfigCategory catPause = config.getCategory("pause");
        catPause.setLanguageKey("tipsext.config.category.pause");

        ConfigCategory catBreathing = config.getCategory("breathing");
        catBreathing.setLanguageKey("tipsext.config.category.breathing");

        // --- 常规 ---
        showPauseTips = config.getBoolean("01_showPauseTips", CAT_GENERAL, true,
                "Whether to show tips on the pause (ESC) menu.", "tipsext.config.showPauseTips");

        enableAutoRefresh = config.getBoolean("02_enableAutoRefresh", CAT_GENERAL, true,
                "Whether to enable periodic tip refreshing.", "tipsext.config.enableAutoRefresh");

        refreshInterval = config.getInt("03_refreshInterval", CAT_GENERAL, 5, 1, 3600,
                "Time between tip changes.", "tipsext.config.refreshInterval");

        // --- 暂停界面位置（独立于 Tips 的加载界面位置）---
        pauseX = config.getInt("01_pauseX", CAT_PAUSE, 4, 0, 4000,
                "Distance from the left edge of the screen, in GUI-scaled pixels.",
                "tipsext.config.pauseX");

        pauseY = config.getInt("02_pauseY", CAT_PAUSE, 4, 0, 4000,
                "Distance from the bottom edge of the screen, in GUI-scaled pixels. "
                        + "The tip block is bottom-aligned, so the title grows upwards from here.",
                "tipsext.config.pauseY");

        pauseWidthPercent = config.getInt("03_pauseWidthPercent", CAT_PAUSE, 50, 10, 100,
                "Maximum width of the tip body, as a percentage of the screen width.",
                "tipsext.config.pauseWidthPercent");

        // --- 呼吸效果 ---
        enableFade = config.getBoolean("01_enableFade", CAT_BREATHING, true,
                "Enable breathing effect.", "tipsext.config.enableFade");

        fadeSpeed = config.getFloat("02_fadeSpeed", CAT_BREATHING, 800.0f, 100.0f, 5000.0f,
                "Higher values = Slower breathing.", "tipsext.config.fadeSpeed");

        minAlpha = config.getFloat("03_minAlpha", CAT_BREATHING, 0.4f, 0.0f, 1.0f,
                "Minimum transparency.", "tipsext.config.minAlpha");

        maxAlpha = config.getFloat("04_maxAlpha", CAT_BREATHING, 1.0f, 0.0f, 1.0f,
                "Maximum transparency.", "tipsext.config.maxAlpha");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
