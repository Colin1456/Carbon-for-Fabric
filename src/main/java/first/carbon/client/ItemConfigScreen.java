package first.carbon.client;

import first.carbon.Carbon;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

// 1.17.1 无 GuiGraphics 与 Tooltip 组件：改用 PoseStack 渲染与 CycleButton 自带 tooltip 供应器。
public final class ItemConfigScreen extends Screen {
    private final Screen parent;

    public ItemConfigScreen(Screen parent) {
        super(new net.minecraft.network.chat.TranslatableComponent("carbon.configuration.title", "Carbon"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        var settings = java.util.List.of(Carbon.CONFIG.enabled,
                Carbon.CONFIG.optimizeDroppedItems, Carbon.CONFIG.physicalDroppedItems, Carbon.CONFIG.skipVisibilityChecks);
        String[] names = {"enabled", "optimizeDroppedItems", "physicalDroppedItems", "skipVisibilityChecks"};
        for (int i = 0; i < settings.size(); i++) {
            var setting = settings.get(i);
            String key = names[i];
            addRenderableWidget(CycleButton.onOffBuilder(setting.get())
                    .withTooltip(value -> java.util.List.of(new net.minecraft.network.chat.TranslatableComponent("carbon.configuration." + key + ".tooltip").getVisualOrderText()))
                    .create(width / 2 - 100, height / 2 - 48 + i * 24, 200, 20,
                            new net.minecraft.network.chat.TranslatableComponent("carbon.configuration." + key), (button, value) -> {
                                setting.set(value);
                                Carbon.CONFIG.save();
                                Carbon.Client.RENDERER.invalidate();
                                Carbon.Client.DROPPED_RENDERER.invalidate();
                            }));
        }
        addRenderableWidget(new AbstractSliderButton(width / 2 - 100, height / 2 + 48, 200, 20,
                new net.minecraft.network.chat.TranslatableComponent("carbon.configuration.cacheUpdateBudgetMs").copy().append(": " + Carbon.CONFIG.cacheUpdateBudgetMs.get() + " ms"),
                Carbon.CONFIG.cacheUpdateBudgetMs.get() / 20) {
            @Override
            protected void updateMessage() {
                setMessage(new net.minecraft.network.chat.TranslatableComponent("carbon.configuration.cacheUpdateBudgetMs").copy().append(": " + Math.round(value * 200) / 10.0 + " ms"));
            }

            @Override
            protected void applyValue() {
                Carbon.CONFIG.cacheUpdateBudgetMs.set(Math.round(value * 200) / 10.0);
                Carbon.CONFIG.save();
            }
        });
        addRenderableWidget(new Button(width / 2 - 100, height - 40, 200, 20, CommonComponents.GUI_DONE, button -> onClose()));
    }

    @Override
    public void render(com.mojang.blaze3d.vertex.PoseStack pose, int mouseX, int mouseY, float partialTick) {
        renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        drawCenteredString(pose, font, title, width / 2, 30, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}
