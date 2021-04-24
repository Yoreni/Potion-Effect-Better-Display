package a.yoreni.potioneffectmod.mixin;

import com.google.common.collect.ComparisonChain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectMixin
{
    @Shadow public abstract int getDuration();

    @Shadow public abstract boolean isAmbient();

    @Shadow public abstract StatusEffect getEffectType();

    /**
     * @author yoreni
     * @reason an attempt to remove the hardcoded soft limit
     */
    @Overwrite
    public int compareTo(StatusEffectInstance statusEffectInstance) {
        //int i = true;
        int magicNumber = 100000;
        return (this.getDuration() <= magicNumber || statusEffectInstance.getDuration() <= magicNumber)
                && (!this.isAmbient() || !statusEffectInstance.isAmbient())
                    ? ComparisonChain.start().compare(this.isAmbient(), statusEffectInstance.isAmbient()).compare(this.getDuration(), statusEffectInstance.getDuration()).compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor()).result()
                    : ComparisonChain.start().compare(this.isAmbient(), statusEffectInstance.isAmbient()).compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor()).result();
    }

    /**
     * @author yoreni
     * @reason an attempt to remove the hardcoded soft limit
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public boolean isPermanent() {
        return false;
    }


    @Inject(method="<init>(Lnet/minecraft/entity/effect/StatusEffectInstance;)V", at=@At("RETURN"))
    public void addsampletext(StatusEffectInstance statusEffectInstance, CallbackInfo ci)
    {

    }
}
