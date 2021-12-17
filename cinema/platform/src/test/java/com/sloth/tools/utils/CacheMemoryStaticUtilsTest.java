package com.sloth.tools.utils;

import com.sloth.tools.constant.CacheConstants;
import com.sloth.tools.util.CacheMemoryStaticUtils;
import com.sloth.tools.util.CacheMemoryUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2019/01/04
 *     desc  : test CacheMemoryStaticUtils
 * </pre>
 */
public class CacheMemoryStaticUtilsTest extends BaseTest {

    private CacheMemoryUtils mCacheMemoryUtils = CacheMemoryUtils.getInstance(3);

    @Before
    public void setUp() {
        for (int i = 0; i < 10; i++) {
            CacheMemoryStaticUtils.put(String.valueOf(i), i);
        }
        for (int i = 0; i < 10; i++) {
            CacheMemoryStaticUtils.put(String.valueOf(i), i, mCacheMemoryUtils);
        }
    }

    @Test
    public void get() {
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(i), CacheMemoryStaticUtils.get(String.valueOf(i)));
        }
        for (int i = 0; i < 10; i++) {
            if (i < 7) {
                assertNull(CacheMemoryStaticUtils.get(String.valueOf(i), mCacheMemoryUtils));
            } else {
                assertEquals(Integer.valueOf(i), CacheMemoryStaticUtils.get(String.valueOf(i), mCacheMemoryUtils));
            }
        }
    }

    @Test
    public void getExpired() throws Exception {
        CacheMemoryStaticUtils.put("10", 10, 2 * CacheConstants.SEC);

        assertEquals(Integer.valueOf(10), CacheMemoryStaticUtils.get("10"));
        Thread.sleep(1500);
        assertEquals(Integer.valueOf(10), CacheMemoryStaticUtils.get("10"));
        Thread.sleep(1500);
        assertNull(CacheMemoryStaticUtils.get("10"));
    }

    @Test
    public void getDefault() {
        assertNull(CacheMemoryStaticUtils.get("10"));
        assertEquals("10", CacheMemoryStaticUtils.get("10", "10"));
    }

    @Test
    public void getCacheCount() {
        assertEquals(10, CacheMemoryStaticUtils.getCacheCount());
        assertEquals(3, CacheMemoryStaticUtils.getCacheCount(mCacheMemoryUtils));
    }

    @Test
    public void remove() {
        assertEquals(0, CacheMemoryStaticUtils.remove("0"));
        assertNull(CacheMemoryStaticUtils.get("0"));
        assertNull(CacheMemoryStaticUtils.remove("0"));
    }

    @Test
    public void clear() {
        CacheMemoryStaticUtils.clear();
        CacheMemoryStaticUtils.clear(mCacheMemoryUtils);

        for (int i = 0; i < 10; i++) {
            assertNull(CacheMemoryStaticUtils.get(String.valueOf(i)));
        }
        for (int i = 0; i < 10; i++) {
            assertNull(CacheMemoryStaticUtils.get(String.valueOf(i), mCacheMemoryUtils));
        }
        assertEquals(0, CacheMemoryStaticUtils.getCacheCount());
        assertEquals(0, CacheMemoryStaticUtils.getCacheCount(mCacheMemoryUtils));
    }
}