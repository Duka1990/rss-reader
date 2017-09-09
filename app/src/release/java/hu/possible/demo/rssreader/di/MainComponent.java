package hu.possible.demo.rssreader.di;

import javax.inject.Singleton;

import dagger.Component;

/**
 * The class defined the component methods injecting types in the app.
 */
@Singleton
@Component(modules = {ManagerModule.class})
public interface MainComponent extends MainComponentBase {
}
