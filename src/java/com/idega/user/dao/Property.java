package com.idega.user.dao;

public class Property<K, V> {

	private K key;

	private V value;

	public Property(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Key: " + getKey() + ", value: " + getValue();
	}

}