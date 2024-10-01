package com.hellfire.net.debug_visualizer.impl;

import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.impl.debugmod.DebugModOptions;
import com.hellfire.net.debug_visualizer.impl.debugmod.DebugModVisualizer;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleOptions;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleVisualizer;
import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.visualizers.IDebugVisualizer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;

public class DebugParticleVisualizerTest {


    // Setup server
    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        // Create the instance
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
        );
        instanceContainer.setBlock(new Vec(0, 43, 0), Block.DIAMOND_BLOCK);

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 41, 0));

            MinecraftServer.getSchedulerManager().scheduleTask(
                    () -> createBlock(new Vec(0, 43, 0), player),
                    TaskSchedule.seconds(2),
                    TaskSchedule.stop()
            );
        });
    }

    public static void createBlock(Vec position, Player player) {
        final IDebugVisualizer visualizer = new DebugParticleVisualizer();
        visualizer.createBlock(position)
                .withConfig(DebugParticleOptions.class, DebugParticleOptions.createWithColor(DebugColor.DARK_BLUE))
                .withConfig(DebugModOptions.class, DebugModOptions.createPrimaryColor(DebugColor.DARK_BLUE))
                .toCollection()
                .draw(VisualSupervisor.STD.create(player));

        visualizer.createLine(new Vec(0, 0, 0), new Vec(0, 50, 5))
                .withConfig(DebugParticleOptions.class, DebugParticleOptions.createWithColor(DebugColor.DARK_RED))
                .toCollection()
                .draw(VisualSupervisor.STD.create(player));

        visualizer.createArea(new Vec(5, 45, 5), new Vec(10, 50, 10))
                .withConfig(DebugParticleOptions.class, DebugParticleOptions.createWithDensity(0.1))
                .toCollection()
                .draw(VisualSupervisor.STD.create(player));
    }

}