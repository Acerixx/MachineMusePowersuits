package net.machinemuse_old.numina.common.proxy;

import net.machinemuse_old.numina.event.FOVUpdateEventHandler;
import net.machinemuse_old.numina.event.KeybindKeyHandler;
import net.machinemuse_old.numina.mouse.MouseEventHandler;
import net.machinemuse_old.numina.render.RenderGameOverlayEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 11:57 AM, 9/3/13
 *
 * Ported to Java by lehjr on 10/26/16.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new MouseEventHandler());
        MinecraftForge.EVENT_BUS.register(new RenderGameOverlayEventHandler());
        MinecraftForge.EVENT_BUS.register(new FOVUpdateEventHandler());
        MinecraftForge.EVENT_BUS.register(new KeybindKeyHandler());
    }
}