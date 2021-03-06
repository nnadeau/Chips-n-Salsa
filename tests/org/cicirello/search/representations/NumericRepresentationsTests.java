/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
 *
 * This file is part of Chips-n-Salsa (https://chips-n-salsa.cicirello.org/).
 * 
 * Chips-n-Salsa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Chips-n-Salsa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.cicirello.search.representations;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * JUnit 4 test cases for the classes that represent inputs to
 * functions (univariate vs multivariate, integer inputs vs floating-point inputs).
 */
public class NumericRepresentationsTests {
	
	private final double EPSILON = 1e-10;
	
	@Test
	public void testUnivariate() {
		SingleReal f0 = new SingleReal();
		assertEquals("Default constructor", 0, f0.get(), EPSILON);
		SingleReal f5 = new SingleReal(5.0);
		assertEquals("Constructor", 5.0, f5.get(), EPSILON);
		assertEquals("Test get(i)", 0, f0.get(0), EPSILON);
		assertEquals("Test get(i)", 5.0, f5.get(0), EPSILON);
		double[] array1 = f0.toArray(null);
		assertEquals("Test toArray null param", 0, array1[0], EPSILON);
		double[] array2 = f5.toArray(null);
		assertEquals("Test toArray null param", 5, array2[0], EPSILON);
		double[] array3 = f5.toArray(array1);
		assertTrue("Returned array should be same array object", array1 == array3);
		assertEquals("Test toArray non-null param", 5, array3[0], EPSILON);
		
		SingleReal copy = new SingleReal(f5);
		assertEquals("Copy Constructor", 5.0, copy.get(), EPSILON);
		SingleReal copy2 = f5.copy();
		assertEquals("Copy method", 5.0, copy2.get(), EPSILON);
		assertTrue("Verify copy method returns a new object", copy2 != f5);
		assertEquals("Verify copy method returns correct runtime type", f5.getClass(), copy2.getClass());
		f0.set(10);
		assertEquals("Test the set and get methods", 10.0, f0.get(), EPSILON);
		f0.set(0, 8);
		assertEquals("Test the set(i) and get(i) methods", 8.0, f0.get(0), EPSILON);
		
		assertEquals("Testing equals method", f5, copy);
		assertEquals("Testing equals method", f5, copy2);
		assertEquals("hashCodes of equal objects should be equal", f5.hashCode(), copy.hashCode());
		assertEquals("hashCodes of equal objects should be equal", f5.hashCode(), copy2.hashCode());
		
		assertEquals("Length of the input to a univariate function is always 1", 1, f0.length());
		assertEquals("Length of the input to a univariate function is always 1", 1, f5.length());
	}
	
	@Test
	public void testIntegerUnivariate() {
		SingleInteger f0 = new SingleInteger();
		assertEquals("Default constructor", 0, f0.get());
		SingleInteger f5 = new SingleInteger(5);
		assertEquals("Constructor", 5, f5.get());
		assertEquals("Test get(i)", 0, f0.get(0));
		assertEquals("Test get(i)", 5, f5.get(0));
		int[] array1 = f0.toArray(null);
		assertEquals("Test toArray null param", 0, array1[0]);
		int[] array2 = f5.toArray(null);
		assertEquals("Test toArray null param", 5, array2[0]);
		int[] array3 = f5.toArray(array1);
		assertTrue("Returned array should be same array object", array1 == array3);
		assertEquals("Test toArray non-null param", 5, array3[0]);
		
		SingleInteger copy = new SingleInteger(f5);
		assertEquals("Copy Constructor", 5, copy.get());
		SingleInteger copy2 = f5.copy();
		assertEquals("Copy method", 5, copy2.get());
		assertTrue("Verify copy method returns a new object", copy2 != f5);
		assertEquals("Verify copy method returns correct runtime type", f5.getClass(), copy2.getClass());
		f0.set(10);
		assertEquals("Test the set and get methods", 10, f0.get());
		f0.set(0, 8);
		assertEquals("Test the set(i) and get(i) methods", 8, f0.get(0));
		
		assertEquals("Testing equals method", f5, copy);
		assertEquals("Testing equals method", f5, copy2);
		assertEquals("hashCodes of equal objects should be equal", f5.hashCode(), copy.hashCode());
		assertEquals("hashCodes of equal objects should be equal", f5.hashCode(), copy2.hashCode());
		
		assertEquals("Length of the input to a univariate function is always 1", 1, f0.length());
		assertEquals("Length of the input to a univariate function is always 1", 1, f5.length());
	}
	
	@Test
	public void testMultivariate() {
		for (int n = 0; n <= 10; n++) {
			RealVector f = new RealVector(n);
			assertEquals("length should be the number of values", n, f.length());
			double[] array = f.toArray(null);
			assertEquals("length of toArray should be the number of values", n, array.length);
			for (int i = 0; i < n; i++) {
				assertEquals("All values should be 0.0", 0.0, f.get(i), EPSILON);
				assertEquals("All values toArray should be 0.0", 0.0, array[i], EPSILON);
			}
			double[] initial = new double[n];
			for (int i = 0; i < n; i++) {
				initial[i] = n - i;
			}
			RealVector f2 = new RealVector(initial);
			array = f2.toArray(null);
			double[] array2 = new double[n];
			double[] array3 = f2.toArray(array2);
			assertTrue("toArray should return the same array", array2 == array3);
			RealVector f3 = new RealVector(initial);
			assertEquals("length should be the number of values", n, f2.length());
			assertEquals("length toArray should be the number of values", n, array.length);
			assertEquals("Testing equals: same", f2, f3);
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), f3.hashCode());
			if (n > 1) assertNotEquals("Testing equals: different", f2, f);
			for (int i = 0; i < n; i++) {
				f.set(i, (double)(n-i));
				assertEquals("Testing constructor 2 and get", (double)(n-i), f2.get(i), EPSILON);
				assertEquals("Testing toArray", (double)(n-i), array[i], EPSILON);
				assertEquals("Testing toArray", (double)(n-i), array3[i], EPSILON);
				assertEquals("Testing set and get", (double)(n-i), f.get(i), EPSILON);
				f3.set(i, 100.0);
				assertNotEquals("Testing equals: different after set", f2, f3);
			}
			assertEquals("Testing equals: same after set", f2, f);
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), f.hashCode());
			RealVector copy = new RealVector(f2);
			RealVector copy2 = f2.copy();
			assertEquals("Verify copy method returns correct runtime type", f2.getClass(), copy2.getClass());
			assertEquals("Testing copy constructor", f2, copy);
			assertEquals("Testing copy method", f2, copy2);
			assertTrue("Testing copy method produces new object", f2 != copy2);
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), copy.hashCode());
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), copy2.hashCode());
		}
	}
	
	@Test
	public void testIntegerMultivariate() {
		for (int n = 0; n <= 10; n++) {
			IntegerVector f = new IntegerVector(n);
			assertEquals("length should be the number of values", n, f.length());
			int[] array = f.toArray(null);
			assertEquals("length of toArray should be the number of values", n, array.length);
			for (int i = 0; i < n; i++) {
				assertEquals("All values should be 0", 0, f.get(i));
				assertEquals("All values toArray should be 0", 0, array[i]);
			}
			int[] initial = new int[n];
			for (int i = 0; i < n; i++) {
				initial[i] = n - i;
			}
			IntegerVector f2 = new IntegerVector(initial);
			array = f2.toArray(null);
			int[] array2 = new int[n];
			int[] array3 = f2.toArray(array2);
			assertTrue("toArray should return the same array", array2 == array3);
			IntegerVector f3 = new IntegerVector(initial);
			assertEquals("length should be the number of values", n, f2.length());
			assertEquals("length toArray should be the number of values", n, array.length);
			assertEquals("Testing equals: same", f2, f3);
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), f3.hashCode());
			if (n > 1) assertNotEquals("Testing equals: different", f2, f);
			for (int i = 0; i < n; i++) {
				f.set(i, n-i);
				assertEquals("Testing constructor 2 and get", n-i, f2.get(i));
				assertEquals("Testing toArray", (n-i), array[i]);
				assertEquals("Testing toArray", (n-i), array3[i]);
				assertEquals("Testing set and get", n-i, f.get(i));
				f3.set(i, 100);
				assertNotEquals("Testing equals: different after set", f2, f3);
			}
			assertEquals("Testing equals: same after set", f2, f);
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), f.hashCode());
			IntegerVector copy = new IntegerVector(f2);
			IntegerVector copy2 = f2.copy();
			assertEquals("Verify copy method returns correct runtime type", f2.getClass(), copy2.getClass());
			assertEquals("Testing copy constructor", f2, copy);
			assertEquals("Testing copy method", f2, copy2);
			assertTrue("Testing copy method produces new object", f2 != copy2);
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), copy.hashCode());
			assertEquals("hashCodes of equal objects should be equal", f2.hashCode(), copy2.hashCode());
		}
	}
}