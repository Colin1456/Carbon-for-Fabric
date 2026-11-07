package first.carbon.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import first.carbon.Carbon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    private Iterable<Entity> imo$entities(ClientLevel level, Operation<Iterable<Entity>> original) {
        Iterable<Entity> entities = original.call(level);
        return Carbon.CONFIG.optimizeDroppedItems.get() ? Carbon.Client.DROPPED_RENDERER.collect(entities) : entities;
    }
}
