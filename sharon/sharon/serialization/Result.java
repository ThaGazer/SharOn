/*
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * Represents a SharOn search result and provides serialization/deserialization
 */
public class Result {
    /* holds the file id*/
    private long fileId;

    /* holds the file size*/
    private long fileSize;

    /* holds the file name */
    private String fileName;

    /*string check for numeric characters*/
    private static final String nums = "^[\\d]+$";

    /*string check for alphanumeric characters*/
    private static final String alphaNums = "^[\\w\\-\\_\\.]+$";

    /*error message*/
    private static final String errMessage = "Error: bad field";

    /*id field*/
    private static final String IDstr = "ID";

    /*Size field*/
    private static final String Sizestr = "SIZE";

    /*Name field*/
    private static final String Namestr = "NAME";

    /**
     * Constructs a single Result instance from given input stream
     * @param in input stream to parse
     * @throws IOException if problem parsing Result instance,
     *      including null MessageInput
     * @throws BadAttributeValueException if bad attribute value
     */
    public Result(MessageInput in) throws IOException,
            BadAttributeValueException{
    }

    /**
     * Constructs a Result from given input
     * @param id file ID
     * @param size file size
     * @param name file name
     * @throws BadAttributeValueException if bad attribute value
     */
    public Result(String id, String size, String name)
            throws BadAttributeValueException{
        setFileId(id);
        setFileSize(size);
        setFileName(name);
    }

    /**
     * gets file id in bytes
     * @return file id in bytes
     */
    private byte[] getFileId_bytes() {
        ByteBuffer byteArr = ByteBuffer.allocate(4);
        byteArr.putInt((int)getFileId());
        return byteArr.array();
    }

    /**
     * gets file size in bytes
     * @return file size in bytes
     */
    private byte[] getFileSize_bytes() {
        ByteBuffer byteArr = ByteBuffer.allocate(4);
        byteArr.putInt((int)getFileSize());
        return byteArr.array();
    }

    /**
     * Get file ID
     * @return file ID
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * Get file Size
     * @return file Size
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Get file Name
     * @return file Name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set file ID
     * @param id new file ID
     */
    public void setFileId(String id) throws BadAttributeValueException {
        if (id != null && !id.isEmpty()) {
            if (id.matches(nums)) {
                fileId = Long.parseLong(id);
            } else {
                throw new BadAttributeValueException(errMessage, IDstr);
            }
        } else {
            throw new BadAttributeValueException(errMessage, IDstr);
        }
    }


    /**
     * Set file ID
     * @param id new file ID
     */
    public void setFileSize(String id) throws BadAttributeValueException {
        if (id != null && !id.isEmpty()) {
            if (id.matches(nums)) {
                fileId = Long.parseLong(id);
            } else {
                throw new BadAttributeValueException(errMessage, Sizestr);
            }
        } else {
            throw new BadAttributeValueException(errMessage, Sizestr);
        }
    }

    /**
     * Set file Name
     * @param name new file Name
     * @throws BadAttributeValueException if bad attribute value
     */
    public void setFileName(String name) throws BadAttributeValueException{
        if (name != null && !name.isEmpty()) {
            if (name.matches(alphaNums)) {
                if(fileName.contains("\n")) {
                    fileName = name;
                }
                else {
                    fileName = name + "\n";
                }
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
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        bOut.write(getFileId_bytes());
        bOut.write(getFileSize_bytes());
        bOut.write(getFileName().getBytes(StandardCharsets.US_ASCII));
        out.writeStr(bOut);
    }

    /**
     * Implement according to the equals contract in Object
     */
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
     */
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
        return "fileID: " + getFileId() + ", fileSize: " + getFileSize() +
                ", fileName: " + getFileName();
    }
}
