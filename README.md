# rss-reader

Basic RSS Reader with the following features:
* Add/remove RSS feed sources.
* Share and order RSS feed sources by name and feed items by publication date (if it's available).
* Display the content of the feed items and an option to open the source of the item.
Possible future tasks:
* Prevent errors from ConentManager to affect previous screens.
** Possible solution: Introduce an 'IDLE' state which gets emitted right after the error and will be ignored by the subscribers.
** Possible problem: Valid content update shouldn't be ignored. E.g.: If a feed loaded the first on FeedActivity, changes could be displayed also on the previous, MainActivity, when the user gets back to it.
* ...
