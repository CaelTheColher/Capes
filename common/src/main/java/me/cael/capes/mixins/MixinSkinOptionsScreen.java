package me.cael.capes.mixins;

import me.cael.capes.menu.SelectorMenu;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SkinOptionsScreen.class)
public class MixinSkinOptionsScreen extends GameOptionsScreen {

    @Unique
    private static final Identifier CAPE_OPTIONS_ICON_TEXTURE = Identifier.of("capes","icon/cape_options");
    @Unique
    private final SelectorMenu selectorMenu = new SelectorMenu(this, this.gameOptions);

    public MixinSkinOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Override
    public void addOptions() {
        List<ClickableWidget> list = new ArrayList<>();
        PlayerModelPart[] var2 = PlayerModelPart.values();

        for (PlayerModelPart playerModelPart : var2) {
            list.add(CyclingButtonWidget.onOffBuilder(this.gameOptions.isPlayerModelPartEnabled(playerModelPart)).build(playerModelPart.getOptionName(), (cyclingButtonWidget, boolean_) -> {
                this.gameOptions.togglePlayerModelPart(playerModelPart, boolean_);
            }));
        }

        list.add(this.gameOptions.getMainArm().createWidget(this.gameOptions));
        this.body.addAll(list);
        this.addDrawableChild(TextIconButtonWidget.builder(Text.empty(), (buttonWidget) -> this.client.setScreen(selectorMenu), true).dimension(20, 20).texture(CAPE_OPTIONS_ICON_TEXTURE, 16, 16).build()).setPosition(this.width / 2 - 179, this.height / 6);

    }
}
