//package net.ziftech.copycats_extended.block;
//
//import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
//import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
//import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
//import com.simibubi.create.content.contraptions.StructureTransform;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.item.context.BlockPlaceContext;
//import net.minecraft.world.level.BlockAndTintGetter;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//import net.minecraft.world.level.block.state.properties.DirectionProperty;
//import net.minecraft.world.level.block.state.properties.IntegerProperty;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.VoxelShape;
//import net.ziftech.copycats_extended.block.base.ModBlockPlacement;
//import net.ziftech.copycats_extended.client.model.CopycatPyramid_Geometry;
//import net.ziftech.copycats_extended.registry.ModBlockEntities;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//
//public class CopycatPyramid extends CCWaterloggedCopycatBlock {
//
//  public static final DirectionProperty FACING = BlockStateProperties.FACING;
//
//  public static final IntegerProperty TOP_POINT_X = IntegerProperty.create("top_point_x", 0, 8);
//  public static final IntegerProperty TOP_POINT_Y = IntegerProperty.create("top_point_y", 1, 8);
//  public static final IntegerProperty TOP_POINT_Z = IntegerProperty.create("top_point_z", 0, 8);
//
//
//  public CopycatPyramid(Properties properties) {
//    super(properties);
//
//    registerDefaultState(
//      defaultBlockState()
//        .setValue(FACING, Direction.UP)
//        .setValue(TOP_POINT_X, 4)
//        .setValue(TOP_POINT_Y, 8)
//        .setValue(TOP_POINT_Z, 4)
//    );
//  }
//
//  @Override
//  public BlockState transform(BlockState blockState, StructureTransform structureTransform) {
//    if (structureTransform.rotationAxis == null)
//      return blockState;
//
//    Direction facing = blockState.getValue(FACING);
//
//    return blockState.setValue(
//      FACING,
//      structureTransform.rotateFacing(facing)
//    );
//  }
//
//
//  @Override
//  protected void createBlockStateDefinition(
//    StateDefinition.Builder<Block, BlockState> builder
//  ) {
//    super.createBlockStateDefinition(builder);
//    builder.add(
//      FACING,
//      TOP_POINT_X,
//      TOP_POINT_Y,
//      TOP_POINT_Z
//    );
//  }
//
//  @Override
//  public BlockState getStateForPlacement(BlockPlaceContext context) {
//    BlockState state = super.getStateForPlacement(context);
//
//    return ModBlockPlacement.placeOnClickedFace(
//      context,
//      state,
//      FACING
//    );
//  }
//
//  @Override
//  public @NotNull VoxelShape getShape(
//    BlockState state,
//    @NotNull BlockGetter level,
//    @NotNull BlockPos pos,
//    @NotNull CollisionContext context
//  ) {
//    return CopycatPyramid_Geometry.createShape(
//      state.getValue(FACING),
//      state.getValue(TOP_POINT_X),
//      state.getValue(TOP_POINT_Y),
//      state.getValue(TOP_POINT_Z)
//    );
//  }
//
//  @Override
//  public @NotNull VoxelShape getCollisionShape(
//    @NotNull BlockState state,
//    @NotNull BlockGetter level,
//    @NotNull BlockPos pos,
//    @NotNull CollisionContext context
//  ) {
//    return getShape(state, level, pos, context);
//  }
//
//  @Override
//  public @NotNull Class<CCCopycatBlockEntity> getBlockEntityClass() {
//    return CCCopycatBlockEntity.class;
//  }
//
//  @Override
//  public @NotNull BlockEntityType<? extends CCCopycatBlockEntity> getBlockEntityType() {
//    return ModBlockEntities.COPYCAT_PYRAMID.get();
//  }
//
//  @Override
//  public boolean canConnectTexturesToward(
//    BlockAndTintGetter reader,
//    BlockPos fromPos,
//    BlockPos toPos,
//    BlockState state
//  ) {
//    if (!isCTEnabled(state, reader, fromPos))
//      return false;
//
//    return super.canConnectTexturesToward(reader, fromPos, toPos, state);
//  }
//
//  @Override
//  public boolean isIgnoredConnectivitySide(
//    BlockAndTintGetter reader,
//    BlockState state,
//    Direction face,
//    BlockPos fromPos,
//    @Nullable BlockPos toPos,
//    @Nullable BlockState toState
//  ) {
//    if (!isCTEnabled(state, reader, fromPos))
//      return true;
//
//    return super.isIgnoredConnectivitySide(
//      reader, state, face, fromPos, toPos, toState
//    );
//  }
//
//  public boolean supportsExternalFaceHiding(BlockState state) {
//    return true;
//  }
//
//  public boolean hidesNeighborFace(
//    BlockGetter level,
//    BlockPos pos,
//    BlockState state,
//    BlockState neighborState,
//    Direction dir
//  ) {
//    return ICopycatBlock.hidesNeighborFace(
//      level, pos, state, neighborState, dir
//    );
//  }
//}
