package first.carbon.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// 1.17.1 无 SpriteContents，精灵原始图像存于 TextureAtlasSprite.mainImage。
@Mixin(TextureAtlasSprite.class)
public interface SpriteContentsAccessor {
    @Accessor("mainImage")
    NativeImage[] carbon$image();
}
