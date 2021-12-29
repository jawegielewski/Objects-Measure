package pl.jawegiel.mierzenieopencv.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "WZORCE";

    // Table columns
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String REAL_CM = "real_cm";
    public static final String DEFAULT_BENCHMARK_NAME = "CD";
    public static final String DEFAULT_BENCHMARK_SIZE = "12";

    // Database Information
    static final String DB_NAME = "JAWEGIEL.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL, " + REAL_CM + " TEXT);";

    private static final String INSERT_DEFAULT_BENCHMARK = "insert into " + TABLE_NAME + " ("+NAME+", "+REAL_CM+") values ('" +DEFAULT_BENCHMARK_NAME + "', '" + DEFAULT_BENCHMARK_SIZE + "');";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(INSERT_DEFAULT_BENCHMARK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}