package com.roughlyunderscore.enchs.util

import com.roughlyunderscore.enchs.UnderscoreEnchants
import org.bukkit.Bukkit
import java.io.BufferedWriter
import java.io.IOException
import java.time.Instant
import java.util.*

class Debug constructor(private val log: Boolean, private val writer: BufferedWriter? = null, private val plugin: UnderscoreEnchants) {

  override fun toString(): String {
    return "Debug(log=$log, writer=$writer, plugin=$plugin)"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Debug) return false

    if (log != other.log) return false
    if (writer != other.writer) return false
    if (plugin != other.plugin) return false

    return true
  }

  override fun hashCode(): Int {
    var result = log.hashCode()
    result = 31 * result + (writer?.hashCode() ?: 0)
    result = 31 * result + plugin.hashCode()
    return result
  }

  fun log(message: String) {
    if (!log) return

    Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
      try {
        writer!!.write("[${Date.from(Instant.now())}]$message".trimIndent())
        writer.write("\n")
      } catch (e: IOException) {
        e.printStackTrace()
      }
    })

  }
}