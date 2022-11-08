package com.springboot.imgur.responsedata;

import java.util.Date;

import com.springboot.imgur.responsedata.User;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private User userData;
    private Imgur data;
    private String  image;
    

	private Date timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
	
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	
    
    
    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }
    
    public JwtResponse(String accessToken,User userData) {
        this.token = accessToken;
        this.userData=userData;
    }
    
    

    public JwtResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	/**
	 * @return the data
	 */
	public Imgur getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Imgur data) {
		this.data = data;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	
	
    
    
}