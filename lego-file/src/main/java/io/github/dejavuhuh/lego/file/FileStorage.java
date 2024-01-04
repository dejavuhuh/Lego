package io.github.dejavuhuh.lego.file;

import java.io.InputStream;

/**
 * 文件存储
 *
 * @author wu.yue
 * @since 2023/12/30 14:31
 */
public interface FileStorage {

    String put(InputStream inputStream);

    InputStream get(String id);

    void delete(String id);
}
