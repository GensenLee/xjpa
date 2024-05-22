package org.devops.data.xjpa.table;


public class TableField implements TableFieldMetadata {
    private String Field;
    private String Comment;
    private String Type;
    private String Null;
    private String Extra;
    private String Privileges;
    private String Key;
    private String Default;

    public TableField() {
    }

    public TableField(String field, String comment, String type, String aNull, String extra, String privileges, String key, String aDefault) {
        Field = field;
        Comment = comment;
        Type = type;
        Null = aNull;
        Extra = extra;
        Privileges = privileges;
        Key = key;
        Default = aDefault;
    }

    @Override
    public String getField() {
        return Field;
    }


    @Override
    public String getComment() {
        return Comment;
    }


    @Override
    public String getType() {
        return Type;
    }


    @Override
    public String getNull() {
        return Null;
    }


    @Override
    public String getExtra() {
        return Extra;
    }


    @Override
    public String getPrivileges() {
        return Privileges;
    }


    @Override
    public String getKey() {
        return Key;
    }


    @Override
    public String getDefault() {
        return Default;
    }


    void setField(String field) {
        Field = field;
    }

    void setComment(String comment) {
        Comment = comment;
    }

    void setType(String type) {
        Type = type;
    }

    void setNull(String aNull) {
        Null = aNull;
    }

    void setExtra(String extra) {
        Extra = extra;
    }

    void setPrivileges(String privileges) {
        Privileges = privileges;
    }

    void setKey(String key) {
        Key = key;
    }

    void setDefault(String aDefault) {
        Default = aDefault;
    }
}
