
package net.ziftech.copycats_extended.registry.tooltip;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public enum ModCharacteristics implements StringRepresentable {

  TEST_INFO,
  TEST_KEY(() -> "R");

  private static final Map<Item, List<ModCharacteristics>> ITEM_CHARACTERISTICS = new HashMap<>();
  private final Supplier<Object>[] args;

  @SafeVarargs
  ModCharacteristics(Supplier<Object>... args) {
    this.args = args;
  }

  @Override
  public @NotNull String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  public String getTitleKey() {
    return "tooltip.copycats_extended.characteristics." + getSerializedName() + ".title";
  }

  public String getDescriptionKey() {
    return "tooltip.copycats_extended.characteristics." + getSerializedName() + ".description";
  }

  public Supplier<Object>[] getArgs() {
    return args;
  }

  public static void register(
    ItemLike item,
    ModCharacteristics... characteristics
  ) {
    ITEM_CHARACTERISTICS.put(item.asItem(), List.of(characteristics));
  }

  public static List<ModCharacteristics> get(Item item) {
    return ITEM_CHARACTERISTICS.getOrDefault(item, List.of());
  }

  public static boolean has(Item item) {
    return ITEM_CHARACTERISTICS.containsKey(item);
  }

  public static List<ModCharacteristics> all() {
    return List.of(values());
  }
}
