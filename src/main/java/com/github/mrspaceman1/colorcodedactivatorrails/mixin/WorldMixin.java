package com.github.mrspaceman1.colorcodedactivatorrails.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.mrspaceman1.colorcodedactivatorrails.ColorcodedDetectorRail.*;

@Mixin(World.class)
public abstract class WorldMixin {

    @Inject(at=@At("HEAD"), method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", cancellable = true)
    public void onSetBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir){
        colorcodedActivation((World) (Object) this, pos, state, flags, cir);
    }
}
