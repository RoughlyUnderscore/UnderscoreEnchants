package com.roughlyunderscore.enchs.util

import java.util.*

/**
 * By https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
 */
class RomanNumber {
  init {
    throw InstantiationException("This class is not for instantiation")
  }

  companion object {
    private val map = TreeMap<Int, String>()

    init {
      map[1000] = "M"
      map[900] = "CM"
      map[500] = "D"
      map[400] = "CD"
      map[100] = "C"
      map[90] = "XC"
      map[50] = "L"
      map[40] = "XL"
      map[10] = "X"
      map[9] = "IX"
      map[5] = "V"
      map[4] = "IV"
      map[1] = "I"
    }

    /**
     * Converts a number to a Roman numeral.
     *
     * @param number The number to convert.
     * @return The Roman numeral.
     */
    @JvmStatic
    fun toRoman(number: Int): String? {
      val l = map.floorKey(number)
      return if (number == l) {
        map[number]
      } else map[l] + toRoman(number - l)
    }

    /**
     * Converts a Roman numeral to a number.
     * @param roman0 The Roman numeral to convert.
     * @return The number.
     */
    @JvmStatic
    fun fromRoman(roman0: String): Int {
      var roman = roman0.uppercase()

      var result = 0
      var i = roman.length - 1
      var num = charToNum(roman[i])
      var pre: Int
      while (i >= 0) {
        pre = num
        num = charToNum(roman[i])
        if (num < pre) {
          result -= num
        } else {
          result += num
        }
        i--
      }
      return result
    }

    private fun charToNum(ch: Char): Int {
      return when (ch.uppercase()) {
        "I" -> 1
        "V" -> 5
        "X" -> 10
        "L" -> 50
        "C" -> 100
        "D" -> 500
        "M" -> 1000
        else -> 0
      }
    }
  }
}