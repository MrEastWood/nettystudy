package study.lxy.nettystudy;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    
    @Test
    public void lineSpliter(){
    	
    	System.out.println(System.lineSeparator());
    	System.out.println("\r\n".equals(System.lineSeparator()));
    	System.out.println(System.getProperty("line.separator").equals(System.lineSeparator()));
    	
    }
}
