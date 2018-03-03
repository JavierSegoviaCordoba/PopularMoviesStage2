package com.videumcorp.popularmoviesstage2.SQLites;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteMoviesHelper extends SQLiteOpenHelper {
    private static final String TABLE_favorites = "favorites";
    private static final String KEY_id = "_id";
    private static final String KEY_gson_movie = "gson_movie";
    private static final String KEY_gson_videos = "gson_videos";
    private static final String KEY_gson_reviews = "gson_reviews";

    private final String sqlCreate = "CREATE TABLE " + TABLE_favorites + "("
            + KEY_id + " INTEGER PRIMARY KEY,"
            + KEY_gson_movie + " TEXT,"
            + KEY_gson_videos + " TEXT,"
            + KEY_gson_reviews + " TEXT"
            + ")";

    public FavoriteMoviesHelper(Context contexto, String database_name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(contexto, database_name, cursorFactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int versionAnterior, int versionNueva) {
        database.execSQL("DROP TABLE IF EXISTS favorites");
        database.execSQL(sqlCreate);
    }
}