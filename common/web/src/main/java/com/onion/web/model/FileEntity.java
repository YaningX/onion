package com.onion.web.model;

import javax.persistence.*;

/**
 * Created by sunhonglin on 16-1-7.
 */
@Entity
@Table(name = "file", schema = "storage", catalog = "")
public class FileEntity {
    private int id;
    private String filename;
    private String blockIDs;
    private String username;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "filename", nullable = false, length = 50)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic
    @Column(name = "blockIDs", nullable = false, length = -1)
    public String getBlockIDs() {
        return blockIDs;
    }

    public void setBlockIDs(String blockIDs) {
        this.blockIDs = blockIDs;
    }

    @Basic
    @Column(name = "username", nullable = false, length = 20)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntity that = (FileEntity) o;

        if (id != that.id) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (blockIDs != null ? !blockIDs.equals(that.blockIDs) : that.blockIDs != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (blockIDs != null ? blockIDs.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
