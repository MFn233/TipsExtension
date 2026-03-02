package com.mfn233.tipsext;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = TipsExtension.MODID,
        name = TipsExtension.NAME,
        version = TipsExtension.VERSION,
        dependencies = "required-after:tips", // 强制要求在 Tips Mod 之后加载
        clientSideOnly = true, // 这是一个纯客户端 Mod
        guiFactory = "com.mfn233.tipsext.GuiFactory")
public class TipsExtension {

    public static final String MODID = "tipsext";
    public static final String NAME = "Tips Extension";
    public static final String VERSION = "1.0.0";

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Tips Extension 已就绪。");
        ModConfig.init(event);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
        logger.info("Tips Extension 配置已加载，当前刷新间隔: {} 秒", ModConfig.refreshInterval);
    }
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        // 检查发生变化的 ModID 是否是当前 Mod
        if (event.getModID().equals(MODID)) {
            ModConfig.syncConfig(); // 重新从文件读取最新值到内存
            logger.info("配置已更新，新的刷新间隔: {} 秒",ModConfig.refreshInterval);
        }
    }
}
