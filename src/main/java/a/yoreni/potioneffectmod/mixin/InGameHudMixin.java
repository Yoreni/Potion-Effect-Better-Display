package a.yoreni.potioneffectmod.mixin;

import a.yoreni.potioneffectmod.DrawStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper
{
    @Shadow private int scaledWidth;

    @Inject(method="render", at=@At("RETURN"))
    public void addsampletext(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        // Happy Aprill Fools
        Date todaysDate = new Date(System.currentTimeMillis());
        if(todaysDate.getMonth() == 4 && todaysDate.getDay() == 1)
        {
            MinecraftClient.getInstance().textRenderer.draw(matrices, "Sample Text!", 0f, 0f, 0xFFFFFF);
        }
    }

    /**
     * This overwrites the vanilia method and draws how it is from my DrawStatusEffects class
     *
     * @author yoreni
     * @reason cos yeah
     */
    @Overwrite
    public void renderStatusEffectOverlay(MatrixStack matrices)
    {
        DrawStatusEffects dse = new DrawStatusEffects(this);
        dse.renderStatusEffectOverlay(matrices);
    }
}
