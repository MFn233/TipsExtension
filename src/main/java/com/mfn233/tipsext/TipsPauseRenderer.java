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

/**
 * 该类负责在暂停菜单渲染提示语，并控制提示语的呼吸灯特效。
 */
@Mod.EventBusSubscriber(modid = TipsExtension.MODID, value = Side.CLIENT)
public class TipsPauseRenderer {

    // 存储当前显示的提示语字符串
    private static String currentTip = "";
    // 记录上一次提示语更新的时间戳（毫秒）
    private static long lastChangeTime = 0;

    /**
     * 每刻逻辑更新事件。用于动态修改 Tips Mod 的静态变量以实现视觉特效。
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 我们只在 Tick 结束阶段执行逻辑，确保所有数值计算已经完成
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();

            // 核心逻辑：只有在开启了呼吸效果，且当前玩家处于暂停菜单(ESC)时才计算 Alpha。
            // 这样可以避免加载页面(Loading Screen)因为渲染频率不固定导致的呼吸闪烁问题。
            if (ModConfig.showPauseTips && ModConfig.enableFade && mc.currentScreen instanceof GuiIngameMenu) {
                // 计算振幅：(最大透明度 - 最小透明度) / 2
                float range = (ModConfig.maxAlpha - ModConfig.minAlpha) / 2.0f;
                // 计算偏移量：基准点
                float offset = ModConfig.minAlpha + range;
                double speed = ModConfig.fadeSpeed;

                // 使用正弦函数计算当前透明度：sin(时间 / 速度) * 振幅 + 偏移
                // 结果会平滑地在 minAlpha 和 maxAlpha 之间循环
                float alpha = (float) (Math.sin(System.currentTimeMillis() / speed) * range + offset);

                // 边界安全性检查，确保 alpha 始终在 0.0 到 1.0 之间
                alpha = Math.max(0.0f, Math.min(1.0f, alpha));

                // 将 0.0-1.0 的浮点数转为 0-255 的整数，并左移 24 位准备写入颜色通道
                int alphaInt = (int) (alpha * 255) << 24;

                // 将新的 Alpha 通道与原有的 RGB 颜色合并，直接修改原版 Tips 的静态配置项
                Config.textColor = (Config.textColor & 0x00FFFFFF) | alphaInt;
            } else {
                // 如果当前不在暂停界面，或者关闭了功能，必须强制将透明度恢复为 255 (不透明)。
                // 这样做是为了确保在加载页面时提示语始终可见，且退出菜单时颜色能瞬间重置。
                if ((Config.textColor >> 24 & 0xFF) != 255) {
                    Config.textColor = (Config.textColor & 0x00FFFFFF) | (0xFF << 24);
                }
            }
        }
    }

    /**
     * 当 GUI 界面打开时的监听。
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        // 只要玩家按下 ESC 打开暂停菜单，就立即随机更换一条提示语。
        // 这增强了交互感，让玩家每次打开菜单都有新鲜感。
        if (event.getGui() instanceof GuiIngameMenu) {
            currentTip = TipsAPI.getRandomTip();
            lastChangeTime = System.currentTimeMillis();
        }
    }

    /**
     * 渲染事件监听。在界面绘制完成后，我们在其上方覆盖一层自定义渲染。
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        // 首先检查全局开关是否允许在暂停界面显示
        if (!ModConfig.showPauseTips) {
            return;
        }

        // 仅在暂停菜单界面进行绘制
        if (event.getGui() instanceof GuiIngameMenu) {
            Minecraft mc = Minecraft.getMinecraft();
            // 获取当前的缩放因数，用于正确计算文字在不同窗口大小下的坐标
            ScaledResolution resolution = new ScaledResolution(mc);
            int scaleFactor = resolution.getScaleFactor();

            long currentTime = System.currentTimeMillis();

            // 逻辑：处理提示语的自动轮换
            if (ModConfig.enableAutoRefresh) {
                long interval = ModConfig.refreshInterval * 1000L;
                // 如果当前为空或者达到了设定的刷新间隔，则更换提示语
                if (currentTip.isEmpty() || currentTime - lastChangeTime > interval) {
                    currentTip = TipsAPI.getRandomTip();
                    lastChangeTime = currentTime;
                }
            } else if (currentTip.isEmpty()) {
                // 如果关闭了自动刷新但缓存为空（如刚启动），则进行初始获取
                currentTip = TipsAPI.getRandomTip();
                lastChangeTime = currentTime;
            }

            // 安全检查：如果确实拿不到任何提示语，则跳过渲染逻辑
            if (currentTip == null || currentTip.isEmpty()) return;

            // 读取 Tips Mod 的原始配置值
            int textColor = Config.textColor; // 这里获取的 textColor 已经在 onClientTick 中被我们动态修改了 Alpha
            int titleColor = Config.titleColor;
            int x = Config.xOffset * scaleFactor;
            int y = event.getGui().height - Config.yOffset;

            // 处理标题翻译，如果 .lang 没写则回退为默认的 "Tip"
            String localizedTitle = I18n.hasKey("tips.gui.title") ? I18n.format("tips.gui.title") : "Tip";

            // 绘制标题：加粗显示
            mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + localizedTitle, x, y, titleColor);

            // 设置正文的最大宽度（屏幕宽度的二分之一），防止长文本横穿整个屏幕
            int maxWidth = event.getGui().width / 2;
            int yOffset = mc.fontRenderer.FONT_HEIGHT;

            // 绘制正文：支持自动换行
            mc.fontRenderer.drawSplitString(currentTip, x, y + yOffset + 2, maxWidth, textColor);
        }
    }
}