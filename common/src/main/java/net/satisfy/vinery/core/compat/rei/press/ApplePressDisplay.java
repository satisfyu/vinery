package net.satisfy.vinery.core.compat.rei.press;


import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.vinery.core.Vinery;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("all")
public class ApplePressDisplay extends BasicDisplay {

    public static final CategoryIdentifier<ApplePressDisplay> APPLE_PRESS_DISPLAY = CategoryIdentifier.of(Vinery.MOD_ID, "apple_press_display");
/*
    public ApplePressDisplay(ApplePressRecipe recipe) {
        this(Collections.singletonList(EntryIngredients.ofIngredient(recipe.input)), Collections.singletonList(EntryIngredients.of(recipe.getResultItem())), Optional.ofNullable(recipe.getId()));
    }
    */

    public ApplePressDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
        super(inputs, outputs, location);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return APPLE_PRESS_DISPLAY;
    }


}
