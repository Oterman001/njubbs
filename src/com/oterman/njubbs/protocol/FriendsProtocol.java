package com.oterman.njubbs.protocol;


/**
 * ��ȡ������Ϊ�ҵĺ���
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.text.TextUtils;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.bean.FriendInfo;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.SPutils;
/**
 * ��ȡվ��������
 * @author oterman
 *
 */
public class FriendsProtocol {

	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public  List<FriendInfo> loadFromServer(String url,boolean saveToLocal,Context context) {
		 List<FriendInfo> list=null;
		try {
			String cookie = BaseApplication.getCookie();
			if (cookie == null) {
				cookie=BaseApplication.autoLogin(context,true);
			}

			//	"_U_NUM=xx;_U_UID=xx;_U_KEY=xx
			String[] strs = cookie.split(";");
			if(strs.length==3){
				Map<String, String> cookies=new HashMap<>();
				cookies.put("_U_NUM",strs[0].split("=")[1]);
				cookies.put("_U_UID",strs[1].split("=")[1]);
				cookies.put("_U_KEY",strs[2].split("=")[1]);
				
				Document doc= Jsoup.connect(url).cookies(cookies).get();
				
				if(doc.select("td").size()==0){
					BaseApplication.autoLogin(context,true);
					cookie = BaseApplication.getCookie();
					cookies.put("_U_NUM",strs[0].split("=")[1]);
					cookies.put("_U_UID",strs[1].split("=")[1]);
					cookies.put("_U_KEY",strs[2].split("=")[1]);
					
					doc= Jsoup.connect(url).cookies(cookies).get();
				}
				String result=doc.html();
				LogUtil.d("�������ݣ�"+result);
				
				if (!result.contains("����δ��¼")) {
					list = parseHtml(doc);
					LogUtil.d("�ӷ�������ȡ�������ݳɹ���");
				}else{
					
					LogUtil.d("��ȡ��������ʧ�ܣ�");
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * �ӱ��ؼ�������
	 */
	public List<FriendInfo> loadFromCache(String url,Context context) {
		String html = CacheUtilsNew.loadFromLocal(getSaveKey());

		if (!TextUtils.isEmpty(html)) {
			Document doc = Jsoup.parse(html);
			LogUtil.d("�ӱ��ػ����ȡ�ɹ���");

			return parseHtml(doc);
		} else {
			return loadFromServer(url,true,context);
		}
	}

	private String getSaveKey() {
		return "friends"+"_"+SPutils.getFromSP("id");
	}

	private List<FriendInfo> parseHtml(Document doc) {
		List<FriendInfo> list=new ArrayList<>();
		Elements trEles = doc.select("tr");
		if(trEles.size()<2){
			LogUtil.d("û�к���");
			return list;
		}
		
		for (int i = 1; i < trEles.size(); i++) {
			Elements tdEles = trEles.get(i).select("td");
			
			if(tdEles.size()==4){
				String rankth = tdEles.get(0).text().trim();
				String friendId=tdEles.get(1).text().trim();
				String desc = tdEles.get(2).text().trim();
				
//				UserProtocol protocol=new UserProtocol();
//				UserInfo userInfo = protocol.getUserInfoFromServer(friendId);
				FriendInfo friend=new FriendInfo(rankth, friendId, desc);
//				friend.userInfo=userInfo;
				
				list.add(friend);
			}
		}
		
		return list;
	}
}
