/*
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a SharOn search result and provides serialization/deserialization
 */
public class Result {

    private long fileId; //holds the file id
    private long fileSize; //holds the file size
    private String fileName; //holds the file name

    /*frame size of result*/
    private int frameSize = 9;

    /*string check for alphanumeric characters*/
    private static final String alphaNums = "^[\\w\\-_.\\s]+$";

    private static final String errMessage = "bad field"; //error message

    private static final String IDstr = "ID"; //id field
    private static final String Sizestr = "SIZE"; //Size field
    private static final String Namestr = "NAME"; //Name field
    private static final String msgB2I = "byte to int";

    /**
     * Constructs a single Result instance from given input stream
     * @param in input stream to parse
     * @throws IOException if problem parsing Result instance,
     *      including null MessageInput
     * @throws BadAttributeValueException if bad attribute value
     */
    public Result(MessageInput in) throws IOException,
            BadAttributeValueException {
        setFileID(b2l(in.next4Tok()));
        setFileSize(b2l(in.next4Tok()));
        setFileName(in.getline());
    }

    /**
     * Constructs a Result from given input
     * @param id file ID
     * @param size file size
     * @param name file name
     * @throws BadAttributeValueException if bad attribute value
     */
    public Result(long id, long size, String name)
            throws BadAttributeValueException {
        setFileID(id);
        setFileSize(size);
        setFileName(name);
    }

    /**
     * Get file ID
     * @return file ID
     */
    public long getFileID() {
        return fileId;
    }

    private byte[] getFileID_byte() {
        return new byte[] {(byte)((fileId >>> 24) & 0xFF),
                (byte)((fileId >>> 16) & 0xFF),
                (byte)((fileId >>> 8) & 0xFF),
                (byte)((fileId) & 0xFF)};
    }

    /**
     * Get file Size
     * @return file Size
     */
    public long getFileSize() {
        return fileSize;
    }

    private byte[] getFileSIZE_byte() {
        return new byte[] {(byte)((fileSize >>> 24) & 0xFF),
                (byte)((fileSize >>> 16) & 0xFF),
                (byte)((fileSize >>> 8) & 0xFF),
                (byte)((fileSize) & 0xFF)};
    }
    /**
     * Get file Name
     * @return file Name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets id of result
     * @param id id to set
     * @throws BadAttributeValueException if bad attribute
     */
    public void setFileID(long id) throws BadAttributeValueException {
        if(id >= 0) {
            fileId = id;
        } else {
            throw new BadAttributeValueException(errMessage, IDstr);
        }
    }

    /**
     * Set file Size
     * @param size new file Size
     */
    public void setFileSize(long size) throws BadAttributeValueException {
        if(size >= 0) {
            fileSize = size;
        } else {
            throw new BadAttributeValueException(errMessage, Sizestr);
        }
    }

    /**
     * Set file Name
     * @param name new file Name
     * @throws BadAttributeValueException if bad attribute value
     */
    public void setFileName(String name) throws BadAttributeValueException {
        if (name != null) {
            if (name.matches(alphaNums) && !"\n".contains(name)) {
                fileName = name;
                frameSize += fileName.length();
            } else {
                throw new BadAttributeValueException(errMessage, Namestr);
            }
        } else {
            throw new BadAttributeValueException(errMessage, Namestr);
        }
    }

    /**
     * Serialize Result to given output stream
     * @param out output stream to serialize to
     * @throws IOException if unable to serialize Result instance
     */
    public void encode(MessageOutput out) throws IOException {
        ByteBuffer em = ByteBuffer.allocate(frameSize);

        em.put(getFileID_byte());
        em.put(getFileSIZE_byte());
        em.put(getFileName().getBytes());
        em.put("\n".getBytes());

        out.writeStr(new String(em.array()));
    }

    /**
     * converts a byte[] to an int
     * @param bArr byte[] to convert
     * @return the int value
     * @throws BadAttributeValueException if the byte[] is not the right size
     */
    private int b2l(byte[] bArr) throws BadAttributeValueException {
        if(bArr.length == 4) {
            return (bArr[0] << 24) | (bArr[1] << 16) | (bArr[2] << 8) | bArr[3];
        } else {
            throw new BadAttributeValueException(errMessage, msgB2I);
        }
    }

    /**
     * Implement according to the equals contract in Object
     * @param obj object to compare to
     */
    @Override
    public boolean equals(Object obj) {

        /*a self check*/
        if(this == obj) {
            return true;
        }

        /*a null check*/
        if(obj == null) {
            return false;
        }

        /*a class check*/
        if(getClass() != obj.getClass()) {
            return false;
        }

        /*the data field checks*/
        Result resObj = (Result)obj;
        return Objects.equals(fileId, resObj.fileId) &&
                Objects.equals(fileSize, resObj.fileSize) &&
                Objects.equals(fileName, resObj.fileName);
    }

    /**
     * Implement according to the hashCode contract in Object
     * @return the hash of a result object
     */
    @Override
    public int hashCode() {

        /* a prime number to help in the hash offset*/
        int aPrime = 17;

        /* the resulting hash*/
        int hash = 1;

        hash = aPrime * hash + (int)(fileId ^ (fileId >>> 32));
        hash = aPrime * hash + (int)(fileSize ^ (fileSize >>> 32));
        hash = aPrime * hash + fileName.hashCode();
        return hash;
    }

    /**
     * Returns human-readable Result representation
     * @return human-readable Result representation
     */
    public String toString() {
        return " fileID: " + getFileID() + ", fileSize: " + getFileSize() +
                ", fileName: " + getFileName();
    }
}
