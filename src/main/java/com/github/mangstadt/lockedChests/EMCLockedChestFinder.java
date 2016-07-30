package com.github.mangstadt.lockedChests;

/**
 * @author Michael Angstadt
 */
public class EMCLockedChestFinder {
	public static void main(String[] args) throws Throwable {
		MainView view = new MainViewImpl();
		MainModel model = new MainModelImpl();
		new MainPresenter(view, model);
	}
}
