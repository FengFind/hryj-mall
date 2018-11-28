package com.hryj.service.inventory.task;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 王光银
 * @className: ProductInventoryStoreTask
 * @description:
 * @create 2018/10/8 0008 17:07
 **/
@Slf4j
public class ProductInventoryStoreTask {

    static {
        new Thread(new StoreTask()).start();
    }

    private static final LinkedBlockingQueue<ProductInventoryAdjustmentItem> PROD_INVENTORY_STORE_QUEUE = new LinkedBlockingQueue<>();

    public static void add(ProductInventoryAdjustmentItem adjustmentItem) {
        if (adjustmentItem == null) {
            return;
        }
        try {
            PROD_INVENTORY_STORE_QUEUE.put(adjustmentItem);
        } catch (InterruptedException e) {
            log.error("添加商品库存持久化任务队列失败, 已开启独立任务处理", e);
            new Thread(() -> {
                while (true) {
                    try {
                        PROD_INVENTORY_STORE_QUEUE.put(adjustmentItem);
                        break;
                    } catch (InterruptedException e1) {
                        log.error("添加队列独立任务执行失败, 之后会继续尝试", e);
                        try {
                            Thread.sleep(50);
                        } catch (Exception e2) {
                            log.error("添加队列独立任务暂停失败:", e2);
                        }
                    }
                }
            }).start();
        }

        //启动任务处理队列中的数据

    }

    static class StoreTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    PROD_INVENTORY_STORE_QUEUE.take().store();
                    System.out.println("-------------: 执行库存更新");
                } catch (InterruptedException e) {
                    log.error("持久化商品库存任务失败:", e);
                }
            }
        }
    }
}
