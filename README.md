# Minecraft Debug Visualizer
A small library which enables developers to quickly create debug views.
![basic shapes](https://github.com/HellFire-Creators/Debug_Visualizer/blob/master/imgs/basic_shapes.png)

# Possibilities
Currently *Debug Visualizer* only has 4 basic shapes:
- Block (Highlights a block)
- Area  (Highlights an 3d area between two points)
- Plane (Highlights a 2d area between two points; However the plane has to be aligned to an axis)
- Line  (A line between two points)

Even with only these few basic shapes, a lot can be done.

# Usage
## VisualSupervisor
First, you need a `VisualSupervisor`, an interface that keeps track of the visual elements a certain player can see.  
If you already have a custom player class which extends from net.minestom.Player, then best just implement the interface there.  
If you don't have such a custom implementation, you can also use the premade `VisualSupervisor.STD` implementation; Just keep in mind that you have to keep track of that object then.  
I highly encourage reading the provided documentation of this interface, since the methods `#addVisibleElements` and `#removeVisibleElements` could be misunderstood. Alternatively, view the `VisualSupervisor.STD` implementation.

## DebugVisualizer
`IDebugVisualizer` is the interface which implements the logic to "draw" and "clear" visual elements from the player's view.  
Using any of the methods in this interface will return a `VisualizerElement`, which is a single basic shape. Best to not use these manually.  
Currently, the following implementations exist:
- `EmptyVisualizer`; This is intended as a placeholder for when you don't know which visualizer to use. As the name implies, it does nothing.
- `DebugModVisualizer`; This visualizer implements the methods to "draw" and "clear" visual elements using [mattw's Minecraft Debug Renderer](https://github.com/mworzala/mc_debug_renderer)

## VisualizerElementCollection
(Quite a mouth full)  
As the name implies, this is a collection of `VisualizerElement`s and is very convenient when creating more complex visual effects.  
When you want to "draw" or "clear" something, I'd highly recommend using these. Not only are they convenient, but also have some hidden functionality which uses the `VisualSupervisor` to keep track of the visual elements currently displayed.  
  
Another very convenient class is `SingleVisualizerElementCollection` (the names aren't getting any better...). This class has the convenient effect of always checking if the `VisualSupervisor` already has a visual element with the same *key* displayed to them.
If so, the "old" visual element is removed and the new collection is added. This ensures that there's only ever one collection with the same *key* visible to the `VisualSupervisor`.

# Example
Lets say you created a super awesome raycasting algorithm and want to check where the ray collides, just to be sure it actually works. If so, oddly specific, you could do the following:
```Java
 final double radius = 0.08f;
final IDebugVisualizer vis = player.getDebugVisualizer();        // Get current visualizer
VisualizerElementCollection.builder()        // Create a new collection
    .addElement(vis.createLine(collides, player.getPosAtEyeHeight().asVec()))        // Now I add a line from the position of the player's eyes to where the ray hit a block
    .addElement(vis.createArea(collides.sub(radius), collides.add(radius), DebugAreaOptions.createFillColor(DebugColor.RED)))    // Add a small red box to emphasise the spot it hit
    .buildSingleVis("debug_ray")        // Make the collection "single vis", so every time I cast the ray, the old one automatically vanishes
    .draw(player);        // Draw all the shapes to the player
```
I have a custom implementation of the net.minestom.Player class which also implements `VisualSupervisor` and in this example that variable is called "player".  

# Add to your project
Best to just use [JitPack](https://jitpack.io/#HellFire-Creators/Debug_Visualizer)

## Maven
```
<repositories>
    <!-- ... -->
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.HellFire-Creators</groupId>
    <artifactId>Debug_Visualizer</artifactId>
    <version>[REPLACE_WITH_VERSION]</version>
</dependency>
```

## Gradle
```
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.HellFire-Creators:Debug_Visualizer:[REPLACE_WITH_VERSION]'
}
```

# Future additions
- The vanilla implementation; Pretty high on the priority list for this project
- More shapes; More shapes would be nice, like *Circles*, *tilted Planes*, *spheres*?
- Changing options; The options are "ok" but not good for vanilla implementations. There will probably be a large refactor making them better
