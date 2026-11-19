
package net.ziftech.copycats_extended.item.CopycatKey;

import net.minecraft.world.level.block.Block;

import net.ziftech.copycats_extended.block.DoubleBlock;
import net.ziftech.copycats_extended.block.Pyramids.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CopycatKeyBlockList {

  private static final Map<Class<?>, List<CopycatBlockPropertiesForKey.KeyProperty>> BLOCKS =
    new LinkedHashMap<>();

  static {
    register(
      DoubleBlock.class,
      CopycatBlockPropertiesForKey.KeyProperty.state(DoubleBlock.AXIS),
      CopycatBlockPropertiesForKey.KeyProperty.state(DoubleBlock.SLICE_Y),
      CopycatBlockPropertiesForKey.KeyProperty.state(DoubleBlock.CONNECT_HALF)
    );

    register(
      BASE_CopycatPyramidBlock.class,
      CopycatBlockPropertiesForKey.KeyProperty.state(BASE_CopycatPyramidBlock.FACING)
    );

    register(
      CopycatPyramidOpposite.class,
      CopycatBlockPropertiesForKey.KeyProperty.state(CopycatPyramidOpposite.ROTATION)
    );
  }

  private static void register(
    Class<?> blockClass,
    CopycatBlockPropertiesForKey.KeyProperty... properties
  ) {
    BLOCKS.put(blockClass, List.of(properties));
  }

  public static List<CopycatBlockPropertiesForKey.KeyProperty> getProperties(Block block) {
    return BLOCKS.entrySet().stream()
      .filter(entry -> entry.getKey().isInstance(block))
      .flatMap(entry -> entry.getValue().stream())
      .filter(property ->
        property.stateProperty() == null
          || block.getStateDefinition().getProperties().contains(property.stateProperty())
      )
      .toList();
  }

  private CopycatKeyBlockList() {}
}
