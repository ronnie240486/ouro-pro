package com.ouropro.player.improvements;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CatalogIdentityTest {
    @Test
    public void moviesWithDifferentUrlsShareOneCardIdentity() {
        assertEquals(
                MovieCatalogDeduplicator.key("Netflix", "Filme Exemplo", "101", "http://a/movie/101.ts"),
                MovieCatalogDeduplicator.key("Netflix", "Filme Exemplo", "202", "http://b/movie/202.ts")
        );
    }

    @Test
    public void sameSeriesNameInDifferentCategoriesRemainsSeparate() {
        assertNotEquals(
                SeriesCatalogDeduplicator.key("Netflix", "The Example"),
                SeriesCatalogDeduplicator.key("Amazon", "The Example")
        );
    }

    @Test
    public void whitespaceAndCaseDoNotCreateDuplicateIdentity() {
        assertEquals(
                SeriesCatalogDeduplicator.key(" Netflix ", " The  Example "),
                SeriesCatalogDeduplicator.key("netflix", "the example")
        );
    }
}
