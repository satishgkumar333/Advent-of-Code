package nl.nibsi.aoc.puzzle.plugin;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "generate")
public final class GenerateMojo extends AbstractMojo {
  
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}