package hu.possible.demo.rssreader.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(prefix = "m")
@Root(name = "rss", strict = false)
public class Feed extends RealmObject {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Element(name = "channel")
    @Getter
    @Setter
    private Channel mChannel;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS - - END
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
