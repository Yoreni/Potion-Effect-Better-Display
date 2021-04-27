package a.yoreni.potioneffectmod.mixin;

import com.google.common.collect.Ordering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>
{
    protected HandledScreenMixin(Text title)
    {
        super(title);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci)
    {
        StatusEffect effect = getMouseHoverEffect(mouseX, mouseY);
        if(effect != null)
        {
            List<Text> tooltipText = new ArrayList<Text>();
            tooltipText.add(new TranslatableText(effect.getTranslationKey()));
            tooltipText.add(new TranslatableText(effect.getTranslationKey() + ".description"));
            this.renderTooltip(matrices, tooltipText, mouseX, mouseY);
        }
    }

   /**
     *  This method gets which status effect (the ones that weve
     *  drawn in the HUD) the mouse is hovering over.
     * 
     * @param mouseX the x positon of the mouse
     * @param mouseY the y positon of the mouse
     * @return A StatusEffect instance.
     */
    private StatusEffect getMouseHoverEffect(int mouseX, int mouseY)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Collection<StatusEffectInstance> effects = minecraft.player.getStatusEffects();
        final int scaledWidth = minecraft.getWindow().getScaledWidth();

        // getting the order of the status effects on the screen
        ArrayList<StatusEffect> buffs = new ArrayList<StatusEffect>();
        ArrayList<StatusEffect> debuffs = new ArrayList<StatusEffect>();
        for(StatusEffectInstance effectInstance : Ordering.natural().reverse().sortedCopy(effects))
        {
            if(effectInstance.getEffectType().isBeneficial())
            {
                buffs.add(effectInstance.getEffectType());
            }
            else
            {
                debuffs.add(effectInstance.getEffectType());
            }
        }

        //picking which status effect depending on the position of the mouse
        StatusEffect effect = null;
        int index = ((scaledWidth - mouseX) % 32 >= 4)  ? (scaledWidth - mouseX) / 32 : -1;
        if(mouseY >= 2 && mouseY <= 30) // buffs are always positioned in range 2<=y<=30
        {
            if(index != -1 && index < buffs.size())
            {
                effect = buffs.get(index);
            }
        }
        else if(mouseY >= 33 && mouseY < 64) // debuff are always positioned in range 33<=y<=64
        {
            if(index != -1 && index < debuffs.size())
            {
                effect = debuffs.get(index);
            }
        }

        return effect;
    }
}
