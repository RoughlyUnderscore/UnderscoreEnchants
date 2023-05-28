package com.roughlyunderscore.enchs.config.messages.lang;

import com.roughlyunderscore.enchs.config.messages.MessageConfiguration;
import de.exlll.configlib.Configuration;

@SuppressWarnings("unused")
public class RussianMsgConfig extends MessageConfiguration {
  private MessageConfiguration.Language language = new MessageConfiguration.Language(
    "&cInsufficient permissions!",
    "&cYou can''t use this from console!",
    "&bCreating a log...",
    "&6A log has been created in &b/plugins/UnderscoreEnchants/logfile.log/ folder.",
    "&cCouldn''t create a log!",
    "&cCouldn''t enchant the item - unknown enchantment!",
    "&cCouldn''t enchant the item - its level is either higher than the maximum or less than 1!",
    "&6Enchanted your item with &b<name> <level>.",
    "&6You have activated the &l&b<name> enchantment.",
    "&cWrong parameter (<param>)!",
    "&bEnchantment &e<ench> &bhas been successfully downloaded.",
    "&bEnchantment &e<ench> &bhas been loaded.",
    "&bEnchantment &e<ench> &bhas been unloaded.",
    "&bToggled enchantment &e<ench> &bfor you.",
    "&cYou can''t have more than <limit> enchantments on an item!",
    "&6Reloaded the plugin in <ms> ms."
  );
}
