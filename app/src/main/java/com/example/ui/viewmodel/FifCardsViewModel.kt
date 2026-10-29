package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MusicPlayerManager
import com.example.data.model.CoachCard
import com.example.data.model.FormationSlot
import com.example.data.model.PlayerCard
import com.example.data.model.PlayerSortMethod
import com.example.data.model.PositionCategory
import com.example.data.model.TeamFormation
import com.example.data.repository.FifCardsRepository
import com.example.ui.components.FormationPresets
import com.example.ui.components.getPositionCategory
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FifCardsViewModel(application: Application) : AndroidViewModel(application) {
    val repository = FifCardsRepository(application)
    val musicPlayer = MusicPlayerManager(application)

    // Gradual startup stage for Snapdragon 662 efficiency (0: skeleton, 1: header & stats, 2: full list)
    private val _startupStage = MutableStateFlow(0)
    val startupStage: StateFlow<Int> = _startupStage.asStateFlow()

    // Navigation tab: 0 = Players (Nominal list), 1 = Create/Edit Card, 2 = Formations, 3 = Settings
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    // Search and Sort in Nominal List
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortMethod = MutableStateFlow(PlayerSortMethod.RATING)
    val sortMethod: StateFlow<PlayerSortMethod> = _sortMethod.asStateFlow()

    // Raw players from Room
    val allPlayers: StateFlow<List<PlayerCard>> = repository.allPlayers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered & Sorted nominal list
    val displayedPlayers: StateFlow<List<PlayerCard>> = combine(
        allPlayers,
        _searchQuery,
        _sortMethod
    ) { players, query, sort ->
        val filtered = if (query.isBlank()) {
            players
        } else {
            players.filter { it.name.contains(query, ignoreCase = true) }
        }

        when (sort) {
            PlayerSortMethod.NATIONALITY -> filtered.sortedBy { it.nationality }
            PlayerSortMethod.POSITION -> filtered.sortedBy { getPositionCategory(it.position).ordinal }
            PlayerSortMethod.RATING -> filtered.sortedByDescending { it.rating }
            PlayerSortMethod.NAME_FIRST_LETTER -> filtered.sortedBy { it.name.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Coaches
    val allCoaches: StateFlow<List<CoachCard>> = repository.allCoaches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Formations
    val allFormations: StateFlow<List<TeamFormation>> = repository.allFormations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Formation
    private val _selectedFormationId = MutableStateFlow<Long?>(null)
    val currentFormation: StateFlow<TeamFormation?> = combine(
        allFormations,
        _selectedFormationId
    ) { formations, selectedId ->
        if (formations.isEmpty()) null
        else if (selectedId != null) formations.find { it.id == selectedId } ?: formations.first()
        else formations.first()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Selected Player for detail dialog view
    private val _selectedPlayer = MutableStateFlow<PlayerCard?>(null)
    val selectedPlayer: StateFlow<PlayerCard?> = _selectedPlayer.asStateFlow()

    // Player being edited in Second Interface
    private val _editingPlayer = MutableStateFlow<PlayerCard?>(null)
    val editingPlayer: StateFlow<PlayerCard?> = _editingPlayer.asStateFlow()

    // Slot selection dialog in Formation tab
    private val _selectingSlotIndex = MutableStateFlow<Int?>(null)
    val selectingSlotIndex: StateFlow<Int?> = _selectingSlotIndex.asStateFlow()

    // Settings
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    // Toast/Snackbar message
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        // Snapdragon 662 gradual startup sequence to avoid CPU overloading
        viewModelScope.launch {
            repository.ensureBigSevenPlayers()
            delay(120)
            _startupStage.value = 1
            delay(180)
            _startupStage.value = 2
        }
    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setSortMethod(sort: PlayerSortMethod) {
        _sortMethod.value = sort
    }

    fun selectPlayer(player: PlayerCard?) {
        _selectedPlayer.value = player
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // --- Card Creation & Editing ---
    fun startCreateNewCard() {
        _editingPlayer.value = null
        _activeTab.value = 1 // Switch to second interface
    }

    fun startEditPlayer(player: PlayerCard) {
        _editingPlayer.value = player
        _selectedPlayer.value = null // Close detail modal
        _activeTab.value = 1 // Switch to second interface
    }

    fun savePlayerCard(player: PlayerCard, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.savePlayer(player)
            _statusMessage.value = "Card saved successfully!"
            _editingPlayer.value = null
            _activeTab.value = 0 // Return to nominal list
            onComplete()
        }
    }

    fun deletePlayer(player: PlayerCard) {
        viewModelScope.launch {
            repository.deletePlayer(player)
            _selectedPlayer.value = null
            _statusMessage.value = "Card deleted."
        }
    }

    suspend fun savePlayerPhotoPrivately(uri: Uri): String? {
        return repository.savePlayerImagePrivately(uri)
    }

    // --- Formations Logic ---
    fun selectFormation(id: Long) {
        _selectedFormationId.value = id
    }

    fun openSlotSelection(slotIndex: Int) {
        _selectingSlotIndex.value = slotIndex
    }

    fun closeSlotSelection() {
        _selectingSlotIndex.value = null
    }

    fun assignPlayerToSlot(slotIndex: Int, player: PlayerCard) {
        val formation = currentFormation.value ?: return
        val currentIds = formation.startingPlayerIds.split(",").toMutableList()
        // Ensure list has 11 entries
        while (currentIds.size < 11) {
            currentIds.add("0")
        }
        if (slotIndex in 0..10) {
            currentIds[slotIndex] = player.id.toString()
        }
        val updated = formation.copy(startingPlayerIds = currentIds.joinToString(","))
        viewModelScope.launch {
            repository.saveFormation(updated)
            _selectingSlotIndex.value = null
        }
    }

    fun swapSlots(fromSlot: Int, toSlot: Int) {
        val formation = currentFormation.value ?: return
        if (fromSlot !in 0..10 || toSlot !in 0..10 || fromSlot == toSlot) return
        val currentIds = formation.startingPlayerIds.split(",").toMutableList()
        while (currentIds.size < 11) {
            currentIds.add("0")
        }
        val temp = currentIds[fromSlot]
        currentIds[fromSlot] = currentIds[toSlot]
        currentIds[toSlot] = temp
        val updated = formation.copy(startingPlayerIds = currentIds.joinToString(","))
        viewModelScope.launch {
            repository.saveFormation(updated)
            _statusMessage.value = "Swapped slot positions!"
        }
    }

    fun autoFillBestStartingEleven() {
        val formation = currentFormation.value ?: return
        val players = allPlayers.value
        if (players.isEmpty()) return
        
        val slots = FormationPresets.getSlotsForFormation(formation.formationType)
        val assignedIds = mutableListOf<String>()
        val usedPlayerIds = mutableSetOf<Long>()
        
        slots.forEach { slot: FormationSlot ->
            val targetCategory: PositionCategory = slot.category
            val bestPlayer = players
                .filter { it.id !in usedPlayerIds }
                .sortedWith(
                    compareByDescending<PlayerCard> { p ->
                        if (getPositionCategory(p.position) == targetCategory) 1 else 0
                    }.thenByDescending { p -> p.rating }
                )
                .firstOrNull()
            
            if (bestPlayer != null) {
                assignedIds.add(bestPlayer.id.toString())
                usedPlayerIds.add(bestPlayer.id)
            } else {
                assignedIds.add("0")
            }
        }
        
        val updated = formation.copy(startingPlayerIds = assignedIds.joinToString(","))
        viewModelScope.launch {
            repository.saveFormation(updated)
            _statusMessage.value = "Starting XI filled with top players!"
        }
    }

    fun removePlayerFromSlot(slotIndex: Int) {
        val formation = currentFormation.value ?: return
        val currentIds = formation.startingPlayerIds.split(",").toMutableList()
        while (currentIds.size < 11) {
            currentIds.add("0")
        }
        if (slotIndex in 0..10) {
            currentIds[slotIndex] = "0"
        }
        val updated = formation.copy(startingPlayerIds = currentIds.joinToString(","))
        viewModelScope.launch {
            repository.saveFormation(updated)
            _selectingSlotIndex.value = null
        }
    }

    fun addSubstitute(player: PlayerCard) {
        val formation = currentFormation.value ?: return
        val subs = formation.substitutePlayerIds.split(",")
            .filter { it.isNotBlank() && it != "0" }
            .toMutableList()
        if (!subs.contains(player.id.toString())) {
            subs.add(player.id.toString())
            val updated = formation.copy(substitutePlayerIds = subs.joinToString(","))
            viewModelScope.launch {
                repository.saveFormation(updated)
            }
        }
    }

    fun removeSubstitute(playerId: Long) {
        val formation = currentFormation.value ?: return
        val subs = formation.substitutePlayerIds.split(",")
            .filter { it.isNotBlank() && it != "0" && it != playerId.toString() }
        val updated = formation.copy(substitutePlayerIds = subs.joinToString(","))
        viewModelScope.launch {
            repository.saveFormation(updated)
        }
    }

    fun updateFormationType(newType: String) {
        val formation = currentFormation.value ?: return
        val updated = formation.copy(formationType = newType)
        viewModelScope.launch {
            repository.saveFormation(updated)
        }
    }

    fun createNewFormation(name: String, type: String = "4-3-3") {
        viewModelScope.launch {
            val newFormation = TeamFormation(
                name = name,
                formationType = type,
                coachId = allCoaches.value.firstOrNull()?.id,
                startingPlayerIds = "0,0,0,0,0,0,0,0,0,0,0",
                substitutePlayerIds = ""
            )
            val newId = repository.saveFormation(newFormation)
            _selectedFormationId.value = newId
        }
    }

    fun setFormationCoach(coachId: Long) {
        val formation = currentFormation.value ?: return
        val updated = formation.copy(coachId = coachId)
        viewModelScope.launch {
            repository.saveFormation(updated)
        }
    }

    // --- Precautionary Backup & Restore ---
    suspend fun exportBackupJson(): String {
        return repository.exportBackupJson()
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        val ok = repository.restoreBackupJson(jsonString)
        if (ok) {
            _statusMessage.value = "Backup restored successfully!"
        }
        return ok
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer.release()
    }
}
