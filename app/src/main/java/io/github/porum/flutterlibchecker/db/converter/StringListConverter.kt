package io.github.porum.flutterlibchecker.db.converter

import androidx.room.TypeConverter

class StringListConverter {
  @TypeConverter
  fun setToJson(set: Set<String>?): String? = set?.toString()

  @TypeConverter
  fun stringToSet(json: String?): Set<String>? = json?.substring(1, json.length - 1)?.split(",")?.toSet()
}