package a.yoreni.potioneffectmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.text.TranslatableText;

public class Potioneffectmod implements ModInitializer
{
    @Override
    public void onInitialize()
    {

    }

    /**
     * Converts game ticks in to human readable time format
     * This will be used to display static effect durations
     *
     * @param ticks
     * @return
     */
    public static String formatTime(int ticks)
    {
        // handles negative inputs
        if(ticks < 0)
        {
            return "-" + formatTime(ticks * -1);
        }

        int s = (ticks / 20) % 60;
        int m = (ticks / (20 * 60)) % 60;
        int h = (ticks / (20 * 60 * 60));
        if(h == 0) //mins-secs format
        {
            String text = new TranslatableText("potioneffectmod.time.minsecFormat").getString();
            // we have to replace it cos we dont want the TranslatableText object to handle the % cos it
            // does a bad job at it you cant put extra options such as 0 padding in it but you can with
            // String.format . If we do this this gives the translater more power and can localise the
            // time formatter better.
            text = text.replaceAll("!","%");
            return String.format(text, m, s);
        }
        else if(h < 100) //hrs-mins format
        {
            String text = new TranslatableText("potioneffectmod.time.hrminFormat").getString();
            text = text.replaceAll("!","%");
            return String.format(text, h, m);
        }
        else //we will return infinity if the effect is longer than 100 hours
        {
            return new TranslatableText("potioneffectmod.time.forever").getString();
        }
    }
}
