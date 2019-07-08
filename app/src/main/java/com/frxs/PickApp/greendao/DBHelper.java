package com.frxs.PickApp.greendao;

import com.frxs.PickApp.greendao.servie.GoodsService;
import com.frxs.PickApp.greendao.utils.DbCore;

/**
 * Created by ewu on 2016/4/2.
 */
public class DBHelper {

    private static GoodsService goodsService;

    public static GoodsService getGoodsService() {
        if (goodsService == null) {
            goodsService = new GoodsService(DbCore.getDaoSession().getGoodsDao());
        }
        return goodsService;
    }


//    /**
//     * 读取表的所有信息
//     * @return
//     */
//    public List<Goods> loadAllGoods() {
//        List<Goods> goodses = new ArrayList<Goods>();
//        List<Goods> tmpGoodses = goodsDao.loadAll();
//        goodses.addAll(tmpGoodses);
//        return goodses;
//    }

//    public List<Goods> loadAllGoodsById(String orderId) {
//        List<Goods> goodses = new ArrayList<Goods>();
//        List<Goods> tmpGoodses = goodsDao.queryRaw("WHERE OrderID=?", orderId);
//        goodses.addAll(tmpGoodses);
//        return goodses;
//    }

//    public List<Goods> loadAllPickedGoodsById(String orderId)
//    {
//        List<Goods> goodsList = new ArrayList<Goods>();
//        //获取已经拣货的订单商品列表
//        goodsDao.queryRaw("WHERE OrderID=? AND IsPicked=?", orderId, "1");
//        return goodsList;
//    }

//    public long saveGoods(Goods entity){
//        return goodsDao.insertOrReplace(entity);
//    }
//
//    public void saveGoods(List<Goods> goodsList) {
//        goodsDao.insertInTx(goodsList);
//    }
//
//    /**
//     * 删除一条数据
//     * @param entity
//     */
//    public void deleteGoods(Goods entity) {
//        goodsDao.delete(entity);
//    }
//
//    /**
//     * 删除所有信息
//     * @return
//     */
//    public void deleteAllGoods() {
//        goodsDao.deleteAll();
//    }

//    public void deleteAllGoodsById(String orderId) {
//        List<Goods> tmpGoodses = goodsDao.queryRaw("WHERE OrderID=?", orderId);
//        if (null != tmpGoodses && tmpGoodses.size() > 0)
//        {
//            for (Goods item:tmpGoodses) {
//                goodsDao.delete(item);
//            }
//
//        }
//    }
//
//    /**
//     * 根据ID删除数据
//     * @param id
//     */
//    public void deleteGoodsById(long id){
//        goodsDao.deleteByKey(id);
//    }
//
//    /**
//     * 新增一条数据
//     * @param entity
//     */
//    public void addGoods(Goods entity)
//    {
//        goodsDao.insert(entity);
//    }
//
//    /**
//     * 修改一条数据
//     * @param entity
//     */
//    public void updateGoods(Goods entity)
//    {
//        goodsDao.update(entity);
//    }

//    public void updateGoodsById(Goods entity,long id)
//    {
//        goodsDao.updateKeyAfterInsert(entity,id);
//    }

//    /**
//     * 根据商品ID来更新该商品信息
//     */
//    public void updateGoodsByProductId(int productId)
//    {
//        QueryBuilder<Goods> qb = goodsDao.queryBuilder();
//        UpdateQuery<Goods> uq = qb.where(GoodsDao.Properties.ProductID.eq(productId));
//        if (qb.)
//    }
}
