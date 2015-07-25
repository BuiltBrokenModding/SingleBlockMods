package com.builtbroken.woodenrails;

import cpw.mods.fml.client.registry.RenderingRegistry;
import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.RenderWoodenCart;

/**
 * Created by Dark on 7/25/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        super.init();
        if(WoodenRails.itemWoodCart != null)
        {
            RenderingRegistry.registerEntityRenderingHandler(EntityWoodenCart.class, new RenderWoodenCart());
        }
    }
}
