package com.ampie_guillermo.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Database & Content Provider support with Schematic
 * https://github.com/SimonVT/schematic.git - Automatically generate a
 * ContentProvider backed by an SQLite database.
 */

@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {
    private static final String LOG_TAG = MoviesDatabase.class.getSimpleName();

    protected static final int VERSION = 1;

    private MoviesDatabase() {
    }

    @Table(MovieColumns.class) @IfNotExists
    public static final String MOVIES_TABLE = "movies_tbl";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context context,
                                 SQLiteDatabase db,
                                 int oldVersion,
                                 int newVersion) {
        Log.i(LOG_TAG,
              "Upgrading database: current version: [" + oldVersion + "], new version: ["
              + newVersion + "]");

        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + MOVIES_TABLE);

        // Reset the primary key field (SQLITE autoincrement)
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MOVIES_TABLE + "'");

        // Re-build the database
        onCreate(context, db);
    }
}
