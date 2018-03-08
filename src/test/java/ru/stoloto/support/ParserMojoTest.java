package ru.stoloto.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Log4j2
public class ParserMojoTest extends TestCase {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        Files.walk(new java.io.File( "C://users/user//IdeaProjects/" ).toPath())
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".java"))
                .forEach(j ->{
                    try {
                        CompilationUnit cu = JavaParser.parse(j);
                        Optional<PackageDeclaration> packageDeclaration  = cu.getPackageDeclaration();
                        if (!packageDeclaration.isPresent()) {
                            log.info(j.getFileName());
                        }
                        cu.findAll(FieldDeclaration.class).forEach(fd -> {
                            log.info(fd.getVariables().get(0).getName().asString());
                        });
                    } catch (IOException e) {
                        log.error(e);
                    }
                });

    }

}