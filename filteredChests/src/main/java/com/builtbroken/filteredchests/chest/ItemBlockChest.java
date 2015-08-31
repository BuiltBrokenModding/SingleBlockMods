package com.builtbroken.filteredchests.chest;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockChest extends ItemBlock
{
    public ItemBlockChest(Block p_i45326_1_)
    {
        super(p_i45326_1_);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
}
