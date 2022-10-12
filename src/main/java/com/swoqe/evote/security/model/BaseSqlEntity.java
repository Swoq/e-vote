package com.swoqe.evote.security.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseSqlEntity implements Serializable {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    protected UUID id;

    @Column(name = "created_time")
    protected long createdTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseSqlEntity that = (BaseSqlEntity) o;

        if (createdTime != that.createdTime) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (createdTime ^ (createdTime >>> 32));
        return result;
    }
}
