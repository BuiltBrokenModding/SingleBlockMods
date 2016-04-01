package com.builtbroken.redcow;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Dark on 8/3/2015.
 */
public class RenderWings
{
    public static ResourceLocation wingTexture = new ResourceLocation(RedCow.DOMAIN, "textures/models/wings.png");

    public static boolean disableExtraUtilitiesSupport = false;

    private Class angleRingClass;
    private Field curFlyingPlayers;

    public boolean shouldRenderForPlayer(EntityPlayer player)
    {
        if (player != null && player.getEntityWorld() != null)
        {
            //We want to yield way to ExtraUtilities wing renderer so not to have an odd visual
            if (!disableExtraUtilitiesSupport)
            {
                try
                {
                    if (angleRingClass == null)
                        angleRingClass = Class.forName("com.rwtema.extrautils.item.ItemAngelRing");
                    if (curFlyingPlayers == null)
                        curFlyingPlayers = angleRingClass.getField("curFlyingPlayers");

                    return ((Map) curFlyingPlayers.get(null)).containsKey(player.getGameProfile().getName());
                } catch (ClassNotFoundException e)
                {
                    RedCow.LOGGER.error("Failed to locate ExtraUtilities angle ring item class. This is most likely a mod version error and should be reported to the Built Broken Modding Team");
                    e.printStackTrace();
                    disableExtraUtilitiesSupport = true;
                } catch (NoSuchFieldException e)
                {
                    RedCow.LOGGER.error("Failed to locate ExtraUtilities current flying players field. This is most likely a mod version error and should be reported to the Built Broken Modding Team");
                    e.printStackTrace();
                    disableExtraUtilitiesSupport = true;
                } catch (IllegalAccessException e)
                {
                    RedCow.LOGGER.error("Failed to access ExtraUtilities current flying players field. This is most likely a mod version error and should be reported to the Built Broken Modding Team");
                    e.printStackTrace();
                    disableExtraUtilitiesSupport = true;
                }
            }
            return player.getActivePotionEffect(RedCow.potionRedBull) != null;
        }
        return false;
    }

    @SubscribeEvent
    public void onPostRenderPlayer(RenderPlayerEvent.Specials.Post event)
    {
        if (shouldRenderForPlayer(event.entityPlayer))
        {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(0.0F, -0.3F, 0.15F);
            Tessellator tessellator = Tessellator.instance;
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(wingTexture);
            float wingAngle = 0; //TODO have wings move around, ensure per play flap angle. Maybe tie to player movement?


            //Render wing A
            GL11.glPushMatrix();
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-wingAngle, 0.0F, 1.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, 1.0D, 0.0D);
            tessellator.draw();
            GL11.glPopMatrix();

            //Render wing B, inverted texture of wing A
            GL11.glPushMatrix();
            GL11.glRotatef(wingAngle, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(-1.0D, 1.0D, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(-1.0D, 0.0D, 0.0D, 1.0D, 0.0D);
            tessellator.draw();
            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }
    }
}
