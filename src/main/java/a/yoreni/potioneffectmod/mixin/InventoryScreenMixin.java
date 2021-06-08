package a.yoreni.potioneffectmod.mixin;

import a.yoreni.potioneffectmod.DrawStatusEffects;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractInventoryScreen.class)
public class InventoryScreenMixin<T extends ScreenHandler> extends HandledScreen<T>
{
    public InventoryScreenMixin(T handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Inject(method="render", at=@At("RETURN"))
    public void drawBetterStatusEffects(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        DrawStatusEffects dse = new DrawStatusEffects(this);
        dse.renderStatusEffectOverlay(matrices);
    }

    //code below coppied and pasted from https://github.com/HellsingDarge/CompactStatusEffects/blob/master/src/main/java/me/hellsingdarge/compactstatuseffects/mixins/AbstractInventoryScreenMixin.java
    //thx
    @Inject(method = "applyStatusEffectOffset", at = @At(value = "TAIL"))
    public void applyStatusEffectOffset(CallbackInfo ci)
    {
        this.x = (this.width - this.backgroundWidth) / 2;
    }

    @Inject(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;drawStatusEffectBackgrounds(Lnet/minecraft/client/util/math/MatrixStack;IILjava/lang/Iterable;)V"), cancellable = true)
    public void drawBackground(MatrixStack matrixStack, CallbackInfo ci)
    {
        ci.cancel();
    }

    @Inject(method = "drawStatusEffectSprites", at = @At("HEAD"), cancellable = true)
    void drawSprite(MatrixStack matrixStack, int i, int j, Iterable<StatusEffectInstance> iterable, CallbackInfo ci)
    {
        ci.cancel();
    }

    @Inject(method = "drawStatusEffectDescriptions", at = @At("HEAD"), cancellable = true)
    void drawDescriptions(MatrixStack matrixStack, int i, int j, Iterable<StatusEffectInstance> iterable, CallbackInfo ci)
    {
        ci.cancel();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {

    }
}
