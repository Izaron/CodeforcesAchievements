package com.izaron.cf.mongo.types;

public interface MongoBaseDocument<ID> {

    public void setId(ID id);
    public ID getId();
}
