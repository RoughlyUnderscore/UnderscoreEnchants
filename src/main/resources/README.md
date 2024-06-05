

# Guide to UnderscoreEnchants
Welcome to UnderscoreEnchants!

This plugin adds a bunch of new enchantments to the game, and allows you to customize them to your liking. In addition, it allows you to create your own enchantments, and add them to the game. Most importantly, the actions, conditions, triggers and other tokens of a custom enchantment are not confined to the hardcoded plugin limits. Using the UnderscoreEnchants API, you can create your own ones.

This README is a condensed version of the [GitHub Wiki](https://github.com/RoughlyUnderscore/UnderscoreEnchants) for this plugin. Make sure to check it out!

# Configuration

If this is your first time using UnderscoreEnchants, you should first get familiar with the configuration. Everything tweakable is stored in the `config.json` file. Up ahead is a review of each field.
- `config-version`
  You should not be modifying this field. It is used internally to track updates and add/redirect configuration fields if they are changed between updates.

#### Settings

- `generate-readme`
This field indicates whether you want to generate this README file if it is missing from the plugin folder. Set to `true` by default.
- `locale`
This field indicates the server-wide locale used by default. The locale names can be found in the `messages` folder - for example, if a locale file is called `en_US.json`, you're going to put `en_US` in this field. Set to `en_US` by default.
- `players-change-locales`
This field indicates whether players can change their own locale using `/ue lang`. There are practical reasons to disable this: for example, if your server has a centralized locale manager, you can disable this command and use the UnderscoreEnchants API to change their language when your locale manager requests that. Set to `true` by default.
- `include-default-locales`
This field indicates whether to generate the default locale files if they are missing from the locale folder. Every locale that is bundled with the plugin is considered a default locale. Set to `true` by default.
- `storage-medium`
This field indicates the preferred way of storing plugin data - as of 2.2, the only plugin data being stored is the player locales, and the disabled enchantments. The acceptable values for this field are `YAML, JSON, MYSQL and MONGODB`, case-insensitive. Set to `json` by default.
- `storage-saving-period-ticks`
This field indicates the interval between saving data to the storage medium. The value is specified in ticks (1 second = 20 ticks). Set to `900` by default.
- `notify-players-of-data-loading`
Upon joining the server, a player should have their data loaded, if it is not loaded yet. This field specifies whether to send messages to the player, like "Your data is being loaded, please wait" & "Your data has been loaded successfully". Set to `true` by default.
- `repository-url`
This field indicates the URL of the REST API server that this plugin uses to pull enchantments and enchantment packs. It is set to the official repository by default, but if you host your own repository, you can change this field to your own URL. Instructions on how to host your own repository and its source code can be found at [UnderscoreEnchantsBackend](https://github.com/RoughlyUnderscore/UnderscoreEnchantsBackend).
- `strip-server-metadata`
The packs and enchantments uploaded to the server store server-related metadata - that is, for example, the upload timestamp, and the amount of downloads. This field, set to `true` by default, indicates whether to strip this metadata from the enchantments and packs before sending them to the client. This is useful if you want to save bandwidth and storage space, but if you want to keep the metadata for whatever reason, you can set this field to `false`.

#### Misc

- `bStats`
  This field indicates whether the anonymous bStats metric collection should be enabled. This does not harm your performance or transmit any sensitive data. Set to `true` by default.
- `update-checker-frequency-hours`
  This field indicates the frequency (in hours) with which the update checker should check if there are any new updates for UnderscoreEnchants. Set this to -1 to disable the update checker altogether. Set to `24` by default.
- `notify-ops-about-updates`
  This field indicates whether to notify server operators about any new updates for UnderscoreEnchants - that is, if the update checker is enabled. Set to `true` by default.

#### Generation

- `chest-loot-chance`
  This field indicates the chance (in percents) to generate an enchanted item in a chest. Set to `14.2857142857149` (1/7) by default.
- `fishing-loot-chance`
  This field indicates the chance (in percents) to catch an enchanted item whilst fishing. Set to `14.2857142857149` (1/7) by default.
- `villager-item-trade-chance`
  This field indicates the chance (in percents) to generate an enchanted item in a villager trade. Set to `14.2857142857149` (1/7) by default.

#### Enchantments

- `limit`
  This field indicates the limit of enchantments for an item. Set to -1 to disable the limit. Set to `-1` by default.

# Messages

Now that you are comfortable with your configuration, let's move on to the messages folder and take a look at a snippet of `en_US.json`.
```json
{
  "messages": {
    "start-messages": {
      "word-tokens": {
        "started": "Started <action>.",
        "finished": "Finished <action>. Took <ms>ms."
      }
    },
    ...
  }
}
```
Every message file is in this format: all messages are in the `messages` section. Some messages have placeholders like `<player>` - they will be replaced with the relevant value when the message appears.

# Enchantments

It is difficult to describe about the structure of an enchantment in a brief way, but let's take a look at this enchantment:
```json
{
  "name": "Parkour",
  "description": [
    "Desc"
  ],
  "trigger": "playergothurt",
  "applicables": ["feet"],
  "activationIndicator": "bossbar",
  "forbidden": [
    "leather_boots",
    "chainmail_boots"
  ],
  "conflictsWith": [
    "frost_walker",
    "depth_strider"
  ],
  "conditions": [
    { "condition": "cause falling" },
    { "condition": "dmg > 3" }
  ],
  "chance": 30,
  "levels": {
    "1": {
      "conditions": ["!damage-lethal"],
      "cooldownTicks": 5,
      "actions": {
        "action": "cancel"
      }
    }
  }
}
```

- `name`
  This is a mandatory field. It is displayed on enchanted items, but most importantly, it is used when creating a unique key to identify the enchantment. There cannot be two enchantments with the same name on the server.
- `description`
  This mandatory field, represented by a list of strings, is used for debugging enchantments.
- `trigger`
  This is another mandatory field. This is what triggers the enchantment to be activated. One of the built-in triggers, `playergothurt`, is used here. It means that the enchantment will be activated when a player gets hurt.
- `applicables`
  This list of strings is mandatory. It defines what items can be enchanted with this enchantment. One of the built-in applicables, `feet`, is used here, and indicates that any boots can be enchanted with this enchantment.
- `enchantmentSeekers`
  This list of strings is also mandatory. An enchantment seeker is an object that looks for an enchantment when provided a player. One of the built-in seekers, `feet` is used here - this seeker checks if a player has this enchantment on their boots.
- `activationIndicator`
  This is a mandatory field, which indicates the way to tell a player that they have successfully activated an enchantment. In this case, one of the built-in indicators `bossbar` is used - it means that the player will receive a temporary bossbar notification when they activate this enchantment.
- `forbidden`
  This list of strings is optional. It indicates the items that cannot take this enchantment. For example, in this case, the list consists of `leather_boots` and `chainmail_boots`. It means that these items cannot be enchanted with this enchantment (even though the list of applicables contains `feet`). The list of item types can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html).
- `conflictsWith`
  This optional list of strings indicates what enchantments this enchantment conflicts with. Every string in the list must be a NamespacedKey's key: for example, for the enchantment "Guardian Angel" the whole registered NamespacedKey is `underscore_enchants:guardian_angel` and the relevant part for this field is `guardian_angel`.

The other fields are rather complicated for this README. You should check their own READMEs in the wiki.

# API
The API of UnderscoreEnchants is available at Maven Central.
```xml
<dependency>
    <groupId>io.github.roughlyunderscore</groupId>
    <artifactId>UnderscoreEnchantsAPI</artifactId>
    <version>2.2</version>
</dependency>
```
You can find more about the API at the [wiki](https://github.com/RoughlyUnderscore/UnderscoreEnchants/Wiki).
