package com.github.dejavuhuh.lego.file.adapter;

import com.github.dejavuhuh.lego.file.Resource;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author wu.yue
 * @since 2023/12/30 14:43
 */
@RequiredArgsConstructor
public class ByteArrayResource implements Resource {

    final byte[] bytes;

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }
}
