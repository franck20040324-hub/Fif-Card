package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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

        private suspend fun populateInitialData(database: AppDatabase) {
            val initialPlayers = listOf(
                PlayerCard(
                    id = 1,
                    name = "Kylian Mbappé",
                    position = "ST",
                    rating = 91,
                    stat1 = 97, stat2 = 90, stat3 = 80, stat4 = 92, stat5 = 36, stat6 = 78,
                    nationality = "France",
                    nationalityFlag = "🇫🇷",
                    clubName = "Real Madrid",
                    clubBadge = "👑",
                    leagueName = "La Liga",
                    leagueBadge = "🇪🇸"
                ),
                PlayerCard(
                    id = 2,
                    name = "Erling Haaland",
                    position = "ST",
                    rating = 91,
                    stat1 = 89, stat2 = 93, stat3 = 66, stat4 = 80, stat5 = 45, stat6 = 88,
                    nationality = "Norway",
                    nationalityFlag = "🇳🇴",
                    clubName = "Manchester City",
                    clubBadge = "⛵",
                    leagueName = "Premier League",
                    leagueBadge = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
                ),
                PlayerCard(
                    id = 3,
                    name = "Vinícius Jr.",
                    position = "LW",
                    rating = 90,
                    stat1 = 95, stat2 = 84, stat3 = 81, stat4 = 91, stat5 = 29, stat6 = 69,
                    nationality = "Brazil",
                    nationalityFlag = "🇧🇷",
                    clubName = "Real Madrid",
                    clubBadge = "👑",
                    leagueName = "La Liga",
                    leagueBadge = "🇪🇸"
                ),
                PlayerCard(
                    id = 4,
                    name = "Kevin De Bruyne",
                    position = "CM",
                    rating = 90,
                    stat1 = 72, stat2 = 87, stat3 = 94, stat4 = 87, stat5 = 65, stat6 = 74,
                    nationality = "Belgium",
                    nationalityFlag = "🇧🇪",
                    clubName = "Manchester City",
                    clubBadge = "⛵",
                    leagueName = "Premier League",
                    leagueBadge = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
                ),
                PlayerCard(
                    id = 5,
                    name = "Jude Bellingham",
                    position = "CAM",
                    rating = 90,
                    stat1 = 80, stat2 = 86, stat3 = 83, stat4 = 88, stat5 = 78, stat6 = 83,
                    nationality = "England",
                    nationalityFlag = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                    clubName = "Real Madrid",
                    clubBadge = "👑",
                    leagueName = "La Liga",
                    leagueBadge = "🇪🇸"
                ),
                PlayerCard(
                    id = 6,
                    name = "Rodri",
                    position = "CDM",
                    rating = 91,
                    stat1 = 66, stat2 = 80, stat3 = 86, stat4 = 84, stat5 = 87, stat6 = 85,
                    nationality = "Spain",
                    nationalityFlag = "🇪🇸",
                    clubName = "Manchester City",
                    clubBadge = "⛵",
                    leagueName = "Premier League",
                    leagueBadge = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
                ),
                PlayerCard(
                    id = 7,
                    name = "Virgil van Dijk",
                    position = "CB",
                    rating = 89,
                    stat1 = 78, stat2 = 60, stat3 = 71, stat4 = 72, stat5 = 89, stat6 = 86,
                    nationality = "Netherlands",
                    nationalityFlag = "🇳🇱",
                    clubName = "Liverpool",
                    clubBadge = "🦅",
                    leagueName = "Premier League",
                    leagueBadge = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
                ),
                PlayerCard(
                    id = 8,
                    name = "Antonio Rüdiger",
                    position = "CB",
                    rating = 88,
                    stat1 = 82, stat2 = 55, stat3 = 71, stat4 = 68, stat5 = 86, stat6 = 86,
                    nationality = "Germany",
                    nationalityFlag = "🇩🇪",
                    clubName = "Real Madrid",
                    clubBadge = "👑",
                    leagueName = "La Liga",
                    leagueBadge = "🇪🇸"
                ),
                PlayerCard(
                    id = 9,
                    name = "Achraf Hakimi",
                    position = "RB",
                    rating = 85,
                    stat1 = 92, stat2 = 75, stat3 = 80, stat4 = 80, stat5 = 75, stat6 = 78,
                    nationality = "Morocco",
                    nationalityFlag = "🇲🇦",
                    clubName = "Paris Saint-Germain",
                    clubBadge = "🗼",
                    leagueName = "Ligue 1",
                    leagueBadge = "🇫🇷"
                ),
                PlayerCard(
                    id = 10,
                    name = "Théo Hernandez",
                    position = "LB",
                    rating = 87,
                    stat1 = 95, stat2 = 74, stat3 = 76, stat4 = 82, stat5 = 80, stat6 = 89,
                    nationality = "France",
                    nationalityFlag = "🇫🇷",
                    clubName = "AC Milan",
                    clubBadge = "🔴",
                    leagueName = "Serie A",
                    leagueBadge = "🇮🇹"
                ),
                PlayerCard(
                    id = 11,
                    name = "Thibaut Courtois",
                    position = "GK",
                    rating = 89,
                    stat1 = 85, stat2 = 89, stat3 = 76, stat4 = 90, stat5 = 46, stat6 = 88,
                    nationality = "Belgium",
                    nationalityFlag = "🇧🇪",
                    clubName = "Real Madrid",
                    clubBadge = "👑",
                    leagueName = "La Liga",
                    leagueBadge = "🇪🇸"
                ),
                PlayerCard(
                    id = 12,
                    name = "Mohamed Salah",
                    position = "RW",
                    rating = 89,
                    stat1 = 89, stat2 = 87, stat3 = 82, stat4 = 88, stat5 = 45, stat6 = 76,
                    nationality = "Egypt",
                    nationalityFlag = "🇪🇬",
                    clubName = "Liverpool",
                    clubBadge = "🦅",
                    leagueName = "Premier League",
                    leagueBadge = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
                ),
                PlayerCard(
                    id = 13,
                    name = "Lionel Messi",
                    position = "RW",
                    rating = 88,
                    stat1 = 79, stat2 = 85, stat3 = 87, stat4 = 92, stat5 = 33, stat6 = 64,
                    nationality = "Argentina",
                    nationalityFlag = "🇦🇷",
                    clubName = "Inter Miami",
                    clubBadge = "🦩",
                    leagueName = "MLS",
                    leagueBadge = "🇺🇸"
                ),
                PlayerCard(
                    id = 14,
                    name = "Cristiano Ronaldo",
                    position = "ST",
                    rating = 86,
                    stat1 = 77, stat2 = 88, stat3 = 75, stat4 = 80, stat5 = 34, stat6 = 74,
                    nationality = "Portugal",
                    nationalityFlag = "🇵🇹",
                    clubName = "Al Nassr",
                    clubBadge = "🟡",
                    leagueName = "Saudi Pro League",
                    leagueBadge = "🇸🇦"
                )
            )

            database.playerDao().insertAll(initialPlayers)

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
            // [GK(11), LB(10), CB(7), CB(8), RB(9), CDM(6), CM(4), CAM(5), LW(3), ST(1), RW(12)]
            val defaultFormation = TeamFormation(
                id = 1,
                name = "Ultimate XI",
                formationType = "4-3-3",
                coachId = 1,
                startingPlayerIds = "11,10,7,8,9,6,4,5,3,1,12",
                substitutePlayerIds = "2,13,14"
            )
            database.formationDao().insertFormation(defaultFormation)
        }
    }
}
