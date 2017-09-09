package hu.possible.demo.rssreader.di;


import hu.possible.demo.rssreader.activities.AbstractActivity;

/**
 * The class serves as the build type independent base class of {@link MainComponent}.
 */
interface MainComponentBase {

    void inject(AbstractActivity abstractActivity);

}
