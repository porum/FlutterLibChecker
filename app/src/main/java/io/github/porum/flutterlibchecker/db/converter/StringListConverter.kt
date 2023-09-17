package io.github.porum.flutterlibchecker.db.converter

import androidx.room.TypeConverter

class StringListConverter {
  @TypeConverter
  fun listToJson(list: List<String>?): String? = list?.toString()

  @TypeConverter
  fun stringToList(json: String?): List<String>? = json?.substring(1, json.length - 1)?.split(",")
}