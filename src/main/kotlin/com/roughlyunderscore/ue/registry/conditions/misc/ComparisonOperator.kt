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

package com.roughlyunderscore.ue.registry.conditions.misc

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.ulib.text.normalize
import com.roughlyunderscore.ue.utils.normalize

enum class ComparisonOperator {
  EQUAL,
  MORE_THAN,
  LESS_THAN,
  MORE_THAN_OR_EQUAL,
  LESS_THAN_OR_EQUAL,
  NOT_AN_OPERATOR;

  companion object {
    /**
     * Gets the operator from [string], first lowercasing it, then stripping it of underscores and dashes.
     */
    @Since("2.2")
    fun getOperator(string: String): ComparisonOperator {
      return when (string.normalize()) {
        "=", "==", "===", "is", "equals", "equal" -> EQUAL
        ">", "more", "above", "larger", "morethan", "largerthan" -> MORE_THAN
        "<", "less", "under", "smaller", "lessthan", "smallerthan" -> LESS_THAN
        ">=", "=>" -> MORE_THAN_OR_EQUAL
        "<=", "=<" -> LESS_THAN_OR_EQUAL
        else -> NOT_AN_OPERATOR
      }
    }
  }
}
