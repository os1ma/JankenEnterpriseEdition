package com.example.janken;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(
        packages = com.example.janken.ArchitectureTest.ROOT_PACKAGE,
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

    // アノテーションから指定可能にするため package private
    static final String ROOT_PACKAGE = "com.example.janken";

    private static final String PRESENTATION_LAYER = ROOT_PACKAGE + ".presentation..";
    private static final String APPLICATION_LAYER = ROOT_PACKAGE + ".application..";
    private static final String DOMAIN_LAYER = ROOT_PACKAGE + ".domain..";
    private static final String INFRASTRUCTURE_LAYER = ROOT_PACKAGE + ".infrastructure..";

    @ArchTest
    public static final ArchRule レイヤーの依存の向きが設計通り = Architectures
            .layeredArchitecture()
            .layer(PRESENTATION_LAYER).definedBy(PRESENTATION_LAYER)
            .layer(APPLICATION_LAYER).definedBy(APPLICATION_LAYER)
            .layer(DOMAIN_LAYER).definedBy(DOMAIN_LAYER)
            .layer(INFRASTRUCTURE_LAYER).definedBy(INFRASTRUCTURE_LAYER)
            .layer(ROOT_PACKAGE).definedBy(ROOT_PACKAGE)
            .whereLayer(PRESENTATION_LAYER).mayOnlyBeAccessedByLayers(ROOT_PACKAGE)
            .whereLayer(APPLICATION_LAYER).mayOnlyBeAccessedByLayers(PRESENTATION_LAYER)
            // HealthAPIController が infrastructure にアクセスするため、PRESENTATION_LAYER も許可
            .whereLayer(INFRASTRUCTURE_LAYER).mayOnlyBeAccessedByLayers(ROOT_PACKAGE, PRESENTATION_LAYER);

}
