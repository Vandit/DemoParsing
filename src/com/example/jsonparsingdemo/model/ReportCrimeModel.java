package ng.com.police.model;

public class ReportCrimeModel {

	private String crimeId = "";
	private String userId = "";
	private String attachmentTag = "";
	private String attachmentPath = "";

	public String getAttachmentTag() {
		return attachmentTag;
	}

	public String getCrimeId() {
		return crimeId;
	}

	public void setCrimeId(String crimeId) {
		this.crimeId = crimeId;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public void setAttachmentTag(String attachmentTag) {
		this.attachmentTag = attachmentTag;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
