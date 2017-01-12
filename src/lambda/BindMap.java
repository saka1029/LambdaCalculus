package lambda;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * {@link java.util.Map} * のラッパークラスです。
 * 変数束縛の環境を表現するために使用します。
 * 
 * @author saka1029
 * @param <K> マップのキーとなる型です。
 * @param <V> マップの値となる型です。
 */
public class BindMap<K, V> {
    
    /**
     * ベースとなるマップです。
     */
    private final Map<K, V> map;
    
    /**
     * コンストラクタです。
     * @param map ベースとなるマップを指定します。
     */
    protected BindMap(Map<K, V> map) {
        this.map = map;
    }

    /**
     * マップのサイズを返します。
     * @return マップのサイズを返します。
     */
    public int size() {
        return map.size();
    }

    /**
     * キーkeyに関連付けられた値を返します。
     * 値が存在しない場合はnullを返します。
     * 
     * @param key キーを指定します。
     * @return 対応する値を返します。
     *         値が存在しない場合はnullを返します。
     */
    public V get(K key) {
        return map.get(key);
    }
    
    /**
     * キーkeyに関連付けられた値を返します。
     * 値が存在しない場合はdefaultValueを返します。
     * 
     * @param key キーを指定します。
     * @param defaultValue 対応する値がない場合に返す値を指定します。
     * @return 対応する値を返します。
     *         値が存在しない場合はdefaultValueを返します。
     */
    public V getOrDefault(K key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }
    
    /**
     * keyとvalueの対を追加します。
     * 
     * @param key キーを指定します。
     * @param value キーに関連付けられる値を指定します。
     * @return valueを返します。
     */
    public V define(K key, V value) {
        map.put(key, value);
        return value;
    }
    
    /**
     * keyとvalueの対を追加します。
     * 追加前の状態に戻すための{@link Restorable} を返します。
     * 返された値の{@code close()}を実行すると
     * 追加前の状態に戻ります。
     * 
     * @param key キーを指定します。
     * @param value キーに関連付けられる値を指定します。
     * @return 追加する前の状態に戻すための{@link Restorable}を返します。
     */
    public Restorable put(K key, V value) {
        V old = map.get(key);
        map.put(key, value);
        return old != null ?
            () -> map.put(key, old) :
            () -> map.remove(key);
    }

    /**
     * エントリーの集合を返します。
     * 
     * @return エントリーの集合を返します。
     */
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
    
    /**
     * 文字列表現を返します。
     * 単にベースとなるマップの文字列表現を返します。
     */
    @Override
    public String toString() {
        return map.toString();
    }
}
