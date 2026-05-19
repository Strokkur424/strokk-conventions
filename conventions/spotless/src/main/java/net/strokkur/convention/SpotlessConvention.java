package net.strokkur.convention;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.diffplug.gradle.spotless.SpotlessPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;
import java.io.InputStream;

public class SpotlessConvention implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(SpotlessPlugin.class);
    final LicenseExtension ext = project.getExtensions().create("license", LicenseExtension.class);

    project.getExtensions().<SpotlessExtension>configure("spotless", spotless ->
      spotless.java(java -> {
        java.target("**/*.java");
        if (ext.header.isPresent()) {
          java.licenseHeader(ext.header.get());
        }
      })
    );
  }

  public static abstract class LicenseExtension {
    private final ProviderFactory providers;
    private final Property<String> header;

    public final Property<String> headerName;
    public final Property<String> headerDescription;

    private final Project project;

    @Inject
    public LicenseExtension(ObjectFactory objects, ProviderFactory providers, Project project) {
      this.providers = providers;
      this.project = project;

      this.header = objects.property(String.class);
      this.headerName = objects.property(String.class);
      this.headerDescription = objects.property(String.class);
    }

    public void useMIT() {
      loadHeader("mit");
    }

    public void useGPL() {
      loadHeader("gpl");
    }

    public void useLGPL() {
      loadHeader("lgpl");
    }

    private void loadHeader(String license) {
      try (InputStream stream = SpotlessConvention.class.getResourceAsStream("/" + license + ".txt")) {
        if (stream == null) {
          return;
        }

        final String content = new String(stream.readAllBytes());
        header.set(providers.provider(() -> {
          String out = content.replaceAll("<<name>>", headerName.getOrElse(project.getName()));
          out = out.replaceAll("<<description>>", headerDescription
            .orElse(providers.provider(project::getDescription))
            .getOrElse("no description set"));
          return out;
        }));
      } catch (Exception e) {
        throw new RuntimeException("Failed to load header text", e);
      }
    }
  }
}
