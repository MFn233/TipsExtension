package com.mfn233.tipsext;

import net.darkhax.tips.TipsAPI;
import net.darkhax.tips.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 在暂停菜单（ESC）渲染提示语，并提供可配置的呼吸灯效果。
 *
 * 关于透明度：1.12.2 的 FontRenderer#drawString 会从颜色参数的高 8 位读取 alpha
 * （FontRenderer.java 中 this.alpha = (color >> 24 & 255) / 255.0F），
 * 因此本类直接把 alpha 编码进颜色，不再改写 Tips 模组的静态 Config.textColor。
 */
@Mod.EventBusSubscriber(modid = TipsExtension.MODID, value = Side.CLIENT)
public final class TipsPauseRenderer {

    /** 当前显示的提示语 */
    private static String currentTip = "";
    /** 上一次更换提示语的时间戳（纳秒）。用 nanoTime 避免系统时钟被调整时动画跳变 */
    private static long lastChangeNanos = 0L;

    private TipsPauseRenderer() {
    }

    /**
     * 打开暂停菜单时立即换一条提示语，保证每次开菜单都有新鲜感。
     */
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            currentTip = safeRandomTip();
            lastChangeNanos = System.nanoTime();
        }
    }

    /**
     * 在暂停菜单绘制完成后再叠一层自己的提示语。
     */
    @SubscribeEvent
    public static void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!ModConfig.showPauseTips) {
            return;
        }

        GuiScreen gui = event.getGui();
        if (!(gui instanceof GuiIngameMenu)) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.fontRenderer == null) {
            return;
        }

        long now = System.nanoTime();
        long intervalNanos = (long) Math.max(1, ModConfig.refreshInterval) * 1000000000L;
        boolean needNewTip = currentTip.isEmpty()
                || (ModConfig.enableAutoRefresh && (now - lastChangeNanos) >= intervalNanos);
        if (needNewTip) {
            currentTip = safeRandomTip();
            lastChangeNanos = now;
        }
        if (currentTip.isEmpty()) {
            return;
        }

        // 坐标统一使用 scaled 单位（与 Tips 自身的 xOffset / yOffset 语义一致）。
        // 旧实现把 x 额外乘了 scaleFactor 而 y 没有，导致换 GUI 缩放时位置漂移。
        // 位置使用本模组独立配置（pauseX / pauseY / pauseWidthPercent），不再借用 Tips 给加载界面调的 xOffset / yOffset
        int x = Math.max(0, ModConfig.pauseX);
        int widthPercent = Math.max(10, Math.min(100, ModConfig.pauseWidthPercent));
        int maxWidth = Math.max(40, gui.width * widthPercent / 100);
        int lineHeight = mc.fontRenderer.FONT_HEIGHT;
        int bodyHeight = mc.fontRenderer.getWordWrappedHeight(currentTip, maxWidth);
        int blockHeight = lineHeight + 2 + bodyHeight;
        int y = Math.max(0, gui.height - ModConfig.pauseY - blockHeight);

        int alphaChannel = Math.round(currentAlpha() * 255.0F);
        // 注意：alpha 为 0 时 FontRenderer 会强制改回 255（完全不透明），故下限取 1
        alphaChannel = Math.max(1, Math.min(255, alphaChannel));

        int bodyColor = (Config.textColor & 0x00FFFFFF) | (alphaChannel << 24);
        int titleColor = (Config.titleColor & 0x00FFFFFF) | (alphaChannel << 24);

        String localizedTitle = I18n.hasKey("tips.gui.title") ? I18n.format("tips.gui.title") : "Tip";
        mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + localizedTitle, x, y, titleColor);
        mc.fontRenderer.drawSplitString(currentTip, x, y + lineHeight + 2, maxWidth, bodyColor);
    }

    /**
     * 按正弦曲线计算当前透明度。
     * fadeSpeed 的单位是毫秒：数值越大，呼吸越慢（与旧版配置语义保持一致）。
     */
    private static float currentAlpha() {
        if (!ModConfig.enableFade) {
            return 1.0F;
        }

        float min = Math.min(ModConfig.minAlpha, ModConfig.maxAlpha);
        float max = Math.max(ModConfig.minAlpha, ModConfig.maxAlpha);
        float range = (max - min) / 2.0F;
        float offset = min + range;

        double speed = Math.max(1.0D, (double) ModConfig.fadeSpeed);
        double millis = System.nanoTime() / 1000000.0D;
        float alpha = (float) (Math.sin(millis / speed) * (double) range + (double) offset);

        // 下限 0.01：避免取到 0 后被 FontRenderer 反向钳制为完全不透明
        return Math.max(0.01F, Math.min(1.0F, alpha));
    }

    private static String safeRandomTip() {
        try {
            String tip = TipsAPI.getRandomTip();
            return tip == null ? "" : tip;
        } catch (RuntimeException e) {
            return "";
        }
    }
}


