package com.github.mangstadt.lockedChests;

/**
 * @author Michael Angstadt
 */
public interface MainModel {
	/**
	 * Starts a new download operation (spawns in a new thread).
	 * @param username the player's username
	 * @param password the player's password
	 * @param startAtPage the rupee transaction history page to start on
	 * @param listener responds to events
	 */
	void start(String username, char[] password, int startAtPage, DownloadListener listener);

	/**
	 * Stops the currently running download operation, if one is running.
	 */
	void stop();
}
