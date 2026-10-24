
package net.ziftech.copycats_extended.block;

import net.minecraft.util.StringRepresentable;

public enum SlopeMode implements StringRepresentable {
  FIRST("first"),
  SECOND("second"),
  THIRD("third");

  private final String name;

  SlopeMode(String name) {
    this.name = name;
  }

  @Override
  public String getSerializedName() {
    return name;
  }
}
