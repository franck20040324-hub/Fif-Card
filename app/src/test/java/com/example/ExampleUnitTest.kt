package com.example

import com.example.data.model.PlayerCard
import com.example.data.model.PositionCategory
import com.example.ui.components.FormationPresets
import com.example.ui.components.getPositionCategory
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppStrings
import org.junit.Assert.*
import org.junit.Test

class FifCardsUnitTest {

    @Test
    fun testPositionCategoryClassification() {
        assertEquals(PositionCategory.GOALKEEPER, getPositionCategory("GK"))
        assertEquals(PositionCategory.DEFENDER, getPositionCategory("CB"))
        assertEquals(PositionCategory.DEFENDER, getPositionCategory("LB"))
        assertEquals(PositionCategory.DEFENDER, getPositionCategory("RB"))
        assertEquals(PositionCategory.MIDFIELDER, getPositionCategory("CM"))
        assertEquals(PositionCategory.MIDFIELDER, getPositionCategory("CDM"))
        assertEquals(PositionCategory.MIDFIELDER, getPositionCategory("CAM"))
        assertEquals(PositionCategory.FORWARD, getPositionCategory("ST"))
        assertEquals(PositionCategory.FORWARD, getPositionCategory("LW"))
        assertEquals(PositionCategory.FORWARD, getPositionCategory("RW"))
    }

    @Test
    fun testTeamRatingCalculation() {
        val ratings = listOf(90, 88, 86, 85, 91, 87, 89, 84, 88, 92, 89)
        assertEquals(11, ratings.size)
        val sum = ratings.sum()
        val average = (sum.toFloat() / 11f).toInt()
        assertEquals(88, average)
    }

    @Test
    fun testFormationPresetsSlotsCount() {
        listOf("4-3-3", "4-4-2", "4-2-3-1", "3-5-2", "5-3-2", "3-4-3").forEach { type ->
            val slots = FormationPresets.getSlotsForFormation(type)
            assertEquals("Formation $type must have exactly 11 starting slots", 11, slots.size)
            assertEquals("First slot must be Goalkeeper", "GK", slots[0].label)
        }
    }

    @Test
    fun testAppStringsLocalization() {
        assertEquals("Fif-Cards", AppStrings.get("app_title", AppLanguage.ENGLISH))
        assertEquals("فيفا كاردز", AppStrings.get("app_title", AppLanguage.ARABIC))

        assertEquals("Goalkeeper", AppStrings.get("pos_gk", AppLanguage.ENGLISH))
        assertEquals("حارس مرمى", AppStrings.get("pos_gk", AppLanguage.ARABIC))
    }

    @Test
    fun testBigSevenLeagues() {
        val names = com.example.data.model.BigSevenLeagues.list.map { it.name }
        assertEquals(7, names.size)
        assertTrue(names.contains("Premier League"))
        assertTrue(names.contains("La Liga"))
        assertTrue(names.contains("Serie A"))
        assertTrue(names.contains("Bundesliga"))
        assertTrue(names.contains("Ligue 1"))
        assertTrue(names.contains("Primeira Liga"))
        assertTrue(names.contains("Eredivisie"))
    }

    @Test
    fun testWorldFlagsExcludesIsrael() {
        val list = com.example.data.model.WorldFlags.countries
        assertTrue(list.isNotEmpty())
        assertTrue("World flags list must NOT contain Israel", list.none { it.name.equals("Israel", ignoreCase = true) })
        assertTrue("World flags list must contain Palestine", list.any { it.name.equals("Palestine", ignoreCase = true) })
    }

    @Test
    fun testBigSevenPlayersRosterCoversAllSevenLeagues() {
        val roster = com.example.data.model.BigSevenPlayersRoster.players
        assertTrue("Roster should have plenty of players", roster.size >= 25)
        val leagues = roster.map { it.leagueName }.toSet()
        assertTrue(leagues.contains("Premier League"))
        assertTrue(leagues.contains("La Liga"))
        assertTrue(leagues.contains("Serie A"))
        assertTrue(leagues.contains("Bundesliga"))
        assertTrue(leagues.contains("Ligue 1"))
        assertTrue(leagues.contains("Primeira Liga"))
        assertTrue(leagues.contains("Eredivisie"))
    }
}
