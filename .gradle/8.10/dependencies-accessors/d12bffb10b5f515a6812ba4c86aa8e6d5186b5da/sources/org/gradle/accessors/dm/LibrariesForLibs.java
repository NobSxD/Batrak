package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final ComLibraryAccessors laccForComLibraryAccessors = new ComLibraryAccessors(owner);
    private final IoLibraryAccessors laccForIoLibraryAccessors = new IoLibraryAccessors(owner);
    private final JakartaLibraryAccessors laccForJakartaLibraryAccessors = new JakartaLibraryAccessors(owner);
    private final JavaxLibraryAccessors laccForJavaxLibraryAccessors = new JavaxLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>com</b>
     */
    public ComLibraryAccessors getCom() {
        return laccForComLibraryAccessors;
    }

    /**
     * Group of libraries at <b>io</b>
     */
    public IoLibraryAccessors getIo() {
        return laccForIoLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jakarta</b>
     */
    public JakartaLibraryAccessors getJakarta() {
        return laccForJakartaLibraryAccessors;
    }

    /**
     * Group of libraries at <b>javax</b>
     */
    public JavaxLibraryAccessors getJavax() {
        return laccForJavaxLibraryAccessors;
    }

    /**
     * Group of libraries at <b>org</b>
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class ComLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlLibraryAccessors laccForComFasterxmlLibraryAccessors = new ComFasterxmlLibraryAccessors(owner);
        private final ComGithubLibraryAccessors laccForComGithubLibraryAccessors = new ComGithubLibraryAccessors(owner);
        private final ComH2databaseLibraryAccessors laccForComH2databaseLibraryAccessors = new ComH2databaseLibraryAccessors(owner);
        private final ComMysqlLibraryAccessors laccForComMysqlLibraryAccessors = new ComMysqlLibraryAccessors(owner);

        public ComLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml</b>
         */
        public ComFasterxmlLibraryAccessors getFasterxml() {
            return laccForComFasterxmlLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.github</b>
         */
        public ComGithubLibraryAccessors getGithub() {
            return laccForComGithubLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.h2database</b>
         */
        public ComH2databaseLibraryAccessors getH2database() {
            return laccForComH2databaseLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.mysql</b>
         */
        public ComMysqlLibraryAccessors getMysql() {
            return laccForComMysqlLibraryAccessors;
        }

    }

    public static class ComFasterxmlLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonLibraryAccessors laccForComFasterxmlJacksonLibraryAccessors = new ComFasterxmlJacksonLibraryAccessors(owner);

        public ComFasterxmlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson</b>
         */
        public ComFasterxmlJacksonLibraryAccessors getJackson() {
            return laccForComFasterxmlJacksonLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonCoreLibraryAccessors laccForComFasterxmlJacksonCoreLibraryAccessors = new ComFasterxmlJacksonCoreLibraryAccessors(owner);

        public ComFasterxmlJacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.core</b>
         */
        public ComFasterxmlJacksonCoreLibraryAccessors getCore() {
            return laccForComFasterxmlJacksonCoreLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonCoreJacksonLibraryAccessors laccForComFasterxmlJacksonCoreJacksonLibraryAccessors = new ComFasterxmlJacksonCoreJacksonLibraryAccessors(owner);

        public ComFasterxmlJacksonCoreLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.core.jackson</b>
         */
        public ComFasterxmlJacksonCoreJacksonLibraryAccessors getJackson() {
            return laccForComFasterxmlJacksonCoreJacksonLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreJacksonLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonCoreJacksonDatabindLibraryAccessors laccForComFasterxmlJacksonCoreJacksonDatabindLibraryAccessors = new ComFasterxmlJacksonCoreJacksonDatabindLibraryAccessors(owner);

        public ComFasterxmlJacksonCoreJacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>annotations</b> with <b>com.fasterxml.jackson.core:jackson-annotations</b> coordinates and
         * with version reference <b>com.fasterxml.jackson.core.jackson.annotations</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAnnotations() {
            return create("com.fasterxml.jackson.core.jackson.annotations");
        }

        /**
         * Dependency provider for <b>core</b> with <b>com.fasterxml.jackson.core:jackson-core</b> coordinates and
         * with version reference <b>com.fasterxml.jackson.core.jackson.core</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("com.fasterxml.jackson.core.jackson.core");
        }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.core.jackson.databind</b>
         */
        public ComFasterxmlJacksonCoreJacksonDatabindLibraryAccessors getDatabind() {
            return laccForComFasterxmlJacksonCoreJacksonDatabindLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreJacksonDatabindLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public ComFasterxmlJacksonCoreJacksonDatabindLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>databind</b> with <b>com.fasterxml.jackson.core:jackson-databind</b> coordinates and
         * with version reference <b>com.fasterxml.jackson.core.jackson.databind</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("com.fasterxml.jackson.core.jackson.databind");
        }

        /**
         * Dependency provider for <b>x1</b> with <b>com.fasterxml.jackson.core:jackson-databind</b> coordinates and
         * with version reference <b>com.fasterxml.jackson.core.jackson.databind.x1</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getX1() {
            return create("com.fasterxml.jackson.core.jackson.databind.x1");
        }

    }

    public static class ComGithubLibraryAccessors extends SubDependencyFactory {
        private final ComGithubUlisesbocchioLibraryAccessors laccForComGithubUlisesbocchioLibraryAccessors = new ComGithubUlisesbocchioLibraryAccessors(owner);

        public ComGithubLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github.ulisesbocchio</b>
         */
        public ComGithubUlisesbocchioLibraryAccessors getUlisesbocchio() {
            return laccForComGithubUlisesbocchioLibraryAccessors;
        }

    }

    public static class ComGithubUlisesbocchioLibraryAccessors extends SubDependencyFactory {
        private final ComGithubUlisesbocchioJasyptLibraryAccessors laccForComGithubUlisesbocchioJasyptLibraryAccessors = new ComGithubUlisesbocchioJasyptLibraryAccessors(owner);

        public ComGithubUlisesbocchioLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github.ulisesbocchio.jasypt</b>
         */
        public ComGithubUlisesbocchioJasyptLibraryAccessors getJasypt() {
            return laccForComGithubUlisesbocchioJasyptLibraryAccessors;
        }

    }

    public static class ComGithubUlisesbocchioJasyptLibraryAccessors extends SubDependencyFactory {
        private final ComGithubUlisesbocchioJasyptSpringLibraryAccessors laccForComGithubUlisesbocchioJasyptSpringLibraryAccessors = new ComGithubUlisesbocchioJasyptSpringLibraryAccessors(owner);

        public ComGithubUlisesbocchioJasyptLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github.ulisesbocchio.jasypt.spring</b>
         */
        public ComGithubUlisesbocchioJasyptSpringLibraryAccessors getSpring() {
            return laccForComGithubUlisesbocchioJasyptSpringLibraryAccessors;
        }

    }

    public static class ComGithubUlisesbocchioJasyptSpringLibraryAccessors extends SubDependencyFactory {
        private final ComGithubUlisesbocchioJasyptSpringBootLibraryAccessors laccForComGithubUlisesbocchioJasyptSpringBootLibraryAccessors = new ComGithubUlisesbocchioJasyptSpringBootLibraryAccessors(owner);

        public ComGithubUlisesbocchioJasyptSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github.ulisesbocchio.jasypt.spring.boot</b>
         */
        public ComGithubUlisesbocchioJasyptSpringBootLibraryAccessors getBoot() {
            return laccForComGithubUlisesbocchioJasyptSpringBootLibraryAccessors;
        }

    }

    public static class ComGithubUlisesbocchioJasyptSpringBootLibraryAccessors extends SubDependencyFactory {

        public ComGithubUlisesbocchioJasyptSpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>starter</b> with <b>com.github.ulisesbocchio:jasypt-spring-boot-starter</b> coordinates and
         * with version reference <b>com.github.ulisesbocchio.jasypt.spring.boot.starter</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getStarter() {
            return create("com.github.ulisesbocchio.jasypt.spring.boot.starter");
        }

    }

    public static class ComH2databaseLibraryAccessors extends SubDependencyFactory {

        public ComH2databaseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>h2</b> with <b>com.h2database:h2</b> coordinates and
         * with version reference <b>com.h2database.h2</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getH2() {
            return create("com.h2database.h2");
        }

    }

    public static class ComMysqlLibraryAccessors extends SubDependencyFactory {
        private final ComMysqlMysqlLibraryAccessors laccForComMysqlMysqlLibraryAccessors = new ComMysqlMysqlLibraryAccessors(owner);

        public ComMysqlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.mysql.mysql</b>
         */
        public ComMysqlMysqlLibraryAccessors getMysql() {
            return laccForComMysqlMysqlLibraryAccessors;
        }

    }

    public static class ComMysqlMysqlLibraryAccessors extends SubDependencyFactory {
        private final ComMysqlMysqlConnectorLibraryAccessors laccForComMysqlMysqlConnectorLibraryAccessors = new ComMysqlMysqlConnectorLibraryAccessors(owner);

        public ComMysqlMysqlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.mysql.mysql.connector</b>
         */
        public ComMysqlMysqlConnectorLibraryAccessors getConnector() {
            return laccForComMysqlMysqlConnectorLibraryAccessors;
        }

    }

    public static class ComMysqlMysqlConnectorLibraryAccessors extends SubDependencyFactory {

        public ComMysqlMysqlConnectorLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>j</b> with <b>com.mysql:mysql-connector-j</b> coordinates and
         * with version reference <b>com.mysql.mysql.connector.j</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJ() {
            return create("com.mysql.mysql.connector.j");
        }

    }

    public static class IoLibraryAccessors extends SubDependencyFactory {
        private final IoReactivexLibraryAccessors laccForIoReactivexLibraryAccessors = new IoReactivexLibraryAccessors(owner);

        public IoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.reactivex</b>
         */
        public IoReactivexLibraryAccessors getReactivex() {
            return laccForIoReactivexLibraryAccessors;
        }

    }

    public static class IoReactivexLibraryAccessors extends SubDependencyFactory {
        private final IoReactivexRxjava3LibraryAccessors laccForIoReactivexRxjava3LibraryAccessors = new IoReactivexRxjava3LibraryAccessors(owner);

        public IoReactivexLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.reactivex.rxjava3</b>
         */
        public IoReactivexRxjava3LibraryAccessors getRxjava3() {
            return laccForIoReactivexRxjava3LibraryAccessors;
        }

    }

    public static class IoReactivexRxjava3LibraryAccessors extends SubDependencyFactory {

        public IoReactivexRxjava3LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>rxjava</b> with <b>io.reactivex.rxjava3:rxjava</b> coordinates and
         * with version reference <b>io.reactivex.rxjava3.rxjava</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRxjava() {
            return create("io.reactivex.rxjava3.rxjava");
        }

    }

    public static class JakartaLibraryAccessors extends SubDependencyFactory {
        private final JakartaXmlLibraryAccessors laccForJakartaXmlLibraryAccessors = new JakartaXmlLibraryAccessors(owner);

        public JakartaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>jakarta.xml</b>
         */
        public JakartaXmlLibraryAccessors getXml() {
            return laccForJakartaXmlLibraryAccessors;
        }

    }

    public static class JakartaXmlLibraryAccessors extends SubDependencyFactory {
        private final JakartaXmlBindLibraryAccessors laccForJakartaXmlBindLibraryAccessors = new JakartaXmlBindLibraryAccessors(owner);

        public JakartaXmlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>jakarta.xml.bind</b>
         */
        public JakartaXmlBindLibraryAccessors getBind() {
            return laccForJakartaXmlBindLibraryAccessors;
        }

    }

    public static class JakartaXmlBindLibraryAccessors extends SubDependencyFactory {
        private final JakartaXmlBindJakartaLibraryAccessors laccForJakartaXmlBindJakartaLibraryAccessors = new JakartaXmlBindJakartaLibraryAccessors(owner);

        public JakartaXmlBindLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>jakarta.xml.bind.jakarta</b>
         */
        public JakartaXmlBindJakartaLibraryAccessors getJakarta() {
            return laccForJakartaXmlBindJakartaLibraryAccessors;
        }

    }

    public static class JakartaXmlBindJakartaLibraryAccessors extends SubDependencyFactory {
        private final JakartaXmlBindJakartaXmlLibraryAccessors laccForJakartaXmlBindJakartaXmlLibraryAccessors = new JakartaXmlBindJakartaXmlLibraryAccessors(owner);

        public JakartaXmlBindJakartaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>jakarta.xml.bind.jakarta.xml</b>
         */
        public JakartaXmlBindJakartaXmlLibraryAccessors getXml() {
            return laccForJakartaXmlBindJakartaXmlLibraryAccessors;
        }

    }

    public static class JakartaXmlBindJakartaXmlLibraryAccessors extends SubDependencyFactory {
        private final JakartaXmlBindJakartaXmlBindLibraryAccessors laccForJakartaXmlBindJakartaXmlBindLibraryAccessors = new JakartaXmlBindJakartaXmlBindLibraryAccessors(owner);

        public JakartaXmlBindJakartaXmlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>jakarta.xml.bind.jakarta.xml.bind</b>
         */
        public JakartaXmlBindJakartaXmlBindLibraryAccessors getBind() {
            return laccForJakartaXmlBindJakartaXmlBindLibraryAccessors;
        }

    }

    public static class JakartaXmlBindJakartaXmlBindLibraryAccessors extends SubDependencyFactory {

        public JakartaXmlBindJakartaXmlBindLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>jakarta.xml.bind:jakarta.xml.bind-api</b> coordinates and
         * with version reference <b>jakarta.xml.bind.jakarta.xml.bind.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("jakarta.xml.bind.jakarta.xml.bind.api");
        }

    }

    public static class JavaxLibraryAccessors extends SubDependencyFactory {
        private final JavaxXmlLibraryAccessors laccForJavaxXmlLibraryAccessors = new JavaxXmlLibraryAccessors(owner);

        public JavaxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>javax.xml</b>
         */
        public JavaxXmlLibraryAccessors getXml() {
            return laccForJavaxXmlLibraryAccessors;
        }

    }

    public static class JavaxXmlLibraryAccessors extends SubDependencyFactory {
        private final JavaxXmlBindLibraryAccessors laccForJavaxXmlBindLibraryAccessors = new JavaxXmlBindLibraryAccessors(owner);

        public JavaxXmlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>javax.xml.bind</b>
         */
        public JavaxXmlBindLibraryAccessors getBind() {
            return laccForJavaxXmlBindLibraryAccessors;
        }

    }

    public static class JavaxXmlBindLibraryAccessors extends SubDependencyFactory {
        private final JavaxXmlBindJaxbLibraryAccessors laccForJavaxXmlBindJaxbLibraryAccessors = new JavaxXmlBindJaxbLibraryAccessors(owner);

        public JavaxXmlBindLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>javax.xml.bind.jaxb</b>
         */
        public JavaxXmlBindJaxbLibraryAccessors getJaxb() {
            return laccForJavaxXmlBindJaxbLibraryAccessors;
        }

    }

    public static class JavaxXmlBindJaxbLibraryAccessors extends SubDependencyFactory {

        public JavaxXmlBindJaxbLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>javax.xml.bind:jaxb-api</b> coordinates and
         * with version reference <b>javax.xml.bind.jaxb.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("javax.xml.bind.jaxb.api");
        }

    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgFlywaydbLibraryAccessors laccForOrgFlywaydbLibraryAccessors = new OrgFlywaydbLibraryAccessors(owner);
        private final OrgKnowmLibraryAccessors laccForOrgKnowmLibraryAccessors = new OrgKnowmLibraryAccessors(owner);
        private final OrgProjectlombokLibraryAccessors laccForOrgProjectlombokLibraryAccessors = new OrgProjectlombokLibraryAccessors(owner);
        private final OrgSpringframeworkLibraryAccessors laccForOrgSpringframeworkLibraryAccessors = new OrgSpringframeworkLibraryAccessors(owner);
        private final OrgTelegramLibraryAccessors laccForOrgTelegramLibraryAccessors = new OrgTelegramLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.flywaydb</b>
         */
        public OrgFlywaydbLibraryAccessors getFlywaydb() {
            return laccForOrgFlywaydbLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.knowm</b>
         */
        public OrgKnowmLibraryAccessors getKnowm() {
            return laccForOrgKnowmLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.projectlombok</b>
         */
        public OrgProjectlombokLibraryAccessors getProjectlombok() {
            return laccForOrgProjectlombokLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework</b>
         */
        public OrgSpringframeworkLibraryAccessors getSpringframework() {
            return laccForOrgSpringframeworkLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.telegram</b>
         */
        public OrgTelegramLibraryAccessors getTelegram() {
            return laccForOrgTelegramLibraryAccessors;
        }

    }

    public static class OrgFlywaydbLibraryAccessors extends SubDependencyFactory {
        private final OrgFlywaydbFlywayLibraryAccessors laccForOrgFlywaydbFlywayLibraryAccessors = new OrgFlywaydbFlywayLibraryAccessors(owner);

        public OrgFlywaydbLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.flywaydb.flyway</b>
         */
        public OrgFlywaydbFlywayLibraryAccessors getFlyway() {
            return laccForOrgFlywaydbFlywayLibraryAccessors;
        }

    }

    public static class OrgFlywaydbFlywayLibraryAccessors extends SubDependencyFactory {

        public OrgFlywaydbFlywayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>org.flywaydb:flyway-core</b> coordinates and
         * with version reference <b>org.flywaydb.flyway.core</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("org.flywaydb.flyway.core");
        }

        /**
         * Dependency provider for <b>mysql</b> with <b>org.flywaydb:flyway-mysql</b> coordinates and
         * with version reference <b>org.flywaydb.flyway.mysql</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMysql() {
            return create("org.flywaydb.flyway.mysql");
        }

    }

    public static class OrgKnowmLibraryAccessors extends SubDependencyFactory {
        private final OrgKnowmXchangeLibraryAccessors laccForOrgKnowmXchangeLibraryAccessors = new OrgKnowmXchangeLibraryAccessors(owner);

        public OrgKnowmLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.knowm.xchange</b>
         */
        public OrgKnowmXchangeLibraryAccessors getXchange() {
            return laccForOrgKnowmXchangeLibraryAccessors;
        }

    }

    public static class OrgKnowmXchangeLibraryAccessors extends SubDependencyFactory {
        private final OrgKnowmXchangeXchangeLibraryAccessors laccForOrgKnowmXchangeXchangeLibraryAccessors = new OrgKnowmXchangeXchangeLibraryAccessors(owner);

        public OrgKnowmXchangeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.knowm.xchange.xchange</b>
         */
        public OrgKnowmXchangeXchangeLibraryAccessors getXchange() {
            return laccForOrgKnowmXchangeXchangeLibraryAccessors;
        }

    }

    public static class OrgKnowmXchangeXchangeLibraryAccessors extends SubDependencyFactory {
        private final OrgKnowmXchangeXchangeStreamLibraryAccessors laccForOrgKnowmXchangeXchangeStreamLibraryAccessors = new OrgKnowmXchangeXchangeStreamLibraryAccessors(owner);

        public OrgKnowmXchangeXchangeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>binance</b> with <b>org.knowm.xchange:xchange-binance</b> coordinates and
         * with version reference <b>org.knowm.xchange.xchange.binance</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBinance() {
            return create("org.knowm.xchange.xchange.binance");
        }

        /**
         * Dependency provider for <b>bybit</b> with <b>org.knowm.xchange:xchange-bybit</b> coordinates and
         * with version reference <b>org.knowm.xchange.xchange.bybit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBybit() {
            return create("org.knowm.xchange.xchange.bybit");
        }

        /**
         * Dependency provider for <b>core</b> with <b>org.knowm.xchange:xchange-core</b> coordinates and
         * with version reference <b>org.knowm.xchange.xchange.core</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("org.knowm.xchange.xchange.core");
        }

        /**
         * Group of libraries at <b>org.knowm.xchange.xchange.stream</b>
         */
        public OrgKnowmXchangeXchangeStreamLibraryAccessors getStream() {
            return laccForOrgKnowmXchangeXchangeStreamLibraryAccessors;
        }

    }

    public static class OrgKnowmXchangeXchangeStreamLibraryAccessors extends SubDependencyFactory {

        public OrgKnowmXchangeXchangeStreamLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>binance</b> with <b>org.knowm.xchange:xchange-stream-binance</b> coordinates and
         * with version reference <b>org.knowm.xchange.xchange.stream.binance</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBinance() {
            return create("org.knowm.xchange.xchange.stream.binance");
        }

        /**
         * Dependency provider for <b>bybit</b> with <b>org.knowm.xchange:xchange-stream-bybit</b> coordinates and
         * with version reference <b>org.knowm.xchange.xchange.stream.bybit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBybit() {
            return create("org.knowm.xchange.xchange.stream.bybit");
        }

        /**
         * Dependency provider for <b>core</b> with <b>org.knowm.xchange:xchange-stream-core</b> coordinates and
         * with version reference <b>org.knowm.xchange.xchange.stream.core</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("org.knowm.xchange.xchange.stream.core");
        }

    }

    public static class OrgProjectlombokLibraryAccessors extends SubDependencyFactory {

        public OrgProjectlombokLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
         * with version reference <b>org.projectlombok.lombok</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLombok() {
            return create("org.projectlombok.lombok");
        }

    }

    public static class OrgSpringframeworkLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootLibraryAccessors laccForOrgSpringframeworkBootLibraryAccessors = new OrgSpringframeworkBootLibraryAccessors(owner);

        public OrgSpringframeworkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot</b>
         */
        public OrgSpringframeworkBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringLibraryAccessors laccForOrgSpringframeworkBootSpringLibraryAccessors = new OrgSpringframeworkBootSpringLibraryAccessors(owner);

        public OrgSpringframeworkBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring</b>
         */
        public OrgSpringframeworkBootSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkBootSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootLibraryAccessors laccForOrgSpringframeworkBootSpringBootLibraryAccessors = new OrgSpringframeworkBootSpringBootLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot</b>
         */
        public OrgSpringframeworkBootSpringBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootSpringBootLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootConfigurationLibraryAccessors laccForOrgSpringframeworkBootSpringBootConfigurationLibraryAccessors = new OrgSpringframeworkBootSpringBootConfigurationLibraryAccessors(owner);
        private final OrgSpringframeworkBootSpringBootStarterLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.configuration</b>
         */
        public OrgSpringframeworkBootSpringBootConfigurationLibraryAccessors getConfiguration() {
            return laccForOrgSpringframeworkBootSpringBootConfigurationLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.starter</b>
         */
        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors getStarter() {
            return laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootConfigurationLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkBootSpringBootConfigurationLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>processor</b> with <b>org.springframework.boot:spring-boot-configuration-processor</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.configuration.processor</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getProcessor() {
            return create("org.springframework.boot.spring.boot.configuration.processor");
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>starter</b> with <b>org.springframework.boot:spring-boot-starter</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("org.springframework.boot.spring.boot.starter");
        }

        /**
         * Dependency provider for <b>amqp</b> with <b>org.springframework.boot:spring-boot-starter-amqp</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.amqp</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAmqp() {
            return create("org.springframework.boot.spring.boot.starter.amqp");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.boot:spring-boot-starter-test</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.test</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("org.springframework.boot.spring.boot.starter.test");
        }

        /**
         * Dependency provider for <b>web</b> with <b>org.springframework.boot:spring-boot-starter-web</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.web</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getWeb() {
            return create("org.springframework.boot.spring.boot.starter.web");
        }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.starter.data</b>
         */
        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors getData() {
            return laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jpa</b> with <b>org.springframework.boot:spring-boot-starter-data-jpa</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.data.jpa</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJpa() {
            return create("org.springframework.boot.spring.boot.starter.data.jpa");
        }

    }

    public static class OrgTelegramLibraryAccessors extends SubDependencyFactory {
        private final OrgTelegramTelegrambotsLibraryAccessors laccForOrgTelegramTelegrambotsLibraryAccessors = new OrgTelegramTelegrambotsLibraryAccessors(owner);

        public OrgTelegramLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.telegram.telegrambots</b>
         */
        public OrgTelegramTelegrambotsLibraryAccessors getTelegrambots() {
            return laccForOrgTelegramTelegrambotsLibraryAccessors;
        }

    }

    public static class OrgTelegramTelegrambotsLibraryAccessors extends SubDependencyFactory {
        private final OrgTelegramTelegrambotsSpringLibraryAccessors laccForOrgTelegramTelegrambotsSpringLibraryAccessors = new OrgTelegramTelegrambotsSpringLibraryAccessors(owner);

        public OrgTelegramTelegrambotsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>longpolling</b> with <b>org.telegram:telegrambots-longpolling</b> coordinates and
         * with version reference <b>org.telegram.telegrambots.longpolling</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLongpolling() {
            return create("org.telegram.telegrambots.longpolling");
        }

        /**
         * Group of libraries at <b>org.telegram.telegrambots.spring</b>
         */
        public OrgTelegramTelegrambotsSpringLibraryAccessors getSpring() {
            return laccForOrgTelegramTelegrambotsSpringLibraryAccessors;
        }

    }

    public static class OrgTelegramTelegrambotsSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgTelegramTelegrambotsSpringBootLibraryAccessors laccForOrgTelegramTelegrambotsSpringBootLibraryAccessors = new OrgTelegramTelegrambotsSpringBootLibraryAccessors(owner);

        public OrgTelegramTelegrambotsSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.telegram.telegrambots.spring.boot</b>
         */
        public OrgTelegramTelegrambotsSpringBootLibraryAccessors getBoot() {
            return laccForOrgTelegramTelegrambotsSpringBootLibraryAccessors;
        }

    }

    public static class OrgTelegramTelegrambotsSpringBootLibraryAccessors extends SubDependencyFactory {

        public OrgTelegramTelegrambotsSpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>starter</b> with <b>org.telegram:telegrambots-spring-boot-starter</b> coordinates and
         * with version reference <b>org.telegram.telegrambots.spring.boot.starter</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getStarter() {
            return create("org.telegram.telegrambots.spring.boot.starter");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final ComVersionAccessors vaccForComVersionAccessors = new ComVersionAccessors(providers, config);
        private final IoVersionAccessors vaccForIoVersionAccessors = new IoVersionAccessors(providers, config);
        private final JakartaVersionAccessors vaccForJakartaVersionAccessors = new JakartaVersionAccessors(providers, config);
        private final JavaxVersionAccessors vaccForJavaxVersionAccessors = new JavaxVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com</b>
         */
        public ComVersionAccessors getCom() {
            return vaccForComVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.io</b>
         */
        public IoVersionAccessors getIo() {
            return vaccForIoVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.jakarta</b>
         */
        public JakartaVersionAccessors getJakarta() {
            return vaccForJakartaVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.javax</b>
         */
        public JavaxVersionAccessors getJavax() {
            return vaccForJavaxVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class ComVersionAccessors extends VersionFactory  {

        private final ComFasterxmlVersionAccessors vaccForComFasterxmlVersionAccessors = new ComFasterxmlVersionAccessors(providers, config);
        private final ComGithubVersionAccessors vaccForComGithubVersionAccessors = new ComGithubVersionAccessors(providers, config);
        private final ComH2databaseVersionAccessors vaccForComH2databaseVersionAccessors = new ComH2databaseVersionAccessors(providers, config);
        private final ComMysqlVersionAccessors vaccForComMysqlVersionAccessors = new ComMysqlVersionAccessors(providers, config);
        public ComVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml</b>
         */
        public ComFasterxmlVersionAccessors getFasterxml() {
            return vaccForComFasterxmlVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.github</b>
         */
        public ComGithubVersionAccessors getGithub() {
            return vaccForComGithubVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.h2database</b>
         */
        public ComH2databaseVersionAccessors getH2database() {
            return vaccForComH2databaseVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.mysql</b>
         */
        public ComMysqlVersionAccessors getMysql() {
            return vaccForComMysqlVersionAccessors;
        }

    }

    public static class ComFasterxmlVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonVersionAccessors vaccForComFasterxmlJacksonVersionAccessors = new ComFasterxmlJacksonVersionAccessors(providers, config);
        public ComFasterxmlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson</b>
         */
        public ComFasterxmlJacksonVersionAccessors getJackson() {
            return vaccForComFasterxmlJacksonVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonCoreVersionAccessors vaccForComFasterxmlJacksonCoreVersionAccessors = new ComFasterxmlJacksonCoreVersionAccessors(providers, config);
        public ComFasterxmlJacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.core</b>
         */
        public ComFasterxmlJacksonCoreVersionAccessors getCore() {
            return vaccForComFasterxmlJacksonCoreVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonCoreJacksonVersionAccessors vaccForComFasterxmlJacksonCoreJacksonVersionAccessors = new ComFasterxmlJacksonCoreJacksonVersionAccessors(providers, config);
        public ComFasterxmlJacksonCoreVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.core.jackson</b>
         */
        public ComFasterxmlJacksonCoreJacksonVersionAccessors getJackson() {
            return vaccForComFasterxmlJacksonCoreJacksonVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreJacksonVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonCoreJacksonDatabindVersionAccessors vaccForComFasterxmlJacksonCoreJacksonDatabindVersionAccessors = new ComFasterxmlJacksonCoreJacksonDatabindVersionAccessors(providers, config);
        public ComFasterxmlJacksonCoreJacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.fasterxml.jackson.core.jackson.annotations</b> with value <b>2.13.4.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAnnotations() { return getVersion("com.fasterxml.jackson.core.jackson.annotations"); }

        /**
         * Version alias <b>com.fasterxml.jackson.core.jackson.core</b> with value <b>2.13.4.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCore() { return getVersion("com.fasterxml.jackson.core.jackson.core"); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.core.jackson.databind</b>
         */
        public ComFasterxmlJacksonCoreJacksonDatabindVersionAccessors getDatabind() {
            return vaccForComFasterxmlJacksonCoreJacksonDatabindVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreJacksonDatabindVersionAccessors extends VersionFactory  implements VersionNotationSupplier {

        public ComFasterxmlJacksonCoreJacksonDatabindVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.fasterxml.jackson.core.jackson.databind</b> with value <b>2.13.4.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> asProvider() { return getVersion("com.fasterxml.jackson.core.jackson.databind"); }

        /**
         * Version alias <b>com.fasterxml.jackson.core.jackson.databind.x1</b> with value <b>2.13.4.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getX1() { return getVersion("com.fasterxml.jackson.core.jackson.databind.x1"); }

    }

    public static class ComGithubVersionAccessors extends VersionFactory  {

        private final ComGithubUlisesbocchioVersionAccessors vaccForComGithubUlisesbocchioVersionAccessors = new ComGithubUlisesbocchioVersionAccessors(providers, config);
        public ComGithubVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github.ulisesbocchio</b>
         */
        public ComGithubUlisesbocchioVersionAccessors getUlisesbocchio() {
            return vaccForComGithubUlisesbocchioVersionAccessors;
        }

    }

    public static class ComGithubUlisesbocchioVersionAccessors extends VersionFactory  {

        private final ComGithubUlisesbocchioJasyptVersionAccessors vaccForComGithubUlisesbocchioJasyptVersionAccessors = new ComGithubUlisesbocchioJasyptVersionAccessors(providers, config);
        public ComGithubUlisesbocchioVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github.ulisesbocchio.jasypt</b>
         */
        public ComGithubUlisesbocchioJasyptVersionAccessors getJasypt() {
            return vaccForComGithubUlisesbocchioJasyptVersionAccessors;
        }

    }

    public static class ComGithubUlisesbocchioJasyptVersionAccessors extends VersionFactory  {

        private final ComGithubUlisesbocchioJasyptSpringVersionAccessors vaccForComGithubUlisesbocchioJasyptSpringVersionAccessors = new ComGithubUlisesbocchioJasyptSpringVersionAccessors(providers, config);
        public ComGithubUlisesbocchioJasyptVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github.ulisesbocchio.jasypt.spring</b>
         */
        public ComGithubUlisesbocchioJasyptSpringVersionAccessors getSpring() {
            return vaccForComGithubUlisesbocchioJasyptSpringVersionAccessors;
        }

    }

    public static class ComGithubUlisesbocchioJasyptSpringVersionAccessors extends VersionFactory  {

        private final ComGithubUlisesbocchioJasyptSpringBootVersionAccessors vaccForComGithubUlisesbocchioJasyptSpringBootVersionAccessors = new ComGithubUlisesbocchioJasyptSpringBootVersionAccessors(providers, config);
        public ComGithubUlisesbocchioJasyptSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github.ulisesbocchio.jasypt.spring.boot</b>
         */
        public ComGithubUlisesbocchioJasyptSpringBootVersionAccessors getBoot() {
            return vaccForComGithubUlisesbocchioJasyptSpringBootVersionAccessors;
        }

    }

    public static class ComGithubUlisesbocchioJasyptSpringBootVersionAccessors extends VersionFactory  {

        public ComGithubUlisesbocchioJasyptSpringBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.github.ulisesbocchio.jasypt.spring.boot.starter</b> with value <b>3.0.5</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getStarter() { return getVersion("com.github.ulisesbocchio.jasypt.spring.boot.starter"); }

    }

    public static class ComH2databaseVersionAccessors extends VersionFactory  {

        public ComH2databaseVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.h2database.h2</b> with value <b>2.2.224</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getH2() { return getVersion("com.h2database.h2"); }

    }

    public static class ComMysqlVersionAccessors extends VersionFactory  {

        private final ComMysqlMysqlVersionAccessors vaccForComMysqlMysqlVersionAccessors = new ComMysqlMysqlVersionAccessors(providers, config);
        public ComMysqlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.mysql.mysql</b>
         */
        public ComMysqlMysqlVersionAccessors getMysql() {
            return vaccForComMysqlMysqlVersionAccessors;
        }

    }

    public static class ComMysqlMysqlVersionAccessors extends VersionFactory  {

        private final ComMysqlMysqlConnectorVersionAccessors vaccForComMysqlMysqlConnectorVersionAccessors = new ComMysqlMysqlConnectorVersionAccessors(providers, config);
        public ComMysqlMysqlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.mysql.mysql.connector</b>
         */
        public ComMysqlMysqlConnectorVersionAccessors getConnector() {
            return vaccForComMysqlMysqlConnectorVersionAccessors;
        }

    }

    public static class ComMysqlMysqlConnectorVersionAccessors extends VersionFactory  {

        public ComMysqlMysqlConnectorVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.mysql.mysql.connector.j</b> with value <b>9.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJ() { return getVersion("com.mysql.mysql.connector.j"); }

    }

    public static class IoVersionAccessors extends VersionFactory  {

        private final IoReactivexVersionAccessors vaccForIoReactivexVersionAccessors = new IoReactivexVersionAccessors(providers, config);
        public IoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.reactivex</b>
         */
        public IoReactivexVersionAccessors getReactivex() {
            return vaccForIoReactivexVersionAccessors;
        }

    }

    public static class IoReactivexVersionAccessors extends VersionFactory  {

        private final IoReactivexRxjava3VersionAccessors vaccForIoReactivexRxjava3VersionAccessors = new IoReactivexRxjava3VersionAccessors(providers, config);
        public IoReactivexVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.reactivex.rxjava3</b>
         */
        public IoReactivexRxjava3VersionAccessors getRxjava3() {
            return vaccForIoReactivexRxjava3VersionAccessors;
        }

    }

    public static class IoReactivexRxjava3VersionAccessors extends VersionFactory  {

        public IoReactivexRxjava3VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>io.reactivex.rxjava3.rxjava</b> with value <b>3.1.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRxjava() { return getVersion("io.reactivex.rxjava3.rxjava"); }

    }

    public static class JakartaVersionAccessors extends VersionFactory  {

        private final JakartaXmlVersionAccessors vaccForJakartaXmlVersionAccessors = new JakartaXmlVersionAccessors(providers, config);
        public JakartaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.jakarta.xml</b>
         */
        public JakartaXmlVersionAccessors getXml() {
            return vaccForJakartaXmlVersionAccessors;
        }

    }

    public static class JakartaXmlVersionAccessors extends VersionFactory  {

        private final JakartaXmlBindVersionAccessors vaccForJakartaXmlBindVersionAccessors = new JakartaXmlBindVersionAccessors(providers, config);
        public JakartaXmlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.jakarta.xml.bind</b>
         */
        public JakartaXmlBindVersionAccessors getBind() {
            return vaccForJakartaXmlBindVersionAccessors;
        }

    }

    public static class JakartaXmlBindVersionAccessors extends VersionFactory  {

        private final JakartaXmlBindJakartaVersionAccessors vaccForJakartaXmlBindJakartaVersionAccessors = new JakartaXmlBindJakartaVersionAccessors(providers, config);
        public JakartaXmlBindVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.jakarta.xml.bind.jakarta</b>
         */
        public JakartaXmlBindJakartaVersionAccessors getJakarta() {
            return vaccForJakartaXmlBindJakartaVersionAccessors;
        }

    }

    public static class JakartaXmlBindJakartaVersionAccessors extends VersionFactory  {

        private final JakartaXmlBindJakartaXmlVersionAccessors vaccForJakartaXmlBindJakartaXmlVersionAccessors = new JakartaXmlBindJakartaXmlVersionAccessors(providers, config);
        public JakartaXmlBindJakartaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.jakarta.xml.bind.jakarta.xml</b>
         */
        public JakartaXmlBindJakartaXmlVersionAccessors getXml() {
            return vaccForJakartaXmlBindJakartaXmlVersionAccessors;
        }

    }

    public static class JakartaXmlBindJakartaXmlVersionAccessors extends VersionFactory  {

        private final JakartaXmlBindJakartaXmlBindVersionAccessors vaccForJakartaXmlBindJakartaXmlBindVersionAccessors = new JakartaXmlBindJakartaXmlBindVersionAccessors(providers, config);
        public JakartaXmlBindJakartaXmlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.jakarta.xml.bind.jakarta.xml.bind</b>
         */
        public JakartaXmlBindJakartaXmlBindVersionAccessors getBind() {
            return vaccForJakartaXmlBindJakartaXmlBindVersionAccessors;
        }

    }

    public static class JakartaXmlBindJakartaXmlBindVersionAccessors extends VersionFactory  {

        public JakartaXmlBindJakartaXmlBindVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>jakarta.xml.bind.jakarta.xml.bind.api</b> with value <b>3.0.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("jakarta.xml.bind.jakarta.xml.bind.api"); }

    }

    public static class JavaxVersionAccessors extends VersionFactory  {

        private final JavaxXmlVersionAccessors vaccForJavaxXmlVersionAccessors = new JavaxXmlVersionAccessors(providers, config);
        public JavaxVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.javax.xml</b>
         */
        public JavaxXmlVersionAccessors getXml() {
            return vaccForJavaxXmlVersionAccessors;
        }

    }

    public static class JavaxXmlVersionAccessors extends VersionFactory  {

        private final JavaxXmlBindVersionAccessors vaccForJavaxXmlBindVersionAccessors = new JavaxXmlBindVersionAccessors(providers, config);
        public JavaxXmlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.javax.xml.bind</b>
         */
        public JavaxXmlBindVersionAccessors getBind() {
            return vaccForJavaxXmlBindVersionAccessors;
        }

    }

    public static class JavaxXmlBindVersionAccessors extends VersionFactory  {

        private final JavaxXmlBindJaxbVersionAccessors vaccForJavaxXmlBindJaxbVersionAccessors = new JavaxXmlBindJaxbVersionAccessors(providers, config);
        public JavaxXmlBindVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.javax.xml.bind.jaxb</b>
         */
        public JavaxXmlBindJaxbVersionAccessors getJaxb() {
            return vaccForJavaxXmlBindJaxbVersionAccessors;
        }

    }

    public static class JavaxXmlBindJaxbVersionAccessors extends VersionFactory  {

        public JavaxXmlBindJaxbVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>javax.xml.bind.jaxb.api</b> with value <b>2.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("javax.xml.bind.jaxb.api"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgFlywaydbVersionAccessors vaccForOrgFlywaydbVersionAccessors = new OrgFlywaydbVersionAccessors(providers, config);
        private final OrgKnowmVersionAccessors vaccForOrgKnowmVersionAccessors = new OrgKnowmVersionAccessors(providers, config);
        private final OrgProjectlombokVersionAccessors vaccForOrgProjectlombokVersionAccessors = new OrgProjectlombokVersionAccessors(providers, config);
        private final OrgSpringframeworkVersionAccessors vaccForOrgSpringframeworkVersionAccessors = new OrgSpringframeworkVersionAccessors(providers, config);
        private final OrgTelegramVersionAccessors vaccForOrgTelegramVersionAccessors = new OrgTelegramVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.flywaydb</b>
         */
        public OrgFlywaydbVersionAccessors getFlywaydb() {
            return vaccForOrgFlywaydbVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.knowm</b>
         */
        public OrgKnowmVersionAccessors getKnowm() {
            return vaccForOrgKnowmVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.projectlombok</b>
         */
        public OrgProjectlombokVersionAccessors getProjectlombok() {
            return vaccForOrgProjectlombokVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework</b>
         */
        public OrgSpringframeworkVersionAccessors getSpringframework() {
            return vaccForOrgSpringframeworkVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.telegram</b>
         */
        public OrgTelegramVersionAccessors getTelegram() {
            return vaccForOrgTelegramVersionAccessors;
        }

    }

    public static class OrgFlywaydbVersionAccessors extends VersionFactory  {

        private final OrgFlywaydbFlywayVersionAccessors vaccForOrgFlywaydbFlywayVersionAccessors = new OrgFlywaydbFlywayVersionAccessors(providers, config);
        public OrgFlywaydbVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.flywaydb.flyway</b>
         */
        public OrgFlywaydbFlywayVersionAccessors getFlyway() {
            return vaccForOrgFlywaydbFlywayVersionAccessors;
        }

    }

    public static class OrgFlywaydbFlywayVersionAccessors extends VersionFactory  {

        public OrgFlywaydbFlywayVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.flywaydb.flyway.core</b> with value <b>10.10.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCore() { return getVersion("org.flywaydb.flyway.core"); }

        /**
         * Version alias <b>org.flywaydb.flyway.mysql</b> with value <b>10.10.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMysql() { return getVersion("org.flywaydb.flyway.mysql"); }

    }

    public static class OrgKnowmVersionAccessors extends VersionFactory  {

        private final OrgKnowmXchangeVersionAccessors vaccForOrgKnowmXchangeVersionAccessors = new OrgKnowmXchangeVersionAccessors(providers, config);
        public OrgKnowmVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.knowm.xchange</b>
         */
        public OrgKnowmXchangeVersionAccessors getXchange() {
            return vaccForOrgKnowmXchangeVersionAccessors;
        }

    }

    public static class OrgKnowmXchangeVersionAccessors extends VersionFactory  {

        private final OrgKnowmXchangeXchangeVersionAccessors vaccForOrgKnowmXchangeXchangeVersionAccessors = new OrgKnowmXchangeXchangeVersionAccessors(providers, config);
        public OrgKnowmXchangeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.knowm.xchange.xchange</b>
         */
        public OrgKnowmXchangeXchangeVersionAccessors getXchange() {
            return vaccForOrgKnowmXchangeXchangeVersionAccessors;
        }

    }

    public static class OrgKnowmXchangeXchangeVersionAccessors extends VersionFactory  {

        private final OrgKnowmXchangeXchangeStreamVersionAccessors vaccForOrgKnowmXchangeXchangeStreamVersionAccessors = new OrgKnowmXchangeXchangeStreamVersionAccessors(providers, config);
        public OrgKnowmXchangeXchangeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.knowm.xchange.xchange.binance</b> with value <b>5.2.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBinance() { return getVersion("org.knowm.xchange.xchange.binance"); }

        /**
         * Version alias <b>org.knowm.xchange.xchange.bybit</b> with value <b>5.2.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBybit() { return getVersion("org.knowm.xchange.xchange.bybit"); }

        /**
         * Version alias <b>org.knowm.xchange.xchange.core</b> with value <b>5.2.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCore() { return getVersion("org.knowm.xchange.xchange.core"); }

        /**
         * Group of versions at <b>versions.org.knowm.xchange.xchange.stream</b>
         */
        public OrgKnowmXchangeXchangeStreamVersionAccessors getStream() {
            return vaccForOrgKnowmXchangeXchangeStreamVersionAccessors;
        }

    }

    public static class OrgKnowmXchangeXchangeStreamVersionAccessors extends VersionFactory  {

        public OrgKnowmXchangeXchangeStreamVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.knowm.xchange.xchange.stream.binance</b> with value <b>5.2.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBinance() { return getVersion("org.knowm.xchange.xchange.stream.binance"); }

        /**
         * Version alias <b>org.knowm.xchange.xchange.stream.bybit</b> with value <b>5.2.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBybit() { return getVersion("org.knowm.xchange.xchange.stream.bybit"); }

        /**
         * Version alias <b>org.knowm.xchange.xchange.stream.core</b> with value <b>5.2.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCore() { return getVersion("org.knowm.xchange.xchange.stream.core"); }

    }

    public static class OrgProjectlombokVersionAccessors extends VersionFactory  {

        public OrgProjectlombokVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.projectlombok.lombok</b> with value <b>1.18.26</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLombok() { return getVersion("org.projectlombok.lombok"); }

    }

    public static class OrgSpringframeworkVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootVersionAccessors vaccForOrgSpringframeworkBootVersionAccessors = new OrgSpringframeworkBootVersionAccessors(providers, config);
        public OrgSpringframeworkVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot</b>
         */
        public OrgSpringframeworkBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringVersionAccessors vaccForOrgSpringframeworkBootSpringVersionAccessors = new OrgSpringframeworkBootSpringVersionAccessors(providers, config);
        public OrgSpringframeworkBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.plagin</b> with value <b>3.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPlagin() { return getVersion("org.springframework.boot.plagin"); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring</b>
         */
        public OrgSpringframeworkBootSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkBootSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootVersionAccessors vaccForOrgSpringframeworkBootSpringBootVersionAccessors = new OrgSpringframeworkBootSpringBootVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot</b>
         */
        public OrgSpringframeworkBootSpringBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootSpringBootVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootConfigurationVersionAccessors vaccForOrgSpringframeworkBootSpringBootConfigurationVersionAccessors = new OrgSpringframeworkBootSpringBootConfigurationVersionAccessors(providers, config);
        private final OrgSpringframeworkBootSpringBootStarterVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors = new OrgSpringframeworkBootSpringBootStarterVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.configuration</b>
         */
        public OrgSpringframeworkBootSpringBootConfigurationVersionAccessors getConfiguration() {
            return vaccForOrgSpringframeworkBootSpringBootConfigurationVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.starter</b>
         */
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootConfigurationVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkBootSpringBootConfigurationVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.configuration.processor</b> with value <b>3.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getProcessor() { return getVersion("org.springframework.boot.spring.boot.configuration.processor"); }

    }

    public static class OrgSpringframeworkBootSpringBootStarterVersionAccessors extends VersionFactory  implements VersionNotationSupplier {

        private final OrgSpringframeworkBootSpringBootStarterDataVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors = new OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter</b> with value <b>3.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> asProvider() { return getVersion("org.springframework.boot.spring.boot.starter"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.amqp</b> with value <b>3.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAmqp() { return getVersion("org.springframework.boot.spring.boot.starter.amqp"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.test</b> with value <b>3.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTest() { return getVersion("org.springframework.boot.spring.boot.starter.test"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.web</b> with value <b>3.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getWeb() { return getVersion("org.springframework.boot.spring.boot.starter.web"); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.starter.data</b>
         */
        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors getData() {
            return vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterDataVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.data.jpa</b> with value <b>3.0.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJpa() { return getVersion("org.springframework.boot.spring.boot.starter.data.jpa"); }

    }

    public static class OrgTelegramVersionAccessors extends VersionFactory  {

        private final OrgTelegramTelegrambotsVersionAccessors vaccForOrgTelegramTelegrambotsVersionAccessors = new OrgTelegramTelegrambotsVersionAccessors(providers, config);
        public OrgTelegramVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.telegram.telegrambots</b>
         */
        public OrgTelegramTelegrambotsVersionAccessors getTelegrambots() {
            return vaccForOrgTelegramTelegrambotsVersionAccessors;
        }

    }

    public static class OrgTelegramTelegrambotsVersionAccessors extends VersionFactory  {

        private final OrgTelegramTelegrambotsSpringVersionAccessors vaccForOrgTelegramTelegrambotsSpringVersionAccessors = new OrgTelegramTelegrambotsSpringVersionAccessors(providers, config);
        public OrgTelegramTelegrambotsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.telegram.telegrambots.longpolling</b> with value <b>7.2.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLongpolling() { return getVersion("org.telegram.telegrambots.longpolling"); }

        /**
         * Group of versions at <b>versions.org.telegram.telegrambots.spring</b>
         */
        public OrgTelegramTelegrambotsSpringVersionAccessors getSpring() {
            return vaccForOrgTelegramTelegrambotsSpringVersionAccessors;
        }

    }

    public static class OrgTelegramTelegrambotsSpringVersionAccessors extends VersionFactory  {

        private final OrgTelegramTelegrambotsSpringBootVersionAccessors vaccForOrgTelegramTelegrambotsSpringBootVersionAccessors = new OrgTelegramTelegrambotsSpringBootVersionAccessors(providers, config);
        public OrgTelegramTelegrambotsSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.telegram.telegrambots.spring.boot</b>
         */
        public OrgTelegramTelegrambotsSpringBootVersionAccessors getBoot() {
            return vaccForOrgTelegramTelegrambotsSpringBootVersionAccessors;
        }

    }

    public static class OrgTelegramTelegrambotsSpringBootVersionAccessors extends VersionFactory  {

        public OrgTelegramTelegrambotsSpringBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.telegram.telegrambots.spring.boot.starter</b> with value <b>6.9.7.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getStarter() { return getVersion("org.telegram.telegrambots.spring.boot.starter"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
