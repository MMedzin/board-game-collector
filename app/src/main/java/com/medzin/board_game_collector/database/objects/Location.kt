package com.medzin.board_game_collector.database.objects

class Location {
    var id: Int = 0
    var name: String? = null
    var comment: String? = null

    constructor(id: Int, name: String){
        this.id = id
        this.name = name
    }

    constructor(id: Int, name: String?, comment: String?){
        this.id = id
        this.name = name
        this.comment = comment
    }

    constructor(name: String, comment: String){
        this.name = name
        this.comment = comment
    }

    constructor(name: String){
        this.name = name
    }

}