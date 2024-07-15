

# Guide to UnderscoreEnchants
Welcome to UnderscoreEnchants!

This plugin adds a bunch of new enchantments to the game, and allows you to customize them to your liking. In addition, it allows you to create your own enchantments, and add them to the game. Most importantly, the actions, conditions, triggers and other tokens of a custom enchantment are not confined to the hardcoded plugin limits. Using the UnderscoreEnchants API, you can create your own ones.

This README is a condensed version of the [documentation](https://ue.runderscore.com/docs) for this plugin. Make sure to check it out!

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

#### Misc

- `bStats`
  This field indicates whether the anonymous bStats metric collection should be enabled. This does not harm your performance or transmit any sensitive data. Set to `true` by default.
- `update-checker-frequency-hours`
  This field indicates the frequency (in hours) with which the update checker should check if there are any new updates for UnderscoreEnchants. Set this to -1 to disable the update checker altogether. Set to `24` by default.
- `notify-ops-about-updates`
  This field indicates whether to notify server operators about any new updates for UnderscoreEnchants - that is, if the update checker is enabled. Set to `true` by default.
- `debug`
  This field indicates whether to print various miscellaneous debug messages to the console.

#### Generation

- `chest-loot-chance`
  This field indicates the chance (in percents) to generate an enchanted item in a chest. Set to `14.2857142857149` (1/7) by default.
- `fishing-loot-chance`
  This field indicates the chance (in percents) to catch an enchanted item whilst fishing. Set to `14.2857142857149` (1/7) by default.
- `villager-item-trade-chance`
  This field indicates the chance (in percents) to generate an enchanted item in a villager trade. Set to `14.2857142857149` (1/7) by default.
- `enchantment-chance`
  This field indicates the chance (in percents) to add another enchantment while enchanting items. Set to `14.2857142857149` (1/7) by default.

#### UI

- `truncate-pack-data-after-x-enchantments`
  This field indicates the amount of max enchantments shown in the pack preview GUI, after which they get truncated

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
The documentation for creating enchantments is available [here](https://ue.runderscore.com/docs/users/enchantments.html).

# API
The API of UnderscoreEnchants is available at Maven Central.

Maven:
```xml
<!-- Repository -->
<repository>
    <id>roughly-underscore</id>
    <url>https://repo.runderscore.com/releases</url>
</repository>
        
<!-- Dependency -->
<dependency>
    <groupId>com.roughlyunderscore</groupId>
    <artifactId>UnderscoreEnchantsAPI</artifactId>
    <version>2.2.0</version>
</dependency>
```

Gradle (Kotlin):
```kotlin
// Repository
maven("https://repo.runderscore.com/releases")

// Dependency
implementation("com.roughlyunderscore:UnderscoreEnchantsAPI:2.2.0")
```

Gradle (Groovy):
```groovy
// Repository
maven "https://repo.runderscore.com/releases"

// Dependency
implementation "com.roughlyunderscore:UnderscoreEnchantsAPI:2.2.0"
```

You can find more about the API in the [developer guide](https://ue.runderscore.com/docs/devs/api.html).
