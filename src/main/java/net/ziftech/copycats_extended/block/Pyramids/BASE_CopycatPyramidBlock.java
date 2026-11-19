
package net.ziftech.copycats_extended.block.Pyramids;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramid_Entity;
import net.ziftech.copycats_extended.block.base.ModBlockPlacement;
import net.ziftech.copycats_extended.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BASE_CopycatPyramidBlock extends CCWaterloggedCopycatBlock {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  protected BASE_CopycatPyramidBlock(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState().setValue(FACING, Direction.UP)
    );
  }

  public enum ShapeType {
    PYRAMID,
    CORNER,
    OPPOSITE,
    QUAD,
    TRIPLE
  }

  public abstract ShapeType getShapeType();

  protected int[] getPyramidData(BlockGetter level, BlockPos pos) {
    if (level.getBlockEntity(pos) instanceof CopycatPyramid_Entity entity) {
      return new int[] {
        entity.getTopPointX(),
        entity.getTopPointY(),
        entity.getTopPointZ()
      };
    }

    return new int[] {4, 8, 4};
  }

  @Override
  public BlockState transform(BlockState blockState, StructureTransform structureTransform) {
    if (structureTransform.rotationAxis == null)
      return blockState;

    return blockState.setValue(
      FACING,
      structureTransform.rotateFacing(blockState.getValue(FACING))
    );
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    return ModBlockPlacement.placeOnClickedFace(context, state, FACING);
  }

  @Override
  public abstract @NotNull VoxelShape getShape(
    BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  );

  @Override
  public @NotNull VoxelShape getCollisionShape(
    @NotNull BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return getShape(state, level, pos, context);
  }

  @Override
  public @NotNull Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public @NotNull BlockEntityType<? extends CCCopycatBlockEntity> getBlockEntityType() {
    return ModBlockEntities.COPYCAT_PYRAMID.get();
  }

  @Override
  public boolean canConnectTexturesToward(
    BlockAndTintGetter reader,
    BlockPos fromPos,
    BlockPos toPos,
    BlockState state
  ) {
    if (!isCTEnabled(state, reader, fromPos))
      return false;

    return super.canConnectTexturesToward(reader, fromPos, toPos, state);
  }

  @Override
  public boolean isIgnoredConnectivitySide(
    BlockAndTintGetter reader,
    BlockState state,
    Direction face,
    BlockPos fromPos,
    @Nullable BlockPos toPos,
    @Nullable BlockState toState
  ) {
    if (!isCTEnabled(state, reader, fromPos))
      return true;

    return super.isIgnoredConnectivitySide(
      reader, state, face, fromPos, toPos, toState
    );
  }

  @Override
  public boolean supportsExternalFaceHiding(BlockState state) {
    return true;
  }

  @Override
  public VoxelShape getOcclusionShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos
  ) {
    VoxelShape shape = getCollisionShape(
      state, level, pos, CollisionContext.empty()
    );

    BlockPos neighborPos = pos.relative(Direction.WEST);
    BlockState neighborState = level.getBlockState(neighborPos);

    VoxelShape ownFace = shape.getFaceShape(Direction.WEST);
    VoxelShape neighborFace = neighborState.getFaceOcclusionShape(
      level, neighborPos, Direction.EAST
    );

    System.out.println("=== PYRAMID OCCLUSION ===");
    System.out.println("Own WEST: " + ownFace.toAabbs());
    System.out.println("Neighbor EAST: " + neighborFace.toAabbs());

    return shape;
  }

  @Override
  public boolean hidesNeighborFace(
    BlockGetter level,
    BlockPos pos,
    BlockState state,
    BlockState neighborState,
    Direction dir
  ) {
    return ICopycatBlock.hidesNeighborFace(
      level, pos, state, neighborState, dir
    );
  }
}
