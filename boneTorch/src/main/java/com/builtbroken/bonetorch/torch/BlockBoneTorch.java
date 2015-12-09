package com.builtbroken.bonetorch.torch;

import com.builtbroken.bonetorch.BoneTorchMod;
import net.minecraft.block.BlockTorch;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/9/2015.
 */
public class BlockBoneTorch extends BlockTorch
{
    public BlockBoneTorch()
    {
        super();
        this.setBlockName(BoneTorchMod.PREFIX + "boneTorch");
        this.setBlockTextureName(BoneTorchMod.PREFIX + "boneTorch");
        this.setHardness(0.0F);
        this.setLightLevel(0.9375F);
        this.setStepSound(soundTypeWood);
    }
}
