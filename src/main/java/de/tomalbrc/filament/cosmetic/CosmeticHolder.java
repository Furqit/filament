package de.tomalbrc.filament.cosmetic;

import de.tomalbrc.filament.behaviour.item.Cosmetic;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CosmeticHolder extends ElementHolder {
    private final LivingEntity entity;
    private final DisplayElement displayElement;

    private double prevX = 0;
    private double prevZ = 0;

    private float bodyYaw;

    public CosmeticHolder(LivingEntity entity, ItemStack itemStack) {
        super();

        this.entity = entity;

        this.displayElement = new ItemDisplayElement(itemStack);

        Cosmetic.Config cosmeticData = CosmeticUtil.getCosmeticData(itemStack);
        if (cosmeticData != null) {
            this.displayElement.setTranslation(cosmeticData.translation);
            this.displayElement.setScale(cosmeticData.scale);
        }

        this.displayElement.setTeleportDuration(1);

        this.addElement(this.displayElement);
    }

    @Override
    public void onTick() {
        super.onTick();

        this.tickMovement(this.entity);
        this.displayElement.setYaw(this.bodyYaw);

        this.prevX = this.entity.getX();
        this.prevZ = this.entity.getZ();
    }

    private void tickMovement(final LivingEntity player) {
        float yaw = this.entity.getYRot();
        double i = player.getX() - this.prevX;
        double d = player.getZ() - this.prevZ;
        float f = (float)(i * i + d * d);
        float g = this.bodyYaw;
        if (f > 0.0025f) {
            // Using internal Mojang math utils here
            float l = (float) Mth.atan2(d, i) * Mth.RAD_TO_DEG - 90.0F;
            float m = Mth.abs(Mth.wrapDegrees(yaw) - l);
            if (95.f < m && m < 265.f) {
                g = l - 180.f;
            } else {
                g = l;
            }
        }

        this.turnBody(g, yaw);
    }

    public void turnBody(float bodyRotation, float yaw) {
        float f = Mth.wrapDegrees(bodyRotation - this.bodyYaw);
        this.bodyYaw += f * 0.3F;
        float g = Mth.wrapDegrees(yaw - this.bodyYaw);
        if (g < -75.0F) {
            g = -75.0F;
        }

        if (g >= 75.0F) {
            g = 75.0F;
        }

        this.bodyYaw = yaw - g;
        if (g * g > 2500.0F) {
            this.bodyYaw += g * 0.2F;
        }
    }

    @Override
    protected void notifyElementsOfPositionUpdate(Vec3 newPos, Vec3 delta) {
    }
    @Override
    protected void updateInitialPosition() {
        if (this.entity instanceof ServerPlayer serverPlayer) this.startWatching(serverPlayer);
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        var ret = super.startWatching(player);

        player.send(VirtualEntityUtils.createRidePacket(this.entity.getId(), this.displayElement.getEntityIds()));

        return ret;
    }
}
