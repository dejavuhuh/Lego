package io.github.dejavuhuh.lego.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源
 *
 * @author wu.yue
 * @since 2023/12/30 14:07
 */
public interface Resource {

    InputStream getInputStream() throws IOException;
}
