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
 
package org.cicirello.search.concurrent;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SplittableRandom;


/**
 * JUnit 4 tests for TimedParallelMultistarter.
 */
public class TimedParallelMultistarterTests {
	
	@Test
	public void testTimedParallelMultistarterOne() {
		int numThreads = 1;
		ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(numThreads);
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestProblem problem = new TestProblem();
		for (int i = 1; i <= numThreads; i++) {
			searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
		}		
		TimedParallelMultistarter<TestObject> tpm = new TimedParallelMultistarter<TestObject>(searches, 1000);
		assertEquals(1000, tpm.getTimeUnit());
		tpm.setTimeUnit(10);
		assertEquals(10, tpm.getTimeUnit());
		assertTrue(tracker == tpm.getProgressTracker());
		assertTrue(problem == tpm.getProblem());
		assertEquals(0, tpm.getTotalRunLength());
		assertNull(tpm.getSearchHistory());
		long time1 = System.nanoTime();
		SolutionCostPair<TestObject> solution = tpm.optimize(8);
		long time2 = System.nanoTime();
		int combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			assertTrue(search.totalRunLength >= (search.optimizeCalled-1)*1001);
			assertTrue(search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
		assertEquals(8, history.size());
		for (int i = 1; i < history.size(); i++) {
			assertTrue(history.get(i).getCostDouble() <= history.get(i-1).getCostDouble());
			assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
			TestObject s = history.get(i).getSolution();
			if (s != null) {
				assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
			}
		}
		assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
		long actualRunTime = time2-time1;
		assertTrue("verifying runtime, actual="+actualRunTime+" ns, should be at least 80000000 ns", 
					actualRunTime >= 80000000);
					
		// verify can call optimize again
		solution = tpm.optimize(1);
		assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
		combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.optimizeCalled;
			assertTrue(msg, search.totalRunLength >= (search.optimizeCalled-2)*1001);
			assertTrue(msg, search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		history = tpm.getSearchHistory();
		assertEquals(1, history.size());
		assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
		TestObject s = history.get(0).getSolution();
		if (s != null) {
			assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
		}
		
		// Close the parallel multistarter
		tpm.close();
	}
	
	@Test
	public void testTimedParallelMultistarterThree() {
		int numThreads = 3;
		ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(numThreads);
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestProblem problem = new TestProblem();
		for (int i = 1; i <= numThreads; i++) {
			searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
		}		
		TimedParallelMultistarter<TestObject> tpm = new TimedParallelMultistarter<TestObject>(searches, 1000);
		assertEquals(1000, tpm.getTimeUnit());
		tpm.setTimeUnit(10);
		assertEquals(10, tpm.getTimeUnit());
		assertTrue(tracker == tpm.getProgressTracker());
		assertTrue(problem == tpm.getProblem());
		assertEquals(0, tpm.getTotalRunLength());
		assertNull(tpm.getSearchHistory());
		long time1 = System.nanoTime();
		SolutionCostPair<TestObject> solution = tpm.optimize(8);
		long time2 = System.nanoTime();
		int combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			assertTrue(search.totalRunLength >= (search.optimizeCalled-1)*1001);
			assertTrue(search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
		assertEquals(8, history.size());
		for (int i = 1; i < history.size(); i++) {
			assertTrue(history.get(i).getCostDouble() <= history.get(i-1).getCostDouble());
			assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
			TestObject s = history.get(i).getSolution();
			if (s != null) {
				assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
			}
		}
		assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
		long actualRunTime = time2-time1;
		assertTrue("verifying runtime, actual="+actualRunTime+" ns, should be at least 80000000 ns", 
					actualRunTime >= 80000000);
		
		// verify can call optimize again
		solution = tpm.optimize(1);
		assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
		combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.optimizeCalled;
			assertTrue(msg, search.totalRunLength >= (search.optimizeCalled-2)*1001);
			assertTrue(msg, search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		history = tpm.getSearchHistory();
		assertEquals(1, history.size());
		assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
		TestObject s = history.get(0).getSolution();
		if (s != null) {
			assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
		}
		
		// Close the parallel multistarter
		tpm.close();
	}
	
	@Test
	public void testTimedParallelReoptimizableMultistarterOne() {
		int numThreads = 1;
		ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(numThreads);
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestProblem problem = new TestProblem();
		for (int i = 1; i <= numThreads; i++) {
			searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
		}		
		TimedParallelReoptimizableMultistarter<TestObject> tpm = new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
		assertEquals(1000, tpm.getTimeUnit());
		tpm.setTimeUnit(10);
		assertEquals(10, tpm.getTimeUnit());
		assertTrue(tracker == tpm.getProgressTracker());
		assertTrue(problem == tpm.getProblem());
		assertEquals(0, tpm.getTotalRunLength());
		assertNull(tpm.getSearchHistory());
		long time1 = System.nanoTime();
		SolutionCostPair<TestObject> solution = tpm.optimize(8);
		long time2 = System.nanoTime();
		int combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			assertTrue(search.totalRunLength >= (search.optimizeCalled-1)*1001);
			assertTrue(search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
		assertEquals(8, history.size());
		for (int i = 1; i < history.size(); i++) {
			assertTrue(history.get(i).getCostDouble() <= history.get(i-1).getCostDouble());
			assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
			TestObject s = history.get(i).getSolution();
			if (s != null) {
				assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
			}
		}
		assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
		long actualRunTime = time2-time1;
		assertTrue("verifying runtime, actual="+actualRunTime+" ns, should be at least 80000000 ns", 
					actualRunTime >= 80000000);
					
		// verify can call optimize again
		solution = tpm.optimize(1);
		assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
		combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.optimizeCalled;
			assertTrue(msg, search.totalRunLength >= (search.optimizeCalled-2)*1001);
			assertTrue(msg, search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		history = tpm.getSearchHistory();
		assertEquals(1, history.size());
		assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
		TestObject s = history.get(0).getSolution();
		if (s != null) {
			assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
		}
		
		// Close the parallel multistarter
		tpm.close();
	}
	
	@Test
	public void testTimedParallelReoptimizableMultistarterThree() {
		int numThreads = 3;
		ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(numThreads);
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestProblem problem = new TestProblem();
		for (int i = 1; i <= numThreads; i++) {
			searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
		}		
		TimedParallelReoptimizableMultistarter<TestObject> tpm = new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
		assertEquals(1000, tpm.getTimeUnit());
		tpm.setTimeUnit(10);
		assertEquals(10, tpm.getTimeUnit());
		assertTrue(tracker == tpm.getProgressTracker());
		assertTrue(problem == tpm.getProblem());
		assertEquals(0, tpm.getTotalRunLength());
		assertNull(tpm.getSearchHistory());
		long time1 = System.nanoTime();
		SolutionCostPair<TestObject> solution = tpm.optimize(8);
		long time2 = System.nanoTime();
		int combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			assertTrue(search.totalRunLength >= (search.optimizeCalled-1)*1001);
			assertTrue(search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
		assertEquals(8, history.size());
		for (int i = 1; i < history.size(); i++) {
			assertTrue(history.get(i).getCostDouble() <= history.get(i-1).getCostDouble());
			assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
			TestObject s = history.get(i).getSolution();
			if (s != null) {
				assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
			}
		}
		assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
		long actualRunTime = time2-time1;
		assertTrue("verifying runtime, actual="+actualRunTime+" ns, should be at least 80000000 ns", 
					actualRunTime >= 80000000);
					
		// verify can call optimize again
		solution = tpm.optimize(1);
		assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
		combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.reoptimizeCalled);
			assertTrue(search.optimizeCalled > 0);
			String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.optimizeCalled;
			assertTrue(msg, search.totalRunLength >= (search.optimizeCalled-2)*1001);
			assertTrue(msg, search.totalRunLength <= search.optimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		history = tpm.getSearchHistory();
		assertEquals(1, history.size());
		assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
		TestObject s = history.get(0).getSolution();
		if (s != null) {
			assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
		}
		
		// Close the parallel multistarter
		tpm.close();
	}
	
	@Test
	public void testTimedParallelReoptimizableMultistarterOneReopt() {
		int numThreads = 1;
		ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(numThreads);
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestProblem problem = new TestProblem();
		for (int i = 1; i <= numThreads; i++) {
			searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
		}		
		TimedParallelReoptimizableMultistarter<TestObject> tpm = new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
		assertEquals(1000, tpm.getTimeUnit());
		tpm.setTimeUnit(10);
		assertEquals(10, tpm.getTimeUnit());
		assertTrue(tracker == tpm.getProgressTracker());
		assertTrue(problem == tpm.getProblem());
		assertEquals(0, tpm.getTotalRunLength());
		assertNull(tpm.getSearchHistory());
		long time1 = System.nanoTime();
		SolutionCostPair<TestObject> solution = tpm.reoptimize(8);
		long time2 = System.nanoTime();
		int combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.optimizeCalled);
			assertTrue(search.reoptimizeCalled > 0);
			assertTrue(search.totalRunLength >= (search.reoptimizeCalled-1)*1001);
			assertTrue(search.totalRunLength <= search.reoptimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
		assertEquals(8, history.size());
		for (int i = 1; i < history.size(); i++) {
			assertTrue(history.get(i).getCostDouble() <= history.get(i-1).getCostDouble());
			assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
			TestObject s = history.get(i).getSolution();
			if (s != null) {
				assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
			}
		}
		assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
		long actualRunTime = time2-time1;
		assertTrue("verifying runtime, actual="+actualRunTime+" ns, should be at least 80000000 ns", 
					actualRunTime >= 80000000);
					
		// verify can call reoptimize again
		solution = tpm.reoptimize(1);
		assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
		combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.optimizeCalled);
			assertTrue(search.reoptimizeCalled > 0);
			String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.reoptimizeCalled;
			assertTrue(msg, search.totalRunLength >= (search.reoptimizeCalled-2)*1001);
			assertTrue(msg, search.totalRunLength <= search.reoptimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		history = tpm.getSearchHistory();
		assertEquals(1, history.size());
		assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
		TestObject s = history.get(0).getSolution();
		if (s != null) {
			assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
		}
		
		// Close the parallel multistarter
		tpm.close();
	}
	
	@Test
	public void testTimedParallelReoptimizableMultistarterThreeReopt() {
		int numThreads = 3;
		ArrayList<TestRestartedMetaheuristic> searches = new ArrayList<TestRestartedMetaheuristic>(numThreads);
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestProblem problem = new TestProblem();
		for (int i = 1; i <= numThreads; i++) {
			searches.add(new TestRestartedMetaheuristic(i, tracker, problem));
		}		
		TimedParallelReoptimizableMultistarter<TestObject> tpm = new TimedParallelReoptimizableMultistarter<TestObject>(searches, 1000);
		assertEquals(1000, tpm.getTimeUnit());
		tpm.setTimeUnit(10);
		assertEquals(10, tpm.getTimeUnit());
		assertTrue(tracker == tpm.getProgressTracker());
		assertTrue(problem == tpm.getProblem());
		assertEquals(0, tpm.getTotalRunLength());
		assertNull(tpm.getSearchHistory());
		long time1 = System.nanoTime();
		SolutionCostPair<TestObject> solution = tpm.reoptimize(8);
		long time2 = System.nanoTime();
		int combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.optimizeCalled);
			assertTrue(search.reoptimizeCalled > 0);
			assertTrue(search.totalRunLength >= (search.reoptimizeCalled-1)*1001);
			assertTrue(search.totalRunLength <= search.reoptimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		ArrayList<SolutionCostPair<TestObject>> history = tpm.getSearchHistory();
		assertEquals(8, history.size());
		for (int i = 1; i < history.size(); i++) {
			assertTrue(history.get(i).getCostDouble() <= history.get(i-1).getCostDouble());
			assertTrue(history.get(i).getCostDouble() >= tracker.getCostDouble());
			TestObject s = history.get(i).getSolution();
			if (s != null) {
				assertEquals(problem.cost(s), history.get(i).getCostDouble(), 0.0);
			}
		}
		assertEquals(solution.getCostDouble(), tracker.getCostDouble(), 0.0);
		long actualRunTime = time2-time1;
		assertTrue("verifying runtime, actual="+actualRunTime+" ns, should be at least 80000000 ns", 
					actualRunTime >= 80000000);
					
		// verify can call reoptimize again
		solution = tpm.reoptimize(1);
		assertTrue(solution.getCostDouble() >= tracker.getCostDouble());
		combinedRun = 0;
		for (TestRestartedMetaheuristic search : searches) {
			assertEquals(0, search.optimizeCalled);
			assertTrue(search.reoptimizeCalled > 0);
			String msg = "trl=" + search.totalRunLength + ", #optCalled=" + search.reoptimizeCalled;
			assertTrue(msg, search.totalRunLength >= (search.reoptimizeCalled-2)*1001);
			assertTrue(msg, search.totalRunLength <= search.reoptimizeCalled*1001);
			combinedRun += search.totalRunLength;
		}
		assertEquals(combinedRun, tpm.getTotalRunLength());
		history = tpm.getSearchHistory();
		assertEquals(1, history.size());
		assertTrue(history.get(0).getCostDouble() >= tracker.getCostDouble());
		TestObject s = history.get(0).getSolution();
		if (s != null) {
			assertEquals(problem.cost(s), history.get(0).getCostDouble(), 0.0);
		}
		
		// Close the parallel multistarter
		tpm.close();
	}
	
	private static class TestRestartedMetaheuristic implements ReoptimizableMetaheuristic<TestObject> {
		
		private ProgressTracker<TestObject> tracker;
		private int id;
		public volatile int optimizeCalled;
		public volatile int reoptimizeCalled;
		private SplittableRandom r;
		private TestProblem problem;
		public volatile int totalRunLength;
		
		public TestRestartedMetaheuristic(int id, ProgressTracker<TestObject> tracker, TestProblem problem) {
			this.id = id;
			this.tracker = tracker;
			this.problem = problem;
			optimizeCalled = 0;
			reoptimizeCalled = 0;
			totalRunLength = 0;
			r = new SplittableRandom(id);
		}
		
		@Override
		public SolutionCostPair<TestObject> optimize(int runLength) {
			optimizeCalled++;
			TestObject threadBest = new TestObject(r.nextInt(10000));
			totalRunLength++;
			double bestCost = problem.cost(threadBest);
			tracker.update(bestCost, threadBest);
			while (!tracker.isStopped() && runLength > 0) {
				runLength--;
				TestObject candidate = new TestObject(r.nextInt(10000));
				totalRunLength++;
				double cost = problem.cost(candidate);
				if (cost < bestCost) {
					threadBest = candidate;
					bestCost = cost;					
					tracker.update(bestCost, threadBest);
				}
			}
			return new SolutionCostPair<TestObject>(threadBest, problem.cost(threadBest));
		}
		
		@Override
		public SolutionCostPair<TestObject> reoptimize(int runLength) {
			reoptimizeCalled++;
			TestObject threadBest = new TestObject(r.nextInt(10000));
			totalRunLength++;
			double bestCost = problem.cost(threadBest);
			tracker.update(bestCost, threadBest);
			while (!tracker.isStopped() && runLength > 0) {
				runLength--;
				TestObject candidate = new TestObject(r.nextInt(10000));
				totalRunLength++;
				double cost = problem.cost(candidate);
				if (cost < bestCost) {
					threadBest = candidate;
					bestCost = cost;					
					tracker.update(bestCost, threadBest);
				}
			}
			return new SolutionCostPair<TestObject>(threadBest, problem.cost(threadBest));
		}
		
	
		public TestRestartedMetaheuristic split() {
			return new TestRestartedMetaheuristic(10*id, tracker, problem);
		}	
		public ProgressTracker<TestObject> getProgressTracker() { return tracker; }
		public void setProgressTracker(ProgressTracker<TestObject> tracker) {
			if (tracker != null) this.tracker = tracker;
		}
		public OptimizationProblem<TestObject> getProblem() { return problem; }
		public long getTotalRunLength() { return totalRunLength; }
	}
	
	private static class TestObject implements Copyable<TestObject> {
		private int value;
		public TestObject(int value) { this.value = value; }
		public TestObject copy() { return new TestObject(value); }
		public int getValue() { return value; }
	}
	
	private static class TestProblem implements OptimizationProblem<TestObject> {
		public double cost(TestObject o) { return o.getValue(); }
		public boolean isMinCost(double c) { return false; }
		public double minCost() { return -10000; }
		public double value(TestObject o) { return o.getValue(); }
	}
	
}