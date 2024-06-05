package com.roughlyunderscore.ue.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.roughlyunderscore.data.EnchantmentPackMetadata
import com.roughlyunderscore.json.DeserializationNames
import com.roughlyunderscore.ulib.json.*
import org.bukkit.Material
import java.lang.reflect.Type

object PackMetadataDeserializer : JsonDeserializer<EnchantmentPackMetadata> {
  override fun deserialize(json: JsonElement?, type: Type?, ctx: JsonDeserializationContext?): EnchantmentPackMetadata? {
    val pack = json?.asJsonObject ?: return null

    val builder = EnchantmentPackMetadata.Builder()

    builder.name(pack.anyString(DeserializationNames.PackMetadata.NAME) ?: return null)
    builder.version(pack.anyString(DeserializationNames.PackMetadata.VERSION) ?: return null)
    builder.authors(pack.anyArrayOfStrings(DeserializationNames.PackMetadata.AUTHORS) ?: emptyList())
    builder.description(pack.anyArrayOfStrings(DeserializationNames.PackMetadata.DESCRIPTION) ?: emptyList())
    builder.website(pack.anyString(DeserializationNames.PackMetadata.WEBSITE) ?: return null)
    builder.worldBlacklist(pack.anyArrayOfStrings(DeserializationNames.PackMetadata.WORLD_BLACKLIST) ?: emptyList())
    builder.worldWhitelist(pack.anyArrayOfStrings(DeserializationNames.PackMetadata.WORLD_WHITELIST) ?: emptyList())
    builder.material(pack.onAnyString(DeserializationNames.PackMetadata.ITEM) { Material.matchMaterial(this) } ?: return null)

    return builder.build()
  }
}