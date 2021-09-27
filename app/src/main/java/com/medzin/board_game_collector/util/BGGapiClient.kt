package com.medzin.board_game_collector.util

import android.content.Context
import com.medzin.board_game_collector.R
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class BGGapiClient {

    companion object {
        private fun queryXML(context: Context, urlString: String, output: String): Boolean {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val workDir = File("${context.filesDir}/XML")
                if (!workDir.exists()) workDir.mkdir()
                val fos = FileOutputStream("$workDir/$output")
                val data = ByteArray(1024)
                var count = 0
                var total: Long = 0
                var progress = 0
                count = isStream.read(data)
                while (count != -1) {
                    total += count.toLong()
                    val progressTemp = total.toInt() * 100 / lengthOfFile
                    if (progressTemp % 10 == 0 && progress != progressTemp) {
                        progress = progressTemp
                    }
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
                connection.disconnect()
            } catch (e: Exception) {
                return false
            }
            return true
        }

        fun searchGame(context: Context, title: String): Boolean {
            val urlString = context.getString(R.string.game_search_url, title)
            return queryXML(context, urlString, context.getString(R.string.game_search_xml))
        }

        fun getGameDetails(context: Context, bggID: Int): Boolean {
            val urlString = context.getString(R.string.game_details_url, bggID)
            return queryXML(context, urlString, context.getString(R.string.game_details_xml))
        }

        fun getUserCollection(context: Context, username: String, getRanks: Boolean = false): Boolean {
            val urlString = context.getString(
                    (if (getRanks) R.string.user_collection_url else R.string.ranks_collection_url),
                    username
            )
            return queryXML(context, urlString, context.getString(R.string.user_collection_xml))
        }

    }

}