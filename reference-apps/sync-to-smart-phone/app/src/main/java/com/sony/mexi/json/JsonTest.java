package com.sony.mexi.json;

import com.sony.imaging.app.base.shooting.camera.parameters.BooleanSupportedChecker;
import com.sony.mexi.json.JsValue;
import org.junit.Assert;
import org.junit.Test;

/* loaded from: classes.dex */
public class JsonTest {
    private String[][] surrogatePairs = {new String[]{"𠀋", "𠀋", "\\ud840\\udc0b"}, new String[]{"𪚲", "𪚲", "\\ud869\\udeb2"}};

    @Test
    public void testParse() {
        testParseUndef();
        testParseJsNumber();
        testParseJsBoolean();
        testParseJsString();
        testParseJsObject();
        testParseJsArray();
        testParseSpace();
        testParseJsValueException();
    }

    public void testParseUndef() {
        Assert.assertEquals(JsValue.Type.UNDEFINED, Json.parse("undefined").type());
        Assert.assertEquals("undefined", Json.parse("undefined").toString());
        Assert.assertEquals(JsValue.Type.STRING, Json.parse("\"undefined\"").type());
        Assert.assertEquals("\"undefined\"", Json.parse("\"undefined\"").toString());
        Assert.assertEquals(JsValue.Type.STRING, Json.parse("\"ab.c\"").type());
        Assert.assertEquals("\"ab.c\"", Json.parse("\"ab.c\"").toString());
        Assert.assertEquals(JsValue.Type.NULL, Json.parse("null").type());
        Assert.assertEquals("null", Json.parse("null").toString());
        Assert.assertEquals(JsValue.Type.STRING, Json.parse("\"null\"").type());
        Assert.assertEquals("\"null\"", Json.parse("\"null\"").toString());
    }

    public void testParseJsNumber() {
        Assert.assertTrue(Json.parse("1").isInt());
        Assert.assertTrue(Json.parse("1").isDouble());
        Assert.assertEquals(1L, Json.parse("1").toJavaInt());
        Assert.assertEquals(1L, Json.parse("1.0").toJavaInt());
        Assert.assertEquals(1.0d, Json.parse("1").toJavaDouble(), 0.0d);
        try {
            Assert.assertEquals(1L, Json.parse("1.1").toJavaInt());
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
        }
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("1").type());
        Assert.assertEquals("1", Json.parse("1").toString());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("11").type());
        Assert.assertEquals("11", Json.parse("11").toString());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("2147483647").type());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse(" 11").type());
        Assert.assertEquals("11", Json.parse(" 11").toString());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("-11").type());
        Assert.assertEquals("-11", Json.parse("-11").toString());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("1.1e+3").type());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("2.2E-3").type());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("1.7976931348623157e+308").type());
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("-2.2250738585072014e-308").type());
        Assert.assertEquals(0.001d, Json.parse("1.0E-3").toJavaDouble(), 0.0d);
        Assert.assertEquals(3.3d, Json.parse("3.300000e+00").toJavaDouble(), 0.0d);
        Assert.assertEquals((Object) null, Json.parse("010"));
        Assert.assertEquals((Object) null, Json.parse("0x10"));
        Assert.assertEquals(JsValue.Type.NUMBER, Json.parse("1.0").type());
        Assert.assertEquals("1.0", Json.parse("1.0").toString());
    }

    public void testParseJsBoolean() {
        Assert.assertTrue(Json.parse(BooleanSupportedChecker.TRUE).isBoolean());
        Assert.assertTrue(Json.parse(BooleanSupportedChecker.TRUE).toJavaBoolean());
        Assert.assertFalse(Json.parse("false").toJavaBoolean());
        Assert.assertEquals(JsValue.Type.BOOLEAN, Json.parse(BooleanSupportedChecker.TRUE).type());
        Assert.assertEquals(BooleanSupportedChecker.TRUE, Json.parse(BooleanSupportedChecker.TRUE).toString());
        Assert.assertEquals(JsValue.Type.BOOLEAN, Json.parse("false").type());
        Assert.assertEquals("false", Json.parse("false").toString());
    }

    public void testParseJsString() {
        Assert.assertEquals(JsValue.Type.STRING, Json.parse("\"name\"").type());
        Assert.assertEquals("\"name\"", Json.parse("\"name\"").toString());
        Assert.assertEquals("あ", Json.parse("\"あ\"").toJavaString());
        Assert.assertEquals("あ", Json.parse("\"あ\"").toJavaString());
        Assert.assertEquals("あ", Json.parse("\"\\u3042\"").toJavaString());
        Assert.assertEquals("\"あ\"", Json.parse("\"あ\"").toString());
        Assert.assertEquals("\"あ\"", Json.parse("\"あ\"").toString());
        Assert.assertEquals("\"あ\"", Json.parse("\"\\u3042\"").toString());
        Assert.assertEquals("あ\u0000\b\t\n\f\r\"'\\", Json.parse("\"\\u3042\\0\\b\\t\\n\\f\\r\\\"\\'\\\\\"").toJavaString());
        Assert.assertEquals("a'b", Json.parse("\"a'b\"").toJavaString());
        Assert.assertEquals("\"/\"", Json.parse("\"\\/\"").toString());
        testParseSurrogatePairs();
    }

    public void testParseJsObject() {
        Assert.assertEquals(JsValue.Type.OBJECT, Json.parse("{}").type());
        Assert.assertEquals("{}", Json.parse("{}").toString());
        Assert.assertEquals(JsValue.Type.OBJECT, Json.parse("{\"name\":\"value\"}").type());
        Assert.assertEquals("{\"name\":\"value\"}", Json.parse("{\"name\":\"value\"}").toString());
        Assert.assertEquals(JsValue.Type.OBJECT, Json.parse("{\"name1\":\"value1\", \"name2\":\"value2\"}").type());
        Assert.assertEquals("{\"name\":\"value\",\"name2\":\"value2\"}", Json.parse("{\"name\":\"value\", \"name2\":\"value2\"}").toString());
        Assert.assertEquals(JsValue.Type.OBJECT, Json.parse("{\"name1\":{\"name2\":3}}").type());
        Assert.assertEquals("{\"name1\":{\"name2\":3}}", Json.parse("{\"name1\":{\"name2\":3}}").toString());
    }

    public void testParseJsArray() {
        Assert.assertEquals(JsValue.Type.ARRAY, Json.parse("[]").type());
        Assert.assertEquals("[]", Json.parse("[]").toString());
        Assert.assertEquals(JsValue.Type.ARRAY, Json.parse("[1, 2, 3]").type());
        Assert.assertEquals("[1,2,3]", Json.parse("[1, 2, 3]").toString());
        Assert.assertEquals(JsValue.Type.ARRAY, Json.parse("[1.0, true, \"value\"]").type());
        Assert.assertEquals("[1.0,true,\"value\"]", Json.parse("[1.0, true, \"value\"]").toString());
        Assert.assertEquals(JsValue.Type.ARRAY, Json.parse("[{\"name\":[\"value\"]}]").type());
        Assert.assertEquals("[{\"name\":[\"value\"]}]", Json.parse("[{\"name\":[\"value\"]}]").toString());
        Assert.assertEquals((Object) null, Json.parse("["));
    }

    public void testParseSpace() {
        Assert.assertEquals(JsValue.Type.ARRAY, Json.parse(" [ {  \"name\" : [ \"value\"  ] } ] ").type());
        Assert.assertEquals("[{\"name\":[\"value\"]}]", Json.parse(" [ {  \"name\" : [ \"value\"  ] } ] ").toString());
        Assert.assertEquals(JsValue.Type.ARRAY, Json.parse("\t[\t{\t\t\"name\"\t:\t[\t\"value\"\t]\t}\t]\t").type());
        Assert.assertEquals("[{\"name\":[\"value\"]}]", Json.parse("\t[\t{\t\t\"name\"\t:\t[\t\"value\"\t]\t}\t]\t").toString());
        Assert.assertEquals(JsValue.Type.ARRAY, Json.parse("\n[\n{\n\n\"name\"\n:\n[\n\"value\"\n]\n}\n]\n").type());
        Assert.assertEquals("[{\"name\":[\"value\"]}]", Json.parse("\n[\n{\n\n\"name\"\n:\n[\n\"value\"\n]\n}\n]\n").toString());
    }

    public void testParseJsValueException() {
        Assert.assertFalse(Json.parse("1").isBoolean());
        Assert.assertTrue(Json.parse("1").isInt());
        Assert.assertTrue(Json.parse("1").isDouble());
        Assert.assertFalse(Json.parse("1").isString());
        Assert.assertFalse(Json.parse("\"name\"").isBoolean());
        Assert.assertFalse(Json.parse("\"name\"").isInt());
        Assert.assertFalse(Json.parse("\"name\"").isDouble());
        Assert.assertTrue(Json.parse("\"name\"").isString());
        try {
            Json.parse("\"name\"").toJavaBoolean();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
        }
        try {
            Json.parse("\"name\"").toJavaInt();
        } catch (Exception e2) {
            Assert.assertTrue(e2 instanceof RuntimeException);
        }
        try {
            Json.parse("\"name\"").toJavaDouble();
        } catch (Exception e3) {
            Assert.assertTrue(e3 instanceof RuntimeException);
        }
        try {
            Json.parse("1").toJavaString();
        } catch (Exception e4) {
            Assert.assertTrue(e4 instanceof RuntimeException);
        }
    }

    public void testParseSurrogatePairs() {
        for (int i = 0; i < this.surrogatePairs.length; i++) {
            String to = this.surrogatePairs[i][0];
            Assert.assertEquals(to, Json.parse("\"" + to + "\"").toJavaString());
            Assert.assertEquals(to, Json.parse("\"" + this.surrogatePairs[i][1] + "\"").toJavaString());
            Assert.assertEquals(to, Json.parse("\"" + this.surrogatePairs[i][2] + "\"").toJavaString());
            Assert.assertEquals("\"" + this.surrogatePairs[i][0] + "\"", Json.parse("\"" + to + "\"").toString());
            Assert.assertEquals("\"" + this.surrogatePairs[i][0] + "\"", Json.parse("\"" + this.surrogatePairs[i][1] + "\"").toString());
            Assert.assertEquals("\"" + this.surrogatePairs[i][0] + "\"", Json.parse("\"" + this.surrogatePairs[i][2] + "\"").toString());
        }
    }

    @Test
    public void testStringify() {
        Assert.assertEquals("null", Json.stringify((String) null));
        Assert.assertEquals("\"\"", Json.stringify(""));
        Assert.assertEquals("1", Json.stringify(1));
        Assert.assertEquals("0.0", Json.stringify(0.0d));
        Assert.assertEquals(BooleanSupportedChecker.TRUE, Json.stringify(true));
        Assert.assertEquals("\"name\"", Json.stringify("name"));
        Assert.assertEquals("null", Json.stringify((int[]) null));
        Assert.assertEquals("null", Json.stringify((double[]) null));
        Assert.assertEquals("null", Json.stringify((boolean[]) null));
        Assert.assertEquals("null", Json.stringify((String[]) null));
        Assert.assertEquals("[]", Json.stringify(new int[0]));
        Assert.assertEquals("[1,3,5]", Json.stringify(new int[]{1, 3, 5}));
        Assert.assertEquals("[1.0,3.0,5.0]", Json.stringify(new double[]{1.0d, 3.0d, 5.0d}));
        Assert.assertEquals("[true,false,false]", Json.stringify(new boolean[]{true}));
        Assert.assertEquals("[\"one\",\"three\",\"five\"]", Json.stringify(new String[]{"one", "three", "five"}));
        Assert.assertEquals("\"あ\"", Json.stringify("あ"));
        Assert.assertEquals("\"あ\"", Json.stringify("あ"));
        Assert.assertEquals("\"×\"", Json.stringify("×"));
        Assert.assertEquals("\"×\"", Json.stringify("×"));
        String to = this.surrogatePairs[0][0];
        Assert.assertEquals("\"" + this.surrogatePairs[0][0] + "\"", Json.stringify(to));
        Assert.assertEquals("\"あ\\0\\b\\t\\n\\f\\r\\\"'\\\\\"", Json.stringify("あ\u0000\b\t\n\f\r\"'\\"));
        Assert.assertEquals("\"𡌛あ\\t𠀋\"", Json.stringify("𡌛あ\t𠀋"));
    }

    @Test
    public void testStringifyAndParse() {
        Assert.assertEquals(JsValue.Type.NULL, Json.parse(Json.stringify((String) null)).type());
        Assert.assertTrue(Json.parse(Json.stringify("")).isString());
        Assert.assertEquals("", Json.parse(Json.stringify("")).toJavaString());
        Assert.assertTrue(Json.parse(Json.stringify(1)).isInt());
        Assert.assertEquals(1L, Json.parse(Json.stringify(1)).toJavaInt());
        Assert.assertTrue(Json.parse(Json.stringify(0.0d)).isDouble());
        Assert.assertEquals(0.0d, Json.parse(Json.stringify(0.0d)).toJavaDouble(), 0.0d);
        Assert.assertTrue(Json.parse(Json.stringify(true)).isBoolean());
        Assert.assertEquals(true, Boolean.valueOf(Json.parse(Json.stringify(true)).toJavaBoolean()));
        Assert.assertTrue(Json.parse(Json.stringify("name")).isString());
        Assert.assertEquals("name", Json.parse(Json.stringify("name")).toJavaString());
        Assert.assertEquals("あ", Json.parse(Json.stringify("あ")).toJavaString());
        Assert.assertEquals("あ", Json.parse(Json.stringify("あ")).toJavaString());
        Assert.assertEquals("あ", Json.parse(Json.stringify("あ")).toJavaString());
        Assert.assertEquals("×", Json.parse(Json.stringify("×")).toJavaString());
        Assert.assertTrue(Json.parse(Json.stringify(new int[0])).isIntArray());
        Assert.assertArrayEquals(new int[0], Json.parse(Json.stringify(new int[0])).toJavaIntArray());
        Assert.assertTrue(Json.parse(Json.stringify(new int[]{1, 3, 5})).isIntArray());
        Assert.assertArrayEquals(new int[]{1, 3, 5}, Json.parse(Json.stringify(new int[]{1, 3, 5})).toJavaIntArray());
        Assert.assertTrue(Json.parse(Json.stringify(new double[]{1.0d, 3.0d, 5.0d})).isDoubleArray());
        Assert.assertArrayEquals(new double[]{1.0d, 3.0d, 5.0d}, Json.parse(Json.stringify(new double[]{1.0d, 3.0d, 5.0d})).toJavaDoubleArray(), 0.0d);
        boolean[] bs = {true};
        Assert.assertTrue(Json.parse(Json.stringify(bs)).isBooleanArray());
        Assert.assertEquals(Boolean.valueOf(bs[0]), Boolean.valueOf(Json.parse(Json.stringify(bs)).toJavaBooleanArray()[0]));
        Assert.assertEquals(Boolean.valueOf(bs[1]), Boolean.valueOf(Json.parse(Json.stringify(bs)).toJavaBooleanArray()[1]));
        Assert.assertEquals(Boolean.valueOf(bs[2]), Boolean.valueOf(Json.parse(Json.stringify(bs)).toJavaBooleanArray()[2]));
        String[] ss = {"one", "three", "five"};
        Assert.assertTrue(Json.parse(Json.stringify(ss)).isStringArray());
        Assert.assertArrayEquals(ss, Json.parse(Json.stringify(ss)).toJavaStringArray());
        Assert.assertEquals("あ\u0000\b\t\n\f\r\"'\\", Json.parse(JsString.toJson("あ\u0000\b\t\n\f\r\"'\\")).toJavaString());
        Assert.assertEquals("\"あ\\0\\b\\t\\n\\f\\r\\\"'\\\\\"", Json.parse(JsString.toJson("あ\u0000\b\t\n\f\r\"'\\")).toString());
    }
}
