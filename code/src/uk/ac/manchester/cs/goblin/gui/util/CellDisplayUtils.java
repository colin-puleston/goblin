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

package uk.ac.manchester.cs.goblin.gui.util;

import java.awt.Color;

import uk.ac.manchester.cs.mekon_util.gui.icon.*;

/**
 * @author Colin Puleston
 */
public class CellDisplayUtils {

	static private final int LARGE_ICON_SIZE = 12;
	static private final int MEDIUM_ICON_SIZE = 8;
	static private final int SMALL_ICON_SIZE = 6;

	static public GIconRenderer largeCircle(Color clr) {

		return new GOvalRenderer(clr, LARGE_ICON_SIZE);
	}

	static public GIconRenderer mediumCircleCentred(Color clr) {

		return reducedCircleCentred(clr, MEDIUM_ICON_SIZE);
	}

	static public GIconRenderer smallCircleCentred(Color clr) {

		return reducedCircleCentred(clr, SMALL_ICON_SIZE);
	}

	static public GIconRenderer mediumCircleMediumXShifted(Color clr) {

		return offsetCircle(clr, MEDIUM_ICON_SIZE, MEDIUM_ICON_SIZE, 0);
	}

	static public GIconRenderer largeRightwardTriangle(Color clr) {

		return new GTriangleRenderer(
						GTriangleRenderer.Type.RIGHTWARD,
						clr,
						LARGE_ICON_SIZE,
						LARGE_ICON_SIZE);
	}

	static public GIconRenderer mediumRightwardTriangle(Color clr) {

		return new GTriangleRenderer(
						GTriangleRenderer.Type.RIGHTWARD,
						clr,
						MEDIUM_ICON_SIZE,
						MEDIUM_ICON_SIZE);
	}

	static private GIconRenderer reducedCircleCentred(Color clr, int size) {

		int offset = (LARGE_ICON_SIZE - size) / 2;

		return offsetCircle(clr, size, offset, offset);
	}

	static private GIconRenderer offsetCircle(Color clr, int size, int xOffset, int yOffset) {

		GIconRenderer r = new GOvalRenderer(clr, size);

		r.setXOffset(xOffset);
		r.setYOffset(yOffset);

		return r;
	}
}
