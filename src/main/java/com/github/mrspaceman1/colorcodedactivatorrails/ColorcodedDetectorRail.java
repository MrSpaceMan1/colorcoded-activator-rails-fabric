package com.github.mrspaceman1.colorcodedactivatorrails;

import net.minecraft.world.item.ItemStack;
import java.util.Set;

public class ColorcodedDetectorRail {
    public static boolean checkInventoryContainsItemStacks(Set<ItemStack> thisInventory, Set<ItemStack> thatInventory){
        for(ItemStack thisItemStack : thisInventory){
            for(ItemStack thatItemStack : thatInventory){
                if(ItemStack.matches(thisItemStack, thatItemStack))
                    return true;
            }
        }
        return false;
    }

}
