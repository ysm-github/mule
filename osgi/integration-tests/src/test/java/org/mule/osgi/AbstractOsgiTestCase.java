/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFileExtend;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;

@RunWith(PaxExam.class)
public abstract class AbstractOsgiTestCase
{

    public String karafVersion()
    {
        ConfigurationManager cm = new ConfigurationManager();
        String karafVersion = cm.getProperty("pax.exam.karaf.version", "4.0.1");
        return karafVersion;
    }

    @Configuration
    public Option[] config() throws URISyntaxException
    {
        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.mule.osgi")
                .artifactId("mule-osgi-standalone")
                .versionAsInProject()
                .type("zip");
        //MavenArtifactUrlReference karafUrl = maven()
        //        .groupId("org.apache.karaf")
        //        .artifactId("apache-karaf")
        //        .version(karafVersion())
        //        .type("zip");
        MavenUrlReference karafStandardRepo = maven()
                .groupId("org.apache.karaf.features")
                .artifactId("standard")
                .version(karafVersion())
                .classifier("features")
                .type("xml");
        //TODO(pablo.kraan): OSGi - Use version from a mavel URL
        final String muleFeatures = "mvn:org.mule.osgi/mule-osgi-features/4.0-SNAPSHOT/xml/features";
        final File unpackDirectory = new File("target", "exam");
        return new Option[] {
                //TODO(pablo.kraan): OSGi - add some system property to enable debugging without needing re-build
                //KarafDistributionOption.debugConfiguration("5005", true),
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(unpackDirectory)
                        .useDeployFolder(false),

                // This install only the specified default features
                editConfigurationFilePut("etc/org.apache.karaf.features.cfg", "featuresBoot", "mule-core,mule-spring-config"),
                editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories",", file:${maven.local.repo}@id=mavenlocalrepo@snapshots"),

                //editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg","org.ops4j.pax.url.mvn.defaultRepositories",
                //                         "file:${karaf.home}/${karaf.default.repository}@id=system.repository@snapshots,\n" +
                //                         "file:${karaf.data}/kar@id=kar.repository@multi@snapshots,\n" +
                //                         "file:/Users/pablokraan/.m2/repository/jar/@id=local"),
                //replaceConfigurationFile("etc/startup.properties", new File(getClass().getClassLoader().getResource("startup.properties").toURI())),
                editConfigurationFileExtend("etc/org.apache.karaf.features.cfg", "featuresRepositories", muleFeatures.toString()),
                keepRuntimeFolder(),
                //systemProperty("pax.exam.osgi.unresolved.fail").value("true"),
                logLevel(LogLevelOption.LogLevel.INFO),
                configureConsole().ignoreLocalConsole(),
                //features(karafStandardRepo, "scr"),
                //features(maven().groupId("org.mule.osgi")
                //                 .artifactId("mule-osgi-features").type("xml").classifier("features")
                //                 .versionAsInProject(), "mule-spring-config"),
                mavenBundle()
                        .groupId("org.mule.osgi")
                        .artifactId("mule-osgi-sample-app")
                        .versionAsInProject().start(),

                //vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
                //systemTimeout(0),
                //debugConfiguration("5005", true),
                // KarafDistributionOption.debugConfiguration("5005", true),
        };
    }
}
