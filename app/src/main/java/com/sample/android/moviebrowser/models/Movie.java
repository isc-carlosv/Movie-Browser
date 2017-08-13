package com.sample.android.moviebrowser.models;

import android.graphics.Bitmap;


/**
 * @author Carlos Vasconcelos
 *
 */
public class Movie {
	private long id;
	private String coverURL;
	private String artist;
	private String title;
	private double price;
	
	private Bitmap cover;
	
		
	public Movie() {
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCoverURL() {
		return coverURL;
	}
	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}
	public Bitmap getCover() {
		return cover;
	}
	public synchronized void setCover(Bitmap cover) {
		this.cover = cover;
	}
	
}
