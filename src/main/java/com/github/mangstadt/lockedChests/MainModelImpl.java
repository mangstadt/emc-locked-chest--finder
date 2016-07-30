package com.github.mangstadt.lockedChests;

import java.io.IOException;

import com.github.mangstadt.emc.net.InvalidCredentialsException;
import com.github.mangstadt.emc.rupees.RupeeTransactionReader;
import com.github.mangstadt.emc.rupees.dto.LockTransaction;
import com.github.mangstadt.emc.rupees.dto.RupeeTransaction;

/**
 * @author Michael Angstadt
 */
public class MainModelImpl implements MainModel {
	private Thread thread;

	@Override
	public synchronized void start(String username, char[] password, int startAtPage, DownloadListener listener) {
		thread = new Thread() {
			private int curPage = -1;

			@Override
			public void run() {
				try (RupeeTransactionReader reader = new RupeeTransactionReader.Builder(username, new String(password)).start(startAtPage).build()) {
					RupeeTransaction transaction;
					while ((transaction = reader.next()) != null) {
						if (Thread.interrupted()) {
							break;
						}

						int page = reader.getCurrentPageNumber();
						if (page != curPage) {
							curPage = page;
							listener.onParsingPage(page);
						}

						if (transaction instanceof LockTransaction) {
							listener.onChestFound((LockTransaction) transaction);
						}
					}
					listener.onDone();
				} catch (IOException e) {
					listener.onError(e.getMessage());
				} catch (InvalidCredentialsException e) {
					listener.onError("Invalid login credentials.");
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public synchronized void stop() {
		if (thread != null) {
			thread.interrupt();
		}
	}
}
