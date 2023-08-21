package com.github.mrspaceman1.colorcodedactivatorrails.mixin;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.mrspaceman1.colorcodedactivatorrails.ColorcodedDetectorRail.checkInventoryContainsItemStacks;
import static com.github.mrspaceman1.colorcodedactivatorrails.ColorcodedDetectorRail.getCartsAtPos;
import static net.minecraft.block.DetectorRailBlock.POWERED;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow public abstract boolean setBlockState(BlockPos pos, BlockState state, int flags);

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Shadow @Nullable public abstract BlockEntity getBlockEntity(BlockPos pos);

    @Inject(at=@At("HEAD"), method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", cancellable = true)
    public void onSetBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir){
        if(!(state.getBlock() instanceof DetectorRailBlock detectorRailBlock)) return;
        if(!state.get(POWERED)) return;

        BlockPos configBarrelPos = pos.offset(Direction.DOWN, 2);
        if(!(this.getBlockState(configBarrelPos).getBlock() instanceof BarrelBlock configBarrelBlock)) return;
        BarrelBlockEntity barrelEntity = (BarrelBlockEntity) this.getBlockEntity(configBarrelPos);

        Set<ItemStack> barrelItemStackSet = new HashSet<>();
        for (int i=0; i<barrelEntity.size(); i++){
            ItemStack itemStack = barrelEntity.getStack(i);
            if(!itemStack.isEmpty())
                barrelItemStackSet.add(itemStack);
        }

        List<AbstractMinecartEntity> minecarts = getCartsAtPos((World) (Object) this, pos);
        for(AbstractMinecartEntity minecart : minecarts){
            if(!(minecart instanceof MinecartEntity)) continue;
            Optional<Entity> optionalPassenger = Optional.ofNullable(minecart.getFirstPassenger());

            if(optionalPassenger.isPresent() && optionalPassenger.get() instanceof PlayerEntity player){
                Set<ItemStack> playerItemStackSet = new HashSet<>();
                PlayerInventory inventory = player.getInventory();

                for(int i=0; i<inventory.size(); i++){
                    ItemStack itemStack = inventory.getStack(i);
                    if(!itemStack.isEmpty())
                        playerItemStackSet.add(itemStack);
                }

                if(checkInventoryContainsItemStacks(barrelItemStackSet, playerItemStackSet)) return;
            }
        }
        cir.setReturnValue(this.setBlockState(pos, state.with(POWERED, false), flags));
    }
}
