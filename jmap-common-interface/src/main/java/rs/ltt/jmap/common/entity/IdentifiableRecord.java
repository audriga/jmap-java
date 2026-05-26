package rs.ltt.jmap.common.entity;

public interface IdentifiableRecord extends Identifiable {
    String id();

    @Override
    default String getId() {
        return id();
    }
}
