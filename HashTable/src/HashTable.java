import java.util.LinkedList;

public class HashTable<K, V> {

	// is actually a LinkedList<Entry<K,V>>[]
	private Object[] table;

	private int size;

	private final double MAX_LOAD_FACTOR = .7;

	private final int initcapacity;

	public HashTable(int initcapacity) {
		table = new Object[initcapacity];
		this.initcapacity = initcapacity;
		size = 0;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<Entry<K, V>> getIndex(int index) {
		return (LinkedList<Entry<K, V>>) table[index];
	}

	private void put(Entry<K, V> entry, int index) {
		LinkedList<Entry<K, V>> list = getIndex(index);
		if (list == null) {
			table[index] = new LinkedList<Entry<K, V>>();
			list = getIndex(index);
		}
		int indexfound = -1;
		for (int i = 0; i < list.size(); i++) {
			if (entry.getKey().equals(list.get(i).getKey())) {
				indexfound = i;
				break;
			}
		}

		if (indexfound != -1) {
			list.remove(indexfound);
			size--;

		}
		list.add(entry);
		size++;

		if (loadFactor() > MAX_LOAD_FACTOR) {
			doubleCapacity();
		}
	}

	private V get(K key, int index) {
		LinkedList<Entry<K, V>> list = getIndex(index);
		if (list == null)
			return null;
		for (Entry<K, V> entry : list) {
			if (key.equals(entry.getKey()))
				return entry.getValue();
		}
		return null;
	}

	private boolean remove(K key, int index) {
		LinkedList<Entry<K, V>> list = getIndex(index);
		if (list != null && list.remove(new Entry<K, V>(key, null))) {
			size--;
			return true;
		} else
			return false;
	}

	private boolean contains(K key, int index) {
		LinkedList<Entry<K, V>> list = getIndex(index);
		return list != null && list.contains(new Entry<K, V>(key, null));
	}

	// returns an index to put entry in
	public int hashFunc(K key) {
		return Math.abs(key.hashCode()) % table.length;
	}

	public void put(K key, V value) {
		put(new Entry<K, V>(key, value), hashFunc(key));
	}

	public V get(K key) {
		return get(key, hashFunc(key));
	}

	public boolean remove(K key) {
		return remove(key, hashFunc(key));
	}

	public boolean contains(K key) {
		return contains(key, hashFunc(key));
	}

	private void doubleCapacity() {
		Object[] oldtable = this.table;
		Object[] newtable = new Object[table.length * 2];
		this.table = newtable;
		size = 0;
		for (Object o : oldtable) {
			@SuppressWarnings("unchecked")
			LinkedList<Entry<K, V>> list = (LinkedList<Entry<K, V>>) o;

			if (list != null) {
				for (Entry<K, V> entry : list) {
					put(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	public double loadFactor() {
		return (double) size / table.length;
	}

	public int numberOfCollisions() {
		int num = 0;
		for (int i = 0; i < table.length; i++) {
			LinkedList<Entry<K, V>> list = getIndex(i);
			if (list != null && list.size() > 1) {
				num += list.size() - 1;
			}
		}
		return num;
	}

	public int size() {
		return size;
	}

	public int tableSize() {
		return table.length;
	}

	public void clear() {
		size = 0;
		table = new Object[initcapacity];
	}

	@Override
	public String toString() {
		String s = "";
		s += "-----------------------------------------\n";
		for (int i = 0; i < table.length; i++) {
			LinkedList<Entry<K, V>> list = getIndex(i);
			if (list != null) {
				for (Entry<K, V> entry : list) {
					s += entry.getKey() + ", ";
				}
			}
			s += "\n";
		}
		s += "-----------------------------------------\n";
		return s;
	}
}

class Entry<K, V> {

	private final K key;
	private final V value;

	public Entry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object another) {
		return key.equals(((Entry<K, V>) another).key);
	}

	@SuppressWarnings("unchecked")
	public boolean fullyEquals(Object another) {
		return key.equals(((Entry<K, V>) another).key) && value.equals(((Entry<K, V>) another).value);
	}
}
