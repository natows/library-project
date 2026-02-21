package ug.project.library.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "ug.project.library", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_entities =
            noClasses().that().haveSimpleNameEndingWith("Controller")
                    .should().dependOnClassesThat().resideInAPackage("..model.entity..");

    @ArchTest
    static final ArchRule services_should_be_in_service_package =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..");

    @ArchTest
    static final ArchRule layered_architecture_rules =
            layeredArchitecture().consideringAllDependencies()
                    .layer("Controller").definedBy("..controller..", "..web..")
                    .layer("Service").definedBy("..service..")
                    .layer("Persistence").definedBy("..repository..", "..dao..")
                    .layer("Scheduler").definedBy("..scheduler..")
                    .layer("Config").definedBy("..config..")
                    .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Scheduler")
                    .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service", "Config")
                    .whereLayer("Scheduler").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Config").mayNotBeAccessedByAnyLayer();
}
