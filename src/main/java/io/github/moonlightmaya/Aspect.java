package io.github.moonlightmaya;

import io.github.moonlightmaya.data.BaseStructures;
import io.github.moonlightmaya.model.AspectModelPart;
import io.github.moonlightmaya.model.WorldRootModelPart;
import io.github.moonlightmaya.texture.AspectTexture;
import io.github.moonlightmaya.util.AspectMatrixStack;
import io.github.moonlightmaya.vanilla.VanillaRenderer;
import net.minecraft.client.render.VertexConsumerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An entity can equip one or more Aspects, which will alter the entity's physical appearance.
 */
public class Aspect {

    //Variables temporarily public for testing
    public final AspectModelPart entityRoot;
    public final List<WorldRootModelPart> worldRoots;
    public final List<AspectTexture> textures;

    public final VanillaRenderer vanillaRenderer = new VanillaRenderer();

    private final UUID userId; //uuid of the entity using this aspect
    private final UUID aspectId; //uuid of this aspect itself

    public void renderEntity(VertexConsumerProvider vcp, AspectMatrixStack matrixStack) {
        matrixStack.multiply(vanillaRenderer.modelTransform);
        entityRoot.render(vcp, matrixStack);
    }

    public UUID getAspectId() {
        return aspectId;
    }

    /**
     * Render the world-parented parts
     */
    public void renderWorld(VertexConsumerProvider vcp, AspectMatrixStack matrixStack) {
        for (WorldRootModelPart worldRoot : worldRoots) {
            worldRoot.render(vcp, matrixStack);
        }
    }


    public Aspect(UUID user, BaseStructures.AspectStructure materials) {
        this.userId = user;
        this.aspectId = UUID.randomUUID();

        //Load textures first, needed for making model parts
        textures = new ArrayList<>();
        for (BaseStructures.Texture base : materials.textures()) {
            try {
                AspectTexture tex = new AspectTexture(this, base);
                tex.uploadIfNeeded(); //upload texture
                textures.add(tex);
            } catch (IOException e) {
                throw new RuntimeException("Error importing texture " + base.name() + "!");
            }
        }

        entityRoot = new AspectModelPart(materials.entityRoot(), this);
        worldRoots = new ArrayList<>(materials.worldRoots().size());

        for (BaseStructures.ModelPartStructure worldRoot : materials.worldRoots())
            worldRoots.add(new WorldRootModelPart(worldRoot, this));
    }

    /**
     * Destroy this object and free any native resources.
     */
    public void destroy() {

    }

}
