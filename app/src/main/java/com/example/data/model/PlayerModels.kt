package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlayerPosition(val code: String, val category: PositionCategory) {
    // Goalkeepers
    GK("GK", PositionCategory.GOALKEEPER),
    
    // Defenders
    CB("CB", PositionCategory.DEFENDER),
    LB("LB", PositionCategory.DEFENDER),
    RB("RB", PositionCategory.DEFENDER),
    LWB("LWB", PositionCategory.DEFENDER),
    RWB("RWB", PositionCategory.DEFENDER),
    
    // Midfielders
    CDM("CDM", PositionCategory.MIDFIELDER),
    CM("CM", PositionCategory.MIDFIELDER),
    CAM("CAM", PositionCategory.MIDFIELDER),
    LM("LM", PositionCategory.MIDFIELDER),
    RM("RM", PositionCategory.MIDFIELDER),
    
    // Forwards
    LW("LW", PositionCategory.FORWARD),
    RW("RW", PositionCategory.FORWARD),
    ST("ST", PositionCategory.FORWARD),
    CF("CF", PositionCategory.FORWARD)
}

enum class PositionCategory {
    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD
}

@Entity(tableName = "player_cards")
data class PlayerCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val position: String = "ST",
    val rating: Int = 88,
    // 6 Stats: (PAC, SHO, PAS, DRI, DEF, PHY) or (DIV, HAN, KIC, REF, SPE, POS)
    val stat1: Int = 85,
    val stat2: Int = 87,
    val stat3: Int = 80,
    val stat4: Int = 88,
    val stat5: Int = 45,
    val stat6: Int = 78,
    val nationality: String = "Brazil",
    val nationalityFlag: String = "🇧🇷",
    val clubName: String = "Real Madrid",
    val clubBadge: String = "👑",
    val leagueName: String = "La Liga",
    val leagueBadge: String = "🇪🇸",
    val photoPath: String? = null, // Local private storage path
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "coaches")
data class CoachCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nationality: String = "Italy",
    val nationalityFlag: String = "🇮🇹",
    val clubName: String = "Real Madrid",
    val tacticStyle: String = "Tiki-Taka",
    val rating: Int = 89,
    val photoPath: String? = null
)

@Entity(tableName = "formations")
data class TeamFormation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val formationType: String = "4-3-3", // "4-3-3", "4-4-2", "4-2-3-1", "3-5-2", "5-3-2", "3-4-3"
    val coachId: Long? = null,
    // Comma-separated or serialized list of starting 11 player IDs (or 0 if slot empty)
    val startingPlayerIds: String = "0,0,0,0,0,0,0,0,0,0,0",
    // Comma-separated list of substitute player IDs
    val substitutePlayerIds: String = ""
)

data class FormationSlot(
    val index: Int,
    val label: String,
    val category: PositionCategory,
    val xPercent: Float, // 0.0 to 1.0 on tactical pitch
    val yPercent: Float  // 0.0 (top/attackers) to 1.0 (bottom/goalkeeper)
)

enum class PlayerSortMethod {
    NATIONALITY,
    POSITION,
    RATING,
    NAME_FIRST_LETTER
}
