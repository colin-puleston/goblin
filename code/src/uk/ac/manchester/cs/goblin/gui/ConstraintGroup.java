/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.goblin.gui;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConstraintGroup {

	private ConstraintType type;

	private Set<Concept> validValuesLinkedConcepts = new HashSet<Concept>();
	private Set<Concept> impliedValueLinkedConcepts = new HashSet<Concept>();

	public boolean equals(Object other) {

		return other instanceof ConstraintGroup && equalsGroup((ConstraintGroup)other);
	}

	public int hashCode() {

		return type.hashCode()
				+ validValuesLinkedConcepts.hashCode()
				+ impliedValueLinkedConcepts.hashCode();
	}

	ConstraintGroup(ConstraintType type, Set<Constraint> constraints) {

		this.type = type;

		for (Constraint constraint : constraints) {

			addLinkedConcepts(constraint);
		}
	}

	abstract boolean inwardGroup();

	boolean anyConstraints() {

		return !validValuesLinkedConcepts.isEmpty()
				|| !impliedValueLinkedConcepts.isEmpty();
	}

	String getTypeName() {

		return type.getName();
	}

	Set<Concept> getValidValuesLinkedConcepts() {

		return validValuesLinkedConcepts;
	}

	Set<Concept> getImpliedValueLinkedConcepts() {

		return impliedValueLinkedConcepts;
	}

	abstract void addLinkedConcepts(Constraint constraint, Set<Concept> linkedConcepts);

	private void addLinkedConcepts(Constraint constraint) {

		addLinkedConcepts(constraint, getLinkedConcepts(constraint.getSemantics()));
	}

	private Set<Concept> getLinkedConcepts(ConstraintSemantics semantics) {

		return semantics.validValues()
				? validValuesLinkedConcepts
				: impliedValueLinkedConcepts;
	}

	private boolean equalsGroup(ConstraintGroup other) {

		return type.equals(other.type)
				&& validValuesLinkedConcepts.equals(other.validValuesLinkedConcepts)
				&& impliedValueLinkedConcepts.equals(other.impliedValueLinkedConcepts);
	}
}
