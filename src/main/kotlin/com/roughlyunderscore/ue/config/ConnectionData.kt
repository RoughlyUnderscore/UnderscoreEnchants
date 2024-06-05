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

package com.roughlyunderscore.ue.config

import com.roughlyunderscore.annotations.Since

/**
 * Stores connection data from `connection.json` for convenience.
 */
@Since("2.2")
data class ConnectionData(
  /**
   * The connection data for MongoDB databases.
   */
  val mongo: MongoDBConnectionData,

  /**
   * The connection data for JDBC-type databases.
   */
  val jdbc: JDBCConnectionData,
) {
  data class MongoDBConnectionData(
    val url: String,
    val database: String,
    val collection: String,
    val username: String,
    val password: String,
  )

  data class JDBCConnectionData(
    val url: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
  )
}