
package net.ziftech.copycats_extended.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

import net.neoforged.neoforge.common.data.LanguageProvider;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.registry.ModRegistryNames;

import static net.ziftech.copycats_extended.registry.ModRegistryNames.*;

public final class ModLanguageProvider extends LanguageProvider {

  private static final String RU_LOCALE = "ru_ru";
  private static final String EN_LOCALE = "en_us";

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
    generator.addProvider(includeClient,
      new ModLanguageProvider(output, EN_LOCALE));

    generator.addProvider(includeClient,
      new ModLanguageProvider(output, RU_LOCALE));
  }

  private record Translation(String locale, String value) {
  }

  private static Translation RU(String value) {
    return new Translation(RU_LOCALE, value);
  }

  private static Translation EN(String value) {
    return new Translation(EN_LOCALE, value);
  }

  private void add(String key, Translation... translations) {
    if (translations.length == 0)
      throw new IllegalArgumentException("At least one translation is required for key: " + key);

    boolean hasCurrentLocale = false;

    for (Translation translation : translations) {
      if (translation.locale().equals(locale)) {
        if (hasCurrentLocale)
          throw new IllegalArgumentException("Duplicate translation for locale '" + locale + "' and key: " + key);

        super.add(key, translation.value());
        hasCurrentLocale = true;
      }
    }
  }

  @Override
  protected void addTranslations() {

    //// Block's
    add(
      "block.copycats_extended." + BASE_BLOCK,
      RU("Тестовый Блок"),
      EN("Test Block")
    );

    // Pyramid's
    add(
      "block.copycats_extended." + COPYCAT_PYRAMID,
      RU("Пирамида"),
      EN("Pyramid")
    );

    add(
      "block.copycats_extended." + COPYCAT_PYRAMID_SIDE,
      RU("Боковая Пирамида"),
      EN("Pyramid Side")
    );

    add(
      "block.copycats_extended." + COPYCAT_PYRAMID_CORNER,
      RU("Угловая пирамида"),
      EN("Corner Pyramid")
    );

    add(
      "block.copycats_extended."  + COPYCAT_PYRAMID_OPPOSITE,
      RU("Двусторонняя пирамида"),
      EN("Opposite Pyramid")
    );

    add(
      "block.copycats_extended."  + COPYCAT_PYRAMID_QUAD,
      RU("Четырёхсторонняя пирамида"),
      EN("Four-Sided Pyramid")
    );

    add(
      "block.copycats_extended." + COPYCAT_PYRAMID_TRIPLE,
      RU("Трёхсторонняя пирамида"),
      EN("Three-Sided Pyramid")
    );

    add(
      "block.copycats_extended." + DOUBLE_COPYCAT,
      RU("Двойной Блок"),
      EN("Double Block")
    );

    // Item's
    add(
      "item.copycats_extended." + COPYCAT_KEY,
      RU("Ключ Copycat"),
      EN("Copycat Key")
    );

    // Characteristics
    add(
      "itemGroup.copycats_extended." + MOD_NAME_MENU,
      RU("Copycat+ | Расширенный"),
      EN("Copycats+ | Extended")
    );

//    add(
//      "tooltip.copycats_extended.characteristics.test_info.title",
//      RU("Тестовая информация"),
//      EN("Test Info")
//    );
//
//    add(
//      "tooltip.copycats_extended.characteristics.test_info.description",
//      RU("Это тестовая характеристика."),
//      EN("This is a test characteristic.")
//    );
//
//    add(
//      "tooltip.copycats_extended.characteristics.test_key.title",
//      RU("Тестовая клавиша"),
//      EN("Test Key")
//    );
//
//    add(
//      "tooltip.copycats_extended.characteristics.test_key.description",
//      RU("Нажмите _%s_, чтобы активировать функцию."),
//      EN("Press _%s_ to activate the feature.")
//    );
  }
}
