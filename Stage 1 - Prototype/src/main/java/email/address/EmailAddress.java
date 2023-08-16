package email.address;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import email.email.body.tag.EmailTag;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an email address.
 * @author Jordan Jones
 */
public class EmailAddress implements Serializable {

    //CONSTANTS
    private static final int MAX_LENGTH = 320;

    //Attributes
    private String address;
    private String nickname;
    private HashMap<EmailTag, String[]> tagValues = new HashMap<>();

    /**
     * Creates an email address with a nickname.
     * @param address Email address text
     * @param nickname Nickname of the email address
     * @throws InvalidEmailAddressException Thrown if address format is invalid
     */
    public EmailAddress(String address, String nickname)
        throws InvalidEmailAddressException {
        setAddress(address);
        setNickname(nickname);
    }

    /**
     * Creates an email address.
     * @param address Email address text
     * @throws InvalidEmailAddressException Thrown if address format is invalid
     */
    public EmailAddress(String address) throws InvalidEmailAddressException {
        setAddress(address);
    }

    /**
     * Converts an array of email addresses to a string.
     * @param emailAddresses Array of email addresses to be converted
     * @return String of email addresses
     */
    public static @NotNull String arrayToString(
        EmailAddress @NotNull [] emailAddresses) {
        if (emailAddresses.length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < (emailAddresses.length - 1) - 1; i++) {
                stringBuilder.append(emailAddresses[i].getAddress())
                    .append(", ");
            }
            stringBuilder.append(emailAddresses[emailAddresses.length - 1]);
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    /**
     * Sets the email address text.
     * @param address Email address text
     * @throws InvalidEmailAddressException Thrown if address format is invalid
     */
    private void setAddress(String address)
        throws InvalidEmailAddressException {
         if (!Pattern.compile(
             "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
            .matcher(address).matches() || address.length() > MAX_LENGTH) {
            throw new InvalidEmailAddressException(address);
        }
        this.address = address.toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the nickname of the email address.
     * @param nickname Nickname of the email address
     */
    private void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Sets the emailTags that the email address needs values for.
     * @param emailTags Tags the email addresses needs values for
     */
    public void setTags(EmailTag @NotNull [] emailTags) {
        HashMap<EmailTag, String[]> tempValues = new HashMap<>();
        for (EmailTag emailTag : emailTags) {
            String[] tempValue = new String[]{""};
            if (tagValues.get(emailTag) != null) {
                tempValue = tagValues.get(emailTag);
            }
            tempValues.put(emailTag, tempValue);
        }
        tagValues = tempValues;
    }

    /**
     * Sets the hash map of the tag values.
     * @param tagValues Hash map of the tag values
     */
    public void setTagValuesHashMap(HashMap<EmailTag, String[]> tagValues) {
        this.tagValues = tagValues;
    }

    /**
     * Sets the value for a tag for this email address.
     * @param emailTag Tag to set value for
     * @param tagValue Value for the give tag
     */
    public void setTagValue(EmailTag emailTag, String[] tagValue) {
        tagValues.replace(emailTag, tagValue);
    }

    /**
     * Gets the email address text.
     * @return Email address text
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the email address's nickname.
     * @return Email address nickname
     */
    private String getNickname() {
        return nickname;
    }

    /**
     * Gets the tag values hash map.
     * @return Tag value hash map
     */
    public HashMap<EmailTag, String[]> getTagValuesHashMap() {
        return tagValues;
    }

    /**
     * Gets all the tag values.
     * @return All the tag values
     */
    public String[][] getTagValues() {
        return tagValues.values().toArray(new String[0][0]);
    }


    /**
     * Gets the email address's value for a given tag.
     * @param emailTag Tag that a value is wanted for
     * @return Email address's value for a given tag
     */
    public String[] getTagValue(EmailTag emailTag) {
        return tagValues.get(emailTag);
    }

    /**
     * Gets the hash code of all of its tag values.
     * @return Hash code of all of its tag values
     */
    public int getTagValuesHashCode() {
        return Arrays.hashCode(tagValues.values().toArray());
    }

    /**
     * Checks if the given object is equivalent to the email address.
     * @param o Object to compare with
     * @return True if the email addresses are equivalent
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(getAddress(), that.getAddress());
    }

    /**
     * Gets the hash code of the email address.
     * @return Hash code of the email address
     */
    @Override
    public int hashCode() {
        return Objects.hash(getAddress());
    }

    /**
     * Gets the email address in string format.
     * @return Email address in string format
     */
    public String toString() {
        if (getNickname() != null && !getNickname().equals("")) {
            return getNickname();
        } else {
            return getAddress();
        }
    }

}
