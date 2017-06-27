package org.webant.plugin.test.weiboyi;

import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.nio.charset.Charset;

public class WeiboyiCrawler {

    public static void main(String[] args) {
        int total = 47185;
        int size = 200;

        int totalPage = (int)Math.ceil((float)total / size);
        for (int pageNo = 0; pageNo < totalPage; ++pageNo) {
            try {
                crawl(pageNo * size, size);
                System.out.println("抓取第 " + pageNo + " 页完成，数据量 " + size);
//                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void crawl(int start, int size) throws IOException {
        String url = "http://chuanbo.weiboyi.com/hworder/weixin/filterlist/source/all";
        String body = "web_csrf_token=594b295672927&price_list=top%2Csecond%2Cother%2Csingle&start=" + start + "&limit=" + size;
        String referer = "http://chuanbo.weiboyi.com/hworder/weixin/index?price_list=top%2Csecond%2Cother%2Csingle&start=" + start + "&limit=" + size;

        Response resp = org.apache.http.client.fluent.Request.Post(url)
                .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
                .addHeader("Proxy-Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Cookie", "PHPSESSID=gqu2l9mloahmng7goejmv9l9h4; Hm_lvt_29d7c655e7d1db886d67d7b9b3846aca=1498049922; Hm_lpvt_29d7c655e7d1db886d67d7b9b3846aca=1498098009; Hm_lvt_9a2792b12b6388cfcc41e508c781a8be=1498049923; Hm_lpvt_9a2792b12b6388cfcc41e508c781a8be=1498098009; aLastLoginTime=1498098006; loginHistoryRecorded=0; TRACK_USER_ID=422902; TRACK_IDENTIFY_AT=2017-06-22T02%3A20%3A39.613Z; TRACK_SESSION_ID=4fcdf2f735047ef3a7e1d10e00bb953a; TRACK_DETECTED=1.0.1; TRACK_BROWSER_ID=4a734947754705a34ad87b86f200ed5b; Hm_lvt_5ff3a7941ce54a1ba102742f48f181ab=1498098039,1498098069; Hm_lpvt_5ff3a7941ce54a1ba102742f48f181ab=1498098069; _gscu_867320846=9804992204kk0467; _gscs_867320846=t98098009rgoshs67|pv:3; _gscbrs_867320846=1")
                .addHeader("DNT", "1")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Referer", referer)
                .addHeader("Origin", "http://chuanbo.weiboyi.com")
                .addHeader("Host", "chuanbo.weiboyi.com")
                .addHeader("Proxy-Connection", "keep-alive")
                .execute();
        String result = resp.returnContent().asString(Charset.forName("UTF-8"));

        String fileName = "D:/cache/weiboyi/pages/data_" + start + "_" + size + ".json";
//        FileUtils.writeStringToFile(new File(fileName), result, "UTF-8");
    }
}
