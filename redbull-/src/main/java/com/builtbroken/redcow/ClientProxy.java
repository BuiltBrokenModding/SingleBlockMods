package com.builtbroken.redcow;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Dark on 7/25/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        super.init();
        RenderWings.disableExtraUtilitiesSupport = !Loader.isModLoaded("ExtraUtilities");
        MinecraftForge.EVENT_BUS.register(new RenderWings());
    }
}
