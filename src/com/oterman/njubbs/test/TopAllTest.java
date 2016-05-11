package com.oterman.njubbs.test;

import java.util.List;
import java.util.Map;

import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.TopAllProtocol;
import com.oterman.njubbs.utils.Constants;

import android.test.AndroidTestCase;

public class TopAllTest  {

	public void testMap(){
		TopAllProtocol protocol=new TopAllProtocol();
		
		Map<String, List<TopicInfo>> map = protocol.loadFromServer(Constants.TOP_ALL_URL, true);
		
		Map<Integer, String> keyMap = protocol.getKeyMap();
		
		for (int i = 0; i < keyMap.keySet().size(); i++) {
			String section=keyMap.get(i);
			
			List<TopicInfo> list = map.get(section);
			System.out.println(section+":"+list.size());
		}
		
	}

}
