
package net.ziftech.copycats_extended.item.CopycatKey;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

public class CopycatBlockPropertiesForKey {

  public static class KeyProperty {

    private final String name;
    private final Property<?> stateProperty;
    private final Function<BlockState, String> stateReader;
    private final BiFunction<Level, BlockPos, String> entityReader;
    private final BiFunction<BlockState, Boolean, BlockState> stateCycler;
    private final EntityCycler entityCycler;



    private KeyProperty(
      String name,
      Property<?> stateProperty,
      Function<BlockState, String> stateReader,
      BiFunction<Level, BlockPos, String> entityReader,
      BiFunction<BlockState, Boolean, BlockState> stateCycler,
      EntityCycler entityCycler
    ) {
      this.name = name;
      this.stateProperty = stateProperty;
      this.stateReader = stateReader;
      this.entityReader = entityReader;
      this.stateCycler = stateCycler;
      this.entityCycler = entityCycler;
    }

    public String name() {
      return name;
    }
    public Property<?> stateProperty() {
      return stateProperty;
    }

    public static <T extends Comparable<T>> KeyProperty state(Property<T> property) {
      return new KeyProperty(
        property.getName(),
        property,
        state -> property.getName().replace('_', ' ') + ": "
          + property.getName(state.getValue(property)),
        null,
        (state, reverse) -> cycleStateProperty(state, property, reverse),
        null
      );
    }

    public static <E> KeyProperty entityInt(
      String name,
      Class<E> entityType,
      ToIntFunction<E> getter,
      ObjIntConsumer<E> setter,
      int min,
      int max
    ) {
      return new KeyProperty(
        name,
        null,
        null,
        (level, pos) -> {
          if (!entityType.isInstance(level.getBlockEntity(pos)))
            return name.replace('_', ' ') + ": ?";

          E entity = entityType.cast(level.getBlockEntity(pos));
          return name.replace('_', ' ') + ": " + getter.applyAsInt(entity);
        },
        null,
        (level, pos, reverse) -> {
          if (!entityType.isInstance(level.getBlockEntity(pos)))
            return;

          E entity = entityType.cast(level.getBlockEntity(pos));
          int current = getter.applyAsInt(entity);
          int direction = reverse ? -1 : 1;
          int next = Math.floorMod(current - min + direction, max - min + 1) + min;

          setter.accept(entity, next);
        }
      );
    }

    public String getValue(Level level, BlockPos pos, BlockState state) {
      if (stateReader != null)
        return stateReader.apply(state);

      return entityReader.apply(level, pos);
    }

    public void cycle(Level level, BlockPos pos, BlockState state, boolean reverse) {
      if (stateCycler != null) {
        BlockState newState = stateCycler.apply(state, reverse);

        if (newState != state)
          level.setBlock(pos, newState, 3);

        return;
      }

      if (entityCycler != null)
        entityCycler.cycle(level, pos, reverse);
    }
  }

  @FunctionalInterface
  private interface EntityCycler {
    void cycle(Level level, BlockPos pos, boolean reverse);
  }


  public static String getPropertyText(
    Level level,
    BlockPos pos,
    BlockState state,
    KeyProperty property
  ) {
    return property.getValue(level, pos, state);
  }

  public static void cycleProperty(
    Level level,
    BlockPos pos,
    BlockState state,
    KeyProperty property,
    boolean reverse
  ) {
    property.cycle(level, pos, state, reverse);
  }

  private static <T extends Comparable<T>> BlockState cycleStateProperty(
    BlockState state,
    Property<T> property,
    boolean reverse
  ) {
    if (!state.hasProperty(property))
      return state;

    List<T> values = List.copyOf(property.getPossibleValues());

    if (values.isEmpty())
      return state;

    int currentIndex = values.indexOf(state.getValue(property));
    int direction = reverse ? -1 : 1;
    int nextIndex = Math.floorMod(currentIndex + direction, values.size());

    return state.setValue(property, values.get(nextIndex));
  }
}
