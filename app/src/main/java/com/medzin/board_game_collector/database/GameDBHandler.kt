package com.medzin.board_game_collector.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import com.medzin.board_game_collector.R
import com.medzin.board_game_collector.database.objects.Game
import com.medzin.board_game_collector.database.objects.GameRank
import com.medzin.board_game_collector.database.objects.Location
import com.medzin.board_game_collector.util.DBQuery
import com.medzin.board_game_collector.util.GameType
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

class GameDBHandler(var context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?,
                    version: Int) : SQLiteOpenHelper(context, DATABASE_NAME,
                                                factory, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "gameDB.db"
        val TABLE_GAMES = "games"
        val COLUMN_ID = "id"
        val COLUMN_TITLE = "titile"
        val COLUMN_ORG_TITLE = "original_title"
        val COLUMN_YEAR_PUBLISHED = "year_published"
        val COLUMN_DESCRIPTION = "description"
        val COLUMN_ORDER_DATE = "order_date"
        val COLUMN_COLLECT_DATE = "collect_date"
        val COLUMN_PRICE = "price"
        val COLUMN_SUGGESTED_DETAIL_PRICE = "suggested_detail_price"
        val COLUMN_EAN_OR_UPC = "ean_or_upc"
        val COLUMN_BGG_ID = "bgg_id"
        val COLUMN_PRODUCT_CODE = "product_code"
        val COLUMN_CURR_RANK = "curr_rank"
        val COLUMN_TYPE = "type"
        val COLUMN_COMMENT = "comment"
        val COLUMN_THUMBNAIL = "thumbnail"
        val COLUMN_LOCAL_ID = "localisation_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " + TABLE_GAMES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TITLE + " TEXT," +
                COLUMN_ORG_TITLE + " TEXT," + COLUMN_YEAR_PUBLISHED + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT," + COLUMN_ORDER_DATE + " TEXT," +
                COLUMN_COLLECT_DATE + " TEXT," + COLUMN_PRICE + " TEXT," +
                COLUMN_SUGGESTED_DETAIL_PRICE + " TEXT," + COLUMN_EAN_OR_UPC + " INTEGER," +
                COLUMN_BGG_ID + " INTEGER," + COLUMN_PRODUCT_CODE + " TEXT," +
                COLUMN_CURR_RANK + " INTEGER," + COLUMN_TYPE + " TEXT," +
                COLUMN_COMMENT + " TEXT," + COLUMN_THUMBNAIL + " BLOB," +
                COLUMN_LOCAL_ID + " INTEGER)")
        db.execSQL(CREATE_GAMES_TABLE)
        DesignerDBHelper.onCreate(db)
        ArtistDBHelper.onCreate(db)
        LocationDBHelper.onCreate(db)
        RankDBHelper.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addGame(game: Game){
        val values = ContentValues()
        val db = this.writableDatabase
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_ORG_TITLE, game.originalTitle)
        values.put(COLUMN_YEAR_PUBLISHED, game.yearPublished)
        values.put(COLUMN_DESCRIPTION, game.description)
        if (game.dateOfOrder != null) {
            values.put(
                COLUMN_ORDER_DATE,
                game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        if (game.dateOfCollecting != null){
            values.put(
                COLUMN_COLLECT_DATE,
                game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        values.put(COLUMN_PRICE, game.pricePaid)
        values.put(COLUMN_SUGGESTED_DETAIL_PRICE, game.suggestedDetailPrice)
        values.put(COLUMN_EAN_OR_UPC, game.eanOrUpcCode)
        values.put(COLUMN_BGG_ID, game.bggId)
        values.put(COLUMN_PRODUCT_CODE, game.productCode)
        values.put(COLUMN_CURR_RANK, game.currRank)
        values.put(COLUMN_TYPE, game.type?.name)
        if (game.location != null){
            values.put(
                    COLUMN_LOCAL_ID,
                    LocationDBHelper.getIdOfLocation(game.location?.name, db)
            )
        }
        if(game.comment != null){
            values.put(COLUMN_COMMENT, game.comment)
        }
        if(game.thumbnail != null){
            val stream = ByteArrayOutputStream()
            game.thumbnail?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            values.put(COLUMN_THUMBNAIL, stream.toByteArray())
        }

        db.insert(TABLE_GAMES, null, values)


        game.id = findGame(game.title!!, db)!!.id

        ArtistDBHelper.addArtist(game.id, game.artists, db)
        DesignerDBHelper.addDesigner(game.id, game.designers, db)

        db.close()
    }

    fun updateGame(game: Game){
        val values = ContentValues()
        val db = this.writableDatabase
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_ORG_TITLE, game.originalTitle)
        values.put(COLUMN_YEAR_PUBLISHED, game.yearPublished)
        values.put(COLUMN_DESCRIPTION, game.description)
        if (game.dateOfOrder != null) {
            values.put(
                COLUMN_ORDER_DATE,
                game.dateOfOrder?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        if (game.dateOfCollecting != null){
            values.put(
                COLUMN_COLLECT_DATE,
                game.dateOfCollecting?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        values.put(COLUMN_PRICE, game.pricePaid)
        values.put(COLUMN_SUGGESTED_DETAIL_PRICE, game.suggestedDetailPrice)
        values.put(COLUMN_EAN_OR_UPC, game.eanOrUpcCode)
        values.put(COLUMN_BGG_ID, game.bggId)
        values.put(COLUMN_PRODUCT_CODE, game.productCode)
        values.put(COLUMN_CURR_RANK, game.currRank)
        values.put(COLUMN_TYPE, game.type?.name)
        if (game.location != null){
            values.put(
                COLUMN_LOCAL_ID,
                LocationDBHelper.getIdOfLocation(game.location?.name, db)
            )
        }
        if(game.comment != null){
            values.put(COLUMN_COMMENT, game.comment)
        }
        if(game.thumbnail != null){
            val stream = ByteArrayOutputStream()
            game.thumbnail?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            values.put(COLUMN_THUMBNAIL, stream.toByteArray())
        }

        db.update(TABLE_GAMES, values, "$COLUMN_ID=?", arrayOf(game.id.toString()))

        ArtistDBHelper.updateArtist(game.id, game.artists, db)
        DesignerDBHelper.updateDesigner(game.id, game.designers, db)

        db.close()
    }

    fun findGame(gameTitle: String,  db: SQLiteDatabase):Game? {
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_TITLE LIKE ?"
        val cursor = db.rawQuery(query, arrayOf(gameTitle))
        var game: Game? = null

        if(cursor.moveToFirst()){
            game = buildGameFromData(cursor, db)
            cursor.close()
        }
        return game
    }

    fun findGameNonExtension(gameTitle: String,  db: SQLiteDatabase):Game? {
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_TITLE LIKE ? AND $COLUMN_TYPE != ?"
        val cursor = db.rawQuery(query, arrayOf(gameTitle, GameType.EXPANSION.name))
        var game: Game? = null

        if(cursor.moveToFirst()){
            game = buildGameFromData(cursor, db)
            cursor.close()
        }
        return game
    }

    fun findGame(gameId: Int,  db: SQLiteDatabase):Game? {
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID=?"
        val cursor = db.rawQuery(query, arrayOf(gameId.toString()))
        var game: Game? = null

        if(cursor.moveToFirst()){
            game = buildGameFromData(cursor, db)
            cursor.close()
        }
        return game
    }

    fun findGame(gameId: Int):Game? {
        val db = this.writableDatabase
        var game: Game? = findGame(gameId, db)
        db.close()
        return game
    }

    fun findGame(gameTitle: String):Game? {
        val db = this.writableDatabase
        var game: Game? = findGame(gameTitle, db)
        db.close()
        return game
    }

    fun deleteGame(gameTitle: String): Boolean {
        var result = false
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_TITLE LIKE ?"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, arrayOf(gameTitle))
        if(cursor.moveToFirst()){
            val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
            DesignerDBHelper.deleteDesigners(id, db)
            ArtistDBHelper.deleteArtists(id, db)
            ArchiveRankDBHelper.deleteRanks(id, db)
            db.delete(TABLE_GAMES, "$COLUMN_ID = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    fun deleteGame(gameId: Int): Boolean {
        var result = false
        val db = this.writableDatabase
        if (db.delete(TABLE_GAMES, "$COLUMN_ID = ?", arrayOf(gameId.toString())) > 0){
            result = true
        }
        db.close()
        return result
    }

    fun getGamesCollection() = sequence<Game> {
        val query = "SELECT * FROM $TABLE_GAMES"
        val db = this@GameDBHandler.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            yield(buildGameFromData(cursor, db))
        }
        cursor.close()
        db.close()
    }

    fun getGamesCollectionNonExtensions() = sequence<Game> {
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_TYPE != ?"
        val db = this@GameDBHandler.writableDatabase
        val cursor = db.rawQuery(query, arrayOf(GameType.EXPANSION.name))

        while (cursor.moveToNext()) {
            yield(buildGameFromData(cursor, db))
        }
        cursor.close()
        db.close()
    }

    fun buildGameFromData(cursor: Cursor, db: SQLiteDatabase): Game {
        val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
        val title = DBQuery.queryNullable(cursor, COLUMN_TITLE)
        val originalTitle = DBQuery.queryNullable(cursor, COLUMN_ORG_TITLE)
        val yearPublished = Integer.parseInt(cursor.getString(cursor.getColumnIndex(
            COLUMN_YEAR_PUBLISHED)))
        val designers = DesignerDBHelper.findDesigners(id, db)
        val artists = ArtistDBHelper.findArtists(id, db)
        val description = DBQuery.queryNullable(cursor, COLUMN_DESCRIPTION)
        val dateOfOrder = DBQuery.queryDate(cursor, COLUMN_ORDER_DATE)
        val dateOfCollecting = DBQuery.queryDate(cursor, COLUMN_COLLECT_DATE)
        val pricePaid = DBQuery.queryNullable(cursor, COLUMN_PRICE)
        val suggestedDetailPrice = DBQuery.queryNullable(cursor, COLUMN_SUGGESTED_DETAIL_PRICE)
        val eanOrUpcCode = DBQuery.queryNullableInt(cursor, COLUMN_EAN_OR_UPC)
        val bggId = DBQuery.queryNullableInt(cursor, COLUMN_BGG_ID)
        val productCode = DBQuery.queryNullable(cursor, COLUMN_PRODUCT_CODE)
        val currRank = DBQuery.queryNullableInt(cursor, COLUMN_CURR_RANK)
        val type = GameType.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)))
//        val type = DBQuery.queryNullable(cursor, COLUMN_TYPE)
        val comment = DBQuery.queryNullable(cursor, COLUMN_COMMENT)
        val thumbnail = DBQuery.queryBitmap(cursor, COLUMN_THUMBNAIL)
        val localisation = if (cursor.isNull(cursor.getColumnIndex(COLUMN_LOCAL_ID))) null else LocationDBHelper.findLocation(
                DBQuery.queryNullableInt(cursor, COLUMN_LOCAL_ID), db)
        return Game(id, title, originalTitle, yearPublished, designers, artists, description,
            dateOfOrder, dateOfCollecting, pricePaid, suggestedDetailPrice, eanOrUpcCode,
            bggId, productCode, currRank, type, comment, thumbnail, localisation)
    }

    fun findArchiveRanks(gameTitle: String): MutableList<GameRank>{
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_TITLE LIKE \"?\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, arrayOf(gameTitle))
        var result = mutableListOf<GameRank>()

        if(cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
            result = ArchiveRankDBHelper.findRanks(id, db)
            cursor.close()
        }
        db.close()
        return result
    }

    fun addLocation(location: Location){
        val db = this.writableDatabase
        LocationDBHelper.addLocation(location, db)
        db.close()
    }

    fun addArchiveRanke(gameId: Int, rank: GameRank){
        val db = this.writableDatabase
        ArchiveRankDBHelper.addRank(gameId, rank, db)
        db.close()
    }

    fun getLocations(): Array<Location>{
        val db = this.writableDatabase
        var result: Array<Location> = emptyArray()
        LocationDBHelper.getLocations(db).forEach {
            result = result.plus(it) }
        db.close()
        return result
    }

    fun updateLocation(location: Location){
        val db = this.writableDatabase
        LocationDBHelper.updateLocation(location, db)
        db.close()
    }

}