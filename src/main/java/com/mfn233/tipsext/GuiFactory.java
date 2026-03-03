package com.mfn233.tipsext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Set;

public class GuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        java.util.List<net.minecraftforge.fml.client.config.IConfigElement> elements = new java.util.ArrayList<>();

        // 添加两个分类作为一级入口
        elements.add(new net.minecraftforge.common.config.ConfigElement(ModConfig.config.getCategory("general")));
        elements.add(new net.minecraftforge.common.config.ConfigElement(ModConfig.config.getCategory("breathing")));

        return new GuiConfig(parentScreen,
                elements,
                TipsExtension.MODID,
                false,
                false,
                net.minecraft.client.resources.I18n.format("tipsext.config.title"));
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }


}