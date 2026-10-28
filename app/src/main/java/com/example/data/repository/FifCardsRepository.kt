package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.model.CoachCard
import com.example.data.model.PlayerCard
import com.example.data.model.TeamFormation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FifCardsRepository(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val playerDao = database.playerDao()
    private val coachDao = database.coachDao()
    private val formationDao = database.formationDao()

    // --- Players ---
    val allPlayers: Flow<List<PlayerCard>> = playerDao.getAllPlayers()

    suspend fun getPlayerById(id: Long): PlayerCard? = withContext(Dispatchers.IO) {
        playerDao.getPlayerById(id)
    }

    suspend fun savePlayer(player: PlayerCard): Long = withContext(Dispatchers.IO) {
        playerDao.insertPlayer(player)
    }

    suspend fun deletePlayer(player: PlayerCard) = withContext(Dispatchers.IO) {
        // Also remove local private image if present
        player.photoPath?.let { path ->
            try {
                val file = File(path)
                if (file.exists() && file.parentFile?.name == "player_images") {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        playerDao.deletePlayer(player)
    }

    // --- Coaches ---
    val allCoaches: Flow<List<CoachCard>> = coachDao.getAllCoaches()

    suspend fun saveCoach(coach: CoachCard): Long = withContext(Dispatchers.IO) {
        coachDao.insertCoach(coach)
    }

    suspend fun deleteCoach(coach: CoachCard) = withContext(Dispatchers.IO) {
        coachDao.deleteCoach(coach)
    }

    // --- Formations ---
    val allFormations: Flow<List<TeamFormation>> = formationDao.getAllFormations()

    suspend fun saveFormation(formation: TeamFormation): Long = withContext(Dispatchers.IO) {
        formationDao.insertFormation(formation)
    }

    suspend fun deleteFormation(formation: TeamFormation) = withContext(Dispatchers.IO) {
        formationDao.deleteFormation(formation)
    }

    // --- Private Image Storage ---
    /**
     * Saves user-selected image into app private storage (filesDir/player_images/).
     * This guarantees the photo is saved offline and NEVER appears in the phone's gallery.
     */
    suspend fun savePlayerImagePrivately(imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val imagesDir = File(context.filesDir, "player_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            // Add a .nomedia file as extra assurance that no media scanner indexes this
            val noMedia = File(imagesDir, ".nomedia")
            if (!noMedia.exists()) {
                noMedia.createNewFile()
            }

            val fileName = "player_${System.currentTimeMillis()}.jpg"
            val destFile = File(imagesDir, fileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                // Resize if too huge to keep app lightning fast on Snapdragon 662
                val maxDim = 800
                val scaledBitmap = if (bitmap.width > maxDim || bitmap.height > maxDim) {
                    val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                    val targetW = if (ratio > 1f) maxDim else (maxDim * ratio).toInt()
                    val targetH = if (ratio > 1f) (maxDim / ratio).toInt() else maxDim
                    Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
                } else {
                    bitmap
                }

                FileOutputStream(destFile).use { out ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
                }
                destFile.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Backup & Restore (JSON precautionary backup) ---
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "Fif-Cards")
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        val players = playerDao.getAllPlayersList()
        val playersArray = JSONArray()
        players.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("position", p.position)
            obj.put("rating", p.rating)
            obj.put("stat1", p.stat1)
            obj.put("stat2", p.stat2)
            obj.put("stat3", p.stat3)
            obj.put("stat4", p.stat4)
            obj.put("stat5", p.stat5)
            obj.put("stat6", p.stat6)
            obj.put("nationality", p.nationality)
            obj.put("nationalityFlag", p.nationalityFlag)
            obj.put("clubName", p.clubName)
            obj.put("clubBadge", p.clubBadge)
            obj.put("leagueName", p.leagueName)
            obj.put("leagueBadge", p.leagueBadge)
            playersArray.put(obj)
        }
        root.put("players", playersArray)

        val coaches = coachDao.getAllCoachesList()
        val coachesArray = JSONArray()
        coaches.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("nationality", c.nationality)
            obj.put("nationalityFlag", c.nationalityFlag)
            obj.put("clubName", c.clubName)
            obj.put("tacticStyle", c.tacticStyle)
            obj.put("rating", c.rating)
            coachesArray.put(obj)
        }
        root.put("coaches", coachesArray)

        val formations = formationDao.getAllFormationsList()
        val formationsArray = JSONArray()
        formations.forEach { f ->
            val obj = JSONObject()
            obj.put("id", f.id)
            obj.put("name", f.name)
            obj.put("formationType", f.formationType)
            obj.put("coachId", f.coachId ?: 0)
            obj.put("startingPlayerIds", f.startingPlayerIds)
            obj.put("substitutePlayerIds", f.substitutePlayerIds)
            formationsArray.put(obj)
        }
        root.put("formations", formationsArray)

        root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.optString("app") != "Fif-Cards") return@withContext false

            val playersArray = root.optJSONArray("players")
            if (playersArray != null) {
                val list = mutableListOf<PlayerCard>()
                for (i in 0 until playersArray.length()) {
                    val o = playersArray.getJSONObject(i)
                    list.add(
                        PlayerCard(
                            id = o.optLong("id", 0),
                            name = o.getString("name"),
                            position = o.optString("position", "ST"),
                            rating = o.optInt("rating", 85),
                            stat1 = o.optInt("stat1", 80),
                            stat2 = o.optInt("stat2", 80),
                            stat3 = o.optInt("stat3", 80),
                            stat4 = o.optInt("stat4", 80),
                            stat5 = o.optInt("stat5", 80),
                            stat6 = o.optInt("stat6", 80),
                            nationality = o.optString("nationality", "Global"),
                            nationalityFlag = o.optString("nationalityFlag", "🌍"),
                            clubName = o.optString("clubName", "Club"),
                            clubBadge = o.optString("clubBadge", "⚽"),
                            leagueName = o.optString("leagueName", "League"),
                            leagueBadge = o.optString("leagueBadge", "🏆")
                        )
                    )
                }
                playerDao.insertAll(list)
            }

            val coachesArray = root.optJSONArray("coaches")
            if (coachesArray != null) {
                val list = mutableListOf<CoachCard>()
                for (i in 0 until coachesArray.length()) {
                    val o = coachesArray.getJSONObject(i)
                    list.add(
                        CoachCard(
                            id = o.optLong("id", 0),
                            name = o.getString("name"),
                            nationality = o.optString("nationality", "Global"),
                            nationalityFlag = o.optString("nationalityFlag", "🌍"),
                            clubName = o.optString("clubName", "Club"),
                            tacticStyle = o.optString("tacticStyle", "Balanced"),
                            rating = o.optInt("rating", 85)
                        )
                    )
                }
                coachDao.insertAll(list)
            }

            val formationsArray = root.optJSONArray("formations")
            if (formationsArray != null) {
                val list = mutableListOf<TeamFormation>()
                for (i in 0 until formationsArray.length()) {
                    val o = formationsArray.getJSONObject(i)
                    list.add(
                        TeamFormation(
                            id = o.optLong("id", 0),
                            name = o.getString("name"),
                            formationType = o.optString("formationType", "4-3-3"),
                            coachId = if (o.has("coachId") && o.getLong("coachId") > 0) o.getLong("coachId") else null,
                            startingPlayerIds = o.optString("startingPlayerIds", "0,0,0,0,0,0,0,0,0,0,0"),
                            substitutePlayerIds = o.optString("substitutePlayerIds", "")
                        )
                    )
                }
                formationDao.insertAll(list)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
