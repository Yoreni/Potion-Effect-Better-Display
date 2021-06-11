package a.yoreni.potioneffectmod;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.List;

public class DrawStatusEffects
{
    private DrawableHelper drawableHelper;

    public DrawStatusEffects(DrawableHelper drawableHelper)
    {
        this.drawableHelper = drawableHelper;
    }

    /**
     * This draws the status effects icon on to the screen
     *
     * @param matrices
     */
    public void renderStatusEffectOverlay(MatrixStack matrices)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraft.textRenderer;
        final int scaledWidth = minecraft.getWindow().getScaledWidth();

        Collection<StatusEffectInstance> collection = minecraft.player.getStatusEffects();
        if (!collection.isEmpty())
        {
            RenderSystem.enableBlend();
            int goodEffects = 0;
            int badEffects = 0;
            StatusEffectSpriteManager statusEffectSpriteManager = minecraft.getStatusEffectSpriteManager();

            //these two Runnable Lists are used to draw the Sprites and Text
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
            List<Runnable> textDrawList = Lists.newArrayListWithExpectedSize(collection.size());

            //minecraft.getTextureManager().bindTexture(HandledScreen.BACKGROUND_TEXTURE);
            RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
            float scaleFactor = 1.25f;
            int spacing = 32;

            for(StatusEffectInstance effectInstance : Ordering.natural().reverse().sortedCopy(collection))
            {
                StatusEffect statusEffect = effectInstance.getEffectType();
                if (effectInstance.shouldShowIcon())
                {
                    //We are scaling it the time thingy fits
                    matrices.scale(scaleFactor, scaleFactor, scaleFactor);

                    int xpos = (int) (scaledWidth * (1 / scaleFactor));
                    int ypos = 1;
                    if (minecraft.isDemo())
                    {
                        ypos += 15 * (1 / scaleFactor);
                    }

                    if (statusEffect.isBeneficial())
                    {
                        ++goodEffects;
                        xpos -= spacing * goodEffects * (1 / scaleFactor);
                    }
                    else
                    {
                        ++badEffects;
                        xpos -= spacing * badEffects * (1 / scaleFactor);
                        ypos += spacing * (1 / scaleFactor);
                    }

                    // Drawing the background of the icon
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    if (effectInstance.isAmbient())
                    {
                        drawableHelper.drawTexture(matrices, xpos, ypos, 165, 166, 24, 24);
                    }
                    else
                    {
                        drawableHelper.drawTexture(matrices, xpos, ypos, 141, 166, 24, 24);
                    }

                    // Drawing the effect icon
                    Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
                    int finalK = (int) ((float) xpos * (scaleFactor));
                    int finalL = (int) ((float) ypos * (scaleFactor));
                    float iconAlpha = calcSpriteAlpha(effectInstance);
                    list.add(() ->
                    {
                        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, iconAlpha);
                        drawableHelper.drawSprite(matrices, finalK + (int) (5 * (scaleFactor)), finalL
                                + (int) (4 * (scaleFactor)), drawableHelper.getZOffset(), 18, 18, sprite);
                    });

                    //putting it back again or funny things happen
                    matrices.scale(1f / scaleFactor, 1f / scaleFactor, 1f / scaleFactor);
                    xpos = (int) Math.ceil(xpos * scaleFactor);
                    ypos = (int) Math.ceil(ypos * scaleFactor);
                    final float textScaleFactor = 0.9f;

                    // Drawing the text
                    int finalXpos = (int) (xpos * (1 / textScaleFactor));
                    int finalYpos = (int) ((ypos + 0.5) * (1 / textScaleFactor));
                    textDrawList.add(() ->
                            {
                                matrices.scale(textScaleFactor, textScaleFactor, textScaleFactor);

                                // Drawing the effect duration
                                String text = Potioneffectmod.formatTime(effectInstance.getDuration());
                                textRenderer.drawWithShadow(matrices, text, finalXpos + 4, finalYpos + 21, 0xFFFFFF);

                                //Drawing the amplifier
                                TranslatableText ampText = getAmplifierText(effectInstance.getAmplifier());
                                textRenderer.drawWithShadow(matrices, ampText
                                        , finalXpos + (30 - textRenderer.getWidth(ampText)), finalYpos + 3
                                        , 0xFFFFFF);

                                matrices.scale(1f / textScaleFactor, 1f / textScaleFactor, 1f / textScaleFactor);
                            });
                }
            }
            list.forEach(Runnable::run);
            textDrawList.forEach(Runnable::run);
        }
    }

    /**
     * this calculates the alpha of the effect sprite to warn the player when
     * the effect will run out.
     *
     * @param effect the effect (this method will use its getDuration() and isAmbient() methods)
     * @return A float value between 0-1
     */
    private float calcSpriteAlpha(StatusEffectInstance effect)
    {
        final float secondsOfWarning = 10;

        if (effect.isAmbient() || effect.getDuration() >= secondsOfWarning * 20)
        {
            return 1f;
        }
        else
        {
            float secondsLeft = (float) Math.floor(10 - effect.getDuration() / 20);
            float alpha = MathHelper.clamp((float) effect.getDuration() / secondsOfWarning / 5f * 0.5f, 0f, 0.5f)
                    + MathHelper.cos((float) effect.getDuration() * (float) Math.PI / 5f)
                    * MathHelper.clamp(secondsLeft / secondsOfWarning * 0.25f, 0f, 0.25f);
            return alpha;
        }
    }

    /**
     * This gets the amplifer text from a lang file however it its not
     * there it will show its eqvilant in arabic numerals
     *
     * @param amplifer the number you want to be displayed
     * @return a TranslatableText object containing the text that should be shown to the player
     */
    private TranslatableText getAmplifierText(int amplifer)
    {
        String key = "potion.potency." + amplifer;
        TranslatableText ampText = new TranslatableText(key);
        //if the potions amplifiers havnt been translated we will show it in arabic numerals instead
        if(ampText.getString().equals(key))
        {
            // amplifiers 128 and above is returned as a negative number so we add a number on to it to fix it
            if(amplifer < 0)
            {
                amplifer += 256;
            }
            ampText = new TranslatableText((amplifer + 1) + "");
        }

        return ampText;
    }
}
