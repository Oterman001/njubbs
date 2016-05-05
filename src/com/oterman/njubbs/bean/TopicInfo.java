package com.oterman.njubbs.bean;

import java.io.Serializable;

/**
 * @author oterman
 *
 */
public class TopicInfo  implements Serializable{
	
	public String board;//版块
	public String author;//作者
	public String title;//标题
	
	public String replyCount;//回复数量
	
	public String boardUrl;//版面的地址
	public String contentUrl;//帖子内容地址
	public String authorUrl;//作者地址
	
	public String rankth;//排名
	
	public boolean shouldTop;//是否置顶
	public String pubTime;//发布时间
	public String id;//序号
	public String loadMoreUrl;//下一页

	
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
