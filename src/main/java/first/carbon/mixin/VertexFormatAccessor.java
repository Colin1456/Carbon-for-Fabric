package first.carbon.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VertexFormat.class)
public interface VertexFormatAccessor {
    @Accessor("offsets")
    IntList carbon$offsets();
}
