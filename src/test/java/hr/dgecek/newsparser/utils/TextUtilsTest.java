package hr.dgecek.newsparser.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by dgecek on 01.06.17..
 */
public class TextUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void removeHTMLAndJS() throws Exception {
        final String htmlString = "<img>";
        final String withoutHtmlString = TextUtils.removeHTMLAndJS(htmlString);
        Assert.assertEquals(withoutHtmlString, "");

        final String htmlAndTextString = "yozo<img>yozo";
        final String withoutHtmlTextString = TextUtils.removeHTMLAndJS(htmlAndTextString);
        Assert.assertEquals(withoutHtmlTextString, "yozoyozo");
    }

    @Test
    public void removeInterpunction() throws Exception {

    }

    @Test
    public void formatInterpunction() throws Exception {

    }

    @Test
    public void iIsTermAlphaWord() {
        Assert.assertFalse(TextUtils.isTermAlphaWord("333"));
        Assert.assertFalse(TextUtils.isTermAlphaWord(""));
        Assert.assertFalse(TextUtils.isTermAlphaWord("   "));
        Assert.assertFalse(TextUtils.isTermAlphaWord(null));
        Assert.assertTrue(TextUtils.isTermAlphaWord("bla"));
        Assert.assertFalse(TextUtils.isTermAlphaWord("bja4"));
        Assert.assertFalse(TextUtils.isTermAlphaWord("bja%"));
        Assert.assertFalse(TextUtils.isTermAlphaWord("bja^"));
        Assert.assertFalse(TextUtils.isTermAlphaWord("bja."));
    }

}