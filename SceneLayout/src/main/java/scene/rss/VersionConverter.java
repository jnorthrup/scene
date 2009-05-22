package scene.rss;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class VersionConverter extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type.equals(Version.class);
    }

    public String toString(Object obj) {
        return obj == null ? null : ((Version) obj).getValue();
    }

    public Object fromString(String str) {
        return new Version(str);
    }
}
