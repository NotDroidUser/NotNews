package com.notdroid.notnews.composables

import com.notdroid.notnews.db.entities.NewsApiLocalSave
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.repeat

object PreviewUtils {
  fun generateMockApi(howMany:Int):List<NewsApiLocalSave>{
    return mutableListOf<NewsApiLocalSave>().apply {
      repeat(howMany) {
        this.add(NewsApiLocalSave(
          0,
          "VentureBeat",
          "Dean Takahashi",
          "Ansys says simulations will close the gap with reality and make the world more sustainable",
          "You may not have heard of Ansys, but it's in the process of being acquired by chip design tool firm Synopsys for \$35 billion.",
          "https://venturebeat.com/games/ansys-says-simulations-will-close-the-gap-with-reality-and-make-the-world-more-sustainable/",
          "https://media.zenfs.com/en/thewrap.com/58791b5d6507adf4c4e88f9fcf8357b0",
          GregorianCalendar.getInstance().apply {
            roll(Calendar.DAY_OF_YEAR, -1)
          }.time,
          false,
          false))
      }
    }
  }
}