package me.cael.capes.mixins;

import me.cael.capes.menu.SelectorMenu;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkinOptionsScreen.class)
public abstract class MixinSkinOptionsScreen extends GameOptionsScreen {

    @Unique
    private static final Identifier CAPE_OPTIONS_ICON_TEXTURE = Identifier.of("capes","icon/cape_options");
    @Unique
    private final SelectorMenu capes$selectorMenu = new SelectorMenu(this, this.gameOptions);

    public MixinSkinOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "addOptions", at = @At("RETURN"))
    public void addOptions(CallbackInfo info) {
        this.addDrawableChild(TextIconButtonWidget.builder(Text.empty(), (buttonWidget) -> this.client.setScreen(capes$selectorMenu), true).dimension(20, 20).texture(CAPE_OPTIONS_ICON_TEXTURE, 16, 16).build()).setPosition(body.getRowLeft() - 25, body.getY() + 4);
    }
}
