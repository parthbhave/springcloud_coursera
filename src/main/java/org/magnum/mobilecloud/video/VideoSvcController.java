/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class VideoSvcController {
	
	@Autowired
	private VideoRepository videos;
	
	@RequestMapping(value="/video", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList()
	{
		return Lists.newArrayList(videos.findAll());
	}
	
	@RequestMapping(value="/video/search/findByName", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(@RequestParam("title") String title)
	{
		return videos.findByName(title);
	}
	
	@RequestMapping(value="/video/search/findByDurationLessThan", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(@RequestParam("duration") long duration)
	{
		return videos.findByDurationLessThan(duration);
	}
	
	@RequestMapping(value="/video/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") Long id)
	{
		Video v = null;
		Iterator<Video> itr = (videos.findAll(Lists.newArrayList(id))).iterator();
	    while(itr.hasNext()) {
	         v = itr.next();
	    }
	    return v;
	}
	
	@RequestMapping(value="/video/{id}/likedby", method=RequestMethod.GET)
	public @ResponseBody List getLikedBy(@PathVariable("id") Long id)
	{
		Video v = videos.findOne(id);
		Set s = v.getLikesUsernames();
		return new ArrayList<String>(s);
	}
	
	@RequestMapping(value="/video/{id}/unlike", method=RequestMethod.POST)
	public @ResponseBody void unlikeVideo(@PathVariable("id") Long id, Principal p, HttpServletResponse response) throws Exception
	{
		try
		{
			Video v = videos.findOne(id);
			if(v==null) throw new Exception("not found");
			Set likeUsers = v.getLikesUsernames();
			if(!likeUsers.contains(p.getName())) throw new Exception("no likes"); 
			else
			{
				likeUsers.remove(p.getName());
				v.setLikesUsernames(likeUsers);
				v.setLikes(v.getLikes() - 1);
			}
			videos.save(v);
		}
		catch(Exception e)
		{
			if(e.getMessage().equals("not found")) response.setStatus(404);
			else if(e.getMessage().equals("no likes")) response.setStatus(400);
		}
	}
	
	@RequestMapping(value="/video/{id}/like", method=RequestMethod.POST)
	public @ResponseBody void likeVideo(@PathVariable("id") Long id, Principal p, HttpServletResponse response) throws Exception
	{
		try
		{
			Video v = videos.findOne(id);
			if(v==null) throw new Exception("not found");
			Set likeUsers = v.getLikesUsernames();
			if(likeUsers.contains(p.getName())) throw new Exception("more than one like"); 
			else
			{
				likeUsers.add(p.getName());
				v.setLikesUsernames(likeUsers);
				v.setLikes(v.getLikes() + 1);
			}
			videos.save(v);
		}
		catch(Exception e)
		{
			if(e.getMessage().equals("not found")) response.setStatus(404);
			else if(e.getMessage().equals("more than one like")) response.setStatus(400);
		}
	}
	
	@RequestMapping(value="/video", method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v)
	{
		Video savedVid = videos.save(v);
		return savedVid;
	}
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 */

	
	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}
	
}
