package io.github.moonlightmaya.mixin.models;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerEntityModel.class)
public interface PlayerEntityModelAccessor {
    @Accessor ModelPart getCloak();
    @Accessor ModelPart getEar();
    @Accessor boolean getThinArms();
}
