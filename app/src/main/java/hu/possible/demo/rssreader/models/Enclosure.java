package hu.possible.demo.rssreader.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(prefix = "m")
@Root(name = "enclosure", strict = false)
public class Enclosure extends RealmObject {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Attribute(name = "url")
    @Getter
    @Setter
    private String mUrl;

    @Attribute(name = "length", required = false)
    @Getter
    @Setter
    private String mLength;

    @Attribute(name = "type")
    @Getter
    @Setter
    private String mType;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FIELDS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
