# Debug

This package contains all the code that is used for debugging the plugin. This includes some implementations of `Describer`, such as `EnchantmentDescriber`.

# BriefRegistryDescriber
This object describes a registry in a brief way. It is used by the log creator.

# EnchantmentDescriber
This object describes an enchantment and is used all across the plugin, such as the log creator and the `/ue debug enchantment` command.

# WorldDescriber
This object describes a world and is used by the log creator.

# LogCreator
This object creates a log of the plugin's state. It is used by the `/ue debug log` command.

# RegistryDebugger
This class is used by the `/ue debug registry` command. It contains two functions - one describes the entire registry in a brief way (but not in the same manner that `BriefRegistryDescriber` does), and the other describes a specific registry entry in a more detailed way.

# Debug roadmap
- [ ] Nothing yet!