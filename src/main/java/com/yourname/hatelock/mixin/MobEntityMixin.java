package com.yourname.hatelock.mixin;

import com.yourname.hatelock.AggroHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {

	@Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
	private void onSetTarget(LivingEntity target, CallbackInfo ci) {

		if (!AggroHandler.isEnabled()) return;

		if (!(target instanceof ServerPlayerEntity player)) return;

		MobEntity self = (MobEntity)(Object)this;

		if (!AggroHandler.isAllowed(self, player)) {
			ci.cancel();
		}
	}
}