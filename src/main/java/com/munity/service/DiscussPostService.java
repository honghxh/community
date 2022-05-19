package com.munity.service;

import com.munity.pojo.entity.DiscussPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.munity.pojo.model.Post;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
public interface DiscussPostService extends IService<DiscussPost> {
  public  int  addDiscussPost(DiscussPost discussPost);
  public Post findDiscussPostById(int discussPostId);
  public List<DiscussPost> getUserPost(int userId);
  public DiscussPost selectById(int discussPostId);
}
