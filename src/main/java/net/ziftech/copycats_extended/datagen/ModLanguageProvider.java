package net.ziftech.copycats_extended.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

import net.neoforged.neoforge.common.data.LanguageProvider;

import net.ziftech.copycats_extended.CopycatsExtended;

public final class ModLanguageProvider extends LanguageProvider {

  private final String locale;

  private ModLanguageProvider(
    PackOutput output,
    String locale
  ) {
    super(output, CopycatsExtended.MOD_ID, locale);
    this.locale = locale;
  }

  public static void register(
    DataGenerator generator,
    PackOutput output,
    boolean includeClient
  ) {
    generator.addProvider(includeClient, new ModLanguageProvider(output, "en_us"));
    generator.addProvider(includeClient, new ModLanguageProvider(output, "ru_ru"));
  }

  @Override
  protected void addTranslations() {
    if (locale.equals("en_us")) {
      addEnglish();
    } else if (locale.equals("ru_ru")) {
      addRussian();
    }
  }

  private void addEnglish() {
    // Block names
    add("block.copycats_extended.base_block",
      "Base Block");

    add("block.copycats_extended.copycat_adv_slope",
      "Advanced Copycat Slope");

    add("block.copycats_extended.copycat_adv_slope_layer",
      "Advanced Copycat Slope Layer");

    add("block.copycats_extended.TEST_copycat_block",
      "Test Copycat Block");

    // Characteristics
    add("tooltip.copycats_extended.characteristics.test_info.title",
      "Test Info");

    add("tooltip.copycats_extended.characteristics.test_info.description",
      "This is a test characteristic.");

    add("tooltip.copycats_extended.characteristics.test_key.title",
      "Test Key");

    add("tooltip.copycats_extended.characteristics.test_key.description",
      "Press _%s_ to activate the feature.");
  }

  private void addRussian() {
    // Block names
    add("block.copycats_extended.base_block",
      "Базовый Блок");

    add("block.copycats_extended.copycat_adv_slope",
      "Продвинутый скос");

    add("block.copycats_extended.copycat_adv_slope_layer",
      "Продвинутый слой скоса");

    add("block.copycats_extended.TEST_copycat_block",
      "Тестовый копикат-блок");

    // Characteristics
    add("tooltip.copycats_extended.characteristics.test_info.title",
      "Тестовая информация");

    add("tooltip.copycats_extended.characteristics.test_info.description",
      "Это тестовая характеристика.");

    add("tooltip.copycats_extended.characteristics.test_key.title",
      "Тестовая клавиша");

    add("tooltip.copycats_extended.characteristics.test_key.description",
      "Нажмите _%s_, чтобы активировать функцию.");
  }
}
