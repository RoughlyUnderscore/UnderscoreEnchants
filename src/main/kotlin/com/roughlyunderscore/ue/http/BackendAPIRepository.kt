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

package com.roughlyunderscore.ue.http

import com.roughlyunderscore.data.EnchantmentPack
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.utils.loadPackFromInputStream
import com.roughlyunderscore.ulib.io.requireChildDirectory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class BackendAPIRepository(private val api: BackendAPI, private val plugin: UnderscoreEnchantsPlugin) {
  suspend fun downloadPack(id: Long): Pair<EnchantmentPack, File>? = downloadAndParse(
    { api.downloadPack(id) },
    { input, name -> saveToFile(content = input, name = name).await() },
    { content -> content.byteInputStream(Charsets.UTF_8).use { stream -> stream.loadPackFromInputStream(plugin) } }
  )

  suspend fun downloadEnchantment(id: Long): Pair<UnderscoreEnchantment, File>? = downloadAndParse(
    { api.downloadEnchantment(id) },
    { input, name -> saveToFile(content = input, name = name).await() },
    { content -> plugin.gson.fromJson(content, UnderscoreEnchantment::class.java) }
  )

  suspend fun downloadLocale(id: Long): Pair<UELocale, File>? = downloadAndParse(
    { api.downloadLocale(id) },
    { input, name -> saveToFile(content = input, name = name, subdirectory = "messages").await() },
    { content -> plugin.gson.fromJson(content, UELocale::class.java) }
  )

  suspend fun getPacks() = api.packs().body() ?: emptyList()
  suspend fun getEnchantments() = api.enchs().body() ?: emptyList()
  suspend fun getLocales() = api.locales().body() ?: emptyList()

  private suspend inline fun <reified T> downloadAndParse(
    crossinline downloadCallback: suspend BackendAPI.() -> Response<ResponseBody>,
    crossinline inputCallback: suspend (String, String) -> File,
    crossinline loadCallback: (String) -> T?
  ): Pair<T, File>? = coroutineScope {
    val file = api.downloadCallback()
    if (!file.isSuccessful) return@coroutineScope null

    val header = file.headers().get("Content-Disposition") ?: return@coroutineScope null
    val name = header.replace("file; filename=", "").replace("attachment; filename=", "")

    val body = file.body()?.source() ?: return@coroutineScope null

    val content = body.inputStream().use { input -> input.bufferedReader().readText() }
    val resultFile = inputCallback(content, name)
    val result = loadCallback(content) ?: return@coroutineScope null // plugin.gson.fromJson(content, T::class.java)
    result to resultFile
  }


  private fun saveToFile(content: String, subdirectory: String = "enchantments", name: String) = GlobalScope.async {
    plugin.dataFolder.requireChildDirectory(subdirectory)
    var file = plugin.dataFolder.resolve(subdirectory).resolve(name)

    val nameWithoutExtension = name.substringBeforeLast(".")
    val extension = name.substringAfterLast(".")
    var counter = 1

    while (file.exists()) {
      file = plugin.dataFolder.resolve(subdirectory).resolve("$nameWithoutExtension ($counter).$extension")
      counter++
    }

    file.apply { writeText(content) }
  }
}