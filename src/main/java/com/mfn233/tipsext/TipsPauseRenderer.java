package com.mfn233.tipsext;

import net.darkhax.tips.TipsAPI;
import net.darkhax.tips.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = TipsExtension.MODID, value = Side.CLIENT)
public class TipsPauseRenderer {

    private static String currentTip = ""; //当前Tip
    private static long lastChangeTime = 0; //上一个Tip显示的时间

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 用动态修改 Tips mod textColor 的方式来增加呼吸灯效果。
        if (ModConfig.enableFade && event.phase == TickEvent.Phase.END) {
            // 使用配置的参数计算呼吸效果
            // 计算范围：将正弦波 (-1 到 1) 映射到玩家定义的 (minAlpha 到 maxAlpha)
            float range = (ModConfig.maxAlpha - ModConfig.minAlpha) / 2.0f;
            float offset = ModConfig.minAlpha + range;

            // 使用 ModConfig.fadeSpeed 控制频率
            double speed = ModConfig.fadeSpeed;
            float alpha = (float) (Math.sin(System.currentTimeMillis() / speed) * range + offset);

            // 限制在有效范围内，防止配置错误导致溢出
            alpha = Math.max(0.0f, Math.min(1.0f, alpha));
            int alphaInt = (int) (alpha * 255) << 24;

            // 修改正文颜色
            Config.textColor = (Config.textColor & 0x00FFFFFF) | alphaInt;
        } else {
            // 检查当前透明度是否不是 255，如果是，则恢复它
            if ((Config.textColor >> 24 & 0xFF) != 255) {
                Config.textColor = (Config.textColor & 0x00FFFFFF) | (0xFF << 24);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        // 当打开的 GUI 是暂停菜单时，强制刷新提示。
        if (event.getGui() instanceof GuiIngameMenu) {
            currentTip = TipsAPI.getRandomTip();
            lastChangeTime = System.currentTimeMillis();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution resolution = new ScaledResolution(mc);
            int scaleFactor = resolution.getScaleFactor(); // 获取界面尺寸因数

            // 获取当前时间
            long currentTime = System.currentTimeMillis();

            // 开启自动刷新时，根据时间间隔切换提示
            if (ModConfig.enableAutoRefresh) {
                long interval = ModConfig.refreshInterval * 1000L;
                if (currentTip.isEmpty() || currentTime - lastChangeTime > interval) {
                    currentTip = TipsAPI.getRandomTip();
                    lastChangeTime = currentTime;
                }
            } else if (currentTip.isEmpty()) {
                // 如果关闭了自动刷新，但当前是空的（比如刚进界面），仍需获取一次
                currentTip = TipsAPI.getRandomTip();
                lastChangeTime = currentTime;
            }

            if (currentTip == null || currentTip.isEmpty()) return;

            // 引用 Tips Mod 的配置
            int textColor = Config.textColor;
            int titleColor = Config.titleColor;
            int x = Config.xOffset * scaleFactor;
            int y = event.getGui().height - Config.yOffset;

            // 渲染提示语标题
            String localizedTitle = I18n.hasKey("tips.gui.title") ? I18n.format("tips.gui.title") : "Tip";
            mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + localizedTitle, x, y, titleColor);

            // 渲染提示语正文
            //int maxWidth = event.getGui().width - x - 10;
            int maxWidth = event.getGui().width / 2;
            int yOffset = mc.fontRenderer.FONT_HEIGHT;

            mc.fontRenderer.drawSplitString(currentTip, x, y + yOffset + 2, maxWidth, textColor);
        }
    }
}