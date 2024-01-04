package io.github.dejavuhuh.lego.file.s3;

import lombok.Value;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2023/12/30 15:25
 */
@Value
public class S3Config {

    String endpoint;
    String region;
    String bucket;
    String accessKey;
    String secretKey;
}
