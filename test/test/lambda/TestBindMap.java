package test.lambda;

import static org.junit.Assert.*;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import lambda.BindMap;
import lambda.Restorable;

public class TestBindMap {

    static class M<K, V> extends BindMap<K, V> {
        public M(Map<K, V> map) {
            super(map);
        }
    }
    
    @Test
    public void testGeOrDefault() {
        BindMap<String, Integer> b = new M<>(new HashMap<>());
        assertEquals(null, b.get("one"));
        assertEquals(1, (int)b.getOrDefault("one", 1));
        b.define("one", 1);
        assertEquals(1, (int)b.get("one"));
        assertEquals(1, (int)b.getOrDefault("one", 100));
    }

    @Test
    public void testPutRemove() {
        BindMap<String, Integer> b = new M<>(new HashMap<>());
        try (Restorable r = b.put("one", 1)) {
            assertEquals(1, (int)b.get("one"));
        }
        assertEquals(null, b.get("one"));
    }

    @Test
    public void testPutRestore() {
        BindMap<String, Integer> b = new M<>(new HashMap<>());
        b.define("one", 100);
        try (Restorable r = b.put("one", 1)) {
            assertEquals(1, (int)b.get("one"));
        }
        assertEquals(100, (int)b.get("one"));
    }
    
    @Test
    public void testIdentityMap() {
        BindMap<String, Integer> b = new M<>(new IdentityHashMap<>());
        String one = "one";
        String one2 = new StringBuilder(one).toString();
        b.define(one, 1);
        b.define(one2, 2);
        assertEquals(1, (int)b.get(one));
        assertEquals(2, (int)b.get(one2));
    }
    
    @Test
    public void testEntrySet() {
        BindMap<String, Integer> b = new M<>(new HashMap<>());
        b.define("one", 1);
        b.define("two", 2);
        Iterator<Entry<String, Integer>> it = b.entrySet().iterator();
        assertTrue(it.hasNext());
        Entry<String, Integer> e0 = it.next();
        assertEquals("one", e0.getKey());
        assertEquals(1, (int)e0.getValue());
        assertTrue(it.hasNext());
        Entry<String, Integer> e1 = it.next();
        assertEquals("two", e1.getKey());
        assertEquals(2, (int)e1.getValue());
        assertFalse(it.hasNext());
    }
    
    @Test
    public void testSize() {
        BindMap<String, Integer> b = new M<>(new HashMap<>());
        assertEquals(0, b.size());
        b.define("one", 1);
        assertEquals(1, b.size());
        b.define("two", 2);
        assertEquals(2, b.size());
    }
    
    @Test
    public void testToString() {
        BindMap<String, Integer> b = new M<>(new HashMap<>());
        b.define("one", 1);
        b.define("two", 2);
        assertEquals("{one=1, two=2}", b.toString());
    }

}
