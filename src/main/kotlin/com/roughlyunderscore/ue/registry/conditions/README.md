# Conditions

Conditions are used to determine whether the enchantment should be triggered. Inside an enchantment, the list of global conditions can be specified this way:
```json
{
  "enchantment": {
    "conditions": [
      {
        "condition": "condition1"
      },
      {
        "condition": "condition2"
      }
    ]
  }
}
```

Per-level conditions are similar in the specification:
```json
{
  "enchantment": {
    "levels": [
      {
        "level": 1,
        "conditions": [
          {
            "condition": "condition1"
          },
          {
            "condition": "condition2"
          }
        ]
      }
    ]
  }
}
```

A valid condition can take two extra parameters: `negate` and `player`.
- `negate` is a boolean that specifies whether the condition should be negated. If `negate` is `true`, the condition will be considered true if it is false, and vice versa. Defaults to `false`.
- `player` is a string that accepts either `first` or `second`. It specifies, which player should be used for the condition. Defaults to `first`.

Example condition:
```json
{
  "condition": "day",
  "negate": true,
  "player": "second"
}
```
This condition checks whether the time for the second player is **not** daytime (below 12300 or above 23850).

# Creating a condition

Conditions are classes that implement the `RegistrableCondition` interface provided by UEAPI:
```kotlin
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableCondition
import com.roughlyunderscore.registry.RegistrableTrigger
import org.bukkit.entity.Player
import org.bukkit.event.Event

class DayCondition : RegistrableCondition {
  override val aliases = listOf(
    "day",
    "isday",
    "daytime",
    "isdaytime",
    "is-day",
    "is-daytime",
    "is-day-time",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return false
    val player = method.invoke(event) as? Player ?: return false
    return player.world.time.let { it < 12300 || it > 23850 }
  }
}
```

Negation is handled by the enchantment registrar.

Registering the condition is similar to the registration process of other registrables:

```kotlin
override fun onEnable() {
  val plugin = Bukkit.getPluginManager().getPlugin("UnderscoreEnchants")
  if (plugin != null) {
    val registrables = mutableListOf<Registrable>()
    registrables.add(DayCondition())
    
    plugin.registry.provide(object: RegistrablesProvider {
      override fun getAssociatedPlugin(): JavaPlugin = this@MyPlugin
      override fun getProvidedRegistrables(): MutableList<Registrable> = registrables
    })
  }
}
```

As always, the aliases cannot clash with other conditions.

# Out-of-the-box conditions
Default UnderscoreEnchants conditions can be found in /registry/conditions. They include:
- Time conditions (`day`)
- Weather conditions (`rain`, `thunder`)
- World environment conditions (`nether`, `end`, `overworld`)
- World difficulty conditions (`peaceful`, `easy`, `normal`, `hard`, `hardcore`)
- Player state conditions (`sneaking`, `sprinting`, `sleeping` and more)
- Concrete comparisons (health, food level, exp, and more)
- Event-specific conditions (`equipped`, `block-is`, and more)

# Conditions roadmap
- [x] Implement comparison of any two values (e.g., placeholder against number)
- [ ] `optional` field that accepts a boolean and specifies whether the condition is optional (defaults to `false`)
- [x] A condition field for actions