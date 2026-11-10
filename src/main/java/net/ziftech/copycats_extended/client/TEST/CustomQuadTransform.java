package net.ziftech.copycats_extended.client.TEST;

import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableQuad;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableVec3;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction.Axis;

public class CustomQuadTransform {

  public static QuadTransform symmetricSlope(
    Axis axis,
    QuadSlope.QuadSlopeFunction func
  ) {
    return (quad, sprite) -> {
      for (int i = 0; i < 4; i++) {
        MutableVec3 vertex = quad.vertices.get(i).xyz;

        double a;
        double b;
        double value;

        switch (axis) {
          case X -> {
            a = vertex.y * 16;
            b = vertex.z * 16;
            value = vertex.x;
          }
          case Y -> {
            a = vertex.x * 16;
            b = vertex.z * 16;
            value = vertex.y;
          }
          case Z -> {
            a = vertex.x * 16;
            b = vertex.y * 16;
            value = vertex.z;
          }
          default -> throw new IllegalStateException();
        }

        double factor = func.apply(a, b) / 16.0;
        double result = 0.5 + (value - 0.5) * factor;

        switch (axis) {
          case X -> vertex.x = result;
          case Y -> vertex.y = result;
          case Z -> vertex.z = result;
        }
      }

      return true;
    };
  }
}
