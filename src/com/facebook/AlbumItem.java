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

public class AlbumItem{
	private String id;
	private String name;
	private String description;
	
	public AlbumItem(String id,String name,String description){
		this.id=id;
		this.name=name;
		this.description=description;
	}

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id=id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name=name;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description=description;
	}

	@Override
	public String toString(){
		//returning name because that is what will be displayed in the Spinner control
		return(this.name);
	}
}