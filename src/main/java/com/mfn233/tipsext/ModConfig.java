package com.mfn233.tipsext;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfig {
    public static Configuration config;

    // 定义分类名称常量
    public static String CAT_GENERAL = "general";
    public static String CAT_BREATHING = "breathing";

    public static boolean enableAutoRefresh;
    public static int refreshInterval;
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
        // 常规分类
        ConfigCategory catGeneral = config.getCategory("general");
        catGeneral.setLanguageKey("tipsext.config.category.general");

        // 呼吸效果分类
        ConfigCategory catBreathing = config.getCategory("breathing");
        catBreathing.setLanguageKey("tipsext.config.category.breathing");

        // --- 常规 ---
        enableAutoRefresh = config.getBoolean("enableAutoRefresh", CAT_GENERAL, true,
                "Whether to enable periodic tip refreshing.", "tipsext.config.enableAutoRefresh");

        refreshInterval = config.getInt("refreshInterval", CAT_GENERAL, 5, 1, 3600,
                "Time between tip changes.", "tipsext.config.refreshInterval");

        // --- 呼吸效果 ---
        enableFade = config.getBoolean("enableFade", CAT_BREATHING, true,
                "Enable breathing effect.", "tipsext.config.enableFade");

        fadeSpeed = config.getFloat("fadeSpeed", CAT_BREATHING, 800.0f, 100.0f, 5000.0f,
                "Higher values = Slower breathing.", "tipsext.config.fadeSpeed");

        minAlpha = config.getFloat("minAlpha", CAT_BREATHING, 0.4f, 0.0f, 1.0f,
                "Minimum transparency.", "tipsext.config.minAlpha");

        maxAlpha = config.getFloat("maxAlpha", CAT_BREATHING, 1.0f, 0.0f, 1.0f,
                "Maximum transparency.", "tipsext.config.maxAlpha");

        if (config.hasChanged()) {
            config.save();
        }
    }
}