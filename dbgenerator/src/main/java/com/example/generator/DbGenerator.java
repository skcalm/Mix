package com.example.generator;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class DbGenerator {
    public final static int SCHEMA_VERSION = 1;
    public final static String SCHEMA_PACKAGE = "example.com.mix.data.dao.gen";
    public final static String SCHEMA_OUT_DIR = "app/src/main/java";

    private Schema schema;
    public static void main(String[] args) throws Exception{
        DbGenerator dbGenerator = new DbGenerator();
        dbGenerator.generate();
    }

    public void generate() throws Exception{
        DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(schema, SCHEMA_OUT_DIR);
    }

    public DbGenerator(){
        schema = new Schema(SCHEMA_VERSION, SCHEMA_PACKAGE);
        schema.enableKeepSectionsByDefault();

        createUser();
    }

    public void createUser(){
        Entity entity = schema.addEntity("User");
        entity.addIdProperty().autoincrement();
        entity.addStringProperty("name").unique();
        entity.addStringProperty("tel");
        entity.addIntProperty("gender");
    }
}
