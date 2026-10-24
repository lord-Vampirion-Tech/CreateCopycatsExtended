package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.content.copycat.slope_layer.CopycatSlopeLayerBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockUtils;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.ziftech.copycats_extended.block.base.CopycatAdvBlockBase;
import net.ziftech.copycats_extended.client.model.CopycatAdvSlopeLayerGeometry;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class CopycatAdvSlopeLayerBlock extends CopycatSlopeLayerBlock {

  public static final EnumProperty<WallOrientation> WALL_ORIENTATION =
    EnumProperty.create("wall_orientation", WallOrientation.class);

  public enum WallOrientation implements StringRepresentable {
    NORMAL("normal"),
    UP("up"),
    DOWN("down"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    WallOrientation(String name) {
      this.name = name;
    }

    @Override
    public String getSerializedName() {
      return name;
    }

    public boolean isWallMounted() {
      return this != NORMAL;
    }

    public boolean isSideways() {
      return this == LEFT || this == RIGHT;
    }
  }

  public CopycatAdvSlopeLayerBlock(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState()
        .setValue(FACING, Direction.NORTH)
        .setValue(HALF, Half.BOTTOM)
        .setValue(LAYERS, 1)
        .setValue(WALL_ORIENTATION, WallOrientation.NORMAL)
    );
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(
      builder.add(WALL_ORIENTATION)
    );
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState placement = super.getStateForPlacement(context);

    if (placement == null) {
      return null;
    }

    return CopycatAdvBlockBase.getLayerPlacement(
      context,
      placement,
      this,
      FACING,
      HALF,
      WALL_ORIENTATION
    );
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean canBeReplaced(
    BlockState state,
    BlockPlaceContext context
  ) {
    if (context.isSecondaryUseActive()) {
      return false;
    }

    if (!context.getItemInHand().is(asItem())) {
      return false;
    }

    return state.getValue(LAYERS) < 8;
  }

  @Override
  public VoxelShape getShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    WallOrientation orientation = state.getValue(WALL_ORIENTATION);

    if (!orientation.isWallMounted()) {
      return super.getShape(state, level, pos, context);
    }

    return CopycatAdvSlopeLayerGeometry.createWallShape(
      state.getValue(FACING),
      state.getValue(HALF),
      state.getValue(LAYERS),
      orientation
    );
  }

  @Override
  public VoxelShape getCollisionShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return getShape(state, level, pos, context);
  }

  @Override
  public boolean supportsExternalFaceHiding(BlockState state) {
    return !state.getValue(WALL_ORIENTATION).isWallMounted();
  }

  @Override
  public boolean hidesNeighborFace(
    BlockGetter level,
    BlockPos pos,
    BlockState state,
    BlockState neighborState,
    Direction direction
  ) {
    if (state.getValue(WALL_ORIENTATION).isWallMounted()) {
      return false;
    }

    return ICopycatBlock.hidesNeighborFace(
      level,
      pos,
      state,
      neighborState,
      direction
    );
  }

  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return true;
  }

  @Override
  public float getShadeBrightness(
    BlockState state,
    BlockGetter level,
    BlockPos pos
  ) {
    return state.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
  }

  @Override
  public BlockState transform(
    BlockState state,
    StructureTransform transform
  ) {
    WallOrientation orientation = state.getValue(WALL_ORIENTATION);

    if (!orientation.isWallMounted()) {
      return BlockUtils.transformStepLikeHorizontal(
        state,
        transform,
        defaultBlockState()
      );
    }

    BlockState transformed = BlockUtils.transformStepLikeVertical(
      state,
      transform,
      defaultBlockState()
    );

    return transformed
      .setValue(WALL_ORIENTATION, orientation)
      .setValue(LAYERS, state.getValue(LAYERS))
      .setValue(HALF, state.getValue(HALF));
  }

  @Override
  public Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends CCCopycatBlockEntity>
  getBlockEntityType() {
    return ModBlockEntities.COPYCAT_ADV_SLOPE_LAYER.get();
  }
}