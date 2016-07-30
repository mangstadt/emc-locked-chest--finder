package com.github.mangstadt.lockedChests;

import com.github.mangstadt.emc.rupees.dto.LockTransaction;

/**
 * @author Michael Angstadt
 */
public interface MainView {
	/**
	 * Sets the code to run when the user starts a download.
	 * @param runnable
	 */
	void setOnStart(Runnable runnable);

	/**
	 * Sets the code to run when the user cancels a download.
	 * @param runnable
	 */
	void setOnCancel(Runnable runnable);

	/**
	 * Sets the code to run when the user quits the program.
	 * @param runnable
	 */
	void setOnQuit(Runnable runnable);

	/**
	 * Gets the player's username.
	 * @return the username
	 */
	String getUsername();

	/**
	 * Gets the player's password.
	 * @return the password
	 */
	char[] getPassword();

	/**
	 * Gets the page the player wants to start downloading at.
	 * @return the page number
	 */
	int getStartAtPage();

	/**
	 * Sets the error that occurred while downloading.
	 * @param message the error message
	 */
	void setError(String message);

	/**
	 * Updates the status of the current download.
	 * @param page the page number the downloader is currently on
	 */
	void setStatus(int page);

	/**
	 * Adds a chest locking transaction to the output.
	 * @param transaction the transaction
	 */
	void addLockTransaction(LockTransaction transaction);

	/**
	 * Called when the downloader has reached the end of the player's rupee
	 * transaction history.
	 */
	void onDone();

	/**
	 * Displays the window.
	 */
	void display();

	/**
	 * Closes the window.
	 */
	void close();
}
