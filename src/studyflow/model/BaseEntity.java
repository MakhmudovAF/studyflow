package studyflow.model;

import java.util.Objects;

public abstract class BaseEntity {
    private final long id;

    public BaseEntity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BaseEntity that = (BaseEntity) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}