package com.builtbroken.redcow;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by Dark on 7/25/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        super.init();
        LayerRedWings.disableExtraUtilitiesSupport = !Loader.isModLoaded("ExtraUtilities");
        MinecraftForge.EVENT_BUS.register(new LayerRedWings());
    }
}
