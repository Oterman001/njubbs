package com.oterman.njubbs.bean;

import java.io.Serializable;

/**
 * @author oterman
 *
 */
public class TopicInfo  implements Serializable{
	
	public String board;//���
	public String author;//����
	public String title;//����
	
	public String replyCount;//�ظ�����
	
	public String boardUrl;//����ĵ�ַ
	public String contentUrl;//�������ݵ�ַ
	public String authorUrl;//���ߵ�ַ
	
	public String rankth;//����
	
	public boolean shouldTop;//�Ƿ��ö�
	public String pubTime;//����ʱ��
	public String id;//���
	public String loadMoreUrl;//��һҳ

	
	public TopicInfo(String board, String author, String title,
			String replyCount, String boardUrl, String contentUrl,
			String authorUrl, String rankth) {
		super();
		this.board = board;
		this.author = author;
		this.title = title;
		this.replyCount = replyCount;
		this.boardUrl = boardUrl;
		this.contentUrl = contentUrl;
		this.authorUrl = authorUrl;
		this.rankth = rankth;
	}

	public TopicInfo(String board, String author, String title, String id,
			String loadMoreUrl, String pubTime, String contentUrl,String replyCount,String boardUrl) {
		super();
		this.board = board;
		this.author = author;
		this.title = title;
		this.id = id;
		this.loadMoreUrl = loadMoreUrl;
		this.pubTime = pubTime;
		this.contentUrl = contentUrl;
		this.boardUrl=boardUrl;
		
		this.authorUrl="bbsqry?userid="+author;
		
		if(id==null||id.length()==0){
			shouldTop=true;
		}else{
			shouldTop=false;
		}
		
		this.replyCount=replyCount;
		
	}

	public TopicInfo() {
		super();
	}

	
	
	public TopicInfo(String board, String title, String boardUrl,
			String contentUrl) {
		super();
		this.board = board;
		this.title = title;
		this.boardUrl = boardUrl;
		this.contentUrl = contentUrl;
	}

	@Override
	public String toString() {
		return "TopicInfo [board=" + board + ", title=" + title + ", boardUrl="
				+ boardUrl + ", contentUrl=" + contentUrl + "]";
	}
	
	


}
