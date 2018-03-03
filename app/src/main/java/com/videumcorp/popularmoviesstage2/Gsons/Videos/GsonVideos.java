package com.videumcorp.popularmoviesstage2.Gsons.Videos;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class GsonVideos{

	@SerializedName("id")
	private int id;

	@SerializedName("results")
	private List<VideosResultsItem> results;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setResults(List<VideosResultsItem> results){
		this.results = results;
	}

	public List<VideosResultsItem> getResults(){
		return results;
	}

	@Override
 	public String toString(){
		return 
			"GsonVideos{" + 
			"id = '" + id + '\'' + 
			",results = '" + results + '\'' + 
			"}";
		}
}