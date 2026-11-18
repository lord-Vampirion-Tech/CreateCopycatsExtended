
package net.ziftech.copycats_extended.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import net.ziftech.copycats_extended.block.DoubleBlock;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramidOpposite;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CopycatBlockPropertiesForKey {

  private static final Map<Class<?>, List<Property<?>>> PROPERTIES = new LinkedHashMap<>();

  static {
    register(
      DoubleBlock.class,
      DoubleBlock.AXIS,
      DoubleBlock.SLICE_Y,
      DoubleBlock.CONNECT_HALF
    );

    register(
      BASE_CopycatPyramidBlock.class,
      BASE_CopycatPyramidBlock.FACING,
      BASE_CopycatPyramidBlock.TOP_POINT_X,
      BASE_CopycatPyramidBlock.TOP_POINT_Y,
      BASE_CopycatPyramidBlock.TOP_POINT_Z
    );
  }

  public static void register(Class<?> blockClass, Property<?>... properties) {
    PROPERTIES.put(blockClass, List.of(properties));
  }

  public static List<Property<?>> getProperties(Block block) {

    if (block instanceof CopycatPyramidOpposite) {
      return List.of(
        BASE_CopycatPyramidBlock.FACING,
        BASE_CopycatPyramidBlock.TOP_POINT_X,
        BASE_CopycatPyramidBlock.TOP_POINT_Z
      );
    }


    return PROPERTIES.entrySet().stream()
      .filter(entry -> entry.getKey().isInstance(block))
      .flatMap(entry -> entry.getValue().stream())
      .filter(block.getStateDefinition().getProperties()::contains)
      .toList();
  }
}
