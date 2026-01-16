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
import javax.swing.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class UserConfirmations implements Confirmations {

	private abstract class ConstraintsRemovalSourceHandler {

		static private final int MAX_TARGETS_TO_DISPLAY = 3;

		private StringBuilder info = new StringBuilder();

		void initialise(List<Constraint> removals) {

			addMessageHeader();

			checkAddForSemanticsType(ConstraintSemantics.VALID_VALUES, removals);
			checkAddForSemanticsType(ConstraintSemantics.IMPLIED_VALUE, removals);

			addMessageFooter();
		}

		boolean checkContinue() {

			return InfoDisplay.checkContinue(info.toString());
		}

		abstract String describeEditProcess();

		abstract String describeEditProcessForQuery();

		private void checkAddForSemanticsType(
						ConstraintSemantics semantics,
						List<Constraint> allRemovals) {

			List<Constraint> typeRemovals = semantics.select(allRemovals);

			if (!typeRemovals.isEmpty()) {

				addSemanticsTypeHeader(semantics);

				for (Constraint removal : typeRemovals) {

					addRemoval(removal);
				}

				info.append("\n");
			}
		}

		private void addMessageHeader() {

			info.append(describeEditProcess());
			info.append(" will cause the following");
			info.append(" conflicting constraints to be removed...");
			info.append("\n\n");
		}

		private void addMessageFooter() {

			info.append(describeEditProcessForQuery());
			info.append(" and remove conflicting constraints?");
			info.append("\n\n");
		}

		private void addSemanticsTypeHeader(ConstraintSemantics semantics) {

			info.append("  ");
			info.append(semantics.getDisplayLabel());
			info.append(" constraints:");
			info.append("\n\n");
		}

		private void addRemoval(Constraint removal) {

			info.append("    ");
			info.append(removal.getSourceValue());
			info.append(" ==> ");
			addTargets(removal.getTargetValues());
			info.append('\n');
		}

		private void addTargets(Collection<Concept> targets) {

			int i = 0;

			checkAddTargetsBracket('[', targets);

			for (Concept target : targets) {

				if (i == MAX_TARGETS_TO_DISPLAY) {

					info.append("...");

					break;
				}

				if (i++ > 0) {

					info.append(", ");
				}

				info.append(target);
			}

			checkAddTargetsBracket(']', targets);
		}

		private void checkAddTargetsBracket(char c, Collection<Concept> targets) {

			if (targets.size() > 1) {

				info.append(c);
			}
		}
	}

	private class ConceptMoveHandler extends ConstraintsRemovalSourceHandler {

		private String conceptLabel;

		ConceptMoveHandler(Concept moved, List<Constraint> invalidatedConstraints) {

			conceptLabel = ("\"" + moved.getConceptId().getLabel() + "\"");

			initialise(invalidatedConstraints);
		}

		String describeEditProcess() {

			return "Moving " + conceptLabel;
		}

		String describeEditProcessForQuery() {

			return "Move " + conceptLabel;
		}
	}

	private class ConstraintAdditionHandler extends ConstraintsRemovalSourceHandler {

		ConstraintAdditionHandler(List<Constraint> conflicts) {

			initialise(conflicts);
		}

		String describeEditProcess() {

			return "Adding constraint";
		}

		String describeEditProcessForQuery() {

			return "Add constraint";
		}
	}

	public boolean confirmConceptMove(Concept moved, List<Constraint> invalidatedConstraints) {

		return new ConceptMoveHandler(moved, invalidatedConstraints).checkContinue();
	}

	public boolean confirmConstraintAddition(List<Constraint> conflicts) {

		return new ConstraintAdditionHandler(conflicts).checkContinue();
	}
}
