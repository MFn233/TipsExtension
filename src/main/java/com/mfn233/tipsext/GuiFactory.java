package com.mfn233.tipsext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 游戏内配置界面的入口。
 *
 * 注意：分类必须在这里显式登记，Forge 不会自动把所有分类都列出来。
 * 为了让以后新增分类不再漏掉，这里用「顺序表 + 兜底遍历」的写法：
 * 先按 CATEGORY_ORDER 指定的顺序添加，再把没登记的剩余分类补在后面。
 */
public class GuiFactory implements IModGuiFactory {

    /** 配置页中分类的显示顺序。新增分类时只需加到这个数组里。 */
    private static final String[] CATEGORY_ORDER = new String[] {
            ModConfig.CAT_GENERAL,
            ModConfig.CAT_PAUSE,
            ModConfig.CAT_BREATHING
    };

    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        if (ModConfig.config == null) {
            // 极端情况下（配置尚未加载）退回到默认界面，避免 NPE
            return new GuiConfig(parentScreen, TipsExtension.MODID, false, false,
                    I18n.format("tipsext.config.title"));
        }

        List<IConfigElement> elements = new ArrayList<>();
        Set<String> added = new LinkedHashSet<>();

        Set<String> available = ModConfig.config.getCategoryNames();

        // 1) 按既定顺序添加
        for (String name : CATEGORY_ORDER) {
            if (available.contains(name) && added.add(name)) {
                ConfigCategory category = ModConfig.config.getCategory(name);
                elements.add(new ConfigElement(category));
            }
        }

        // 2) 兜底：顺序表之外的分类也要显示，防止以后新增分类忘了登记
        for (String name : available) {
            if (added.add(name)) {
                elements.add(new ConfigElement(ModConfig.config.getCategory(name)));
            }
        }

        return new GuiConfig(parentScreen,
                elements,
                TipsExtension.MODID,
                false,
                false,
                I18n.format("tipsext.config.title"));
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
