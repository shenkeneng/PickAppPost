package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class PickDaoGenerator {

    public static void main(String[] args) throws Exception{
        Schema schema = new Schema(4, "com.frxs.PickApp.greendao");
//      Schema schema = new Schema(1, "me.itangqi.bean");
//      schema.setDefaultJavaPackageDao("me.itangqi.dao");

        // schema2.enableActiveEntitiesByDefault();
        // schema2.enableKeepSectionsByDefault();
        addGoods(schema);

        new DaoGenerator().generateAll(schema, "../PickAppPost/app/src/main/java");
    }

    /*
     * @param schema
     */
    private static void addGoods(Schema schema){
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Goods」（既类名）
        Entity entity = schema.addEntity("Goods");

        // 重新给表命名
        // note.setTableName("NODE");


        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段：
        entity.addIdProperty();// 自增长ID
        entity.addStringProperty("ID");// 设置字段“ID”
        entity.addStringProperty("OrderID");
        entity.addIntProperty("ProductID");
        entity.addStringProperty("SKU");
        entity.addStringProperty("ProductName");//商品名字
        entity.addStringProperty("BarCode");
        entity.addStringProperty("ProductImageUrl400");
        entity.addIntProperty("ShelfAreaID");
        entity.addStringProperty("ShelfCode");
        entity.addDoubleProperty("SaleQty").notNull();
        entity.addStringProperty("SaleUnit");
        entity.addDoubleProperty("SalePackingQty").notNull();
        entity.addDoubleProperty("PickQty").notNull();
        entity.addStringProperty("Remark");
        entity.addDoubleProperty("PreQty").notNull();
        entity.addDoubleProperty("SalePrice").notNull();
        entity.addStringProperty("Unit");
        entity.addDoubleProperty("UnitQty").notNull();
        entity.addDoubleProperty("UnitPrice").notNull();
        entity.addIntProperty("IsPicked").notNull();// 已拣标识 0:未拣 1:已拣
        entity.addIntProperty("IsModify").notNull();// 修改数量切换单位标识 0:未修改 1:已修改
        entity.addStringProperty("PreUnit");
        entity.addDoubleProperty("PreNum").notNull();
        entity.addDoubleProperty("PreSalePackingQty").notNull();
        entity.addStringProperty("CurrentUnit");
        entity.addStringProperty("PrePickQty");
        entity.addStringProperty("IsGiftStr");
        entity.implementsSerializable();
    }
}
