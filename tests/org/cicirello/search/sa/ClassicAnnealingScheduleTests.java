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
 
package org.cicirello.search.sa;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.math.rand.RandomIndexer;

/**
 * JUnit 4 test cases for the annealing schedules.
 */
public class ClassicAnnealingScheduleTests {
	
	private final static double EPSILON = 1e-10;
	
	@Test
	public void testExponentialCooling() {
		// Note that 0.5 would be an unusually fast rate of cooling (unusually low alpha),
		// but used here for testing for an easily determined sequence of expected and correct
		// temperature values (especially in common with the power of 2 used for the initial 
		// temperature in this test case).
		double[] expectedT = {4.0, 2.0, 1.0, 0.5, 0.25, 0.125, 0.0625, 0.03125, 0.015625, 0.0078125, 0.00390625, 0.001953125, 0.0009765625};
		int evals = expectedT.length + 5;
		ExponentialCooling c = new ExponentialCooling(4.0, 0.5);
		c.init(evals);
		for (int i = 0; i < evals; i++) {
			assertEquals("checking for correct temperature cooling", i < expectedT.length ? expectedT[i] : expectedT[expectedT.length-1], c.getTemperature(), EPSILON);
			c.accept(2, 5);
		}	
		// repeat to make sure init resets correctly
		c.init(evals);
		for (int i = 0; i < evals; i++) {
			assertEquals("checking for correct temperature cooling", i < expectedT.length ? expectedT[i] : expectedT[expectedT.length-1], c.getTemperature(), EPSILON);
			c.accept(2, 5);
		}	
		// verify accepting correctly
		c = new ExponentialCooling(100.0, 0.95);
		c.init(100);
		for (int i = 0; i < 10; i++) {
			assertTrue("should always accept lower or same cost neighbors", c.accept(i, 9));
		}
		// verify accept will reject by passing infinite cost. 
		for (int i = 0; i < 10; i++) {
			assertFalse("should reject if neighbor cost is significantly above current cost", c.accept(Double.POSITIVE_INFINITY, 9));
		}
		// verify accept both accepts some higher cost neighbors and rejects other higher cost neighbors.
		final int RUN_LENGTH = 1000;
		c.init(RUN_LENGTH);
		int count = 0;
		for (int i = 0; i < RUN_LENGTH; i++) {
			if (c.accept(10001 + RandomIndexer.nextInt(5), 10000)) count++;
		}
		assertTrue("Verify accepts some higher cost neighbors", count > 0);
		assertTrue("Verify rejects some higher cost neighbors", count < RUN_LENGTH);
		// Now test a step size other than 1.
		c = new ExponentialCooling(4.0, 0.5, 3);
		c.init(evals);
		for (int i = 0; i < evals; i++) {
			for (int j = 0; j < 3; j++) {
				assertEquals("checking for correct temperature cooling", i < expectedT.length ? expectedT[i] : expectedT[expectedT.length-1], c.getTemperature(), EPSILON);
				c.accept(2, 5);
			}
		}
	}
	
	@Test
	public void testLinearCooling() {
		// 1 would be an unusually high deltaT, but using it here for easily determined
		// expected temperatures for testing
		LinearCooling c = new LinearCooling(9.0001, 1.0);
		c.init(15);
		double expected = 9.0001;
		for (int i = 0; i < 15; i++) {
			assertEquals("checking for correct temperature cooling", expected, c.getTemperature(), EPSILON);
			c.accept(2, 5);
			if (expected > 0.001) expected -= 1.0;
			if (expected < 0.001) expected = 0.001;
		}	
		// repeat to make sure init resets correctly
		c.init(15);
		expected = 9.0001;
		for (int i = 0; i < 15; i++) {
			assertEquals("checking for correct temperature cooling", expected, c.getTemperature(), EPSILON);
			c.accept(2, 5);
			if (expected > 0.001) expected -= 1.0;
			if (expected < 0.001) expected = 0.001;
		}	
		// verify accepting correctly
		c = new LinearCooling(2.0, 0.01);
		c.init(100);
		for (int i = 0; i < 10; i++) {
			assertTrue("should always accept lower or same cost neighbors", c.accept(i, 9));
		}
		// verify accept will reject by passing infinite cost. 
		for (int i = 0; i < 10; i++) {
			assertFalse("should reject if neighbor cost is significantly above current cost", c.accept(Double.POSITIVE_INFINITY, 9));
		}
		// verify accept both accepts some higher cost neighbors and rejects other higher cost neighbors.
		final int RUN_LENGTH = 1000;
		c.init(RUN_LENGTH);
		int count = 0;
		for (int i = 0; i < RUN_LENGTH; i++) {
			if (c.accept(10001 + RandomIndexer.nextInt(5), 10000)) count++;
		}
		assertTrue("Verify accepts some higher cost neighbors", count > 0);
		assertTrue("Verify rejects some higher cost neighbors", count < RUN_LENGTH);
		// Now test a step size other than 1.
		c = new LinearCooling(9.0001, 1.0, 3);
		c.init(15);
		expected = 9.0001;
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 3; j++) {
				assertEquals("checking for correct temperature cooling", expected, c.getTemperature(), EPSILON);
				c.accept(2, 5);
			}
			if (expected > 0.001) expected -= 1.0;
			if (expected < 0.001) expected = 0.001;
		}
	}
	
	@Test
	public void testLogarithmicCooling() {
		double t0 = 10;
		LogarithmicCooling c = new LogarithmicCooling(t0);
		c.init(100);
		for (int i = 0; i < 10; i++) {
			double expected = t0 / StrictMath.log(StrictMath.E + i);
			assertEquals("checking for correct temperature cooling", expected, c.getTemperature(), EPSILON);
			c.accept(2, 5);
		}
		c.init(100);
		for (int i = 0; i < 10; i++) {
			double expected = t0 / StrictMath.log(StrictMath.E + i);
			assertEquals("checking for correct temperature cooling", expected, c.getTemperature(), EPSILON);
			c.accept(2, 5);
		}
		t0 = 100;
		c = new LogarithmicCooling(t0);
		c.init(100);
		for (int i = 0; i < 10; i++) {
			double expected = t0 / StrictMath.log(StrictMath.E + i);
			assertEquals("checking for correct temperature cooling", expected, c.getTemperature(), EPSILON);
			c.accept(2, 5);
		}
		c.init(100);
		for (int i = 0; i < 10; i++) {
			double expected = t0 / StrictMath.log(StrictMath.E + i);
			assertEquals("checking for correct temperature cooling", expected, c.getTemperature(), EPSILON);
			c.accept(2, 5);
		}
	}
	
}