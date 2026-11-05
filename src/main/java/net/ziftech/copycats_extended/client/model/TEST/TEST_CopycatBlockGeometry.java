package net.ziftech.copycats_extended.client.model.TEST;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

public class TEST_CopycatBlockGeometry {

  public static void assemble(
    CopycatRenderContext context,
    AssemblyTransform transform
  ) {

    CopycatSlopeModelCore.assembleSlope(
      context,
      transform,
      0,
      16,
      false
    );

  }

//  public static void assemble(
//    CopycatRenderContext context,
//    AssemblyTransform transform
//  ) {
//    // Центральная колонна
//    context.assemblePiece(
//      transform,
//      vec3(6, 0, 6),
//      aabb(4, 16, 4),
//      cull(0)
//    );
//
//    // Горизонтальная часть X
//    context.assemblePiece(
//      transform,
//      vec3(0, 4, 6),
//      aabb(16, 4, 4),
//      cull(0)
//    );
//
//    // Горизонтальная часть Z
//    context.assemblePiece(
//      transform,
//      vec3(6, 8, 0),
//      aabb(4, 4, 16),
//      cull(0)
//    );
//
//    // Верхняя площадка
//    context.assemblePiece(
//      transform,
//      vec3(3, 12, 3),
//      aabb(10, 4, 10),
//      cull(0)
//    );
//  }
}
