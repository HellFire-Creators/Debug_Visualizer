package com.hellfire.net.debug_visualizer.impl;

import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleOptions;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleVisualizer;
import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.visualizers.Shape;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElementCollection;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.Direction;

public class DebugParticleVisualizerTest {

    public static void main(String[] args) {
        setupServer();
    }

    private static void setupServer() {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        // Create the instance
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
        );
        instanceContainer.setTimeRate(0);

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(3, 40.2, -2.2));

            MinecraftServer.getSchedulerManager().scheduleTask(
                    () -> runTests(player, instanceContainer),
                    TaskSchedule.seconds(1),
                    TaskSchedule.stop()
            );
        });
    }

    private static void runTests(Player player, Instance world) {
        spawnPoints(player, world);
        spawnBlocks(player, world);
        spawnAreas(player, world);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tests
    ///////////////////////////////////////////////////////////////////////////

    private static void spawnPoints(Player p, Instance w) {
        w.setBlock(new Vec(8, 40, 3), Block.STONE);
        w.setBlock(new Vec(6, 40, 3), Block.STONE);
        final VisualizerElementCollection.Builder b = VisualizerElementCollection.builder();

        // Spawn points on corners
        b.addElement(Shape.createPoint(new Vec(8, 41, 3), DebugParticleOptions.createWithColor(DebugColor.WHITE)));
        b.addElement(Shape.createPoint(new Vec(9, 41, 3), DebugParticleOptions.createWithColor(DebugColor.GREEN)));
        b.addElement(Shape.createPoint(new Vec(8, 41, 4), DebugParticleOptions.createWithColor(DebugColor.YELLOW)));
        b.addElement(Shape.createPoint(new Vec(9, 41, 4), DebugParticleOptions.createWithColor(DebugColor.RED)));

        // Spawn in the middle
        b.addElement(Shape.createPoint(new Vec(6.5, 41, 3.5), DebugParticleOptions.createWithColor(DebugColor.GOLD)));

        b.build().draw(VisualSupervisor.STD.create(p, new DebugParticleVisualizer()));
    }

    private static void spawnBlocks(Player p, Instance w) {
        w.setBlock(new Vec(2, 40, 3), Block.STONE);

        VisualizerElementCollection.builder()
                .addElement(Shape.createBlock(new Vec(2, 41, 3)))
                .build().draw(VisualSupervisor.STD.create(p, new DebugParticleVisualizer()));
    }

    private static void spawnAreas(Player p, Instance w) {
        w.setBlock(-3, 40, 3, Block.STONE);
        fillArea(new Vec(-5, 40, 3), new Vec(-6, 40, 4), Block.STONE, w);
        fillArea(new Vec(-8, 40, 3), new Vec(-9, 40, 5), Block.STONE, w);
        fillArea(new Vec(-11, 40, 3), new Vec(-13, 40, 4), Block.STONE, w);
        fillArea(new Vec(-15, 40, 3), new Vec(-16, 40, 4), Block.STONE, w);

        final VisualizerElementCollection.Builder b = VisualizerElementCollection.builder();

        // Spawn blocky area
        b.addElement(Shape.createBlockArea(new Vec(-2, 41, 3), new Vec(-3, 43, 4)));

        // Spawn non-rotated area
        b.addElement(Shape.createArea(
                new Vec(-5, 41, 4), new Vec(2, 3, 2),
                Direction.UP.vec(), 0
        ));

        b.addElement(Shape.createArea(
                new Vec(-8, 42, 3), new Vec(2, 3, 2),
                Direction.SOUTH.vec(), 0
        ));

        b.addElement(Shape.createArea(
                new Vec(-10, 42, 4), new Vec(2, 3, 2),
                Direction.WEST.vec(), 0
        ));

        // Spawn rotated area
        b.addElement(Shape.createArea(
                new Vec(-15, 41, 4), new Vec(2, 3, 2),
                Direction.UP.vec(), 45
        ));


        b.build().draw(VisualSupervisor.STD.create(p, new DebugParticleVisualizer()));
    }

    public static void createBlock(Vec position, Player player) {
        VisualizerElementCollection.builder()
                .addElement(Shape.createArea(
                        new Vec(3, 44, 8), new Vec(3, 5, 3),
                        new Vec(0, -1, 0), 45,
                        DebugParticleOptions.createWithDensity(0.2)
                ))
                .build().draw(VisualSupervisor.STD.create("Conorsmine", new DebugParticleVisualizer()));
    }

    /*                    Helper funcs                    */

    private static void fillArea(Vec a, Vec b, Block block, Instance w) {
        final Vec cA = a.min(b);
        final Vec cB = a.max(b);

        final Vec diff = cB.sub(cA);

        for (int x = 0; x <= diff.x(); x++) {
            for (int z = 0; z <= diff.z(); z++) {
                for (int y = 0; y <= diff.y(); y++) {
                    w.setBlock(cA.add(x, y, z), block);
                }
            }
        }
    }
}