import com.google.gson.JsonElement;

/**
 * Class representing parsed JSON element where firstElement represent a string to be used for sorting
 * and fullVersion is the full JSON element (Array, Object or String).
 */
public class ParsedElement implements Comparable<ParsedElement> {
    String firstElement;
    JsonElement fullVersion;

    /**
     * Constructs ParsedElement given first string and full version of JSON element.
     * @param firstElement string to be used for sorting.
     * @param fullVersion full version of JSON element.
     */
    public ParsedElement (String firstElement, JsonElement fullVersion){
        this.firstElement = firstElement;
        this.fullVersion = fullVersion;
    }

    @Override
    public int compareTo(ParsedElement o) {
        return firstElement.compareTo(o.firstElement);
    }

    @Override
    public String toString() {
        return this.fullVersion.toString();
    }
}
