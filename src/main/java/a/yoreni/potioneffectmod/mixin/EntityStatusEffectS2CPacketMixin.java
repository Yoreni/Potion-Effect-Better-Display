package a.yoreni.potioneffectmod.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityStatusEffectS2CPacket.class)
public abstract class EntityStatusEffectS2CPacketMixin
{
    @Mutable @Final @Shadow private int duration;

    /*
        this removes the limit where the server only wants to send potion effect durations as big as 32767
        idk why its there its like coded in to the game for no reason
     */
    @Inject(method="<init>(ILnet/minecraft/entity/effect/StatusEffectInstance;)V", at=@At("RETURN"))
    public void removeStupidLimit(int entityId, StatusEffectInstance effect, CallbackInfo ci)
    {
        this.duration = effect.getDuration();
    }


}
