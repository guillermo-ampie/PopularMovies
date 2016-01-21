package com.ampie_guillermo.popularmovies.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.Table;

/**
 * Database & Content Provider support with Schematic
 * https://github.com/SimonVT/schematic.git - Automatically generate a
 * ContentProvider backed by an SQLite database.
 */

@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {

    public static final int VERSION = 1;

    private MoviesDatabase() {
    }

    @Table(MovieColumns.class) @IfNotExists
    public static final String MOVIES_DB = "moviesDB";

}
