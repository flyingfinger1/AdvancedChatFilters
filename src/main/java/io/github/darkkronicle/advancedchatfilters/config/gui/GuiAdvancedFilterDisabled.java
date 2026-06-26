/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.util.Colors;
import io.github.darkkronicle.advancedchatcore.util.StyleFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class GuiAdvancedFilterDisabled extends GuiBase {

    private final List<FormattedCharSequence> warning;

    public GuiAdvancedFilterDisabled(Screen parent) {
        this.title = StringUtils.translate("advancedchatfilters.screen.warning");
        setParent(parent);
        MutableComponent text = Component.literal(StringUtils.translate("advancedchatfilters.warning.advancedfilters"));
        warning = new ArrayList<>();
        Minecraft client = Minecraft.getInstance();
        int width = client.getWindow().getGuiScaledWidth();
        for (Component t :
                StyleFormatter.wrapText(
                        client.font, width - 100, StyleFormatter.formatText(text))) {
            warning.add(t.getVisualOrderText());
        }
    }

    @Override
    public void init() {
        super.init();
        int x = 10;
        int y = 26;

        String backText = ButtonListener.Type.BACK.getDisplayName();
        int backWidth = StringUtils.getStringWidth(backText) + 10;
        ButtonGeneric back = new ButtonGeneric(x + backWidth, y, backWidth, true, backText);
        this.addButton(
                back, new GuiAdvancedFilterDisabled.ButtonListener(ButtonListener.Type.BACK, this));
        x += back.getWidth() + 2;
    }

    public void back() {
        closeGui(true);
    }

    @Override
    protected void drawContents(GuiContext ctx, int mouseX, int mouseY, float partialTicks) {
        super.drawContents(ctx, mouseX, mouseY, partialTicks);
        if (this.mc == null) return;

        int width = this.mc.getWindow().getGuiScaledWidth();
        int y = 100;
        for (FormattedCharSequence warn : warning) {
            ctx.drawCenteredString(this.font,
                    warn,
                    width / 2,
                    y,
                    Colors.getInstance().getColorOrWhite("white").color());
            y += this.font.lineHeight + 2;
        }
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiAdvancedFilterDisabled parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, GuiAdvancedFilterDisabled parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                parent.back();
            }
        }

        public enum Type {
            BACK("back");

            private final String translation;

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            Type(String key) {
                this.translation = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translation);
            }
        }
    }
}
