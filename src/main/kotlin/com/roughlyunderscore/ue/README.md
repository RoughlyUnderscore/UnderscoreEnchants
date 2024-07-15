# Main package

You have found yourself observing the contents of the main package of UnderscoreEnchants. What is over here?

## Sub-packages

There are a bunch of sub-packages here, providing structure and proper organization for the plugin. Every package has its own README.md. This file is solely a high-level overview.
- `commands`
This package is home to all commands of UnderscoreEnchants.
- `config`
This package stores everything related to configuration and message files.
- `data`
This package is home to classes containing various constants, and classes related to player data in one way or another.
- `debug`
This package stores all the debug-related classes and logic.
- `hook`
This package stores all the hooks made by the plugin.
- `http`
This package stores all the logic for making HTTP requests to the repository.
- `json`
This package stores all the logic for serializing and deserializing JSON.
- `listeners`
This package is home to all the event listeners used by the plugin, including the listener for custom events.
- `registry`
This package stores the implementation of the UnderscoreEnchantsAPI registry, as well as the built-in registrables.
- `storage`
This package stores all the logic for loading and saving player data from/to a storage medium.
- `tasks`
This package stores all the tasks being run by the plugin.
- `ui`
This package stores all the logic for creating and managing UIs.
- `utils`
This package stores all the important utilities used by the plugin, including but not limited to IO, math and text utilities.

## Classes

Only 2 classes are at the root of the main package - they are as follows:
- `UnderscoreEnchantment`
  This class is the implementation of the UnderscoreEnchantsAPI's RegistrableEnchantment. Aside storing all the data of an enchantment, it also contains a `registerEnchantment` method, which turns a stale RegistrableEnchantment into a functioning enchantment.
- `UnderscoreEnchantsPlugin`
  This is the core class of the plugin, containing the initializers, such as registering commands, listeners and enchantments, and the implementation of the API methods at the bottom part of the class.

The documentation is available [here](https://ue.runderscore.com/docs).