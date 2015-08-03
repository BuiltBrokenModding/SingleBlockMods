package com.builtbroken.coloredchests.network;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;
import java.util.EnumMap;

import static cpw.mods.fml.relauncher.Side.CLIENT;
import static cpw.mods.fml.relauncher.Side.SERVER;

@Sharable
public class PacketManager
{
    private static final EnumMap<Side, FMLEmbeddedChannel> channels = Maps.newEnumMap(Side.class);

    public static void init()
    {
        PacketHandler handler = new PacketHandler();

        handler.addDiscriminator(0, PacketChest.class);

        channels.putAll(NetworkRegistry.INSTANCE.newChannel("ColoredChests", handler, new HandlerServer()));

        // add handlers
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            // for the client
            FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
            String codecName = channel.findChannelHandlerNameForType(PacketHandler.class);
            channel.pipeline().addAfter(codecName, "ClientHandler", new HandlerClient());
        }
    }

    public static void sendToServer(PacketBase packet)
    {
        channels.get(CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(CLIENT).writeAndFlush(packet);
    }

    public static void sendToPlayer(PacketBase packet, EntityPlayer player)
    {
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(SERVER).writeAndFlush(packet);
    }

    public static void sendToAllAround(PacketBase packet, TileEntity tile)
    {
        sendToAllAround(packet, tile, 64);
    }

    public static void sendToAllAround(PacketBase packet, TileEntity tile, int distance)
    {
        sendToAllAround(packet, new NetworkRegistry.TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, distance));
    }

    public static void sendToAllAround(PacketBase packet, NetworkRegistry.TargetPoint point)
    {
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(SERVER).writeAndFlush(packet);
    }

    public static void sendToDimension(PacketBase packet, int dimension)
    {
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channels.get(SERVER).writeAndFlush(packet);
    }

    public static void sendToAll(PacketBase packet)
    {
        channels.get(SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(SERVER).writeAndFlush(packet);
    }

    public static Packet toMcPacket(PacketBase packet)
    {
        return channels.get(FMLCommonHandler.instance().getEffectiveSide()).generatePacketFrom(packet);
    }

    private static final class PacketHandler extends FMLIndexedMessageToMessageCodec<PacketBase>
    {
        @Override
        public void encodeInto(ChannelHandlerContext ctx, PacketBase packet, ByteBuf target) throws Exception
        {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            packet.encode(output);
            target.writeBytes(output.toByteArray());
        }

        @Override
        public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, PacketBase packet)
        {
            ByteArrayDataInput input = ByteStreams.newDataInput(source.array());
            input.skipBytes(1);
            packet.decode(input);
        }

        @Override
        public FMLIndexedMessageToMessageCodec<PacketBase> addDiscriminator(int discriminator, Class<? extends PacketBase> type)
        {
            if (!checkEmptyConstructor(type))
            {
                LogManager.getLogger().log(Level.FATAL, "Empty constructor is required for packet type " + type.getName() + "!");
            }

            return super.addDiscriminator(discriminator, type);
        }

        @SuppressWarnings("rawtypes")
        private static boolean checkEmptyConstructor(Class type)
        {
            try
            {
                for (Constructor c : type.getConstructors())
                {
                    if (c.getParameterTypes().length == 0)
                    {
                        return true;
                    }
                }
            } catch (SecurityException e)
            {
                e.printStackTrace();
            }

            return false;
        }
    }

    @Sharable
    @SideOnly(Side.CLIENT)
    private static final class HandlerClient extends SimpleChannelInboundHandler<PacketBase>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, PacketBase packet) throws Exception
        {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null)
            {
                if (mc.theWorld != null)
                {
                    packet.onClientPacket(mc.theWorld, mc.thePlayer);
                }
                else
                {
                    LogManager.getLogger().log(Level.FATAL, "Error player is null");
                }
            }
            else
            {
                LogManager.getLogger().log(Level.FATAL, "Error world is null");
            }
        }
    }

    @Sharable
    private static final class HandlerServer extends SimpleChannelInboundHandler<PacketBase>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, PacketBase packet) throws Exception
        {
            if (FMLCommonHandler.instance().getEffectiveSide().isServer())
            {
                EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
                if (player != null)
                {
                    if (player.getEntityWorld() != null)
                    {
                        packet.onServerPacket(player.getEntityWorld(), player);
                    }
                    else
                    {
                        LogManager.getLogger().log(Level.FATAL, "Error player is null");
                    }
                }
                else
                {
                    LogManager.getLogger().log(Level.FATAL, "Error world is null");
                }
            }
        }
    }

}