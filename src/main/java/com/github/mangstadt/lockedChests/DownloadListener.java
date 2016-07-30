package com.github.mangstadt.lockedChests;

import com.github.mangstadt.emc.rupees.dto.LockTransaction;

/**
 * @author Michael Angstadt
 * @see MainModel#start
 */
public interface DownloadListener {
	/**
	 * Called when an error occurs, causing the download operation to
	 * terminate.
	 * @param message the error message
	 */
	void onError(String message);

	/**
	 * Called when a lock chest transaction is found.
	 * @param transaction the transaction
	 */
	void onChestFound(LockTransaction transaction);

	/**
	 * Called when the downloader starts parsing a rupee transaction history
	 * page.
	 * @param page the page number
	 */
	void onParsingPage(int page);

	/**
	 * Called when the downloader reaches the end of the rupee transaction
	 * history.
	 */
	void onDone();
}
