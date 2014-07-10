package ng.com.police.model;

import java.io.Serializable;

/**
 * Model for saving data of Gallery module.
 * 
 * @author
 * 
 */
public class GalleryModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String galleryId = "";
	private String imageTitle = "";
	private String imageDescription = "";
	private String imageAddedDate = "";
	private String imagePostDate = "";
	private String imagePath = "";
	private String imageSlug = "";
	private String imageDownloadCount = "";
	private String imageLikeCount = "";
	private String imageCommentCount = "";
	private String imageShortDescription = "";
	private String imageShareUrl = "";
	private int isLike = 0;
	
	public String getGalleryId() {
		return galleryId;
	}
	public void setGalleryId(String galleryId) {
		this.galleryId = galleryId;
	}
	public String getImageTitle() {
		return imageTitle;
	}
	public void setImageTitle(String imageTitle) {
		this.imageTitle = imageTitle;
	}
	public String getImageDescription() {
		return imageDescription;
	}
	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}
	public String getImageAddedDate() {
		return imageAddedDate;
	}
	public void setImageAddedDate(String imageAddedDate) {
		this.imageAddedDate = imageAddedDate;
	}
	public String getImagePostDate() {
		return imagePostDate;
	}
	public void setImagePostDate(String imagePostDate) {
		this.imagePostDate = imagePostDate;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getImageSlug() {
		return imageSlug;
	}
	public void setImageSlug(String imageSlug) {
		this.imageSlug = imageSlug;
	}
	public String getImageDownloadCount() {
		return imageDownloadCount;
	}
	public void setImageDownloadCount(String imageDownloadCount) {
		this.imageDownloadCount = imageDownloadCount;
	}
	public String getImageLikeCount() {
		return imageLikeCount;
	}
	public void setImageLikeCount(String imageLikeCount) {
		this.imageLikeCount = imageLikeCount;
	}
	public String getImageCommentCount() {
		return imageCommentCount;
	}
	public void setImageCommentCount(String imageCommentCount) {
		this.imageCommentCount = imageCommentCount;
	}
	public String getImageShortDescription() {
		return imageShortDescription;
	}
	public void setImageShortDescription(String imageShortDescription) {
		this.imageShortDescription = imageShortDescription;
	}
	public String getImageShareUrl() {
		return imageShareUrl;
	}
	public void setImageShareUrl(String imageShareUrl) {
		this.imageShareUrl = imageShareUrl;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getIsLike() {
		return isLike;
	}
	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}
	
	
}
