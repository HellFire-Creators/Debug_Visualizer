package com.hellfire.net.debug_visualizer.impl;

import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleOptions;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleVisualizer;
import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.transformations.ObjTransformation;
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
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.Direction;

import static java.lang.Math.PI;

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
        spawnLines(player, world);
        spawnPoints(player, world);
        spawnBlocks(player, world);
        spawnAreas(player, world);
        spawnPlanes(player, world);

        // Dev tests
        final Vec dir = new Vec(1, 1, 1);
        final Vec center = new Vec(8.5, 43, -1.5);
        final VisualizerElementCollection.Builder builder = VisualizerElementCollection.builder();
        builder.addElement(Shape.createPlane(
                1, 1,
                center, new ObjTransformation().faceTowards(dir)
        ));

        builder.addElement(Shape.createLine(
                        center, center.add(dir.mul(10)), DebugParticleOptions.createWithColor(DebugColor.GOLD)
                ))
                .build().draw(VisualSupervisor.STD.create(player, new DebugParticleVisualizer()));
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
                .addElement(Shape.createBlock(new Vec(2, 41, 3), null))
                .build().draw(VisualSupervisor.STD.create(p, new DebugParticleVisualizer()));
    }

    private static void spawnAreas(Player p, Instance w) {
        w.setBlock(-3, 40, 12, Block.STONE);
        fillArea(new Vec(-2, 40, 9), new Vec(-2, 40, 10), Block.STONE, w);
        fillArea(new Vec(-2, 40, 7), new Vec(-3, 40, 7), Block.STONE, w);
        fillArea(new Vec(-2, 40, 3), new Vec(-3, 40, 5), Block.STONE, w);
        fillArea(new Vec(-5, 40, 3), new Vec(-6, 40, 4), Block.STONE, w);
        fillArea(new Vec(-8, 40, 3), new Vec(-9, 40, 5), Block.STONE, w);
        fillArea(new Vec(-11, 40, 3), new Vec(-13, 40, 4), Block.STONE, w);
        fillArea(new Vec(-5, 40, 7), new Vec(-6, 40, 8), Block.STONE, w);
        fillArea(new Vec(-8, 40, 7), new Vec(-9, 40, 9), Block.STONE, w);
        fillArea(new Vec(-11, 40, 7), new Vec(-13, 40, 8), Block.STONE, w);
        fillArea(new Vec(-16, 40, 3), new Vec(-17, 40, 4), Block.STONE, w);

        final VisualizerElementCollection.Builder b = VisualizerElementCollection.builder();

        // Spawn blocky area
        b.addElement(Shape.createBlockArea(new Vec(-2, 41, 3), new Vec(-3, 43, 5), null));
        b.addElement(Shape.createBlockArea(new Vec(-2, 41, 7), new Vec(-3, 41, 7), null));
        b.addElement(Shape.createBlockArea(new Vec(-2, 41, 9), new Vec(-2, 42, 10), null));
        b.addElement(Shape.createBlockArea(new Vec(-3, 41, 12), new Vec(-3, 44, 12), null));

        // Spawn non-rotated area
        b.addElement(Shape.createArea(
                new Vec(-6, 41, 5), new Vec(-4, 45, 3), null
        ));

        b.addElement(Shape.createArea(
                new Vec(-6, 41, 9), new Vec(-4, 44, 7),
                new ObjTransformation().faceTowards(Direction.SOUTH.vec())
        ));

        // Spawn rotated area
        b.addElement(Shape.createArea(
                new Vec(-8.5, 41, 6), new Vec(-7.5, 43, 3),
                null
        ));
        b.addElement(Shape.createArea(
                new Vec(-8.5, 41, 10), new Vec(-7.5, 43, 7),
                new ObjTransformation().rotate(Direction.UP.vec(), PI/ 8)
        ));

        b.addElement(Shape.createArea(
                new Vec(-10, 41, 3.5), new Vec(-13, 43, 4.5),
                null
        ));
        b.addElement(Shape.createArea(
                new Vec(-10, 41, 7.5), new Vec(-13, 43, 8.5),
                new ObjTransformation().faceAndRotate(Direction.SOUTH.vec(), PI / 4)
        ));

        b.addElement(Shape.createArea(
                new Vec(-16, 42, 3), new Vec(-17, 43, 5),
                new ObjTransformation().faceTowards(new Vec(1, 1, 1))
        ));


        b.build().draw(VisualSupervisor.STD.create(p, new DebugParticleVisualizer()));
    }

    private static void spawnPlanes(Player p, Instance w) {
        w.setBlock(0, 40, -8, Block.STONE);
        w.setBlock(0, 40, -10, Block.STONE);
        w.setBlock(0, 40, -12, Block.STONE);
        w.setBlock(0, 40, -14, Block.STONE);
        w.setBlock(0, 40, -16, Block.STONE);
        w.setBlock(2, 40, -8, Block.STONE);
        w.setBlock(2, 40, -10, Block.STONE);
        w.setBlock(2, 40, -12, Block.STONE);
        w.setBlock(2, 40, -14, Block.STONE);
        w.setBlock(2, 40, -16, Block.STONE);

        fillArea(new Vec(5, 40, -8), new Vec(5, 40, -9), Block.STONE, w);
        fillArea(new Vec(7, 40, -8), new Vec(8, 40, -9), Block.STONE, w);

        VisualizerElementCollection.Builder b = VisualizerElementCollection.builder();

        // Single planes
        b.addElement(Shape.createPlane(1, 1, new Vec(0.5, 41, -7.5), new ObjTransformation().faceTowards(Direction.UP.vec())));
        b.addElement(Shape.createPlane(1, 1, new Vec(2.5, 41, -7.5), new ObjTransformation().faceTowards(Direction.UP.vec()).rotate(Direction.UP.vec(), PI / 4)));

        b.addElement(Shape.createPlane(1, 1, new Vec(0.5, 41, -9.5), new ObjTransformation().faceTowards(Direction.DOWN.vec()).rotate(Direction.UP.vec(), 0)));
        b.addElement(Shape.createPlane(1, 1, new Vec(2.5, 41, -9.5), new ObjTransformation().faceTowards(Direction.DOWN.vec()).rotate(Direction.UP.vec(), PI / 4)));

        b.addElement(Shape.createPlane(1, 1, new Vec(0.5, 41.5, -11.5), new ObjTransformation().faceTowards(Direction.SOUTH.vec()) .rotate(Direction.UP.vec(), 0)));
        b.addElement(Shape.createPlane(1, 1, new Vec(2.5, 41.5, -11.5), new ObjTransformation().faceTowards(Direction.SOUTH.vec()) .rotate(Direction.UP.vec(), PI / 4)));

        b.addElement(Shape.createPlane(1, 1, new Vec(0.5, 41.5, -13.5), new ObjTransformation().faceTowards(Direction.WEST.vec()) .rotate(Direction.UP.vec(), 0)));
        b.addElement(Shape.createPlane(1, 1, new Vec(2.5, 41.5, -13.5), new ObjTransformation().faceTowards(Direction.WEST.vec()) .rotate(Direction.UP.vec(), PI / 4)));

        // Conor-02.11.2024
        // Todo:
        //  Incorrect rotation!!!
        b.addElement(Shape.createPlane(1, 1, new Vec(0.5, 41.5, -15.5), new ObjTransformation().faceTowards(new Vec(1, -1, -1))));
        b.addElement(Shape.createPlane(1, 1, new Vec(2.5, 41.5, -15.5), new ObjTransformation().faceTowards(new Vec(1, -1, -1)).rotate(Direction.UP.vec(), PI / 4)));

        // Larger planes
        b.addElement(Shape.createPlane(1, 2, new Vec(5.5, 41, -8), new ObjTransformation().faceTowards(Direction.UP.vec()).rotate(Direction.UP.vec(), 0)));
        b.addElement(Shape.createPlane(1, 2, new Vec(8, 41, -8), new ObjTransformation().faceTowards(Direction.UP.vec()).rotate(Direction.UP.vec(), PI / 4).scale(2)));


        b.build().draw(VisualSupervisor.STD.create(p, new DebugParticleVisualizer()));
    }

    private static void spawnLines(Player p, Instance w) {
        fillArea(new Vec(-3, 40, -8), new Vec(-5, 40, -8), Block.STONE, w);
        fillArea(new Vec(-4, 40, -10), new Vec(-4, 40, -12), Block.STONE, w);

        w.setBlock(-4, 40, -14, Block.STONE);
        w.setBlock(-7, 40, -8, Block.STONE);
        w.setBlock(-8, 40, -9, Block.STONE);
        w.setBlock(-9, 40, -10, Block.STONE);

        VisualizerElementCollection.Builder b = VisualizerElementCollection.builder();

        // Dir lines
        b.addElement(Shape.createLine(new Vec(-2.5, 41, -7.5), new Vec(-4.3, 41, -7.5)));
        b.addElement(Shape.createLine(new Vec(-3.5, 41, -9.5), new Vec(-3.5, 41, -11.5)));
        b.addElement(Shape.createLine(new Vec(-3.5, 41, -13.5), new Vec(-3.5, 43, -13.5)));

        // Diag lines
        b.addElement(Shape.createLine(new Vec(-6.5, 41, -7.5), new Vec(-8.5, 41, -9.5)));

        b.build().draw(VisualSupervisor.STD.create(p, new DebugParticleVisualizer()));
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