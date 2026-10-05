/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import com.mojang.blaze3d.platform.InputConstants;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetIntBox;
import io.github.darkkronicle.advancedchatcore.util.Colors;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import io.github.darkkronicle.advancedchatfilters.scripting.ScriptFilter;
import io.github.darkkronicle.advancedchatfilters.scripting.ScriptManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WidgetAdvancedFilterEntry extends WidgetListEntryBase<ScriptFilter> {

    private final WidgetListAdvancedFilters parent;
    private final boolean isOdd;
    private final int buttonStartX;
    private final ScriptFilter filter;
    private final TextFieldWrapper<WidgetIntBox> num;

    public WidgetAdvancedFilterEntry(
            int x,
            int y,
            int width,
            int height,
            boolean isOdd,
            ScriptFilter filter,
            int listIndex,
            WidgetListAdvancedFilters parent) {
        super(x, y, width, height, filter, listIndex);
        this.parent = parent;
        this.isOdd = isOdd;
        this.filter = filter;

        y += 1;

        int pos = x + width - 2;
        WidgetIntBox num =
                new WidgetIntBox(pos - 40, y, 40, 20, Minecraft.getInstance().font);
        num.setValue(filter.getOrder().toString());
        num.setApply(
                () -> {
                    Integer order = num.getInt();
                    if (order == null) {
                        order = 0;
                    }
                    this.filter.setOrder(order);
                    Collections.sort(ScriptManager.getInstance().getFilters());
                    FiltersHandler.getInstance().loadFilters();
                    this.parent.refreshEntries();
                });
        this.num =
                new TextFieldWrapper<>(
                        num,
                        new ITextFieldListener<WidgetIntBox>() {
                            @Override
                            public boolean onTextChange(WidgetIntBox textField) {
                                return false;
                            }

                            @Override
                            public boolean onGuiClosed(WidgetIntBox textField) {
                                Integer order = num.getInt();
                                if (order == null) {
                                    order = 0;
                                }
                                filter.setOrder(order);
                                Collections.sort(ScriptManager.getInstance().getFilters());
                                return false;
                            }
                        });
        this.parent.addTextField(this.num);
        pos -= num.getWidth() + 2;
        ButtonOnOff active =
                addOnOffButton(
                        pos, y, ButtonListener.Type.ACTIVE, filter.getActive().getBooleanValue());
        pos -= active.getWidth() + 1;
        if (!filter.isImported()) {
            String importName = ButtonListener.Type.IMPORT.getDisplayName();
            int importWidth = StringUtils.getStringWidth(importName) + 2;
            ButtonGeneric importButton = new ButtonGeneric(pos, y, importWidth, true, importName);
            this.addButton(importButton, new ButtonListener(ButtonListener.Type.IMPORT, this));
            active.setEnabled(false);
            num.setEditable(false);
            pos -= importButton.getWidth() + 1;
        }

        buttonStartX = pos;
    }

    private ButtonOnOff addOnOffButton(
            int xRight, int y, ButtonListener.Type type, boolean isCurrentlyOn) {
        ButtonOnOff button = new ButtonOnOff(xRight, y, -1, true, type.translate, isCurrentlyOn);
        this.addButton(button, new ButtonListener(type, this));

        return button;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected) {
        // Draw a lighter background for the hovered and the selected entry
        if (selected || this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(ctx,
                    this.x,
                    this.y,
                    this.width,
                    this.height,
                    Colors.getInstance().getColorOrWhite("listhover").color());
        } else if (this.isOdd) {
            RenderUtils.drawRect(ctx,
                    this.x,
                    this.y,
                    this.width,
                    this.height,
                    Colors.getInstance().getColorOrWhite("list1").color());
        } else {
            RenderUtils.drawRect(ctx,
                    this.x,
                    this.y,
                    this.width,
                    this.height,
                    Colors.getInstance().getColorOrWhite("list2").color());
        }
        String name = this.filter.getDisplayName();
        this.drawString(ctx,
                this.x + 4,
                this.y + 7,
                Colors.getInstance().getColorOrWhite("white").color(),
                name
        );

        this.drawTextFields(ctx, mouseX, mouseY);

        super.render(ctx, mouseX, mouseY, selected);
    }

    private static class ButtonListener implements IButtonActionListener {

        private final Type type;
        private final WidgetAdvancedFilterEntry parent;

        public ButtonListener(Type type, WidgetAdvancedFilterEntry parent) {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (type == Type.ACTIVE) {
                this.parent
                        .filter
                        .getActive()
                        .setBooleanValue(!this.parent.filter.getActive().getBooleanValue());
                FiltersHandler.getInstance().loadFilters();
                parent.parent.refreshEntries();
            } else if (type == Type.IMPORT) {
                FiltersConfigStorage.IMPORTED_FILTERS.add(parent.filter.getId());
                ScriptManager.getInstance().init();
                parent.parent.refreshEntries();
            }
        }

        public enum Type {
            ACTIVE("active"),
            IMPORT("import");

            private final String translate;

            Type(String name) {
                this.translate = translate(name);
            }

            private static String translate(String key) {
                return "advancedchatfilters.config.filtermenu." + key;
            }

            public String getDisplayName() {
                return StringUtils.translate(translate);
            }
        }
    }

    @Override
    protected boolean onKeyTypedImpl(KeyEvent keyEvent) {
        if (this.num != null && this.num.isFocused()) {
            if (keyEvent.input() == InputConstants.KEY_RETURN) {
                this.num.textField().getApply().run();
                return true;
            } else {
                return this.num.onKeyTyped(keyEvent);
            }
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharacterEvent characterEvent) {
        if (this.num != null && this.num.onCharTyped(characterEvent)) {
            return true;
        }

        return super.onCharTypedImpl(characterEvent);
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        if (super.onMouseClickedImpl(mouseButtonEvent, doubleClick)) {
            return true;
        }

        boolean ret = false;

        int mouseX = (int) mouseButtonEvent.x();
        int mouseY = (int) mouseButtonEvent.y();

        if (this.num != null) {
            ret = this.num.textField().mouseClicked(mouseButtonEvent, doubleClick);
        }

        if (!this.subWidgets.isEmpty()) {
            for (WidgetBase widget : this.subWidgets) {
                ret |=
                        widget.isMouseOver(mouseX, mouseY)
                                && widget.onMouseClicked(mouseButtonEvent, doubleClick);
            }
        }

        return ret;
    }

    protected void drawTextFields(GuiContext ctx, int mouseX, int mouseY) {
        if (this.num != null) {
            this.num.draw(ctx, mouseX, mouseY);
        }
    }

    @Override
    public void postRenderHovered(GuiContext ctx, int mouseX, int mouseY, boolean selected) {
        super.postRenderHovered(ctx, mouseX, mouseY, selected);

        List<String> hoverLines;

        if (filter.isImported()) {
            hoverLines = this.filter.getHoverLines();
        } else {
            hoverLines =
                    Arrays.asList(
                            StringUtils.translate(
                                            "advancedchatfilters.config.filtermenu.info.import")
                                    .split("\n"));
        }

        if (hoverLines != null && hoverLines.size() > 0) {
            if (mouseX >= this.x
                    && mouseX < this.buttonStartX
                    && mouseY >= this.y
                    && mouseY <= this.y + this.height) {
                RenderUtils.drawHoverText(ctx, mouseX, mouseY, hoverLines);
            }
        }
    }
}
