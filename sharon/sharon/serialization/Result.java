/*
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;

import java.io.IOException;
import java.util.Objects;


/**
 * Represents a SharOn search result and provides serialization/deserialization
 */
public class Result {
//     holds the file id
    private long fileId;

//     holds the file size
    private long fileSize;

//     holds the file name
    private String fileName;

//    string check for numeric characters
    private static final String nums = "^[\\d]+$";

//    string check for alphanumeric characters
    private static final String alphaNums = "^[\\w\\-_.\\s]+$";

//    error message
    private static final String errMessage = "Error: bad field";

//    id field
    private static final String IDstr = "ID";

//    Size field
    private static final String Sizestr = "SIZE";

//    Name field
    private static final String Namestr = "NAME";

    /**
     * Constructs a single Result instance from given input stream
     * @param in input stream to parse
     * @throws IOException if problem parsing Result instance,
     *      including null MessageInput
     * @throws BadAttributeValueException if bad attribute value
     */
    public Result(MessageInput in) throws IOException,
            BadAttributeValueException {
        if(in.hasMore()) {
            setFileID(Long.parseLong(in.nextTok()));
            setFileSize(Long.parseLong(in.nextTok()));
            setFileName(in.getline());
        }
    }

    /**
     * Constructs a Result from given input
     * @param id file ID
     * @param size file size
     * @param name file name
     * @throws BadAttributeValueException if bad attribute value
     */
    public Result(Long id, Long size, String name)
            throws BadAttributeValueException {
        setFileID(id);
        setFileSize(size);
        setFileName(name);
    }

    /**
     * gets file id in bytes
     * @return file id in bytes
     */
    private String getFileID_bytes() {
        byte[] resultBytes = new byte[4];
        StringBuilder resStr = new StringBuilder();

        for (int i = 3; i >= 0; i--) {
            resultBytes[i] = (byte)(fileId & 0xFF);
            fileId >>= 4;
        }

        for (byte bytes : resultBytes) {
            resStr.append(bytes);
        }

        return resStr.toString();
    }

    /**
     * gets file size in bytes
     * @return file size in bytes
     */
    private String getFileSize_bytes() {
        byte[] resultBytes = new byte[4];
        StringBuilder resStr = new StringBuilder();

        for (int i = 3; i >= 0; i--) {
            resultBytes[i] = (byte)(fileSize & 0xFF);
            fileSize >>= 4;
        }

        for (byte bytes : resultBytes) {
            resStr.append(bytes);
        }

        return resStr.toString();
    }

    /**
     * Get file ID
     * @return file ID
     */
    public long getFileID() {
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
     * @param id_Long new file ID
     */
    private void setFileID(Long id_Long) throws BadAttributeValueException {
        String id_Str = String.valueOf(id_Long);
        if (id_Str != null && !id_Str.isEmpty()) {
            if (id_Str.matches(nums)) {
                fileId = Long.parseLong(id_Str);
            } else {
                throw new BadAttributeValueException(errMessage, IDstr);
            }
        } else {
            throw new BadAttributeValueException(errMessage, IDstr);
        }
    }

    /**
     * Set file ID
     * @param id_Long new file ID
     */
    private void setFileSize(Long id_Long) throws BadAttributeValueException {
        String id_Str = String.valueOf(id_Long);
        if (id_Str != null && !id_Str.isEmpty()) {
            if (id_Str.matches(nums)) {
                fileSize = Long.parseLong(id_Str);
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
    private void setFileName(String name) throws BadAttributeValueException{
        if (name != null && !name.isEmpty()) {
            if (name.matches(alphaNums)) {
                fileName = name;
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
        String id_Str = getFileID_bytes() + getFileSize_bytes();
        id_Str += getFileName() + "\n\n";
        /*
        String id_bytes = new String(getFileID_bytes());
        String size_bytes = new String(getFileSize_bytes(),
                StandardCharsets.US_ASCII);

        sOut = id_bytes + size_bytes + getFileName() + "\n\n";*/
        out.writeStr(id_Str);
    }

    /**
     * Implement according to the equals contract in Object
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
        return "fileID: " + getFileID() + ", fileSize: " + getFileSize() +
                ", fileName: " + getFileName();
    }
}
