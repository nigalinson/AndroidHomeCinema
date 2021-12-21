/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.fetcher;

import com.sloth.tools.util.LogUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cn.edu.hfut.dmic.webcollector.conf.CommonConfigured;
import cn.edu.hfut.dmic.webcollector.crawldb.DBManager;
import cn.edu.hfut.dmic.webcollector.crawldb.Generator;
import cn.edu.hfut.dmic.webcollector.crawldb.GeneratorFilter;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;

/**
 * 抓取器
 *
 * @author hu
 */
public class Fetcher extends CommonConfigured{

    public DBManager dbManager;

    public Executor executor;
    public NextFilter nextFilter = null;

    private AtomicInteger activeThreads;
    private AtomicInteger startedThreads;
    private AtomicInteger spinWaiting;
    private AtomicLong lastRequestStart;
    private QueueFeeder feeder = null;
    private FetchQueue fetchQueue = null;

    /**
     *
     */
    public static final int FETCH_SUCCESS = 1;

    /**
     *
     */
    public static final int FETCH_FAILED = 2;
    private int threads = 50;
    //private boolean isContentStored = false;

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     *
     */
    public static class FetchItem {

        public CrawlDatum datum;

        public FetchItem(CrawlDatum datum) {
            this.datum = datum;
        }
    }

    public static class FetchQueue {

        public AtomicInteger totalSize = new AtomicInteger(0);

        public final List<FetchItem> queue = Collections.synchronizedList(new LinkedList<FetchItem>());

        public void clear() {
            queue.clear();
        }

        public int getSize() {
            return queue.size();
        }

        public synchronized void addFetchItem(FetchItem item) {
            if (item == null) {
                return;
            }
            queue.add(item);
            totalSize.incrementAndGet();
        }

        public synchronized FetchItem getFetchItem() {
            if (queue.isEmpty()) {
                return null;
            }
            return queue.remove(0);
        }

        public synchronized void dump() {
            for (int i = 0; i < queue.size(); i++) {
                FetchItem it = queue.get(i);
                LogUtils.i( "  " + i + ". " + it.datum.url());
            }

        }

    }

    public static class QueueFeeder extends Thread {

        public FetchQueue queue;

        public DBManager dbManager;
        public Generator generator = null;
        public GeneratorFilter generatorFilter = null;
        public int size;

        public QueueFeeder(FetchQueue queue, DBManager dbManager, GeneratorFilter generatorFilter, int size) {
            this.queue = queue;
            this.dbManager = dbManager;
            this.generatorFilter = generatorFilter;
            this.size = size;
        }

        public void stopFeeder(){
            running = false;
            while (this.isAlive()) {
                try {
                    Thread.sleep(1000);
                     LogUtils.i("stopping feeder......");
                } catch (InterruptedException ex) {
                }
            }
        }

        public void closeGenerator() throws Exception {
            if(generator!=null) {
                generator.close();
                 LogUtils.i("close generator:" + generator.getClass().getName());
            }
        }

        public volatile boolean running = true;

        @Override
        public void run(){

            try {
                generator = dbManager.createGenerator(generatorFilter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
             LogUtils.i("create generator:" + generator.getClass().getName());
            String generatorFilterClassName = (generatorFilter==null)?"null":generatorFilter.getClass().getName();
             LogUtils.i("use generatorFilter:" + generatorFilterClassName);

            boolean hasMore = true;
            running = true;
            while (hasMore && running) {

                int feed = size - queue.getSize();
                if (feed <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    continue;
                }
                while (feed > 0 && hasMore && running) {

                    CrawlDatum datum = generator.next();
                    hasMore = (datum != null);

                    if (hasMore) {
                        queue.addFetchItem(new FetchItem(datum));
                        feed--;
                    }

                }

            }

        }

    }

    private class FetcherThread extends Thread {

        @Override
        public void run() {
            startedThreads.incrementAndGet();
            activeThreads.incrementAndGet();
            FetchItem item = null;
            try {

                while (running) {
                    try {
                        item = fetchQueue.getFetchItem();
                        if (item == null) {
                            if (feeder.isAlive() || fetchQueue.getSize() > 0) {
                                spinWaiting.incrementAndGet();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception ex) {
                                }

                                spinWaiting.decrementAndGet();
                                continue;
                            } else {
                                return;
                            }
                        }

                        lastRequestStart.set(System.currentTimeMillis());

                        CrawlDatum crawlDatum = item.datum;
                        //String url = crawlDatum.getUrl();
                        //Page page = getPage(crawlDatum);

                        //crawlDatum.incrRetry(page.getRetry());
//                        crawlDatum.setFetchTime(System.currentTimeMillis());
                        CrawlDatums next = new CrawlDatums();
                        try {
                            executor.execute(crawlDatum, next);
                            if (nextFilter != null) {
                                CrawlDatums filteredNext = new CrawlDatums();
                                for (int i = 0; i < next.size(); i++) {
                                    CrawlDatum filterResult = nextFilter.filter(next.get(i), crawlDatum);
                                    if (filterResult != null) {
                                        filteredNext.add(filterResult);
                                    }
                                }
                                next = filteredNext;
                            }

                             LogUtils.i(String.format("done: %s", crawlDatum.briefInfo()));

                            crawlDatum.setStatus(CrawlDatum.STATUS_DB_SUCCESS);
                        } catch (Exception ex) {
                             LogUtils.i(String.format("failed: %s", crawlDatum.briefInfo()), ex.toString());
                            crawlDatum.setStatus(CrawlDatum.STATUS_DB_FAILED);
                        }

                        crawlDatum.incrExecuteCount(1);
                        crawlDatum.setExecuteTime(System.currentTimeMillis());
                        try {
                            dbManager.writeFetchSegment(crawlDatum);
                            if (crawlDatum.getStatus() == CrawlDatum.STATUS_DB_SUCCESS && !next.isEmpty()) {
                                dbManager.writeParseSegment(next);
                            }
                        } catch (Exception ex) {
                             LogUtils.i("Exception when updating db", ex.toString());
                        }
                        long executeInterval = getConf().getExecuteInterval();
                        if (executeInterval > 0) {
                            try {
                                Thread.sleep(executeInterval);
                            } catch (Exception sleepEx) {
                            }
                        }

                    } catch (Exception ex) {
                         LogUtils.i("Exception", ex.toString());
                    }
                }

            } catch (Exception ex) {
                 LogUtils.i("Exception", ex.toString());

            } finally {
                activeThreads.decrementAndGet();
            }

        }

    }

    /**
     * 抓取当前所有任务，会阻塞到爬取完成
     *
     * @throws IOException 异常
     */
    public int fetchAll(GeneratorFilter generatorFilter) throws Exception {
        if (executor == null) {
             LogUtils.i("Please Specify An Executor!");
            return 0;
        }

        dbManager.merge();

        try {
            dbManager.initSegmentWriter();
             LogUtils.i("init segmentWriter:" + dbManager.getClass().getName());
            running = true;
            lastRequestStart = new AtomicLong(System.currentTimeMillis());

            activeThreads = new AtomicInteger(0);
            startedThreads = new AtomicInteger(0);
            spinWaiting = new AtomicInteger(0);
            fetchQueue = new FetchQueue();
            feeder = new QueueFeeder(fetchQueue, dbManager, generatorFilter, 1000);
            feeder.start();

            FetcherThread[] fetcherThreads = new FetcherThread[threads];
            for (int i = 0; i < threads; i++) {
                fetcherThreads[i] = new FetcherThread();
                fetcherThreads[i].start();
            }

            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                 LogUtils.i("-activeThreads=" + activeThreads.get()
                        + ", spinWaiting=" + spinWaiting.get() + ", fetchQueue.size="
                        + fetchQueue.getSize());

                if (!feeder.isAlive() && fetchQueue.getSize() < 5) {
                    fetchQueue.dump();
                }

                if ((System.currentTimeMillis() - lastRequestStart.get()) > getConf().getThreadKiller()) {
                     LogUtils.i("Aborting with " + activeThreads + " hung threads.");
                    break;
                }

            } while (running && (startedThreads.get() != threads || activeThreads.get() > 0));
            running = false;
            long waitThreadEndStartTime = System.currentTimeMillis();
            if (activeThreads.get() > 0) {
                 LogUtils.i("wait for activeThreads to end");
            }
            /*等待存活线程结束*/
            while (activeThreads.get() > 0) {
                 LogUtils.i("-activeThreads=" + activeThreads.get());
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
                if (System.currentTimeMillis() - waitThreadEndStartTime > getConf().getWaitThreadEndTime()) {
                     LogUtils.i("kill threads");
                    for (int i = 0; i < fetcherThreads.length; i++) {
                        if (fetcherThreads[i].isAlive()) {
                            try {
                                fetcherThreads[i].stop();
                                 LogUtils.i("kill thread " + i);
                            } catch (Exception ex) {
                                 LogUtils.i("Exception", ex.toString());
                            }
                        }
                    }
                    break;
                }
            }
             LogUtils.i("clear all activeThread");
            feeder.stopFeeder();
            fetchQueue.clear();
        } finally {
            if(feeder!=null) {
                feeder.closeGenerator();
            }
            dbManager.closeSegmentWriter();
             LogUtils.i("close segmentWriter:" + dbManager.getClass().getName());
        }
        return feeder.generator.getTotalGenerate();
    }

    volatile boolean running;

    /**
     * 停止爬取
     */
    public void stop() {
        running = false;
    }

    /**
     * 返回爬虫的线程数
     *
     * @return 爬虫的线程数
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 设置爬虫的线程数
     *
     * @param threads 爬虫的线程数
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    public DBManager getDBManager() {
        return dbManager;
    }

    public void setDBManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }


    public NextFilter getNextFilter() {
        return nextFilter;
    }

    public void setNextFilter(NextFilter nextFilter) {
        this.nextFilter = nextFilter;
    }
    
    

}
