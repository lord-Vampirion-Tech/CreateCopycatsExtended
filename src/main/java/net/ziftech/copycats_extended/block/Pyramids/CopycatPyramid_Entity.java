package net.ziftech.copycats_extended.block.Pyramids;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class CopycatPyramid_Entity extends CCCopycatBlockEntity {

  private int topPointX = 4;
  private int topPointY = 8;
  private int topPointZ = 4;

  public CopycatPyramid_Entity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COPYCAT_PYRAMID.get(), pos, state);
  }

  public int getTopPointX() {
    return topPointX;
  }

  public int getTopPointY() {
    return topPointY;
  }

  public int getTopPointZ() {
    return topPointZ;
  }

  public void setTopPoint(int x, int y, int z) {
    x = Mth.clamp(x, 0, 8);
    y = Mth.clamp(y, 1, 8);
    z = Mth.clamp(z, 0, 8);

    if (topPointX == x && topPointY == y && topPointZ == z)
      return;

    topPointX = x;
    topPointY = y;
    topPointZ = z;

    setChanged();
    notifyUpdate();
  }

  @Override
  public void read(
    CompoundTag tag,
    HolderLookup.Provider registries,
    boolean clientPacket
  ) {
    super.read(tag, registries, clientPacket);

    topPointX = Mth.clamp(tag.getInt("TopPointX"), 0, 8);
    topPointY = Mth.clamp(tag.getInt("TopPointY"), 1, 8);
    topPointZ = Mth.clamp(tag.getInt("TopPointZ"), 0, 8);
  }

  @Override
  public void writeSafe(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.writeSafe(tag, registries);

    tag.putInt("TopPointX", topPointX);
    tag.putInt("TopPointY", topPointY);
    tag.putInt("TopPointZ", topPointZ);
  }

  @Override
  public void write(
    CompoundTag tag,
    HolderLookup.Provider registries,
    boolean clientPacket
  ) {
    super.write(tag, registries, clientPacket);

    tag.putInt("TopPointX", topPointX);
    tag.putInt("TopPointY", topPointY);
    tag.putInt("TopPointZ", topPointZ);
  }

}
