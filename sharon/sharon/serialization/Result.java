/**
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;

import java.io.IOException;

/** import something*/

public class Result {
    /** holds the file id*/
    private long fileId;

    /** holds the file size*/
    private long fileSize;

    /** holds the file name */
    private String fileName;

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
    public Result(long id, long size, String name)
            throws BadAttributeValueException{
        fileId = id;
        fileSize = size;
        fileName = name;
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
    public void setFileId(long id) throws BadAttributeValueException {
        fileId = id;
    }

    /**
     * Set file Size
     * @param size new file Size
     * @throws BadAttributeValueException if bad attribute value
     */
    public void setFileSize(long size) throws BadAttributeValueException{
        fileSize = size;
    }

    /**
     * Set file Name
     * @param name new file Name
     * @throws BadAttributeValueException if bad attribute value
     */
    public void setFileName(String name) throws BadAttributeValueException{
        fileName = name;
    }

    /**
     * Serialize Result to given output stream
     * @param out output stream to serialize to
     * @throws IOException if unable to serialize Result instance
     */
    public void encode(MessageOutput out) throws IOException {

    }

    /**
     * Implement according to the equals contract in Object
     */
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Implement according to the hashCode contract in Object
     */
    public int hashcode() {
        return 0;
    }

    /**
     * Returns human-readable Result representation
     * @return human-readable Result representation
     */
    public String toString() {
        return super.toString();
    }
}
