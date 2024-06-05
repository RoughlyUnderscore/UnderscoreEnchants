# Commands

This package stores all commands used by the plugin. Namely, it is one central command `/underscoreenchants` and its subcommands.

## `/ue` | `/underscoreenchants`
This is the main command. It is used to access all other commands. When run without any arguments or with arguments that do not match any subcommands, it will display a help message.

## `/ue debug`
This command is used to debug various parts of the plugin. It is not intended for use by players, but rather by developers or administrators receiving help from the support team. As such, user-friendliness is not a priority. When run without any arguments, it will display a help message, displaying all arguments it can take.
- `log` | `logfile` | `makelog` | `createlog`
Creates a log file in the plugin's data folder. This log file contains all the information about the plugin, such as the state of the registry and the loaded worlds. The log file is named `log-{currentTime}.txt` and is located in the plugin's data folder in the `logs` subfolder.
- `enchantment <enchantment>` | `ench <enchantment>`
Displays information about the specified enchantment. The information displayed includes the enchantment's name, activation chance, levels etc. If the enchantment is not found, an error message is displayed.
- `registry [type]` | `reg [type]` | `register [type]`
Displays information about the registry. If a type is specified, it will display information about that specific registry branch. If no type is specified, it will display general information about the whole registry. If the type is not found, an error message is displayed.

## `/ue enchant <enchantment> <level> [player]`
This command is used to enchant an item with the specified enchantment. If the player is not specified, it's assumed that the executor is the target player (if the executor is not a player, an error message is displayed).

## `/ue toggle <enchantment> [player] [bypass restrictions]`
This command is used to toggle an enchantment on or off. If the player is not specified, it's assumed that the executor is the target player (if the executor is not a player, an error message is displayed). Optionally, if the last value is set to `true`, conflicting enchantments and enchantments that do not fit the item can be applied.

## `/ue locale <locale> [player]`
This command is used to change the locale of the specified player. If the player is not specified, it's assumed that the executor is the target player (if the executor is not a player, an error message is displayed). If the per-player locale feature is disabled, this command will not work.

## `/ue reload`
This command reloads the plugin's enchantments, registry and configurations.

## `/ue download <id> [load]` - downloads an enchantment/pack/locale by an ID from the repository
This command downloads an enchantment/pack/locale from the repository and loads it if the value of `load` is set to `true`. The repository by default is configured to be the central UnderscoreEnchants repository, however, it can be changed in the `config.json` file.

## `/ue load <enchantment/pack> <filename>` - loads an enchantment/pack from the plugin's data folder
This command loads an enchantment/pack from the plugin's data folder. The file must be in the correct format, otherwise, an error message will be displayed to the command sender.

## `/ue unload <enchantment/pack> <name>` - unloads an enchantment/pack from the plugin's data folder
This command unloads an enchantment/pack from the plugin's data folder. If the enchantment/pack is not found, an error message is displayed to the command sender.