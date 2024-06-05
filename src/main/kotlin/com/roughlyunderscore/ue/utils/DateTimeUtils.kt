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

package com.roughlyunderscore.ue.utils

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import java.time.LocalDateTime

@Since("2.2")
@Stable
/**
 * Converts a LocalDateTime object to a human-readable string.
*/
fun LocalDateTime.readable(): String {
  val month = this.month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
  val day = this.dayOfMonth.toString().padStart(2, '0')
  val year = this.year
  val hour = this.hour.toString().padStart(2, '0')
  val minute = this.minute.toString().padStart(2, '0')
  val second = this.second.toString().padStart(2, '0')

  return "$month $day, $year, $hour:$minute:$second"
}