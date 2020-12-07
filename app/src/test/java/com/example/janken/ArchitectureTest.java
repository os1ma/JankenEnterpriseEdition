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
    private static final String BUSINESS_LOGIC_LAYER = ROOT_PACKAGE + ".businesslogic..";
    private static final String DATA_ACCESS_LAYER = ROOT_PACKAGE + ".dataaccess..";

    @ArchTest
    public static final ArchRule レイヤーの依存の向きが設計通り = Architectures
            .layeredArchitecture()
            .layer(PRESENTATION_LAYER).definedBy(PRESENTATION_LAYER)
            .layer(BUSINESS_LOGIC_LAYER).definedBy(BUSINESS_LOGIC_LAYER)
            .layer(DATA_ACCESS_LAYER).definedBy(DATA_ACCESS_LAYER)
            .layer(ROOT_PACKAGE).definedBy(ROOT_PACKAGE)
            .whereLayer(PRESENTATION_LAYER).mayOnlyBeAccessedByLayers(ROOT_PACKAGE)
            .whereLayer(DATA_ACCESS_LAYER).mayNotBeAccessedByAnyLayer();

}
