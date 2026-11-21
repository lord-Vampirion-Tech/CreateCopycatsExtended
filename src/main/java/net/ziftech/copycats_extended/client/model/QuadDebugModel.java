
package net.ziftech.copycats_extended.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuadDebugModel extends BakedModelWrapper<BakedModel> {

  private static final int VERTEX_STRIDE =
    DefaultVertexFormat.BLOCK.getVertexSize() / Integer.BYTES;

  private static final Set<String> LOGGED = ConcurrentHashMap.newKeySet();

  public QuadDebugModel(BakedModel originalModel) {
    super(originalModel);
  }

  @Override
  public List<BakedQuad> getQuads(
    @Nullable BlockState state,
    @Nullable Direction side,
    RandomSource random
  ) {
    return debugQuads(
      super.getQuads(state, side, random),
      state,
      side
    );
  }

  @Override
  public List<BakedQuad> getQuads(
    @Nullable BlockState state,
    @Nullable Direction side,
    RandomSource random,
    ModelData data,
    @Nullable RenderType renderType
  ) {
    return debugQuads(
      super.getQuads(state, side, random, data, renderType),
      state,
      side
    );
  }

  private static List<BakedQuad> debugQuads(
    List<BakedQuad> quads,
    @Nullable BlockState state,
    @Nullable Direction side
  ) {
    for (BakedQuad quad : quads) {
      int[] vertices = quad.getVertices();

      StringBuilder key = new StringBuilder();
      StringBuilder output = new StringBuilder();

      output.append("\n========== QUAD ==========\n");
      output.append("State: ").append(state).append('\n');
      output.append("Side: ").append(side).append('\n');
      output.append("Direction: ").append(quad.getDirection()).append('\n');

      for (int i = 0; i < 4; i++) {
        int offset = i * VERTEX_STRIDE;

        float x = Float.intBitsToFloat(vertices[offset]);
        float y = Float.intBitsToFloat(vertices[offset + 1]);
        float z = Float.intBitsToFloat(vertices[offset + 2]);

        key.append(Float.floatToRawIntBits(x)).append(',');
        key.append(Float.floatToRawIntBits(y)).append(',');
        key.append(Float.floatToRawIntBits(z)).append(';');

        output.append("Vertex ").append(i + 1)
          .append(": X=").append(x)
          .append(" Y=").append(y)
          .append(" Z=").append(z)
          .append('\n');
      }

      key.append(state).append(quad.getDirection());

      if (LOGGED.add(key.toString())) {
        System.out.println(output);
      }
    }

    return quads;
  }
}
