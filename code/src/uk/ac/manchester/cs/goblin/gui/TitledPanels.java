/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class TitledPanels {

	static JPanel create(JComponent content, String title) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(content, BorderLayout.CENTER);

		return setTitle(panel, title);
	}

	static <P extends JPanel>P setTitle(P panel, String title) {

		panel.setBorder(createBorder(title));

		return panel;
	}

	static private TitledBorder createBorder(String title) {

		TitledBorder border = new TitledBorder(title);
		Font font = border.getTitleFont();

		if (font != null) {

			border.setTitleFont(GFonts.toMedium(font));
		}

		return border;
	}
}
