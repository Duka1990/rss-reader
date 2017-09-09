package hu.possible.demo.rssreader.utils;

import java.util.List;

import hu.possible.demo.rssreader.models.Enclosure;
import hu.possible.demo.rssreader.models.Feed;
import hu.possible.demo.rssreader.models.Image;
import hu.possible.demo.rssreader.models.Item;
import hu.possible.demo.rssreader.models.RssSource;

public class ModelHelpers {

    public static String resolveTitleFromRssSource(RssSource rssSource) {
        Feed feed = rssSource.getFeed();

        if (feed != null && feed.getChannel() != null) {
            return feed.getChannel().getTitle();
        } else {
            return rssSource.getUrl();
        }
    }

    public static String resolveDescriptionFromRssSource(RssSource rssSource) {
        Feed feed = rssSource.getFeed();

        if (feed != null && feed.getChannel() != null) {
            return feed.getChannel().getDescription();
        } else {
            return null;
        }
    }

    public static String resolveImageUrlFromItem(Item item) {
        List<Image> images = item.getImages();
        List<Enclosure> enclosures = item.getEnclosures();

        if (images != null && images.size() > 0) {
            return images.get(0).getUrl();
        }

        if (enclosures != null && enclosures.size() > 0) {
            for (Enclosure enclosure : enclosures) {
                if (enclosure.getType().startsWith("image/")) {
                    return enclosure.getUrl();
                }
            }
        }

        return null;
    }

}
