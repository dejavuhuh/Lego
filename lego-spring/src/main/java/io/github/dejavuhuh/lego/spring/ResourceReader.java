package io.github.dejavuhuh.lego.spring;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/13 00:38
 */
public class ResourceReader {

    public static String readFileFromResources(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] fileBytes = Files.readAllBytes(resource.getFile().toPath());
        return new String(fileBytes);
    }
}
