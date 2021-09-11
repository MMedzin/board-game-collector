package com.medzin.board_game_collector.xml_parser

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.text.isDigitsOnly
import com.medzin.board_game_collector.R
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.database.objects.Person
import com.medzin.board_game_collector.exceptions.ResultNotReadyException
import com.medzin.board_game_collector.util.GameType
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class GameDetailsParser {

    companion object {

        private fun parse(context: Context, title: String, baseGame: Game?): Game? {
            val game: Game
            if (baseGame == null) {
                game = Game(title)
            } else {
                game = baseGame
            }
            try {
                val workDir = File("${context.filesDir}/XML")
                if (workDir.exists()) {
                    val file = File(workDir, context.getString(R.string.game_details_xml))
                    if (file.exists()) {
                        val xmlDoc: Document =
                            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                        xmlDoc.documentElement.normalize()

                        if (xmlDoc.getElementsByTagName("message").length > 0)
                            throw ResultNotReadyException()

                        if (xmlDoc.getElementsByTagName("error").length > 0) {
                            return null
                        }

                        val items: NodeList = xmlDoc.getElementsByTagName("item")

                        for (i in 0 until items.length) {
                            val itemNode: Node = items.item(i)

                            if (itemNode.nodeType == Node.ELEMENT_NODE) {

                                val elem = itemNode as Element
                                game.bggId = elem.getAttribute("id").toInt()
                                when (elem.getAttribute("type")) {
                                    "boardgame" -> {
                                        game.type = GameType.GAME
                                    }
                                    "boardgameexpansion" -> {
                                        game.type = GameType.EXPANSION
                                    }
                                    "mixed" -> {
                                        game.type = GameType.MIXED
                                    }
                                }
                                val children = elem.childNodes

                                for (j in 0 until children.length) {
                                    val node = children.item(j)
                                    if (node is Element) {
                                        when (node.nodeName) {
                                            "thumbnail" -> {
                                                val url = URL(node.textContent)
                                                game.thumbnail = BitmapFactory.decodeStream(
                                                    url.openConnection().getInputStream()
                                                )
                                            }
                                            "name" -> {
                                                if (node.getAttribute("type") == "primary") {
                                                    game.originalTitle = node.getAttribute("value")
                                                }
                                            }
                                            "description" -> {
                                                game.description = node.textContent
                                            }
                                            "yearpublished" -> {
                                                game.yearPublished =
                                                    node.getAttribute("value").toInt()
                                            }
                                            "link" -> {
                                                when (node.getAttribute("type")) {
                                                    "boardgamedesigner" -> {
                                                        if (game.designers == null) game.designers =
                                                            mutableListOf()
                                                        game.designers?.add(
                                                            Person(
                                                                node.getAttribute(
                                                                    "value"
                                                                )
                                                            )
                                                        )
                                                    }
                                                    "boardgameartist" -> {
                                                        if (game.artists == null) game.artists =
                                                            mutableListOf()
                                                        game.artists?.add(Person(node.getAttribute("value")))
                                                    }
                                                }
                                            }
                                            "statistics" -> {
                                                val ranks = (node.getElementsByTagName("ratings")
                                                    .item(0) as Element).getElementsByTagName("ranks")
                                                    .item(0).childNodes
                                                for (k in 0 until ranks.length) {
                                                    val rank = ranks.item(k)
                                                    if (rank is Element && rank.nodeName == "rank" &&
                                                        rank.getAttribute("name") ==
                                                        "boardgame"
                                                    ) {
                                                        if (rank.getAttribute("value")
                                                                .isDigitsOnly()
                                                        ) game.currRank =
                                                            rank.getAttribute("value").toInt()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (rex: ResultNotReadyException) {
                throw rex
            }   catch (e: Exception){
                e.printStackTrace()
                return null
        }
        return game
        }

        fun parse(context: Context, title: String): Game? {
            return parse(context, title, null)
        }

        fun parse(context: Context, baseGame: Game): Game? {
            return parse(context, "", baseGame)
        }

    }

}