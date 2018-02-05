package com.aspire.generic.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenericReading {
	static List<Apple> apples = Arrays.asList(new Apple());
	static List<Fruit> fruit = Arrays.asList(new Fruit());
	static class Reader<T> {
		T readExact(List<T> list) {
			return list.get(0);
		}
	}

	static void f1() {
		Reader<Fruit> fruitReader = new Reader<Fruit>();
		// Errors: List<Fruit> cannot be applied to List<Apple>.
		//Fruit f = fruitReader.readExact(apples);
	}

	public static void main(String[] args) {
		f1();
		f2();
	}
	
	static class CovariantReader<T> {
	    T readCovariant(List<? extends T> list) {
	        return list.get(0);
	    }
	}
	static void f2() {
	    CovariantReader<Fruit> fruitReader = new CovariantReader<Fruit>();
	    Fruit f = fruitReader.readCovariant(fruit);
	    Fruit a = fruitReader.readCovariant(apples);
	}
}
