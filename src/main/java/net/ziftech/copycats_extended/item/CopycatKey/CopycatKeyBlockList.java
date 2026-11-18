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
      CopycatBlockPropertiesForKey.state(DoubleBlock.AXIS),
      CopycatBlockPropertiesForKey.state(DoubleBlock.SLICE_Y),
      CopycatBlockPropertiesForKey.state(DoubleBlock.CONNECT_HALF)
    );

    register(
      BASE_CopycatPyramidBlock.class,
      CopycatBlockPropertiesForKey.state(BASE_CopycatPyramidBlock.FACING),

      CopycatBlockPropertiesForKey.entityInt(
        "top_point_x",
        CopycatPyramid_Entity.class,
        CopycatPyramid_Entity::getTopPointX,
        (entity, value) -> entity.setTopPoint(
          value,
          entity.getTopPointY(),
          entity.getTopPointZ()
        ),
        0, 8
      ),

      CopycatBlockPropertiesForKey.entityInt(
        "top_point_y",
        CopycatPyramid_Entity.class,
        CopycatPyramid_Entity::getTopPointY,
        (entity, value) -> entity.setTopPoint(
          entity.getTopPointX(),
          value,
          entity.getTopPointZ()
        ),
        1, 8
      ),

      CopycatBlockPropertiesForKey.entityInt(
        "top_point_z",
        CopycatPyramid_Entity.class,
        CopycatPyramid_Entity::getTopPointZ,
        (entity, value) -> entity.setTopPoint(
          entity.getTopPointX(),
          entity.getTopPointY(),
          value
        ),
        0, 8
      )
    );

    register(
      CopycatPyramidOpposite.class,
      CopycatBlockPropertiesForKey.state(CopycatPyramidOpposite.ROTATION)
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