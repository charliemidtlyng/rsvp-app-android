package no.charlie.rsvpapp.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import no.charlie.rsvpapp.domain.Event;

import static no.charlie.rsvpapp.persistence.EventDbHelper.CreationResult.ALREADY_EXISTS;
import static no.charlie.rsvpapp.persistence.EventDbHelper.CreationResult.CREATED;
import static no.charlie.rsvpapp.persistence.EventDbHelper.CreationResult.ERROR;

public class EventDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "events.db";


    private static final String EVENT_TABLE_CREATE =
            "CREATE TABLE event (" +
                    "id INTEGER PRIMARY KEY, " +
                    "title TEXT," +
                    "start_time INTEGER," +
                    "reg_start INTEGER," +
                    "reg_end INTEGER" +
                    ");";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public CreationResult create(Event event) {
        if (eventExists(event.id)) {
            return ALREADY_EXISTS;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", event.id);
        values.put("title", event.subject);
        values.put("start_time", event.startTime.getMillis());
        values.put("reg_start", event.regStart.getMillis());
        values.put("reg_end", event.regEnd.getMillis());
        long result = db.insertOrThrow("event", null, values);
        return result == -1 ? ERROR : CREATED;
    }

    public enum CreationResult {
        ALREADY_EXISTS,
        CREATED,
        ERROR
    }

    private boolean eventExists(long eventId) {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), "event", "id = ?",
                new String[]{String.valueOf(eventId)}) == 1;
    }

    public Cursor list() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("event", new String[]{"id", "title", "start_time", "reg_start", "reg_end"}, null, null, null, null, "start_time DESC");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EVENT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
