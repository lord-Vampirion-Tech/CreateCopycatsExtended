
package net.ziftech.copycats_extended.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.network.CycleBlockPropertyPayload;
import net.ziftech.copycats_extended.registry.CopycatBlockPropertiesForKey;
import net.ziftech.copycats_extended.registry.ModItems;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import net.ziftech.copycats_extended.registry.CopycatBlockPropertiesForKey.KeyProperty;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public class CopycatKeyOverlay implements LayeredDraw.Layer {

  public static final CopycatKeyOverlay INSTANCE = new CopycatKeyOverlay();

  // Цвета текста
  private static final int TEXT_COLOR = 0xFFFFFFFF;
  private static final int SECONDARY_TEXT_COLOR = 0xFFB8CBE8;
  private static final int SELECTED_BORDER = 0xFFFFD45C;

  // Спрайты
  private static final ResourceLocation PANEL = texture("panel");
  private static final ResourceLocation PANEL_DARK = texture("panel_dark");

  private static final ResourceLocation CELL = texture("cell");
  private static final ResourceLocation CELL_DARK = texture("cell_dark");

  private static final ResourceLocation CELL_SELECTED = texture("cell_selected");

  // Размеры
  private static final int MIN_WIDTH = 150;
  private static final int PANEL_HEIGHT = 45;
  private static final int CELL_HEIGHT = 18;

  // Отступы
  private static final int PADDING = 5;
  private static final int CELL_PADDING = 5;
  private static final int CELL_GAP = 5;

  // Положение
  private static final int PANEL_Y_OFFSET = 100;
  private static final int TITLE_Y_OFFSET = 7;
  private static final int CELLS_Y_OFFSET = 23;
  private static final int CELL_TEXT_Y_OFFSET = 28;
  private static final int HINT_Y_OFFSET = 17;

  private static boolean leftAltDown;
  private static int selectedIndex;

  private static BlockPos lastTargetPos;
  private static Block lastTargetBlock;
  private static BlockState lastTargetState;
  private static List<KeyProperty> lastTargetProperties = List.of();

  private static ResourceLocation texture(String name) {
    return ResourceLocation.fromNamespaceAndPath(CopycatsExtended.MOD_ID, "copycat_key/" + name);
  }

  // Регистрация
  @SubscribeEvent
  public static void register(RegisterGuiLayersEvent event) {
    event.registerAbove(VanillaGuiLayers.HOTBAR, texture("overlay"), INSTANCE);
  }

  // Управление
  @SubscribeEvent
  public static void onKeyInput(InputEvent.Key event) {
    if (event.getKey() == GLFW.GLFW_KEY_LEFT_ALT) {
      leftAltDown = event.getAction() != GLFW.GLFW_RELEASE;
    }
  }

  @SubscribeEvent
  public static void onMouseScrolled(InputEvent.MouseScrollingEvent event) {
    if (!leftAltDown)
      return;

    List<KeyProperty> properties = getTargetProperties(Minecraft.getInstance());

    if (properties.isEmpty())
      return;

    double delta = event.getScrollDeltaY();

    if (delta == 0)
      return;

    selectedIndex = Math.floorMod(selectedIndex + (delta < 0 ? 1 : -1), properties.size());
    event.setCanceled(true);
  }

  // Отрисовка
  @Override
  public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
    Minecraft mc = Minecraft.getInstance();

    if (isOverlayAvailable(mc)) {
      leftAltDown = false;
      resetTarget();
      return;
    }

    if (!(mc.hitResult instanceof BlockHitResult hit)) {
      resetTarget();
      return;
    }

    BlockPos pos = hit.getBlockPos();
    assert mc.level != null;
    BlockState state = mc.level.getBlockState(pos);

    List<KeyProperty> properties =
      CopycatBlockPropertiesForKey.getProperties(state.getBlock());

    if (properties.isEmpty()) {
      resetTarget();
      return;
    }

    updateTarget(pos, state, properties);
    selectedIndex = Math.floorMod(selectedIndex, lastTargetProperties.size());
    renderPanel(graphics, mc, pos, lastTargetState, lastTargetProperties);
  }

  private static boolean isOverlayAvailable(Minecraft mc) {
    return mc.player == null
      || mc.level == null
      || mc.options.hideGui
      || mc.screen != null
      || !mc.player.getMainHandItem().is(ModItems.COPYCAT_KEY.get());
  }

  private static List<KeyProperty> getTargetProperties(Minecraft mc) {
    if (isOverlayAvailable(mc))
      return List.of();

    if (!(mc.hitResult instanceof BlockHitResult hit))
      return List.of();

    BlockPos pos = hit.getBlockPos();
    assert mc.level != null;
    BlockState state = mc.level.getBlockState(pos);

    List<KeyProperty> properties =
      CopycatBlockPropertiesForKey.getProperties(state.getBlock());

    if (!properties.isEmpty())
      updateTarget(pos, state, properties);

    return properties;
  }

  private static void renderPanel(
    GuiGraphics graphics,
    Minecraft mc,
    BlockPos pos,
    BlockState state,
    List<KeyProperty> properties
  ) {
    int screenWidth = mc.getWindow().getGuiScaledWidth();
    int screenHeight = mc.getWindow().getGuiScaledHeight();
    int y = screenHeight - PANEL_Y_OFFSET;

    // Подготовка текста и расчёт ширины
    List<String> propertyTexts = new ArrayList<>(properties.size());
    int contentWidth = 0;

    for (KeyProperty property : properties) {
      String text = CopycatBlockPropertiesForKey.getPropertyText(
        mc.level,
        pos,
        state,
        property
      );

      propertyTexts.add(text);
      contentWidth += mc.font.width(text) + CELL_PADDING * 2;
    }

    contentWidth += Math.max(0, properties.size() - 1) * CELL_GAP;

    int width = Math.max(MIN_WIDTH, contentWidth + PADDING * 2);
    int x = (screenWidth - width) / 2;

    // Фон панели
    drawSprite(graphics, leftAltDown ? PANEL : PANEL_DARK, x, y, width, PANEL_HEIGHT);

    // Заголовок
    String title = state.getBlock().getName().getString();

    graphics.drawCenteredString(
      mc.font,
      "[" + title.toUpperCase(java.util.Locale.ROOT) + "]",
      screenWidth / 2,
      y + TITLE_Y_OFFSET,
      TEXT_COLOR
    );

    // Ячейки свойств
    int cellX = x + PADDING;
    int cellY = y + CELLS_Y_OFFSET;

    for (int i = 0; i < properties.size(); i++) {
      String text = propertyTexts.get(i);

      boolean selected = i == selectedIndex;
      int cellWidth = mc.font.width(text) + CELL_PADDING * 2;

      ResourceLocation cellSprite = selected
        ? CELL_SELECTED
        : leftAltDown ? CELL : CELL_DARK;

      drawSprite(graphics, cellSprite, cellX, cellY, cellWidth, CELL_HEIGHT);
      graphics.drawCenteredString(mc.font, text, cellX + cellWidth / 2, y + CELL_TEXT_Y_OFFSET, TEXT_COLOR);

      cellX += cellWidth + CELL_GAP;
    }

    // Подсказка
    String hint = leftAltDown ? "[SCROLL] to Cycle" : "Hold [LEFT ALT] to Focus";

    graphics.drawCenteredString(
      mc.font,
      hint,
      screenWidth / 2,
      y - HINT_Y_OFFSET,
      leftAltDown ? SELECTED_BORDER : SECONDARY_TEXT_COLOR
    );
  }

  private static void drawSprite(
    GuiGraphics graphics,
    ResourceLocation sprite,
    int x, int y,
    int width, int height
  ) {
    graphics.blitSprite(sprite, x, y, width, height);
  }

  // Целевой блок
  private static void updateTarget(
    BlockPos pos,
    BlockState state,
    List<KeyProperty> properties
  ) {
    Block block = state.getBlock();

    if (lastTargetBlock != block) {
      selectedIndex = 0;
    }

    lastTargetPos = pos.immutable();
    lastTargetBlock = block;
    lastTargetState = state;
    lastTargetProperties = List.copyOf(properties);
  }

  private static void resetTarget() {
    lastTargetPos = null;
    lastTargetBlock = null;
    lastTargetState = null;
    lastTargetProperties = List.of();
    selectedIndex = 0;
  }

  // Использование ключа
  public static void handleKeyUse(UseOnContext context) {
    Minecraft mc = Minecraft.getInstance();

    if (mc.player == null || mc.level == null)
      return;

    if (!mc.player.getMainHandItem().is(ModItems.COPYCAT_KEY.get()))
      return;

    BlockPos pos = context.getClickedPos();
    BlockState state = context.getLevel().getBlockState(pos);
    List<KeyProperty> properties =
      CopycatBlockPropertiesForKey.getProperties(state.getBlock());

    if (properties.isEmpty())
      return;

    updateTarget(pos, state, properties);
    selectedIndex = Math.floorMod(selectedIndex, properties.size());

    KeyProperty selectedProperty = properties.get(selectedIndex);

    boolean reverse =
      context.getPlayer() != null && context.getPlayer().isShiftKeyDown();

    PacketDistributor.sendToServer(
      new CycleBlockPropertyPayload(
        pos,
        selectedProperty.name(),
        reverse
      )
    );
  }
}
