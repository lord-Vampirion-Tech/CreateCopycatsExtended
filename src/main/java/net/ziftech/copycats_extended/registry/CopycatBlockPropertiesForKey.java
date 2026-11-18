package net.ziftech.copycats_extended.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import net.ziftech.copycats_extended.block.DoubleBlock;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramidOpposite;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramid_Entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CopycatBlockPropertiesForKey {

  public enum EntityProperty {
    TOP_POINT_X,
    TOP_POINT_Y,
    TOP_POINT_Z
  }

  public record KeyProperty(
    String name,
    Property<?> stateProperty,
    EntityProperty entityProperty
  ) {
    public static KeyProperty state(Property<?> property) {
      return new KeyProperty(property.getName(), property, null);
    }

    public static KeyProperty entity(EntityProperty property) {
      return new KeyProperty(property.name().toLowerCase(), null, property);
    }
  }

  private static final Map<Class<?>, List<KeyProperty>> PROPERTIES = new LinkedHashMap<>();

  static {
    register(
      DoubleBlock.class,
      KeyProperty.state(DoubleBlock.AXIS),
      KeyProperty.state(DoubleBlock.SLICE_Y),
      KeyProperty.state(DoubleBlock.CONNECT_HALF)
    );

    register(
      BASE_CopycatPyramidBlock.class,
      KeyProperty.state(BASE_CopycatPyramidBlock.FACING),
      KeyProperty.entity(EntityProperty.TOP_POINT_X),
      KeyProperty.entity(EntityProperty.TOP_POINT_Y),
      KeyProperty.entity(EntityProperty.TOP_POINT_Z)
    );
  }

  public static void register(Class<?> blockClass, KeyProperty... properties) {
    PROPERTIES.put(blockClass, List.of(properties));
  }

  public static List<KeyProperty> getProperties(Block block) {
    List<KeyProperty> result = new ArrayList<>();

    PROPERTIES.entrySet().stream()
      .filter(entry -> entry.getKey().isInstance(block))
      .flatMap(entry -> entry.getValue().stream())
      .filter(property ->
        property.stateProperty() == null
          || block.getStateDefinition().getProperties().contains(property.stateProperty())
      )
      .forEach(result::add);

    if (block instanceof CopycatPyramidOpposite) {
      result.add(KeyProperty.state(CopycatPyramidOpposite.ROTATION));
    }

    return List.copyOf(result);
  }

  public static String getPropertyText(
    Level level,
    BlockPos pos,
    BlockState state,
    KeyProperty property
  ) {
    String name = property.name().replace('_', ' ');

    if (property.stateProperty() != null) {
      return getStatePropertyText(state, property.stateProperty());
    }

    if (level.getBlockEntity(pos) instanceof CopycatPyramid_Entity entity) {
      return switch (property.entityProperty()) {
        case TOP_POINT_X -> name + ": " + entity.getTopPointX();
        case TOP_POINT_Y -> name + ": " + entity.getTopPointY();
        case TOP_POINT_Z -> name + ": " + entity.getTopPointZ();
      };
    }

    return name + ": ?";
  }

  private static <T extends Comparable<T>> String getStatePropertyValue(
    BlockState state,
    Property<T> property
  ) {
    return property.getName(state.getValue(property));
  }

  private static <T extends Comparable<T>> String getStatePropertyText(
    BlockState state,
    Property<T> property
  ) {
    String name = property.getName().replace('_', ' ');

    if (!state.hasProperty(property))
      return name + ": ?";

    return name + ": " + property.getName(state.getValue(property));
  }

  public static void cycleProperty(
    Level level,
    BlockPos pos,
    BlockState state,
    KeyProperty property,
    boolean reverse
  ) {
    if (property.stateProperty() != null) {
      BlockState newState = cycleStateProperty(
        state,
        property.stateProperty(),
        reverse
      );

      if (newState != state) {
        level.setBlock(pos, newState, 3);
      }

      return;
    }

    if (!(level.getBlockEntity(pos) instanceof CopycatPyramid_Entity entity))
      return;

    int x = entity.getTopPointX();
    int y = entity.getTopPointY();
    int z = entity.getTopPointZ();

    int direction = reverse ? -1 : 1;

    switch (property.entityProperty()) {
      case TOP_POINT_X -> x = Math.floorMod(x + direction, 9);
      case TOP_POINT_Y -> y = Math.floorMod(y - 1 + direction, 8) + 1;
      case TOP_POINT_Z -> z = Math.floorMod(z + direction, 9);
    }

    entity.setTopPoint(x, y, z);
  }

  private static BlockState cycleStateProperty(
    BlockState state,
    Property<?> property,
    boolean reverse
  ) {
    return cycleStatePropertyUnchecked(state, property, reverse);
  }

  private static <T extends Comparable<T>> BlockState cycleStatePropertyUnchecked(
    BlockState state,
    Property<T> property,
    boolean reverse
  ) {
    if (!state.hasProperty(property))
      return state;

    List<T> values = List.copyOf(property.getPossibleValues());

    if (values.isEmpty())
      return state;

    T currentValue = state.getValue(property);

    int currentIndex = values.indexOf(currentValue);
    int direction = reverse ? -1 : 1;
    int nextIndex = Math.floorMod(currentIndex + direction, values.size());

    return state.setValue(property, values.get(nextIndex));
  }
}