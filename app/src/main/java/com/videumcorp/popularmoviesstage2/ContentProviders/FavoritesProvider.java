package com.videumcorp.popularmoviesstage2.ContentProviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.videumcorp.popularmoviesstage2.SQLites.FavoriteMoviesHelper;

public class FavoritesProvider extends ContentProvider {

    private static final String uri = "content://com.videumcorp.popularmoviesstage2.contentproviders/favorites";

    public static final Uri CONTENT_URI = Uri.parse(uri);

    private static final int FAVORITES = 1;
    private static final int FAVORITES_ID = 2;
    private static final UriMatcher uriMatcher;

    public static final class Favorites implements BaseColumns {
        private Favorites() {
        }

        public static final String COL_ID = "_id";
        public static final String COL_GSON_MOVIE = "gson_movie";
        public static final String COL_GSON_VIDEOS = "gson_videos";
        public static final String COL_GSON_REVIEWS = "gson_reviews";

    }

    private FavoriteMoviesHelper favoriteMoviesHelper;
    private static final String DB_NAME = "db_favorites";
    private static final int DB_VERSION = 1;
    private static final String TABLE_FAVORITES = "favorites";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.videumcorp.popularmoviesstage2.contentproviders", "favorites", FAVORITES);
        uriMatcher.addURI("com.videumcorp.popularmoviesstage2.contentproviders", "favorites/#", FAVORITES_ID);
    }

    @Override
    public boolean onCreate() {

        favoriteMoviesHelper = new FavoriteMoviesHelper(getContext(), DB_NAME, null, DB_VERSION);

        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        String where = selection;
        if (uriMatcher.match(uri) == FAVORITES_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase database = favoriteMoviesHelper.getWritableDatabase();

        return database.query(TABLE_FAVORITES, projection, where, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        long regId;

        SQLiteDatabase database = favoriteMoviesHelper.getWritableDatabase();

        regId = database.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        return ContentUris.withAppendedId(CONTENT_URI, regId);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int cont;

        String where = selection;
        if (uriMatcher.match(uri) == FAVORITES_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase database = favoriteMoviesHelper.getWritableDatabase();

        cont = database.update(TABLE_FAVORITES, values, where, selectionArgs);

        return cont;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int cont;

        //Si es una consulta a un ID concreto construimos el WHERE
        String where = selection;
        if (uriMatcher.match(uri) == FAVORITES_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase database = favoriteMoviesHelper.getWritableDatabase();

        cont = database.delete(TABLE_FAVORITES, where, selectionArgs);

        return cont;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        int match = uriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return "vnd.android.cursor.dir/vnd.videumcorp.favorite";
            case FAVORITES_ID:
                return "vnd.android.cursor.item/vnd.videumcorp.favorite";
            default:
                return null;
        }
    }
}