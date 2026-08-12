package com.ouropro.player.improvements;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SeriesPosterRepairTest {
    @Test
    public void matchesM3UEpisodeTitleToSeriesCatalogTitle() {
        assertEquals(
                SeriesPosterRepair.key("The Walking Dead"),
                SeriesPosterRepair.key("The Walking Dead S01E01"));
    }

    @Test
    public void ignoresAccentsAndSeparatorsOnlyForMatching() {
        assertEquals(
                SeriesPosterRepair.key("A Grande Família"),
                SeriesPosterRepair.key("A_Grande.Familia"));
    }
}
