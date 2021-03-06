package com.github.mangstadt.lockedChests;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Contains GUI utility methods.
 * @author Michael Angstadt
 */
public class GuiUtils {
	/**
	 * True if the local operating system is Linux, false if not.
	 */
	public static final boolean linux = System.getProperty("os.name").toLowerCase().contains("linux");

	//this class should not be created in a static init block because it causes AWT to initialize when the CLI is run
	private static Desktop desktop;
	private static boolean desktopCreated = false;

	/**
	 * Opens a webpage in the user's browser.
	 * @param uri the URI
	 * @throws IOException if there's a problem opening the web page
	 */
	public static void openWebPage(URI uri) throws IOException {
		if (!canOpenWebPages()) {
			return;
		}

		desktop.browse(uri);
	}

	/**
	 * Determines if the user's computer supports opening webpages.
	 * @return true if it can open web pages, false if not
	 */
	public static boolean canOpenWebPages() {
		if (!desktopCreated) {
			if (Desktop.isDesktopSupported()) {
				desktop = Desktop.getDesktop();
				if (desktop != null && !desktop.isSupported(Desktop.Action.BROWSE)) {
					desktop = null;
				}
			}
			desktopCreated = true;
		}

		return desktop != null;
	}

	/**
	 * Builds a standardized tooltip string.
	 * @param text the tooltip text
	 * @return the standardized tooltip string
	 */
	public static String toolTipText(String text) {
		text = text.replace("\n", "<br>");
		return "<html><div width=300>" + text + "</div></html>";
	}

	/**
	 * Configures a dialog to close when the escape key is pressed.
	 * @param dialog the dialog
	 */
	public static void closeOnEscapeKeyPress(final JDialog dialog) {
		onEscapeKeyPress(dialog, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
	}

	/**
	 * Adds a listener to a dialog, which will fire when the escape key is
	 * pressed.
	 * @param dialog the dialog
	 * @param listener the listener
	 */
	public static void onEscapeKeyPress(RootPaneContainer dialog, ActionListener listener) {
		onKeyPress(dialog, KeyEvent.VK_ESCAPE, listener);
	}

	/**
	 * Adds all the listeners of a button to a dialog, which will fire when the
	 * escape key is pressed.
	 * @param dialog the dialog
	 * @param button the button
	 */
	public static void onEscapeKeyPress(RootPaneContainer dialog, final AbstractButton button) {
		onKeyPress(dialog, KeyEvent.VK_ESCAPE, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ActionListener listener : button.getActionListeners()) {
					listener.actionPerformed(e);
				}
			}
		});
	}

	/**
	 * Adds a listener to a dialog which is fired when the dialog window is
	 * closed.
	 * @param dialog the dialog
	 * @param listener the listener
	 */
	public static void addCloseDialogListener(Window dialog, final ActionListener listener) {
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				listener.actionPerformed(null);
			}
		});
	}

	/**
	 * Adds a listener to a dialog, which will fire when a key is pressed.
	 * @param frame the frame
	 * @param key the key (see constants in {@link KeyEvent})
	 * @param listener the listener
	 */
	public static void onKeyPress(RootPaneContainer frame, int key, ActionListener listener) {
		frame.getRootPane().registerKeyboardAction(listener, KeyStroke.getKeyStroke(key, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Adds all the listeners of a button to a dialog, which will fire when a
	 * key is pressed.
	 * @param dialog the dialog
	 * @param key the key (see constants in {@link KeyEvent})
	 * @param button the button
	 */
	public static void onKeyPress(RootPaneContainer dialog, int key, final AbstractButton button) {
		dialog.getRootPane().registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ActionListener listener : button.getActionListeners()) {
					listener.actionPerformed(e);
				}
			}
		}, KeyStroke.getKeyStroke(key, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Used for changing the cursor to a busy cursor.
	 * @param window the window
	 * @param busy true for a busy cursor, false for the default cursor
	 */
	public static void busyCursor(Window window, boolean busy) {
		Cursor cursor = busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
		window.setCursor(cursor);
	}

	/**
	 * Fires a list of events.
	 * @param listeners the events to fire
	 */
	public static void fireEvents(List<ActionListener> listeners) {
		fireEvents(listeners, null);
	}

	/**
	 * Fires a list of events.
	 * @param listeners the events to fire
	 * @param event the event to fire
	 */
	public static void fireEvents(List<ActionListener> listeners, ActionEvent event) {
		for (ActionListener listener : listeners) {
			listener.actionPerformed(event);
		}
	}

	/**
	 * Fires a list of events using {@link SwingUtilities#invokeLater}.
	 * @param listeners the events to fire
	 */
	public static void fireEventsLater(final List<ActionListener> listeners) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fireEvents(listeners);
			}
		});
	}

	/**
	 * Copies text to the clipboard.
	 * @param text the text to copy to the clipboard
	 */
	public static void copyToClipboard(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(text);
		clipboard.setContents(stringSelection, stringSelection);
	}

	/**
	 * "Shakes" a dialog box, like Mac OSX does when you enter a wrong password.
	 * @param window the window to shake
	 */
	public static void shake(final Window window) {
		final int SHAKE_DISTANCE = 20;
		final double SHAKE_CYCLE = 50;
		final int SHAKE_DURATION = 200;
		final int SHAKE_UPDATE = 5;
		final double TWO_PI = Math.PI * 2.0;

		final Point naturalLocation = window.getLocation();
		final long startTime = System.currentTimeMillis();

		final Timer shakeTimer = new Timer(SHAKE_UPDATE, null);
		shakeTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// calculate elapsed time
				long elapsed = System.currentTimeMillis() - startTime;
				// use sin to calculate an x-offset
				double waveOffset = (elapsed % SHAKE_CYCLE) / SHAKE_CYCLE;
				double angle = waveOffset * TWO_PI;

				// offset the x-location by an amount 
				// proportional to the sine, up to
				// shake_distance
				int shakenX = (int) ((Math.sin(angle) * SHAKE_DISTANCE) + naturalLocation.x);
				window.setLocation(shakenX, naturalLocation.y);
				window.repaint();

				// should we stop timer?
				if (elapsed >= SHAKE_DURATION) {
					shakeTimer.stop();
					window.setLocation(naturalLocation);
					window.repaint();
				}
			}
		});
		shakeTimer.start();
	}

	/**
	 * Shrinks the font of a component.
	 * @param component the component
	 */
	public static void shrinkFont(Component component) {
		Font font = component.getFont();
		if (font == null) {
			return;
		}

		Font smaller = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
		component.setFont(smaller);
	}

	/**
	 * Adds bold styling to the font of a component.
	 * @param component the component
	 */
	public static void boldFont(Component component) {
		Font font = component.getFont();
		if (font == null) {
			return;
		}

		Font bold = new Font(font.getName(), Font.BOLD, font.getSize());
		component.setFont(bold);
	}

	private GuiUtils() {
		//hide
	}
}
