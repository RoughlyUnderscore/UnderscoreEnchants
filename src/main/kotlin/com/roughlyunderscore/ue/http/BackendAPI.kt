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

import com.roughlyunderscore.data.server.BackendEnchantment
import com.roughlyunderscore.data.server.BackendEnchantmentPack
import com.roughlyunderscore.data.server.BackendLocale
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface BackendAPI {
  @Streaming
  @GET("download_pack?")
  suspend fun downloadPack(@Query("id", encoded = true) id: Long): Response<ResponseBody>

  @Streaming
  @GET("download_enchantment?")
  suspend fun downloadEnchantment(@Query("id", encoded = true) id: Long): Response<ResponseBody>

  @Streaming
  @GET("download_locale?")
  suspend fun downloadLocale(@Query("id", encoded = true) id: Long): Response<ResponseBody>

  @GET("packs")
  suspend fun packs(): Response<List<BackendEnchantmentPack>>

  @GET("enchs")
  suspend fun enchs(): Response<List<BackendEnchantment>>

  @GET("locales")
  suspend fun locales(): Response<List<BackendLocale>>
}