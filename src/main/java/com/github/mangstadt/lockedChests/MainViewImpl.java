package com.github.mangstadt.lockedChests;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.github.mangstadt.emc.rupees.dto.LockTransaction;

import net.miginfocom.swing.MigLayout;

/**
 * @author Michael Angstadt
 */
@SuppressWarnings("serial")
public class MainViewImpl extends JFrame implements MainView {
	private JButton ok, cancel;
	private JTextField username, startAtPage;
	private JPasswordField password;
	private JLabel message;
	private JTextArea transactions;

	private Runnable onStart, onCancel, onQuit;

	private boolean downloading = false;

	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public MainViewImpl() {
		setTitle("EMC Locked Chest Finder by shavingfoam");

		message = new JLabel();
		username = new JTextField();
		password = new JPasswordField();
		startAtPage = new JTextField("1");
		transactions = new JTextArea();
		transactions.setEditable(false);
		Font font = transactions.getFont();
		if (font != null) {
			transactions.setFont(new Font(Font.MONOSPACED, font.getStyle(), font.getSize()));
		}

		cancel = new JButton("Quit");
		cancel.addActionListener((event) -> {
			if (downloading) {
				onCancel.run();

				message.setText("Stopped.");
				setState(false);
			} else {
				onQuit.run();
			}
		});
		GuiUtils.onEscapeKeyPress(this, cancel);

		ok = new JButton("Start");
		ok.addActionListener((event) -> {
			message.setText("Logging in...");

			setState(true);
			transactions.setText("");

			onStart.run();
		});
		GuiUtils.onKeyPress(this, KeyEvent.VK_ENTER, ok);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				onQuit.run();
			}
		});

		///////////////////////

		setLayout(new MigLayout());

		add(new JLabel("Username:"));
		add(username, "w 100%, wrap");
		add(new JLabel("Password:"));
		add(password, "w 100%, wrap");
		add(new JLabel("Start at page:"));
		add(startAtPage, "w 50, wrap");
		add(message, "align center, span 2, wrap");

		add(new JScrollPane(transactions), "align center, span 2, w 100%, h 100%, wrap");

		add(ok, "split 2, span 2, align center");
		add(cancel);

		setSize(400, 300);
	}

	@Override
	public void setOnStart(Runnable listener) {
		onStart = listener;
	}

	@Override
	public void setOnCancel(Runnable listener) {
		onCancel = listener;
	}

	@Override
	public void setOnQuit(Runnable listener) {
		onQuit = listener;
	}

	@Override
	public String getUsername() {
		return username.getText();
	}

	@Override
	public char[] getPassword() {
		return password.getPassword();
	}

	@Override
	public int getStartAtPage() {
		try {
			return Integer.parseInt(startAtPage.getText());
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	@Override
	public void setError(String message) {
		this.message.setText("<html><font color=red>" + message);
		setState(false);
		GuiUtils.shake(this);
	}

	@Override
	public void setStatus(int page) {
		message.setText("Parsing page " + page);
	}

	@Override
	public void addLockTransaction(LockTransaction transaction) {
		transactions.append(df.format(transaction.getTs()) + " - " + transaction.getDescription() + System.lineSeparator());
	}

	@Override
	public void onDone() {
		message.setText("Done.");
		setState(false);
	}

	@Override
	public void display() {
		setVisible(true);
	}

	@Override
	public void close() {
		dispose();
	}

	private void setState(boolean downloading) {
		cancel.setText(downloading ? "Stop" : "Quit");
		ok.setEnabled(!downloading);
		username.setEnabled(!downloading);
		password.setEnabled(!downloading);
		startAtPage.setEnabled(!downloading);
		this.downloading = downloading;
	}
}
