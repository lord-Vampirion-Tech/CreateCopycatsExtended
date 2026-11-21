package net.ziftech.copycats_extended.utility;

import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableVec3;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;

import net.minecraft.core.Direction.Axis;

public class QuadTransformHelper {

  public static final double DEFAULT_OFFSET = 0.5;

  public static QuadTransform symmetricSlope(
    Axis axis,
    QuadSlope.QuadSlopeFunction func
  ) {
    return symmetricSlope(axis, func, DEFAULT_OFFSET);
  }

  public static QuadTransform symmetricSlope(
    Axis axis,
    QuadSlope.QuadSlopeFunction func,
    double offset
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
        double result = offset + (value - offset) * factor;

        switch (axis) {
          case X -> vertex.x = result;
          case Y -> vertex.y = result;
          case Z -> vertex.z = result;
        }
      }

      return true;
    };
  }

  public static QuadTransform roundCoordinates() {
    return (quad, sprite) -> {
      for (int i = 0; i < 4; i++) {
        MutableVec3 vertex = quad.vertices.get(i).xyz;

        vertex.x = roundEdge(vertex.x);
        vertex.y = roundEdge(vertex.y);
        vertex.z = roundEdge(vertex.z);
      }

      return true;
    };
  }

  private static double roundEdge(double value) {
    if (Math.abs(value) < 0.002) return 0.0;
    if (Math.abs(value - 1.0) < 0.002) return 1.0;

    return value;
  }

  public static MutableVec3.AsAngle X(double angle) {
    return new MutableVec3.AsAngle(angle, 0, 0);
  }

  public static MutableVec3.AsAngle Y(double angle) {
    return new MutableVec3.AsAngle(0, angle, 0);
  }

  public static MutableVec3.AsAngle Z(double angle) {
    return new MutableVec3.AsAngle(0, 0, angle);
  }

}
