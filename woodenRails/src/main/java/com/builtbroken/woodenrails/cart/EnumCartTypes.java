package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.cart.types.*;

/**
 * Created by Cow Pi on 8/11/2015.
 */
public enum EnumCartTypes
{
    EMPTY(EntityEmptyCart.class, "WoodenCart"),
    CHEST(EntityChestCart.class, "ChestCart"),
    WORKTABLE(EntityWorkbenchCart.class, "WorkbenchCart"),
    FURNACE(EntityPoweredCart.class, "PoweredCart"),
    HOPPER(EntityHopperCart.class, "HopperCart"),
    TNT(EntityTNTCart.class, "TNTCart"),
    TANK(EntityTankCart.class, "BCTankCart");

    public final Class<? extends EntityWoodenCart> clazz;
    public final String entityName;

    EnumCartTypes(Class<? extends EntityWoodenCart> clazz, String entityName)
    {
        this.clazz = clazz;
        this.entityName = "wr" + entityName;
    }
}
