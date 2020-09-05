package me.cael.capes.mixins;

import com.mojang.brigadier.Message;
import me.cael.capes.CapeMenu;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.screen.options.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkinOptionsScreen.class)
public class MixinSkinOptionsScreen extends GameOptionsScreen {

    public MixinSkinOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void init(CallbackInfo info) {
        this.addButton(new ButtonWidget(this.width / 2 - 179, this.height / 6, 20, 20, Text.of(""), (buttonWidget) -> {
            this.client.openScreen(new CapeMenu(this, this.gameOptions));
        }));
    }
}
