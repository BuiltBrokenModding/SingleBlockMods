package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Dark on 7/25/2015.
 */
public class RenderWoodenCart extends RenderMinecart
{
    private static final ResourceLocation minecartTextures = new ResourceLocation(WoodenRails.DOMAIN, "textures/entity/minecart.png");

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart p_110775_1_)
    {
        return minecartTextures;
    }
}
