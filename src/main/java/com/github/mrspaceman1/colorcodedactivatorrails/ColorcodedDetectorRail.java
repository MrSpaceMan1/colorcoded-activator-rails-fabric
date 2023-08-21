package com.github.mrspaceman1.colorcodedactivatorrails;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.block.DetectorRailBlock.POWERED;

public class ColorcodedDetectorRail {

    public static Box getCartBox(BlockPos pos){
        return new Box(
                (double)pos.getX() + 0.2,
                (double) pos.getY(),
                (double)pos.getZ() + 0.2,
                (double)(pos.getX() + 1) - 0.2,
                (double)(pos.getY() + 1) - 0.2,
                (double)(pos.getZ() + 1) - 0.2);

    }

    public static List<AbstractMinecartEntity> getCartsAtPos(World world, BlockPos pos){
        return world.getEntitiesByType(
                TypeFilter.instanceOf(AbstractMinecartEntity.class),
                getCartBox(pos),
                (entity) -> true
        );
    }

    public static boolean checkInventoryContainsItemStacks(Set<ItemStack> thisInventory, Set<ItemStack> thatInventory){
        for(ItemStack thisItemStack : thisInventory){
            for(ItemStack thatItemStack : thatInventory){
                if(ItemStack.areEqual(thisItemStack, thatItemStack))
                    return true;
            }
        }
        return false;
    }

    public static void colorcodedActivation(World world, BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir){
        if(!(state.getBlock() instanceof DetectorRailBlock detectorRailBlock)) return;
        if(!state.get(POWERED)) return;

        BlockPos configBarrelPos = pos.offset(Direction.DOWN, 2);
        if(!(world.getBlockState(configBarrelPos).getBlock() instanceof BarrelBlock configBarrelBlock)) return;
        BarrelBlockEntity barrelEntity = (BarrelBlockEntity) world.getBlockEntity(configBarrelPos);

        Set<ItemStack> barrelItemStackSet = new HashSet<>();
        for (int i=0; i<barrelEntity.size(); i++){
            ItemStack itemStack = barrelEntity.getStack(i);
            if(!itemStack.isEmpty())
                barrelItemStackSet.add(itemStack);
        }

        List<AbstractMinecartEntity> minecarts = getCartsAtPos((World) (Object) world, pos);
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
        cir.setReturnValue(world.setBlockState(pos, state.with(POWERED, false), flags));
    }
}
