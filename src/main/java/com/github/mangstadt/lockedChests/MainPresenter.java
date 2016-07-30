package com.github.mangstadt.lockedChests;

import static javax.swing.SwingUtilities.invokeLater;

import com.github.mangstadt.emc.rupees.dto.LockTransaction;

/**
 * @author Michael Angstadt
 */
public class MainPresenter {
	private final MainView view;
	private final MainModel model;

	public MainPresenter(MainView view, MainModel model) {
		this.view = view;
		this.model = model;

		view.setOnStart(() -> onStart());
		view.setOnCancel(() -> onCancel());
		view.setOnQuit(() -> onQuit());

		view.display();
	}

	private void onStart() {
		model.start(view.getUsername(), view.getPassword(), view.getStartAtPage(), new DownloadListener() {
			public void onError(String message) {
				invokeLater(() -> view.setError(message));
			}

			public void onChestFound(LockTransaction transaction) {
				invokeLater(() -> view.addLockTransaction(transaction));
			}

			public void onParsingPage(int page) {
				invokeLater(() -> view.setStatus(page));
			}

			public void onDone() {
				invokeLater(() -> view.onDone());
			}
		});
	}

	private void onCancel() {
		model.stop();
	}

	private void onQuit() {
		model.stop();
		view.close();
	}
}
