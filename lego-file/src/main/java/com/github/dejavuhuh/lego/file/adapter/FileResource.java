package com.github.dejavuhuh.lego.file.adapter;

import com.github.dejavuhuh.lego.file.Resource;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author wu.yue
 * @since 2023/12/30 14:53
 */
@RequiredArgsConstructor
public class FileResource implements Resource {

    final File file;

    public FileResource(String pathname) {
        this(new File(pathname));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file.toPath());
    }
}
