package net.strokkur.convention;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.api.provider.Property;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

public class JavaConvention implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    final StrokkConventionExtension ext = project.getExtensions().create("strokkConventions", StrokkConventionExtension.class);

    project.getPlugins().apply("java-library");
    project.getExtensions().<JavaPluginExtension>configure("java", java -> {
      java.getToolchain().getLanguageVersion().set(ext.javaVersion
        .orElse(25)
        .map(JavaLanguageVersion::of));
    });

    project.getTasks().withType(JavaCompile.class).configureEach(
      compile -> compile.getOptions().getRelease()
    );

    if (project.getPlugins().hasPlugin("maven-publish")) {
      project.getExtensions().<PublishingExtension>configure("publishing", publishing -> {
        publishing.getRepositories().maven(maven -> {
          maven.authentication(auth -> {
            maven.credentials(PasswordCredentials.class, cred -> {
              cred.setUsername(System.getenv("NEXUS_USERNAME"));
              cred.setUsername(System.getenv("NEXUS_PASSWORD"));
            });
          });

          maven.setName("EldoNexus");
          if (project.getVersion().toString().endsWith("-SNAPSHOT")) {
            maven.setUrl("https://eldonexus.de/repository/maven-snapshots/");
          } else {
            maven.setUrl("https://eldonexus.de/repository/maven-releases/");
          }
        });

        publishing.getPublications().create("maven", MavenPublication.class, publication -> {
          publication.from(project.getComponents().getByName("java"));
          publication.withBuildIdentifier();
        });
      });
    }

    if (project.getPlugins().hasPlugin("checkstyle")) {
      project.getExtensions().<CheckstyleExtension>configure("checkstyle", checkstyle -> {
        checkstyle.setToolVersion("13.4.2");
        checkstyle.setConfig(project.getResources().getText().fromUri(
          Objects.requireNonNull(JavaConvention.class.getResource("/checkstyle/checkstyle.xml"))
        ));

        if (ext.checkstyleConvention.suppressionFile.isPresent()) {
          checkstyle.setConfigProperties(Map.of(
            "suppressionFile", ext.checkstyleConvention.suppressionFile.get().getAsFile().getAbsolutePath()
          ));
        }
      });
    }
  }

  public static abstract class StrokkConventionExtension {
    private final StrokkCheckstyleConvention checkstyleConvention;

    public final Property<Integer> javaVersion;

    @Inject
    public StrokkConventionExtension(ObjectFactory objects) {
      javaVersion = objects.property(Integer.class);

      checkstyleConvention = new StrokkCheckstyleConvention(objects);
    }

    public StrokkCheckstyleConvention getCheckstyle() {
      return checkstyleConvention;
    }

    public void checkstyle(Action<StrokkCheckstyleConvention> configure) {
      configure.execute(checkstyleConvention);
    }
  }

  public record StrokkCheckstyleConvention(Property<RegularFile> suppressionFile) {
    public StrokkCheckstyleConvention(ObjectFactory suppressionFile) {
      this(suppressionFile.fileProperty());
    }
  }
}
