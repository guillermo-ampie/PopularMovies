package com.ampie_guillermo.popularmovies.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Database & Content Provider support with Schematic
 * https://github.com/SimonVT/schematic.git - Automatically generate a
 * ContentProvider backed by an SQLite database.
 */

public interface MovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    String ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull @Unique
    String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    String ORIGINAL_TILE = "original_title";

    @DataType(DataType.Type.TEXT)
    String RELEASE_YEAR = "release_year";

    @DataType(DataType.Type.TEXT)
    String OVERVIEW = "overview";

    @DataType(DataType.Type.TEXT) @NotNull
    String POSTER_COMPLETE_URI = "poster_complete_uri";

    @DataType(DataType.Type.REAL)
    String VOTE_AVERAGE = "vote_average";

    @DataType(DataType.Type.INTEGER)
    String VOTE_COUNT = "vote_count";

    @DataType(DataType.Type.INTEGER)
    String FAVORITE = "favorite";
}
