package com.builtbroken.coloredchests.tests;

import com.builtbroken.coloredchests.ColoredChests;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.awt.*;

/**
 * Created by Dark on 7/28/2015.
 */
@RunWith(VoltzTestRunner.class)
public class ColorWRTest extends AbstractTest
{
    public void testRGB()
    {
        Color color = Color.RED;
        int rgb = ColoredChests.getRGB(color);
        Color color2 = ColoredChests.getColor(rgb);
        Assert.assertTrue("Colors do not match", color.getRGB() == color2.getRGB());
    }

    public void testNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        Color color = Color.RED;
        int rgb = ColoredChests.getRGB(color);
        nbt.setInteger("rgb", rgb);
        int read = nbt.getInteger("rgb");
        Assert.assertTrue("RGB read does not match", rgb == read);
        Assert.assertTrue("RGB does not convert to correct color", ColoredChests.getColor(read).getRGB() == color.getRGB());
    }
}
