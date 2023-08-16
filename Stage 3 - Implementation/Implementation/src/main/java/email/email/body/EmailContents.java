package email.email.body;

import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import email.address.EmailAddress;
import email.address.EmptyTagValueException;
import util.Alphabet;
import util.BulletPointType;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Represents the contents of an email.
 * @author Jordan Jones
 */
public class EmailContents implements Serializable {

    //Attributes
    private String text;
    private EmailTag[] emailTags;

    /**
     * Creates the contents of an email.
     * @param text Text of the contents
     * @param emailTags Tags of the contents
     */
    public EmailContents(String text, EmailTag[] emailTags) {
        setText(text);
        setTags(emailTags);
    }

    /**
     * Creates the contents of a minimum email.
     * @param text Text of the contents
     */
    public EmailContents(String text) {
        setText(text);
    }

    /**
     * Sets the text of the contents.
     * @param text Text of the contents
     */
    private void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the emailTags of the contents.
     * @param emailTags Tags of the contents
     */
    private void setTags(EmailTag[] emailTags) {
        this.emailTags = emailTags;
    }

    /**
     * Gets the text of the contents.
     * @return Text of the contents
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the emailTags of the contents.
     * @return Tags of the contents
     */
    public EmailTag[] getTags() {
        return emailTags;
    }

    /**
     * Gets the contents with its emailTags filled for the address.
     * @param emailAddress Email address to get emailTags for
     * @return Contents the emailTags filled for the given address
     * @throws EmptyTagValueException Thrown if an address' tag value is empty
     */
    public String getFilledText(EmailAddress emailAddress)
        throws EmptyTagValueException {
        String filledText = text;
        for (EmailTag emailTag : getTags()) {
            if (emailTag instanceof EmailListTag) {
                String[] tagValues = emailAddress.getTagValue(emailTag);
                StringBuilder bulletPoints = new StringBuilder();
                EmailListTag emailListTag = ((EmailListTag) emailTag);
                int i = 0;
                Alphabet alphabet = new Alphabet(false);
                for (String tagValue : tagValues) {
                    if (tagValue == null || tagValue.equals("")) {
                        throw new EmptyTagValueException(
                            emailAddress, emailTag);
                    }
                    String value;
                    if (emailListTag.getBulletPointType().equals(
                        BulletPointType.NUMBER_BULLET_POINT)) {
                        i++;
                        value = i + ".";
                    } else if (emailListTag.getBulletPointType().equals(
                        BulletPointType.LETTER_BULLET_POINT)) {
                        value = alphabet.letter(i) + ".";
                        i++;
                    } else {
                        value = emailListTag.getBulletPointType().toString();
                    }
                    bulletPoints.append(value).append(" ")
                        .append(tagValue).append("<br>");
                }
                filledText = filledText.replaceAll(Pattern.quote(
                    emailTag.toString()), bulletPoints.substring(0,
                    bulletPoints.length() - "<br>".length()));
            } else {
                String tagValue = emailAddress.getTagValue(emailTag)[0];
                if (tagValue == null || tagValue.equals("")) {
                    throw new EmptyTagValueException(emailAddress, emailTag);
                }
                filledText = filledText.replaceAll(
                    Pattern.quote(emailTag.toString()), tagValue);
            }
        }
        return filledText;
    }

    /**
     * Gets the contents in a string format.
     * @return Contents in a string format
     */
    @Override
    public String toString() {
        return getText();
    }

}
