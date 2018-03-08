package ru.stoloto.support;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import ru.stoloto.util.FileParameter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Mojo(name = "generate-docs", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        threadSafe = false,requiresDependencyCollection = ResolutionScope.RUNTIME,requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ParserMojo extends AbstractMojo {

    @Component
    protected MavenProject mavenProject;
    @Component
    protected MavenSession mavenSession;

    @Parameter
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private FileParameter file;

    @Parameter
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private FileParameter docpath;

    @Parameter(defaultValue = "UTF-8")
    @SuppressWarnings("unused")
    private String charset;


    public void scanComments(File projectDir) {


        file.getPath().mkdir();

        getLog().info(projectDir.getAbsolutePath());
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        Optional supertype = n.getExtendedTypes().stream().findFirst();
                        //  if (n.getComment().isPresent() && n.isAnnotationPresent(Deprecated.class)) {
                            n.getFields().forEach(c -> getLog().info(c.getComment().get().getContent()));
                       // }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);


        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        Optional supertype = n.getExtendedTypes().stream().findFirst();
                        //  if (n.getComment().isPresent() && n.isAnnotationPresent(Deprecated.class)) {
                        n.getFields().forEach(c -> getLog().info(c.getComment().get().getContent()));
                        // }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(docpath.getPath());
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( "Starting analyze the project..." );
        scanComments(mavenProject.getBasedir());

    }

}
