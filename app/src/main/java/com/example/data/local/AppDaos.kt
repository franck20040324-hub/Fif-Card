package com.example.data.local

import androidx.room.*
import com.example.data.model.CoachCard
import com.example.data.model.PlayerCard
import com.example.data.model.TeamFormation
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_cards ORDER BY id DESC")
    fun getAllPlayers(): Flow<List<PlayerCard>>

    @Query("SELECT * FROM player_cards WHERE id = :id LIMIT 1")
    suspend fun getPlayerById(id: Long): PlayerCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerCard): Long

    @Update
    suspend fun updatePlayer(player: PlayerCard)

    @Delete
    suspend fun deletePlayer(player: PlayerCard)

    @Query("DELETE FROM player_cards WHERE id = :id")
    suspend fun deletePlayerById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(players: List<PlayerCard>)

    @Query("SELECT * FROM player_cards")
    suspend fun getAllPlayersList(): List<PlayerCard>
}

@Dao
interface CoachDao {
    @Query("SELECT * FROM coaches ORDER BY id DESC")
    fun getAllCoaches(): Flow<List<CoachCard>>

    @Query("SELECT * FROM coaches WHERE id = :id LIMIT 1")
    suspend fun getCoachById(id: Long): CoachCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoach(coach: CoachCard): Long

    @Delete
    suspend fun deleteCoach(coach: CoachCard)

    @Query("SELECT * FROM coaches")
    suspend fun getAllCoachesList(): List<CoachCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coaches: List<CoachCard>)
}

@Dao
interface FormationDao {
    @Query("SELECT * FROM formations ORDER BY id DESC")
    fun getAllFormations(): Flow<List<TeamFormation>>

    @Query("SELECT * FROM formations WHERE id = :id LIMIT 1")
    suspend fun getFormationById(id: Long): TeamFormation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormation(formation: TeamFormation): Long

    @Update
    suspend fun updateFormation(formation: TeamFormation)

    @Delete
    suspend fun deleteFormation(formation: TeamFormation)

    @Query("SELECT * FROM formations")
    suspend fun getAllFormationsList(): List<TeamFormation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(formations: List<TeamFormation>)
}
