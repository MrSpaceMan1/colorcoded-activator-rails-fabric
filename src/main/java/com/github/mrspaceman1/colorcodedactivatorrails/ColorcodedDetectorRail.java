package com.github.mrspaceman1.colorcodedactivatorrails;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
