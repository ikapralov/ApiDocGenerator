package ru.stoloto.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Optional;

@Log4j2
public class ParserMojoTest extends TestCase {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws IOException {

        Files.walk(new java.io.File( "C://users/user//IdeaProjects/parser/").toPath())
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


    @Test
    public void test2() throws IOException {

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver
                (new File("C://users/user//IdeaProjects/plugintest/")));

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8);
        parserConfiguration.setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

        JavaParser.setStaticConfiguration(parserConfiguration);

        Files.walk(new java.io.File( "C://users/user//IdeaProjects/plugintest/").toPath())
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".java"))
                .forEach(j ->{
                    try {
                        CompilationUnit cu = JavaParser.parse(j);

                        cu.findAll(MethodDeclaration.class).forEach(node -> {

                            if (node.isAnnotationPresent("RemoteMethod")) {

                                node.getParameters().forEach(parameter -> {
                                    ResolvedReferenceType type = parameter.getType().asClassOrInterfaceType().resolve();

                                });
//                                ResolvedType typeOfTheNode = JavaParserFacade.get(combinedTypeSolver).getType(node);
//                                log.info(typeOfTheNode.describe());
                            }

                        });
                    } catch (IOException e) {
                        log.error(e);
                    }
                });
    }
}