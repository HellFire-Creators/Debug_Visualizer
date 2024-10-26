package com.hellfire.net.debug_visualizer.impl;

import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.impl.debugmod.DebugModOptions;
import com.hellfire.net.debug_visualizer.impl.displayblock.DebugDisplayOptions;
import com.hellfire.net.debug_visualizer.impl.displayblock.DebugDisplayVisualizer;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleOptions;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleVisualizer;
import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.Shape;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElementCollection;
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
import net.minestom.server.particle.Particle;
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
        instanceContainer.setTimeRate(0);

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 41, 0));

            MinecraftServer.getSchedulerManager().scheduleTask(
                    () -> createBlock(new Vec(0, 43, 0), player),
                    TaskSchedule.seconds(1),
                    TaskSchedule.stop()
            );
        });
    }

    public static void createBlock(Vec position, Player player) {
        double rot = 263;
        Vec dir = new Vec(1, 1, 1);

        VisualizerElementCollection.builder()
                .addElement(Shape.createPlane(
                        8, 4,
                        new Vec(0, 46, 5),
                        dir, rot
                ))
                .build().draw(VisualSupervisor.STD.create(player, new DebugDisplayVisualizer()));


        VisualizerElementCollection.builder()
                .addElement(Shape.createPlane(
                        8, 4,
                        new Vec(0, 44, 5),
                        dir, rot,
                        DebugParticleOptions.createWithDensity(0.1).setFillDensity(0.3)
                ))
                .build().draw(VisualSupervisor.STD.create(player, new DebugParticleVisualizer()));
    }

}