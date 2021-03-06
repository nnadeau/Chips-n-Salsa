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
 
package org.cicirello.search.ss;

import org.cicirello.util.Copyable;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.SimpleMetaheuristic;

/**
 * <p>Iterative sampling is the simplest possible form of a stochastic sampling search.
 * In iterative sampling, the search generates N random candidate solutions to the 
 * problem, each sampled uniformly at random from the space of possible solutions.
 * It evaluates each of the N candidate solutions with respect to the optimization
 * problem's cost function, and returns the best of the N candidate solutions.</p>
 *
 * <p>For an early empirical comparison of iterative sampling with systematic search
 * algorithms, see:<br>
 * P. Langley. Systematic and nonsystematic search strategies. 
 * Proceedings of the First International Conference on Artificial Intelligence
 * Planning Systems, pages 145–152, 1992.</p>
 *
 * @param <T> The type of object under optimization.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.15.2020
 */
public final class IterativeSampling<T extends Copyable<T>> implements Metaheuristic<T>, SimpleMetaheuristic<T> {
	
	private final OptimizationProblem<T> pOpt;
	private final IntegerCostOptimizationProblem<T> pOptInt;
	private final Initializer<T> initializer;
	private ProgressTracker<T> tracker;
	private long totalNumSamples;
	private final OneSample<T> sampler;
	
	/**
	 * Constructs an iterative sampling search for a real-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(OptimizationProblem<T> problem, Initializer<T> initializer, ProgressTracker<T> tracker) {
		if (problem == null || initializer == null || tracker == null) {
			throw new NullPointerException();
		}
		pOpt = problem;
		pOptInt = null;
		this.initializer = initializer;
		this.tracker = tracker;
		// Deliberately using default: totalNumSamples = 0;
		sampler = initSamplerDouble();
	}
	
	/**
	 * Constructs an iterative sampling search for a integer-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(IntegerCostOptimizationProblem<T> problem, Initializer<T> initializer, ProgressTracker<T> tracker) {
		if (problem == null || initializer == null || tracker == null) {
			throw new NullPointerException();
		}
		pOptInt = problem;
		pOpt = null;
		this.initializer = initializer;
		this.tracker = tracker;
		// Deliberately using default: totalNumSamples = 0;
		sampler = initSamplerInt();
	}
	
	/**
	 * Constructs an iterative sampling search for a real-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(OptimizationProblem<T> problem, Initializer<T> initializer) {
		this(problem, initializer, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs an iterative sampling search for a integer-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(IntegerCostOptimizationProblem<T> problem, Initializer<T> initializer) {
		this(problem, initializer, new ProgressTracker<T>());
	}
	
	/*
	 * private copy constructor in support of the split method.
	 * note: copies references to thread-safe components, and splits potentially non-threadsafe components 
	 */
	private IterativeSampling(IterativeSampling<T> other) {
		// these are threadsafe, so just copy references
		pOpt = other.pOpt;
		pOptInt = other.pOptInt;
		
		// this one must be shared so just copy reference.
		tracker = other.tracker;
		
		// split: might not be threadsafe
		initializer = other.initializer.split();

		// initialize fresh: NOT threadsafe
		sampler = pOptInt != null ? initSamplerInt() : initSamplerDouble();
		
		// use default of 0 for this one: totalNumSamples
	}
	
	
	@Override
	public SolutionCostPair<T> optimize() {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		return sampler.oneSample();
	}
	
	/**
	 * <p>Generates multiple samples using Iterative Sampling.
	 * Returns the best solution of the set of samples.</p>
	 *
	 * @param numSamples The number of samples of Iterative Sampling to perform.
	 * @return The best solution of this set of samples, which may or may not be the 
	 * same as the solution contained
	 * in this search's {@link org.cicirello.search.ProgressTracker ProgressTracker}, 
	 * which contains the best of all runs
	 * across all calls to the various optimize methods.
	 * Returns null if no runs executed, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 */
	@Override
	public SolutionCostPair<T> optimize(int numSamples) {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		SolutionCostPair<T> best = null;
		for (int i = 0; i < numSamples && !tracker.didFindBest() && !tracker.isStopped(); i++) {
			SolutionCostPair<T> current = sampler.oneSample();
			if (best == null || current.compareTo(best) < 0) best = current;
		}
		return best;
	}
	
	/**
	 * <p>Gets the total run length, where run length is number of samples
	 * generated.  This is the total run length
	 * across all calls to the search.</p>
	 *
	 * @return the total number of solutions sampled by the search, across
	 * all calls to the various optimize methods.
	 */
	@Override
	public long getTotalRunLength() {
		return totalNumSamples;
	}
	
	@Override
	public ProgressTracker<T> getProgressTracker() {
		return tracker;
	}
	
	@Override
	public void setProgressTracker(ProgressTracker<T> tracker) {
		if (tracker != null) this.tracker = tracker;
	}
	
	@Override
	public Problem<T> getProblem() {
		return (pOptInt != null) ? pOptInt : pOpt;
	}
	
	@Override
	public IterativeSampling<T> split() {
		return new IterativeSampling<T>(this);
	}
	
	private interface OneSample<T extends Copyable<T>> {
		SolutionCostPair<T> oneSample();
	}
	
	private OneSample<T> initSamplerDouble() {
		return new OneSample<T>() {
			public SolutionCostPair<T> oneSample() {
				T s = initializer.createCandidateSolution();
				totalNumSamples++;
				double cost = pOpt.cost(s);
				// update tracker
				if (cost < tracker.getCostDouble()) {
					tracker.update(cost, s);
					if (cost == pOpt.minCost()) {
						tracker.setFoundBest();
					}
				}
				return new SolutionCostPair<T>(s, cost);
			}
		};
	}
	
	private OneSample<T> initSamplerInt() {
		return new OneSample<T>() {
			public SolutionCostPair<T> oneSample() {
				T s = initializer.createCandidateSolution();
				totalNumSamples++;
				int cost = pOptInt.cost(s);
				// update tracker
				if (cost < tracker.getCost()) {
					tracker.update(cost, s);
					if (cost == pOptInt.minCost()) {
						tracker.setFoundBest();
					}
				}
				return new SolutionCostPair<T>(s, cost);
			}
		};
	}
	
}