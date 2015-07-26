package com.builtbroken.coloredchests;

import com.builtbroken.coloredchests.chests.RenderChest;
import com.builtbroken.coloredchests.chests.TileChest;
import cpw.mods.fml.client.registry.ClientRegistry;

/**
 * Created by Dark on 7/25/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        super.init();
        ClientRegistry.bindTileEntitySpecialRenderer(TileChest.class, new RenderChest());
    }
}
