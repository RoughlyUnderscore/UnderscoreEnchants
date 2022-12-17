package com.roughlyunderscore.enchs.parsers.action

import com.cryptomorin.xseries.XMaterial
import com.roughlyunderscore.enchs.UnderscoreEnchants
import com.roughlyunderscore.enchs.util.general.Utils.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object ActionUtils {
  /**
   * This will complete the action, taking in account the action postfixes.
   * As such, this frees the developer of actions, aka me, from handling it all
   * by myself. The developer will do the main action steps, then encapsulate it
   * with this method and it'll handle the background steps.
   * <br></br><br></br>
   * <table border="1">
   * <caption>Example:</caption>
   * <thead>
   * <tr><th scope="col">The full action</th><th scope="col">Only action, no postfixes</th><th scope="col">Only postfixes, no action</th></tr>
   * </thead>
   * <tbody style="text-align:left">
   * <tr><th scope="row">`create-world earth delay:15`</th><td>`create-world earth`</td><td>`delay:15`</td></tr>
   * <tr><th scope="row">`tp 0 <max_y_at_0_0> 0 delay:15;chance:10`</th><td>`tp 0 50 0`</td><td>`delay:15;chance:10`</td></tr>
   * </tbody>
   * </table>
   *
   * @param action the action to complete in the form of a `BukkitRunnable`
   * @param args0   the split condition
   */
  @JvmStatic
  fun completeAction(action: BukkitRunnable, args0: Array<String>, plugin: UnderscoreEnchants) {

    if (args0.isEmpty()) {
      action.run()
      return
    }

    val args = args0.dropLastWhile { it.isEmpty() }.toTypedArray()



    // split last arg by ;
    val prePostfixes = args[args.size - 1].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (prePostfixes.size == 1) {
      // no postfixes
      action.run()
      return
    }

    // length is more than 1, so remove first argument
    val postfixes = prePostfixes.copyOfRange(1, prePostfixes.size)
    if (arrayOfStringsContains(postfixes, "delay:")) {
      // delay
      val delay = parseI(postfixes[0].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
      action.runTaskLater(plugin, delay.toLong())
    }
    if (arrayOfStringsContains(postfixes, "chance:")) {
      // chance
      val chance = parseI(postfixes[0].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
      if (chance < 100) {
        if (Math.random() * 100 > chance) {
          return
        }
      }
      action.run()
    }
  }

  /**
   * Complex material parsing into an item.
   * <br></br><br></br>
   * <table>
   * <caption>Usage:</caption>
   * <thead>
   * <tr><th scope="col">Input</th><th scope="col">Output</th><th scope="col">Explanation</th></tr>
  </thead> *
   * <tbody>
   * <tr>
   * <th scope="row">`"stone"`</th>
   * <td>Item with `Material.STONE`</td>
   * <td>Simple material</td>
  </tr> *
   * <tr>
   * <th scope="row">`"stone:3"`</th>
   * <td>Item with `Material.STONE` x3</td>
   * <td>Simple material with amount</td>
  </tr> *
   * <tr>
   * <th scope="row">`"stone:3;cobblestone"`</th>
   * <td>Item with 50% chance of `Material.STONE` x3, 50% chance of `Material.COBBLESTONE`</td>
   * <td>Complex even chance material</td>
  </tr> *
   * <tr>
   * <th scope="row">`"stone:3;cobblestone:20%"`</th>
   * <td>Item with 80% chance of `Material.STONE` x3, 20% chance of `Material.COBBLESTONE`</td>
   * <td>Complex material with chance</td>
  </tr> *
   * <tr>
   * <th scope="row">`"stone:3:30%;cobblestone:6:20%;dirt:5"`</th>
   * <td>Item with 30% chance of `Material.STONE` x3, 20% chance of `Material.COBBLESTONE` x6, 50% chance of `Material.DIRT` x5</td>
   * <td>Complex material with chance and amount</td>
  </tr> *
  </tbody> *
  </table> *
   *
   * @param material0 the material to parse
   * @return the parsed item
   */
  @JvmStatic
  fun parseItem(material0: String): ItemStack {
    var material = material0
    material = material.uppercase(Locale.getDefault())
    val air = ItemStack(Material.AIR)
    // split by ;
    val materials = material.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    // if length is 1, then it's a simple material
    if (materials.size == 1) {
      println("code 1;?")
      // split by :
      val materialData = materials[0].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      val mat = XMaterial.matchXMaterial(materialData[0].uppercase(Locale.getDefault())).orElse(XMaterial.AIR).parseMaterial()
        ?: return air

      // if length is 1, then it's a simple material
      if (materialData.size == 1) {
        println("code 1;1")
        return ItemStack(mat)
      }

      // if length is 2, then it's a simple material with amount OR chance
      if (materialData.size == 2) {
        println("code 1;2")
        // if it contains %, then it's a chance
        if (materialData[1].contains("%")) {
          println("code 1;2;%")
          // get chance
          val chance = parseI(materialData[1].replace("%", ""))
          return if (Math.random() * 100 > chance) {
            println("code 1;2;%;air")
            air
          } else {
            println("code 1;2;%;item")
            ItemStack(mat)
          }

        }
        println("code 1;2;amount;item")
        // else, it's a simple material with amount
        return ItemStack(mat, parseI(materialData[1]))
      }

      // if length is 3, then it's a simple material with amount and chance.
      // chance can be either [1] or [2]
      if (materialData.size == 3) {
        println("code 1;3")
        val amount: Int
        val chance: Int
        if (materialData[1].contains("%")) {
          println("code 1;3;%;amount")
          // get chance
          chance = parseI(materialData[1].replace("%", ""))
          amount = parseI(materialData[2])
        } else {
          println("code 1;3;amount;%")
          // get chance
          chance = parseI(materialData[2].replace("%", ""))
          amount = parseI(materialData[1])
        }
        return if (Math.random() * 100 > chance) {
          println("code 1;3;%&amount;air")
          air
        } else ItemStack(mat, amount)
      }
    }

    // if length is more than 1, then it's a complex material
    if (materials.size > 1) {
      println("code 2;?")
      // get total chance
      var totalChance = 0

      // calculate the total chance for all materials
      for (materialData in materials) {
        // split by :
        val materialDataSplit = materialData.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // if length is 3, then it's a simple material with amount and chance
        if (materialDataSplit.size == 3) {
          println("code 2;3")
          // chance can be either [1] or [2]
          if (materialDataSplit[1].contains("%")) {
            totalChance += parseI(materialDataSplit[1].replace("%", ""))
            println("code 2;3;%;amount")
          } else {
            totalChance +=parseI(materialDataSplit[2].replace("%", ""))
            println("code 2;3;amount;%")
          }
        }

        // if length is 2, then it's a simple material with amount OR chance
        if (materialDataSplit.size == 2) {
          println("code 2;2")
          // if it contains %, then it's a chance
          if (materialDataSplit[1].contains("%")) {
            println("code 2;2;%")
            totalChance += parseI(materialDataSplit[1].replace("%", ""))
          }
        }
      }

      // now that we've calculated the total chance, let's get to generating the item
      // get a random number between 0 and totalChance
      var random = (Math.random() * totalChance).toInt()

      // loop through all materials
      for (materialData in materials) {
        println("code 2;prep")
        // split by :
        val materialDataSplit = materialData.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (materialDataSplit.size == 1) {
          println("code 2;1")
          val mat = XMaterial.matchXMaterial(materialDataSplit[0].uppercase(Locale.getDefault())).orElse(XMaterial.AIR).parseMaterial()
            ?: return air
          return ItemStack(mat)
        }

        // if length is 3, then it's a simple material with amount and chance
        if (materialDataSplit.size == 3) {
          println("code 2;prep;3")
          // chance can be either [1] or [2]
          val chance: Int
          if (materialDataSplit[1].contains("%")) {
            println("code 2;prep;3;%;amount")
            // get chance
            chance = parseI(materialDataSplit[1].replace("%", ""))
            if (random < chance) {
              // get material
              val mat = XMaterial.matchXMaterial(materialDataSplit[0].uppercase()).orElse(XMaterial.AIR).parseMaterial()
              if (mat == null || mat == Material.AIR) {
                println("code 2;prep;3;%;amount;air")
                return air
              }
              // get amount
              val amount = parseI(materialDataSplit[2])
              println("code 2;prep;3;%;amount;item")
              return ItemStack(mat, amount)
            }
          } else {
            println("code 2;prep;3;amount;%")
            // get chance
            chance = parseI(materialDataSplit[2].replace("%", ""))
            if (random < chance) {
              // get material
              val mat = XMaterial.matchXMaterial(materialDataSplit[0].uppercase()).orElse(XMaterial.AIR).parseMaterial()
              if (mat == null || mat == Material.AIR) {
                println("code 2;prep;3;amount;%;air")
                return air
              }
              // get amount
              val amount = parseI(materialDataSplit[1])
              println("code 2;prep;3;amount;%;item")
              return ItemStack(mat, amount)
            }
          }
          random -= chance
        }

        // if length is 2, then it's a simple material with amount OR chance
        if (materialDataSplit.size == 2) {
          println("code 2;prep;2")
          // if it contains %, then it's a chance
          random -= if (materialDataSplit[1].contains("%")) {
            println("code 2;prep;2;%")
            // get chance
            val chance = parseI(materialDataSplit[1].replace("%", ""))
            if (random < chance) {
              // get material
              val mat = XMaterial.matchXMaterial(materialDataSplit[0].uppercase(Locale.getDefault())).orElse(XMaterial.AIR).parseMaterial()
              return if (mat == null || mat == Material.AIR) {
                println("code 2;prep;2;%;air")
                air
              } else {
                println("code 2;prep;2;%;item")
                ItemStack(mat)

              }
            }
            chance
          } else {
            println("code 2;prep;2;amount")
            // get material
            val mat = XMaterial.matchXMaterial(materialDataSplit[0].uppercase(Locale.getDefault())).orElse(XMaterial.AIR).parseMaterial()
            if (mat == null || mat == Material.AIR) {
              println("code 2;prep;2;amount;air")
              return air
            }
            // get amount
            val amount = parseI(materialDataSplit[1])
            println("code 2;prep;2;amount;item")
            return ItemStack(mat, amount)
          }
        }
      }
    }
    println("code air")
    return air
  }

  /**
   * Collects the arguments' tail into a string.
   *
   * @param args      the arguments
   * @param start     the start index
   * @param ignored   the ignored arguments
   * @param separator the separator
   * @return the tail
   */
  @JvmStatic
  fun collectTail(args: Array<String?>, start: Int, separator: String?, ignored: Array<String?>?): String {
    val builder = StringBuilder()
    for (i in start until args.size) {
      if (ignored != null && arrayOfStringsContainsPartly(ignored, args[i])) continue
      builder.append(args[i]).append(separator)
    }
    return builder.toString().trim { it <= ' ' }
  }
}