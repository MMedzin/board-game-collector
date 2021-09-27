package com.medzin.board_game_collector.xml_parser

import android.content.Context
import com.medzin.board_game_collector.R
import com.medzin.board_game_collector.exceptions.ResultNotReadyException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class SearchedGamesParser {

    companion object {
        fun parse(context: Context): Map<String, Int> {
            var searchedGames = mapOf<String, Int>()
            try{
                val workDir = File("${context.filesDir}/XML")
                if (workDir.exists()){
                    val file = File(workDir, context.getString(R.string.game_search_xml))
                    if (file.exists()){
                        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                        xmlDoc.documentElement.normalize()

                        if (xmlDoc.getElementsByTagName("message").length > 0) {
                            throw ResultNotReadyException()
                        }

                        if (xmlDoc.getElementsByTagName("error").length > 0) {
                            return mapOf()
                        }


                        val items: NodeList = xmlDoc.getElementsByTagName("item")

                        for (i in 0 until items.length){
                            val itemNode : Node = items.item(i)

                            if (itemNode.nodeType == Node.ELEMENT_NODE){

                                val elem = itemNode as Element
                                val id = elem.getAttribute("id").toInt()
                                val children = elem.childNodes
                                for (j in 0 until children.length){
                                    val node = children.item(j)
                                    if (node is Element){
                                        if (node.nodeName == "name"){
                                            searchedGames = searchedGames.plus(Pair(
                                                    node.getAttribute("value"), id
                                            ))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            } catch (e: Exception){
                return mapOf()
            }
            return searchedGames
        }
    }

}