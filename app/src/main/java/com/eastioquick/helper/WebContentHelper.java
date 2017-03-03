package com.eastioquick.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class WebContentHelper {
    private Context context;

    public WebContentHelper(Context context) {
        this.context = context;
    }

    public Map toJSON(JSONObject jsono) throws Exception {
        Map rtn = new Hashtable();
        Iterator iter = jsono.keys();
        while (iter.hasNext()) {
            String k = (String) iter.next();
            rtn.put(k, jsono.getString(k));
        }

        return rtn;
    }

    public List toJSON(String arrStr) throws Exception {
        List rtn = new ArrayList();
        if (arrStr == null) return rtn;
        String aStr = arrStr.trim();
        if (aStr.length() == 0 || !aStr.startsWith("[")) return rtn;
        JSONArray arrJson = new JSONArray(aStr);
        for (int i = 0; i < arrJson.length(); i++) {
            JSONObject jsono = arrJson.getJSONObject(i);
            Map m = toJSON(jsono);
            rtn.add(m);
        }
        return rtn;
    }

    public List findByXpath(InputSource source, String path) throws Exception {
        List rtn = new ArrayList();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        //InputSource source=new InputSource(new FileReader("tds.xml"));
        NodeList nodeList = (NodeList) xPath.evaluate(path, source, XPathConstants.NODESET);
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(0);
                rtn.add(element.getTextContent());
            }
        }
        /*
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element show = (Element) nodeList.item(i);
            String guestName = xPath.evaluate("guest/name", show);
            String guestCredit = xPath.evaluate("guest/credit", show);
            System.out.println(show.getAttribute("weekday") + ", " + show.getAttribute("date") + " - "
                    + guestName + " (" + guestCredit + ")");
        }
        */
        return rtn;
    }

    //xmlPullParser解析xml文件
    public List findByXMLPullParseXML(InputStream is, String tag) {
        List rtn = new ArrayList();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            //xmlPullParser.setInput(Thread.currentThread().getContextClassLoader().getResourceAsStream(BOOKS_PATH), "UTF-8");
            xmlPullParser.setInput(is, "UTF-8");
            //xmlPullParser.setInput(reader);
            int eventType = xmlPullParser.getEventType();

            try {
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = xmlPullParser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (nodeName.equals(tag)) {
                                rtn.add(xmlPullParser.nextText());
                                break;
                            }
                        default:
                            break;
                    }
                    eventType = xmlPullParser.next();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    //dom解析xml文件
    public List findByDomParseXML(InputStream is, String tag) {
        List rtn = new ArrayList();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            Element root = doc.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName(tag);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String s = element.getFirstChild().getNodeValue();
                rtn.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public String inputStream2String(InputStream is) {
        return inputStream2String(is, "UTF-8");
    }

    public String inputStream2String(InputStream is, String charSet) {

        BufferedReader r = null;
        StringBuilder total = new StringBuilder();
        try {
            r = new BufferedReader(new InputStreamReader(is, charSet));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                r.close();
            } catch (Exception e) {
            }
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return total.toString();
    }

    public interface HttpRequestCallBack {
        void onNetwork(InputStream is);
    }

    public static class HttpRequest extends AsyncTask<String, Void, Void> {
        HttpRequestCallBack callBack = null;

        public HttpRequest(HttpRequestCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public Void doInBackground(String... arg0) {
            String request = arg0[0];//url
            String urlParameters = arg0[1];
            String method = arg0[2];

            InputStream is = null;
            try {
                String surl = "GET".equals(method) ? request + ("".equals(urlParameters)?"":"?") + urlParameters : request;
                URL url = new URL(surl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15 * 1000);
                conn = this.handleRedirect(conn);
                if ("GET".equals(method)) {
                    conn.setRequestMethod("GET");
                } else {
                    byte[] postData = urlParameters.getBytes("UTF-8");
                    int postDataLength = postData.length;
                    conn.setDoOutput(true);
                    //conn.setInstanceFollowRedirects( false );
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                    conn.setUseCaches(false);
                    try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                        wr.write(postData);
                        wr.flush();
                        wr.close();
                    }
                }

/******* cookie start ******
 // Fetch and set cookies in requests
 CookieManager cookieManager = CookieManager.getInstance();
 String cookie = cookieManager.getCookie(conn.getURL().toString());
 if (cookie != null) {
 conn.setRequestProperty("Cookie", cookie);
 }
 */
                conn.connect();
/*
                // Get cookies from responses and save into the cookie manager
                List cookieList = conn.getHeaderFields().get("Set-Cookie");
                if (cookieList != null) {
                    for (Object cookieTemp : cookieList) {
                        cookieManager.setCookie(conn.getURL().toString(), (String)cookieTemp);
                    }
                }
****** cookie end *******/

                is = conn.getInputStream();
                callBack.onNetwork(is);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            return null;
            //return is;
        }

        /*
                @Override
                public void onPostExecute(InputStream result) {
                    callBack.onNetwork(result);
                    //super.onPostExecute(result);
                }*/
        private HttpURLConnection handleRedirect(HttpURLConnection conn) throws Exception {
            boolean redirect = false;
            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }
            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                //conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                //conn.addRequestProperty("User-Agent", "Mozilla");
                //conn.addRequestProperty("Referer", "google.com");

                Log.d(this.getClass().getSimpleName(), "Redirect to URL : " + newUrl);

            }
            return conn;
        }
    }
}