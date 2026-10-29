package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BigSevenPlayersRoster
import com.example.data.model.CoachCard
import com.example.data.model.PlayerCard
import com.example.data.model.TeamFormation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PlayerCard::class, CoachCard::class, TeamFormation::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun coachDao(): CoachDao
    abstract fun formationDao(): FormationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fif_cards_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial sample FIFA cards asynchronously
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Insert players from all Big 7 European Leagues with latest info
            database.playerDao().insertAll(BigSevenPlayersRoster.players)

            val initialCoach = CoachCard(
                id = 1,
                name = "Carlo Ancelotti",
                nationality = "Italy",
                nationalityFlag = "🇮🇹",
                clubName = "Real Madrid",
                tacticStyle = "Counter-Attack / Balance",
                rating = 92
            )
            database.coachDao().insertCoach(initialCoach)

            // Starting 11 IDs for a 4-3-3:
            // [GK(206 Courtois), LB(304 Hernandez), CB(105 Van Dijk), CB(504 Marquinhos), RB(502 Hakimi),
            //  CDM(102 Rodri), CM(103 De Bruyne), CAM(202 Bellingham),
            //  LW(203 Vini Jr), ST(101 Haaland), RW(104 Salah)]
            val defaultFormation = TeamFormation(
                id = 1,
                name = "Big 7 All-Stars XI",
                formationType = "4-3-3",
                coachId = 1,
                startingPlayerIds = "206,304,105,504,502,102,103,202,203,101,104",
                substitutePlayerIds = "201,401,601,402,301,701"
            )
            database.formationDao().insertFormation(defaultFormation)
        }
    }
}
