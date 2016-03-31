package com.builtbroken.redcow;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Dark on 8/3/2015.
 * Updated by Kolatra on 3/30/2016.
 */
public class LayerRedWings implements LayerRenderer<AbstractClientPlayer>
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
    public void onPostPlayerRender(RenderPlayerEvent.Post event) {
        if (shouldRenderForPlayer(event.entityPlayer)) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(0.0F, -0.3F, 0.15F);
            Minecraft.getMinecraft().renderEngine.bindTexture(wingTexture);
            float wingAngle = 0;

            Tessellator instance = Tessellator.getInstance();
            WorldRenderer t = instance.getWorldRenderer();

            // Wing A
            GL11.glPushMatrix();
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-wingAngle, 0.0F, 1.0F, 0.0F);
            t.begin(7, DefaultVertexFormats.POSITION_TEX);
            t.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            t.pos(0.0D, 1.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
            t.pos(1.0D, 1.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
            t.pos(1.0D, 0.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
            instance.draw();
            GL11.glPopMatrix();

            // Wing B
            GL11.glPushMatrix();
            GL11.glRotatef(wingAngle, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
            t.begin(7, DefaultVertexFormats.POSITION_TEX);
            t.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            t.pos(0.0D, 1.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
            t.pos(-1.0D, 1.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
            t.pos(-1.0D, 0.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
            instance.draw();
            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {

    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
