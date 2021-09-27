package com.medzin.board_game_collector.xml_parser

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.medzin.board_game_collector.R
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.exceptions.ResultNotReadyException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class UserCollectionParser {

    companion object {
        fun parse(context: Context): Array<Game> {
            var games = arrayOf<Game>()

            try {
                val workDir = File("${context.filesDir}/XML")
                if (workDir.exists()){
                    val file = File(workDir, context.getString(R.string.user_collection_xml))
                    if (file.exists()){
                        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                        xmlDoc.documentElement.normalize()

                        if (xmlDoc.getElementsByTagName("message").length > 0)
                            throw ResultNotReadyException()

                        if (xmlDoc.getElementsByTagName("error").length > 0)
                            return arrayOf()

                        val items: NodeList = xmlDoc.getElementsByTagName("item")

                        for (i in 0 until items.length){
                            val itemNode : Node = items.item(i)
                            val game = Game()

                            if (itemNode.nodeType == Node.ELEMENT_NODE){

                                val elem = itemNode as Element
                                game.bggId = elem.getAttribute("objectid").toInt()

                                val children = elem.childNodes

                                for (j in 0 until children.length){
                                    val node = children.item(j)
                                    if (node is Element){
                                        when(node.nodeName){
                                            "name" -> {
                                                game.title = node.textContent
                                            }
                                            "comment" -> {
                                                game.comment = node.textContent
                                            }
                                            "stats" -> {
                                                val ranks = (node.getElementsByTagName("rating")
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
                                games = games.plus(game)
                            }
                        }
                    }
                }

            } catch (rex: ResultNotReadyException) {
                throw rex
            } catch (e: Exception){
                return arrayOf()
            }
            return games
        }
    }

}