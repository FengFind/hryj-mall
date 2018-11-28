package com.hryj.entity.vo.declare.ceb;

import org.apache.commons.codec.digest.DigestUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 报文头
 * 
 * @author Administrator
 *
 */
@XmlRootElement(name="MessageHead")
@XmlAccessorType(XmlAccessType.FIELD)
public class DTCMessageHead {
	public String MessageType;
	public String MessageId;
	public String ActionType;
	public String MessageTime;
	/// 十位编码
	public String SenderId;
	public String ReceiverId;
	public String SenderAddress;
	private String ReceiverAddress;
	private String PlatFormNo;
	private String CustomCode;
	private String SeqNo;
	private String Note;
	private String UserNo;
	private String Password;


	/**
	 * 海关报文头 
	 * 
	 * @param receiverId 
	 * 					默认：CQITC
	 * 
	 * @param senderId  
	 * 					备案编号
	 * 
	 * @param userNo
	 * 				备案用户名
	 * 
	 * @param password
	 * 				密码
	 * 
	 * 
	 * @param msgType
	 * 				类型
	 * 
	 * @param msgAction
	 * 				默认为:1
	 * 
	 * @return
	 */
	public static DTCMessageHead NewHeader(String receiverId,String senderId,String userNo,String password,String msgType, String msgAction) {
		 DTCMessageHead mh = new DTCMessageHead();
		 if(null == receiverId || "".equals(receiverId)){
			 receiverId = "CQITC";
		 }
		 if(null == msgAction || "".equals(msgAction)){
			 msgAction = "1";
		 }
        mh.ActionType =msgAction;
        mh.MessageType = msgType;
        mh.ReceiverId = receiverId;//SpringUtils.get("DTC_Message_ReceiverId");
        mh.SenderId = senderId;//SpringUtils.get("DTC_Message_SenderId"); 
        mh.UserNo = userNo;//SpringUtils.get("DTC_Message_UserNo");
        mh.MessageId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        mh.Password = DigestUtils.md5Hex(mh.MessageId + password).toUpperCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdd = new SimpleDateFormat("HH:mm:ss");
        mh.MessageTime = sdf.format(new Date())+"T"+sdd.format(new Date());
        return mh;
	}
	public String getMessageType() {
		return MessageType;
	}
	public void setMessageType(String messageType) {
		MessageType = messageType;
	}
	public String getMessageId() {
		return MessageId;
	}
	public void setMessageId(String messageId) {
		MessageId = messageId;
	}
	public String getActionType() {
		return ActionType;
	}
	public void setActionType(String actionType) {
		ActionType = actionType;
	}
	public String getMessageTime() {
		return MessageTime;
	}
	public void setMessageTime(String messageTime) {
		MessageTime = messageTime;
	}
	public String getSenderId() {
		return SenderId;
	}
	public void setSenderId(String senderId) {
		SenderId = senderId;
	}
	public String getReceiverId() {
		return ReceiverId;
	}
	public void setReceiverId(String receiverId) {
		ReceiverId = receiverId;
	}
	public String getSenderAddress() {
		return SenderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		SenderAddress = senderAddress;
	}
	public String getReceiverAddress() {
		return ReceiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		ReceiverAddress = receiverAddress;
	}
	public String getPlatFormNo() {
		return PlatFormNo;
	}
	public void setPlatFormNo(String platFormNo) {
		PlatFormNo = platFormNo;
	}
	public String getCustomCode() {
		return CustomCode;
	}
	public void setCustomCode(String customCode) {
		CustomCode = customCode;
	}
	public String getSeqNo() {
		return SeqNo;
	}
	public void setSeqNo(String seqNo) {
		SeqNo = seqNo;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getUserNo() {
		return UserNo;
	}
	public void setUserNo(String userNo) {
		UserNo = userNo;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
}
