package com.kh.mvc.board.model.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BoardExt extends Board {
	private int attachCount;
	private int commentCount;
	private List<Attachment> attachments = new ArrayList<>();
	
	public BoardExt() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BoardExt(int no, String title, String writer, String content, int readCount, Timestamp regDate) {
		super(no, title, writer, content, readCount, regDate);
//		this.attachCount = attachCount;
	}

	public int getAttachCount() {
		return attachCount;
	}

	public void setAttachCount(int attachCount) {
		this.attachCount = attachCount;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	
	public void addAttachment(Attachment attach) {
		this.attachments.add(attach);
	}

	
	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	@Override
	public String toString() {
		return "BoardExt [attachCount=" + attachCount + ", attachments=" + attachments + ", toString()="
				+ super.toString() + "]";
	}


	
}
