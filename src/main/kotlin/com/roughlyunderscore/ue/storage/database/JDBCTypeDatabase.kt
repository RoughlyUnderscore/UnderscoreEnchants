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

package com.roughlyunderscore.ue.storage.database

import com.roughlyunderscore.annotations.Since
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.coroutineScope
import java.sql.ResultSet

/**
 * This class represents a generic JDBC-compliant [database], be it MySQL, PostgreSQL or any other type of [driver],
 * as long as it is present in the plugin. [url], [port], [username], [password] and [database] required for creating a connection.
 */
@Since("2.2")
abstract class JDBCTypeDatabase(
  private val url: String, private val driver: String, private val port: Int, private val username: String, private val password: String, private val database: String
) {
  private var dataSource: HikariDataSource = HikariDataSource().apply {
    this.jdbcUrl = "jdbc:$driver://$url:$port/$database"
    this.username = this@JDBCTypeDatabase.username
    this.password = this@JDBCTypeDatabase.password
  }

  /**
   * Executes an update-type [query] on the database with [params]. For example: `INSERT`, `UPDATE` and `DELETE` queries.
   */
  @Since("2.2")
  suspend fun update(query: String, vararg params: Any?) = coroutineScope {
    dataSource.connection.use { conn ->
      conn.prepareStatement(query).use { statement ->
        params.forEachIndexed { index, param -> statement.setObject(index + 1, param) }

        statement.executeUpdate()
      }
    }

    Unit
  }

  suspend fun update(query: String, params: List<Any>?) = update(query, *params?.toTypedArray() ?: emptyArray())

  /**
   * Executes a fetch-type [query] on the database with [params] (for example: `SELECT` queries)
   * and operates on the ResultSet using the provided [block].
   */
  @Since("2.2")
  suspend fun <R> fetch(query: String, params: List<Any>, block: suspend ResultSet.() -> R): R = coroutineScope {
    dataSource.connection.use { conn ->
      conn.prepareStatement(query).use { statement ->
        params.forEachIndexed { index, param -> statement.setObject(index + 1, param) }

        return@coroutineScope statement.executeQuery().use { rs -> block(rs) }
      }
    }
  }
}