package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.CCShapes;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.WaterloggedMultiStateCopycatBlock;
import com.copycatsplus.copycats.utility.BlockFaceUtils;
import com.copycatsplus.copycats.utility.BlockUtils;
import com.copycatsplus.copycats.utility.InteractionUtils;
import com.simibubi.create.content.contraptions.StructureTransform;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Set;

import static com.copycatsplus.copycats.content.copycat.slab.CopycatSlabBlock.*;

import com.copycatsplus.copycats.utility.InteractionUtils;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.foundation.placement.PoleHelper;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import net.ziftech.copycats_extended.registry.ModBlocks;

import java.util.List;
import java.util.function.Predicate;

public class CopycatStackedHalfLayerSlopeBlock extends WaterloggedMultiStateCopycatBlock {

  public static final String TOP = "top";
  public static final String BOTTOM = "bottom";

  public static final BooleanProperty HAS_TOP = BooleanProperty.create("has_top");
  public static final BooleanProperty HAS_BOTTOM = BooleanProperty.create("has_bottom");

  public static final DirectionProperty TOP_FACING =
    DirectionProperty.create(
      "top_facing",
      Direction.NORTH,
      Direction.SOUTH,
      Direction.EAST,
      Direction.WEST
    );

  public static final DirectionProperty BOTTOM_FACING =
    DirectionProperty.create(
      "bottom_facing",
      Direction.NORTH,
      Direction.SOUTH,
      Direction.EAST,
      Direction.WEST
    );

  public static final EnumProperty<Half> TOP_HALF = EnumProperty.create("top_half", Half.class);
  public static final EnumProperty<Half> BOTTOM_HALF = EnumProperty.create("bottom_half", Half.class);

  public static final BooleanProperty TOP_WALL_MOUNTED = BooleanProperty.create("top_wall_mounted");
  public static final BooleanProperty BOTTOM_WALL_MOUNTED = BooleanProperty.create("bottom_wall_mounted");

  private static final Set<String> MATERIAL_PARTS = Set.of(TOP, BOTTOM);
  private static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new PlacementHelper());

  public CopycatStackedHalfLayerSlopeBlock(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState()
        .setValue(HAS_TOP, true)
        .setValue(HAS_BOTTOM, false)
        .setValue(TOP_FACING, Direction.NORTH)
        .setValue(BOTTOM_FACING, Direction.NORTH)
        .setValue(TOP_HALF, Half.BOTTOM)
        .setValue(BOTTOM_HALF, Half.BOTTOM)
        .setValue(TOP_WALL_MOUNTED, false)
        .setValue(BOTTOM_WALL_MOUNTED, false)
    );
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(
      builder.add(
        HAS_TOP,
        HAS_BOTTOM,
        TOP_FACING,
        BOTTOM_FACING,
        TOP_HALF,
        BOTTOM_HALF,
        TOP_WALL_MOUNTED,
        BOTTOM_WALL_MOUNTED
      )
    );
  }

  ///============================

  @Override
  public String defaultProperty() {
    return TOP;
  }

  @Override
  public Set<String> storageProperties() {
    return MATERIAL_PARTS;
  }

  @Override
  public ItemInteractionResult useItemOn(
    ItemStack stack,
    BlockState state,
    Level level,
    BlockPos pos,
    Player player,
    InteractionHand hand,
    BlockHitResult hitResult
  ) {
    // Shift + клик: пропускаем Placement Helper,
    // чтобы сработала логика объединения половин.
    if (player != null && player.isShiftKeyDown()) {
      return super.useItemOn(
        stack, state, level, pos, player, hand, hitResult
      );
    }

    return InteractionUtils.sequentialItem(
      () -> InteractionUtils.usePlacementHelper(
        PLACEMENT_HELPER_ID,
        stack,
        state,
        level,
        pos,
        player,
        hand,
        hitResult
      ),
      () -> super.useItemOn(
        stack, state, level, pos, player, hand, hitResult
      )
    );
  }

  private static class PlacementHelper extends PoleHelper<Direction> {

    private PlacementHelper() {
      super(
        state -> state.is(
          ModBlocks.COPYCAT_STACKED_HALF_LAYER_SLOPE.get()
        ),
        state -> {
          // Если верхняя часть существует, используем её
          // ориентацию. Иначе используем нижнюю.
          boolean top = state.getValue(HAS_TOP);

          boolean wallMounted = state.getValue(
            top ? TOP_WALL_MOUNTED : BOTTOM_WALL_MOUNTED
          );

          Direction facing = state.getValue(
            top ? TOP_FACING : BOTTOM_FACING
          );

          return wallMounted
            ? Direction.Axis.Y
            : facing.getClockWise().getAxis();
        },
        TOP_FACING
      );
    }

    @Override
    public Predicate<ItemStack> getItemPredicate() {
      return stack ->
        stack.getItem() instanceof BlockItem item
          && item.getBlock().getClass()
          == CopycatStackedHalfLayerSlopeBlock.class;
    }

    @Override
    public PlacementOffset getOffset(
      Player player,
      Level world,
      BlockState state,
      BlockPos pos,
      BlockHitResult ray
    ) {
      List<Direction> directions =
        IPlacementHelper.orderedByDistance(
          pos,
          ray.getLocation(),
          dir -> dir.getAxis() == axisFunction.apply(state)
        );

      for (Direction dir : directions) {
        int range =
          AllConfigs.server().equipment.placementAssistRange.get();

        if (player != null) {
          AttributeInstance reach =
            player.getAttribute(
              Attributes.BLOCK_INTERACTION_RANGE
            );

          if (reach != null
            && reach.hasModifier(
            ExtendoGripItem.singleRangeAttributeModifier.id()
          )) {
            range += 4;
          }
        }

        int poles = attachedPoles(world, pos, dir);

        if (poles >= range) {
          continue;
        }

        BlockPos newPos = pos.relative(dir, poles + 1);
        BlockState newState = world.getBlockState(newPos);

        if (!newState.canBeReplaced()) {
          continue;
        }

        return PlacementOffset.success(
          newPos,
          placementState -> copyShapeState(state, placementState)
        );
      }

      return PlacementOffset.fail();
    }

    private static BlockState copyShapeState(
      BlockState source,
      BlockState target
    ) {
      return target
        .setValue(HAS_TOP, source.getValue(HAS_TOP))
        .setValue(HAS_BOTTOM, source.getValue(HAS_BOTTOM))

        .setValue(TOP_FACING, source.getValue(TOP_FACING))
        .setValue(BOTTOM_FACING, source.getValue(BOTTOM_FACING))

        .setValue(TOP_HALF, source.getValue(TOP_HALF))
        .setValue(BOTTOM_HALF, source.getValue(BOTTOM_HALF))

        .setValue(
          TOP_WALL_MOUNTED,
          source.getValue(TOP_WALL_MOUNTED)
        )
        .setValue(
          BOTTOM_WALL_MOUNTED,
          source.getValue(BOTTOM_WALL_MOUNTED)
        );
    }
  }

  @Override
  public int getColorIndex(String property) {
    return switch (property) {
      case TOP -> 0;
      case BOTTOM -> 1;
      default -> -1;
    };
  }

  @Override
  public boolean partExists(
    BlockState state,
    String property
  ) {
    return switch (property) {
      case TOP -> state.getValue(HAS_TOP);
      case BOTTOM -> state.getValue(HAS_BOTTOM);
      default -> false;
    };
  }

  @Override
  public Vec3i vectorScale(BlockState state) {
    return new Vec3i(1, 2, 1);
  }

  @Override
  public Vec3i getVectorFromProperty(
    BlockState state,
    String property
  ) {
    return switch (property) {
      case TOP -> new Vec3i(0, 1, 0);
      case BOTTOM -> Vec3i.ZERO;
      default -> Vec3i.ZERO;
    };
  }

  @Override
  public String getPropertyFromInteraction(
    BlockState state,
    BlockGetter level,
    Vec3i hitLocation,
    BlockPos blockPos,
    Direction facing,
    Vec3 unscaledHit
  ) {
    return unscaledHit.y >= 0.5 ? TOP : BOTTOM;
  }

  @Override
  public VoxelShape getPartialFaceShape(
    BlockGetter level,
    BlockState state,
    String property,
    Direction face
  ) {
    if (!partExists(state, property)) {
      return Shapes.empty();
    }

    return BlockFaceUtils.getPartialFaceShape(
      level,
      state,
      property,
      face
    );
  }

  ///===========================

  @Override
  public BlockState getStateForPlacement(
    BlockPlaceContext context
  ) {
    BlockState existing =
      context.getLevel().getBlockState(context.getClickedPos());

    Vec3 localHit = context.getClickLocation()
      .subtract(Vec3.atLowerCornerOf(context.getClickedPos()));

    if (existing.is(this)) {
      /*
       * Тестовая логика объединения:
       * Shift выбирает свободную часть существующего блока.
       *
       * Обычная установка пока оставляет состояние без изменений,
       * чтобы не перезаписывать уже установленную часть.
       */
      if (!context.getPlayer().isShiftKeyDown()) {
        return null;
      }

      boolean useTop = localHit.y >= 0.5;

      if (useTop && !existing.getValue(HAS_TOP)) {
        return createPartState(
          existing,
          context,
          true
        );
      }

      if (!useTop && !existing.getValue(HAS_BOTTOM)) {
        return createPartState(
          existing,
          context,
          false
        );
      }

      // Если выбранная часть занята, пробуем вторую.
      if (useTop && !existing.getValue(HAS_BOTTOM)) {
        return createPartState(
          existing,
          context,
          false
        );
      }

      if (!useTop && !existing.getValue(HAS_TOP)) {
        return createPartState(
          existing,
          context,
          true
        );
      }

      return null;
    }

    BlockState state = super.getStateForPlacement(context);

    if (state == null) {
      return null;
    }

    Direction facing = context.getClickedFace().getAxis().isVertical()
      ? context.getHorizontalDirection()
      : context.getClickedFace().getOpposite();

    boolean top = localHit.y >= 0.5;

    state = state
      .setValue(HAS_TOP, top)
      .setValue(HAS_BOTTOM, !top);

    return setPartOrientation(
      state,
      context,
      top,
      facing
    );
  }

  private BlockState createPartState(
    BlockState existing,
    BlockPlaceContext context,
    boolean top
  ) {
    BlockState result = existing
      .setValue(top ? HAS_TOP : HAS_BOTTOM, true);

    Direction facing = context.getClickedFace().getAxis().isVertical()
      ? context.getHorizontalDirection()
      : context.getClickedFace().getOpposite();

    return setPartOrientation(
      result,
      context,
      top,
      facing
    );
  }

  private BlockState setPartOrientation(
    BlockState state,
    BlockPlaceContext context,
    boolean top,
    Direction facing
  ) {
    Vec3 localHit = context.getClickLocation()
      .subtract(Vec3.atLowerCornerOf(context.getClickedPos()));

    Half half = localHit.y >= 0.5
      ? Half.TOP
      : Half.BOTTOM;

    if (top) {
      return state
        .setValue(TOP_FACING, facing.getAxis().isHorizontal()
          ? facing
          : context.getHorizontalDirection())
        .setValue(TOP_HALF, half)
        .setValue(TOP_WALL_MOUNTED,
          context.getClickedFace().getAxis().isHorizontal());
    }

    return state
      .setValue(BOTTOM_FACING, facing.getAxis().isHorizontal()
        ? facing
        : context.getHorizontalDirection())
      .setValue(BOTTOM_HALF, half)
      .setValue(BOTTOM_WALL_MOUNTED,
        context.getClickedFace().getAxis().isHorizontal());
  }

  ///=========================

  @Override
  public VoxelShape getShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    VoxelShape shape = Shapes.empty();

    if (state.getValue(HAS_TOP)) {
      shape = Shapes.joinUnoptimized(
        shape,
        getPartShape(state, true),
        BooleanOp.OR
      );
    }

    if (state.getValue(HAS_BOTTOM)) {
      shape = Shapes.joinUnoptimized(
        shape,
        getPartShape(state, false),
        BooleanOp.OR
      );
    }

    return shape.optimize();
  }

  private VoxelShape getPartShape(
    BlockState state,
    boolean top
  ) {
    Direction facing = state.getValue(
      top ? TOP_FACING : BOTTOM_FACING
    );

    Half half = state.getValue(
      top ? TOP_HALF : BOTTOM_HALF
    );

    boolean wallMounted = state.getValue(
      top ? TOP_WALL_MOUNTED : BOTTOM_WALL_MOUNTED
    );

    VoxelShape slope = wallMounted
      ? CCShapes.VERTICAL_SLOPE.get(facing).toShape()
      : CCShapes.SLOPE.get(facing).get(half).toShape();

    VoxelShape partBounds = top
      ? Block.box(0, 8, 0, 16, 16, 16)
      : Block.box(0, 0, 0, 16, 8, 16);

    return Shapes.joinUnoptimized(
      slope,
      partBounds,
      BooleanOp.AND
    ).optimize();
  }

  ///=======================

  @Override
  public BlockState transform(
    BlockState state,
    StructureTransform transform
  ) {
    return fromTransformableState(
      state,
      toTransformableState(state).transform(transform)
    );
  }

  @Override
  public void transformStorage(
    BlockState state,
    IMultiStateCopycatBlockEntity blockEntity,
    StructureTransform transform
  ) {
    state = fromTransformableState(
      state,
      toTransformableState(state).untransform(transform)
    );

    fromTransformableStorage(
      state,
      blockEntity,
      toTransformableStorage(state, blockEntity).transform(transform)
    );
  }

  @Override
  public boolean supportsExternalFaceHiding(BlockState state) {
    return true;
  }

  @Override
  public boolean hidesNeighborFace(
    BlockGetter level,
    BlockPos pos,
    BlockState state,
    BlockState neighborState,
    Direction direction
  ) {
    return ICopycatBlock.hidesNeighborFace(
      level,
      pos,
      state,
      neighborState,
      direction
    );
  }
}
