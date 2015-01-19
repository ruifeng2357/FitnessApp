/*
FacebookAlbumDemo - Example of how to display Facebook albums in an Android application
Copyright (C) 2010-2011 Hugues Johnson

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software 
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package com.facebook;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class FacebookJSONParser {

	public static String parseLatestStatus(String json) {
		String latestStatus = "";
		String startTag = "\"message\":\"";
		int indexOf = json.indexOf(startTag);
		if (indexOf > 0) {
			int start = indexOf + startTag.length();
			String endTag = "\",";
			return (json.substring(start, json.indexOf(endTag, start)));
		}
		return (latestStatus);
	}

	public static ArrayList<AlbumItem> parseAlbums(String json)
			throws JSONException {
		ArrayList<AlbumItem> albums = new ArrayList<AlbumItem>();
		JSONObject rootObj = new JSONObject(json);
		JSONArray itemList = rootObj.getJSONArray("data");
		int albumCount = itemList.length();
		for (int albumIndex = 0; albumIndex < albumCount; albumIndex++) {
			JSONObject album = itemList.getJSONObject(albumIndex);
			String description = "";
			try {
				description = album.getString("link");
			} catch (JSONException x) {/* not implemented */
			}
			int pic_count;
			try {
				pic_count = album.getInt("count");
			} catch (JSONException e) {
				pic_count = 0;
				e.printStackTrace();
			}
			if (pic_count > 0)
				albums.add(new AlbumItem(album.getString("id"), album
						.getString("name"), description));
		}
		return (albums);
	}

	public static ArrayList<PhotoItem> parsePhotos(String json)
			throws JSONException {
		ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
		JSONObject rootObj = new JSONObject(json);
		JSONArray itemList = rootObj.getJSONArray("data");
		int photoCount = itemList.length();
		for (int photoIndex = 0; photoIndex < photoCount; photoIndex++) {
			JSONObject photo = itemList.getJSONObject(photoIndex);
			photos.add(new PhotoItem(photo.getString("picture"), photo
					.getString("source")));
		}
		boolean isMore = false;
		String moreImageUrl;
		try {
			JSONObject moreImage = rootObj.getJSONObject("paging");
			moreImageUrl = moreImage.getString("next");
			isMore = true;
		} catch (Exception e) {
			e.printStackTrace();
			isMore = false;
			moreImageUrl = null;
		}
		photos.add(new PhotoItem(isMore, moreImageUrl));
		return (photos);
	}
}