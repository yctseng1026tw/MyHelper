package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.eastioquick.helper.MessageHelper;
import com.eastioquick.helper.WebContentHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class WebContentActivity extends Activity {
    EditText etUrl;
    EditText etTag;

    TextView textView;
    WebContentHelper helper;
    MessageHelper messageHelper;
    WebContentActivity me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_content);
        me = this;
        helper = new WebContentHelper(this);
        messageHelper = new MessageHelper(this);
        etTag = (EditText) findViewById(R.id.etTag);
        etUrl = (EditText) findViewById(R.id.eturl);

        textView = (TextView) findViewById(R.id.textView);
        etUrl.setText("http://opendata.cwb.gov.tw/govdownload?dataid=F-C0032-021&authorizationkey=rdec-key-123-45678-011121314");

    }

    public void doBrowser(View v) {
        WebView wv=(WebView)findViewById(R.id.webView);
        EditText et=(EditText)findViewById(R.id.etWVUrl);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                //view.loadUrl(request.getUrl().toString());
                view.loadUrl(url);
                return true;
            }});
        wv.loadUrl(et.getText().toString());
    }

    public void doXML(View v) {
/*    Thread thread = new Thread(mutiThread);
      thread.start();
*/
//   runOnUiThread(mutiThread);
        String url = "http://opendata.cwb.gov.tw/govdownload";
        String para = "dataid=F-C0032-021&authorizationkey=rdec-key-123-45678-011121314";
        WebContentHelper.HttpRequestCallBack cb = new WebContentHelper.HttpRequestCallBack() {
            public void onNetwork(InputStream is) {
                try {
                    String stag = etTag.getText().toString();
                    List result = helper.findByXMLPullParseXML(is, stag);

                    StringBuffer sb = new StringBuffer();
                    for (Object o : result) {
                        sb.append(o);
                    }

                    final String ss = sb.toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(ss);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        WebContentHelper.HttpRequest task = new WebContentHelper.HttpRequest(cb);
        task.execute(url, para, "GET");
    }

    public void doJSON(View v) {
        //VghLogin vgh=new VghLogin();
        //vgh.execute();

        String url = "http://cloud.culture.tw/frontsite/trans/SearchShowAction.do";
        String para = "method=doFindTypeJ&category=13";
        String method = "GET";

        url="http://oldpaper.g0v.ronny.tw/index/json";
        para="";
        WebContentHelper.HttpRequestCallBack cb = new WebContentHelper.HttpRequestCallBack() {
            public void onNetwork(InputStream is) {
                try {

                    String s = helper.inputStream2String(is);
                    //final List l = helper.toJSON(s);
                    JSONObject root=new JSONObject(s);
                    JSONArray data=root.getJSONArray("data");
                    JSONObject data1=data.getJSONObject(0);
                    final String headlines=data1.getString("headlines");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        textView.setText(headlines);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        WebContentHelper.HttpRequest task = new WebContentHelper.HttpRequest(cb);
        task.execute(url, para, method);

    }

    public class VghLogin extends AsyncTask<Void, Void, String> {
        @Override
        public String doInBackground(Void... arg0) {
            // String surl=arg0[0];
            InputStream is = null;
            try {
                URL url = new URL("http://ehis.vghtc.gov.tw/cpoe/m2/user/login?m2Login_login=CC4F&m2Login_pass=APCC4FVGHTC");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15 * 1000);
                connection.connect();
                is = connection.getInputStream();
                String s = helper.inputStream2String(is, "Big5");
                Log.d(this.getClass().getSimpleName(), s);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
