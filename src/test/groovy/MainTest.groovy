import org.junit.Test

import static org.junit.Assert.assertEquals;
class ParseJsonTest {
    @Test
    void testPackageNotFound(){
        assertEquals("Package not found",ParseJson.work("-url https://bower.herokuapp.com/packages/lookup/angula -version v1.5.3".split(" ")),1);
    }

    @Test
    void testIncorrectVersion(){
        assertEquals("Incorrect version",ParseJson.work("-url https://bower.herokuapp.com/packages/lookup/angular -version v1.5.5".split(" ")),2);
    }

    @Test
    void testAllOk(){
        assertEquals("the file exists and it isn't empty ",ParseJson.work("-url https://bower.herokuapp.com/packages/lookup/angular -version v1.5.3".split(" ")),0);

    }

    @Test
    void testFileExists(){
        assertEquals("the file exists and it isn't empty ",ParseJson.work("-url https://bower.herokuapp.com/packages/lookup/angular -version v1.5.3".split(" ")),3);
    }

    @Test
    void testMasterVersion(){
        assertEquals("the file exists and it isn't empty ",ParseJson.work("-url https://bower.herokuapp.com/packages/lookup/angular".split(" ")),0);
    }
}
