package ru.stoloto.support;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

@Log4j2
public class ParserMojoTest extends TestCase {

    @Test
    public void test() throws IOException {
        Files.walk(new java.io.File( "C://users/user//IdeaProjects/" ).toPath())
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".java"))
                .forEach(j ->{
                    try {
                        CompilationUnit cu = JavaParser.parse(j);
                        cu.getComments().forEach(comment -> {
                            log.info(comment.getContent());
                        });
                    } catch (IOException e) {
                        log.error(e);
                    }
                });

    }
}