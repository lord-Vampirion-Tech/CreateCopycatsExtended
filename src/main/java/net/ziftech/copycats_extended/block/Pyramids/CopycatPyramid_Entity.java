package net.ziftech.copycats_extended.block.Pyramids;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.registry.ModBlockEntities;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.core.Direction.*;

public class CopycatPyramid_Entity extends CCCopycatBlockEntity
  implements TransformableBlockEntity {

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

    if (level == null)
      return;

    if (!level.isClientSide()) {
      notifyUpdate();
    } else {
      redraw();
    }
  }

  private void redraw() {
    if (level == null)
      return;

    requestModelDataUpdate();

    level.sendBlockUpdated(
      worldPosition,
      getBlockState(),
      getBlockState(),
      16
    );
  }

  @Override
  public void read(
    @NotNull CompoundTag tag,
    HolderLookup.@NotNull Provider registries,
    boolean clientPacket
  ) {
    super.read(tag, registries, clientPacket);

    int oldX = topPointX;
    int oldY = topPointY;
    int oldZ = topPointZ;

    topPointX = Mth.clamp(tag.getInt("TopPointX"), 0, 8);
    topPointY = Mth.clamp(tag.getInt("TopPointY"), 1, 8);
    topPointZ = Mth.clamp(tag.getInt("TopPointZ"), 0, 8);

    if (clientPacket &&
      (oldX != topPointX || oldY != topPointY || oldZ != topPointZ)) {
      redraw();
    }
  }

  @Override
  public void transform(BlockEntity blockEntity, StructureTransform transform) {
    Direction newFacing = getBlockState().getValue(BASE_CopycatPyramidBlock.FACING);
    Direction oldFacing = newFacing;

    int reverseTurns = (4 - transform.rotation.ordinal()) % 4;

    for (int i = 0; i < reverseTurns; i++) {
      oldFacing = oldFacing.getClockWise(transform.rotationAxis);
    }

    if (transform.rotationAxis == Axis.Y) {
      System.out.println(
        "Rotate: Axis.Y"
      );

      if (oldFacing != UP && oldFacing != Direction.DOWN)
        return;

      Vec3 point = transform.applyWithoutOffset(
        new Vec3(topPointX / 8.0, 0.5, topPointZ / 8.0)
      );

      int x = Mth.clamp((int) Math.round(point.x * 8), 0, 8);
      int z = Mth.clamp((int) Math.round(point.z * 8), 0, 8);

      setTopPoint(x, topPointY, z);
    }

    if (transform.rotationAxis == Axis.Z) {
      System.out.println("Rotate: Axis.Z");

      Vec3 point = new Vec3(
        topPointX / 8.0,
        topPointY / 8.0,
        topPointZ / 8.0
      );

      Vec3 worldPoint = rotatePoint(point, oldFacing);
      Vec3 transformedPoint = VecHelper.rotateCentered(
        worldPoint, transform.angle, Axis.Z
      );
      Vec3 newPoint = inverseRotatePoint(transformedPoint, newFacing);

      int resultX = Mth.clamp((int) Math.round(newPoint.x * 8), 0, 8);
      int resultZ = Mth.clamp((int) Math.round(newPoint.z * 8), 0, 8);

      if (isTransition(oldFacing, newFacing, UP, EAST)) {
        int temp = resultX;
        resultX = 8 - resultZ;
        resultZ = 8 - temp;

      } else if (isTransition(oldFacing, newFacing, UP, WEST)) {
        int temp = resultX;
        resultX = resultZ;
        resultZ = temp;

      } else if ((oldFacing == WEST && newFacing == DOWN) || (oldFacing == DOWN && newFacing == EAST)
      ) {
        int temp = resultX;
        resultX = resultZ;
        resultZ = 8 - temp;

      } else if ((oldFacing == DOWN && newFacing == WEST) || (oldFacing == EAST && newFacing == DOWN)
      ) {
        int temp = resultX;
        resultX = 8 - resultZ;
        resultZ = temp;
      }

      setTopPoint(resultX, topPointY, resultZ);
      return;
    }

    if (transform.rotationAxis == Axis.X) {
      Vec3 point = new Vec3(topPointX / 8.0, topPointY / 8.0, topPointZ / 8.0);

      Vec3 worldPoint = rotatePoint(point, oldFacing);
      Vec3 transformedPoint = VecHelper.rotateCentered(worldPoint, -transform.angle, Axis.X);
      Vec3 newPoint = inverseRotatePoint(transformedPoint, newFacing);

      boolean reflectX =
        isTransition(oldFacing, newFacing, NORTH, DOWN) ||
          isTransition(oldFacing, newFacing, NORTH, UP) ||
          isTransition(oldFacing, newFacing, SOUTH, NORTH);

      boolean reflectZ =
        isTransition(oldFacing, newFacing, UP, SOUTH) ||
          isTransition(oldFacing, newFacing, NORTH, DOWN) ||
          isTransition(oldFacing, newFacing, UP, DOWN) ||
          isTransition(oldFacing, newFacing, SOUTH, NORTH);

      double x = reflectX ? 1 - newPoint.x : newPoint.x;

      int resultX = Mth.clamp((int) Math.round(x * 8), 0, 8);

      int resultZ = reflectZ
        ? 8 - topPointZ
        : oldFacing == Direction.DOWN && newFacing == Direction.SOUTH
        ? topPointZ
        : Mth.clamp((int) Math.round(newPoint.z * 8), 0, 8);

      setTopPoint(resultX, topPointY, resultZ);
      return;
    }


//    if (transform.rotationAxis == Axis.X) {
//      System.out.println("Rotate: Axis.X");
//
//      System.out.println(transform.rotation);
//
//
//      if (oldFacing == newFacing) {
//        System.out.println(transform.rotation);
//        Vec3 point = transform.applyWithoutOffset(
//          new Vec3(topPointX / 8.0, 0.5, topPointZ / 8.0)
//        );
//
//        int x = Mth.clamp((int) Math.round(point.x * 8), 0, 8);
//        int z = Mth.clamp((int) Math.round(point.z * 8), 0, 8);
//
//        setTopPoint(x, topPointY, z);
//        return;
//      }
//      int x = topPointX;
//      int z = topPointZ;
//
////      if (oldFacing == newFacing) {
////        boolean sameSide = (x >= 4) == (z >= 4);
////
////        boolean reflectX = false;
////        boolean reflectZ = false;
////
////        boolean reverseRotation = oldFacing == Direction.WEST;
////
////        if (transform.rotation == Rotation.CLOCKWISE_90) {
////          if (sameSide) {
////            if (reverseRotation)
////              reflectX = true;
////            else
////              reflectZ = true;
////          } else {
////            if (reverseRotation)
////              reflectZ = true;
////            else
////              reflectX = true;
////          }
////        }
////
////        if (transform.rotation == Rotation.COUNTERCLOCKWISE_90) {
////          if (sameSide) {
////            if (reverseRotation)
////              reflectZ = true;
////            else
////              reflectX = true;
////          } else {
////            if (reverseRotation)
////              reflectX = true;
////            else
////              reflectZ = true;
////          }
////        }
////
////        if (transform.rotation == Rotation.CLOCKWISE_180) {
////          reflectX = true;
////          reflectZ = true;
////        }
////
////        if (reflectX)
////          x = 8 - x;
////
////        if (reflectZ)
////          z = 8 - z;
////
////        setTopPoint(x, topPointY, z);
////        return;
////      }
//
//      boolean reflectX = false;
//      boolean reflectZ = false;
//
//      // Здесь экспериментируешь
//      if ((oldFacing == Direction.NORTH && newFacing == Direction.DOWN)
//        || (oldFacing == Direction.DOWN && newFacing == Direction.NORTH)
//        || (oldFacing == Direction.NORTH && newFacing == Direction.UP)
//        || (oldFacing == Direction.UP && newFacing == Direction.NORTH)) {
//        reflectX = true;
//      }
//
//      if ((oldFacing == Direction.UP && newFacing == Direction.SOUTH)
//        || (oldFacing == Direction.SOUTH && newFacing == Direction.UP)
//        || (oldFacing == Direction.DOWN && newFacing == Direction.NORTH)
//        || (oldFacing == Direction.NORTH && newFacing == Direction.DOWN)) {
//        reflectZ = true;
//      }
//
//
//      if (reflectX)
//        x = 8 - x;
//
//      if (reflectZ)
//        z = 8 - z;
//
//
//      setTopPoint(x, topPointY, z);
//    }


    System.out.println(
      "old=" + oldFacing + " new=" + newFacing +
        "\n X=" + topPointX + " Z=" + topPointZ +
        "\n rotation=" + transform.rotation
    );

  }

  private static boolean isTransition(
    Direction oldFacing,
    Direction newFacing,
    Direction first,
    Direction second
  ) {
    return (oldFacing == first && newFacing == second) ||
      (oldFacing == second && newFacing == first);
  }

  private static Vec3 rotatePoint(Vec3 point, Direction facing) {
    double x = point.x;
    double y = point.y;
    double z = point.z;

    return switch (facing) {
      case UP -> new Vec3(x, y, z);
      case DOWN -> new Vec3(x, 1 - y, 1 - z);
      case NORTH -> new Vec3(x, 1 - z, y);
      case SOUTH -> new Vec3(x, z, 1 - y);
      case WEST -> new Vec3(1 - y, x, z);
      case EAST -> new Vec3(y, 1 - x, z);
    };
  }

  private static Vec3 inverseRotatePoint(Vec3 point, Direction facing) {
    double x = point.x;
    double y = point.y;
    double z = point.z;

    return switch (facing) {
      case UP -> new Vec3(x, y, z);
      case DOWN -> new Vec3(x, 1 - y, 1 - z);
      case NORTH -> new Vec3(x, z, 1 - y);
      case SOUTH -> new Vec3(x, 1 - z, y);
      case WEST -> new Vec3(y, 1 - x, z);
      case EAST -> new Vec3(1 - y, x, z);
    };
  }

//  private int[] transformXZ(
//    Direction oldFacing,
//    Direction newFacing,
//    Axis axis,
//    int turns
//  ) {
//    turns = Math.floorMod(turns, 4);
//
//    if (turns == 0)
//      return new int[] {topPointX, topPointZ};
//
//    boolean fixedFacing;
//    boolean positiveDirection;
//
//    if (axis == Axis.Z) {
//      fixedFacing = oldFacing.getAxis() == Axis.Z;
//      positiveDirection = oldFacing == Direction.NORTH;
//    } else {
//      fixedFacing = oldFacing.getAxis() == Axis.X;
//      positiveDirection = oldFacing == Direction.WEST;
//    }
//
//    if (!fixedFacing) {
//      if (axis == Axis.Z) {
//        return switch (newFacing) {
//          case UP, WEST, EAST -> new int[] {8, 8};
//          case DOWN -> new int[] {0, 0};
//          default -> new int[] {topPointX, topPointZ};
//        };
//      }
//
//      return switch (newFacing) {
//        case UP, SOUTH, DOWN, NORTH -> new int[] {8, 8};
//        default -> new int[] {topPointX, topPointZ};
//      };
//    }
//
//    boolean clockwise = turns == 1;
//    boolean counterClockwise = turns == 3;
//
//    boolean usePositive =
//      (positiveDirection && clockwise) ||
//        (!positiveDirection && counterClockwise);
//
//    int effectiveTurns = usePositive
//      ? turns
//      : (4 - turns) % 4;
//
//    return switch (effectiveTurns) {
//      case 0 -> new int[] {8, 8};
//      case 1 -> new int[] {0, 8};
//      case 2 -> new int[] {0, 0};
//      case 3 -> new int[] {8, 0};
//      default -> new int[] {topPointX, topPointZ};
//    };
//  }
//
//  private static Vec3 rotatePoint(Vec3 point, Direction facing) {
//    double x = point.x;
//    double y = point.y;
//    double z = point.z;
//
//    return switch (facing) {
//      case UP -> new Vec3(x, y, z);
//      case DOWN -> new Vec3(x, 1 - y, 1 - z);
//      case NORTH -> new Vec3(x, 1 - z, y);
//      case SOUTH -> new Vec3(x, z, 1 - y);
//      case WEST -> new Vec3(1 - y, x, z);
//      case EAST -> new Vec3(y, 1 - x, z);
//    };
//  }

//  private static Vec3 inverseRotatePoint(Vec3 point, Direction facing) {
//    double x = point.x;
//    double y = point.y;
//    double z = point.z;
//
//    return switch (facing) {
//      case UP -> new Vec3(x, y, z);
//      case DOWN -> new Vec3(x, 1 - y, 1 - z);
//      case NORTH -> new Vec3(x, z, 1 - y);
//      case SOUTH -> new Vec3(x, 1 - y, z);
//      case WEST -> new Vec3(y, 1 - x, z);
//      case EAST -> new Vec3(1 - y, x, z);
//    };
//  }


  @Override
  public void writeSafe(
    @NotNull CompoundTag tag,
    HolderLookup.@NotNull Provider registries
  ) {
    super.writeSafe(tag, registries);

    tag.putInt("TopPointX", topPointX);
    tag.putInt("TopPointY", topPointY);
    tag.putInt("TopPointZ", topPointZ);
  }

  @Override
  public void write(
    @NotNull CompoundTag tag,
    HolderLookup.@NotNull Provider registries,
    boolean clientPacket
  ) {
    super.write(tag, registries, clientPacket);

    tag.putInt("TopPointX", topPointX);
    tag.putInt("TopPointY", topPointY);
    tag.putInt("TopPointZ", topPointZ);
  }

}
