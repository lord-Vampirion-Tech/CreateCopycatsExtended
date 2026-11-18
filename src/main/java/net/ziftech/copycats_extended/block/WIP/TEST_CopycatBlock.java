//package net.ziftech.copycats_extended.block.WIP;
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
//import net.minecraft.world.level.block.state.properties.BooleanProperty;
//import net.minecraft.world.level.block.state.properties.DirectionProperty;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.VoxelShape;
//import net.ziftech.copycats_extended.block.base.ModBlockPlacement;
//import net.ziftech.copycats_extended.client.model.WIP.TEST.model.TEST_CopycatBlock_Geometry;
//import net.ziftech.copycats_extended.registry.ModBlockEntities;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.EnumMap;
//import java.util.Map;
//
//
//import org.jetbrains.annotations.Nullable;
//
//public class TEST_CopycatBlock extends CCWaterloggedCopycatBlock {
//
//  public static final BooleanProperty ENHANCED = BooleanProperty.create("enhanced");
//  public static final DirectionProperty FACING = BlockStateProperties.FACING;
//
//  private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
//
//  //public static final IntegerProperty TEST_VALUE_X = IntegerProperty.create("test_value_x", 0, 16);
//  //public static final IntegerProperty TEST_VALUE_Z = IntegerProperty.create("test_value_z", 0, 16);
//
//  static {
//    for (Direction direction : Direction.values()) {
//      SHAPES.put(direction, TEST_CopycatBlock_Geometry.createShape(direction));
//    }
//  }
//
//  public TEST_CopycatBlock(Properties properties) {
//    super(properties);
//
//    registerDefaultState(defaultBlockState()
//      .setValue(ENHANCED, false)
//      //.setValue(TEST_VALUE_X, 8)
//      //.setValue(TEST_VALUE_Z, 8)
//    );
//  }
//
//  @Override
//  public BlockState transform(BlockState blockState, StructureTransform structureTransform) {
//    return null;
//  }
//
//  @Override
//  protected void createBlockStateDefinition(
//    StateDefinition.Builder<Block, BlockState> builder
//  ) {
//    super.createBlockStateDefinition(
//      builder.add(FACING, ENHANCED/*, TEST_VALUE_X,TEST_VALUE_Z*/)
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
//    return SHAPES.get(state.getValue(FACING));
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
//    return ModBlockEntities.TEST_COPYCAT_BLOCK.get();
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
