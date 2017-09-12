/*
 * ResultTest
 * version 0.0 created 9/4/2017
 *
 * Authors:
 * -Justin Ritter
 * -Cole Crawford
 */

package sharon.serialization.test;

import sharon.serialization.Result;
import static org.junit.Assert.*;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import sharon.serialization.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@RunWith(Parameterized.class)
public class ResultTest_equals {

    /*first result object*/
    private Result a;

    /*comparison object*/
    private Result b;

    public ResultTest_equals(String a_id, String a_size, String a_name,
                             String b_id, String b_size, String b_name) {
        try {
            a = new Result(Long.parseLong(a_id), Long.parseLong(a_size), a_name);
            b = new Result(Long.parseLong(b_id), Long.parseLong(b_size), b_name);
        }
        catch (BadAttributeValueException e) {
            System.out.println(e.getAttributeName());
        }
    }

    /**
     * the parameter for the test to run
     * @return an arraylist object that holds all the parameters to test
     */
    @Parameters
    public static Collection<Object[]> list() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{"0001", "0001", "Bob jones.txt", "0001", "0001", "Bob jones.txt"});
        return a;
    }

    @Test
    public void testReflexive() {
        assertEquals(a, a);
    }

    @Test
    public void testSymmetric() {
        assertEquals(a.equals(b), b.equals(a));
    }

    @Test
    public void testTransitive() throws BadAttributeValueException{
        Result c = new Result(b.getFileID(),b.getFileSize(), b.getFileName());

        if(a.equals(b)) {
            if(b.equals(c)) {
                assertTrue(a.equals(c));
            }
        }
    }

    @Test
    public void testConsistent() {
        boolean eqlResult;
        boolean variableCondition;
        int count = 0;

        if(a.equals(b)) {
            variableCondition = true;
            eqlResult = true;
        }
        else {
            variableCondition = false;
            eqlResult = false;
        }

        while(eqlResult == variableCondition && count < 100000 ) {
            eqlResult = a.equals(b);
            count++;
        }

        assertEquals(eqlResult, variableCondition);
    }
}
