package hu.possible.demo.rssreader.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(prefix = "m")
@Root(name = "item", strict = false)
public class Item extends RealmObject {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PrimaryKey
    @Getter
    private String mId = UUID.randomUUID().toString();

    @Element(name = "title")
    @Getter
    @Setter
    private String mTitle;

    @Element(name = "link")
    @Getter
    @Setter
    private String mLink;

    @Element(name = "description", required = false)
    @Getter
    @Setter
    private String mDescription;

    @Element(name = "pubDate", required = false)
    @Getter
    @Setter
    private String mPubDate;

    @ElementList(name = "image", inline = true, required = false)
    @Getter
    @Setter
    private RealmList<Image> mImages;

    @ElementList(name = "enclosure", inline = true, required = false)
    @Getter
    @Setter
    private RealmList<Enclosure> mEnclosures;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
