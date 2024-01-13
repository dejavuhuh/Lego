package io.github.dejavuhuh.lego.id;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/13 01:37
 */
public class IdGenerator {

    private final Snowflake snowflake;

    public IdGenerator(WorkerIdProvider provider) {
        long workerId = provider.getWorkerId();
        this.snowflake = new Snowflake(workerId);
    }

    public long nextId() {
        return snowflake.nextId();
    }
}
