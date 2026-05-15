package net.strokkur.convention;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.diffplug.gradle.spotless.SpotlessPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.nio.file.Files;

public class SpotlessConvention implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(SpotlessPlugin.class);
    final StrokkSpotlessExtension ext = project.getExtensions().create("strokkConventionsSpotless", StrokkSpotlessExtension.class);

    project.getExtensions().<SpotlessExtension>configure("spotless", spotless ->
      spotless.java(java -> {
        if (ext.header.isPresent()) {
          java.licenseHeaderFile(ext.header);
        } else if (Files.exists(project.getRootDir().toPath().resolve("HEADER"))) {
          java.licenseHeaderFile(project.getRootProject().file("HEADER"));
        }
        java.target(ext.target.orElse("**/*.java"));
      })
    );
  }

  public static abstract class StrokkSpotlessExtension {
    public final Property<Object> header;
    public final Property<String> target;

    @Inject
    public StrokkSpotlessExtension(ObjectFactory objects) {
      header = objects.property(Object.class);
      target = objects.property(String.class);
    }
  }
}
