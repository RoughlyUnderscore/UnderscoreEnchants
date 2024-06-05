// Copyright 2024 RoughlyUnderscore
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.roughlyunderscore.ue.json.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.roughlyunderscore.json.DeserializationNames
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.config.UEConfiguration
import com.roughlyunderscore.ue.utils.shutdown
import com.roughlyunderscore.ulib.json.anyInt
import com.roughlyunderscore.ulib.plugin.nextTickAsync
import java.lang.reflect.Type

class ConfigurationDeserializer(private val plugin: UnderscoreEnchantsPlugin) : JsonDeserializer<UEConfiguration?> {
  private val prettyGson = GsonBuilder().setPrettyPrinting().create()

  override fun deserialize(src: JsonElement?, type: Type?, ctx: JsonDeserializationContext?): UEConfiguration? {
    val json = src?.asJsonObject ?: return null
    val configuration = json.get("configuration")?.asJsonObject ?: return null

    val version = json.anyInt(DeserializationNames.Configuration.VERSION) ?: return null

    when {
      version < 22 -> {
        plugin.ueLogger.commit { info("Found outdated configuration (schema version $version). Will recreate the configuration file and add new default values.") }
        // If the version is below 22, we deserialize the configuration (new fields take
        // on the default values), serialize it with the new values and save it back
        val config = ConfigSchema.Schema22.deserialize(configuration) ?: return null

        return config.apply {
          // Calling this before returning the configuration causes a JsonSyntaxException with the error message
          // "JSON document was not fully consumed". I still do not know what exactly causes this, but I think that
          // it happens because [toJson] calls a function [assertFullConsumption], and since the file is overwritten,
          // something weird happens and [reader.peek()] stops returning [JsonToken.END_DOCUMENT]. I found that
          // delaying it by 1 tick helps avoid this error.
          plugin.nextTickAsync {
            plugin.dataFolder.resolve("config.json").writeText(prettyGson.toJson(ConfigSchema.Schema22.serialize(this)))
          }
        }
      }

      version == 22 -> {
        plugin.ueLogger.commit { info("Found modern configuration (schema version $version).") }
        return ConfigSchema.Schema22.deserialize(configuration)
      }

      else -> {
        plugin.shutdown("Found configuration schema version $version (the current modern version is 22). Shutting down")
        return null
      }
    }
  }
}