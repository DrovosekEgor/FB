package com.egor.drovosek.kursv01.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.graphics.Bitmap;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.util.List;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.egor.drovosek.kursv01.Misc.Team;

import static android.support.v7.appcompat.R.id.image;
import static com.egor.drovosek.kursv01.DB.Schema.*;

/**
 * Created by Drovosek on 31/01/2017.
 */

public class FootballDBHelper extends SQLiteOpenHelper
{
        Context _context;
        private String TRACE = "FootballDBHelper";


        public FootballDBHelper(Context context)
        {
            super(context, Schema.DATABASE_NAME, null, Schema.DATABASE_VERSION);
            _context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_TEAMS);
            db.execSQL(CREATE_TABLE_MATCHES);
            db.execSQL(CREATE_TABLE_PLAYERS);
            db.execSQL(CREATE_TABLE_GOALS);
            db.execSQL(CREATE_TABLE_STAFF);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            if (oldVersion < newVersion)
            {
                //this.DeleteTables();
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);

                onCreate(db);
            }
        }

        public void DeleteTables()
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
            db.close();
        }

        public void ClearTables()
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_MATCHES, null, null);
            db.delete(TABLE_TEAMS, null, null);
            db.close();
        }

        public void ClearTable(String tableName)
        {
             SQLiteDatabase db = this.getWritableDatabase();
             db.delete(tableName, null, null);
             db.close();
        }

        /*---------------------------------------------
          возвращает курсор со всеми коммандами за
          определенный год
          "select * from teams where season=inSeason"
         ---------------------------------------------*/
        public Cursor getAllTeams(int season) throws SQLException
        {
            SQLiteDatabase db = this.getReadableDatabase();

            String selectQuery = "SELECT  * FROM " + TABLE_TEAMS +
                                 " WHERE " + TEAMS_SEASON + "=" + String.valueOf(season);

            return db.rawQuery(selectQuery, null);

        }

          /*---------------------------------------------
        возвращает курсор со всеми матчами за определенный год

          SELECT m.round, HOME.title, GUEST.title, m.score_home, m.score_guest, m.datem, m.place
          FROM matches AS m
          JOIN teams AS HOME ON m.home_team_id=HOME.T_ID
          JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID
          where m.season=2016;
        ---------------------------------------------*/
        public Cursor getMatchesSeason(int season) throws SQLException
        {
            SQLiteDatabase db = this.getReadableDatabase();

            /*String selectQuery = "SELECT m.round, HOME.title AS home_title, GUEST.title AS guest_title, m.score_home, m.score_guest, m.datem, m.location "
                    + "FROM "
                    + TABLE_MATCHES + " AS m "
                    + " JOIN " + TABLE_TEAMS + " AS HOME ON m." + MATCHES_HOME_TEAM_ID + "=HOME." + TEAMS_M_ID
                    + " JOIN " + TABLE_TEAMS + " AS GUEST ON m." + MATCHES_HOME_TEAM_ID + "=GUEST." + TEAMS_M_ID
                    + " WHERE m." + MATCHES_SEASON + "=" + String.valueOf(season);*/

            String selectQuery = "SELECT m.round as _id, m.round, HOME.title AS home_title, GUEST.title AS guest_title, m.score_home, m.score_guest, m.datem, m.location FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+ String.valueOf(season)+";";

            Cursor mCursor = db.rawQuery(selectQuery, null);

         return mCursor;

        }
        /*---------------------------------------------
        возвращает курсор со всеми матчами за определенный год, для указанной комманды

        SELECT m.round, HOME.title, GUEST.title, m.score_home, m.score_guest, m.datem, m.place
        FROM matches AS m
        JOIN teams AS HOME ON m.home_team_id=HOME.T_ID
        JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID
        where m.season=2016;
        ---------------------------------------------*/
        public Cursor getMatchesSeason(int season, String teamName) throws SQLException
        {
            SQLiteDatabase db = this.getReadableDatabase();

            /*String selectQuery = "SELECT m.round, HOME.title AS home_title, GUEST.title AS guest_title, m.score_home, m.score_guest, m.datem, m.location "
                    + "FROM "
                    + TABLE_MATCHES + " AS m "
                    + " JOIN " + TABLE_TEAMS + " AS HOME ON m." + MATCHES_HOME_TEAM_ID + "=HOME." + TEAMS_M_ID
                    + " JOIN " + TABLE_TEAMS + " AS GUEST ON m." + MATCHES_HOME_TEAM_ID + "=GUEST." + TEAMS_M_ID
                    + " WHERE m." + MATCHES_SEASON + "=" + String.valueOf(season)
                    + " and (HOME.title='teamName' or GUEST.title='teamName');"*/

            //String selectQuery = "SELECT m.round as _id, m.round, HOME.title AS home_title, GUEST.title AS guest_title, m.score_home, m.score_guest, m.datem, m.location FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+ String.valueOf(season) + " and (HOME.title='"+teamName+"' or GUEST.title='"+teamName+"');";
            String selectQuery = "SELECT m.round as _id, " +
                    "m.round, " +
                    "HOME.title AS home_title, " +
                    "HOME.emblem AS homeLogo, " +
                    "GUEST.title AS guest_title, " +
                    "GUEST.emblem AS guestLogo, " +
                    "m.score_home, " +
                    "m.score_guest, " +
                    "m.datem, " +
                    "m.location " +
                    "FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+String.valueOf(season)+
                    " and (HOME.title='"+teamName+"' or GUEST.title='"+teamName+"');";

            Cursor mCursor = db.rawQuery(selectQuery, null);

            return mCursor;

        }

    public Cursor getMatchesAwaySeason(int season, String teamName) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT m.round as _id, m.round, HOME.title AS home_title, GUEST.title AS guest_title, m.score_home, m.score_guest, m.datem, m.location FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+ String.valueOf(season) + " and (GUEST.title='"+teamName+"');";
        String selectQuery = "SELECT m.round as _id, " +
                "m.round, " +
                "HOME.title AS home_title, " +
                "HOME.emblem AS homeLogo, " +
                "GUEST.title AS guest_title, " +
                "GUEST.emblem AS guestLogo, " +
                "m.score_home, " +
                "m.score_guest, " +
                "m.datem, " +
                "m.location " +
                "FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+String.valueOf(season)+
                " and (GUEST.title='"+teamName+"');";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        return mCursor;

    }


    public Cursor getMatchesHomeSeason(int season, String teamName) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT m.round as _id, m.round, HOME.title AS home_title, GUEST.title AS guest_title, m.score_home, m.score_guest, m.datem, m.location FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+ String.valueOf(season) + " and (GUEST.title='"+teamName+"');";
        String selectQuery = "SELECT m.round as _id, " +
                "m.round, " +
                "HOME.title AS home_title, " +
                "HOME.emblem AS homeLogo, " +
                "GUEST.title AS guest_title, " +
                "GUEST.emblem AS guestLogo, " +
                "m.score_home, " +
                "m.score_guest, " +
                "m.datem, " +
                "m.location " +
                "FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+String.valueOf(season)+
                " and (HOME.title='"+teamName+"');";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        return mCursor;

    }

    /*
    * Возвращает текущее количество матчей в базе данных */
    public int getNumberOfMatches(int season) throws SQLException
    {
        int numOfMatches = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "select COUNT(*) as numofmatches from matches where season="+ String.valueOf(season)+";";

        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();

            if(mCursor.getCount()>0) {
                int colIndex = mCursor.getColumnIndex("numofmatches");
                numOfMatches = mCursor.getInt(colIndex);
            }
        }

        db.close();

        return numOfMatches;
    }

    public int getLastCompleteRound(int season) throws SQLException
    {
        int lastRound = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "select round from matches where season="+ String.valueOf(season)+" AND status='COMPLETED' order BY round DESC;";

        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();

            if(mCursor.getCount()>0) {
                int colIndex = mCursor.getColumnIndex("round");
                lastRound = mCursor.getInt(colIndex);
            }
        }

        return lastRound;
    }

    public int getFutureRounds(int season) throws SQLException
    {
        int lastRound = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        return lastRound;
    }

    public Cursor getRounds(int season) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select distinct round, round as _id from matches where season="+ String.valueOf(season)+";";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        return mCursor;
    }

    public int getNumberRounds(int season) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select distinct round, round as _id from matches where season="+ String.valueOf(season)+";";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        if (mCursor != null)
            return mCursor.getCount();

        return 0;
    }

    public int getInProgressRounds(int season) throws SQLException
    {
        int lastRound = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "select COUNT(*) as numofmatches from matches where season="+ String.valueOf(season)+";";

        return lastRound;
    }
    /*---------------------------------------------
  возвращает курсор со всеми матчами за определенный год и тур

    SELECT m.round, HOME.title, GUEST.title, m.score_home, m.score_guest, m.datem, m.place
    FROM matches AS m
    JOIN teams AS HOME ON m.home_team_id=HOME.T_ID
    JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID
    where m.season=2016 AND m.round=1;
  ---------------------------------------------*/
    public Cursor getMatchesSeasonRound(int season, int round) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

            /*String selectQuery = "SELECT m.round, HOME.title AS home_title, GUEST.title AS guest_title, m.score_home, m.score_guest, m.datem, m.location "
                    + "FROM "
                    + TABLE_MATCHES + " AS m "
                    + " JOIN " + TABLE_TEAMS + " AS HOME ON m." + MATCHES_HOME_TEAM_ID + "=HOME." + TEAMS_M_ID
                    + " JOIN " + TABLE_TEAMS + " AS GUEST ON m." + MATCHES_HOME_TEAM_ID + "=GUEST." + TEAMS_M_ID
                    + " WHERE m." + MATCHES_SEASON + "=" + String.valueOf(season);*/

        String selectQuery = "SELECT m.round as _id, " +
                "m.round, " +
                "HOME.title AS home_title, " +
                "HOME.emblem AS homeLogo, " +
                "GUEST.title AS guest_title, " +
                "GUEST.emblem AS guestLogo, " +
                "m.score_home, " +
                "m.score_guest, " +
                "m.datem, " +
                "m.location " +
                "FROM matches AS m JOIN teams AS HOME ON m.home_team_id=HOME.T_ID JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID where m.season="+  String.valueOf(season) +" AND m.round=" + String.valueOf(round)+";";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        return mCursor;

    }

    /*---------------------------------------------
    возвращает статистику за определенный год

  SELECT teamName,
		 SUM(NOM) as 'Игр',
		 SUM(Win) as 'Побед',
		 SUM(Draw) as 'Ничьи',
		 SUM(Lost) as 'Проигр',
         SUM(score_in) as 'Число забитых',
		 SUM(score_out) as 'Число пропущенных' ,
		 SUM(Win*3 + Draw) as points
  FROM
    (SELECT m.M_ID, HOME.T_ID as teamID, HOME.title as teamName, SUM(m.score_home) AS score_in, SUM(m.score_guest) AS score_out,
     	COUNT(case when score_home=score_guest then 1 else null end) AS Draw,
		COUNT(case when score_home>score_guest then 1 else null end) AS Win,
		COUNT(case when score_home<score_guest then 1 else null end) AS Lost,
		COUNT(HOME.title) AS NOM
        FROM matches AS m
        JOIN teams AS HOME ON m.home_team_id=HOME.T_ID
        JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID
        GROUP BY HOME.title
    union
    SELECT m.M_ID, GUEST.T_ID as teamID, GUEST.title as teamName, SUM(m.score_guest) AS score_in, SUM(m.score_home) AS score_out,
	      COUNT(case when score_home=score_guest then 1 else null end) AS Draw,
		  COUNT(case when score_home<score_guest then 1 else null end) AS Win,
		  COUNT(case when score_home>score_guest then 1 else null end) AS Lost,
		  COUNT(GUEST.title) AS NOM
          FROM matches AS m
          JOIN teams AS HOME ON m.home_team_id=HOME.T_ID
          JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID
          GROUP BY GUEST.title
      )
    GROUP BY teamName ORDER BY points DESC;
---------------------------------------------*/
    public Cursor getStats(int season) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT teamName, " +
                " teamID as _id, " +
                " teamLogo, " +
                " SUM(NOM) as numberofgames, " +
                " SUM(Win) as wins, " +
                " SUM(Draw) as draws, " +
                " SUM(Lost) as losts, " +
                " SUM(score_in) as goalsfor, " +
                " SUM(score_out) as goalsagainst, " +
                " SUM(Win*3 + Draw) as points " +

                " FROM " +
                "    (SELECT m.M_ID, HOME.T_ID as teamID, HOME.title as teamName, HOME.emblem as teamLogo, SUM(m.score_home) AS score_in, SUM(m.score_guest) AS score_out, " +
                "       COUNT(case when score_home=score_guest then 1 else null end) AS Draw, " +
                "       COUNT(case when score_home>score_guest then 1 else null end) AS Win, " +
                "       COUNT(case when score_home<score_guest then 1 else null end) AS Lost, " +
                "       COUNT(HOME.title) AS NOM " +
                "     FROM matches AS m " +
                "        JOIN teams AS HOME ON m.home_team_id=HOME.T_ID " +
                "        JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID " +
                "     WHERE m.season=" + String.valueOf(season) +
                "          AND m.status='COMPLETED' " +
                "     GROUP BY HOME.title " +
                "    union " +
                "    SELECT m.M_ID, GUEST.T_ID as teamID, GUEST.title as teamName, GUEST.emblem as teamLogo, SUM(m.score_guest) AS score_in, SUM(m.score_home) AS score_out, " +
                "       COUNT(case when score_home=score_guest then 1 else null end) AS Draw, " +
                "       COUNT(case when score_home<score_guest then 1 else null end) AS Win, " +
                "       COUNT(case when score_home>score_guest then 1 else null end) AS Lost, " +
                "       COUNT(GUEST.title) AS NOM " +
                "    FROM matches AS m " +
                "          JOIN teams AS HOME ON m.home_team_id=HOME.T_ID " +
                "          JOIN teams AS GUEST ON m.guest_team_id=GUEST.T_ID " +
                "        WHERE m.season=" + String.valueOf(season) +
                "          AND m.status='COMPLETED' " +
                "          GROUP BY GUEST.title " +
                "      ) " +
                "    GROUP BY teamName ORDER BY points DESC;";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        return mCursor;

    }

    public Cursor getBestPlayers(int season) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        /*
            SELECT p.first_name, p.second_name, t.title AS teamName, COUNT(g.match_id) AS numberOfGoals
            FROM players AS p
            JOIN goals AS g ON p.P_ID=g.player_id
            JOIN matches AS m ON g.match_id=m.M_ID AND m.season=2016
            JOIN teams AS t ON p.team_id=t.T_ID
            GROUP BY p.second_name
            ORDER BY numberOfGoals;
         */

        String selectQuery =
                "SELECT p.P_ID AS _id, "    +
                        "p.first_name AS first_name, "    +
                        "p.second_name AS second_name, " +
                        "t.title AS teamName, "          +
                        "t.emblem AS logo, "             +
                        "COUNT(g.match_id) AS numberOfGoals " +
                " FROM players AS p " +
                " JOIN goals AS g ON p.P_ID=g.player_id " +
                " JOIN matches AS m ON g.match_id=m.M_ID AND m.season=" + String.valueOf(season) +
                " JOIN teams AS t ON p.team_id=t.T_ID " +
                " GROUP BY p.second_name " +
                " ORDER BY numberOfGoals DESC;";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        return mCursor;

    }

    public Cursor getMembersOfTeam(int season, String inTeamName) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        /*
          SELECT st.player_id AS _id,
                 st.position,
                 plr.first_name,
                 plr.second_name from staff AS st
         JOIN players AS plr ON st.player_id=plr.P_ID
          WHERE st.season=2016 AND st.team_id=3;
         */
        int teamID = getTeamID(inTeamName);

        String selectQuery =
                "SELECT st.player_id AS _id,  "    +
                       "st.position AS position, "    +
                       "plr.first_name AS first_name, " +
                       "plr.second_name AS second_name from staff AS st " +
                       "JOIN players AS plr ON st.player_id=plr.P_ID " +
                       "WHERE st.season=" + String.valueOf(season) +
                        " AND st.team_id=" + String.valueOf(teamID) +
                        " AND st.position!='тренер'" + ";";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        return mCursor;

    }
    public String getCouchOfTeam(int season, String inTeamName) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        int teamID = getTeamID(inTeamName);
        String name= "";

        String selectQuery =
                "SELECT st.player_id AS _id,  "    +
                        "st.position AS position, "    +
                        "plr.first_name AS first_name, " +
                        "plr.second_name AS second_name from staff AS st " +
                        "JOIN players AS plr ON st.player_id=plr.P_ID " +
                        "WHERE st.season=" + String.valueOf(season) +
                        " AND st.team_id=" + String.valueOf(teamID) +
                        " AND st.position='тренер'" + ";";

        Cursor mCursor = db.rawQuery(selectQuery, null);

        if (mCursor !=null) {
            mCursor.moveToFirst();

            if(mCursor.getCount()>0) {
               name = mCursor.getString( mCursor.getColumnIndex("first_name")) +
                              mCursor.getString( mCursor.getColumnIndex("second_name"));
            }
        }

        return name;

    }

        /*---------------------------------------------
        возвращает уникальное ID комманды
         ---------------------------------------------*/
        public int getTeamID(String inTeamName) throws SQLException
        {
            SQLiteDatabase db = this.getReadableDatabase();

            String selectQuery = "SELECT  " + TEAMS_M_ID + " FROM " + TABLE_TEAMS +
                    " WHERE " + TEAMS_TITLE + "='" + inTeamName + "'";

            Cursor mCursor = db.rawQuery(selectQuery, null);
            if (mCursor !=null && mCursor.getCount()>0)
            {
                mCursor.moveToFirst();
                return mCursor.getInt(mCursor.getColumnIndex(Schema.TEAMS_M_ID));
            }
            else
               return -1;
        }

    /*---------------------------------------------
возвращает адрес сайта комманды
 ---------------------------------------------*/
    public String getTeamURL(String inTeamName) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  " + TEAMS_SITE + " FROM " + TABLE_TEAMS +
                " WHERE " + TEAMS_TITLE + "='" + inTeamName + "'";

        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor !=null && mCursor.getCount()>0)
        {
            mCursor.moveToFirst();
            return mCursor.getString(mCursor.getColumnIndex(Schema.TEAMS_SITE));
        }
        else
            return "Нет сайта!";
    }

        /*-------------------------------------------------------
            возвращает уникальное ID игрока по имени и фамилии
        -------------------------------------------------------*/
    public int getPlayerID(String inFirstName, String inSecondName) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  " + PLAYERS_P_ID + " FROM " + TABLE_PLAYERS +
                " WHERE " + PLAYERS_FIRST_NAME + "='" + inFirstName + "'" +
                " AND " + PLAYERS_SECOND_NAME + "='" + inSecondName + "'";

        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor !=null) {
            mCursor.moveToFirst();
            if(mCursor.getCount()>0) {
                int colIndex = mCursor.getColumnIndex(PLAYERS_P_ID);
                return mCursor.getInt(colIndex);
            }
            else
                return -1;
        }
        else
            return -1;
    }

    /*-------------------------------------------------------
            возвращает уникальное ID матча по ID домашней комманды
            и дате
        -------------------------------------------------------*/
    public int getMatchID(int inHomeTeamID, String inDateAndTime) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  " + MATCHES_M_ID + " FROM " + TABLE_MATCHES +
                " WHERE " + MATCHES_HOME_TEAM_ID + "=" + inHomeTeamID +
                " AND " + MATCHES_DATEM + "='" + inDateAndTime + "'";

        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor !=null &&
            mCursor.getCount() > 0)
        {
            mCursor.moveToFirst();
            int index = mCursor.getColumnIndex(Schema.MATCHES_M_ID);
            int mID = mCursor.getInt(index);
            return mID;
        }
        else
            return -1;
    }

    /*--------------------------------------------------------------------------------------
        Возвращает количество матчей в определенном туре данного сезона
       SELECT COUNT(round) as matchesinround FROM matches WHERE season=2016 and round=1;
     -------------------------------------------------------------------------------------*/
        public int getCountRoundsInSeason(int season, int round) throws SQLException
        {
            SQLiteDatabase db = this.getReadableDatabase();

            /*String selectQuery = "SELECT  " + TEAMS_M_ID + " FROM " + TABLE_TEAMS +
                    " WHERE " + TEAMS_TITLE + "='" + inTeamName + "'";*/
            String selectQuery = "SELECT COUNT(round) as numberofrounds FROM matches WHERE season="+String.valueOf(season)+" and round="+String.valueOf(round)+";";

            Cursor mCursor = db.rawQuery(selectQuery, null);
            if (mCursor !=null&& mCursor.getCount()>1) {
                mCursor.moveToFirst();
                return mCursor.getInt(mCursor.getColumnIndex("numberofrounds"));
            }
            else
                return 0;
        }

        public void addTeam(ContentValues _teamValue)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            long rowID = db.insert(TABLE_TEAMS, null, _teamValue);
            if (rowID < 0)
                Log.v(TRACE, "addTeam FAILED");
        }

        public ContentValues createTeamValue(  String inTitle,
                                            String inCity,
                                            Bitmap inLogo,
                                            String inSite,
                                            int inSeason )
        {
            ContentValues teamValue = new ContentValues();
            teamValue.put(TEAMS_TITLE, inTitle);
            teamValue.put(TEAMS_CITY, inCity);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            inLogo.compress(Bitmap.CompressFormat.PNG, 100, out);
            byte[] bufferWithLogo =out.toByteArray();

            teamValue.put(TEAMS_EMBLEM, bufferWithLogo);

            teamValue.put(TEAMS_SITE, inSite);
            teamValue.put(TEAMS_SEASON, inSeason);
            teamValue.put(TEAMS_WIN, 0);
            teamValue.put(TEAMS_DRAW, 0);
            teamValue.put(TEAMS_LOST, 0);

            return teamValue;
        }
           /* как конвертировать JPEG to BLOB
            Bitmap image = BitmapFactory.decodeResource(_context.getResources(), R.drawable.pushups);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            byte[] buffer=out.toByteArray();
           */

        public ContentValues createMatchValue( String inHomeTeam,
                                           String inGuestTeam,
                                           String inScoreHome,
                                           String inScoreGuest,
                                           String inSeason,
                                           String inRound,
                                           String inDateAndTime,
                                           String inLocation,
                                           String inStatus)
        {

        ContentValues teamValue = new ContentValues();

        teamValue.put(MATCHES_HOME_TEAM_ID, getTeamID(inHomeTeam));
        teamValue.put(MATCHES_GUEST_TEAM_ID, getTeamID(inGuestTeam));
        teamValue.put(MATCHES_SCORE_HOME, Integer.valueOf(inScoreHome));
        teamValue.put(MATCHES_SCORE_GUEST, Integer.valueOf(inScoreGuest));
        teamValue.put(MATCHES_ROUND, Integer.valueOf(inRound));
        teamValue.put(MATCHES_SEASON, Integer.valueOf(inSeason));


        /*Date dateAndTime = new Date();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm"); //01.04.2016	18:30
        try {
            dateAndTime = df.parse(inDateAndTime);
        }
        catch(ParseException pe) {
            pe.printStackTrace();
        }*/

        teamValue.put(MATCHES_DATEM, inDateAndTime); //need to convert to Date before add to table

        teamValue.put(MATCHES_LOCATION, inLocation);
        teamValue.put(MATCHES_STATUS, inStatus);

        return teamValue;
    }

        public void addMatch(ContentValues _matchValue)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            long rowID = db.insert(TABLE_MATCHES, null, _matchValue);
            if (rowID < 0)
                Log.v(TRACE, "addMatch FAILED");
        }

        public void updateMatch(int matchID, ContentValues _matchValue)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            int count = db.update(TABLE_MATCHES, _matchValue, "M_ID=?", new String[] {String.valueOf(matchID)});
            if (count < 0)
                Log.v(TRACE, "updateMatch FAILED");
        }

        public void addPlayer(ContentValues _playerValue)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            long rowID = db.insert(TABLE_PLAYERS, null, _playerValue);
            if (rowID < 0)
                Log.v(TRACE, "addPlayer FAILED");
        }

        public ContentValues createPlayerValue(  String inFirstName,
                                                 String inSecond,
                                                 int    inHeight,
                                                 int    inWeight,
                                                 String country,
                                                 String birth,
                                                 byte[] inPhoto,
                                                 int    inTeamId)
        {
            ContentValues teamValue = new ContentValues();
            teamValue.put(PLAYERS_FIRST_NAME, inFirstName);
            teamValue.put(PLAYERS_SECOND_NAME, inSecond);
            teamValue.put(PLAYERS_HEIGHT, inHeight);
            teamValue.put(PLAYERS_WEIGHT, inWeight);
            teamValue.put(PLAYERS_COUNTRY, country);
            teamValue.put(PLAYERS_BIRTH, birth);
            teamValue.put(PLAYERS_PHOTO, inPhoto);
            teamValue.put(PLAYERS_TEAM_ID, inTeamId);

            return teamValue;
        }

        public void addMember(ContentValues _memberValue)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            long rowID = db.insert(TABLE_STAFF, null, _memberValue);
            if (rowID < 0)
                Log.v(TRACE, "addMember FAILED");
        }

        public ContentValues createMemberValue(int    inTeamId,
                                               int    inPlayerID,
                                               String inAmplua,
                                               int    inSeason)
        {
            ContentValues memberValue = new ContentValues();
            memberValue.put(STAFF_TEAM_ID, inTeamId);
            memberValue.put(STAFF_PLAYER_ID, inPlayerID);
            memberValue.put(STAFF_SEASON, inSeason);
            memberValue.put(STAFF_POSITION, inAmplua);

            return memberValue;
        }

        public void addGoal(ContentValues _goalValue)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            long rowID = db.insert(TABLE_GOALS, null, _goalValue);
            if (rowID < 0)
                Log.v(TRACE, "addGoal FAILED");
        }

        public ContentValues createGoalValue(  String inMinute,
                                           int    inPlayerId,
                                           int    inMatchId,
                                           String type)
        {
            ContentValues teamValue = new ContentValues();
            teamValue.put(GOALS_MINUTE, inMinute);
            teamValue.put(GOALS_PLAYER_ID, inPlayerId);
            teamValue.put(GOALS_MATCH_ID, inMatchId);
            teamValue.put(GOALS_TYPE, type);


            return teamValue;
        }

        public List<Team> getListTeams(int inSeason)
    {
        // получаем список комманд из db table TEAMS
        Cursor curs = getAllTeams(inSeason);

        List<Team> teamsList = new ArrayList<Team>();
        String teamName;
        String teamCity;
        Bitmap logo;
        byte[]     logoBlob;
        int    season;

        if (curs != null && curs.getCount()>0) {
            curs.moveToFirst();
            for (int j = 0; j < curs.getCount(); j++)
            {
                teamName = curs.getString(curs.getColumnIndex(Schema.TEAMS_TITLE));
                teamCity = curs.getString(curs.getColumnIndex(Schema.TEAMS_CITY));
                logoBlob  = curs.getBlob(curs.getColumnIndex(Schema.TEAMS_EMBLEM));
                logo    = BitmapFactory.decodeByteArray(logoBlob, 0 ,logoBlob.length);
                season   = curs.getInt(curs.getColumnIndex(Schema.TEAMS_SEASON));

                Team item = new Team(teamName, teamCity, logo, season);
                teamsList.add(item);
                curs.moveToNext();
            }
        }

        return teamsList;
     }
    }