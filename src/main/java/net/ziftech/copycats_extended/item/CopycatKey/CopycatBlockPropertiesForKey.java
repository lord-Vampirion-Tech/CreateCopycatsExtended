
package net.ziftech.copycats_extended.item.CopycatKey;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CopycatBlockPropertiesForKey {

  public static class KeyProperty {

    private final String name;
    private final Property<?> stateProperty;
    private final Function<BlockState, String> stateReader;
    private final BiFunction<BlockState, Boolean, BlockState> stateCycler;

    private KeyProperty(
      String name,
      Property<?> stateProperty,
      Function<BlockState, String> stateReader,
      BiFunction<BlockState, Boolean, BlockState> stateCycler
    ) {
      this.name = name;
      this.stateProperty = stateProperty;
      this.stateReader = stateReader;
      this.stateCycler = stateCycler;
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
        (state, reverse) -> cycleStateProperty(state, property, reverse)
      );
    }

    public String getValue(BlockState state) {
      return stateReader.apply(state);
    }

    public void cycle(Level level, BlockPos pos, BlockState state, boolean reverse) {
      BlockState newState = stateCycler.apply(state, reverse);

      if (newState != state)
        level.setBlock(pos, newState, 3);
    }
  }

  public static String getPropertyText(
    Level level,
    BlockPos pos,
    BlockState state,
    KeyProperty property
  ) {
    return property.getValue(state);
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
