/*
 * @file JEIPlugin.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * JEI plugin (see https://github.com/mezz/JustEnoughItems/wiki/Creating-Plugins)
 */
package wile.engineersdecor.eapi.jei;

//public class JEIPlugin {}


import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import wile.engineersdecor.ModConfig;
import wile.engineersdecor.ModContent;
import wile.engineersdecor.ModEngineersDecor;
import wile.engineersdecor.libmc.Auxiliaries;
import wile.engineersdecor.libmc.Registries;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@mezz.jei.api.JeiPlugin
public class JEIPlugin implements mezz.jei.api.IModPlugin
{
  @Override
  public @NotNull ResourceLocation getPluginUid()
  { return new ResourceLocation(ModEngineersDecor.MODID, "jei_plugin_uid"); }

  @Override
  public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration)
  {
  }

  @Override
  public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime)
  {
      HashSet<Item> blacklisted = new HashSet<>();
      for(Block e: Registries.getRegisteredBlocks()) {
          if(ModConfig.isOptedOut(e) && (ForgeRegistries.ITEMS.getKey(e.asItem()).getPath()).equals((ForgeRegistries.BLOCKS.getKey(e).getPath()))) {
              blacklisted.add(e.asItem());
          }
      }
      for(Item e: Registries.getRegisteredItems()) {
          if(ModConfig.isOptedOut(e) && (!(e instanceof BlockItem))) {
              blacklisted.add(e);
          }
      }
      if(!blacklisted.isEmpty()) {
          List<ItemStack> blacklist = blacklisted.stream().map(ItemStack::new).collect(Collectors.toList());
          try {
              jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, blacklist);
          } catch(Exception e) {
              Auxiliaries.logger().warn("Exception in JEI opt-out processing: '" + e.getMessage() + "', skipping further JEI optout processing.");
          }
      }
  }

  @Override
  public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration)
  {
    if(!ModConfig.isOptedOut(ModContent.getBlock("small_lab_furnace"))) {
      registration.addRecipeCatalyst(new ItemStack(ModContent.getBlock("small_lab_furnace")), RecipeTypes.SMELTING);
    }
    if(!ModConfig.isOptedOut(ModContent.getBlock("small_electrical_furnace"))) {
      registration.addRecipeCatalyst(new ItemStack(ModContent.getBlock("small_electrical_furnace")), RecipeTypes.SMELTING);
    }
  }
}
