package com.roughlyunderscore.enchs.config.messages.lang;

import com.roughlyunderscore.enchs.config.messages.MessageConfiguration;
import de.exlll.configlib.Configuration;

@SuppressWarnings("unused")
public class SwedishMsgConfig extends MessageConfiguration {
  private MessageConfiguration.Language language = new MessageConfiguration.Language(
    "&cOtillräckliga behörigheter!",
    "&cDu kan inte använda detta från konsolen!",
    "&bSkapar en logg...",
    "&6En logg har skapats i &b/plugins/UnderscoreEnchants/logfile.log/ folder.",
    "&cKunde inte skapa en logg!",
    "&cKunde inte enchanta föremålet - okänd enchant!",
    "&cKunde inte enchanta föremålet - dess nivå är antingen högre än maxvärdet eller mindre än 1!",
    "&6Enchantade ditt föremål med &b<name> <level>.",
    "&6Du har aktivetat &l&b<name> enchanten.",
    "&cFel parameter (<param>)!",
    "&bEnchantment &e<ench> &bhas laddats ner framgångsrikt.",
    "&bEnchantment &e<ench> &bhar laddats.",
    "&bEnchantment &e<ench> &bhar avaktiverats.",
    "&bToggled enchantment &e<ench> &bfor you.",
    "&cYou can''t have more than <limit> enchantments on an item!",
    "&6Reloaded the plugin in <ms> ms."
  );
}
